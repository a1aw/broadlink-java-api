package com.github.mob41.blapi;

public class EnvironmentalSensorRaw {
    
	private float temperature;
	
	private float humidity;
	
	private byte light;
	
	private byte airquality;
	
	private byte noise;
	
	public float getTemperature() {
		return temperature;
	}
	
	public void setTemperature(float temperature) {
		this.temperature = temperature;
	}
	
	public float getHumidity() {
		return humidity;
	}
	
	public void setHumidity(float humidity) {
		this.humidity = humidity;
	}
	
	public byte getLight() {
		return light;
	}
	
	public void setLight(byte light) {
		this.light = light;
	}
	
	public byte getAirquality() {
		return airquality;
	}
	
	public void setAirquality(byte airquality) {
		this.airquality = airquality;
	}
	
	public byte getNoise() {
		return noise;
	}
	
	public void setNoise(byte noise) {
		this.noise = noise;
	}

}
