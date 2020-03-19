package com.example.guuber.model;

/**
 * Driver subclass of user, additional driver functionality added to user base
 */
public class Driver extends User {
	private Vehicle vehicle;
	private String status;

	/**
	 * On create
	 * @param phoneNumber - User phone number
	 * @param email - User email
	 * @param firstName - User first name
	 * @param lastName - User last name
	 * @param vehicle - User vehicle
	 */
	public Driver(String phoneNumber, String email, String firstName, String lastName, String uid, String uname, Vehicle vehicle) {
		super(phoneNumber, email, firstName, lastName, uid, uname);
		this.vehicle = vehicle;
		this.status = "AVAILABLE";
	}

	/**
	 * Deprecated constructor (Delete later)
	 * @param phoneNumber - User phone number
	 * @param email - User email
	 * @param firstName - User first name
	 * @param lastName - User last name
	 * @param vehicle - User vehicle
	 */
	public Driver(String phoneNumber, String email, String firstName, String lastName, Vehicle vehicle) {
		super(phoneNumber, email, firstName, lastName);
		this.vehicle = vehicle;
		this.status = "AVAILABLE";
	}

	/**
	 * Get user type
	 * @return - Driver label
	 */
	public String getLabel(){
		return "Driver";
	}

	/**
	 * Accept an offer TODO
	 */
	public void acceptOffer(){

	}

	/**
	 * Reject an offer TODO
	 */
	public void rejectOffer(){

	}

	/**
	 * Get the drivers vehicle object
	 * @return - Driver's vehicle
	 */
	public Vehicle getVehicle() {
		return vehicle;
	}

	/**
	 * Set the drivers vehicle
	 * @param vehicle - Drivers vehicle
	 */
	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

	/**
	 * Get the drivers status (Available, Busy)
	 * @return - Driver status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Set the drivers status
	 * @param status - Driver status
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * Register a drivers vehicle into the database TODO
	 */
	public void regVehicle(){}

	/**
	 * Scan a qrCode TODO
	 */
	public void scanQr(){}


//	public void displayProfExternal(){}
//
//	public void displayProfile(Activity activity){
//		Intent intent;
//		intent = new Intent(getActivity(), DriverProfilActivity.class);
//		startActivity(intent);
//	}
}
