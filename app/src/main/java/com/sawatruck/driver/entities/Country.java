package com.sawatruck.driver.entities;

/**
 * Created by royal on 8/29/2017.
 */

public class Country {
    private String ID;
    private String CountryCode;
    private String CountryName;
    private String CountryDialCode;
    private int AdjustLunarDay;
    private String GoogleMapsCountryID;
    private String ISO3;
    private int NumCode;
    private int Status;
    private int LanguageID;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getCountryCode() {
        return CountryCode;
    }

    public void setCountryCode(String countryCode) {
        CountryCode = countryCode;
    }

    public String getCountryName() {
        return CountryName;
    }

    public void setCountryName(String countryName) {
        CountryName = countryName;
    }

    public String getCountryDialCode() {
        return CountryDialCode;
    }

    public void setCountryDialCode(String countryDialCode) {
        CountryDialCode = countryDialCode;
    }

    public int getAdjustLunarDay() {
        return AdjustLunarDay;
    }

    public void setAdjustLunarDay(int adjustLunarDay) {
        AdjustLunarDay = adjustLunarDay;
    }

    public String getGoogleMapsCountryID() {
        return GoogleMapsCountryID;
    }

    public void setGoogleMapsCountryID(String googleMapsCountryID) {
        GoogleMapsCountryID = googleMapsCountryID;
    }

    public String getISO3() {
        return ISO3;
    }

    public void setISO3(String ISO3) {
        this.ISO3 = ISO3;
    }

    public int getNumCode() {
        return NumCode;
    }

    public void setNumCode(int numCode) {
        NumCode = numCode;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public int getLanguageID() {
        return LanguageID;
    }

    public void setLanguageID(int languageID) {
        LanguageID = languageID;
    }
}
