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
package com.github.mob41.blapi.mac;

import com.github.mob41.blapi.ex.BLApiRuntimeException;

/**
 * <code>MacFormatException</code> is thrown where the MAC address String /
 * bytes array specified does not have a valid format. (Total 48 bit / 6 bytes
 * 00:00:00:00:00:00)
 * 
 * @author Anthony
 *
 */
public class MacFormatException extends BLApiRuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = -4391144827334864769L;

    private static final String INVALID_MAC_FORMAT = "Invalid MAC address String format";

    private static final String INCORRECT_MAC_ADDR_BYTES = "Incorrect MAC address bytes";

    /**
     * Creates the exception with a "Invalid MAC address String format" message.
     * 
     * @param arg0
     *            the invalid MAC address String
     */
    public MacFormatException(String arg0) {
        super(INVALID_MAC_FORMAT + ": " + arg0);
    }

    /**
     * Creates the exception with a "Invalid MAC address String format" message,
     * and a <code>Throwable</code>
     * 
     * @param arg0
     *            the invalid MAC address String
     * @param arg1
     *            the Throwable
     */
    public MacFormatException(String arg0, Throwable arg1) {
        super(INVALID_MAC_FORMAT + ": " + arg0, arg1);
    }

    /**
     * Creates the exception with a "Incorrect MAC address bytes"
     * 
     * @param bytes
     *            the bytes array with incorrect length (not equal to 6)
     */
    public MacFormatException(byte[] bytes) {
        super(INCORRECT_MAC_ADDR_BYTES + ": " + (bytes != null ? bytes.length : "null") + "/6: "
                + bytesArrToHexStrArr(bytes));
    }

    private static String[] bytesArrToHexStrArr(byte[] ba) {
        if (ba == null) {
            return null;
        }

        String[] outa = new String[ba.length];
        for (int i = 0; i < outa.length; i++) {
            outa[i] = Integer.toHexString(ba[i]);
        }

        return outa;
    }

}
