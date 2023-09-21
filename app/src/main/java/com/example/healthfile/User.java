package com.example.healthfile;

public class User {
    public String profile;
    public String name;
    public String dob;
    public String email;
    public String cnic;
    public String address;
    public String phone;
    public User(){

    }
    public User(String profile, String name, String dob, String email, String cnic, String address, String phone){
        this.profile= profile;
        this.name= name;
       this.cnic= cnic;
       this.email= email;
       this.dob= dob;
       this.address= address;
       this.phone= phone;

    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCnic() {
        return cnic;
    }

    public void setCnic(String cnic) {
        this.cnic = cnic;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}