package com.example.guuber;

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
        // Generate transaction
        Transaction transaction = new Transaction(amount, createID());

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
        wallet1.recordTrans(transaction);
        wallet2.recordTrans(transaction);
        return true;
    };

    /**
     * Generate and execute a deposit transaction
     * @param user1 - User receiving funds
     * @param amount - Deposit amount
     * @return - True if transaction success
     */
    public static boolean processDeposit(User user1, Double amount){
        // Generate transaction
        Transaction transaction = new Transaction(amount, createID());

        // Get wallet object
        Wallet wallet1 = user1.getWallet();

        // Deposit funds
        wallet1.deposit(amount);

        // Record the transaction
        wallet1.recordTrans(transaction);
        return true;
    }

}
