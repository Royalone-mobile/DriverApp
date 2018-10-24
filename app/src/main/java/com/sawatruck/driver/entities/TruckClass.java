package com.sawatruck.driver.entities;

import java.util.ArrayList;

/**
 * Created by royal on 8/30/2017.
 */

public class TruckClass {
    private Integer ClassID;
    private String Name;
    private ArrayList<TruckType> Types;
    private ArrayList<TruckBodyType> Bodytypes;


    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public ArrayList<TruckType> getTypes() {
        return Types;
    }

    public void setTypes(ArrayList<TruckType> types) {
        Types = types;
    }

    public ArrayList<TruckBodyType> getBodytypes() {
        return Bodytypes;
    }

    public void setBodytypes(ArrayList<TruckBodyType> bodytypes) {
        Bodytypes = bodytypes;
    }

    public Integer getClassID() {
        return ClassID;
    }

    public void setClassID(Integer classID) {
        ClassID = classID;
    }
}
