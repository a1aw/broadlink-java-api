package com.github.mob41.blapi;

import com.github.mob41.blapi.mac.Mac;

public class RM2Device extends BLDevice {

	public RM2Device(String host, Mac mac) {
		super(BLDevice.DEV_RM_2, host, mac);
	}

}
