package com.github.mob41.blapi.pkt.cmd.rm2;

import com.github.mob41.blapi.pkt.CmdPayload;
import com.github.mob41.blapi.pkt.Payload;

public final class RMTempCmdPayload implements CmdPayload {

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

}
