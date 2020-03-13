package com.example.guuber;

public class Vehicle {
	private String make;
	private String model;
	private String color;
	private String reg;

	/**
	 * On create
	 * @param make - The maker of the vehicle
	 * @param model - the model of the vehicle
	 * @param color - the color of the  vehicle
	 * @param reg - who the car is registered to
	 */
	public Vehicle(String make, String model, String color, String reg) {
		this.make = make;
		this.model = model;
		this.color = color;
		this.reg = reg;
	}

	/**
	 * Get the person the car is registered to
	 * @return - register driver
	 */
	public String getReg() { return reg; }

	/**
	 * set the person the car is registered to
	 * @param reg - set who is register to the vehicle
	 */
	public void setReg(String reg) { this.reg = reg; }

	/**
	 * Get the make of the vehicle
	 * @return - make
	 */
	public String getMake() {
		return make;
	}

	/**
	 * set the make of the vehicle
	 * @param make - the make of the vehicle
	 */
	public void setMake(String make) {
		this.make = make;
	}

	/**
	 * Get the model of the vehicle
	 * @return - model
	 */
	public String getModel() {
		return model;
	}

	/**
	 * set the model of the vehicle
	 * @param model - the model of the vehicle
	 */
	public void setModel(String model) {
		this.model = model;
	}

	/**
	 * Get the color of the vehicle
	 * @return - color
	 */
	public String getColor() {
		return color;
	}

	/**
	 * set the color of the vehicle
	 * @param color - the color of the vehicle
	 */
	public void setColor(String color) {
		this.color = color;
	}
}
