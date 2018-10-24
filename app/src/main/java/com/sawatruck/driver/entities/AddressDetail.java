package com.sawatruck.driver.entities;

/**
 * Created by royal on 9/21/2017.
 */

public class AddressDetail {
    private Double latitude;
    private Double longitude;
    private String formatted_address;
    private String country;
    private String city;
    private String StreetNumber;
    private String route;
    private String PostalCodePrefix;
    private String AdministrativeAreaLevel1;
    private String AdministrativeAreaLevel2;
    private String AdministrativeAreaLevel3;
    private String AddressDetails;

    public AddressDetail(){
        latitude = Double.valueOf(0);
        longitude = Double.valueOf(0);
        formatted_address = "";
        country = "";
        city = "";
        StreetNumber = "";
        route = "";
        PostalCodePrefix = "";
        AdministrativeAreaLevel1 = "";
        AdministrativeAreaLevel2 = "";
        AdministrativeAreaLevel3 = "";
        AddressDetails = "";
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getFormatted_address() {
        return formatted_address;
    }

    public void setFormatted_address(String formatted_address) {
        this.formatted_address = formatted_address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        if(this.city.length() ==0)
            this.city = city;
    }

    public String getStreetNumber() {
        return StreetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        StreetNumber = streetNumber;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getPostalCodePrefix() {
        return PostalCodePrefix;
    }

    public void setPostalCodePrefix(String postalCodePrefix) {
        PostalCodePrefix = postalCodePrefix;
    }

    public String getAdministrativeAreaLevel1() {
        return AdministrativeAreaLevel1;
    }

    public void setAdministrativeAreaLevel1(String administrativeAreaLevel1) {
        AdministrativeAreaLevel1 = administrativeAreaLevel1;
    }

    public String getAdministrativeAreaLevel2() {
        return AdministrativeAreaLevel2;
    }

    public void setAdministrativeAreaLevel2(String administrativeAreaLevel2) {
        AdministrativeAreaLevel2 = administrativeAreaLevel2;
    }

    public String getAdministrativeAreaLevel3() {
        return AdministrativeAreaLevel3;
    }

    public void setAdministrativeAreaLevel3(String administrativeAreaLevel3) {
        AdministrativeAreaLevel3 = administrativeAreaLevel3;
    }

    public String getAddressDetails() {
        return AddressDetails;
    }

    public void setAddressDetails(String addressDetails) {
        AddressDetails = addressDetails;
    }
}
