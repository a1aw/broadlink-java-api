package com.github.mob41.blapi;

/**
 * This is the base class of all Broadlink devices (e.g. SP1, RMPro)
 * @author Anthony
 *
 */
public abstract class BLDevice {
	
	public static final byte[] INITIAL_KEY = {
			0x09, 0x76, 0x28, 0x34,
			0x3f, (byte) 0xe9, (byte) 0x9e, 0x23,
			0x76, 0x5c, 0x15, 0x13,
			(byte) 0xac, (byte) 0xcf, (byte) 0x8b, 0x02
	}; //16-byte
	
	public static final byte[] INITIAL_IV = {
			0x56, 0x2e, 0x17, (byte) 0x99,
			0x6d, 0x09, 0x3d, 0x28,
			(byte) 0xdd, (byte) 0xb3, (byte) 0xba, 0x69,
			0x5a, 0x2e, 0x6f, 0x58
	}; //16-short
	
	public static final short DEV_SP1 = 0x2711;
	
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

	public BLDevice(short deviceType, String host, String mac) {
		
	}

}
