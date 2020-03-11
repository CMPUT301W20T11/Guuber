package com.example.guuber;

public class User {
    private Wallet wallet;
    private String phoneNumber;
    private String email;
    private String firstName;
    private String lastName;
    public String image;



    public User(String phoneNumber, String email, String firstName, String lastName) {
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        wallet = new Wallet();
    }



    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setImage(String image){this.image = image;}

    public String getImage(){return this.image;}

    public void rateUser(){}
    public void viewProfile(){}
}
