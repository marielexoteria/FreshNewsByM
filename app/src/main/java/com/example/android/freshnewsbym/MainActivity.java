package com.example.android.freshnewsbym;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.widget.TextView;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<List<FreshNews>> {

    private static final String LOG_TAG = MainActivity.class.getName();

    /**
     * URL for the news from The Guardian API.
     */
    private static final String GUARDIAN_API_REQUEST_URL =
            "https://content.guardianapis.com/search?from-date=2017-01-01&order-by=newest" +
                    "&show-fields=headline%2Cbyline%2Cthumbnail&page-size=25&format=json" +
                    "&api-key=5c759d1c-239f-445f-b72b-bfdb2d10b86b";

    /**
     * Constant value for the news loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int NEWS_LOADER_ID = 1;

    /** Adapter for the list of news */
    private FreshNewsAdapter newsAdapter;

    /** TextView that is displayed when the list is empty */
    private TextView emptyStateTextView;
    private ImageView emptyStateImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Variable needed to build the footer
        TextView footer = new TextView(this);

        //Find the ListView and attach the adapter to it.
        ListView freshNewsListView = (ListView) findViewById(R.id.fresh_news_list);

        //Connecting the TextView that will be the empty view.
        emptyStateImageView = (ImageView) findViewById(R.id.empty_view_icon);
        emptyStateTextView = (TextView) findViewById(R.id.empty_view);
        freshNewsListView.setEmptyView(emptyStateImageView);
        freshNewsListView.setEmptyView(emptyStateTextView);

        //Create a new adapter that takes an empty list of news as input.
        newsAdapter = new FreshNewsAdapter(this, new ArrayList<FreshNews>());

        //Populating the ListView
        freshNewsListView.setAdapter(newsAdapter);

        //Building the footer
        footer.setText(getString(R.string.footer));
        footer.setGravity(Gravity.CENTER);
        footer.setTextColor(getResources().getColor(R.color.footer_and_empty_state_text_color));
        footer.setPadding(0,10,0, 30);

        //Adding the footer
        freshNewsListView.addFooterView(footer, null, false);

        /*Enabling the link on each ListView item, so that the app user can read the news on their
        preferred browser */
        freshNewsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                //Find the current article that was clicked on.
                FreshNews currentFreshNews = newsAdapter.getItem(position);

                //Convert the String URL into a URI object (to pass into the Intent constructor).
                Uri freshNewsUri = Uri.parse(currentFreshNews.getURL());

                //Create a new intent to view the news URI.
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, freshNewsUri);

                //Send the intent to launch a new activity.
                startActivity(websiteIntent);
            }
        });

        //Get a reference to the ConnectivityManager to check state of network connectivity.
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        //Get details on the currently active default data network.
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        //If there is a network connection, fetch data.
        if (networkInfo != null && networkInfo.isConnected()) {

            //Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            //Initialize the loader. Pass in the int ID constant defined above and pass in null for
            //the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            //because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {

            //Otherwise, display error. First, hide the loading indicator and text so that
            //the error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            TextView loadingIndicatorText = findViewById(R.id.text_loading);
            loadingIndicator.setVisibility(View.GONE);
            loadingIndicatorText.setVisibility(View.GONE);

            //Update the empty state with no connection error message.
            emptyStateImageView.setImageResource(R.drawable.no_wifi);
            emptyStateTextView.setText(R.string.no_internet_connection);
        }


    }

    @Override
    public Loader<List<FreshNews>> onCreateLoader(int i, Bundle bundle) {

        //Create a new loader for the given URL.
        return new FreshNewsLoader(this, GUARDIAN_API_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<FreshNews>> loader, List<FreshNews> news) {

        //Hide the loading indicator and text because the data has been loaded.
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);
        TextView textLoading = findViewById(R.id.text_loading);
        textLoading.setVisibility(View.GONE);

        //Set empty state text to display "No news found."
        emptyStateTextView.setText(R.string.no_news);

        //Clear the adapter of previous news data
        newsAdapter.clear();

        //If there is a valid list of {@link FreshNews}, then add them to the adapter's
        //data set. This will trigger the ListView to update.
        if (news != null && !news.isEmpty()) {
            newsAdapter.addAll(news);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<FreshNews>> loader) {

        //Loader reset, so we can clear out our existing data.
        newsAdapter.clear();
    }
}
