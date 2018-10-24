package com.sawatruck.driver.entities;

/**
 * Created by royal on 9/15/2017.
 */

public class Offer {
    private String ID;
    private String Name;
    private String LoadTypeName;
    private String LoadTypeImgURL;
    private String Date;
    private Double Price;
    private String Currency;
    private Double LoadBudget;
    private String LoadBudgetCurrency;
    private String PickupCity;
    private String PickupCountry;
    private String PickupDate;
    private String DeliveryCity;
    private String DeliveryCountry;
    private String DeliveryDate;
    private int Status;


    private String TravelID;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getLoadTypeName() {
        return LoadTypeName;
    }

    public void setLoadTypeName(String loadTypeName) {
        LoadTypeName = loadTypeName;
    }

    public String getLoadTypeImgURL() {
        return LoadTypeImgURL;
    }

    public void setLoadTypeImgURL(String loadTypeImgURL) {
        LoadTypeImgURL = loadTypeImgURL;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public Double getPrice() {
        return Price;
    }

    public void setPrice(Double price) {
        Price = price;
    }

    public String getCurrency() {
        return Currency;
    }

    public void setCurrency(String currency) {
        Currency = currency;
    }

    public Double getLoadBudget() {
        return LoadBudget;
    }

    public void setLoadBudget(Double loadBudget) {
        LoadBudget = loadBudget;
    }

    public String getLoadBudgetCurrency() {
        return LoadBudgetCurrency;
    }

    public void setLoadBudgetCurrency(String loadBudgetCurrency) {
        LoadBudgetCurrency = loadBudgetCurrency;
    }

    public String getPickupCity() {
        return PickupCity;
    }

    public void setPickupCity(String pickupCity) {
        PickupCity = pickupCity;
    }

    public String getPickupCountry() {
        return PickupCountry;
    }

    public void setPickupCountry(String pickupCountry) {
        PickupCountry = pickupCountry;
    }

    public String getPickupDate() {
        return PickupDate;
    }

    public void setPickupDate(String pickupDate) {
        PickupDate = pickupDate;
    }

    public String getDeliveryCity() {
        return DeliveryCity;
    }

    public void setDeliveryCity(String deliveryCity) {
        DeliveryCity = deliveryCity;
    }

    public String getDeliveryCountry() {
        return DeliveryCountry;
    }

    public void setDeliveryCountry(String deliveryCountry) {
        DeliveryCountry = deliveryCountry;
    }

    public String getDeliveryDate() {
        return DeliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        DeliveryDate = deliveryDate;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public String getTravelID() {
        return TravelID;
    }

    public void setTravelID(String travelID) {
        TravelID = travelID;
    }
}
