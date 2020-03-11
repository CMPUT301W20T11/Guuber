package com.example.guuber;

import android.content.Intent;

public class Rider extends User {
	public Rider(String phoneNumber, String email, String firstName, String lastName) {
		super(phoneNumber, email, firstName, lastName);
	}

	public void generatePayment(Integer amnt){

	}

	public void makeOffer(Integer amt){

	}

	public void requestRide(){}
	public void cancelReq(){}
	//public void displayProfExternal(){}

	public void displayProfile(){
		Intent intent = new Intent(getActivity(), RiderProfileActivity.class);
		startActivity(intent);

	}

}
