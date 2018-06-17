package com.github.mob41.blapi.pkt.cmd.hysen;

public class SetTempCommand extends GenericByteCommand {

    public SetTempCommand(double temp) {
        super(new byte[] { 0x01, 0x06, 0x00, 0x01, 0x00, getTempByte(temp) });
    }
}
