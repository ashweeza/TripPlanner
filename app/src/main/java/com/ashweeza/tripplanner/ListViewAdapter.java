package com.ashweeza.tripplanner;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ListViewAdapter extends ArrayAdapter<PlaceData> {
    // Declare Variables
    Context context;
    LayoutInflater inflater;
    ArrayList<PlaceData> placelist;
    private SparseBooleanArray mSelectedItemsIds;
    private DisplayMetrics metrics_;

    public ListViewAdapter(Context context, int resourceId,
                           ArrayList<PlaceData> worldpopulationlist, DisplayMetrics metrics) {
        super(context, resourceId, worldpopulationlist);
        mSelectedItemsIds = new SparseBooleanArray();
        this.context = context;
        this.placelist = worldpopulationlist;
        inflater = LayoutInflater.from(context);
        this.metrics_ = metrics;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.items, null);
            // Locate the TextViews in listview_item.xml
            holder.name = (TextView) convertView.findViewById(R.id.nameTV);
            holder.vicinity = (TextView) convertView.findViewById(R.id.vicinityTV);
            holder.rating = (TextView) convertView.findViewById(R.id.ratingTV);
            holder.icon = (ImageView) convertView.findViewById(R.id.imageViewIcon);
            holder.opennow = (TextView) convertView.findViewById(R.id.opentv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
       /* Animation animation = null;
        animation = AnimationUtils.loadAnimation(context, R.anim.pushleft_in);*/
        // Capture position and set to the TextViews
        TextView name = holder.name;
        TextView rating = holder.rating;
        TextView vicinity = holder.vicinity;
        ImageView icon = holder.icon;
        TextView open = holder.opennow;

        name.setText(placelist.get(position).getName());
        rating.setText(placelist.get(position).getRating().toString());
        vicinity.setText(placelist.get(position).getVicinity());
        switch (placelist.get(position).getPrice_level()) {
            case 0:
                open.setText("N/A");
                open.setBackgroundColor(Color.WHITE);
                break;

            case 1:
                open.setText(String.valueOf(placelist.get(position).getPrice_level()));
                open.setBackgroundColor(Color.CYAN);
                break;
            case 2:
                open.setText(String.valueOf(placelist.get(position).getPrice_level()));
                open.setBackgroundColor(Color.GREEN);
                break;
            case 3:
                open.setText(String.valueOf(placelist.get(position).getPrice_level()));
                open.setBackgroundColor(Color.YELLOW);
                break;
            case 4:
                open.setText(String.valueOf(placelist.get(position).getPrice_level()));
                open.setBackgroundColor(Color.RED);
                break;
        }
      /*  else{
            open.setText("Closed");
            open.setBackgroundColor(Color.RED);
        }*/

        Picasso.with(context).load(placelist.get(position).getIcon_url()).resize(100, 100).into(icon);
        /*final View finalConvertView = convertView;
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(finalConvertView.getContext(), "ICon clicked",Toast.LENGTH_LONG).show();
            }
        });*/
      /*  animation.setDuration(500);
        convertView.startAnimation(animation);
        animation = null;*/
        return convertView;
    }

    @Override
    public void remove(PlaceData object) {
        placelist.remove(object);
        notifyDataSetChanged();
    }

    public ArrayList<PlaceData> getWorldPopulation() {
        return placelist;
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

    public static class ViewHolder {
        TextView name;
        ImageView icon;
        TextView vicinity;
        TextView rating;
        TextView opennow;
    }
}