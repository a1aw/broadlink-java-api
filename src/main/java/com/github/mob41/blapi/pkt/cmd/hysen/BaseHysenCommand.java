package com.github.mob41.blapi.pkt.cmd.hysen;

import java.net.DatagramPacket;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mob41.blapi.dev.hysen.BaseHysenDevice;
import com.github.mob41.blapi.pkt.CmdPayload;
import com.github.mob41.blapi.pkt.Crc16;
import com.github.mob41.blapi.pkt.Payload;

import static com.github.mob41.blapi.ByteToHexString.*;

/**
 * Base hysen command Payload. Handles crc calculation
 *
 *
 * Adapted from https://github.com/mjg59/python-broadlink
 *
 * @author alpapad
 *
 */
public abstract class BaseHysenCommand implements CmdPayload {

    protected static final Logger log = LoggerFactory.getLogger(BaseHysenCommand.class);

    public byte[] execute(BaseHysenDevice device) throws Exception {

        DatagramPacket packet = device.sendCmdPkt(this);

        byte[] data = packet.getData();

        log.debug(this.getClass().getSimpleName() + " received encrypted bytes: " + toHexString(data));

        int err = data[0x22] | (data[0x23] << 8);

        if (err == 0) {
            byte[] pl = device.decryptFromDeviceMessage(data);
            log.debug(this.getClass().getSimpleName() + " received bytes (decrypted): " + toHexString(pl));
            return Arrays.copyOfRange(pl, 2, pl.length);
        } else {
            log.warn(this.getClass().getSimpleName() + " received an error: " + Integer.toHexString(err) + " / " + err);
        }
        return null;
    }

    @Override
    public byte getCommand() {
        return 0x6a;
    }

    @Override
    public Payload getPayload() {
        return new Payload() {
            /**
             * hysen thermostats require a crc16 calculated on the payload before it can be
             * send and a length field.
             *
             * Payload format: 2 bytes len: first byte is len, second is 0. len includes
             * also CRC (2 bytes_ X bytes payload 2 bytes CRC16 in ModBus format
             */
            @Override
            public byte[] getData() {
                byte[] p = getCmdBytes();
                byte[] cmd = new byte[p.length + 4];
                cmd[0] = (byte) (p.length + 2);
                cmd[1] = 0x00;

                System.arraycopy(p, 0, cmd, 2, p.length);

                int crc = Crc16.getCrc16(p, p.length, 0xffff);
                cmd[cmd.length - 2] = (byte) (crc & 0xff);
                cmd[cmd.length - 1] = (byte) ((crc >> 8) & 0xFF);
                return cmd;
            }
        };
    }

    protected abstract byte[] getCmdBytes();

    protected static byte getTempByte(double temp) {
        return (byte) ((int) (temp * 2) & 0xff);
    }
}
