package com.sawatruck.driver.entities;

/**
 * Created by royal on 10/3/2017.
 */

public class PayPalPayment {
    private String payerID;
    private String amount;

    public String getPayerID() {
        return payerID;
    }

    public void setPayerID(String payerID) {
        this.payerID = payerID;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
