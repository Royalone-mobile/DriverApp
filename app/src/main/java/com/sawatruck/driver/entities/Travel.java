package com.sawatruck.driver.entities;

/**
 * Created by royal on 9/21/2017.
 */

public class Travel {
    private String TravelID;
    private String LoaderRate;
    private String DriverRate;

    public String getTravelID() {
        return TravelID;
    }

    public void setTravelID(String travelID) {
        TravelID = travelID;
    }

    public String getLoaderRate() {
        return LoaderRate;
    }

    public void setLoaderRate(String loaderRate) {
        LoaderRate = loaderRate;
    }

    public String getDriverRate() {
        return DriverRate;
    }

    public void setDriverRate(String driverRate) {
        DriverRate = driverRate;
    }
}
