package com.github.mob41.blapi;

public class EnvironmentalSensor {
    
    private float temperature;
    
    private float humidity;
    
    private String light;
    
    private String airquality;
    
    private String noise;
    
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
    
    public String getLight() {
        return light;
    }
    
    public void setLight(String light) {
        this.light = light;
    }
    
    public String getAirquality() {
        return airquality;
    }
    
    public void setAirquality(String airquality) {
        this.airquality = airquality;
    }
    
    public String getNoise() {
        return noise;
    }
    
    public void setNoise(String noise) {
        this.noise = noise;
    }

}
