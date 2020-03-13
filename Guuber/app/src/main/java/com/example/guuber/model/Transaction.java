package com.example.guuber.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Record for a transaction
 */
public class Transaction {
    private static DateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    private static DateFormat tFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

    private String time;
    private String date;
    private double amount;
    private String id;

    /**
     * Transaction between wallets
     * @param amount - Transaction amount
     * @param id - Transaction id
     */
    public Transaction(double amount, String id) {
        Date datetime = new Date();
        this.time = tFormat.format(datetime);
        this.date = dFormat.format(datetime);

        this.amount = amount;
        this.id = id;
    }


    /**
     * Get the time of transaction
     * @return - Formatted string of time of transaction
     */
    public String getTime() {
        return time;
    }

    /**
     * Set the time of transaction by parsing a date object
     * @param time - Time in the form of a date object
     */
    public void setTime(Date time) {
        this.time = tFormat.format(time);
    }

    /**
     * Return the date of transaction
     * @return - Formatted string of date of transaction
     */
    public String getDate() {
        return date;
    }

    /**
     * Set the date of transaction by parsing a date object
     * @param date - Date in the form of a date object
     */
    public void setDate(Date date) {
        this.date = dFormat.format(date);
    }

    /**
     * Get the transaction amount
     * @return - Transaction amount
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Set the transaction amount
     * @param amount - Transaction amount
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    /**
     * Get transaction id
     * @return - Transaction id
     */
    public String getId() {
        return id;
    }

    /**
     * Set transaction id
     * @param id - Transaction id
     */
    public void setId(String id) {
        this.id = id;
    }
}
