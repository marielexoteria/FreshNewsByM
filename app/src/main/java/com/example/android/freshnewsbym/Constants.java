package com.example.android.freshnewsbym;

/*
 * "Config" file for the app. It stores values that are needed here and there.
 */

public final class Constants {

    //Values used in QueryUtils.java
    public static final int DELAYCONNECTION = 2000;
    public static final int READTIMEOUT = 10000;
    public static final int CONNECTTIMEOUT = 15000;
    public static final String IOEXCEPTIONHTTPREQUEST = "Problem making the HTTP request.";
    public static final String MALFORMEDURLEXCEPTION = "Problem building the URL ";
    public static final String RESPONDECODEURLCONNECTION = "Error response code: ";
    public static final String IOEXCEPTIONURLCONNECTION = "Problem retrieving the JSON results (URL connection).";
    public static final String JSONEXCEPTIONQUERYUTILS = "Problem parsing the news JSON results.";

    public static final String RESPONSE = "response";
    public static final String RESULTS = "results";
    public static final String FIELDS = "fields";

    public static final String THUMBNAIL = "thumbnail";
    public static final String HEADLINE = "headline";
    public static final String BYLINE = "byline";
    public static final String SECTIONNAME = "sectionName";
    public static final String WEBURL = "webUrl";
    public static final String WEBPUBLICATIONDATE = "webPublicationDate";

    public static final String THUMBNAILURL = "https://github.com/marielexoteria/FreshNewsByM/blob/" +
            "master/app/src/main/res/drawable/error_and_fallback_image_portrait.pngtrait.png";
    public static final String BYLINENOTFOUND = "Author not available";
}
