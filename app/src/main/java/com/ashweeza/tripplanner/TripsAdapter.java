package com.ashweeza.tripplanner;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Ashweeza on 2/16/2017.
 */

public class TripsAdapter extends ArrayAdapter<Trip> {
    ArrayList<Trip> tripslist;
    Context context;
    int resource;
    private SparseBooleanArray mSelectedItemsIds;

    public TripsAdapter(Context context, int resource, ArrayList<Trip> objects) {
        super(context, resource, objects);
        this.tripslist = objects;
        this.resource = resource;
        this.context = context;
        mSelectedItemsIds = new SparseBooleanArray();
    }
    @Override
    public void remove(Trip object) {
        tripslist.remove(object);
        notifyDataSetChanged();
    }

    public ArrayList<Trip> getWorldPopulation() {
        return tripslist;
    }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);
        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TripsAdapter.ViewwHolder holder = new TripsAdapter.ViewwHolder();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.tripslistitem, parent, false);
            holder.tripname = (TextView) convertView.findViewById(R.id.tripnameTV);
            holder.tripdays = (TextView) convertView.findViewById(R.id.tripdaysTV);
            convertView.setTag(holder);
        }
        holder = (TripsAdapter.ViewwHolder) convertView.getTag();
        TextView name = holder.tripname;
        TextView days = holder.tripdays;
        name.setText(tripslist.get(position).getTripname());
        days.setText(tripslist.get(position).getNumdays());

        return convertView;

    }

    public static class ViewwHolder {
        TextView tripname;
        TextView tripdays;
    }
}
