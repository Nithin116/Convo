package com.example.nchat;

public class firebasemodel {

    String name;
    String image;
    String Uid;
    String status;


    public firebasemodel(String name, String image, String uid, String status) {
        this.name = name;
        this.image = image;
        Uid = uid;
        this.status = status;
    }

    public firebasemodel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
