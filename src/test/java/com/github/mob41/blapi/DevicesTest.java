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

import java.util.Arrays;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DevicesTest {

    private static final Logger log = LoggerFactory.getLogger(DevicesTest.class);

    @Test
    @Ignore
    public void testDevices() throws Exception {
        BLDevice[] devs = BLDevice.discoverDevices(0);
        log.info("BLDevice returned " + devs.length + " number of devices.");
        for (int i = 0; i < devs.length; i++) {
            BLDevice dev = devs[i];

            log.info("BLDevice mac is " + dev.getMac().getMacString());

            dev.auth();
            switch (dev.getDeviceType()) {
            case BLDevice.DEV_A1:
                EnvData theData = ((A1Device) dev).getSensorsData();
                log.info("A1Device Temp: " + theData.getTemp() + ", Humidity: " + theData.getHumidity()
                        + ", light: " + theData.getLight() + ", air_quality: " + theData.getAirQualityDescription() + ", noise: "
                        + theData.getNoise());
                break;
            case BLDevice.DEV_MP1:
                boolean[] states = ((MP1Device) dev).getStates();
                log.info("MP1 Device all status: " + Arrays.toString(states));
                break;
            case BLDevice.DEV_SP2:
            case BLDevice.DEV_SP2_HONEYWELL_ALT1:
            case BLDevice.DEV_SP2_HONEYWELL_ALT2:
            case BLDevice.DEV_SP2_HONEYWELL_ALT3:
            case BLDevice.DEV_SP2_HONEYWELL_ALT4:
            case BLDevice.DEV_SPMINI:
            case BLDevice.DEV_SP3:
            case BLDevice.DEV_SPMINI2:
            case BLDevice.DEV_SPMINI_OEM_ALT1:
            case BLDevice.DEV_SPMINI_OEM_ALT2:
            case BLDevice.DEV_SPMINI_PLUS:
                boolean state = ((SP2Device) dev).getState();
                log.info("SP2Device returned state: " + state);
                break;
            case BLDevice.DEV_SP1:
                ((SP1Device) dev).setPower(true);
                log.info("SP1Device set power to true");
                break;
            case BLDevice.DEV_RM_2:
            case BLDevice.DEV_RM_MINI:
            case BLDevice.DEV_RM_PRO_PHICOMM:
            case BLDevice.DEV_RM_2_HOME_PLUS:
            case BLDevice.DEV_RM_2_2HOME_PLUS_GDT:
            case BLDevice.DEV_RM_2_PRO_PLUS:
            case BLDevice.DEV_RM_2_PRO_PLUS_2:
            case BLDevice.DEV_RM_2_PRO_PLUS_2_BL:
            case BLDevice.DEV_RM_MINI_SHATE:
                double theTemp = ((RM2Device) dev).getTemp();
                log.info("RMDevice get temperature: " + theTemp);
                break;

            }

        }
    }

}
