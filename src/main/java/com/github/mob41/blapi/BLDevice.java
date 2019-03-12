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

import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mob41.blapi.mac.Mac;
import com.github.mob41.blapi.pkt.CmdPacket;
import com.github.mob41.blapi.pkt.CmdPayload;
import com.github.mob41.blapi.pkt.Packet;
import com.github.mob41.blapi.pkt.auth.AES;
import com.github.mob41.blapi.pkt.auth.AuthCmdPayload;
import com.github.mob41.blapi.pkt.dis.DiscoveryPacket;

/**
 * This is the base class of all Broadlink devices (e.g. SP1, RMPro)
 * 
 * @author Anthony
 *
 */
public abstract class BLDevice implements Closeable {

    /**
     * The specific logger for this class
     */
    protected static final Logger log = LoggerFactory.getLogger(BLDevice.class);

    /**
     * Initial key for encryption
     */
    public static final byte[] INITIAL_KEY = { 0x09, 0x76, 0x28, 0x34, 0x3f, (byte) 0xe9, (byte) 0x9e, 0x23, 0x76, 0x5c,
            0x15, 0x13, (byte) 0xac, (byte) 0xcf, (byte) 0x8b, 0x02 }; // 16-byte

    /**
     * Initial iv for encryption
     */
    public static final byte[] INITIAL_IV = { 0x56, 0x2e, 0x17, (byte) 0x99, 0x6d, 0x09, 0x3d, 0x28, (byte) 0xdd,
            (byte) 0xb3, (byte) 0xba, 0x69, 0x5a, 0x2e, 0x6f, 0x58 }; // 16-short

    public static final int DEFAULT_BYTES_SIZE = 0x38; // 56-bytes

    // Devices type HEX

    public static final short DEV_SP1 = 0x0;

    public static final short DEV_SP2 = 0x2711;

    public static final short DEV_SP2_HONEYWELL_ALT1 = 0x2719;

    public static final short DEV_SP2_HONEYWELL_ALT2 = 0x7919;

    public static final short DEV_SP2_HONEYWELL_ALT3 = 0x271a;

    public static final short DEV_SP2_HONEYWELL_ALT4 = 0x791a;

    public static final short DEV_SPMINI = 0x2720;

    public static final short DEV_SP3 = 0x753e;

    public static final short DEV_SPMINI2 = 0x2728;

    public static final short DEV_SPMINI_OEM_ALT1 = 0x2733;

    public static final short DEV_SPMINI_OEM_ALT2 = 0x273e;

    public static final short DEV_SPMINI_PLUS = 0x2736;

    public static final short DEV_RM_2 = 0x2712;

    public static final short DEV_RM_MINI = 0x2737;

    public static final short DEV_RM_MINI_3 = 0x27c2;

    public static final short DEV_RM_PRO_PHICOMM = 0x273d;

    public static final short DEV_RM_2_HOME_PLUS = 0x2783;

    public static final short DEV_RM_2_2HOME_PLUS_GDT = 0x277c;

    public static final short DEV_RM_2_PRO_PLUS = 0x272a;

    public static final short DEV_RM_2_PRO_PLUS_2 = 0x2787;

    public static final short DEV_RM_2_PRO_PLUS_2_BL = 0x278b;

    public static final short DEV_RM_MINI_SHATE = 0x278f;

    public static final short DEV_A1 = 0x2714;

    public static final short DEV_MP1 = 0x4EB5;
    
    public static final short DEV_HYSEN = 0x4EAD;

    public static final short DEV_FLOUREON = 0xffffffad;

    //
    // Friendly device description
    //
    // Notice: Developers are not recommended to use device description as device identifiers.
    //         Instead, developers are advised to use Device Type Hex numbers.
    
    //Unknown
    
    public static final String DESC_UNKNOWN = "Unknown Device";
    
    //RM Series

    public static final String DESC_RM_2 = "RM 2";

    public static final String DESC_RM_MINI = "RM Mini";

    public static final String DESC_RM_MINI_3 = "RM Mini 3";

    public static final String DESC_RM_PRO_PHICOMM = "RM Pro";

    public static final String DESC_RM_2_HOME_PLUS = "RM 2 Home Plus";

    public static final String DESC_RM_2_2HOME_PLUS_GDT = "RM 2 Home Plus GDT";

    public static final String DESC_RM_2_PRO_PLUS = "RM 2 Pro Plus";

    public static final String DESC_RM_2_PRO_PLUS_2 = "RM 2 Pro Plus 2";

    public static final String DESC_RM_2_PRO_PLUS_2_BL = "RM 2 Pro Plus 2 BL";

    public static final String DESC_RM_MINI_SHATE = "RM Mini SHATE";
    
    //A Series

    public static final String DESC_A1 = "Environmental Sensor";
    
    //MP Series

    public static final String DESC_MP1 = "Power Strip";
    
    //SP Series

    public static final String DESC_SP1 = "Smart Plug V1";

    public static final String DESC_SP2 = "Smart Plug V2";

    public static final String DESC_SP2_HONEYWELL_ALT1 = "Smart Plug Honeywell Alt 1";

    public static final String DESC_SP2_HONEYWELL_ALT2 = "Smart Plug Honeywell Alt 2";

    public static final String DESC_SP2_HONEYWELL_ALT3 = "Smart Plug Honeywell Alt 3";

    public static final String DESC_SP2_HONEYWELL_ALT4 = "Smart Plug Honeywell Alt 4";

    public static final String DESC_SPMINI = "Smart Plug Mini";

    public static final String DESC_SP3 = "Smart Plug V3";

    public static final String DESC_SPMINI2 = "Smart Plug Mini V2";

    public static final String DESC_SPMINI_OEM_ALT1 = "Smart Plug OEM Alt 1";

    public static final String DESC_SPMINI_OEM_ALT2 = "Smart Plug OEM Alt 2";

    public static final String DESC_SPMINI_PLUS = "Smart Plug Mini Plus";

    public static final String DESC_HYSEN = "Hysen Thermostat";

    public static final String DESC_FLOUREON = "Floureon Thermostat";
    /**
     * The destination port for discovery broadcasting (from __init__.py)
     */
    public static final int DISCOVERY_DEST_PORT = 80;

    /**
     * The discovery receive buffer size (from __init__.py)
     */
    public static final int DISCOVERY_RECEIVE_BUFFER_SIZE = 0x40; // 64-bytes

    /**
     * Default discovery timeout (10 seconds)
     */
    public static final int DEFAULT_TIMEOUT = 10000; // 10 seconds (10000 ms)

    /**
     * Packet count that is sent by this instance of BLDevice. This is for
     * {@link #sendCmdPkt(CmdPayload) sendCmdPkt} method.
     */
    private int pktCount;

    /**
     * Encryption key. Initialization value is {@link #INITIAL_KEY INITIAL_KEY}.
     * This is for {@link #sendCmdPkt(CmdPayload) sendCmdPkt} method.
     */
    private byte[] key;

    /**
     * Encryption iv. Initialization value is {@link #INITIAL_IV INITIAL_IV}.
     * This is for {@link #sendCmdPkt(CmdPayload) sendCmdPkt} method.
     */
    private byte[] iv;

    /**
     * Device/Client ID. Initialization value is <code>{0,0,0,0}</code>. And it
     * is changed after the {@link #auth() auth} method, that Broadlink devices
     * will provide a id for this client/device. This is for
     * {@link #sendCmdPkt(CmdPayload) sendCmdPkt} method.
     */
    private byte[] id;

    /**
     * Device type received from discovering devices, or those
     * <code>BLDevice.DEV_*</code> constants
     */
    private final short deviceType;
    
    /**
     * A friendly description of this device
     */
    private final String deviceDesc;

    /**
     * Specific datagram socket for this instance, to reuse address.
     */
    private DatagramSocket sock;

    /**
     * Target device host
     */
    private String host;

    /**
     * Target device MAC, using {@link com.github.mob41.blapi.mac.Mac}
     * implementation to handle MAC addresses
     */
    private Mac mac;
    
    /**
     * AES decryption object
     */
    private AES aes = null;
    
    /**
     * flag to denote this object alreay authorized.
     */
    private boolean alreadyAuthorized;
    
    /**
     * Constructs a <code>BLDevice</code>, with a device type (constants),
     * hostname and MAC address
     * 
     * @param deviceType
     *            Device type constants (<code>BLDevice.DEV_*</code>)
     * @param deviceDesc
     *            Friendly device description
     * @param host
     *            Hostname of target Broadlink device
     * @param mac
     *            MAC address of target Broadlink device
     * @throws IOException
     *             Problems on constructing a datagram socket
     */
    protected BLDevice(short deviceType, String deviceDesc, String host, Mac mac) throws IOException {
        key = INITIAL_KEY;
        iv = INITIAL_IV;
        id = new byte[] { 0, 0, 0, 0 };

        pktCount = new Random().nextInt(0xffff);

        this.deviceType = deviceType;
        this.deviceDesc = deviceDesc;
        
        this.host = host;
        this.mac = mac;

        sock = new DatagramSocket();
        sock.setReuseAddress(true);
        sock.setBroadcast(true);
        aes = new AES(iv, key);
        alreadyAuthorized = false;
    }

    /**
     * Releases the resources of this <code>BLDevice</code>
     */
    @Override
    public void close() {
        sock.close();
    }

    /**
     * Returns the device type of this Broadlink device
     * 
     * @return The device type in <code>short</code>
     */
    public short getDeviceType() {
        return deviceType;
    }

    /**
     * Returns this Broadlink device's hostname / IP address
     * 
     * @return The hostname / IP address in String
     */
    public String getHost() {
        return host;
    }

    /**
     * Returns this Broadlink device's MAC address
     * 
     * @return The MAC address in BLApi's <code>Mac</code> implementation
     */
    public Mac getMac() {
        return mac;
    }

    public AES getAes() {
		return aes;
	}

	/**
     * Returns a friendly description of this BLDevice
     * @return a String
     */
    public String getDeviceDescription() {
        return deviceDesc;
    }

    /**
     * Compatibility with previous code
     * @return Boolean whether this method is success or not
     * @throws IOException If I/O goes wrong
     */
    public boolean auth() throws IOException {
    	return auth(false);
    }

    /**
     * Authenticates with the broadlink device, before any other control
     * commands
     * @param reauth Setting this to true forces to perform re-auth with the device. Defaults not to perform re-auth.
     * @return Boolean whether this method is success or not
     * @throws IOException
     *             If I/O goes wrong
     */
    public boolean auth(boolean reauth) throws IOException {
        log.debug("auth Authentication method starts");
        if(alreadyAuthorized && !reauth) {
        	log.debug("auth Already Authorized.");
        	return true;
        }

        AuthCmdPayload sendPayload = new AuthCmdPayload();
        log.debug("auth Sending CmdPacket with AuthCmdPayload: cmd=" + Integer.toHexString(sendPayload.getCommand())
                    + " len=" + sendPayload.getPayload().getData().length);

        log.debug("auth AuthPayload initial bytes to send: {}", DatatypeConverter.printHexBinary(sendPayload.getPayload().getData()));

        DatagramPacket recvPack = sendCmdPkt(10000, 2048, sendPayload);

        byte[] data = recvPack.getData();
        
        if(data.length <= 0) {
            log.error("auth Received 0 bytes on initial request.");
            alreadyAuthorized = false;
            return false;
        }

        log.debug("auth recv encrypted data bytes (" + data.length +") after initial req: {}", DatatypeConverter.printHexBinary(data));

        byte[] payload = null;
        try {
            log.debug("auth Decrypting encrypted data");

            payload = decryptFromDeviceMessage(data);

            log.debug("auth Decrypted. len=" + payload.length);

        } catch (Exception e) {
            log.error("auth Received datagram decryption error. Aborting method", e);
            alreadyAuthorized = false;
            return false;
        }

        log.debug("auth Packet received payload bytes: " + DatatypeConverter.printHexBinary(payload));

        key = subbytes(payload, 0x04, 0x14);

        log.debug("auth Packet received key bytes: " + DatatypeConverter.printHexBinary(key));

        if (key.length % 16 != 0) {
            log.error("auth Received key len is not a multiple of 16! Aborting");
            alreadyAuthorized = false;
            return false;
        }

        // recreate AES object with new key
        aes = new AES(iv, key);

        id = subbytes(payload, 0x00, 0x04);

        log.debug("auth Packet received id bytes: " + DatatypeConverter.printHexBinary(id) + " with ID len=" + id.length);

        log.debug("auth End of authentication method");
        alreadyAuthorized = true;

        return true;
    }

    /**
     * Sends a command packet from localhost to Broadlink device, with buffer
     * size 1024 bytes, 10 seconds timeout<br>
     * <br>
     * Before any commands to be sent to the device, {@link #auth() auth} must
     * be ran first in order to authenticate with the device and gain a device
     * ID, encryption key and IV.
     * 
     * @param cmdPayload
     *            Command data to be sent
     * @return {@link DatagramPacket} containing the byte data and sender host
     *         information.
     * @throws IOException
     *             Problems when sending the packet
     */
    public DatagramPacket sendCmdPkt(CmdPayload cmdPayload) throws IOException {
        return sendCmdPkt(10000, cmdPayload);
    }

    /**
     * Sends a command packet from localhost to Broadlink device, with default
     * buffer size 1024 bytes<br>
     * <br>
     * Before any commands to be sent to the device, {@link #auth() auth} must
     * be ran first in order to authenticate with the device and gain a device
     * ID, encryption key and IV.
     * 
     * @param timeout
     *            Socket read timeout
     * @param cmdPayload
     *            Command data to be sent
     * @return {@link DatagramPacket} containing the byte data and sender host
     *         information.
     * @throws IOException
     *             Problems when sending the packet
     */
    public DatagramPacket sendCmdPkt(int timeout, CmdPayload cmdPayload) throws IOException {
        return sendCmdPkt(InetAddress.getLocalHost(), 0, timeout, 1024, cmdPayload);
    }

    /**
     * Sends a command packet from localhost to Broadlink device<br>
     * <br>
     * Before any commands to be sent to the device, {@link #auth() auth} must
     * be ran first in order to authenticate with the device and gain a device
     * ID, encryption key and IV.
     * 
     * @param timeout
     *            Socket read timeout
     * @param bufSize
     *            Receive datagram buffer size
     * @param cmdPayload
     *            Command data to be sent
     * @return {@link DatagramPacket} containing the byte data and sender host
     *         information.
     * @throws IOException
     *             Problems when sending the packet
     */
    public DatagramPacket sendCmdPkt(int timeout, int bufSize, CmdPayload cmdPayload) throws IOException {
        return sendCmdPkt(InetAddress.getLocalHost(), 0, timeout, bufSize, cmdPayload);
    }

    /**
     * Binds to a specific IP address and sends a command packet to Broadlink
     * device<br>
     * <br>
     * Before any commands to be sent to the device, {@link #auth() auth} must
     * be ran first in order to authenticate with the device and gain a device
     * ID, encryption key and IV.
     * 
     * @param sourceIpAddr
     *            Bind the socket to this IP address
     * @param sourcePort
     *            Bind the socket to this port
     * @param timeout
     *            Socket read timeout
     * @param bufSize
     *            Receive datagram buffer size
     * @param cmdPayload
     *            Command data to be sent
     * @return {@link DatagramPacket} containing the byte data and sender host
     *         information.
     * @throws IOException
     *             Problems when sending the packet
     */
    public DatagramPacket sendCmdPkt(InetAddress sourceIpAddr, int sourcePort, int timeout, int bufSize,
            CmdPayload cmdPayload) throws IOException {
        CmdPacket cmdPkt = new CmdPacket(mac, pktCount++, id, aes, cmdPayload);
        log.debug("sendCmdPkt - Send Command Packet bytes: {}", DatatypeConverter.printHexBinary(cmdPkt.getData()));
        return sendPkt(sock, cmdPkt, InetAddress.getByName(host), 80, timeout, bufSize);
    }

    /**
     * Creates a Broadlink device client
     * 
     * @param deviceType
     *            Device type constant (<code>BLDevice.DEV_*</code>)
     * @param host
     *            Target Broadlink device hostname
     * @param mac
     *            Target Broadlink device MAC address
     * @return A BLDevice client
     * @throws IOException
     *             Problems when constucting a datagram socket
     */
    public static BLDevice createInstance(short deviceType, String host, Mac mac) throws IOException {
        String desc = BLDevice.getDescOfType(deviceType);
        switch (deviceType) {
        case DEV_SP1:
            return new SP1Device(host, mac);
        case DEV_SP2:
        case DEV_SP2_HONEYWELL_ALT1:
        case DEV_SP2_HONEYWELL_ALT2:
        case DEV_SP2_HONEYWELL_ALT3:
        case DEV_SP2_HONEYWELL_ALT4:
        case DEV_SPMINI:
        case DEV_SP3:
        case DEV_SPMINI2:
        case DEV_SPMINI_OEM_ALT1:
        case DEV_SPMINI_OEM_ALT2:
        case DEV_SPMINI_PLUS:
            return new SP2Device(deviceType, desc, host, mac);
        case DEV_RM_2:
        case DEV_RM_MINI:
        case DEV_RM_MINI_3:
            return new RM2Device(deviceType, desc, host, mac);
        case DEV_RM_PRO_PHICOMM:
        case DEV_RM_2_HOME_PLUS:
        case DEV_RM_2_2HOME_PLUS_GDT:
        case DEV_RM_2_PRO_PLUS:
        case DEV_RM_2_PRO_PLUS_2:
        case DEV_RM_2_PRO_PLUS_2_BL:
        case DEV_RM_MINI_SHATE:
            return new RM2Device(deviceType, desc, host, mac);
        case DEV_A1:
            return new A1Device(host, mac);
        case DEV_MP1:
            return new MP1Device(host, mac);
        case DEV_FLOUREON:
            return new FloureonDevice(host, mac);
        case DEV_HYSEN:
            return new HysenDevice(host, mac);
        }
        return null;
    }

    /**
     * Discover Broadlink devices in the local network, with
     * {@link #DEFAULT_TIMEOUT default timeout}
     * 
     * @return An array of <code>BLDevice</code> in the network
     * @throws IOException
     *             Problems when discovering
     */
    public static BLDevice[] discoverDevices() throws IOException {
        return discoverDevices(DEFAULT_TIMEOUT);
    }

    /**
     * Discover Broadlink devices in the local network
     * 
     * @param timeout
     *            Socket read timeout
     * @return An array of <code>BLDevice</code> in the network
     * @throws IOException
     *             Problems when discovering
     */
    public static BLDevice[] discoverDevices(int timeout) throws IOException {
        return discoverDevices(InetAddress.getLocalHost(), 0, timeout);
    }

    /**
     * Discover Broadlink devices in the network, binded with a specific IP
     * address
     * 
     * @param sourceIpAddr
     *            The IP address to be binded
     * @param sourcePort
     *            The port to be binded
     * @param timeout
     *            Socket read timeout
     * @return An array of <code>BLDevice</code> in the network
     * @throws IOException
     *             Problems when discovering
     */
    public static BLDevice[] discoverDevices(InetAddress sourceIpAddr, int sourcePort, int timeout) throws IOException {
        boolean debug = log.isDebugEnabled();

        if (debug)
            log.debug("Discovering devices");

        List<BLDevice> devices = new ArrayList<BLDevice>(50);

        if (debug)
            log.debug("Constructing DiscoveryPacket");

        DiscoveryPacket dpkt = new DiscoveryPacket(sourceIpAddr, sourcePort);

        DatagramSocket sock = new DatagramSocket(sourcePort, sourceIpAddr);

        sock.setBroadcast(true);
        sock.setReuseAddress(true);

        byte[] sendBytes = dpkt.getData();
        DatagramPacket sendpack = new DatagramPacket(sendBytes, sendBytes.length,
                InetAddress.getByName("255.255.255.255"), DISCOVERY_DEST_PORT);

        if (debug)
            log.debug("Sending broadcast");

        sock.send(sendpack);

        byte[] receBytes = new byte[DISCOVERY_RECEIVE_BUFFER_SIZE];

        DatagramPacket recePacket = new DatagramPacket(receBytes, 0, receBytes.length);
        if (timeout == 0) {
            if (debug)
                log.debug("No timeout was set. Blocking thread until received");
            log.debug("Waiting for datagrams");

            sock.receive(recePacket);

            if (debug)
                log.debug("Received. Closing socket");

            sock.close();

            String host = recePacket.getAddress().getHostAddress();
            Mac mac = new Mac(subbytes(receBytes, 0x3a, 0x40));
            short deviceType = (short) (receBytes[0x34] | receBytes[0x35] << 8);

            if (debug)
                log.debug("Info: host=" + host + " mac=" + mac.getMacString() + " deviceType=0x"
                        + Integer.toHexString(deviceType));
            log.debug("Creating BLDevice instance");

            BLDevice inst = createInstance(deviceType, host, mac);

            if (inst != null) {
                if (debug)
                    log.debug("Adding to found devices list");

                devices.add(inst);
            } else if (debug) {
                log.debug("Cannot create instance, returned null, not adding to found devices list");
            }
        } else {
            if (debug)
                log.debug("A timeout of " + timeout + " ms was set. Running loop");

            long startTime = System.currentTimeMillis();
            long elapsed;
            while ((elapsed = System.currentTimeMillis() - startTime) < timeout) {
                if (debug)
                    log.debug("Elapsed: " + elapsed + " ms");
                log.debug("Socket timeout: timeout-elapsed=" + (timeout - elapsed));

                sock.setSoTimeout((int) (timeout - elapsed));

                try {
                    if (debug)
                        log.debug("Waiting for datagrams");

                    sock.receive(recePacket);
                } catch (SocketTimeoutException e) {
                    if (debug)
                        log.debug("Socket timed out for " + (timeout - elapsed) + " ms", e);

                    break;
                }

                if (debug)
                    log.debug("Received datagram");

                String host = recePacket.getAddress().getHostAddress();
                Mac mac = new Mac(reverseBytes(subbytes(receBytes, 0x3a, 0x40)));
                short deviceType = (short) (receBytes[0x34] | receBytes[0x35] << 8);

                if (debug)
                    log.debug("Info: host=" + host + " mac=" + mac.getMacString() + " deviceType=0x"
                            + Integer.toHexString(deviceType));
                log.debug("Creating BLDevice instance");

                BLDevice inst = createInstance(deviceType, host, mac);

                if (inst != null) {
                    if (debug)
                        log.debug("Adding to found devices list");

                    devices.add(inst);
                } else if (debug) {
                    log.debug("Cannot create instance, returned null, not adding to found devices list");
                }
            }
        }

        if (debug)
            log.debug("Converting list to array: " + devices.size());

        BLDevice[] out = new BLDevice[devices.size()];

        for (int i = 0; i < out.length; i++) {
            out[i] = devices.get(i);
        }

        if (debug)
            log.debug("End of device discovery: " + out.length);
        
        sock.close();

        return out;
    }
    
    public static String getDescOfType(short devType){
        switch (devType) {
        
        //
        // RM Series
        //
        
        case BLDevice.DEV_RM_2:
            return DESC_RM_2;
        case BLDevice.DEV_RM_MINI:
            return DESC_RM_MINI;
        case BLDevice.DEV_RM_MINI_3:
            return DESC_RM_MINI_3;
        case BLDevice.DEV_RM_PRO_PHICOMM:
            return DESC_RM_PRO_PHICOMM;
        case BLDevice.DEV_RM_2_HOME_PLUS:
            return DESC_RM_2_HOME_PLUS;
        case BLDevice.DEV_RM_2_2HOME_PLUS_GDT:
            return DESC_RM_2_2HOME_PLUS_GDT;
        case BLDevice.DEV_RM_2_PRO_PLUS:
            return DESC_RM_2_PRO_PLUS;
        case BLDevice.DEV_RM_2_PRO_PLUS_2:
            return DESC_RM_2_PRO_PLUS_2;
        case BLDevice.DEV_RM_2_PRO_PLUS_2_BL:
            return DESC_RM_2_PRO_PLUS_2_BL;
        case BLDevice.DEV_RM_MINI_SHATE:
            return DESC_RM_MINI_SHATE;
        
        //
        // SP2 Series
        //

        case BLDevice.DEV_SP2:
            return DESC_SP2;
        case BLDevice.DEV_SP2_HONEYWELL_ALT1:
            return DESC_SP2_HONEYWELL_ALT1;
        case BLDevice.DEV_SP2_HONEYWELL_ALT2:
            return DESC_SP2_HONEYWELL_ALT2;
        case BLDevice.DEV_SP2_HONEYWELL_ALT3:
            return DESC_SP2_HONEYWELL_ALT3;
        case BLDevice.DEV_SP2_HONEYWELL_ALT4:
            return DESC_SP2_HONEYWELL_ALT4;
        case BLDevice.DEV_SP3:
            return DESC_SP3;
        case BLDevice.DEV_SPMINI:
            return DESC_SPMINI;
        case BLDevice.DEV_SPMINI2:
            return DESC_SPMINI2;
        case BLDevice.DEV_SPMINI_OEM_ALT1:
            return DESC_SPMINI_OEM_ALT1;
        case BLDevice.DEV_SPMINI_OEM_ALT2:
            return DESC_SPMINI_OEM_ALT2;
        case BLDevice.DEV_SPMINI_PLUS:
            return DESC_SPMINI_PLUS;

        case BLDevice.DEV_SP1:
        	return BLDevice.DESC_SP1;
        case BLDevice.DEV_MP1:
        	return BLDevice.DESC_MP1;
        case BLDevice.DEV_A1:
        	return BLDevice.DESC_A1;
        case BLDevice.DEV_HYSEN:
            return BLDevice.DESC_HYSEN;
        case BLDevice.DEV_FLOUREON:
            return BLDevice.DESC_FLOUREON;
        //
        // Unregonized
        //
        default:
            return DESC_UNKNOWN;
        }
    }

    /**
     * Misc: Reverse the byte array
     * 
     * @param data
     *            Original data
     * @return Result byte array
     */
    public static byte[] reverseBytes(byte[] data) {
        byte[] out = new byte[data.length];

        for (int i = 0; i < out.length; i++) {
            out[i] = data[data.length - 1 - i];
        }

        return out;
    }

    /**
     * Misc: Pull bytes out from end of array until a non null is detected
     * 
     * @param data
     *            Original data
     * @param offset
     *            Starting offset
     * @return Result byte array
     */
    public static byte[] removeNullsFromEnd(byte[] data, int offset) {
    	int new_length = 0;
        for (int i = data.length - 1; i >= offset; i--) {
            if (data[i] != 0x00) { // null
            	new_length = i + 1;
                break;
            }
        }

        byte[] out = new byte[new_length];

        for (int x = offset; x < new_length; x++) {
            out[x - offset] = data[x];
        }

        return out;
    }

    /**
     * Misc: Pull bytes out from an array until a NULL (0) is detected
     * 
     * @param data
     *            Original data
     * @param offset
     *            Starting offset
     * @return Result byte array
     */
    public static byte[] subbytesTillNull(byte[] data, int offset) {
    	int new_length = 0;
        for (int i = offset; i < data.length; i++) {
            if (data[i] == 0x00) { // null
            	new_length = i;
                break;
            }
        }

        byte[] out = new byte[new_length];

        for (int x = offset; x < new_length; x++) {
            out[x - offset] = data[x];
        }

        return out;
    }

    /**
     * Get Payload without header and padded for decryption.
     * 
     * @param data the encrypted data message from the device and includes the header
     * @return Payload bytes without the header and padded to modulo 16
     */
    public byte[] getRawPayloadBytesPadded(byte[] data) {
        byte[] encData = subbytes(data, BLDevice.DEFAULT_BYTES_SIZE, data.length);
        byte[] newBytes = null;
        if(encData.length > 0) {
          int numpad = 16 - (encData.length % 16);

          newBytes = new byte[encData.length+numpad];
          for(int i = 0; i < newBytes.length; i++) {
        	  if(i < encData.length)
        		  newBytes[i] = encData[i];
        	  else
        		  newBytes[i] = 0x00;
          }
        }
        return newBytes;
    }
    
    protected byte[] decryptFromDeviceMessage(byte[] encData) throws Exception {
    	byte[] encPL = getRawPayloadBytesPadded(encData);
        byte[] pl = aes.decrypt(encPL);
        
    	return pl;
    }
    /**
     * Picks bytes from start-set to the end-set in a bytes array
     * 
     * @param data
     *            The bytes array to be used
     * @param start
     *            The starting position to be picked
     * @param end
     *            The ending position to be picked
     * @return The bytes array picked with length (<code>end - start</code>)
     */
    public static byte[] subbytes(byte[] data, int start, int end) {
        byte[] out = new byte[end - start];

        int outi = 0;
        for (int i = start; i < end; i++, outi++) {
            out[outi] = data[i];
        }

        return out;
    }

    /**
     * Sends a compiled packet to a destination host and port, and receives a
     * datagram from the source port specified.
     * 
     * @param pkt
     *            The compiled packet to be sent
     * @param sourceIpAddr
     *            Source IP address to be binded for receiving datagrams
     * @param sourcePort
     *            Source Port to be bineded for receiving datagrams
     * @param destIpAddr
     *            Destination IP address
     * @param destPort
     *            Destination Port
     * @param timeout
     *            Socket timeout. 0 will disable the timeout
     * @param bufSize
     *            Receiving datagram's buffer size
     * @return The received datagram
     * @throws IOException
     *             Thrown if socket timed out, cannot bind source IP and source
     *             port, no permission, etc.
     */
    public static DatagramPacket sendPkt(Packet pkt, InetAddress sourceIpAddr, int sourcePort, InetAddress destIpAddr,
            int destPort, int timeout, int bufSize) throws IOException {
    	log.debug("sendPkt - call with create socket for: " + sourceIpAddr.getHostAddress() + " and port " + sourcePort);
        DatagramSocket sock = new DatagramSocket(sourcePort, sourceIpAddr);

        sock.setBroadcast(true);
        sock.setReuseAddress(true);

        DatagramPacket recePkt = sendPkt(sock, pkt, destIpAddr, destPort, timeout, bufSize);
        sock.close();

        return recePkt;
    }

    /**
     * Sends a compiled packet to a destination host and port, and receives a
     * datagram from the source port specified.
     * 
     * @param sock
     *            Uses an external socket
     * @param pkt
     *            The compiled packet to be sent
     * @param destIpAddr
     *            Destination IP address
     * @param destPort
     *            Destination Port
     * @param timeout
     *            Socket timeout. 0 will disable the timeout
     * @param bufSize
     *            Receiving datagram's buffer size
     * @return The received datagram
     * @throws IOException
     *             Thrown if socket timed out, cannot bind source IP and source
     *             port, no permission, etc.
     */
    public static DatagramPacket sendPkt(DatagramSocket sock, Packet pkt, InetAddress destIpAddr, int destPort, int timeout, int bufSize) throws IOException {
    	
    	String boundHost = null;
    	if(sock.getInetAddress() == null)
    		boundHost = "0.0.0.0";
    	else
    		boundHost = sock.getInetAddress().getHostAddress();
    	log.debug("sendPkt - call with given sock for " + boundHost + " and port " + sock.getPort());

        byte[] data = pkt.getData();
        DatagramPacket sendpack = new DatagramPacket(data, data.length, destIpAddr, destPort);
        log.debug("snedPkt - data for length: " + data.length + " to: " + sendpack.getAddress().getHostAddress() + " for port: " + sendpack.getPort());

        byte[] rece = new byte[bufSize];
        DatagramPacket recepack = new DatagramPacket(rece, 0, rece.length);

        long startTime = System.currentTimeMillis();
        long elapsed;
        while ((elapsed = System.currentTimeMillis() - startTime) < timeout) {
            try {
                sock.send(sendpack);
                sock.setSoTimeout(1000);
                sock.receive(recepack);
                break;
            } catch (SocketTimeoutException e) {
                if (elapsed > timeout) {
                    break;
                }

                continue;
            }
        }

        log.debug("sendPkt - recv data bytes (" + recepack.getData().length +") after initial req: {}", DatatypeConverter.printHexBinary(recepack.getData()));
        recepack.setData(removeNullsFromEnd(recepack.getData(), 0));
        return recepack;
    }

    public static byte[] chgLen(byte[] data, int newLen) {
        byte[] newBytes = new byte[newLen];
        for (int i = 0; i < data.length; i++) {
            newBytes[i] = data[i];
        }
        return newBytes;
    }
}
