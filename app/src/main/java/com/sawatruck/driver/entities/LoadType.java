package com.sawatruck.driver.entities;

/**
 * Created by royal on 8/29/2017.
 */

public class LoadType {
    private int LoadTypeID;
    private String Name;
    private String Photo;

    public int getLoadTypeID() {
        return LoadTypeID;
    }

    public void setLoadTypeID(int loadTypeID) {
        LoadTypeID = loadTypeID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhoto() {
        return Photo;
    }

    public void setPhoto(String photo) {
        Photo = photo;
    }
}
