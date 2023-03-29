package com.mstudio.android.mstory.app.model;

public class User {
    private String id;
    private String user_name;
    private String image_url;
    private String bio_account;
    private String image_profile;
    private String verified_badge;

    public User(String id, String user_name, String image_url, String bio_account, String image_profile, String verified_badge) {
        this.id = id;
        this.user_name = user_name;
        this.image_url = image_url;
        this.bio_account = bio_account;
        this.image_profile = image_profile;
        this.verified_badge = verified_badge;
    }
    public User(){

    }

    public String getVerified_badge() {
        return verified_badge;
    }

    public void setVerified_badge(String verified_badge) {
        this.verified_badge = verified_badge;
    }

    public String getImage_profile() {
        return image_profile;
    }

    public void setImage_profile(String image_profile) {
        this.image_profile = image_profile;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getBio_account() {
        return bio_account;
    }

    public void setBio(String bio) {
        this.bio_account = bio_account;
    }
}
