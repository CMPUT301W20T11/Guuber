package com.example.guuber;

import java.util.ArrayList;

public class Wallet {
    private double balance;
    private ArrayList<Transaction> transactions;

    public Wallet(){
        balance = 0;
        transactions = new ArrayList<Transaction>();
    }

    public void deposit(double amount){
        balance += amount;
    }

    public void withdraw(double amount){
        if(validateTrans(amount)){
            balance -= amount;
        }
        else {
            throw new IllegalArgumentException("Insufficient funds");
        }
    }

    public boolean validateTrans(double amount){
        return (balance - amount >= 0);
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }

    public void recordTrans(Transaction transaction){
        transactions.add(transaction);
    }
}
