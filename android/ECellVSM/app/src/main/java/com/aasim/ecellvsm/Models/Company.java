package com.aasim.ecellvsm.Models;

import java.util.ArrayList;

/**
 * Created by aasim on 26/12/16.
 */

public class Company {
    String _id,name,shortname;
    float rate,previous,change;

    public String get_id() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public String getShortname() {
        return shortname;
    }

    public float getRate() {
        return rate;
    }

    public float getPrevious() {
        return previous;
    }

    public float getChange() {
        return change;
    }
}
