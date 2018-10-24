package com.sawatruck.driver.entities;

/**
 * Created by royal on 9/10/2017.
 */

public class Message {
    private String ReceiverID;
    private String SenderID;
    private String SenderFullName;
    private String SenderName;
    private String SenderImageURL;
    private String Text;
    private String Date;
    private String SeenDate;
    private boolean IsMeSender;
    private boolean IsSeen;
    private String ID;

    public String getReceiverID() {
        return ReceiverID;
    }

    public void setReceiverID(String receiverID) {
        ReceiverID = receiverID;
    }

    public String getSenderID() {
        return SenderID;
    }

    public void setSenderID(String senderID) {
        SenderID = senderID;
    }

    public String getSenderFullName() {
        return SenderFullName;
    }

    public void setSenderFullName(String senderFullName) {
        SenderFullName = senderFullName;
    }

    public String getSenderName() {
        return SenderName;
    }

    public void setSenderName(String senderName) {
        SenderName = senderName;
    }

    public String getSenderImageURL() {
        return SenderImageURL;
    }

    public void setSenderImageURL(String senderImageURL) {
        SenderImageURL = senderImageURL;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getSeenDate() {
        return SeenDate;
    }

    public void setSeenDate(String seenDate) {
        SeenDate = seenDate;
    }

    public boolean isMeSender() {
        return IsMeSender;
    }

    public void setMeSender(boolean meSender) {
        IsMeSender = meSender;
    }

    public boolean isSeen() {
        return IsSeen;
    }

    public void setSeen(boolean seen) {
        IsSeen = seen;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
