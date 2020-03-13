package com.example.guuber;

import java.util.ArrayList;

/**
 * User wallet, used for fund storage and movement. Also maintains a transaction history.
 */
public class Wallet {
	private double balance;							// Wallet balance
	private ArrayList<Transaction> transactions;    // List of wallet transactions

	/**
	 * Construct wallet with an empty balance, transaction list
	 */
	public Wallet(){
		balance = 0;
		transactions = new ArrayList<Transaction>();
	}

	/**
	 * Deposit an amount to wallet
	 * @param amount - Amount to deposit
	 */
	public void deposit(double amount) throws IllegalArgumentException{
		if(amount <= 0){
			throw new IllegalArgumentException("Invalid deposit amount");
		}
		balance += amount;
	}

	/**
	 * Attempt to withdraw amount from wallet
	 * @param amount - Amount to withdraw
	 * @throws IllegalArgumentException - Thrown if insufficient funds for withdrawal
	 */
	public void withdraw(double amount) throws IllegalArgumentException{
		if(validateTrans(amount)){
			balance -= amount;
		}
		else {
			throw new IllegalArgumentException("Insufficient funds");
		}
	}

	/**
	 * Validate if a withdrawal is possible
	 * @param amount - Amount attempting to be withdrawn
	 * @return - True if withdrawal is possible
	 */
	public boolean validateTrans(double amount){
		return (balance - amount >= 0);
	}

	/**
	 * Get wallet balance
	 * @return - Wallet balance
	 */
	public double getBalance() {
		return balance;
	}

	/**
	 * Set wallet balance
	 * @param balance - Wallet balance
	 */
	public void setBalance(double balance) {
		this.balance = balance;
	}

	/**
	 * Get list of transactions
	 * @return - ArrayList of transactions
	 */
	public ArrayList<Transaction> getTransactions() {
		return transactions;
	}

	/**
	 * Set list of transactions
	 * @param transactions - ArrayList of transactions
	 */
	public void setTransactions(ArrayList<Transaction> transactions) {
		this.transactions = transactions;
	}

	/**
	 * Add a transaction to the record list
	 * @param transaction - Transaction to be recorded
	 */
	public void recordTrans(Transaction transaction){
		transactions.add(transaction);
	}
}
