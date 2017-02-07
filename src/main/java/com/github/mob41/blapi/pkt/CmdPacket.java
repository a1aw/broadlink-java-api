package com.github.mob41.blapi.pkt;

import com.github.mob41.blapi.ex.BLApiRuntimeException;
import com.github.mob41.blapi.mac.Mac;

/**
 * This constructs a byte array with the format of a command to the Broadlink device
 * @author Anthony
 *
 */
public class CmdPacket implements Packet {

	private static final int DEFAULT_BYTES_SIZE = 0x38; //56-bytes
	
	private final byte[] data;
	
	/**
	 * Constructs a command packet
	 * @param targetMac Target Broadlink device MAC address
	 * @param count Count of packets sent (provided by BLDevice sendPkt())
	 * @param id This BLDevice ID provided by the Broadlink device. It is {0,0,0,0} if auth() not ran
	 * @param iv Encryption IV. It is from the INITIAL_IV or the Broadlink device
	 * @param key Encrytion KEY. It is from the INITIAL_KEY or the Broadlink device
	 * @param cmd The command
	 * @param payload The data to be sent
	 */
	public CmdPacket(Mac targetMac, int count,
			byte[] id, byte[] iv, byte[] key,
			byte cmd, byte[] payload) {
		count = (count + 1) & 0xffff; //increased by the sendPkt()
		
		data = new byte[DEFAULT_BYTES_SIZE + payload.length];
		
		data[0x00] = 0x5a;
		data[0x01] = (byte) 0xa5;
		data[0x02] = (byte) 0xaa;
		data[0x03] = 0x55;
		data[0x04] = 0x5a;
		data[0x05] = (byte) 0xa5;
		data[0x06] = (byte) 0xaa;
		data[0x07] = 0x55;

		data[0x24] = 0x2a;
		data[0x25] = 0x27;
		data[0x26] = cmd;
		
		data[0x28] = (byte) (count & 0xff);
		data[0x29] = (byte) (count >> 8);
		
		byte[] mac = targetMac.getMac();
		
		data[0x2a] = mac[0];
		data[0x2b] = mac[1];
		data[0x2c] = mac[2];
		data[0x2d] = mac[3];
		data[0x2e] = mac[4];
		data[0x2f] = mac[5];
		
		data[0x30] = id[0];
		data[0x31] = id[1];
		data[0x32] = id[2];
		data[0x33] = id[3];
		
		short checksum = (short) 0xbeaf;
		for (int i = 0; i < payload.length; i++){
			checksum += payload[i];
			checksum &= 0xffff;
		}
		
		AES aes = new AES(key, iv);
		
		try {
			payload = aes.encrypt(payload);
		} catch (Exception e) {
			throw new BLApiRuntimeException("Cannot encrypt payload", e);
		}
		
		data[0x34] = (byte) (checksum & 0xff);
		data[0x35] = (byte) (checksum >> 8);
		
		for (int b = DEFAULT_BYTES_SIZE, i = 0; b < data.length; b += 0x01, i++){
			data[b] = payload[i];
		}
		
		checksum = (short) 0xbeaf;
		for (int i = 0; i < data.length; i++){
			checksum += data[i];
			checksum &= 0xffff;
		}
		data[0x20] = (byte) (checksum & 0xff);
		data[0x21] = (byte) (checksum >> 8);
	}

	@Override
	public byte[] getData() {
		return data;
	}

}
