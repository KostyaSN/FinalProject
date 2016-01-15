package com.test.myapplicationgetoiil.activities;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.test.myapplicationgetoiil.R;
import com.test.myapplicationgetoiil.fragments.OwnMapFragment;
import com.test.myapplicationgetoiil.interfaces.CallBackForMap;
import com.test.myapplicationgetoiil.interfaces.iCallBackServices;
import com.test.myapplicationgetoiil.loader.DownloaderAsyncTask;
import com.test.myapplicationgetoiil.model.OwnListView;
import com.test.myapplicationgetoiil.model.OwnLocation;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements CallBackForMap, iCallBackServices, LocationListener {
    private final int RADIUS = 5000;
    private final int MINDISTANCETOUPDATE = 250;
    LocationManager locationManager;
    private Fragment mapFragment;
    private Fragment listFragment;
    private List<OwnLocation> locations;
    ActionBar actionBar;
    ProgressDialog progress;

    private Location ownPosition;

    public Location getOwnPosition() {
        return ownPosition;
    }

    public List<OwnLocation> getLocations() {
        if (locations != null)
            return locations;
        else {
            locations = new ArrayList<OwnLocation>();
            return locations;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        actionBar = getActionBar();



        if (findViewById(R.id.container) != null) {
            listFragment = new OwnListView();
            getFragmentManager().beginTransaction().replace(R.id.container, listFragment).commit();
        } else {
            listFragment = (Fragment) getFragmentManager().findFragmentById(R.id.listfragment);
            mapFragment = (Fragment) getFragmentManager().findFragmentById(R.id.mapfragment);
        }
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE); //GETTING THE LOCATION

        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
            onLocationChanged(location);
        }else {
            Toast.makeText(getBaseContext(), "Проблеммы с подключением, проверьте включен ли GPS и интернет", Toast.LENGTH_SHORT).show();}


        locationManager.requestLocationUpdates(provider, 5000, MINDISTANCETOUPDATE, this);
    }



    @Override
    protected void onResume() {
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(provider, 5000, MINDISTANCETOUPDATE, this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.removeUpdates(this);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, listFragment).setCustomAnimations(
                        R.animator.card_flip_left_in, R.animator.card_flip_left_out)
                        .addToBackStack(null)
                        .commit();
                actionBar.setDisplayHomeAsUpEnabled(false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void listItemClicked(int position) {
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (mapFragment != null) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.container, mapFragment).setCustomAnimations(R.animator.card_flip_left_in, R.animator.card_flip_left_out)
                    .addToBackStack(null)
                    .commit();
            ((OwnMapFragment) mapFragment).listItemClicked(position);

        } else {

            mapFragment = new OwnMapFragment();
            Bundle args = new Bundle();
            args.putInt(OwnMapFragment.ARG_POSITION, position);
            mapFragment.setArguments(args);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.container, mapFragment).setCustomAnimations(R.animator.card_flip_left_in, R.animator.card_flip_left_out)
                    .commit();
        }

    }

    public boolean isNetworkOnline(Context context) {
        boolean status = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);
            if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                status = true;
            } else {
                netInfo = cm.getNetworkInfo(1);
                if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED)
                    status = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return status;

    }

    void getGooglePlaces(Location location, int radius) {
        DownloaderAsyncTask task = new DownloaderAsyncTask(this, location, radius);
        task.execute();
    }

    @Override
    public void fail() {
    }

    @Override
    public void success(List<OwnLocation> places) {
        locations = places;
        progress.dismiss();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getBaseContext(), "Данные обновлены, заправок поблизости: " + locations.size(), Toast.LENGTH_SHORT).show();
                //поблизости подорозумевается в радиусе 5 км
                ((OwnListView) listFragment).refreshView(getLocations(), ownPosition);
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        ownPosition = location;
        if(progress == null) {
            progress = new ProgressDialog(this);
            progress.setTitle("Поиск...");
            progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progress.setCancelable(false);
            progress.setCanceledOnTouchOutside(false);
            progress.show();
        }
        getGooglePlaces(location, RADIUS);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub


    }

    @Override
    public void incrementProgressPercentage(int percentage) {
        progress.incrementProgressBy(percentage);

    }

}