package com.test.myapplicationgetoiil.adapters;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.test.myapplicationgetoiil.model.OwnLocation;
import com.test.myapplicationgetoiil.R;

import java.util.ArrayList;
import java.util.List;

public class OwnListAdapter extends ArrayAdapter<OwnLocation> {
    private ArrayList<OwnLocation> items;
    Location ownLocation;

    public OwnListAdapter(Context context, int textViewResourceId, ArrayList<OwnLocation> items_) {
        super(context, textViewResourceId, items_);
        items = items_;
    }

    public void notifyDataSetChanged(List<OwnLocation> list, Location ownLocation) {
        items = (ArrayList<OwnLocation>) list;
        this.ownLocation = ownLocation;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {

        return items.size();
    }

    @Override
    public OwnLocation getItem(int position) {

        return items.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        if (convertView == null) {

            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.own_list_item, null);


            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.placeImage);
            viewHolder.name = (TextView) convertView.findViewById(R.id.placeName);
            viewHolder.distance = (TextView) convertView.findViewById(R.id.placeDistance);


            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        OwnLocation location = items.get(position);
        viewHolder.imageView.setImageResource(location.getImageID());
        viewHolder.name.setText(location.getName().toString());
        final String textString = viewHolder.name.getText().toString();
        float[] distance = new float[1]; //магия
        if (ownLocation != null) {
            Location.distanceBetween(ownLocation.getLatitude(), ownLocation.getLongitude(),
                    location.getLatitude(), location.getLongitude(), distance);
            viewHolder.distance.setText(String.format("%.2f", distance[0] / 1000)+" Км"); //так надо
        }

        return convertView;
    }

    private static class ViewHolder {
        private ImageView imageView;
        private TextView name;
        private TextView distance;
    }
}