package com.ashweeza.tripplanner;

import java.io.Serializable;

/**
 * Created by Ashweeza on 1/25/2017.
 */

public class PlaceData implements Serializable {

    String name;
    Double rating;
    String icon_url;
    String vicinity;
    Double Lattitude;
    Double Longitude;
    boolean opennow;
    int price_level;

    public int getPrice_level() {
        return price_level;
    }

    public void setPrice_level(int price_level) {
        this.price_level = price_level;
    }

    public String getPlaceid() {
        return placeid;
    }

    public void setPlaceid(String placeid) {
        this.placeid = placeid;
    }

    String placeid;

    public boolean isOpennow() {
        return opennow;
    }

    public void setOpennow(boolean opennow) {
        this.opennow = opennow;
    }

    boolean selected = false;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Double getLattitude() {
        return Lattitude;
    }

    public void setLattitude(Double lattitude) {
        Lattitude = lattitude;
    }

    public Double getLongitude() {
        return Longitude;
    }

    public void setLongitude(Double longitude) {
        Longitude = longitude;
    }

    public String getIcon_url() {
        return icon_url;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }
}
