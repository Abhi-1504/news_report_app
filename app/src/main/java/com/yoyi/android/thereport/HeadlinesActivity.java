package com.yoyi.android.thereport;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class HeadlinesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>>, SwipeRefreshLayout.OnRefreshListener {

    // Declaring variables/Objects for loader, SwipeRefreshLayout, custom array adapter and empty state Text View
    private static int LOADER_ID = 0;
    SwipeRefreshLayout swipeRefreshLayout;
    private NewsAdapter mNewsAdapter;
    private TextView emptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_headlines);

        // Linking Views in layout to related objects
        emptyStateTextView = findViewById(R.id.empty_view);
        swipeRefreshLayout = findViewById(R.id.swiperefresh);

        // Setting refresh Listener and color of progress bar indicator on the SwipeRefreshLayout object
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));

        // Creating ListView Object and Linking it to the ListView Layout
        ListView newsListView = findViewById(R.id.list);

        // Setting the emptyStateTextView to the ListView
        // Note : This will appear on screen only if there is no data to load
        newsListView.setEmptyView(emptyStateTextView);

        // Creating adpater object and attaching it to the ListView
        mNewsAdapter = new NewsAdapter(this);
        newsListView.setAdapter(mNewsAdapter);

        // Setting On click listener to the ListView
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                // Extracting the News object from the List View item based on Position
                News news = mNewsAdapter.getItem(position);

                // Extracting the url string from the News object
                String url = news.getUrl();

                // Creating Intent to open the url link in the browser
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });
        // Initializing the loader
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Retrieving the String value from the preferences.
        String section = sharedPreferences.getString(getString(R.string.section_key), getString(R.string.settings_section_default));
        String orderBy = sharedPreferences.getString(getString(R.string.settings_order_by_key), getString(R.string.settings_order_by_default));
        return new NewsLoader(this, section, orderBy);
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> data) {
        // Checking for internet connection
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null) {
            //No internet connection display
            emptyStateTextView.setText(R.string.no_internet);
        } else if (networkInfo != null && networkInfo.isConnected()) {
            // Empty state text view display
            emptyStateTextView.setText(R.string.no_news);
        }
        // Removing the Progress bar indicator once the data gets loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Disabling the refresh of layout
        swipeRefreshLayout.setRefreshing(false);

        // Loading the data on screen if it's present
        if (data != null) {
            mNewsAdapter.setNotifyOnChange(false);
            mNewsAdapter.clear();
            mNewsAdapter.setNotifyOnChange(true);
            mNewsAdapter.addAll(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {

    }

    @Override
    public void onRefresh() {

        // Initializing the loader again on swipe refresh
        getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflating the menu with option items
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
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

