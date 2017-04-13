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

public class SP2Device extends BLDevice {
	private static final Logger log = LoggerFactory.getLogger(SP2Device.class);

	public static final String DESC_SP2 = "Smart Plug V2";

	public static final String DESC_SP2_HONEYWELL_ALT1 = "Smart Plug Honeywell Alt 1";
	
	public static final String DESC_SP2_HONEYWELL_ALT2 = "Smart Plug Honeywell Alt 2";
	
	public static final String DESC_SP2_HONEYWELL_ALT3 = "Smart Plug Honeywell Alt 3";
	
	public static final String DESC_SP2_HONEYWELL_ALT4 = "Smart Plug Honeywell Alt 4";
	
	public static final String DESC_SPMINI = "Smart Plug Mini";
	
	public static final String DESC_SP3 = "Smart Plug V3";
	
	public static final String DESC_SPMINI2 = "Smart Plug Mini V2";
	
	public static final String DESC_SPMINI_OEM_ALT1 = "Smart Plug OEM Alt 1";
	
	public static final String DESC_SPMINI_OEM_ALT2 = "Smart Plug OEM Alt 2";
	
	public static final String DESC_SPMINI_PLUS = "Smart Plug Mini Plus";

	public SP2Device(short deviceType, String host, Mac mac) throws IOException {
		super(deviceType, host, mac);
		switch(deviceType) {
			case BLDevice.DEV_SP2:
				this.setDeviceDescription(DESC_SP2);
				break;
			case BLDevice.DEV_SP2_HONEYWELL_ALT1:
				this.setDeviceDescription(DESC_SP2_HONEYWELL_ALT1);
				break;
			case BLDevice.DEV_SP2_HONEYWELL_ALT2:
				this.setDeviceDescription(DESC_SP2_HONEYWELL_ALT2);
				break;
			case BLDevice.DEV_SP2_HONEYWELL_ALT3:
				this.setDeviceDescription(DESC_SP2_HONEYWELL_ALT3);
				break;
			case BLDevice.DEV_SP2_HONEYWELL_ALT4:
				this.setDeviceDescription(DESC_SP2_HONEYWELL_ALT4);
				break;
			case BLDevice.DEV_SP3:
				this.setDeviceDescription(DESC_SP3);
				break;
			case BLDevice.DEV_SPMINI:
				this.setDeviceDescription(DESC_SPMINI);
				break;
			case BLDevice.DEV_SPMINI2:
				this.setDeviceDescription(DESC_SPMINI2);
				break;
			case BLDevice.DEV_SPMINI_OEM_ALT1:
				this.setDeviceDescription(DESC_SPMINI_OEM_ALT1);
				break;
			case BLDevice.DEV_SPMINI_OEM_ALT2:
				this.setDeviceDescription(DESC_SPMINI_OEM_ALT2);
				break;
			case BLDevice.DEV_SPMINI_PLUS:
				this.setDeviceDescription(DESC_SPMINI_PLUS);
				break;
			default:
				this.setDeviceDescription("Smart Plug Unknown");
				break;
		}
	}

	public void setPower(final boolean state) throws Exception {
		DatagramPacket packet = sendCmdPkt(new CmdPayload(){

			@Override
			public byte getCommand() {
				return 0x6a;
			}

			@Override
			public Payload getPayload() {
				return new Payload(){

					@Override
					public byte[] getData() {
						byte[] b = new byte[16];
						b[0] = (byte)2;
						b[4] = (byte) (state ? 1 : 0);
						return b;
					}
					
				};
			}
			
		});
		
		log.debug("receveid set power bytes: " + packet.getData());
	}

	public boolean checkPower() throws Exception {
		DatagramPacket packet = sendCmdPkt(new CmdPayload(){

			@Override
			public byte getCommand() {
				return 0x6a;
			}

			@Override
			public Payload getPayload() {
				return new Payload(){

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
		
		log.debug("Packet received bytes: " + DatatypeConverter.printHexBinary(data));

		int err = data[0x22] | (data[0x23] << 8);
		
		if (err == 0){
			AES aes = new AES(getIv(), getKey());
			byte[] pl = aes.decrypt(data);
			return pl[0x4] == 1 ? true : false;
		} else {
			log.warn("Received an error: " + Integer.toHexString(err) + " / " + err);
		}
		
		return false;
	}
}
