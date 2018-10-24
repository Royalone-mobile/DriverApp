package com.sawatruck.driver.entities;

/**
 * Created by royal on 8/29/2017.
 */

public class Rating {
    private String Name;
    private String Msg;
    private String Rank;
    private String Date;
    private String imgUrl;
    public Rating(){
        Name = "";
        Msg = "";
        Rank = "";
        Date = "";
    }
    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String msg) {
        Msg = msg;
    }

    public String getRank() {
        return Rank;
    }

    public void setRank(String rank) {
        Rank = rank;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
