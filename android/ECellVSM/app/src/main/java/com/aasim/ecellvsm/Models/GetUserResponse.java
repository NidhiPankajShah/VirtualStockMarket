package com.aasim.ecellvsm.Models;

/**
 * Created by aasim on 26/12/16.
 */

public class GetUserResponse {
    int success;
    User user ;
    int error;

    public int getSuccess() {
        return success;
    }

    public User getUser() {
        return user;
    }

    public int getError() {
        return error;
    }
}
