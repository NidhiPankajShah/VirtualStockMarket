package com.aasim.ecellvsm.Models;

import java.util.ArrayList;

/**
 * Created by aasim on 27/12/16.
 */

public class PendingResponse {
    int success;
    ArrayList<Pending> pending;

    public int getSuccess() {
        return success;
    }

    public ArrayList<Pending> getPending() {
        return pending;

    }
}
