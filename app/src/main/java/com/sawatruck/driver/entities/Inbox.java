package com.sawatruck.driver.entities;

/**
 * Created by royal on 9/15/2017.
 */

public class Inbox {
    private String UserID;
    private String UserFullName;
    private String UserImageUrl;
    private String LastMessage;
    private String LastMessageDate;
    private String MessageTarget;
    private String TargetID;
    private boolean Online;
    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getUserFullName() {
        return UserFullName;
    }

    public void setUserFullName(String userFullName) {
        UserFullName = userFullName;
    }

    public String getUserImageUrl() {
        return UserImageUrl;
    }

    public void setUserImageUrl(String userImageUrl) {
        UserImageUrl = userImageUrl;
    }

    public String getLastMessage() {
        return LastMessage;
    }

    public void setLastMessage(String lastMessage) {
        LastMessage = lastMessage;
    }

    public String getLastMessageDate() {
        return LastMessageDate;
    }

    public void setLastMessageDate(String lastMessageDate) {
        LastMessageDate = lastMessageDate;
    }



    public String getTargetID() {
        return TargetID;
    }

    public void setTargetID(String targetID) {
        TargetID = targetID;
    }

    public String getMessageTarget() {
        return MessageTarget;
    }

    public void setMessageTarget(String messageTarget) {
        MessageTarget = messageTarget;
    }

    public boolean isOnline() {
        return Online;
    }

    public void setOnline(boolean online) {
        Online = online;
    }
}
