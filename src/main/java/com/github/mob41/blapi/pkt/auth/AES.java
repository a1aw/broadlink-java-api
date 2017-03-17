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
