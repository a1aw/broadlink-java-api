package com.github.mob41.blapi;

import java.io.IOException;
import java.net.DatagramPacket;

import com.github.mob41.blapi.mac.Mac;
import com.github.mob41.blapi.pkt.auth.AES;
import com.github.mob41.blapi.pkt.cmd.rm2.EnterLearnCmdPayload;
import com.github.mob41.blapi.pkt.cmd.rm2.RMTempCmdPayload;

public class RM2Device extends BLDevice {

	/**
	 * Creates a RM2Device client instance
	 * @param host The target Broadlink hostname
	 * @param mac The target Broadlink MAC address
	 * @throws IOException Problems on constructing socket
	 */
	public RM2Device(String host, Mac mac) throws IOException {
		super(BLDevice.DEV_RM_2, host, mac);
	}
	
	/**
	 * Requests the RM2 to enter learning mode.<br>
	 * <br>
	 * The {@link #auth() auth()} method must be ran before these commands
	 * @return Result whether the command is successfully sent.
	 * @throws IOException Problems on sending packet
	 */
	public boolean enterLearning() throws IOException{
		EnterLearnCmdPayload cmdPayload = new EnterLearnCmdPayload();
		DatagramPacket packet = sendCmdPkt(10000, cmdPayload);
		
		printBytes(packet.getData());
		return true;
	}
	
	/**
	 * Requests the RM2 to return the room temperature<br>
	 * <br>
	 * The {@link #auth() auth()} method must be ran before these commands
	 * @return The room temperature in a floating number
	 * @throws IOException Problems on sending packet
	 */
	public double getTemp() throws Exception{
		DatagramPacket packet = sendCmdPkt(new RMTempCmdPayload());
		byte[] data = packet.getData();
		
		printBytes(packet.getData());
		
		int err = data[0x22] | (data[0x23] << 8);
		
		if (err == 0){
			AES aes = new AES(getIv(), getKey());
			byte[] pl = aes.decrypt(data);
			return (double) (pl[0x4] * 10 + pl[0x5]) / 10.0;
		} else {
			System.out.println(Integer.toHexString(err) + " / " + err);
		}
		
		return -1;
	}

}
