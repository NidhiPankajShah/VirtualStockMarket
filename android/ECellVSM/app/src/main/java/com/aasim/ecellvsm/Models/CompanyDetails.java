package com.aasim.ecellvsm.Models;

import java.util.AbstractList;
import java.util.ArrayList;

/**
 * Created by aasim on 12/03/17.
 */

public class CompanyDetails {
    ArrayList<History> history;
    int success;

    public CompanyDetails(ArrayList<History> history, int success, Company company, Completed completed) {
        this.history = history;
        this.success = success;
        this.company = company;
        this.completed = completed;
    }

    Company  company;

    public ArrayList<History> getHistory() {
        return history;
    }

    public int getSuccess() {
        return success;
    }

    public Company getCompany() {
        return company;
    }

    public Completed getCompleted() {
        return completed;
    }

    Completed completed;


}
