package com.sawatruck.driver.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by royal on 9/20/2017.
 */

public class NotificationModel {
    private String ID;
    private String NotificationID;
    private String SawaTruckUserID;
    private String SeenDate;
    private boolean IsSeen;

    @SerializedName("Notification")
    public Notification notification;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getNotificationID() {
        return NotificationID;
    }

    public void setNotificationID(String notificationID) {
        NotificationID = notificationID;
    }

    public String getSawaTruckUserID() {
        return SawaTruckUserID;
    }

    public void setSawaTruckUserID(String sawaTruckUserID) {
        SawaTruckUserID = sawaTruckUserID;
    }

    public String getSeenDate() {
        return SeenDate;
    }

    public void setSeenDate(String seenDate) {
        SeenDate = seenDate;
    }

    public boolean isSeen() {
        return IsSeen;
    }

    public void setSeen(boolean seen) {
        IsSeen = seen;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }


    public class Notification{
        private String URL;
        private String Date;
        private String Message;
        private String CssClass;
        private String Icon;
        private String FullName;
        private String ImageUrl;
        private String ScreenName;
        private String NotificationUserID;
        private String Status;
        private String Text;
        private String Title;
        private String TargetID;


        public String getURL() {
            return URL;
        }

        public void setURL(String URL) {
            this.URL = URL;
        }

        public String getMessage() {
            return Message;
        }

        public void setMessage(String message) {
            Message = message;
        }

        public String getDate() {
            return Date;
        }

        public void setDate(String date) {
            Date = date;
        }

        public String getCssClass() {
            return CssClass;
        }

        public void setCssClass(String cssClass) {
            CssClass = cssClass;
        }

        public String getIcon() {
            return Icon;
        }

        public void setIcon(String icon) {
            Icon = icon;
        }

        public String getFullName() {
            return FullName;
        }

        public void setFullName(String fullName) {
            FullName = fullName;
        }

        public String getImageUrl() {
            return ImageUrl;
        }

        public void setImageUrl(String imageUrl) {
            ImageUrl = imageUrl;
        }

        public String getScreenName() {
            return ScreenName;
        }

        public void setScreenName(String screenName) {
            ScreenName = screenName;
        }

        public String getNotificationUserID() {
            return NotificationUserID;
        }

        public void setNotificationUserID(String notificationUserID) {
            NotificationUserID = notificationUserID;
        }

        public String getStatus() {
            return Status;
        }

        public void setStatus(String status) {
            Status = status;
        }

        public String getText() {
            return Text;
        }

        public void setText(String text) {
            Text = text;
        }

        public String getTitle() {
            return Title;
        }

        public void setTitle(String title) {
            Title = title;
        }

        public String getTargetID() {
            return TargetID;
        }

        public void setTargetID(String targetID) {
            TargetID = targetID;
        }
    }
}
