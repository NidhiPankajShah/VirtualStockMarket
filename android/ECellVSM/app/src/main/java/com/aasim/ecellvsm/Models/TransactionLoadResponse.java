package com.aasim.ecellvsm.Models;

import java.util.ArrayList;

/**
 * Created by aasim on 26/12/16.
 */

public class TransactionLoadResponse {
    int success;
    ArrayList<Company> company;
    User user;
    Completed completed;
    ArrayList<Pending> pending;

    public void setSuccess(int success) {
        this.success = success;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setCompleted(Completed completed) {
        this.completed = completed;
    }


    public void setHistory(ArrayList<History> history) {
        this.history = history;
    }

    public ArrayList<Pending> getPending() {

        return pending;
    }

    public Completed getCompleted() {
        return completed;
    }

    ArrayList<History> history;


    public int getSuccess() {
        return success;
    }

    public ArrayList<Company> getCompany() {
        return company;
    }

    public User getUser() {
        return user;
    }

    public ArrayList<History> getHistory() {
        return history;
    }
}
