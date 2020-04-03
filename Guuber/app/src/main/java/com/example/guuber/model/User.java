package com.example.guuber.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.fragment.app.DialogFragment;

import com.example.guuber.MapsDriverActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Base app user class
 */
public class User implements Serializable {
    private Wallet wallet;

    private String phoneNumber;
    private String email;
    private String firstName;
    private String lastName;
    private String uid;
    private String username;
    private Integer isRider = 1;

    private Integer posRating;
    private Integer negRating;


    /**
     * Empty constructor for firebase use
     */
    public User() {
        // Intentionally left empty
    }

    /**
     * On create
     *
     * @param phoneNumber - User phone number
     * @param email       - User email
     * @param firstName   - User first name
     * @param lastName    - User last name
     */
    public User(String phoneNumber, String email, String firstName, String lastName, String uname, int posRating, int negRating) {
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = uname;
        this.wallet = new Wallet();

        this.posRating = posRating;
        this.negRating = negRating;
    }

    public int getPosRating(){return this.posRating;}

    public void setPosRating(int posRating) {
        this.posRating = posRating;
    }

    public int getNegRating() {return this.negRating;}

    public void setNegRating(int negRating) {
        this.negRating = negRating;
    }

    /**
     * adjusts the user rating based on external user's review
     *
     * @param bool
     */
    public void adjustRating(Boolean bool) {
        if (bool) {
            this.posRating = this.posRating + 1;
        } else {
            this.negRating = this.negRating + 1;
        }
    }

    /**
     * Set rider status
     *
     * @param rider
     */
    public void setRider(Integer rider) {
        isRider = rider;
    }

    /**
     * Get rider status
     *
     * @return
     */
    public Integer getRider() {
        return isRider;
    }

    /**
     * Get user wallet
     *
     * @return - User wallet
     */
    public Wallet getWallet() {
        return wallet;
    }

    /**
     * Set user wallet
     *
     * @param wallet - User wallet
     */
    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    /**
     * Get user phone number
     *
     * @return - User phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Set user phone number
     *
     * @param phoneNumber - User phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Get user email
     *
     * @return - User email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set user email
     *
     * @param email - User email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Get user first name
     *
     * @return - User first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Set user first name
     *
     * @param firstName - User first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Get user last name
     *
     * @return - User last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Set user last name
     *
     * @param lastName - User last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


}

