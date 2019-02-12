package com.aasim.ecellvsm.Models;

import java.util.ArrayList;

/**
 * Created by aasim on 28/12/16.
 */

public class CompletedResponse {
    ArrayList<Completed> completed;
    ArrayList<Company> company;
    User user;

    public User getUser() {
        return user;
    }

    public ArrayList<Company> getCompanies() {
        return company;
    }

    public ArrayList<Completed> getCompleted() {
        return completed;
    }
}
