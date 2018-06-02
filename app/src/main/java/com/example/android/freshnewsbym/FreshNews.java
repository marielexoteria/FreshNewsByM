package com.example.android.freshnewsbym;

public class FreshNews {

    //URL of the thumbnail associated with the news article
    //troubleshooting issue loading placeholder and fallback images
    //will remove when I'm finished
    //private int thumbnail;
    private String thumbnail;

    //Headline of the news article
    private String headline;

    //Author of the news article
    private String byline;

    //Date when the news article was published
    private String datePublished;

    //Section to which the news article corresponds
    private String sectionName;

    //URL of the news article
    private String url;

    /*
     * Create a new FreshNews object.
     *
     * @param vThumbnail is the URL of the thumbnail associated with the news article
     * @param vHeadline is the headline of the news article
     * @param vByline is the author of the news article
     * @param vDatePublished is the date when the news article was published
     * @param vSectionName is the section to which the news article corresponds
     * @param vUrl is the URL of the news article
     * */
    public FreshNews (String vThumbnail, String vHeadline, String vByline, String vDatePublished,
                      String vSectionName, String vUrl)
    {
        thumbnail = vThumbnail;
        headline = vHeadline;
        byline = vByline;
        datePublished = vDatePublished;
        sectionName = vSectionName;
        url = vUrl;
    }

    /**
     * Get the URL of the thumbnail
     */
    //troubleshooting issue loading placeholder and fallback images
    //                //will remove when I'm finished
    /*public int getThumbnail() {
        return thumbnail;
    }*/

    public String getThumbnail() {
           return thumbnail;
    }

    /**
     * Get the headline of the news article
     */
    public String getHeadline() {
        return headline;
    }

    /**
     * Get the author of the news article
     */
    public String getByline() {

        return "By "+ byline;
    }

    /**
     * Get the date when the news article was published
     */
    public String getDatePublished() {
        return datePublished;
    }

    /**
     * Get the section to which the news article corresponds
     */
    public String getSectionName() {
        return sectionName;
    }

    /**
     * Get the URL of the news article
     */
    public String getURL() {
        return url;
    }
}
