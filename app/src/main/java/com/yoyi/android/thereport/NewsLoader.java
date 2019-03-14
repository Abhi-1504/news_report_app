package com.yoyi.android.thereport;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<News>> {

    // member variables to store preference strings
    private String mSection;
    private String mOrderBy;

    /***
     * NewsLoader class constructor
     * @param context context of activity
     * @param section stores the section preference inputted by user
     * @param orderBy stores the order by value inputted by user
     */
    public NewsLoader(Context context, String section, String orderBy) {
        super(context);
        mSection = section;
        mOrderBy = orderBy;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public List<News> loadInBackground() {
        List<News> listOfNews = null;
        try {
            // Creating the WEB API query URL based on the preferences
            URL url = QueryUtils.createUrl(mSection, mOrderBy);
            // Storing the JSON response after making the http request
            String jsonResponse = QueryUtils.makeHttpRequest(url);
            // Storing the list of news after parsing the JSON response
            listOfNews = QueryUtils.parseJson(jsonResponse);
        } catch (IOException e) {
            Log.e("Query Utils", "Error Loader LoadInBackground: ", e);
        }
        return listOfNews;
    }
}
