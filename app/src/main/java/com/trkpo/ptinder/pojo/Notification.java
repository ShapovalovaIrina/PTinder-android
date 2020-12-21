package com.trkpo.ptinder.pojo;

public class Notification {
    private String title;
    private String text;
    private boolean isRead;

    public Notification(String title, String text, boolean isRead) {
        this.title = title;
        this.text = text;
        this.isRead = isRead;
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
}
