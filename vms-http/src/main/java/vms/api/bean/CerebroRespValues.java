package vms.api.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CerebroRespValues {
	@JsonProperty("sufficient_Credit")
	private boolean sufficientCredit;

	public boolean isSufficientCredit() {
		return sufficientCredit;
	}

	public void setSufficientCredit(boolean sufficientCredit) {
		this.sufficientCredit = sufficientCredit;
	}
	
}
