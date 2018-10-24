package com.sawatruck.driver.controller;

import java.io.Serializable;

/**
 * Created by royal on 10/20/2017.
 */

public class NearbyLocation implements Serializable{
    private String name;
    private String vincity;
    private String id;
    private String place_id;
    private Double latitude;
    private Double longitude;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVincity() {
        return vincity;
    }

    public void setVincity(String vincity) {
        this.vincity = vincity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
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
}
