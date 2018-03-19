package com.github.mob41.blapi.device.hysen.cmd;

public class SetTimeCommand extends GenericByteCommand {

    public SetTimeCommand(byte hour, byte minute, byte second, byte day) {
        super(new byte[] { 0x01, 0x10, 0x00, 0x08, 0x00, 0x02, 0x04, hour, minute, second, day });
    }
}
