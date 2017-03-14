package com.github.mob41.blapi;

import java.io.IOException;
import java.net.DatagramPacket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mob41.blapi.mac.Mac;
import com.github.mob41.blapi.pkt.CmdPayload;
import com.github.mob41.blapi.pkt.Payload;

public class SP1Device extends BLDevice {
	private static final Logger log = LoggerFactory.getLogger(SP1Device.class);
	public static final String DESC_SP1 = "Smart Plug V1";

	public SP1Device(String host, Mac mac) throws IOException {
		super(BLDevice.DEV_SP1, host, mac);
		this.setDeviceDescription(DESC_SP1);
	}

	public void setPower(final boolean state) throws Exception {
		DatagramPacket packet = sendCmdPkt(new CmdPayload(){

			@Override
			public byte getCommand() {
				return 0x66;
			}

			@Override
			public Payload getPayload() {
				return new Payload(){

					@Override
					public byte[] getData() {
						byte[] b = new byte[4];
						b[0] = (byte) (state ? 1 : 0);
						return b;
					}
					
				};
			}
			
		});
		
		log.debug("receveid set power bytes: " + packet.getData());
	}
}
