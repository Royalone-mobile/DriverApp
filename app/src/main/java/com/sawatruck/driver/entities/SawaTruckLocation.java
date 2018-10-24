package com.sawatruck.driver.entities;

/**
 * Created by royal on 8/29/2017.
 */

public class SawaTruckLocation {
    private String CountryName;
    private String CityName;
    private String Longitude;
    private String Latitude;
    private String AddressDetails;
    private String CountryFlagImgUrl;

    public String getCountryName() {
        return CountryName;
    }

    public void setCountryName(String countryName) {
        CountryName = countryName;
    }

    public String getCityName() {
        return CityName;
    }

    public void setCityName(String cityName) {
        CityName = cityName;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getAddressDetails() {
        return AddressDetails;
    }

    public void setAddressDetails(String addressDetails) {
        AddressDetails = addressDetails;
    }

    public String getCountryFlagImgUrl() {
        return CountryFlagImgUrl;
    }

    public void setCountryFlagImgUrl(String countryFlagImgUrl) {
        CountryFlagImgUrl = countryFlagImgUrl;
    }
}
