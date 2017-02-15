package com.github.mob41.blapi.pkt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mob41.blapi.ex.BLApiRuntimeException;
import com.github.mob41.blapi.mac.Mac;

/**
 * This constructs a byte array with the format of a command to the Broadlink device
 * @author Anthony
 *
 */
public class CmdPacket implements Packet {
	
	private static final Logger log = LoggerFactory.getLogger(CmdPacket.class);

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
			CmdPayload cmdPayload) {
		boolean debug = log.isDebugEnabled();
		
		byte cmd = cmdPayload.getCommand();
		byte[] payload = cmdPayload.getPayload().getData();
		
		if (debug)
			log.debug("Constructor CmdPacket starts");
			log.debug("count=" + count + " cmdPayload.cmd=" + Integer.toHexString(cmd) + " payload.len=" + payload.length);
		
		count = (count + 1) & 0xffff; //increased by the sendPkt()
		
		if (debug)
			log.debug("New count: " + count + " (added by 1)");
			log.debug("Creating byte array with data");
		
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
		
		if (debug)
			log.debug("Running checksum for headers");
		
		short checksum = (short) 0xbeaf;
		for (int i = 0; i < payload.length; i++){
			checksum += payload[i];
			checksum &= 0xffff;
		}
		
		data[0x34] = (byte) (checksum & 0xff);
		data[0x35] = (byte) (checksum >> 8);
		
		if (debug)
			log.debug("Headers checksum: " + Integer.toHexString(checksum));
			log.debug("Creating AES instance with provided key, iv");
		
		AES aes = new AES(key, iv);
		
		try {
			if (debug)
				log.debug("Encrypting payload");
			
			payload = aes.encrypt(payload);
			
			if (debug)
				log.debug("Encrypted. len=" + payload.length);
		} catch (Exception e) {
			log.error("Cannot encrypt payload! Aborting", e);
			throw new BLApiRuntimeException("Cannot encrypt payload", e);
		}
		
		for (int b = DEFAULT_BYTES_SIZE, i = 0; b < data.length; b += 0x01, i++){
			data[b] = payload[i];
		}
		
		if (debug)
			log.debug("Running whole packet checksum");
		
		checksum = (short) 0xbeaf;
		for (int i = 0; i < data.length; i++){
			checksum += data[i];
			checksum &= 0xffff;
		}
		
		if (debug)
			log.debug("Whole packet checksum: " + Integer.toHexString(checksum));
		
		data[0x20] = (byte) (checksum & 0xff);
		data[0x21] = (byte) (checksum >> 8);
		
		if (debug)
			log.debug("End of CmdPacket constructor");
	}

	@Override
	public byte[] getData() {
		return data;
	}

}
