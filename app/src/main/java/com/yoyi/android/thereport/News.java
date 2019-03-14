package com.yoyi.android.thereport;

// News class to store the news attributes returned by the API in JSON Format
public class News {

    private String mTitle;
    private String mAuthor;
    private String mUrl;
    private String mDate;
    private String mSection;

    /***
     * News Class Constructor
     * @param title stores title of the news
     * @param author stores the author of the news
     * @param url contains the web url of the news article
     * @param date contains the published date of the news article
     * @param section conatins the section to which the news belongs
     */
    public News(String title, String author, String url, String date, String section) {
        mTitle = title;
        mAuthor = author;
        mUrl = url;
        mDate = date;
        mSection = section;
    }

    /***
     * returns the title of the news
     */
    public String getTitle() {
        return mTitle;
    }

    /***
     * returns the author of the news article
     */
    public String getAuthor() {
        return mAuthor;
    }

    /***
     * returns the web url of the news article
     */
    public String getUrl() {
        return mUrl;
    }

    /***
     * returns the publishing date of the news article
     */
    public String getDate() {
        return mDate;
    }

    public String getSection() {
        return mSection;
    }
}

