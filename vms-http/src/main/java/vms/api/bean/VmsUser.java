package vms.api.bean;

import java.util.Date;

public class VmsUser {

	private String msisdn;
	private String packId;
	private int status;
	private int hlrFlag;
	private Date nextRenewalDate;
	private String lang;

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getPackId() {
		return packId;
	}

	public void setPackId(String packId) {
		this.packId = packId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getHlrFlag() {
		return hlrFlag;
	}

	public void setHlrFlag(int hlrFlag) {
		this.hlrFlag = hlrFlag;
	}

	public Date getNextRenewalDate() {
		return nextRenewalDate;
	}

	public void setNextRenewalDate(Date nextRenewalDate) {
		this.nextRenewalDate = nextRenewalDate;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

}
