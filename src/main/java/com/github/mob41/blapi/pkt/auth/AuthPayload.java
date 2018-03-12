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
package com.github.mob41.blapi.pkt.auth;

import com.github.mob41.blapi.pkt.Payload;

public class AuthPayload implements Payload {

    private final byte[] data;

    public AuthPayload() {
        data = new byte[0x50];

        data[0x04] = 0x31;
        data[0x05] = 0x31;
        data[0x06] = 0x31;
        data[0x07] = 0x31;
        data[0x08] = 0x31;
        data[0x09] = 0x31;
        data[0x0a] = 0x31;
        data[0x0b] = 0x31;
        data[0x0c] = 0x31;
        data[0x0d] = 0x31;
        data[0x0e] = 0x31;
        data[0x0f] = 0x31;
        data[0x10] = 0x31;
        data[0x11] = 0x31;
        data[0x12] = 0x31;
        data[0x1e] = 0x01;
        data[0x2d] = 0x01;
        data[0x30] = (byte) 'T';
        data[0x31] = (byte) 'e';
        data[0x32] = (byte) 's';
        data[0x33] = (byte) 't';
        data[0x34] = (byte) ' ';
        data[0x35] = (byte) ' ';
        data[0x36] = (byte) '1';
    }

    @Override
    public byte[] getData() {
        return data;
    }
}
