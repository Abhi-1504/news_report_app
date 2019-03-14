package com.yoyi.android.thereport;

import android.net.Uri;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class QueryUtils {

    /***
     * Builds the WEB API query in URI format based on the preference inputted by user
     * @param section stores the section preference inputted by user
     * @param orderBy stores the order by preference inputted by user
     * @return returns the WEB API query url in String form
     */
    private static String createStringUrl(String section, String orderBy) {
        Uri.Builder builder = new Uri.Builder();
        if (!section.equalsIgnoreCase("all")) {
            builder.scheme("http")
                    .encodedAuthority("content.guardianapis.com")
                    .appendPath("search")
                    .appendQueryParameter("order-by", orderBy)
                    .appendQueryParameter("show-references", "author")
                    .appendQueryParameter("show-tags", "contributor")
                    .appendQueryParameter("section", section)
                    .appendQueryParameter("q", "India")
                    .appendQueryParameter("api-key", "3e1b5681-4438-4cbc-830f-aab8399d3a39");
        } else
            builder.scheme("http")
                    .encodedAuthority("content.guardianapis.com")
                    .appendPath("search")
                    .appendQueryParameter("order-by", orderBy)
                    .appendQueryParameter("show-references", "author")
                    .appendQueryParameter("show-tags", "contributor")
                    .appendQueryParameter("q", "India")
                    .appendQueryParameter("api-key", "3e1b5681-4438-4cbc-830f-aab8399d3a39");
        return builder.build().toString();
    }

    /***
     * Creates the WEB API query URL
     * @param section stores the section preference inputted by user
     * @param orderBy stores the order by preference inputted by user
     * @return WEB API query URL (Null on exception)
     */
    static URL createUrl(String section, String orderBy) {
        String stringUrl = createStringUrl(section, orderBy);
        try {
            return new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e("Queryutils", "Error creating URL: ", e);
            return null;
        }
    }

    /***
     * Formats the raw date in JSON to readable formatted date string
     * @param rawDate raw date from the JSON
     * @return readable formatted date string
     */
    private static String formatDate(String rawDate) {
        String jsonDatePattern = "yyyy-MM-dd'T'HH:mm:ss'Z'";
        SimpleDateFormat jsonFormatter = new SimpleDateFormat(jsonDatePattern, Locale.UK);
        try {
            Date parsedJsonDate = jsonFormatter.parse(rawDate);
            String finalDatePattern = "MMM d, yyy";
            SimpleDateFormat finalDateFormatter = new SimpleDateFormat(finalDatePattern, Locale.UK);
            return finalDateFormatter.format(parsedJsonDate);
        } catch (ParseException e) {
            Log.e("QueryUtils", "Error parsing JSON date: ", e);
            return "";
        }
    }

    /***
     * makes the http request using the WEB API query link
     * @param url WEB API query link
     * @return response of the request in JSON format
     * @throws IOException if the http request fails to make a connection with the servers
     */
    static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e("mainActivity", "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e("Queryutils", "Error making HTTP request: ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /***
     * Reads the response received from the HTTP request and converts it into string
     * @param inputStream the response in the stream from the HTTP request
     * @return HTTP response converted into string
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

    /***
     * JSON parser method
     * @param response JSON response formatted into string
     * @return List of the news object extracted from the JSOn response
     */
    static List<News> parseJson(String response) {
        ArrayList<News> listOfNews = new ArrayList<>();
        try {
            JSONObject jsonResponse = new JSONObject(response);
            JSONObject jsonResults = jsonResponse.getJSONObject("response");
            JSONArray resultsArray = jsonResults.getJSONArray("results");

            // Parsing through the JSON array to extract the list of news object
            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject oneResult = resultsArray.getJSONObject(i);
                // Extracting the title
                String webTitle = oneResult.getString("webTitle");
                // Extracting the web URL
                String url = oneResult.getString("webUrl");
                // Extracting the publishing date string
                String date = oneResult.getString("webPublicationDate");
                // Formatting the date string into a readable format
                date = formatDate(date);
                // Extracting the news article section
                String section = oneResult.getString("sectionName");
                // Extracting the tags from the JSON
                JSONArray tagsArray = oneResult.getJSONArray("tags");
                String author = "";

                // Extracting the author of the news article if any otherwise keeping it null from the tags
                if (tagsArray.length() == 0) {
                    author = null;
                } else {
                    for (int j = 0; j < tagsArray.length(); j++) {
                        JSONObject firstObject = tagsArray.getJSONObject(j);
                        author = author + firstObject.getString("webTitle") + ". ";
                    }
                }
                // adding the extracted news objects attribute in the list
                listOfNews.add(new News(webTitle, author, url, date, section));
            }
        } catch (JSONException e) {
            Log.e("Queryutils", "Error parsing JSON response", e);
        }
        return listOfNews;
    }
}
