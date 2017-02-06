/*******************************************************************************
 * MIT License
 *
 * Copyright (c) 2017 Anthony Law
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
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mob41.blapi.mac.Mac;
import com.github.mob41.blapi.pkt.DiscoveryPacket;

/**
 * This is the base class of all Broadlink devices (e.g. SP1, RMPro)
 * @author Anthony
 *
 */
public abstract class BLDevice {
	
	private static final Logger log = LoggerFactory.getLogger(BLDevice.class);
	
	public static final byte[] INITIAL_KEY = {
			0x09, 0x76, 0x28, 0x34,
			0x3f, (byte) 0xe9, (byte) 0x9e, 0x23,
			0x76, 0x5c, 0x15, 0x13,
			(byte) 0xac, (byte) 0xcf, (byte) 0x8b, 0x02
	}; //16-byte
	
	public static final byte[] INITIAL_IV = {
			0x56, 0x2e, 0x17, (byte) 0x99,
			0x6d, 0x09, 0x3d, 0x28,
			(byte) 0xdd, (byte) 0xb3, (byte) 0xba, 0x69,
			0x5a, 0x2e, 0x6f, 0x58
	}; //16-short
	
	public static final short DEV_SP1 = 0x2711;
	
	public static final short DEV_SP2 = 0x2711;
	
	public static final short DEV_SP2_HONEYWELL_ALT1 = 0x2719;
	
	public static final short DEV_SP2_HONEYWELL_ALT2 = 0x7919;
	
	public static final short DEV_SP2_HONEYWELL_ALT3 = 0x271a;
	
	public static final short DEV_SP2_HONEYWELL_ALT4 = 0x791a;
	
	public static final short DEV_SPMINI = 0x2720;
	
	public static final short DEV_SP3 = 0x753e;
	
	public static final short DEV_SPMINI2 = 0x2728;
	
	public static final short DEV_SPMINI_OEM_ALT1 = 0x2733;
	
	public static final short DEV_SPMINI_OEM_ALT2 = 0x273e;
	
	public static final short DEV_SPMINI_PLUS = 0x2736;
	
	public static final short DEV_RM_2 = 0x2712;
	
	public static final short DEV_RM_MINI = 0x2737;
	
	public static final short DEV_RM_PRO_PHICOMM = 0x273d;
	
	public static final short DEV_RM_2_HOME_PLUS = 0x2783;
	
	public static final short DEV_RM_2_2HOME_PLUS_GDT = 0x277c;
	
	public static final short DEV_RM_2_PRO_PLUS = 0x272a;
	
	public static final short DEV_RM_2_PRO_PLUS_2 = 0x2787;
	
	public static final short DEV_RM_2_PRO_PLUS_2_BL = 0x278b;
	
	public static final short DEV_RM_MINI_SHATE = 0x278f;
	
	public static final short DEV_A1 = 0x2714;
	
	public static final short DEV_MP1 = 0x4EB5;
	
	public static final int DISCOVERY_DEST_PORT = 80;
	
	public static final int DISCOVERY_RECEIVE_BUFFER_SIZE = 0x40; //64-bytes

	protected BLDevice(short deviceType, String host, Mac mac) {
		
	}
	
	public static BLDevice createInstance(short deviceType, String host, Mac mac){
		switch (deviceType){
		case DEV_RM_2:
		}
		return null;
	}
	
	public static BLDevice[] discoverDevices(InetAddress sourceIpAddr, int sourcePort, int timeout) throws IOException{
		  List<BLDevice> devices = new ArrayList<BLDevice>(50);
		  
		  DiscoveryPacket dpkt = new DiscoveryPacket(sourceIpAddr, sourcePort);
		  
		  DatagramSocket sock = new DatagramSocket(sourcePort, sourceIpAddr);
		  sock.setBroadcast(true);
		  sock.setReuseAddress(true);
		  
		  byte[] sendBytes = dpkt.getData();
		  DatagramPacket sendpack = new DatagramPacket(sendBytes, sendBytes.length,
				  InetAddress.getByName("255.255.255.255"), DISCOVERY_DEST_PORT);
		  sock.send(sendpack);
		  
		  byte[] receBytes = new byte[DISCOVERY_RECEIVE_BUFFER_SIZE];
		  
		  DatagramPacket recePacket = new DatagramPacket(receBytes, 0, receBytes.length);
		  if (timeout == 0){
			 sock.receive(recePacket);
			 sock.close();
			 
			 String host = recePacket.getAddress().getHostAddress();
			 Mac mac = new Mac(subbytes(receBytes, 0x3a, 0x40));
			 short deviceType = (short) (receBytes[0x34] | receBytes[0x35] << 8);
			 
			 BLDevice inst = createInstance(deviceType, host, mac);
			 
			 if (inst != null){
				 devices.add(inst);
			 }
		  } else {
			  long startTime = System.currentTimeMillis();
			  long elapsed;
			  while ((elapsed = System.currentTimeMillis() - startTime) < timeout){
				  sock.setSoTimeout((int) (timeout - elapsed));
				  
				  try {
					  sock.receive(recePacket);
				  } catch (SocketTimeoutException e){
					  break;
				  }
				  
				  String host = recePacket.getAddress().getHostAddress();
				  Mac mac = new Mac(subbytes(receBytes, 0x3a, 0x40));
				  short deviceType = (short) (receBytes[0x34] | receBytes[0x35] << 8);
				  
				  BLDevice inst = createInstance(deviceType, host, mac);
				  
				  if (inst != null){
					  devices.add(inst);
				  }
			  }
		  }
		  
		  BLDevice[] out = new BLDevice[devices.size()];
		  for (int i = 0; i < out.length; i++){
			  out[i] = devices.get(i);
		  }
		  
		  return out;
	}
	
	/**
	 * Picks bytes from start-set to the end-set in a bytes array
	 * @param data The bytes array to be used
	 * @param start The starting position to be picked
	 * @param end The ending position to be picked
	 * @return The bytes array picked with length (<code>end - start</code>)
	 */
	public static byte[] subbytes(byte[] data, int start, int end){
		byte[] out = new byte[end - start];
		
		int outi = 0;
		for (int i = start; i < end; i += 0x01, outi++){
			out[outi] = data[i];
		}
		
		return out;
	}
	
	/* Not this! This will only send once and close the connection
	/**
	 * Sends a compiled packet to a destination host and port, and
	 * receives a datagram from the source port specified.
	 * @param pkt The compiled packet to be sent
	 * @param sourceIpAddr Source IP address to be binded for receiving datagrams
	 * @param sourcePort Source Port to be bineded for receiving datagrams
	 * @param destIpAddr Destination IP address
	 * @param destPort Destination Port
	 * @param timeout Socket timeout. 0 will disable the timeout
	 * @param bufSize Receiving datagram's buffer size
	 * @return The received datagram
	 * @throws IOException Thrown if socket timed out, cannot bind source IP and source port, no permission, etc.
	 *\/
	public static DatagramPacket sendPkt(Packet pkt, 
			InetAddress sourceIpAddr, int sourcePort,
			InetAddress destIpAddr, int destPort,
			int timeout, int bufSize) throws IOException{
		DatagramSocket sock = new DatagramSocket(sourcePort, sourceIpAddr);
		sock.setBroadcast(true);
		sock.setReuseAddress(true);
		
		//sock.bind(new InetSocketAddress(ipAddr, sourcePort));
		
		byte[] data = pkt.getData();
		DatagramPacket sendpack = new DatagramPacket(data, data.length, destIpAddr, destPort);
		sock.send(sendpack);
		
		byte[] rece = new byte[bufSize];
		
		DatagramPacket recepack = new DatagramPacket(rece, 0, rece.length);
		sock.setSoTimeout(timeout);
		sock.receive(recepack);
		
		sock.close();
		
		return recepack;
	}
	*/
}
