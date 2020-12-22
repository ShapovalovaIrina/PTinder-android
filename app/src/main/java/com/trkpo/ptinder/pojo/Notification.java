package com.trkpo.ptinder.pojo;

public class Notification {
    private String id;
    private String title;
    private String text;
    private boolean isRead;
    private String addresseeGoogleId;
    private String addresseeFromGoogleId;

    public Notification(String id, String title, String text, boolean isRead, String addresseeGoogleId, String addresseeFromGoogleId) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.isRead = isRead;
        this.addresseeGoogleId = addresseeGoogleId;
        this.addresseeFromGoogleId = addresseeFromGoogleId;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddresseeGoogleId() {
        return addresseeGoogleId;
    }

    public void setAddresseeGoogleId(String addresseeGoogleId) {
        this.addresseeGoogleId = addresseeGoogleId;
    }

    public String getAddresseeFromGoogleId() {
        return addresseeFromGoogleId;
    }

    public void setAddresseeFromGoogleId(String addresseeFromGoogleId) {
        this.addresseeFromGoogleId = addresseeFromGoogleId;
    }
}
