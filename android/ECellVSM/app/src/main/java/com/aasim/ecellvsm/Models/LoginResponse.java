package com.aasim.ecellvsm.Models;

/**
 * Created by aasim on 26/12/16.
 */

public class LoginResponse {
    int success ;
    int error;
    User user = new User();

    public int getError() {
        return error;
    }

    public int getSuccess() {
        return success;
    }

    public User getUser() {
        return user;
    }
}
