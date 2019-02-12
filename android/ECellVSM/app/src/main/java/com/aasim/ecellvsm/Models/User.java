package com.aasim.ecellvsm.Models;

/**
 * Created by aasim on 26/12/16.
 */

public class User {
    String _id,username,password;
    double networth,previous,balance;
    int verify;

    public String getId() {
        return _id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public double getNetworth() {
        return networth;
    }

    public double getPrevious() {
        return previous;
    }

    public double getBalance() {
        return balance;
    }

    public int getVerify() {
        return verify;
    }
}
