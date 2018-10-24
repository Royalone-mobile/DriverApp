package com.sawatruck.driver.entities;

/**
 * Created by royal on 8/29/2017.
 */

public class Coupon {
    private String ID;
    private String Code;
    private String CouponType;
    private String CouponTypeID;
    private String PublishDate;
    private String StartDate;
    private String ExpiryDate;
    private double Value;
    private boolean IsPercent;
    private int Currency;
    private int CountryID;
    private int Target;
    private boolean Active;
    private boolean Deleted;
    private String DriverPointsHistory;
    private String UsersActiveCopon;
    private String UsersArchiveCopons;
    private String Invoice;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getCouponType() {
        return CouponType;
    }

    public void setCouponType(String couponType) {
        CouponType = couponType;
    }

    public String getCouponTypeID() {
        return CouponTypeID;
    }

    public void setCouponTypeID(String couponTypeID) {
        CouponTypeID = couponTypeID;
    }

    public String getPublishDate() {
        return PublishDate;
    }

    public void setPublishDate(String publishDate) {
        PublishDate = publishDate;
    }

    public String getStartDate() {
        return StartDate;
    }

    public void setStartDate(String startDate) {
        StartDate = startDate;
    }

    public String getExpiryDate() {
        return ExpiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        ExpiryDate = expiryDate;
    }

    public double getValue() {
        return Value;
    }

    public void setValue(double value) {
        Value = value;
    }

    public boolean isPercent() {
        return IsPercent;
    }

    public void setPercent(boolean percent) {
        IsPercent = percent;
    }

    public int getCurrency() {
        return Currency;
    }

    public void setCurrency(int currency) {
        Currency = currency;
    }

    public int getCountryID() {
        return CountryID;
    }

    public void setCountryID(int countryID) {
        CountryID = countryID;
    }

    public int getTarget() {
        return Target;
    }

    public void setTarget(int target) {
        Target = target;
    }

    public boolean isActive() {
        return Active;
    }

    public void setActive(boolean active) {
        Active = active;
    }

    public boolean isDeleted() {
        return Deleted;
    }

    public void setDeleted(boolean deleted) {
        Deleted = deleted;
    }

    public String getDriverPointsHistory() {
        return DriverPointsHistory;
    }

    public void setDriverPointsHistory(String driverPointsHistory) {
        DriverPointsHistory = driverPointsHistory;
    }

    public String getUsersActiveCopon() {
        return UsersActiveCopon;
    }

    public void setUsersActiveCopon(String usersActiveCopon) {
        UsersActiveCopon = usersActiveCopon;
    }

    public String getUsersArchiveCopons() {
        return UsersArchiveCopons;
    }

    public void setUsersArchiveCopons(String usersArchiveCopons) {
        UsersArchiveCopons = usersArchiveCopons;
    }

    public String getInvoice() {
        return Invoice;
    }

    public void setInvoice(String invoice) {
        Invoice = invoice;
    }
}
