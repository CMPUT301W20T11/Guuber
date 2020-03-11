package com.example.guuber;

public class Vehicle {
	private String make;
	private String model;
	private String color;
	private String reg;

	public Vehicle(String make, String model, String color, String reg) {
		this.make = make;
		this.model = model;
		this.color = color;
		this.reg = reg;
	}

	public String getReg() { return reg; }

	public void setReg(String reg) { this.reg = reg; }

	public String getMake() {
		return make;
	}

	public void setMake(String make) {
		this.make = make;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
}
