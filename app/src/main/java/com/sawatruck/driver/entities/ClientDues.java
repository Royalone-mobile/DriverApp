package com.sawatruck.driver.entities;

/**
 * Created by royal on 11/9/2017.
 */

public class ClientDues {
    private String TravelID;
    private String FromUserID;
    private String FromUserFirstName;
    private String FromUserLastName;
    private String ToUserID;
    private String ToUserFirstName;
    private String ToUserLastName;
    private String DueDate;
    private String TransactionDate;
    private String LoadDetails;
    private String OfferDetails;
    private String Amount;

    public String getTravelID() {
        return TravelID;
    }

    public void setTravelID(String travelID) {
        TravelID = travelID;
    }

    public String getFromUserID() {
        return FromUserID;
    }

    public void setFromUserID(String fromUserID) {
        FromUserID = fromUserID;
    }

    public String getFromUserFirstName() {
        return FromUserFirstName;
    }

    public void setFromUserFirstName(String fromUserFirstName) {
        FromUserFirstName = fromUserFirstName;
    }

    public String getFromUserLastName() {
        return FromUserLastName;
    }

    public void setFromUserLastName(String fromUserLastName) {
        FromUserLastName = fromUserLastName;
    }

    public String getToUserID() {
        return ToUserID;
    }

    public void setToUserID(String toUserID) {
        ToUserID = toUserID;
    }

    public String getDueDate() {
        return DueDate;
    }

    public void setDueDate(String dueDate) {
        DueDate = dueDate;
    }

    public String getToUserFirstName() {
        return ToUserFirstName;
    }

    public void setToUserFirstName(String toUserFirstName) {
        ToUserFirstName = toUserFirstName;
    }

    public String getToUserLastName() {
        return ToUserLastName;
    }

    public void setToUserLastName(String toUserLastName) {
        ToUserLastName = toUserLastName;
    }

    public String getTransactionDate() {
        return TransactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        TransactionDate = transactionDate;
    }

    public String getLoadDetails() {
        return LoadDetails;
    }

    public void setLoadDetails(String loadDetails) {
        LoadDetails = loadDetails;
    }

    public String getOfferDetails() {
        return OfferDetails;
    }

    public void setOfferDetails(String offerDetails) {
        OfferDetails = offerDetails;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }
}
