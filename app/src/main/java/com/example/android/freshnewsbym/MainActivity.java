package com.example.android.freshnewsbym;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.widget.TextView;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindDrawable;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<List<FreshNews>> {

    private static final String LOG_TAG = MainActivity.class.getName();

    /**
     * URL for the news from The Guardian API.
     */
    private static final String GUARDIAN_API_REQUEST_URL =
            "https://content.guardianapis.com/search?";

    /*private static final String GUARDIAN_API_REQUEST_URL =
            "https://content.guardianapis.com/search?from-date=2018-01-01&order-by=newest" +
                    "&show-fields=headline%2Cbyline%2Cthumbnail&page-size=25&format=json" +
                    "&api-key=5c759d1c-239f-445f-b72b-bfdb2d10b86b";*/

    /**
     * Constant value for the news loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int NEWS_LOADER_ID = 1;

    /** Adapter for the list of news */
    private FreshNewsAdapter newsAdapter;

    //Using the Butterknife library to cast the views that will be used
    @BindView(R.id.fresh_news_list) ListView freshNewsListView;
    @BindView(R.id.no_internet_layout) LinearLayout noInternetLayout;
    @BindView(R.id.no_news_layout) LinearLayout noNewsLayout;
    @BindView(R.id.loading_indicator) View loadingIndicator;
    @BindView(R.id.text_loading) TextView loadingIndicatorText;

    //Using the Butterknife library to attach resources used in the footer
    @BindColor(R.color.footer_and_empty_state_text_color) int footerColor;
    @BindString(R.string.footer) String footerText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //Variable needed to build the footer
        TextView footer = new TextView(this);

        //Connecting the TextView that will be the empty view.
        freshNewsListView.setEmptyView(noInternetLayout);
        freshNewsListView.setEmptyView(noNewsLayout);

        //Hiding the no news and the no internet msg
        noNewsLayout.setVisibility(View.GONE);
        noInternetLayout.setVisibility(View.GONE);

        //Create a new adapter that takes an empty list of news as input.
        newsAdapter = new FreshNewsAdapter(this, new ArrayList<FreshNews>());

        //Populating the ListView
        freshNewsListView.setAdapter(newsAdapter);

        //Building the footer
        footer.setText(footerText);
        footer.setGravity(Gravity.CENTER);
        footer.setTextColor(footerColor);
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

                //Check if there is an app installed on the phone that is able to handle an event.

                if (websiteIntent.resolveActivity(getPackageManager()) != null) {

                    //Send the intent to launch a new activity
                    startActivity(websiteIntent);
                 }
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
            loadingIndicator.setVisibility(View.GONE);
            loadingIndicatorText.setVisibility(View.GONE);

            //Hiding the no news and the no internet msg
            noNewsLayout.setVisibility(View.GONE);
            noInternetLayout.setVisibility(View.GONE);

            //Update the empty state with no connection error message.
            noInternetLayout.setVisibility(View.VISIBLE);
        }


    }

    /*@Override
    public Loader<List<FreshNews>> onCreateLoader(int i, Bundle bundle) {

        //Create a new loader for the given URL.
        return new FreshNewsLoader(this, GUARDIAN_API_REQUEST_URL);
    }*/

    @Override
    //onCreateLoader instantiates and returns a new Loader for the given ID
    public Loader<List<FreshNews>> onCreateLoader(int i, Bundle bundle) {

        //To change the URI according to the preferences of the user
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        //getString retrieves a String value from the preferences.
        //The second parameter is the default value for this preference
        String minAmountNews = sharedPrefs.getString(
                getString(R.string.settings_min_amount_news_key),
                getString(R.string.settings_min_amount_news_default));

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
                 );

        //Parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(GUARDIAN_API_REQUEST_URL);

        //buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        //Append query parameter and its value. For example, the `format=json`
        uriBuilder.appendQueryParameter("from-date", "2018-01-01");
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("show-fields", "headline,byline,thumbnail");
        uriBuilder.appendQueryParameter("page-size", minAmountNews);
        uriBuilder.appendQueryParameter("format", "json");
        uriBuilder.appendQueryParameter("api-key", "5c759d1c-239f-445f-b72b-bfdb2d10b86b");

        Log.e(LOG_TAG, "The uri being built is " + uriBuilder.toString());

        //Return the completed URI
        return new FreshNewsLoader(this, uriBuilder.toString());
    }


    @Override
    public void onLoadFinished(Loader<List<FreshNews>> loader, List<FreshNews> news) {

        //Hide the loading indicator and text because the data has been loaded.
        loadingIndicator.setVisibility(View.GONE);
        loadingIndicatorText.setVisibility(View.GONE);

        //Hiding the no news and the no internet msg
        noNewsLayout.setVisibility(View.GONE);
        noInternetLayout.setVisibility(View.GONE);

        //Clear the adapter of previous news data
        newsAdapter.clear();

        //If there is a valid list of {@link FreshNews}, then add them to the adapter's
        //data set. This will trigger the ListView to update.
        if (news != null && !news.isEmpty()) {
            newsAdapter.addAll(news);
        } else {
            //Showing the no news msg
            noNewsLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<FreshNews>> loader) {

        //Loader reset, so we can clear out our existing data.
        newsAdapter.clear();
    }

    @Override
    // This method initialize the contents of the Activity's options menu
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    // This method is called whenever an item in the options menu is selected.
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}