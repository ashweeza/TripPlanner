package com.ashweeza.tripplanner;

import java.io.Serializable;

/**
 * Created by Ashweeza on 2/16/2017.
 */

public class Trip implements Serializable{
    String tripname;
    String numdays;

    public String getNumdays() {
        return numdays;
    }

    public void setNumdays(String numdays) {
        this.numdays = numdays;
    }


    public String getTripname() {
        return tripname;
    }

    public void setTripname(String tripname) {
        this.tripname = tripname;
    }
}
