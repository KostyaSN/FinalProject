package com.test.myapplicationgetoiil.loader;


import android.location.Location;
import android.os.AsyncTask;

import com.test.myapplicationgetoiil.parser.JSONHandler;
import com.test.myapplicationgetoiil.interfaces.iCallBackServices;

public class DownloaderAsyncTask extends AsyncTask<Void, Void, Void> {
    private JSONHandler jsonHandler;
    private Location location;
    private int radius;
    private iCallBackServices resultListener;

    @Override
    protected Void doInBackground(Void... params) {
        resultListener.success(JSONHandler.search("gas_station", location.getLatitude(), location.getLongitude() , radius, resultListener));
        return null;
    }

    public DownloaderAsyncTask(iCallBackServices listener, Location loc, int rad)  {
        location = loc;
        radius = rad;
        resultListener = listener;
    }


}