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

public class MP1Device extends BLDevice {
	private static final Logger log = LoggerFactory.getLogger(MP1Device.class);
	public static final String DESC_MP1 = "Power Strip";

	public MP1Device(String host, Mac mac) throws IOException {
		super(BLDevice.DEV_MP1, host, mac);
		this.setDeviceDescription(DESC_MP1);
	}

	public void setPower(int sid, boolean state) throws Exception {
			int sid_mask = 0x01 << (sid - 1);
			setPowerMask(sid_mask, state);
	}

	public void setPowerMask(final int sid_mask, final boolean state) throws Exception  {
		// """Sets the power state of the smart power strip."""
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
						b[0x00] = (byte) (0x0d);
					    b[0x02] = (byte) (0xa5);
					    b[0x03] = (byte) (0xa5);
					    b[0x04] = (byte) (0x5a);
					    b[0x05] = (byte) (0x5a);
					    b[0x06] = (byte) (0xb2 + (state ? (sid_mask << 1) : sid_mask));
					    b[0x07] = (byte) (0xc0);
					    b[0x08] = (byte) (0x02);
					    b[0x0a] = (byte) (0x03);
					    b[0x0d] = (byte) (sid_mask);
						b[0x0e] = (byte) (state ? sid_mask : 0);
						return b;
					}
					
				};
			}
			
		});
		
		log.debug("receveid set power bytes: " + packet.getData());
	}

	public byte checkPowerRaw() throws Exception {
		// """Returns the power state of the smart power strip in raw format."""
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
						b[0x00] = (byte) (0x0a);
						b[0x02] = (byte) (0xa5);
						b[0x03] = (byte) (0xa5);
						b[0x04] = (byte) (0x5a);
						b[0x05] = (byte) (0x5a);
						b[0x06] = (byte) (0xae);
						b[0x07] = (byte) (0xc0);
						b[0x08] = (byte) (0x01);
						return b;
					}

				};
			}

		});

		byte[] data = packet.getData();

		log.debug("Packet received bytes: " + DatatypeConverter.printHexBinary(data));

		int err = data[0x22] | (data[0x23] << 8);

		if (err == 0) {
			AES aes = new AES(getIv(), getKey());
			byte[] pl = aes.decrypt(data);
			byte state = 0;
			if (pl[0x3c] >= 48 && pl[0x3c] <= 57) {
				String decodeValue1;
				decodeValue1 = String.valueOf(pl[0x46]);
				state = Short.decode(decodeValue1).byteValue();
			} else {
				state = pl[0x46];
			}
			return state;
		} else {
			log.warn("Received an error: " + Integer.toHexString(err) + " / " + err);
		}
		return 0;
	}

	public boolean[] checkPower() throws Exception {
		//"""Returns the power state of the smart power strip."""
		    byte state = checkPowerRaw();
		    boolean[] data = new boolean[4];
		    data[0] = ((state & 0x01) != 0) ? true : false;
		    data[1] = ((state & 0x02) != 0) ? true : false;
		    data[2] = ((state & 0x04) != 0) ? true : false;
		    data[3] = ((state & 0x08) != 0) ? true : false;
		    return data;
	}
}
