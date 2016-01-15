package com.test.myapplicationgetoiil.model;

import android.app.Activity;
import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.test.myapplicationgetoiil.interfaces.CallBackForMap;
import com.test.myapplicationgetoiil.R;
import com.test.myapplicationgetoiil.activities.MainActivity;
import com.test.myapplicationgetoiil.adapters.OwnListAdapter;

import java.util.ArrayList;
import java.util.List;

public class OwnListView extends Fragment implements OnItemClickListener {
    private ListView listView;
    private List<OwnLocation> locations;
    private OwnListAdapter adapter;

    public List<OwnLocation> getLocations() {
        return locations;
    }

    public void setLocations(ArrayList<OwnLocation> locations) {
        this.locations = locations;
    }

    public void refreshView(List<OwnLocation> list, Location ownPosition) {
        locations = list;
        adapter.notifyDataSetChanged(list, ownPosition);
    }

    private CallBackForMap callBack;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        adapter = new OwnListAdapter(getActivity(), R.layout.own_list_item, (ArrayList<OwnLocation>)
                ((MainActivity) getActivity()).getLocations());
        listView = (ListView) rootView.findViewById(R.id.listview);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callBack = ((CallBackForMap) activity);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        callBack.listItemClicked(position);
    }

}
