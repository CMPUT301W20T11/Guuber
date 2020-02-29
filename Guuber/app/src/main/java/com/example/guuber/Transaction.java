package com.example.guuber;

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
    private User user1;
    private User user2;

    /**
     * Transaction between wallets
     * @param amount - Transaction amount
     * @param user1 - User receiving money
     * @param user2 - User giving money
     */
    public Transaction(double amount, String id, User user1, User user2) {
        Date datetime = new Date();
        this.time = tFormat.format(datetime);
        this.date = dFormat.format(datetime);

        this.amount = amount;
        this.id = id;
        this.user1 = user1;
        this.user2 = user2;
    }

    /**
     * Deposit transaction
     * @param amount - Transaction amount
     * @param user1 - User receiving money
     */
    public Transaction(double amount, String id, User user1) {
        Date datetime = new Date();
        this.time = tFormat.format(datetime);
        this.date = dFormat.format(datetime);

        this.amount = amount;
        this.id = id;
        this.user1 = user1;
        this.user2 = null;
    }

    public String getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = tFormat.format(time);
    }

    public String getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = dFormat.format(date);
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser1() {
        return user1;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    public User getUser2() {
        return user2;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
    }
}
