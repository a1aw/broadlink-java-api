package com.github.mob41.blapi.pkt;

public class BytePayload implements Payload {
	
	private final byte[] data;

	public BytePayload(byte[] data) {
		this.data = data;
	}

	@Override
	public byte[] getData() {
		return data;
	}

}
