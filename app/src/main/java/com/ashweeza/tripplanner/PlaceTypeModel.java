package com.ashweeza.tripplanner;

/**
 * Created by Ashweeza on 2/25/2017.
 */

public class PlaceTypeModel {
    String typename;
    int id_;

    public PlaceTypeModel(String name, int id_) {
        this.typename = name;
        this.id_ = id_;
    }


    public String getName() {
        return typename;
    }


    public int getId() {
        return id_;
    }
}
