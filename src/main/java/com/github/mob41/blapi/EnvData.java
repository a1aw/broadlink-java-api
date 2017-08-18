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
 *      - Anthony Law (mob41) - Initial API Implementation
 *      - bwssytems
 *      - Christian Fischer (computerlyrik)
 *******************************************************************************/
package com.github.mob41.blapi;

public class EnvData {
    
    public static final byte LIGHT_DARK = 0x00;
    
    public static final byte LIGHT_DIM = 0x01;
    
    public static final byte LIGHT_NORMAL = 0x02;
    
    public static final byte LIGHT_BRIGHT = 0x03;
    
    public static final byte AIR_QUALITY_EXCELLENT = 0x00;
    
    public static final byte AIR_QUALITY_GOOD = 0x01;
    
    public static final byte AIR_QUALITY_NORMAL = 0x02;
    
    public static final byte AIR_QUALITY_BAD = 0x03;
    
    public static final byte NOISE_QUIET = 0x00;
    
    public static final byte NOISE_NORMAL = 0x01;
    
    public static final byte NOISE_NOISY = 0x02;

    private final float temp;

    private final float humidity;

    private final byte light;

    private final byte airQuality;

    private final byte noise;
    
    protected EnvData(float temp, float hum, byte light, byte airQuality, byte noise){
        this.temp = temp;
        this.humidity = hum;
        this.light = light;
        this.airQuality = airQuality;
        this.noise = noise;
    }

    public float getTemp() {
        return temp;
    }

    public float getHumidity() {
        return humidity;
    }

    public byte getLight() {
        return light;
    }
    
    public String getLightDescription(){
        switch (light){
        case LIGHT_DARK:
            return "dark";
        case LIGHT_DIM:
            return "dim";
        case LIGHT_NORMAL:
            return "normal";
        case LIGHT_BRIGHT:
            return "bright";
        default:
            return "unknown";
        }
    }

    public byte getAirQuality() {
        return airQuality;
    }
    
    public String getAirQualityDescription(){
        switch (light){
        case AIR_QUALITY_EXCELLENT:
            return "excellent";
        case AIR_QUALITY_GOOD:
            return "good";
        case AIR_QUALITY_NORMAL:
            return "normal";
        case AIR_QUALITY_BAD:
            return "bad";
        default:
            return "unknown";
        }
    }

    public byte getNoise() {
        return noise;
    }
    
    public String getNoiseDescription(){
        switch (light){
        case NOISE_QUIET:
            return "quiet";
        case NOISE_NORMAL:
            return "normal";
        case NOISE_NOISY:
            return "noisy";
        default:
            return "unknown";
        }
    }
}
