package com.example.guuber.model;

/**
 * Rider extension of base user class
 */
public class Rider extends User {
	/**
	 * On create
	 * @param phoneNumber - User phone number
	 * @param email - User email
	 * @param firstName - User first name
	 * @param lastName - User last name
	 */
	public Rider(String phoneNumber, String email, String firstName, String lastName) {
		super(phoneNumber, email, firstName, lastName);
	}

	/**
	 * Get user type label
	 * @return - User label
	 */
	public String getLabel(){
		return "Rider";
	}

//	public void displayProfile(){
//		Intent intent = new Intent(getActivity(), RiderProfileActivity.class);
//		startActivity(intent);
//
//	}

}
