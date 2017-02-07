package com.github.mob41.blapi;

import java.io.IOException;

import com.github.mob41.blapi.mac.Mac;

public class RM2Device extends BLDevice {

	public RM2Device(String host, Mac mac) throws IOException {
		super(BLDevice.DEV_RM_2, host, mac);
	}

}
