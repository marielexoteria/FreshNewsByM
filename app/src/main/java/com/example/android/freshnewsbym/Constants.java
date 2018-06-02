package com.example.android.freshnewsbym;

/*
 * "Config" file for the app. It stores values that are needed where specified.
 */

public final class Constants {

    //Values used in QueryUtils.java
    public static final int DELAYCONNECTION = 2000;
    public static final int READTIMEOUT = 10000;
    public static final int CONNECTTIMEOUT = 15000;
    public static final String IOEXCEPTIONHTTPREQUEST = "Problem making the HTTP request.";
    public static final String MALFORMEDURLEXCEPTION = "Problem building the URL ";
    public static final String RESPONSECODEURLCONNECTION = "Error response code: ";
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

    //Fallback values (QueryUtils.java)
    public static final String THUMBNAILURL = "http://marielbackman.com/img/error_and_fallback_image.png"; //need
        //to find a better service for photo hosting than this one
    public static final String BYLINENOTFOUND = "Author not available";

    //Values used in MainActivity.java
    public static final String FROMDATEQUERYPARAMETER = "from-date";
    public static final String ORDERBYQUERYPARAMETER = "order-by";
    public static final String SHOWFIELDSQUERYPARAMETER = "show-fields";
    public static final String PAGESIZEQUERYPARAMETER = "page-size";
    public static final String FORMATQUERYPARAMETER = "format";
    public static final String APIKEYQUERYPARAMETER = "api-key";

    public static final String FROMDATEVALUE = "2018-01-01";
    public static final String SHOWFIELDSVALUE = "headline,byline,thumbnail";
    public static final String FORMATVALUE = "json";
    public static final String APIKEYVALUE = "5c759d1c-239f-445f-b72b-bfdb2d10b86b";

}
