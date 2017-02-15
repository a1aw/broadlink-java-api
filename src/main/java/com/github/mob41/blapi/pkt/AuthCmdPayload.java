package com.github.mob41.blapi.pkt;

public class AuthCmdPayload implements CmdPayload {
	
	private final AuthPayload authPayload;
	
	public AuthCmdPayload(byte[] devId, byte[] devName) {
		authPayload = new AuthPayload(devId, devName);
	}

	@Override
	public byte getCommand() {
		return 0x65;
	}

	@Override
	public Payload getPayload() {
		return authPayload;
	}

}
