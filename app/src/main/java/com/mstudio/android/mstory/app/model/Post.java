package com.mstudio.android.mstory.app.model;

public class Post {
    private String cap_post;
    private String image_post;
    private String user_name;
    private String image_account;
    private String value_like;
    private String value_comment;
    private String value_send;
    private String date_time;
    private String view_post;
    private String publisher;
    private String id;
    public Post(String cap_post, String image_post, String user_name, String image_account, String value_like, String value_comment, String value_send, String date_time, String view_post,String publisher,String id) {
        this.cap_post = cap_post;
        this.user_name = user_name;
        this.image_post = image_post;
        this.image_account = image_account;
        this.value_like = value_like;
        this.value_comment = value_comment;
        this.value_send = value_send;
        this.date_time = date_time;
        this.view_post = view_post;
        this.publisher = publisher;
        this.id = id;
    }
    public Post() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher_post(String publisher) {
        this.publisher = publisher;
    }

    public String getView_post() {
        return view_post;
    }

    public void setView_post(String view_post) {
        this.view_post = view_post;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public void setValue_like(String value_like) {
        this.value_like = value_like;
    }

    public void setValue_comment(String value_comment) {
        this.value_comment = value_comment;
    }

    public void setValue_send(String value_send) {
        this.value_send = value_send;
    }

    public String getValue_like() {
        return value_like;
    }

    public String getValue_comment() {
        return value_comment;
    }

    public String getValue_send() {
        return value_send;
    }

    public String getImage_account() {
        return image_account;
    }

    public void setImage_account(String image_account) {
        this.image_account = image_account;
    }

    public String getUser_name() {
        return user_name;
    }
    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
    public String getCap_post() {
        return cap_post;
    }

    public void setCap_post(String cap_post) {
       this.cap_post = cap_post;
    }

    public String getImage_post() {
        return image_post;
    }

    public void setImage_post(String image_post) {
        this.image_post = image_post;
    }
}
