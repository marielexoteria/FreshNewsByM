package com.example.android.freshnewsbym;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import java.util.ArrayList;

public class FreshNewsAdapter extends ArrayAdapter<FreshNews> {

    private static final String LOG_TAG = FreshNewsAdapter.class.getSimpleName();

    /**
     * This is the own custom constructor (it doesn't mirror a superclass constructor).
     * The context is used to inflate the layout file, and the list is the data we want
     * to populate into the lists.
     *
     * @param context        The current context. Used to inflate the layout file.
     * @param freshNews      A List of FreshNews objects to display in a list
     */
    public FreshNewsAdapter(Activity context, ArrayList<FreshNews> freshNews) {

        /**
         * Here, we initialize the ArrayAdapter's internal storage for the context and the list.
         * The second argument is used when the ArrayAdapter is populating a single TextView.
         * Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
         * going to use this second argument, so it can be any value. Here, we used 0.
         */
        super(context, 0, freshNews);
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position The position in the list of data that should be displayed in the
     *                 list item view.
     * @param convertView The recycled view to populate.
     * @param parent The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //Check if the existing view is being reused, otherwise inflate the view.
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.fresh_news_list_item, parent, false);
        }

        //Get the {@link FreshNews} object located at this position in the list.
        FreshNews currentFreshNews = getItem(position);

        //Start populating the different elements of the list item

        //Thumbnail
        ImageView newsPhoto = (ImageView) listItemView.findViewById(R.id.news_photo);
        //Using Glide library to retrieve the photos from the URLs obtained through the custom object & adapter
        /*centerCrop() is needed so that the image displays full width in the ImageView:
        http://bumptech.github.io/glide/doc/transformations.html*/

        Glide.with(getContext()).load(currentFreshNews.getThumbnail())
                .apply(
                        new RequestOptions()
                                .placeholder(R.drawable.placeholder_image)
                                .fallback(R.drawable.error_and_fallback_image)
                                .error(R.drawable.error_and_fallback_image)
                                .centerCrop())
                .into(newsPhoto);

        //Headline
        TextView headline = (TextView) listItemView.findViewById(R.id.headline);
        headline.setText(currentFreshNews.getHeadline());
        //headline.setText(position+1 + ") " +currentFreshNews.getHeadline());

        //By-line
        TextView byline = (TextView) listItemView.findViewById(R.id.byline);
        byline.setText(currentFreshNews.getByline());

        //Date
        TextView datePublished = (TextView) listItemView.findViewById(R.id.date_published);
        datePublished.setText(currentFreshNews.getDatePublished());

        //Section (sports, travel, etc.)
        TextView sectionName = (TextView) listItemView.findViewById(R.id.section);
        sectionName.setText(currentFreshNews.getSectionName());


        //Return the whole list item layout
        return listItemView;
    }
}
