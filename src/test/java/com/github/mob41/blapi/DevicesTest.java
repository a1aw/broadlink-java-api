package com.github.mob41.blapi;

import org.junit.Test;

public class DevicesTest {

    @Test
    public void testA1() throws Exception {
        BLDevice[] devs = BLDevice.discoverDevices(0);
        System.out.println(devs.length);

        BLDevice dev = devs[0];

        System.out.println(dev.getMac().getMacString());

        dev.auth();

        A1Device device = (A1Device) dev;
        device.checkSensors();
    }

}
