package com.ashweeza.tripplanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Ashweeza on 2/22/2017.
 */

public class WholeTripAdapter extends ArrayAdapter<DbTripData> {
    ArrayList<DbTripData> tripslist;
    Context context;
    int resource;
    float distance[];

    public WholeTripAdapter(Context context, int resource, ArrayList<DbTripData> objects, float objjj[]) {
        super(context, resource, objects);
        this.tripslist = objects;
        this.resource = resource;
        this.context = context;
        distance = new float[tripslist.size()];
        distance = objjj;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.wholertripitem, parent, false);
            holder.daynum = (TextView) convertView.findViewById(R.id.daytv);
            holder.daytime = (TextView) convertView.findViewById(R.id.timedaytv);
            holder.cityname = (TextView) convertView.findViewById(R.id.citytv);
            holder.placeone = (TextView) convertView.findViewById(R.id.ponetv);
            holder.placetwo = (TextView) convertView.findViewById(R.id.ptwotv);
            holder.placethree = (TextView) convertView.findViewById(R.id.pthreetv);
            holder.placefour = (TextView) convertView.findViewById(R.id.pfourtv);
            holder.distance1 = (TextView) convertView.findViewById(R.id.dist1);
            holder.distance2 = (TextView) convertView.findViewById(R.id.dist2);
            holder.distance3 = (TextView) convertView.findViewById(R.id.dist3);
            convertView.setTag(holder);
        }
        holder = (Holder) convertView.getTag();
        TextView day = holder.daynum;
        TextView timeday = holder.daytime;
        TextView city = holder.cityname;
        TextView pone = holder.placeone;
        TextView ptwo = holder.placetwo;
        TextView pthree = holder.placethree;
        TextView pfour = holder.placefour;
        TextView dis1 = holder.distance1;
        TextView dis2 = holder.distance2;
        TextView dis3 = holder.distance3;
        day.setText(String.valueOf(tripslist.get(position).getDaynum()));
        timeday.setText(tripslist.get(position).getDaytime());
        city.setText(tripslist.get(position).getCityname());
        pone.setText(tripslist.get(position).getPlaceone());
        ptwo.setText(tripslist.get(position).getPlacetwo());
        pthree.setText(tripslist.get(position).getPlacethree());
        pfour.setText(tripslist.get(position).getPlacefour());
    /*    float results[] = new float[1];
        Location.distanceBetween(tripslist.get(position).getLatLng().latitude,
                tripslist.get(position).getLatLng().longitude,
                tripslist.get(position+1).getLatLng().latitude,
                tripslist.get(position+1).getLatLng().longitude,results);*/
//       dis1.setText("Distance to next place is:" + String.valueOf(distance[position]));

        return convertView;
    }

    public static class Holder {
        TextView daynum;
        TextView daytime;
        TextView cityname;
        TextView placeone;
        TextView placetwo;
        TextView placethree;
        TextView placefour;
        TextView distance1;
        TextView distance2;
        TextView distance3;
    }
}
