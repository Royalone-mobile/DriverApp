package com.sawatruck.driver.entities;

/**
 * Created by royal on 11/29/2017.
 */

public class GetToDo {
    private String ID;
    private String Number;
    private String StartDate;
    private String EndDate;
    private String UserID;
    private String SessionStatus;
    private String TravelStatus;
    private String Type;
    private String Message;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        Number = number;
    }

    public String getStartDate() {
        return StartDate;
    }

    public void setStartDate(String startDate) {
        StartDate = startDate;
    }

    public String getEndDate() {
        return EndDate;
    }

    public void setEndDate(String endDate) {
        EndDate = endDate;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getSessionStatus() {
        return SessionStatus;
    }

    public void setSessionStatus(String sessionStatus) {
        SessionStatus = sessionStatus;
    }

    public String getTravelStatus() {
        return TravelStatus;
    }

    public void setTravelStatus(String travelStatus) {
        TravelStatus = travelStatus;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }
}
