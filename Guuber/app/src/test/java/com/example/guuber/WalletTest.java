package com.example.guuber;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WalletTest {

	private Wallet mockWallet(){
		return new Wallet();
	}

	private Transaction mockTransaction(){
		return new Transaction(10, "1");
	}

	@Test
	public void creationTest() {
		Wallet wallet = mockWallet();

		// Empty balance
		assertEquals(0, wallet.getBalance());
		// Empty transaction history
		assertEquals(wallet.getTransactions().size(), 0);
	}

	@Test
	public void depositTest(){
		Wallet wallet = mockWallet();

		// Deposit 1
		wallet.deposit(1);
		assertEquals(1, wallet.getBalance());

		// Deposit negative
		assertThrows(IllegalArgumentException.class, () -> {
			wallet.deposit(-1);
		});
	}

	@Test
	public void withdrawTest(){
		Wallet wallet = mockWallet();
		wallet.deposit(1);

		// Withdraw 1
		wallet.withdraw(1);
		assertEquals(0, wallet.getBalance());

		// Over-withdraw
		assertThrows(IllegalArgumentException.class, () -> {
			wallet.withdraw(10);
		});
	}
}
