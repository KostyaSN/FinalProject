package com.test.myapplicationgetoiil.fragments;


import android.app.Activity;
import android.app.Dialog;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.test.myapplicationgetoiil.interfaces.CallBackForMap;
import com.test.myapplicationgetoiil.model.OwnLocation;
import com.test.myapplicationgetoiil.activities.MainActivity;

import java.util.List;

public class OwnMapFragment extends MapFragment implements CallBackForMap {

    public static final String ARG_POSITION = "map_position";
    private GoogleMap map;
    private Integer position_in_array;
    LocationManager locationManager;
    MainActivity parent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            position_in_array = savedInstanceState.getInt(ARG_POSITION);
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        init();
        super.onResume();
    }

    private void init() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity().getBaseContext());
        if (status != ConnectionResult.SUCCESS) {
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, getActivity(), requestCode);
            dialog.show();

        } else {

            // Getting GoogleMap object from the fragment
            map = this.getMap();

            // Enabling MyLocation Layer of Google Map
            map.setMyLocationEnabled(true);

            // Getting LocationManager object from System Service LOCATION_SERVICE
            locationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);

            // Creating a criteria object to retrieve provider
            Criteria criteria = new Criteria();

            // Getting the name of the best provider
            String provider = locationManager.getBestProvider(criteria, true);

            // Getting Current Location
            Location location = locationManager.getLastKnownLocation(provider);

        }
    }

    @Override
    public void onAttach(Activity activity) {
        parent = (MainActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public void listItemClicked(int position) {

        List<OwnLocation> locations = parent.getLocations();
        double latitude = locations.get(position).getLatitude();
        double longitude = locations.get(position).getLongitude();
        String title = locations.get(position).getName() + " - " + locations.get(position).getType().name();
        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(latitude, longitude)).zoom(12).build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        LatLng latLng = new LatLng(latitude,longitude);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
        addMarker(locations.get(position));

    }

    private void addMarker(OwnLocation loc) {

        // create marker
        MarkerOptions marker = new MarkerOptions().position(new LatLng(loc.getLatitude(), loc.getLongitude())).title(loc.getName());

        // adding marker
        map.addMarker(marker);
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle args = getArguments();
        map.getMaxZoomLevel();
        map.getUiSettings().setZoomControlsEnabled(true);
        if (args != null) {
            listItemClicked(args.getInt(ARG_POSITION));
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        initMap();
    }

    private void initMap() {
        if (map == null) {

            map = getMap();
            if (map != null) {

                map.setMyLocationEnabled(true); // false to disable
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }

        }
    }
}