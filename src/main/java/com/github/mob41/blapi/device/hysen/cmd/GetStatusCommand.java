package com.github.mob41.blapi.device.hysen.cmd;

public class GetStatusCommand extends GenericByteCommand {
    private static byte[] cmd = new byte[] { 0x01, 0x03, 0x00, 0x00, 0x00, 0x16 };

    public GetStatusCommand() {
        super(cmd);
    }
}
