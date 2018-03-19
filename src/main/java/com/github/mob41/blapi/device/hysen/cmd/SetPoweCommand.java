package com.github.mob41.blapi.device.hysen.cmd;

public class SetPoweCommand extends GenericByteCommand {

    public SetPoweCommand(byte power, byte remote_lock) {
        super(new byte[] { 0x01, 0x06, 0x00, 0x00, remote_lock, power });
    }
}
