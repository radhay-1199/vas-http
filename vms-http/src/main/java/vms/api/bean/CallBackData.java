package vms.api.bean;

public class CallBackData {
	private int verificationResultCode;
	private boolean renewalConsented;

	public int getVerificationResultCode() {
		return verificationResultCode;
	}

	public void setVerificationResultCode(int verificationResultCode) {
		this.verificationResultCode = verificationResultCode;
	}

	public boolean isRenewalConsented() {
		return renewalConsented;
	}

	public void setRenewalConsented(boolean renewalConsented) {
		this.renewalConsented = renewalConsented;
	}

}
