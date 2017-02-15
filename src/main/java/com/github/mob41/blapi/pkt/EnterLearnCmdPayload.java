package com.github.mob41.blapi.pkt;

public class EnterLearnCmdPayload implements CmdPayload {
	
	private final Payload payload;
	
	private final byte[] payloadBytes;
	
	public EnterLearnCmdPayload(){
		payloadBytes = new byte[16];
		payloadBytes[0] = 3;
		
		payload = new Payload(){

			@Override
			public byte[] getData() {
				return payloadBytes;
			}
			
		};
	}

	@Override
	public byte getCommand() {
		return 0x6a;
	}

	@Override
	public Payload getPayload() {
		return payload;
	}

}
