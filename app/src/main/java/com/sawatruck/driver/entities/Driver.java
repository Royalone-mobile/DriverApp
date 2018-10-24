package com.sawatruck.driver.entities;

/**
 * Created by royal on 9/21/2017.
 */
public class Driver{
    private String ID;
    private String FullName;
    private String Badget;
    private String ImgUrl;
    private String Rate;
    private String Reviews;
    private String CountryName;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getBadget() {
        return Badget;
    }

    public void setBadget(String badget) {
        Badget = badget;
    }

    public String getImgUrl() {
        return ImgUrl;
    }

    public void setImgUrl(String imgUrl) {
        ImgUrl = imgUrl;
    }

    public String getRate() {
        return Rate;
    }

    public void setRate(String rate) {
        Rate = rate;
    }

    public String getReviews() {
        return Reviews;
    }

    public void setReviews(String reviews) {
        Reviews = reviews;
    }

    public String getCountryName() {
        return CountryName;
    }

    public void setCountryName(String countryName) {
        CountryName = countryName;
    }
}