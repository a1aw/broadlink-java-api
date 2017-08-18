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

import java.io.IOException;
import java.net.DatagramPacket;

import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mob41.blapi.mac.Mac;
import com.github.mob41.blapi.pkt.CmdPayload;
import com.github.mob41.blapi.pkt.Payload;
import com.github.mob41.blapi.pkt.auth.AES;

public class A1Device extends BLDevice {

    private static final Logger log = LoggerFactory.getLogger(A1Device.class);

    public static final String DESC_A1 = "Environmental Sensor";

    public A1Device(String host, Mac mac) throws IOException {
        super(BLDevice.DEV_A1, host, mac);
        this.setDeviceDescription(DESC_A1);
    }

    public EnvironmentalSensor checkSensors() throws Exception {
        EnvironmentalSensor sensorData = null;
        EnvironmentalSensorRaw theRawData = checkSensorsRaw();
        if (theRawData != null) {
            sensorData = new EnvironmentalSensor();
            sensorData.setTemperature(theRawData.getTemperature());
            sensorData.setHumidity(theRawData.getHumidity());
            switch (theRawData.getLight()) {
            case 0:
                sensorData.setLight("dark");
                break;
            case 1:
                sensorData.setLight("dim");
                break;
            case 2:
                sensorData.setLight("normal");
                break;
            case 3:
                sensorData.setLight("bright");
                break;
            default:
                sensorData.setLight("unknown");
                break;
            }
            switch (theRawData.getAirquality()) {
            case 0:
                sensorData.setAirquality("excellent");
                break;
            case 1:
                sensorData.setAirquality("good'");
                break;
            case 2:
                sensorData.setAirquality("normal");
                break;
            case 3:
                sensorData.setAirquality("bad");
                break;
            default:
                sensorData.setAirquality("unknown");
                break;
            }
            switch (theRawData.getNoise()) {
            case 0:
                sensorData.setNoise("quiet");
                break;
            case 1:
                sensorData.setNoise("normal");
                break;
            case 2:
                sensorData.setNoise("noisy");
                break;
            default:
                sensorData.setNoise("unknown");
                break;
            }
        }
        return sensorData;
    }

    public EnvironmentalSensorRaw checkSensorsRaw() throws Exception {
        DatagramPacket packet = sendCmdPkt(new CmdPayload() {

            @Override
            public byte getCommand() {
                return 0x6a;
            }

            @Override
            public Payload getPayload() {
                return new Payload() {

                    @Override
                    public byte[] getData() {
                        byte[] b = new byte[16];
                        b[0] = 1;
                        return b;
                    }

                };
            }

        });
        byte[] data = packet.getData();

        log.debug("checkSensors Packet received bytes: {}", DatatypeConverter.printHexBinary(data));

        int err = data[0x22] | (data[0x23] << 8);

        if (err == 0) {
            AES aes = new AES(getIv(), getKey());
            byte[] pl = aes.decrypt(chgLen(subbytes(data, 56, 1024), 1024));
            log.debug("checkSensors Packet received bytes (decrypted): {}", DatatypeConverter.printHexBinary(pl));
            EnvironmentalSensorRaw sensorData = new EnvironmentalSensorRaw();
            if (pl[0x4] >= 48 && pl[0x4] <= 57) {
                String decodeValue1;
                String decodeValue2;
                byte value1;
                byte value2;
                decodeValue1 = String.valueOf(pl[0x4]);
                decodeValue2 = String.valueOf(pl[0x5]);
                value1 = Short.decode(decodeValue1).byteValue();
                value2 = Short.decode(decodeValue2).byteValue();
                sensorData.setTemperature((float) ((value1 * 10 + value2) / 10.0));
                decodeValue1 = String.valueOf(pl[0x6]);
                decodeValue2 = String.valueOf(pl[0x7]);
                value1 = Short.decode(decodeValue1).byteValue();
                value2 = Short.decode(decodeValue2).byteValue();
                sensorData.setHumidity((float) ((value1 * 10 + value2) / 10.0));
                decodeValue1 = String.valueOf(pl[0x8]);
                sensorData.setLight(Short.decode(decodeValue1).byteValue());
                decodeValue1 = String.valueOf(pl[0x0a]);
                sensorData.setAirquality(Short.decode(decodeValue1).byteValue());
                decodeValue1 = String.valueOf(pl[0xc]);
                sensorData.setNoise(Short.decode(decodeValue1).byteValue());
            } else {
                sensorData.setTemperature((float) ((pl[0x4] * 10 + pl[0x5]) / 10.0));
                sensorData.setHumidity((float) ((pl[0x6] * 10 + pl[0x7]) / 10.0));
                sensorData.setLight(pl[0x8]);
                sensorData.setAirquality(pl[0x0a]);
                sensorData.setNoise(pl[0xc]);
            }
            return sensorData;
        } else {
            log.warn("Received an error: " + Integer.toHexString(err) + " / " + err);
        }
        return null;
    }
}
