package com.example.guuber;

public class Driver extends User {
	private Vehicle vehicle;

	public Driver(String phoneNumber, String email, String firstName, String lastName, Vehicle vehicle) {
		super(phoneNumber, email, firstName, lastName);
		this.vehicle = vehicle;
	}


	public void acceptOffer(){

	}

	public void rejectOffer(){

	}

	public void regVehicle(){}
	public void scanQr(){}
}
