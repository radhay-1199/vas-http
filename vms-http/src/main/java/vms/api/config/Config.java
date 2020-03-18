package vms.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@PropertySource({ "file:./config/vms-mo.properties" })

public class Config {

	@Autowired
	private Environment env;

	@Value("${vms.fw.ip}")
	private String fwIp;

	@Value("${vms.fw.port}")
	private int fwPort;

	@Value("${vms.test.number}")
	private String validNumber;

	@Value("${vms.service.id}")
	private String vmsServiceId;

	@Value("${cerebro.callback.url}")
	private String cerebroCallbackURL;

	@Value("${cerebro.login.username}")
	private String cerebroUserName;

	@Value("${cerebro.login.password}")
	private String cerebroPassword;

	@Value("${cerebro.credit.check.api}")
	private String cerebroCreditCheckApi;

	@Value("${cerebro.second.consent.api}")
	private String cerebroSecondConsentApi;

	@Value("${sub.success.msg.text}")
	private String subSuccessMsgText;

	@Value("${sub.success.msg.ivr}")
	private String ivrSubSuccessMsg;

	@Value("${sub.lowbalance.msg.text}")
	private String subLowBalanceMsgText;

	@Value("${renewal.alert.msg.text}")
	private String renewalAlertMsgText;

	@Value("${renewal.success.msg.text}")
	private String renewalSuccessMsgText;

	@Value("${unsub.success.msg.text}")
	private String unsubSuccessMsgText;

	@Value("${unsub.success.msg.ivr}")
	private String ivrUnsubSuccessMsg;

	@Value("${mca.sub.msg.text}")
	private String mcaAlreaySubMsg;

	@Value("${sms.submit.url}")
	private String smsSubmitUrl;

	@Value("${vms.sub.url}")
	private String subApiURL;

	@Value("${vms.unsub.url}")
	private String unSubApiURL;

	@Value("${callback.wait.time}")
	private int callBackWaitTime;

	public int getCallBackWaitTime() {
		return callBackWaitTime;
	}

	public void setCallBackWaitTime(int callBackWaitTime) {
		this.callBackWaitTime = callBackWaitTime;
	}

	public String getSubApiURL() {
		return subApiURL;
	}

	public void setSubApiURL(String subApiURL) {
		this.subApiURL = subApiURL;
	}

	public String getUnSubApiURL() {
		return unSubApiURL;
	}

	public void setUnSubApiURL(String unSubApiURL) {
		this.unSubApiURL = unSubApiURL;
	}

	public String getCerebroUserName() {
		return cerebroUserName;
	}

	public void setCerebroUserName(String cerebroUserName) {
		this.cerebroUserName = cerebroUserName;
	}

	public String getCerebroPassword() {
		return cerebroPassword;
	}

	public void setCerebroPassword(String cerebroPassword) {
		this.cerebroPassword = cerebroPassword;
	}

	public String getCerebroSecondConsentApi() {
		return cerebroSecondConsentApi;
	}

	public void setCerebroSecondConsentApi(String cerebroSecondConsentApi) {
		this.cerebroSecondConsentApi = cerebroSecondConsentApi;
	}

	public String getCerebroCreditCheckApi() {
		return cerebroCreditCheckApi;
	}

	public void setCerebroCreditCheckApi(String cerebroCreditCheckApi) {
		this.cerebroCreditCheckApi = cerebroCreditCheckApi;
	}

	public String getValidNumber() {
		return validNumber;
	}

	public void setValidNumber(String validNumber) {
		this.validNumber = validNumber;
	}

	public Environment getEnv() {
		return env;
	}

	public void setEnv(Environment env) {
		this.env = env;
	}

	public String getFwIp() {
		return fwIp;
	}

	public void setFwIp(String fwIp) {
		this.fwIp = fwIp;
	}

	public int getFwPort() {
		return fwPort;
	}

	public void setFwPort(int fwPort) {
		this.fwPort = fwPort;
	}

	public String getVmsServiceId() {
		return vmsServiceId;
	}

	public void setVmsServiceId(String vmsServiceId) {
		this.vmsServiceId = vmsServiceId;
	}

	public String getCerebroCallbackURL() {
		return cerebroCallbackURL;
	}

	public void setCerebroCallbackURL(String cerebroCallbackURL) {
		this.cerebroCallbackURL = cerebroCallbackURL;
	}

	public String getSubSuccessMsgText() {
		return subSuccessMsgText;
	}

	public void setSubSuccessMsgText(String subSuccessMsgText) {
		this.subSuccessMsgText = subSuccessMsgText;
	}

	public String getIvrSubSuccessMsg() {
		return ivrSubSuccessMsg;
	}

	public void setIvrSubSuccessMsg(String ivrSubSuccessMsg) {
		this.ivrSubSuccessMsg = ivrSubSuccessMsg;
	}

	public String getSubLowBalanceMsgText() {
		return subLowBalanceMsgText;
	}

	public void setSubLowBalanceMsgText(String subLowBalanceMsgText) {
		this.subLowBalanceMsgText = subLowBalanceMsgText;
	}

	public String getRenewalAlertMsgText() {
		return renewalAlertMsgText;
	}

	public void setRenewalAlertMsgText(String renewalAlertMsgText) {
		this.renewalAlertMsgText = renewalAlertMsgText;
	}

	public String getRenewalSuccessMsgText() {
		return renewalSuccessMsgText;
	}

	public void setRenewalSuccessMsgText(String renewalSuccessMsgText) {
		this.renewalSuccessMsgText = renewalSuccessMsgText;
	}

	public String getUnsubSuccessMsgText() {
		return unsubSuccessMsgText;
	}

	public void setUnsubSuccessMsgText(String unsubSuccessMsgText) {
		this.unsubSuccessMsgText = unsubSuccessMsgText;
	}

	public String getIvrUnsubSuccessMsg() {
		return ivrUnsubSuccessMsg;
	}

	public void setIvrUnsubSuccessMsg(String ivrUnsubSuccessMsg) {
		this.ivrUnsubSuccessMsg = ivrUnsubSuccessMsg;
	}

	public String getMcaAlreaySubMsg() {
		return mcaAlreaySubMsg;
	}

	public void setMcaAlreaySubMsg(String mcaAlreaySubMsg) {
		this.mcaAlreaySubMsg = mcaAlreaySubMsg;
	}

	public String getSmsSubmitUrl() {
		return smsSubmitUrl;
	}

	public void setSmsSubmitUrl(String smsSubmitUrl) {
		this.smsSubmitUrl = smsSubmitUrl;
	}

}
