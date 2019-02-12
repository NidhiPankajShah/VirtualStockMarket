package com.aasim.ecellvsm.Models;

import java.util.ArrayList;

/**
 * Created by aasim on 26/12/16.
 */

public class LeaderBoardResponse {
    int success;
    int rank;
    float networth;
    float change;

    public int getRank() {
        return rank;
    }

    public float getNetworth() {
        return networth;
    }

    ArrayList<User> user  ;

    public float getChange() {
        return change;
    }

    public int getSuccess() {
        return success;
    }

    public ArrayList<User> getUser() {
        return user;
    }
}
