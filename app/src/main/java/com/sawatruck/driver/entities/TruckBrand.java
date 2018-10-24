package com.sawatruck.driver.entities;

/**
 * Created by royal on 8/29/2017.
 */

public class TruckBrand {
    private String CompanyID;
    private String Name;
    private String ImageUrl;

    public String getCompanyID() {
        return CompanyID;
    }

    public void setCompanyID(String companyID) {
        CompanyID = companyID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }
}
