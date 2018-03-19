package com.github.mob41.blapi.device.hysen.cmd;

public class GenericByteCommand extends BaseHysenCommand {

    private final byte[] cmd;

    public GenericByteCommand(byte[] cmd) {
        this.cmd = cmd;
    }

    @Override
    protected byte[] getCmdBytes() {
        return cmd;
    }

}
