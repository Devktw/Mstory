package com.mstudio.android.mstory.app.model;

public class Comment {
    String username;
    String comment;
    String image_url;
    String id;
    String publisher;
    private String date_time;
    public Comment() {

    }

    public Comment(String username, String comment, String image_url, String id,String publisher,String date_time) {
        this.username = username;
        this.comment = comment;
        this.image_url = image_url;
        this.id = id;
        this.publisher = publisher;
        this.date_time = date_time;
    }
    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
