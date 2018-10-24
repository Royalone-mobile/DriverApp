package com.sawatruck.driver.entities;

/**
 * Created by royal on 8/28/2017.
 */

public class Currency {
    private String ID;
    private String Country;
    private String Currency1;
    private String Code;
    private String Symbol;
    private String Status;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public String getCurrency1() {
        return Currency1;
    }

    public void setCurrency1(String currency1) {
        Currency1 = currency1;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getSymbol() {
        return Symbol;
    }

    public void setSymbol(String symbol) {
        Symbol = symbol;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
