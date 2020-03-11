package com.example.guuber;

import android.app.Activity;
import android.content.Intent;

public class Driver extends User {
	private Vehicle vehicle;
	private String status;

	public Driver(String phoneNumber, String email, String firstName, String lastName, Vehicle vehicle) {
		super(phoneNumber, email, firstName, lastName);
		this.vehicle = vehicle;
		this.status = "AVAILABLE";
	}

	public String getLabel(){
		return "Driver";
	}

	public void acceptOffer(){

	}

	public void rejectOffer(){

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
		this.status = status;
	}

	public void regVehicle(){}
	public void scanQr(){}
	public void displayProfExternal(){}

	public void displayProfile(Activity activity){
		Intent intent;
		intent = new Intent(getActivity(), DriverProfilActivity.class);
		startActivity(intent);

	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}
}
