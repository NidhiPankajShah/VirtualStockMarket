package com.aasim.ecellvsm;

/**
 * Created by aasim on 14/12/16.
 */
public class sharesListItem {
    private String name , shortname , rate , change , shares , worth ;

    public sharesListItem(){

    }

    public sharesListItem(String name , String shortname , String rate , String change,String shares,String worth){
        this.name = name;
        this.shortname = shortname;
        this.rate = rate;
        this.change = change;
        this.shares = shares;
        this.worth = worth;
    }

    public void setWorth(String worth) {
        this.worth = worth;
    }

    public void setShares(String shares) {

        this.shares = shares;
    }

    public String getWorth() {

        return worth;
    }

    public String getShares() {

        return shares;
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
