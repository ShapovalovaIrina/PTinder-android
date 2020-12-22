package com.trkpo.ptinder.pojo;

public class Notification {
    private String id;
    private String title;
    private String text;
    private boolean isRead;
    private String googleId;

    public Notification(String id, String title, String text, boolean isRead, String googleId) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.isRead = isRead;
        this.googleId = googleId;
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

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }
}
