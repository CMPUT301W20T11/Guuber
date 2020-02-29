package com.example.guuber;

public class TransactionHandler {
    private static long id = 1;

    public static synchronized String createID(){
        return String.valueOf(id++);
    }

    private static void processTrans(){};

}
