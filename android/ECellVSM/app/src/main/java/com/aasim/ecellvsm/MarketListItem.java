package com.aasim.ecellvsm;

/**
 * Created by aasim on 14/12/16.
 */
public class MarketListItem {
    private String name , shortname , rate , change ;

    public MarketListItem(){

    }

    public MarketListItem(String name , String shortname , String rate , String change){
        this.name = name;
        this.shortname = shortname;
        this.rate = rate;
        this.change = change;
    }

    public String getName() {
        return name;
    }

    public String getShortname() {
        return shortname;
    }

    public String getRate() {
        return rate;
    }

    public String getChange() {
        return change;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public void setChange(String change) {
        this.change = change;
    }

}

