package vms.api.config;

public class Constants {

	public static final int SUB_REQ = 1;
	public static final int BAL_REQ = 2;
	public static final int UNSUB_REQ = 3;
	public static final int HLR_SUB = 4;
	public static final int HLR_UNSUB = 5;
	public static final int RENEWAL = 6;

	// RespCodes
	public static final int SUCCESS = 1;
	public static final int LOW_BALANCE = 2;
	public static final int INVALID_PACK = 3;
	public static final int FAILED_UNKNOWN_REASON = 4;
	public static final int ALREADY_SUB = 5;
	public static final int ALREADY_UNSUB = 5;
	public static final int MCA_ACTIVE = 7;
	public static final int INVALID_STATE = 8;

}
