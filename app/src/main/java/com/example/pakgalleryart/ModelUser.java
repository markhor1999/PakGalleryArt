package com.example.pakgalleryart;

public class ModelUser {
    String contact, country, dob, fullname, profileimage, relationshipstatus, status, username;

    public ModelUser() {
    }

    public ModelUser(String contact, String country, String dob, String fullname, String profileimage, String relationshipstatus, String status, String username) {
        this.contact = contact;
        this.country = country;
        this.dob = dob;
        this.fullname = fullname;
        this.profileimage = profileimage;
        this.relationshipstatus = relationshipstatus;
        this.status = status;
        this.username = username;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getRelationshipstatus() {
        return relationshipstatus;
    }

    public void setRelationshipstatus(String relationshipstatus) {
        this.relationshipstatus = relationshipstatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
