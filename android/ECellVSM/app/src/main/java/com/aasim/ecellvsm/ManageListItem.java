package com.aasim.ecellvsm;

/**
 * Created by aasim on 16/12/16.
 */
public class ManageListItem {
    private String name , shortname , shares , change,id ;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {

        return id;
    }

    public ManageListItem(String name, String shortname, String shares, String change,String id) {
        this.name = name;
        this.shortname = shortname;
        this.shares = shares;
        this.change = change;
        this.id = id;

    }

    public void setChange(String change) {
        this.change = change;
    }

    public void setShares(String shares) {

        this.shares = shares;
    }

    public void setShortname(String shortname) {

        this.shortname = shortname;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getChange() {

        return change;
    }

    public String getShares() {

        return shares;
    }

    public String getShortname() {

        return shortname;
    }

    public String getName() {

        return name;
    }


}
