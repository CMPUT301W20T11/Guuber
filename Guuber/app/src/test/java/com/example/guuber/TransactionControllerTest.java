package com.example.guuber;

import com.example.guuber.controller.TransactionController;
import com.example.guuber.model.User;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TransactionControllerTest {
	private User mockUser(){
		User user = new User("780", "email@email.com", "Matt", "D", "matt96", 0, 0);
		user.getWallet().deposit(20);
		return user;
	}

	@Test
	public void transferTransactionTest(){
		User user1 = mockUser();
		User user2 = mockUser();

		// Ensure start balances are correct
		assertEquals(20, user1.getWallet().getBalance());
		assertEquals(20, user2.getWallet().getBalance());

		// Transfer 20 dollars from user2 to user1
		TransactionController.processTrans(user1, user2, 20.0);

		// Ensure balances are updated
		assertEquals(40, user1.getWallet().getBalance());
		assertEquals(0, user2.getWallet().getBalance());

		// Ensure transactions were recorded
		assertEquals(1, user1.getWallet().getTransactions().size());
		assertEquals(1, user2.getWallet().getTransactions().size());

		// Attempt to transfer an invalid amount according to balances
		assertFalse(TransactionController.processTrans(user1, user2, 20.0));
	}

	@Test
	public void depositTransactionTest(){
		User user1 = mockUser();

		// Ensure start balance is correct
		assertEquals(20, user1.getWallet().getBalance());

		// Deposit 20 dollars in two increments
		TransactionController.processDeposit(user1, 10.0);
		TransactionController.processDeposit(user1, 10.0);

		// Ensure balance is updated
		assertEquals(40, user1.getWallet().getBalance());

		// Attempt to deposit an invalid amount

		assertThrows(IllegalArgumentException.class, () -> {
			assertFalse(TransactionController.processDeposit(user1,-10.0));
		});
	}

	@Test
	public void withdrawTransactionTest(){
		User user1 = mockUser();

		// Ensure start balance is correct
		assertEquals(20, user1.getWallet().getBalance());

		// Withdraw 20 dollars in two increments
		TransactionController.processWithdrawal(user1, 10.0);
		TransactionController.processWithdrawal(user1, 10.0);

		// Ensure balance is updated
		assertEquals(0, user1.getWallet().getBalance());

		// Attempt to withdraw an invalid amount
		assertFalse(TransactionController.processWithdrawal(user1,10.0));
	}

}
