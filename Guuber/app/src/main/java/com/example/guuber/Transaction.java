package com.example.guuber;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Transaction {
    private static DateFormat dformat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    private static DateFormat tformat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
    private Date time;
    private Date date;
    private double amount;
    private String id;

}
