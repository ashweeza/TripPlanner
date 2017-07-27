package com.ashweeza.tripplanner;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Ashweeza on 2/22/2017.
 */

public class DbTripData {
    int daynum;
    String daytime;
    String cityname;
    String placeone;
    String placetwo;
    String placethree;
    String placefour;
    LatLng latLng;
    float distance;

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getCityname() {
        return cityname;
    }

    public void setCityname(String cityname) {
        this.cityname = cityname;
    }

    public int getDaynum() {
        return daynum;
    }

    public void setDaynum(int daynum) {
        this.daynum = daynum;
    }

    public String getDaytime() {
        return daytime;
    }

    public void setDaytime(String daytime) {
        this.daytime = daytime;
    }

    public String getPlacefour() {
        return placefour;
    }

    public void setPlacefour(String placefour) {
        this.placefour = placefour;
    }

    public String getPlaceone() {
        return placeone;
    }

    public void setPlaceone(String placeone) {
        this.placeone = placeone;
    }

    public String getPlacethree() {
        return placethree;
    }

    public void setPlacethree(String placethree) {
        this.placethree = placethree;
    }

    public String getPlacetwo() {
        return placetwo;
    }

    public void setPlacetwo(String placetwo) {
        this.placetwo = placetwo;
    }
}
