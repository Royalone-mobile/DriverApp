package com.sawatruck.driver.entities;

import java.util.ArrayList;

/**
 * Created by royal on 8/28/2017.
 */

public class Advertisement {
    private String ID;
    private String TruckImageURL;
    private String PickupCity;
    private String PickupCountry;
    private String PickupDate;
    private String DeliveryCity;
    private String DeliveryCountry;
    private String DeliveryDate;
    private String Budget;
    private String Currency;
    private String Date;
    private String TruckTypeName1;
    private String TruckTypeName2;
    private String Distance;
    private SawaTruckLocation PickupLocation;
    private SawaTruckLocation DeliveryLocation;
    private int Status;
    private ArrayList<AdvertisementBooking> Bookings;
    private String ExpirationDate;
    private boolean Negotiable;
    private String RecipientId;
    private String RecipientName;
    private String RecipientPhoneNumber;
    private String SenderId;
    private String SenderName;
    private String SenderPhoneNumber;
    private String DeliveryDescription;
    private String TravelID;
    private String PromoCode;
    private String PaymentType;
    private ArrayList<TravelDetails> TravelDetails;
    private String NewBookingsCount;
    private String Available;
    private String TravelConfirmationCode;
    private int TrackingStatus;

    private String TravelStatus;
    private String TravelStatusString;

    private String TruckID;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getTruckImageURL() {
        return TruckImageURL;
    }

    public void setTruckImageURL(String truckImageURL) {
        TruckImageURL = truckImageURL;
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

    public String getBudget() {
        return Budget;
    }

    public void setBudget(String budget) {
        Budget = budget;
    }

    public String getCurrency() {
        return Currency;
    }

    public void setCurrency(String currency) {
        Currency = currency;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public String getTruckTypeName1() {
        return TruckTypeName1;
    }

    public void setTruckTypeName1(String truckTypeName1) {
        TruckTypeName1 = truckTypeName1;
    }

    public String getTruckTypeName2() {
        return TruckTypeName2;
    }

    public void setTruckTypeName2(String truckTypeName2) {
        TruckTypeName2 = truckTypeName2;
    }

    public String getDistance() {
        return Distance;
    }

    public void setDistance(String distance) {
        Distance = distance;
    }

    public ArrayList<AdvertisementBooking> getBookings() {
        return Bookings;
    }

    public void setBookings(ArrayList<AdvertisementBooking> bookings) {
        Bookings = bookings;
    }

    public SawaTruckLocation getPickupLocation() {
        return PickupLocation;
    }

    public void setPickupLocation(SawaTruckLocation pickupLocation) {
        PickupLocation = pickupLocation;
    }

    public SawaTruckLocation getDeliveryLocation() {
        return DeliveryLocation;
    }

    public void setDeliveryLocation(SawaTruckLocation deliveryLocation) {
        DeliveryLocation = deliveryLocation;
    }

    public String getExpirationDate() {
        return ExpirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        ExpirationDate = expirationDate;
    }


    public boolean isNegotiable() {
        return Negotiable;
    }

    public void setNegotiable(boolean negotiable) {
        Negotiable = negotiable;
    }

    public String getRecipientId() {
        return RecipientId;
    }

    public void setRecipientId(String recipientId) {
        RecipientId = recipientId;
    }

    public String getRecipientName() {
        return RecipientName;
    }

    public void setRecipientName(String recipientName) {
        RecipientName = recipientName;
    }

    public String getRecipientPhoneNumber() {
        return RecipientPhoneNumber;
    }

    public void setRecipientPhoneNumber(String recipientPhoneNumber) {
        RecipientPhoneNumber = recipientPhoneNumber;
    }

    public String getSenderId() {
        return SenderId;
    }

    public void setSenderId(String senderId) {
        SenderId = senderId;
    }

    public String getSenderName() {
        return SenderName;
    }

    public void setSenderName(String senderName) {
        SenderName = senderName;
    }

    public String getSenderPhoneNumber() {
        return SenderPhoneNumber;
    }

    public void setSenderPhoneNumber(String senderPhoneNumber) {
        SenderPhoneNumber = senderPhoneNumber;
    }

    public String getDeliveryDescription() {
        return DeliveryDescription;
    }

    public void setDeliveryDescription(String deliveryDescription) {
        DeliveryDescription = deliveryDescription;
    }

    public String getTravelID() {
        return TravelID;
    }

    public void setTravelID(String travelID) {
        TravelID = travelID;
    }

    public String getPromoCode() {
        return PromoCode;
    }

    public void setPromoCode(String promoCode) {
        PromoCode = promoCode;
    }

    public String getPaymentType() {
        return PaymentType;
    }

    public void setPaymentType(String paymentType) {
        PaymentType = paymentType;
    }

    public ArrayList<com.sawatruck.driver.entities.TravelDetails> getTravelDetails() {
        return TravelDetails;
    }

    public void setTravelDetails(ArrayList<com.sawatruck.driver.entities.TravelDetails> travelDetails) {
        TravelDetails = travelDetails;
    }

    public String getTravelConfirmationCode() {
        return TravelConfirmationCode;
    }

    public void setTravelConfirmationCode(String travelConfirmationCode) {
        TravelConfirmationCode = travelConfirmationCode;
    }

    public int getTrackingStatus() {
        return TrackingStatus;
    }

    public void setTrackingStatus(int trackingStatus) {
        TrackingStatus = trackingStatus;
    }

    public String getTravelStatus() {
        return TravelStatus;
    }

    public void setTravelStatus(String travelStatus) {
        TravelStatus = travelStatus;
    }

    public String getTravelStatusString() {
        return TravelStatusString;
    }

    public void setTravelStatusString(String travelStatusString) {
        TravelStatusString = travelStatusString;
    }

    public String getAvailable() {
        return Available;
    }

    public void setAvailable(String available) {
        Available = available;
    }

    public String getTruckID() {
        return TruckID;
    }

    public void setTruckID(String truckID) {
        TruckID = truckID;
    }

    public String getNewBookingsCount() {
        return NewBookingsCount;
    }

    public void setNewBookingsCount(String newBookingsCount) {
        NewBookingsCount = newBookingsCount;
    }
}
