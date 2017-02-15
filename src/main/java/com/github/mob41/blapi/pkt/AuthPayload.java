package com.github.mob41.blapi.pkt;

public class AuthPayload implements Payload {

	private final byte[] data;
	
	public AuthPayload(byte[] devId, byte[] devName) {
		data = new byte[0x50];
		
		data[0x04] = devId[0];
		data[0x05] = devId[1];
		data[0x06] = devId[2];
		data[0x07] = devId[3];
		data[0x08] = devId[4];
		data[0x09] = devId[5];
		data[0x0a] = devId[6];
		data[0x0b] = devId[7];
		data[0x0c] = devId[8];
		data[0x0d] = devId[9];
		
		data[0x0e] = devId[10];
		data[0x0f] = devId[11];
		data[0x10] = devId[12];
		data[0x11] = devId[13];
		data[0x12] = devId[14];
		
		data[0x1e] = 0x01;
		data[0x2d] = 0x01;
		
		final int setlen = 0x80 - 0x30;
		int len = devName.length < setlen ? devName.length : setlen;
		System.out.println("devNameLen=" + devName.length);
		System.out.println("setLen=" + setlen);
		System.out.println("finalLen=" + len);
		
		//0x30-0x7f NULL-term String of device name
		for (int i = 0x30; i < len + 0x30; i++){
			System.out.println(Integer.toHexString(i-0x30));
			System.out.println(devName[i-0x30]);
			data[i] = devName[i - 0x30];
		}
	}

	@Override
	public byte[] getData() {
		return data;
	}

	public static byte[] getDefaultDeviceId(){
		return new byte[]{
				0x31, 0x31, 0x31, 0x31, 0x31,
				0x31, 0x31, 0x31, 0x31, 0x31,
				0x31, 0x31, 0x31, 0x31, 0x31};
	}
}
