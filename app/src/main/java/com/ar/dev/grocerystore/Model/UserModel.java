package com.ar.dev.grocerystore.Model;

public class UserModel {
    private String id,name, email, pass;
    private String address, city, pincode,contact;

    public UserModel(){

    }

    public UserModel(String id, String name, String email, String pass, String address, String city, String pincode, String contact) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.pass = pass;
        this.address = address;
        this.city = city;
        this.pincode = pincode;
        this.contact = contact;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPass() {
        return pass;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getPincode() {
        return pincode;
    }

    public String getContact() {
        return contact;
    }
}

