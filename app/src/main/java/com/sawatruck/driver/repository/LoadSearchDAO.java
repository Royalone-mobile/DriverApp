package com.sawatruck.driver.repository;

/**
 * Created by royal on 9/1/2017.
 */

public class LoadSearchDAO {
    private int Id;
    private String loadDate;
    private String loadLocation;
    private String deliveryDate;
    private String deliveryLocation;
    private String searchName;
    private String loadAddress;
    private String deliveryAddress;
    public String getLoadDate() {
        return loadDate;
    }

    public void setLoadDate(String loadDate) {
        this.loadDate = loadDate;
    }

    public String getLoadLocation() {
        return loadLocation;
    }

    public void setLoadLocation(String loadLocation) {
        this.loadLocation = loadLocation;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getDeliveryLocation() {
        return deliveryLocation;
    }

    public void setDeliveryLocation(String deliveryLocation) {
        this.deliveryLocation = deliveryLocation;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getSearchName() {
        return searchName;
    }

    public void setSearchName(String searchName) {
        this.searchName = searchName;
    }

    public String getLoadAddress() {
        return loadAddress;
    }

    public void setLoadAddress(String loadAddress) {
        this.loadAddress = loadAddress;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
}
