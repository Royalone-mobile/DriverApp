package com.sawatruck.driver.entities;

import java.util.ArrayList;

/**
 * Created by royal on 9/14/2017.
 */

public class Transaction {
    private String ID;
    private String InvoiceID;
    private String CoponID;
    private String Date;
    private String Value;
    private String DateOfDeposit;
    private String DepositNo;
    private String PaymentHash;
    private String CurrencyID;
    private String MadeBy;
    private int TransactionType;
    private int  PaymentMethodType;
    private String TransactionTypeText;
    private String PaymentMethodTypeText;
    private String Invoice;

    private ArrayList<TransactionDetail> TransactionDetail;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getInvoiceID() {
        return InvoiceID;
    }

    public void setInvoiceID(String invoiceID) {
        InvoiceID = invoiceID;
    }

    public String getCoponID() {
        return CoponID;
    }

    public void setCoponID(String coponID) {
        CoponID = coponID;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }

    public String getDateOfDeposit() {
        return DateOfDeposit;
    }

    public void setDateOfDeposit(String dateOfDeposit) {
        DateOfDeposit = dateOfDeposit;
    }

    public String getDepositNo() {
        return DepositNo;
    }

    public void setDepositNo(String depositNo) {
        DepositNo = depositNo;
    }

    public String getPaymentHash() {
        return PaymentHash;
    }

    public void setPaymentHash(String paymentHash) {
        PaymentHash = paymentHash;
    }

    public String getCurrencyID() {
        return CurrencyID;
    }

    public void setCurrencyID(String currencyID) {
        CurrencyID = currencyID;
    }

    public String getMadeBy() {
        return MadeBy;
    }

    public void setMadeBy(String madeBy) {
        MadeBy = madeBy;
    }

    public int getTransactionType() {
        return TransactionType;
    }

    public void setTransactionType(int transactionType) {
        TransactionType = transactionType;
    }

    public int getPaymentMethodType() {
        return PaymentMethodType;
    }

    public void setPaymentMethodType(int paymentMethodType) {
        PaymentMethodType = paymentMethodType;
    }

    public String getTransactionTypeText() {
        return TransactionTypeText;
    }

    public void setTransactionTypeText(String transactionTypeText) {
        TransactionTypeText = transactionTypeText;
    }

    public String getPaymentMethodTypeText() {
        return PaymentMethodTypeText;
    }

    public void setPaymentMethodTypeText(String paymentMethodTypeText) {
        PaymentMethodTypeText = paymentMethodTypeText;
    }

    public String getInvoice() {
        return Invoice;
    }

    public void setInvoice(String invoice) {
        Invoice = invoice;
    }

    public ArrayList<TransactionDetail> getTransactionDetail() {
        return TransactionDetail;
    }

    public void setTransactionDetail(ArrayList<TransactionDetail> transactionDetail) {
        TransactionDetail = transactionDetail;
    }

    public static class TransactionDetail {
        private String HdrID;
        private String AccountID;
        private String Currency;
        private Double Value;
        private String TransactionHdr;

        public String getHdrID() {
            return HdrID;
        }

        public void setHdrID(String hdrID) {
            HdrID = hdrID;
        }

        public String getAccountID() {
            return AccountID;
        }

        public void setAccountID(String accountID) {
            AccountID = accountID;
        }

        public String getCurrency() {
            return Currency;
        }

        public void setCurrency(String currency) {
            Currency = currency;
        }

        public Double getValue() {
            return Value;
        }

        public void setValue(Double value) {
            Value = value;
        }

        public String getTransactionHdr() {
            return TransactionHdr;
        }

        public void setTransactionHdr(String transactionHdr) {
            TransactionHdr = transactionHdr;
        }
    }

}
