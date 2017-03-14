package com.github.mob41.blapi;

import java.io.IOException;
import java.net.DatagramPacket;

import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mob41.blapi.mac.Mac;
import com.github.mob41.blapi.pkt.AES;
import com.github.mob41.blapi.pkt.CmdPayload;
import com.github.mob41.blapi.pkt.Payload;

public class A1Device extends BLDevice {
	private static final Logger log = LoggerFactory.getLogger(A1Device.class);
	public static final String DESC_A1 = "Environmental Sensor";

	public A1Device(String host, Mac mac) throws IOException {
		super(BLDevice.DEV_A1, host, mac);
		this.setDeviceDescription(DESC_A1);
	}

	public Object checkSensors() throws Exception {
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
		
		log.debug("checkSensors Packet received bytes: " + DatatypeConverter.printHexBinary(data));
		
		int err = data[0x22] | (data[0x23] << 8);
		
		if (err == 0) {
			AES aes = new AES(getIv(), getKey());
			byte[] pl = aes.decrypt(data);
			byte light;
			byte air_quality;
			byte noise;
			EnvironmentalSensor sensorData = new EnvironmentalSensor();
			
		    if(payload[0x4]) == int:
		        data['temperature'] = (payload[0x4] * 10 + payload[0x5]) / 10.0
		        data['humidity'] = (payload[0x6] * 10 + payload[0x7]) / 10.0
		        light = payload[0x8]
		        air_quality = payload[0x0a]
		        noise = payload[0xc]
		      else:
		        data['temperature'] = (ord(payload[0x4]) * 10 + ord(payload[0x5])) / 10.0
		        data['humidity'] = (ord(payload[0x6]) * 10 + ord(payload[0x7])) / 10.0
		        light = ord(payload[0x8])
		        air_quality = ord(payload[0x0a])
		        noise = ord(payload[0xc])
		  switch(light) {
		  	case 0:
		        data['light'] = 'dark'
		        break;
		  	case 1:
		        data['light'] = 'dim'
		        break;
		  	case 2:
		        data['light'] = 'normal'
		        break;
		  	case 3:
		        data['light'] = 'bright'
		        breask;
		    default:
		        data['light'] = 'unknown'
		        break;
		  }
		      if air_quality == 0:
		        data['air_quality'] = 'excellent'
		      elif air_quality == 1:
		        data['air_quality'] = 'good'
		      elif air_quality == 2:
		        data['air_quality'] = 'normal'
		      elif air_quality == 3:
		        data['air_quality'] = 'bad'
		      else:
		        data['air_quality'] = 'unknown'
		      if noise == 0:
		        data['noise'] = 'quiet'
		      elif noise == 1:
		        data['noise'] = 'normal'
		      elif noise == 2:
		        data['noise'] = 'noisy'
		      else:
		        data['noise'] = 'unknown'
		      return data
		} else {
			log.warn("Received an error: " + Integer.toHexString(err) + " / " + err);
		}
		return null;
	}
}
