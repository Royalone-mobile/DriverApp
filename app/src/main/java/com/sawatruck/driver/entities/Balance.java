package com.sawatruck.driver.entities;

/**
 * Created by royal on 8/28/2017.
 */

public class Balance {
    private String ID;
    private String Balance;
    private String PromoCode;
    private String Points;
    private String Currency;
    private String Dues;
    public Balance(){
        ID = "";
        Balance = "";
        PromoCode = "";
        Points = "";
        Currency = "";
    }
    public String getBalance() {
        return Balance;
    }

    public void setBalance(String balance) {
        Balance = balance;
    }

    public String getPromoCode() {
        return PromoCode;
    }

    public void setPromoCode(String promoCode) {
        PromoCode = promoCode;
    }

    public String getPoints() {
        return Points;
    }

    public void setPoints(String points) {
        Points = points;
    }

    public String getCurrency() {
        return Currency;
    }

    public void setCurrency(String currency) {
        Currency = currency;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getDues() {
        return Dues;
    }

    public void setDues(String dues) {
        Dues = dues;
    }
}
