package com.example.guuber;

import android.content.Intent;

public class Driver extends User {
	private Vehicle vehicle;
	private String status;

	public Driver(String phoneNumber, String email, String firstName, String lastName, Vehicle vehicle) {
		super(phoneNumber, email, firstName, lastName);
		this.vehicle = vehicle;
		this.status = "AVAILABLE";
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		if (this.status == "AVAILABLE"){this.status = "BUSY";}
		else{this.status = "AVAILABLE";}
	}

	public void acceptOffer(){

	}

	public void rejectOffer(){

	}

	public void regVehicle(){}
	public void scanQr(){}
	public void displayProfExternal(){}

	public void displayProfile(){
		Intent intent = new Intent(getActivity(), DriverProfileActivity.class);
		startActivity(intent);

	}
}
