package com.example.guuber;

public class User {
    private Wallet wallet;
    private String phoneNumber;
    private String email;
    private String firstName;
    private String lastName;


    public User(){
        wallet = new Wallet();
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }
}
