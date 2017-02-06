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
package com.github.mob41.blapi.mac;

/**
 * A class that handles a MAC address in String and bytes array format
 * @author Anthony
 *
 */
public class Mac {

	private final byte[] mac;
	
	/**
	 * Creates an instance representing the MAC address
	 * @param macStr The 6-byte MAC address in byte array
	 * @throws MacFormatException If the MAC address bytes array specified is not with length 6 or <code>null</code>
	 */
	public Mac(byte[] macBytes) throws MacFormatException{
		if (!isMACValid(macBytes)){
			throw new MacFormatException(macBytes);
		}
		mac = macBytes;
	}
	
	/**
	 * Creates an instance representing the MAC address
	 * @param macStr MAC address represented in String seperated by cottons (<code>:</code>) (e.g. 00:00:00:00:00:00)
	 * @throws MacFormatException If the MAC address does not have a valid format:
	 * 
	 * does not have a hex (e.g. 0f) in the <code>macStr</code>, or, does not have
	 * 6 hex separated by cottons (:), or, a <code>null</code> is specified
	 */
	public Mac(String macStr) throws MacFormatException{
		mac = macStrToBytes(macStr);
	}
	
	/**
	 * Returns the MAC address in bytes array
	 * @return MAC address in bytes array
	 */
	public byte[] getMac(){
		return mac;
	}
	
	/**
	 * Returns the MAC address represented in String
	 * @return MAC address in String
	 */
	public String getMacString(){
		return bytesToMacStr(mac);
	}
	
	/**
	 * Converts MAC address String into bytes
	 * @param macStr The 6-byte MAC Address (00:00:00:00:00:00) in String separated by cottons (<code>:</code>)
	 * @return Converted MAC Address in bytes
	 * @throws MacFormatException If the MAC address does not have a valid format:
	 * 
	 * does not have a hex (e.g. 0f) in the <code>macStr</code>, or, does not have
	 * 6 hex separated by cottons (:), or, a <code>null</code> is specified
	 */
	public static byte[] macStrToBytes(String macStr) throws MacFormatException{
		if (macStr == null){
			throw new MacFormatException(macStr);
		}
		
		String[] macs = macStr.split(":");
		
		if (macs.length != 6){
			throw new MacFormatException(macStr);
		}
		
		byte[] bout = new byte[6];
		for (int i = 0; i < macs.length; i++){
			try {
				bout[i] = Byte.parseByte(macs[i]);
			} catch (NumberFormatException e){
				throw new MacFormatException(macStr, e);
			}
		}
		
		return bout;
	}
	
	/**
	 * Returns whether the specified MAC bytes array is valid
	 * with the following conditions:<br>
	 * <br>
	 * 1. <code>macBytes</code> not <code>null</code><br>
	 * 2. <code>macBytes</code>'s length is equal to 6
	 * @param macBytes The byte array to be validated
	 * @return The validation result
	 */
	public static boolean isMACValid(byte[] macBytes){
		return macBytes != null && macBytes.length == 6;
	}
	
	/**
	 * Converts MAC address bytes into String
	 * @param macBytes The 6-byte MAC Address in byte array
	 * @return A MAC address String converted from the byte array
	 * @throws MacFormatException If the MAC address bytes array specified is not with length 6 or <code>null</code>
	 */
	public static String bytesToMacStr(byte[] macBytes) throws MacFormatException{
		if (!isMACValid(macBytes)){
			throw new MacFormatException(macBytes);
		}
		
		String str = "";
		
		for (int i = 0; i < macBytes.length; i++){
			String hexStr = Integer.toHexString(macBytes[i]);
			str += hexStr;
			
			if (i != macBytes.length - 1){
				str += ':';
			}
		}
		
		return str;
	}

}
