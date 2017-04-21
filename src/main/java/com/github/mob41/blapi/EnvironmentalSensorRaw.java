/*******************************************************************************
 * MIT License
 *
 * Copyright (c) 2016, 2017 Anthony Law
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * Contributors:
 *     - Anthony Law (mob41) - Initial API Implementation
 *     - bwssytems
 *     - Christian Fischer (computerlyrik)
 *******************************************************************************/
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
