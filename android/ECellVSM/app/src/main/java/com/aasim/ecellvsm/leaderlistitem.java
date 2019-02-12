package com.aasim.ecellvsm;

/**
 * Created by aasim on 14/12/16.
 */
public class leaderlistitem {
    private String username , bal  , rank  , change ;
    public leaderlistitem(){

    }

    public leaderlistitem(String username , String bal , String rank , String change){
        this.username = username;
        this.bal = bal;
        this.rank = rank;
        this.change = change;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setBal(String bal) {
        this.bal = bal;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public String getRank() {
        return rank;
    }

    public String getChange() {
        return change;
    }

    public String getBal() {
        return bal;
    }

    public String getUsername() {
        return username;
    }
}
