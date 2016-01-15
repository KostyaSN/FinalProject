package com.test.myapplicationgetoiil.parser;


import android.util.Log;

import com.test.myapplicationgetoiil.interfaces.iCallBackServices;
import com.test.myapplicationgetoiil.model.OwnLocation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class JSONHandler {
    private static final String LOG_TAG = "ExampleApp";

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";

    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String TYPE_DETAILS = "/details";
    private static final String TYPE_SEARCH = "/search";

    private static final String OUT_JSON = "/json";

    // KEY!
    private static final String API_KEY = "AIzaSyCQPe2SckBieg-7tGk1ExXWmM6ifs32__Y";

    public static ArrayList<OwnLocation> autocomplete(String input) {
        ArrayList<OwnLocation> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE);
            sb.append(TYPE_AUTOCOMPLETE);
            sb.append(OUT_JSON);
            sb.append("?sensor=false");
            sb.append("&key=" + API_KEY);
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<OwnLocation>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                OwnLocation place = new OwnLocation();
                place.reference = predsJsonArray.getJSONObject(i).getString("reference");
                place.setName(predsJsonArray.getJSONObject(i).getString("description"));
                resultList.add(place);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error processing JSON results", e);
        }

        return resultList;
    }

    public static ArrayList<OwnLocation> search(String keyword, double lat, double lng, int radius, iCallBackServices listener) {
        ArrayList<OwnLocation> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE);
            sb.append(TYPE_SEARCH);
            sb.append(OUT_JSON);
            sb.append("?sensor=false");
            sb.append("&key=" + API_KEY);
            sb.append("&keyword=" + URLEncoder.encode(keyword, "utf8"));
            sb.append("&location=" + String.valueOf(lat) + "," + String.valueOf(lng));
            sb.append("&radius=" + String.valueOf(radius));
            Log.d(LOG_TAG, sb.toString());

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            listener.incrementProgressPercentage(20);

            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        listener.incrementProgressPercentage(25);

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("results");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<OwnLocation>();
            for (int i = 0; i < predsJsonArray.length(); i++) {
                OwnLocation place = new OwnLocation();
                place.reference = predsJsonArray.getJSONObject(i).getString("reference");
                place.setName(predsJsonArray.getJSONObject(i).getString("name"));
                listener.incrementProgressPercentage(i);

                JSONObject geometryObject = predsJsonArray.getJSONObject(i).getJSONObject("geometry");      //getting the latitude and longitude
                JSONObject locationObject = geometryObject.getJSONObject("location");
                place.setLatitude(Double.parseDouble(locationObject.getString("lat")));
                place.setLongitude(Double.parseDouble(locationObject.getString("lng")));

                JSONArray temp = predsJsonArray.getJSONObject(i).getJSONArray("types");
                int length = temp.length();
                if (length > 0) {
                    String [] recipients = new String [length];
                    for (int j = 0; j < length; j++) {
                        recipients[j] = temp.getString(j);
                    }
                    place.setTypes(recipients);
                }
                resultList.add(place);
                listener.incrementProgressPercentage(20);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error processing JSON results", e);
        }

        return resultList;
    }

    public static OwnLocation details(String reference) {
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE);
            sb.append(TYPE_DETAILS);
            sb.append(OUT_JSON);
            sb.append("?sensor=false");
            sb.append("&key=" + API_KEY);
            sb.append("&reference=" + URLEncoder.encode(reference, "utf8"));

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return null;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        OwnLocation place = null;
        try {
            JSONObject jsonObj = new JSONObject(jsonResults.toString()).getJSONObject("result");
            place = new OwnLocation();
            place.icon = jsonObj.getString("icon");
            place.setName(jsonObj.getString("name"));
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error processing JSON results", e);
        }

        return place;
    }
}
