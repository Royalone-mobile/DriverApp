package com.sawatruck.driver.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by royal on 8/28/2017.
 */

public class User {
    @SerializedName("userid")
    private String userId;

    @SerializedName("username")
    private String userName;

    @SerializedName("IsAnonymous")
    private boolean anonymous;

    @SerializedName("fullname")
    private String fullName;

    private String email;

    @SerializedName("EmailConfirmed")
    private boolean emailConfirmed;

    @SerializedName("PhoneNumberConfirmed")
    private boolean phoneNumberConfirmed;
    private boolean gender;

    @SerializedName("photo")
    private String photoUrl;

    private String type;

    @SerializedName("access_token")
    private String token;


    private String phone;

    private String DefaultCurrencyID;


    public boolean isAnonymous() {
        return anonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public boolean isEmailConfirmed() {
        return emailConfirmed;
    }

    public void setEmailConfirmed(boolean emailConfirmed) {
        this.emailConfirmed = emailConfirmed;
    }

    public boolean isPhoneNumberConfirmed() {
        return phoneNumberConfirmed;
    }

    public void setPhoneNumberConfirmed(boolean phoneNumberConfirmed) {
        this.phoneNumberConfirmed = phoneNumberConfirmed;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDefaultCurrencyID() {
        return DefaultCurrencyID;
    }

    public void setDefaultCurrencyID(String defaultCurrencyID) {
        DefaultCurrencyID = defaultCurrencyID;
    }
}
