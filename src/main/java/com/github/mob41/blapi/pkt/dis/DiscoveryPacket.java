/*******************************************************************************
 * MIT License
 *
 * Copyright (c) 2017 Anthony Law
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
 *******************************************************************************/
package com.github.mob41.blapi.pkt.dis;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mob41.blapi.ex.BLApiRuntimeException;
import com.github.mob41.blapi.pkt.Packet;

/**
 * This class packs a packet to discover Broadlink devices
 * @author Anthony
 *
 */
public class DiscoveryPacket implements Packet {

    public static final int DEFAULT_SOURCE_PORT = 0; //This source port is from the python-broadlink source code

    private static final Logger log = LoggerFactory.getLogger(DiscoveryPacket.class);

    private final byte[] data;

    public DiscoveryPacket(){
        this(null);
    }

    public DiscoveryPacket(InetAddress localIpAddr){
        this(localIpAddr, DEFAULT_SOURCE_PORT, Calendar.getInstance(), TimeZone.getDefault());
    }

    public DiscoveryPacket(InetAddress localIpAddr, int sourcePort){
        this(localIpAddr, sourcePort, Calendar.getInstance(), TimeZone.getDefault());
    }

    public DiscoveryPacket(InetAddress localIpAddr, int sourcePort, Calendar cal, TimeZone tz) {
        log.debug("DiscoveryPacket constructor start");
        log.trace("cal=" + cal.getTimeInMillis() + " tz=" + tz.getID());
        if (localIpAddr == null){
            log.debug("localIpAddr is null. Calling InetAddress.getLocalHost");
            try {
                localIpAddr = InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                log.error("Could not relieve local IP address", e);
                throw new BLApiRuntimeException("Could not relieve local IP address", e);
            }
        }
        log.debug("localIpAddr= " + localIpAddr.getHostName() + "/" + localIpAddr.getHostAddress());

        int rawOffset = tz.getRawOffset();
        int tzOffset = rawOffset / 3600;

        log.trace("Raw offset: " + rawOffset);
        log.trace("Calculated offset: getRawOffset/1000/-3600=" + tzOffset);

        int min = cal.get(Calendar.MINUTE);
        int hr = cal.get(Calendar.HOUR);

        int year = cal.get(Calendar.YEAR);
        int dayOfWk = dayOfWeekConv(cal.get(Calendar.DAY_OF_WEEK)); //Day of week (May return -1 if Calendar return a wrong field value)
        int dayOfMn = cal.get(Calendar.DAY_OF_MONTH); //Day of month
        int month = cal.get(Calendar.MONTH) + 1; //Month

        log.trace("min=" + min + " hr=" + hr);
        log.trace("year=" + year + " dayOfWk=" + dayOfWk);
        log.trace("dayOfMn=" + dayOfMn + " month=" + month);

        byte[] ipAddrBytes = localIpAddr.getAddress();

        data = new byte[0x30]; //48-byte

        //data[0x00-0x07] = 0x00;

        //This is directly "copied" from the python-broadlink source code
        if (tzOffset < 0){
            data[0x08] = (byte) (0xff + tzOffset - 1);
            data[0x09] = (byte) 0xff;
            data[0x0a] = (byte) 0xff;
            data[0x0b] = (byte) 0xff;
            log.debug("tzOffset<0: 0x08=" + Integer.toHexString(0xff + tzOffset - 1) + " 0x09-0x0b=0xff");
        } else {
            data[0x08] = (byte) tzOffset;
            data[0x09] = (byte) 0x00;
            data[0x0a] = (byte) 0x00;
            data[0x0b] = (byte) 0x00;
            log.debug("tzOffset>0: 0x08=" + Integer.toHexString(tzOffset) + " 0x09-0x0b=0x00");
        }

        data[0x0c] = (byte) (year & 0xff);
        data[0x0d] = (byte) (year >> 8); //Shift 8 bits

        data[0x0e] = (byte) min;
        data[0x0f] = (byte) hr;

        //subyear = str(year)[2:] //Somehow this code is dirty to do the same as python code
        data[0x10] = (byte) Integer.parseInt(Integer.toString(year).substring(2, 4)); //Year without century

        data[0x11] = (byte) dayOfWk;
        data[0x12] = (byte) dayOfMn;
        data[0x13] = (byte) month;

        //IP address
        data[0x18] = ipAddrBytes[0];
        data[0x19] = ipAddrBytes[1];
        data[0x1a] = ipAddrBytes[2];
        data[0x1b] = ipAddrBytes[3];

        data[0x1c] = (byte) (sourcePort & 0xff);
        data[0x1d] = (byte) (sourcePort >> 8);

        data[0x26] = 6;

        //Checksum
        short checksum = (short) 0xbeaf;

        for (int i = 0; i < data.length; i++){
            checksum += (int)(data[i] & 0xff);
        }

        log.debug("checksum=" + Integer.toHexString(checksum));

        data[0x20] = (byte) (checksum & 0xff);
        data[0x21] = (byte) (checksum >> 8);

        log.debug("DiscoveryPacket constructor end");
    }

    @Override
    public byte[] getData() {
        return data;
    }

    private static int dayOfWeekConv(int fieldVal){
        switch (fieldVal){
        case Calendar.SUNDAY:
            return 6;
        case Calendar.MONDAY:
            return 0;
        case Calendar.TUESDAY:
            return 1;
        case Calendar.WEDNESDAY:
            return 2;
        case Calendar.THURSDAY:
            return 3;
        case Calendar.FRIDAY:
            return 4;
        case Calendar.SATURDAY:
            return 5;
        }
        return -1;
    }

}
