package com.example.guuber;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TransactionTest {

	private Transaction mockTransaction(){
		return new Transaction(10, "1");
	}

	@Test
	public void creationTest(){
		Transaction transaction = mockTransaction();

		assertEquals(10, transaction.getAmount());
		assertEquals("1", transaction.getId());
	}
}
