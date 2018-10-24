package com.sawatruck.driver.entities;

/**
 * Created by royal on 8/28/2017.
 */

public class City {
    private String CountryID;
    private String CountryName;
    private String Name;
    private String CountryFlagUrl;
    private Double Latitude;
    private Double Longitude;


    public String getCountryName() {
        return CountryName;
    }

    public void setCountryName(String countryName) {
        CountryName = countryName;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }


    public String getCountryFlagUrl() {
        return CountryFlagUrl;
    }

    public void setCountryFlagUrl(String countryFlagUrl) {
        CountryFlagUrl = countryFlagUrl;
    }

    public String getCountryID() {
        return CountryID;
    }

    public void setCountryID(String countryID) {
        this.CountryID = countryID;
    }

    public Double getLatitude() {
        return Latitude;
    }

    public void setLatitude(Double latitude) {
        Latitude = latitude;
    }

    public Double getLongitude() {
        return Longitude;
    }

    public void setLongitude(Double longitude) {
        Longitude = longitude;
    }
}
