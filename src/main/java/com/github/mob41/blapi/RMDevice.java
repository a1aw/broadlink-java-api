/*******************************************************************************
 * MIT License
 *
 * Copyright (c) 2017 bwssystems, Christian Fischer (computerlyrik)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************/

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
import com.github.mob41.blapi.pkt.cmd.rm2.EnterLearnCmdPayload;

public class RMDevice extends BLDevice {
	private static final Logger log = LoggerFactory.getLogger(RMDevice.class);

	public static final String DESC_RM_2 = "RM 2";
	
	public static final String DESC_RM_MINI = "RM Mini";
	
	public static final String DESC_RM_PRO_PHICOMM = "RM Pro";
	
	public static final String DESC_RM_2_HOME_PLUS = "RM 2 Home Plus";
	
	public static final String DESC_RM_2_2HOME_PLUS_GDT = "RM 2 Home Plus GDT";
	
	public static final String DESC_RM_2_PRO_PLUS = "RM 2 Pro Plus";
	
	public static final String DESC_RM_2_PRO_PLUS_2 = "RM 2 Pro Plus 2";
	
	public static final String DESC_RM_2_PRO_PLUS_2_BL = "RM 2 Pro Plus 2 BL";
	
	public static final String DESC_RM_MINI_SHATE = "RM Mini SHATE";

	public RMDevice(short deviceType, String host, Mac mac) throws IOException {
		super(deviceType, host, mac);
		switch(deviceType) {
			case BLDevice.DEV_RM_2:
				this.setDeviceDescription(DESC_RM_2);
				break;
			case BLDevice.DEV_RM_MINI:
				this.setDeviceDescription(DESC_RM_MINI);
				break;
			case BLDevice.DEV_RM_PRO_PHICOMM:
				this.setDeviceDescription(DESC_RM_PRO_PHICOMM);
				break;
			case BLDevice.DEV_RM_2_HOME_PLUS:
				this.setDeviceDescription(DESC_RM_2_HOME_PLUS);
				break;
			case BLDevice.DEV_RM_2_2HOME_PLUS_GDT:
				this.setDeviceDescription(DESC_RM_2_2HOME_PLUS_GDT);
				break;
			case BLDevice.DEV_RM_2_PRO_PLUS:
				this.setDeviceDescription(DESC_RM_2_PRO_PLUS);
				break;
			case BLDevice.DEV_RM_2_PRO_PLUS_2:
				this.setDeviceDescription(DESC_RM_2_PRO_PLUS_2);
				break;
			case BLDevice.DEV_RM_2_PRO_PLUS_2_BL:
				this.setDeviceDescription(DESC_RM_2_PRO_PLUS_2_BL);
				break;
			case BLDevice.DEV_RM_MINI_SHATE:
				this.setDeviceDescription(DESC_RM_MINI_SHATE);
				break;
			default:
				this.setDeviceDescription("RM Device Unknown");
				break;
		}
	}
	
	public boolean enterLearning() throws IOException {
		EnterLearnCmdPayload cmdPayload = new EnterLearnCmdPayload();
		DatagramPacket packet = sendCmdPkt(10000, cmdPayload);
		
		log.debug("Packet received bytes: " + DatatypeConverter.printHexBinary(packet.getData()));
		return true;
	}
	
	public double getTemp() throws Exception {
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
			return (double) (pl[0x4] * 10 + pl[0x5]) / 10.0;
		} else {
			log.warn("Received an error: " + Integer.toHexString(err) + " / " + err);
		}
		
		return -1;
	}

}
