package vms.api.bean;

public class ApiResp {
	private int respCode;
	private String respText;

	public int getRespCode() {
		return respCode;
	}

	public void setRespCode(int respCode) {
		this.respCode = respCode;
	}

	public String getRespText() {
		return respText;
	}

	public void setRespText(String respText) {
		this.respText = respText;
	}

	@Override
	public String toString() {
		return "ApiResp [respCode=" + respCode + ", " + (respText != null ? "respText=" + respText : "") + "]";
	}

}
