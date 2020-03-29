package com.example.guuber.controller;

import com.example.guuber.model.Transaction;
import com.example.guuber.model.User;
import com.example.guuber.model.Wallet;

/**
 * Static controller to handle transaction generation and execution
 */
public class TransactionController {
    // Global transaction id counter
    private static long id = 1;

    /**
     * Generate a sequential id for a new transaction
     * @return - Sequential transaction id
     */
    private static synchronized String createID(){
        return String.valueOf(id++);
    }

    /**
     * Generate and execute a transaction between two users
     * @param user1 - User receiving funds
     * @param user2 - User withdrawing funds
     * @param amount - Transaction amount
     * @return - True if transaction success
     */
    public static boolean processTrans(User user1, User user2, Double amount){

        // Get some user emails
        String email1 = user1.getEmail();
        String email2 = user2.getEmail();

        // Generate the transaction messages
        String tMessage1 = "Deposit from " + email2;
        String tMessage2 = "Transfer to " + email1;

        // Generate a common transaction ID
        String tID = createID();

        // Generate the transaction records
        Transaction transaction1 = new Transaction(amount, tID, tMessage1);
        Transaction transaction2 = new Transaction(-amount, tID, tMessage2);

        // Get wallet objects
        Wallet wallet1 = user1.getWallet();
        Wallet wallet2 = user2.getWallet();

        // Move funds
        try{
            wallet2.withdraw(amount);
            wallet1.deposit(amount);
        }catch (IllegalArgumentException e){
            return false;
        }

        // Record the transactions
        wallet1.recordTrans(transaction1);
        wallet2.recordTrans(transaction2);
        return true;
    };

    /**
     * Generate and execute a deposit transaction
     * @param user1 - User receiving funds
     * @param amount - Deposit amount
     * @return - True if transaction success
     */
    public static boolean processDeposit(User user1, Double amount){
        // Generate the transaction messages
        String tMessage1 = "External deposit";

        // Generate transaction
        Transaction transaction = new Transaction(amount, createID(), tMessage1);

        // Get wallet object
        Wallet wallet1 = user1.getWallet();

        // Deposit funds
        wallet1.deposit(amount);

        // Record the transaction
        wallet1.recordTrans(transaction);
        return true;
    }

    /**
     * Generate and execute a withdrawal transaction
     * @param user1 - User withdrawing funds
     * @param amount - Withdrawal amount
     * @return - True if transaction success
     */
    public static boolean processWithdrawal(User user1, Double amount){
        // Generate the transaction messages
        String tMessage1 = "External withdrawal";

        // Generate transaction
        Transaction transaction = new Transaction(-amount, createID(), tMessage1);

        // Get wallet object
        Wallet wallet1 = user1.getWallet();

        // Attempt withdrawal
        try{
            wallet1.withdraw(amount);
        }catch (IllegalArgumentException e){
            return false;
        }

        // Record the transaction
        wallet1.recordTrans(transaction);
        return true;
    }

}
