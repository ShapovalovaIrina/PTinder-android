package com.trkpo.ptinder.pojo;

import android.graphics.Bitmap;

public class Feed {
    private String author;
    private String score;
    private String title;
    private Bitmap content;

    public Feed(String author, String score, String title, Bitmap content) {
        this.author = author;
        this.score = score;
        this.title = title;
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Bitmap getContent() {
        return content;
    }

    public void setContent(Bitmap content) {
        this.content = content;
    }
}
