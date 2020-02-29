package com.example.guuber;

public class TransactionController {
    private static long id = 1;

    private static synchronized String createID(){
        return String.valueOf(id++);
    }

    private static boolean processTrans(User user1, User user2, Double amount){
        // Generate transaction
        Transaction transaction = new Transaction(amount, createID(), user1, user2);

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

    private static boolean processDeposit(User user1, Double amount){
        // Generate transaction
        Transaction transaction = new Transaction(amount, createID(), user1);

        // Get wallet object
        Wallet wallet1 = user1.getWallet();

        // Deposit funds
        wallet1.deposit(amount);

        // Record the transaction
        wallet1.recordTrans(transaction);
        return true;
    }

}
