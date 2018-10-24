package com.sawatruck.driver.entities;

/**
 * Created by royal on 8/29/2017.
 */

public class TruckType {
    private Integer ID;
    private String Name;


    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }
}
