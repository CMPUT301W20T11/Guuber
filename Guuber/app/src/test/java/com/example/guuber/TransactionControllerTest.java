package com.example.guuber;

import com.example.guuber.controller.TransactionController;
import com.example.guuber.model.User;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TransactionControllerTest {
	private User mockUser(){
		User user = new User("780999999", "a@gmail.com", "Matthew", "Dziubina");
		user.getWallet().deposit(20);
		return user;
	}

	@Test
	public void transferTransactionTest(){
		User user1 = mockUser();
		User user2 = mockUser();

		// Transfer 20 dollars from user2 to user1
		TransactionController.processTrans(user1, user2, 20.0);

		// Ensure balances are updated
		assertEquals(40, user1.getWallet().getBalance());
		assertEquals(0, user2.getWallet().getBalance());

		// Ensure transactions were recorded
		assertEquals(1, user1.getWallet().getTransactions().size());
		assertEquals(1, user2.getWallet().getTransactions().size());

		// Ensure proper ID was recorded (It's 4 because 3 transactions were done in the deposit test and id was incremented)
		assertEquals("4", user1.getWallet().getTransactions().get(0).getId());
		assertEquals("4", user2.getWallet().getTransactions().get(0).getId());

		// Attempt to transfer an invalid amount according to balances
		assertFalse(TransactionController.processTrans(user1, user2, 20.0));
	}

	@Test
	public void depositTransactionTest(){
		User user1 = mockUser();

		// Deposit 20 dollars in two increments
		TransactionController.processDeposit(user1, 10.0);
		TransactionController.processDeposit(user1, 10.0);

		// Ensure balance is updated
		assertEquals(40, user1.getWallet().getBalance());

		// Ensure proper ID was recorded
		assertEquals("1", user1.getWallet().getTransactions().get(0).getId());
		assertEquals("2", user1.getWallet().getTransactions().get(1).getId());

		// Attempt to deposit an invalid amount

		assertThrows(IllegalArgumentException.class, () -> {
			assertFalse(TransactionController.processDeposit(user1,-10.0));
		});
	}

}
