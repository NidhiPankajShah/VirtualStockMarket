package com.aasim.ecellvsm.Models;

import java.util.AbstractList;
import java.util.ArrayList;

/**
 * Created by aasim on 26/12/16.
 */

public class GetAllCompanyResponse {
    int success;
    ArrayList<Company> company;

    public int getSuccess() {
        return success;
    }

    public ArrayList<Company> getCompany() {
        return company;
    }
}
