package com.aasim.ecellvsm.Models;

/**
 * Created by aasim on 26/12/16.
 */

public class Completed {
    String userid;
    String name;
    String longname ;
    float  quantity;
    float price;

    public String getUserid() {
        return userid;
    }

    public String getName() {
        return name;
    }

    public String getLongname() {
        return longname;
    }

    public int getQuantity() {
        return (int)quantity;
    }

    public float getPrice() {
        return price;
    }
}
