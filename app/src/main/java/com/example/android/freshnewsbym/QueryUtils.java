package com.example.android.freshnewsbym;


import android.text.TextUtils;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public final class QueryUtils {

     private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Return a list of {@link FreshNews} objects that has been built up from
     * parsing a JSON response.
     */

    /**
     * Query the Guardian API and return a list of {@link FreshNews} objects.
     */
    public static List<FreshNews> fetchNewsData(String requestUrl) {

        //Create an URL object
        URL url = createUrl(requestUrl);

        //Slowing down the background thread to test the loading indicator
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        //Extract the relevant fields from the JSON response and create a list of {@link FreshNews}
        List<FreshNews> news = extractFeatureFromJson(jsonResponse);

        //Return the list of {@link FreshNews}
        return news;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        //If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //If the request was successful (response code 200),
            //then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the JSON results (URL connection).", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                //Closing the input stream could throw an IOException, which is why
                //the makeHttpRequest(URL url) method signature specifies than an IOException
                //could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }


    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }


    /**
     * Return a list of {@link FreshNews} objects that has been built up from
     * parsing a JSON response.
     */
    private static List<FreshNews> extractFeatureFromJson(String freshNewsJson) {

        //If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(freshNewsJson)) {
            return null;
        }

        //Create an empty ArrayList that we can start adding fresh news to.
        List<FreshNews> freshNewsArrayList = new ArrayList<>();

        //Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            //Build up a list of FreshNews objects with the corresponding data.

            //(1) Convert JSON_RESPONSE String into a JSONObject.
            JSONObject listJsonResponse = new JSONObject(freshNewsJson);

            //(2) Extract the JSON object "response".
            JSONObject response = listJsonResponse.getJSONObject("response");

            //(3) Extract the JSON array "results".
            JSONArray freshNewsArrayJson = response.getJSONArray("results");

            /* Extracting the items one at a time (until page-size as specified in the
             * Guardian API URL).
             */
            for (int i = 0; i < freshNewsArrayJson.length(); i++) {
                JSONObject currentNews = freshNewsArrayJson.getJSONObject(i);

                //Extracting items directly under "results" (as specified in FreshNews.java)
                String date = currentNews.getString("webPublicationDate");
                String section = currentNews.getString("sectionName");
                String url = currentNews.getString("webUrl");

                //Extracting items under the key "fields" (as specified in FreshNews.java)
                JSONObject fields = currentNews.getJSONObject("fields");

                String headline = fields.getString("headline");
                String thumbnail, byline;

                //Fallback text in case there is no thumbnail
                String thumb = fields.optString("thumbnail", null);
                if (TextUtils.isEmpty(thumb)) {
                    thumbnail = "https://github.com/marielexoteria/FreshNewsByM/blob/" +
                            "master/app/src/main/res/drawable/error_and_fallback_image.png";
                } else {
                    thumbnail= fields.getString("thumbnail");
                }

                //Fallback text in case there is no author
                if (fields.getString("byline").isEmpty()) {
                    byline = "Author not available";
                } else {
                    byline = fields.getString("byline");
                }

                //Formatting the date to "May 27, 2018 14:05" on London time zone.
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ss'Z'", Locale.UK);
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                Date dateFormatted = null;
                try {
                    dateFormatted = simpleDateFormat.parse(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String dateNewFormat = dateFormatInEnglish(dateFormatted);

                //Adding all the elements to the ArrayList
                freshNewsArrayList.add(new FreshNews(thumbnail, headline, byline, dateNewFormat, section, url));
            }

        } catch (JSONException e) {

            //If an error is thrown when executing any of the above statements in the "try" block,
            //catch the exception here, so the app doesn't crash. Print a log message
            //with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the news JSON results", e);
        }

        //Return the list of news
        return freshNewsArrayList;

    }

    //Code inspired by
    // https://stackoverflow.com/questions/6842245/converting-date-time-to-24-hour-format
    private static String dateFormatInEnglish (Date date) {
        SimpleDateFormat newDateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm",Locale.UK);
        newDateFormat.setTimeZone(TimeZone.getDefault());
        return newDateFormat.format(date);
    }

}