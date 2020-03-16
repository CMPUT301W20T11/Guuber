package com.example.guuber.model;

import androidx.fragment.app.DialogFragment;

/**
 * Base app user class
 */
public class User{
    private Wallet wallet;
    private String phoneNumber;
    private String email;
    private String firstName;
    private String lastName;
    private String uid;
    private String username;

    /**
     * Empty constructor for firebase use
     */
    public User() {
        // Intentionally left empty
    }

    /**
     * On create
     * @param phoneNumber - User phone number
     * @param email - User email
     * @param firstName - User first name
     * @param lastName - User last name
     */
    public User(String phoneNumber, String email, String firstName, String lastName, String uid, String uname) {
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.uid = uid;
        this.username = uname;
        wallet = new Wallet();
    }

    /**
     * Deprecated user constructor (Delete later)
     * @param phoneNumber - User phone number
     * @param email - User email
     * @param firstName - User first name
     * @param lastName - User last name
     */
    public User(String phoneNumber, String email, String firstName, String lastName) {
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        wallet = new Wallet();
    }


    /**
     * Get user wallet
     * @return - User wallet
     */
    public Wallet getWallet() {
        return wallet;
    }

    /**
     * Set user wallet
     * @param wallet - User wallet
     */
    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    /**
     * Get user phone number
     * @return - User phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Set user phone number
     * @param phoneNumber - User phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Get user email
     * @return - User email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set user email
     * @param email - User email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Get user first name
     * @return - User first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Set user first name
     * @param firstName - User first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Get user last name
     * @return - User last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Set user last name
     * @param lastName - User last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Rate a user TODO
     * Uses AggPositive + AggNegative from db to make AggTotal (then from there you can easily make a percentage of Negative to Positive reviews for each user)
     * When a user gets a new rating add it to either the AggPositive or AggNegative
     */
    public void rateUser(){}


}
