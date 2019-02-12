package com.aasim.ecellvsm.Models;

/**
 * Created by aasim on 27/12/16.
 */

public class Pending {
    String _id,userid,name,longname;
    int type,quantity;

    public String get_id() {
        return _id;
    }

    public String getUserid() {
        return userid;

    }

    public String getName() {
        return name;
    }

    public String getLongname() {
        return longname;
    }

    public int getType() {
        return type;
    }

    public int getQuantity() {
        return quantity;
    }

}
