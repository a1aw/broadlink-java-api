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

import com.github.mob41.blapi.mac.Mac;
import com.github.mob41.blapi.pkt.auth.AES;
import com.github.mob41.blapi.pkt.cmd.rm2.CheckDataCmdPayload;
import com.github.mob41.blapi.pkt.cmd.rm2.EnterLearnCmdPayload;
import com.github.mob41.blapi.pkt.cmd.rm2.RMTempCmdPayload;

/**
 * Broadlink RM2 device client
 * 
 * @author Anthony
 *
 */
public class RM2Device extends BLDevice {
    
    /**
     * Generic way to create a RM2Device
     * @param deviceType Device Type
     * @param deviceDesc Friendly device description
     * @param host The target Broadlink hostname
     * @param mac The target Broadlink MAC address
     * @throws IOException Problems on constructing socket
     */
    protected RM2Device(short deviceType, String deviceDesc, String host, Mac mac) throws IOException{
        super(deviceType, deviceDesc, host, mac);
    }

    /**
     * Creates a RM2Device client instance
     * 
     * @param host
     *            The target Broadlink hostname
     * @param mac
     *            The target Broadlink MAC address
     * @throws IOException
     *             Problems on constructing socket
     */
    public RM2Device(String host, Mac mac) throws IOException {
        super(BLDevice.DEV_RM_2, BLDevice.DESC_RM_2, host, mac);
    }

    /**
     * Requests the RM2 to return the learned data<br>
     * <br>
     * The {@link #auth() auth()} method must be ran before these commands
     * 
     * @return Result whether the command is successfully sent.
     * @throws IOException
     *             Problems on sending packet
     */
    public byte[] checkData() throws Exception {
        CheckDataCmdPayload cmdPayload = new CheckDataCmdPayload();
        DatagramPacket packet = sendCmdPkt(10000, cmdPayload);
        byte[] data = packet.getData();

        int err = data[0x22] | (data[0x23] << 8);

        if (err == 0) {
            AES aes = new AES(getIv(), getKey());
            byte[] encData = subbytes(data, 0x38, data.length);

            encData = chgLen(encData, 1024);
            encData = aes.decrypt(encData);

            return subbytes(encData, 0x04, encData.length);
        }

        return null;
    }

    /**
     * Requests the RM2 to enter learning mode.<br>
     * <br>
     * The {@link #auth() auth()} method must be ran before these commands
     * 
     * @return Result whether the command is successfully sent.
     * @throws IOException
     *             Problems on sending packet
     */
    public boolean enterLearning() throws IOException {
        EnterLearnCmdPayload cmdPayload = new EnterLearnCmdPayload();
        DatagramPacket packet = sendCmdPkt(10000, cmdPayload);

        return true;
    }

    /**
     * Requests the RM2 to return the room temperature<br>
     * <br>
     * The {@link #auth() auth()} method must be ran before these commands
     * 
     * @return The room temperature in a floating number
     * @throws IOException
     *             Problems on sending packet
     */
    public double getTemp() throws Exception {
        DatagramPacket packet = sendCmdPkt(new RMTempCmdPayload());
        byte[] data = packet.getData();

        int err = data[0x22] | (data[0x23] << 8);

        if (err == 0) {
            AES aes = new AES(getIv(), getKey());

            byte[] encData = BLDevice.subbytes(data, 0x38, data.length);

            encData = chgLen(encData, 1024);

            byte[] pl = aes.decrypt(encData);

            return (double) (pl[0x4] * 10 + pl[0x5]) / 10.0;
        } else {
            System.out.println(Integer.toHexString(err) + " / " + err);
        }

        return -1;
    }

}
