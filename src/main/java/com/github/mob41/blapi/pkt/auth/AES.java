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
package com.github.mob41.blapi.pkt.auth;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {

    private static final String CIPHER_ALGO = "AES/CBC/NoPadding";

    private static final String KEY_ALGO = "AES";

    private final byte[] key;

    private final byte[] iv;

    public AES(byte[] iv, byte[] key){
        this.key = key;
        this.iv = iv;
    }

    public byte[] encrypt(byte[] data) throws Exception {
        Cipher c = Cipher.getInstance(CIPHER_ALGO);
        c.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, KEY_ALGO), new IvParameterSpec(iv));
        return c.doFinal(data);
    }

    public byte[] decrypt(byte[] data) throws Exception {
        Cipher c = Cipher.getInstance(CIPHER_ALGO);
        c.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, KEY_ALGO), new IvParameterSpec(iv));
        return c.doFinal(data);
    }

}
