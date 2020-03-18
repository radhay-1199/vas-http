package vms.api.bean;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CerebroCallBackResponse {

	@JsonProperty("Registration_confirmation")
	private boolean success;

	@JsonProperty("Service_Expiration")
	private String expireDate;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(String expireDate) {
		this.expireDate = expireDate;
	}
}
