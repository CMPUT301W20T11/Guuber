package com.example.guuber;

import com.example.guuber.model.Transaction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransactionTest {
	private String date;
	private String time;
	private DateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
	private DateFormat tFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
	private Transaction transaction;

	@BeforeEach
	public void setUp() {
		// Get a date to inject
		Date datetime = new Date();

		// Create a mock transaction
		transaction = new Transaction(10, "Test message", datetime);

		// Get the date and time as strings from injected date object
		date = dFormat.format(datetime);
		time = tFormat.format(datetime);
	}

	@Test
	public void creationTest(){
		// Ensure proper amount is recorded
		assertEquals(10, transaction.getAmount());

		// Ensure proper message is recorded
		assertEquals("Test message", transaction.getMessage());

		// Ensure date and time is correct
		assertEquals(date, transaction.getDate());
		assertEquals(time, transaction.getTime());
	}
}
