package com.github.mob41.blapi;

import java.io.IOException;
import java.net.DatagramPacket;

import com.github.mob41.blapi.mac.Mac;
import com.github.mob41.blapi.pkt.AES;
import com.github.mob41.blapi.pkt.CmdPacket;
import com.github.mob41.blapi.pkt.CmdPayload;
import com.github.mob41.blapi.pkt.EnterLearnCmdPayload;
import com.github.mob41.blapi.pkt.Payload;

public class RM2Device extends BLDevice {

	public RM2Device(String host, Mac mac) throws IOException {
		super(BLDevice.DEV_RM_2, host, mac);
	}
	
	public boolean enterLearning() throws IOException{
		EnterLearnCmdPayload cmdPayload = new EnterLearnCmdPayload();
		DatagramPacket packet = sendCmdPkt(10000, cmdPayload);
		
		printBytes(packet.getData());
		return true;
	}
	
	public double getTemp() throws Exception{
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
