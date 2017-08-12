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
 *     - Anthony Law (mob41) - Initial API Implementation
 *     - bwssytems
 *     - Christian Fischer (computerlyrik)
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
import com.github.mob41.blapi.pkt.auth.AuthPayload;
import com.github.mob41.blapi.pkt.dis.DiscoveryPacket;

/**
 * This is the base class of all Broadlink devices (e.g. SP1, RMPro)
 * @author Anthony
 *
 */
public abstract class BLDevice implements Closeable{

    /**
     * The specific logger for this class
     */
    private static final Logger log = LoggerFactory.getLogger(BLDevice.class);

    /**
     * Initial key for encryption
     */
    public static final byte[] INITIAL_KEY = {
            0x09, 0x76, 0x28, 0x34,
            0x3f, (byte) 0xe9, (byte) 0x9e, 0x23,
            0x76, 0x5c, 0x15, 0x13,
            (byte) 0xac, (byte) 0xcf, (byte) 0x8b, 0x02
    }; //16-byte

    /**
     * Initial iv for encryption
     */
    public static final byte[] INITIAL_IV = {
            0x56, 0x2e, 0x17, (byte) 0x99,
            0x6d, 0x09, 0x3d, 0x28,
            (byte) 0xdd, (byte) 0xb3, (byte) 0xba, 0x69,
            0x5a, 0x2e, 0x6f, 0x58
    }; //16-short


    //Devices type HEX

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

    public static final short DEV_RM_PRO_PHICOMM = 0x273d;

    public static final short DEV_RM_2_HOME_PLUS = 0x2783;

    public static final short DEV_RM_2_2HOME_PLUS_GDT = 0x277c;

    public static final short DEV_RM_2_PRO_PLUS = 0x272a;

    public static final short DEV_RM_2_PRO_PLUS_2 = 0x2787;

    public static final short DEV_RM_2_PRO_PLUS_2_BL = 0x278b;

    public static final short DEV_RM_MINI_SHATE = 0x278f;

    public static final short DEV_A1 = 0x2714;

    public static final short DEV_MP1 = 0x4EB5;

    /**
     * The destination port for discovery broadcasting (from __init__.py)
     */
    public static final int DISCOVERY_DEST_PORT = 80;

    /**
     * The discovery receive buffer size (from __init__.py)
     */
    public static final int DISCOVERY_RECEIVE_BUFFER_SIZE = 0x40; //64-bytes

    /**
     * Default discovery timeout (10 seconds)
     */
    public static final int DEFAULT_TIMEOUT = 10000; //10 seconds (10000 ms)

    /**
     * Packet count that is sent by this instance of BLDevice. 
     * This is for {@link #sendCmdPkt(CmdPayload) sendCmdPkt} method.
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
     * Device/Client ID. Initialization value is <code>{0,0,0,0}</code>.
     * And it is changed after the {@link #auth() auth} method, that
     * Broadlink devices will provide a id for this client/device. 
     * This is for {@link #sendCmdPkt(CmdPayload) sendCmdPkt} method.
     */
    private byte[] id;

    /**
     * Device type received from discovering devices, or those <code>BLDevice.DEV_*</code> constants
     */
    private final short deviceType;

    private String deviceDescription;

    /**
     * Specific datagram socket for this instance, to reuse address.
     */
    private DatagramSocket sock;

    /**
     * Target device host
     */
    private String host;

    /**
     * Target device MAC, using {@link com.github.mob41.blapi.mac.Mac} implementation to handle MAC addresses
     */
    private Mac mac;

    /**
     * Constructs a <code>BLDevice</code>, with a device type (constants), hostname and MAC address
     * @param deviceType Device type constants (<code>BLDevice.DEV_*</code>)
     * @param host Hostname of target Broadlink device
     * @param mac MAC address of target Broadlink device
     * @throws IOException Problems on constructing a datagram socket
     */
    protected BLDevice(short deviceType, String host, Mac mac) throws IOException{
        key = INITIAL_KEY;
        iv = INITIAL_IV;
        id = new byte[]{0, 0, 0, 0};

        pktCount = new Random().nextInt(0xffff);
        //pktCount = 0;

        this.deviceType = deviceType;

        this.host = host;
        this.mac = mac;

        sock = new DatagramSocket(0);
        sock.setReuseAddress(true);
        sock.setBroadcast(true);
    }

    /**
     * Releases the resources of this <code>BLDevice</code>
     */
    @Override
    public void close(){
        sock.close();
    }

    /**
     * Returns the device type of this Broadlink device
     * @return The device type in <code>short</code>
     */
    public short getDeviceType(){
        return deviceType;
    }

    /**
     * Returns this Broadlink device's hostname / IP address
     * @return The hostname / IP address in String
     */
    public String getHost(){
        return host;
    }

    /**
     * Returns this Broadlink device's MAC address
     * @return The MAC address in BLApi's <code>Mac</code> implementation
     */
    public Mac getMac(){
        return mac;
    }

    /**
     * Returns the encryption IV of this client
     * @return a byte array containing the IV
     */
    public byte[] getIv(){
        return iv;
    }

    /**
     * Returns the encryption key of this client
     * @return a byte array containing the key
     */
    public byte[] getKey(){
        return key;
    }

    public String getDeviceDescription() {
        return deviceDescription;
    }

    public void setDeviceDescription(String deviceDescription) {
        this.deviceDescription = deviceDescription;
    }

    //TODO: remove this
    //Development purpose
    public static void printBytes(byte[] data){
        String str = "";
        for(int i = 0; i < data.length; i++){
            str += Integer.toHexString(data[i]) + ",";
        }
        log.trace("printBytes: {}",str);
    }

    /**
     * Authenticates with the broadlink device, before any other control commands
     * @return Boolean whether the method is success or not
     * @throws IOException If I/O goes wrong
     */
    public boolean auth() throws IOException{
        boolean debug = log.isDebugEnabled();

        if (debug)
            log.debug("Authentication method starts");
        log.debug("Constructing AuthCmdPayload");

        AuthCmdPayload sendPayload = new AuthCmdPayload(AuthPayload.getDefaultDeviceId(), new byte[]{(int) 'T',(int) 'e',(int) 's',(int) 't',
                (int) ' ',(int) ' ',(int) '1'});

        if (debug)
            log.debug("Sending CmdPacket with AuthCmdPayload: cmd=" + Integer.toHexString(sendPayload.getCommand()) + " len=" + sendPayload.getPayload().getData().length);

        printBytes(sendPayload.getPayload().getData());

        DatagramPacket sendPack = sendCmdPkt(10000, 88, sendPayload);

        if (debug)
            log.debug("Received datagram");

        byte[] data = sendPack.getData();

        printBytes(data);

        if (debug)
            log.debug("Getting encrypted data from 0x38 to the end");

        byte[] encData = subbytes(data, 0x38, data.length);

        if (debug)
            log.debug("encDataLen=" + encData.length);

        if (encData.length % 16 != 0){
            log.warn("TODO: Incompatible decryption with non-16 multiple bytes. Forcing to have 1024 bytes");

            byte[] newBytes = new byte[1024];
            for (int i = 0; i < encData.length; i++){
                newBytes[i] = encData[i];
            }
            encData = newBytes;
        }

        if (debug)
            log.debug("Creating AES instance with initial iv, key");

        AES aes = new AES(INITIAL_IV, INITIAL_KEY);

        byte[] payload = null;
        try {
            if (debug)
                log.debug("Decrypting encrypted data");

            payload = aes.decrypt(encData);

            if (debug)
                log.debug("Decrypted. len=" + payload.length);

        } catch (Exception e) {
            log.error("Received datagram decryption error. Aborting method", e);
            return false;
        }

        log.debug("Packet received payload bytes: " + DatatypeConverter.printHexBinary(payload));

        if (debug)
            log.debug("Getting key from 0x04 to 0x14");

        key = subbytes(payload, 0x04, 0x14);

        log.debug("Packet received key bytes: " + DatatypeConverter.printHexBinary(key));

        if (key.length % 16 != 0){
            log.error("Received key len is not a multiple of 16! Aborting");
            return false;
        }

        if (debug)
            log.debug("Getting ID from 0x00 to 0x04");

        id = subbytes(payload, 0x00, 0x04);

        log.debug("Packet received id bytes: " + DatatypeConverter.printHexBinary(id));

        if (debug)
            log.debug("ID len=" + id.length);
        log.debug("End of authentication method");

        return true;
    }

    /**
     * Sends a command packet from localhost to Broadlink device, with buffer size 1024 bytes, 10 seconds timeout<br>
     * <br>
     * Before any commands to be sent to the device, {@link #auth() auth} must be ran
     * first in order to authenticate with the device and gain a device ID, encryption
     * key and IV.
     * @param cmdPayload Command data to be sent
     * @return {@link DatagramPacket} containing the byte data and sender host information.
     * @throws IOException Problems when sending the packet
     */
    public DatagramPacket sendCmdPkt(CmdPayload cmdPayload) throws IOException{
        return sendCmdPkt(10000, cmdPayload);
    }

    /**
     * Sends a command packet from localhost to Broadlink device, with default buffer size 1024 bytes<br>
     * <br>
     * Before any commands to be sent to the device, {@link #auth() auth} must be ran
     * first in order to authenticate with the device and gain a device ID, encryption
     * key and IV.
     * @param timeout Socket read timeout
     * @param cmdPayload Command data to be sent
     * @return {@link DatagramPacket} containing the byte data and sender host information.
     * @throws IOException Problems when sending the packet
     */
    public DatagramPacket sendCmdPkt(int timeout, CmdPayload cmdPayload) throws IOException{
        return sendCmdPkt(InetAddress.getLocalHost(), 0, timeout, 1024, cmdPayload);
    }

    /**
     * Sends a command packet from localhost to Broadlink device<br>
     * <br>
     * Before any commands to be sent to the device, {@link #auth() auth} must be ran
     * first in order to authenticate with the device and gain a device ID, encryption
     * key and IV.
     * @param timeout Socket read timeout
     * @param bufSize Receive datagram buffer size
     * @param cmdPayload Command data to be sent
     * @return {@link DatagramPacket} containing the byte data and sender host information.
     * @throws IOException Problems when sending the packet
     */
    public DatagramPacket sendCmdPkt(int timeout, int bufSize, CmdPayload cmdPayload) throws IOException{
        return sendCmdPkt(InetAddress.getLocalHost(), 0, timeout, bufSize, cmdPayload);
    }

    /**
     * Binds to a specific IP address and sends a command packet to Broadlink device<br>
     * <br>
     * Before any commands to be sent to the device, {@link #auth() auth} must be ran
     * first in order to authenticate with the device and gain a device ID, encryption
     * key and IV.
     * @param sourceIpAddr Bind the socket to this IP address
     * @param sourcePort Bind the socket to this port
     * @param timeout Socket read timeout
     * @param bufSize Receive datagram buffer size
     * @param cmdPayload Command data to be sent
     * @return {@link DatagramPacket} containing the byte data and sender host information.
     * @throws IOException Problems when sending the packet
     */
    public DatagramPacket sendCmdPkt(InetAddress sourceIpAddr, int sourcePort, int timeout, int bufSize, CmdPayload cmdPayload) throws IOException{
        CmdPacket cmdPkt = new CmdPacket(mac, pktCount++, id, iv, key, cmdPayload);
        printBytes(cmdPkt.getData());
        return sendPkt(sock, cmdPkt, sourceIpAddr, sourcePort, InetAddress.getByName(host), 80, timeout, bufSize);
    }

    /**
     * Creates a Broadlink device client
     * @param deviceType Device type constant (<code>BLDevice.DEV_*</code>)
     * @param host Target Broadlink device hostname
     * @param mac Target Broadlink device MAC address
     * @return A BLDevice client
     * @throws IOException Problems when constucting a datagram socket
     */
    public static BLDevice createInstance(short deviceType, String host, Mac mac) throws IOException{
        switch (deviceType){
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
            return new SP2Device(deviceType, host, mac);
        case DEV_RM_2:
        case DEV_RM_MINI:
        case DEV_RM_PRO_PHICOMM:
        case DEV_RM_2_HOME_PLUS:
        case DEV_RM_2_2HOME_PLUS_GDT:
        case DEV_RM_2_PRO_PLUS:
        case DEV_RM_2_PRO_PLUS_2:
        case DEV_RM_2_PRO_PLUS_2_BL:
        case DEV_RM_MINI_SHATE:
            return new RMDevice(deviceType, host, mac);
        case DEV_A1:
            return new A1Device(host, mac);
        case DEV_MP1:
            return new MP1Device(host, mac);
        }
        return null;
    }

    /**
     * Discover Broadlink devices in the local network, with {@link #DEFAULT_TIMEOUT default timeout}
     * @return An array of <code>BLDevice</code> in the network
     * @throws IOException Problems when discovering
     */
    public static BLDevice[] discoverDevices() throws IOException{
        return discoverDevices(DEFAULT_TIMEOUT);
    }

    /**
     * Discover Broadlink devices in the local network
     * @param timeout Socket read timeout
     * @return An array of <code>BLDevice</code> in the network
     * @throws IOException Problems when discovering
     */
    public static BLDevice[] discoverDevices(int timeout) throws IOException{
        return discoverDevices(InetAddress.getLocalHost(), 0, timeout);
    }

    /**
     * Discover Broadlink devices in the network, binded with a specific IP address
     * @param sourceIpAddr The IP address to be binded
     * @param sourcePort The port to be binded
     * @param timeout Socket read timeout
     * @return An array of <code>BLDevice</code> in the network
     * @throws IOException Problems when discovering
     */
    public static BLDevice[] discoverDevices(InetAddress sourceIpAddr, int sourcePort, int timeout) throws IOException{
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
        if (timeout == 0){
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
                log.debug("Info: host=" + host + " mac=" + mac.getMacString() + " deviceType=0x" + Integer.toHexString(deviceType));
            log.debug("Creating BLDevice instance");

            BLDevice inst = createInstance(deviceType, host, mac);

            if (inst != null){
                if (debug)
                    log.debug("Adding to found devices list");

                devices.add(inst);
            } else if (debug){
                log.debug("Cannot create instance, returned null, not adding to found devices list");
            }
        } else {
            if (debug)
                log.debug("A timeout of " + timeout + " ms was set. Running loop");

            long startTime = System.currentTimeMillis();
            long elapsed;
            while ((elapsed = System.currentTimeMillis() - startTime) < timeout){
                if (debug)
                    log.debug("Elapsed: " + elapsed + " ms");
                log.debug("Socket timeout: timeout-elapsed=" + (timeout - elapsed));

                sock.setSoTimeout((int) (timeout - elapsed));

                try {
                    if (debug)
                        log.debug("Waiting for datagrams");

                    sock.receive(recePacket);
                } catch (SocketTimeoutException e){
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
                    log.debug("Info: host=" + host + " mac=" + mac.getMacString() + " deviceType=0x" + Integer.toHexString(deviceType));
                log.debug("Creating BLDevice instance");

                BLDevice inst = createInstance(deviceType, host, mac);

                if (inst != null){
                    if (debug)
                        log.debug("Adding to found devices list");

                    devices.add(inst);
                } else if (debug){
                    log.debug("Cannot create instance, returned null, not adding to found devices list");
                }
            }
        }

        if (debug)
            log.debug("Converting list to array");

        BLDevice[] out = new BLDevice[devices.size()];

        for (int i = 0; i < out.length; i++){
            out[i] = devices.get(i);
        }

        if (debug)
            log.debug("End of device discovery");

        return out;
    }

    /**
     * Misc: Reverse the byte array
     * @param data Original data
     * @return Result byte array
     */
    public static byte[] reverseBytes(byte[] data){
        byte[] out = new byte[data.length];

        for (int i = 0; i < out.length; i++){
            out[i] = data[data.length - 1 - i];
        }

        return out;
    }

    /**
     * Misc: Pull bytes out from an array until a NULL (0) is detected
     * @param data Original data
     * @param offset Starting offset
     * @return Result byte array
     */
    public static byte[] subbytesTillNull(byte[] data, int offset){
        List<Byte> bytes = new ArrayList<Byte>(data.length);

        for (int i = offset; i < bytes.size(); i++){
            if ((bytes.get(i) & 0xff) == 0x00){ //null
                bytes.add(bytes.get(i));
            } else {
                break;
            }
        }

        byte[] out = new byte[bytes.size()];

        for (int i = 0; i < bytes.size(); i++){
            out[i] = data[i];
        }

        return out;
    }

    /**
     * Picks bytes from start-set to the end-set in a bytes array
     * @param data The bytes array to be used
     * @param start The starting position to be picked
     * @param end The ending position to be picked
     * @param endAtNull Whether return at null
     * @return The bytes array picked with length (<code>end - start</code>)
     */
    public static byte[] subbytes(byte[] data, int start, int end){
        byte[] out = new byte[end - start];

        int outi = 0;
        for (int i = start; i < end; i++, outi++){
            out[outi] = data[i];
        }

        return out;
    }

    /**
     * Sends a compiled packet to a destination host and port, and
     * receives a datagram from the source port specified.
     * @param pkt The compiled packet to be sent
     * @param sourceIpAddr Source IP address to be binded for receiving datagrams
     * @param sourcePort Source Port to be bineded for receiving datagrams
     * @param destIpAddr Destination IP address
     * @param destPort Destination Port
     * @param timeout Socket timeout. 0 will disable the timeout
     * @param bufSize Receiving datagram's buffer size
     * @return The received datagram
     * @throws IOException Thrown if socket timed out, cannot bind source IP and source port, no permission, etc.
     */
    public static DatagramPacket sendPkt(Packet pkt, 
            InetAddress sourceIpAddr, int sourcePort,
            InetAddress destIpAddr, int destPort,
            int timeout, int bufSize) throws IOException{
        DatagramSocket sock = new DatagramSocket(sourcePort, sourceIpAddr);

        sock.setBroadcast(true);
        sock.setReuseAddress(true);

        DatagramPacket recePkt = sendPkt(sock, pkt, sourceIpAddr,
                sourcePort, destIpAddr, destPort, timeout, bufSize);
        sock.close();

        return recePkt;
    }

    /**
     * Sends a compiled packet to a destination host and port, and
     * receives a datagram from the source port specified.
     * @param sock Uses an external socket
     * @param pkt The compiled packet to be sent
     * @param sourceIpAddr Source IP address to be binded for receiving datagrams
     * @param sourcePort Source Port to be bineded for receiving datagrams
     * @param destIpAddr Destination IP address
     * @param destPort Destination Port
     * @param timeout Socket timeout. 0 will disable the timeout
     * @param bufSize Receiving datagram's buffer size
     * @return The received datagram
     * @throws IOException Thrown if socket timed out, cannot bind source IP and source port, no permission, etc.
     */
    public static DatagramPacket sendPkt(DatagramSocket sock, Packet pkt, 
            InetAddress sourceIpAddr, int sourcePort,
            InetAddress destIpAddr, int destPort,
            int timeout, int bufSize) throws IOException{
        //sock.bind(new InetSocketAddress(ipAddr, sourcePort));

        byte[] data = pkt.getData();
        log.debug("DESTIP: " + destIpAddr.getHostAddress());
        log.debug("DESTPORT: " + destPort);
        DatagramPacket sendpack = new DatagramPacket(data, data.length, destIpAddr, destPort);
        //sock.send(sendpack);

        byte[] rece = new byte[bufSize];
        DatagramPacket recepack = new DatagramPacket(rece, 0, rece.length);

        long startTime = System.currentTimeMillis();
        long elapsed;
        while ((elapsed = System.currentTimeMillis() - startTime) < timeout){
            try {
                sock.send(sendpack);
                sock.setSoTimeout(1000);
                sock.receive(recepack);
                break;
            } catch (SocketTimeoutException e){
                if (elapsed > timeout){
                    break;
                }

                continue;
            }
        }

        return recepack;
    }

    public static byte[] chgLen(byte[] data, int newLen){
        byte[] newBytes = new byte[newLen];
        for (int i = 0; i < data.length; i++){
            newBytes[i] = data[i];
        }
        return newBytes;
    }
}
