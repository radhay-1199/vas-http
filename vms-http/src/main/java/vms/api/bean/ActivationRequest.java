package vms.api.bean;

import java.util.Date;

public class ActivationRequest {

	private String appid;
	private String msisdn;
	private String packId;
	private int amount;
	private int validity;
	private String channel;
	private String serviceid;
	private String uuid;
	private Date requestTime;
	private String lang;
	private int hlrState;

	public int getHlrState() {
		return hlrState;
	}

	public void setHlrState(int hlrState) {
		this.hlrState = hlrState;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

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

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getValidity() {
		return validity;
	}

	public void setValidity(int validity) {
		this.validity = validity;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getServiceid() {
		return serviceid;
	}

	public void setServiceid(String serviceid) {
		this.serviceid = serviceid;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Date getRequestTime() {
		return requestTime;
	}

	public void setRequestTime(Date requestTime) {
		this.requestTime = requestTime;
	}

	@Override
	public String toString() {
		return "ActivationRequest [" + (appid != null ? "appid=" + appid + ", " : "")
				+ (msisdn != null ? "msisdn=" + msisdn + ", " : "") + (packId != null ? "packId=" + packId + ", " : "")
				+ "amount=" + amount + ", validity=" + validity + ", "
				+ (channel != null ? "channel=" + channel + ", " : "")
				+ (serviceid != null ? "serviceid=" + serviceid + ", " : "")
				+ (uuid != null ? "uuid=" + uuid + ", " : "")
				+ (requestTime != null ? "requestTime=" + requestTime + ", " : "")
				+ (lang != null ? "lang=" + lang + ", " : "") + "hlrState=" + hlrState + "]";
	}

}
