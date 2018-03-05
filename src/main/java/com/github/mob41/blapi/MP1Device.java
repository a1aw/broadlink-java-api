/*******************************************************************************
 * MIT License
 *
 * Copyright (c) 2016, 2017 Anthony Law
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * Contributors:
 *      - Anthony Law (mob41) - Initial API Implementation
 *      - bwssytems
 *      - Christian Fischer (computerlyrik)
 *******************************************************************************/

package com.github.mob41.blapi;

import java.io.IOException;
import java.net.DatagramPacket;

import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mob41.blapi.mac.Mac;
import com.github.mob41.blapi.pkt.CmdPayload;
import com.github.mob41.blapi.pkt.Payload;

public class MP1Device extends BLDevice {

    private static final Logger log = LoggerFactory.getLogger(MP1Device.class);
    
    /**
     * Generic way to create a MP1Device
     * @param deviceType Device Type
     * @param deviceDesc Friendly device description
     * @param host The target Broadlink hostname
     * @param mac The target Broadlink MAC address
     * @throws IOException Problems on constructing socket
     */
    protected MP1Device(short deviceType, String deviceDesc, String host, Mac mac) throws IOException{
        super(deviceType, deviceDesc, host, mac);
    }
    
    /**
     * Creates a MP1Device client instance
     * 
     * @param host
     *            The target Broadlink hostname
     * @param mac
     *            The target Broadlink MAC address
     * @throws IOException
     *             Problems on constructing socket
     */
    public MP1Device(String host, Mac mac) throws IOException {
        super(BLDevice.DEV_MP1, BLDevice.DESC_MP1, host, mac);
    }

    /**
     * Set the power state
     * @param sid The SID
     * @param state Power State
     * @throws Exception
     */
    public void setState(int sid, boolean state) throws Exception {
        int sid_mask = 0x01 << (sid - 1);
        setStateMask(sid_mask, state);
    }

    private void setStateMask(final int sid_mask, final boolean state) throws Exception {
        // """Sets the power state of the smart power strip."""
        DatagramPacket packet = sendCmdPkt(new CmdPayload() {

            @Override
            public byte getCommand() {
                return 0x6a;
            }

            @Override
            public Payload getPayload() {
                return new Payload() {

                    @Override
                    public byte[] getData() {
                        byte[] b = new byte[16];
                        b[0x00] = (byte) (0x0d);
                        b[0x02] = (byte) (0xa5);
                        b[0x03] = (byte) (0xa5);
                        b[0x04] = (byte) (0x5a);
                        b[0x05] = (byte) (0x5a);
                        b[0x06] = (byte) (0xb2 + (state ? (sid_mask << 1) : sid_mask));
                        b[0x07] = (byte) (0xc0);
                        b[0x08] = (byte) (0x02);
                        b[0x0a] = (byte) (0x03);
                        b[0x0d] = (byte) (sid_mask);
                        b[0x0e] = (byte) (state ? sid_mask : 0);
                        return b;
                    }

                };
            }

        });

        log.debug("Received set power bytes: " + DatatypeConverter.printHexBinary(packet.getData()));
    }
    
    public boolean getStateByIndex(int index) throws Exception{
        return getStates()[index];
    }

    public boolean[] getStates() throws Exception {
        // """Returns the power state of the smart power strip."""
        byte state = getStatesRaw();
        boolean[] data = new boolean[4];
        data[0] = ((state & 0x01) != 0) ? true : false;
        data[1] = ((state & 0x02) != 0) ? true : false;
        data[2] = ((state & 0x04) != 0) ? true : false;
        data[3] = ((state & 0x08) != 0) ? true : false;
        return data;
    }
    
    private byte getStatesRaw() throws Exception {
        // """Returns the power state of the smart power strip in raw format."""
        DatagramPacket packet = sendCmdPkt(new CmdPayload() {

            @Override
            public byte getCommand() {
                return 0x6a;
            }

            @Override
            public Payload getPayload() {
                return new Payload() {

                    @Override
                    public byte[] getData() {
                        byte[] b = new byte[16];
                        b[0x00] = (byte) (0x0a);
                        b[0x02] = (byte) (0xa5);
                        b[0x03] = (byte) (0xa5);
                        b[0x04] = (byte) (0x5a);
                        b[0x05] = (byte) (0x5a);
                        b[0x06] = (byte) (0xae);
                        b[0x07] = (byte) (0xc0);
                        b[0x08] = (byte) (0x01);
                        return b;
                    }

                };
            }

        });

        byte[] data = packet.getData();

        log.debug("Packet received bytes: " + DatatypeConverter.printHexBinary(data));

        int err = data[0x22] | (data[0x23] << 8);

        if (err == 0) {
            byte[] pl = decryptFromDeviceMessage(data);
            byte state = 0;
            if (pl[0x3c] >= 48 && pl[0x3c] <= 57) {
                String decodeValue1;
                decodeValue1 = String.valueOf(pl[0x46]);
                state = Short.decode(decodeValue1).byteValue();
            } else {
                state = pl[0x46];
            }
            return state;
        } else {
            log.warn("Received an error: " + Integer.toHexString(err) + " / " + err);
        }
        return 0;
    }
}
