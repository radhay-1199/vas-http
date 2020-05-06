package vms.api.controller;

import org.apache.http.HttpResponse;

import vms.api.config.Constants;
import vms.api.config.StaticRefer;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URLEncoder;
import java.util.UUID;

import vms.api.bean.ActivationConfig;
import vms.api.bean.ActivationRequest;
import vms.api.bean.ApiResp;
import vms.api.bean.CallBackData;
import vms.api.bean.CerebroRespValues;
import vms.api.bean.HLRResponse;
import vms.api.bean.ReportData;
import vms.api.bean.VmsUser;
import vms.api.config.Config;
import vms.api.db.ActivationConfigRepository;
import vms.api.db.ActivationRequestRepository;
import vms.api.db.VmsReportRepository;
import vms.api.db.VmsUserRepository;
import vms.api.service.ProcessHLRRequest;
import vms.api.service.SmsUtil;

@Controller
@CrossOrigin
public class CerebroSubController {

	private Logger log = LogManager.getRootLogger();

	@Autowired
	private Config config;

	@Autowired
	private ActivationRequestRepository activationRequestRepo;

	@Autowired
	private ActivationConfigRepository activationConfigRepo;

	@Autowired
	private ProcessHLRRequest hlrRequestProcessor;

	@Autowired
	private SmsUtil smsUtil;

	@Autowired
	private VmsUserRepository vmsUserRepo;

	@Autowired
	private VmsReportRepository vmsReportRepo;

	public int getChannelCode(String channel) {
		if ("IVR".equals(channel))
			return 1;
		else if ("SMS".equals(channel))
			return 4;
		else if("CC".equals(channel))
				return 6;
		else if("USSD".equals(channel))
			return 3;
		else
			return 1;
	}

	@RequestMapping(value = "/vms/unsub", method = RequestMethod.GET)
	@ResponseBody
	public String vmsUnSubRequest(@RequestParam("msisdn") String msisdn,
			@RequestParam(value = "channel", defaultValue = "IVR") String channel,
			@RequestParam(value = "appid", defaultValue = "AGR") String appId) {

		log.info("UnSub Request|msisdn=" + msisdn);

		if (msisdn.startsWith("+93"))
			msisdn = msisdn.replaceFirst("\\+93", "");

		VmsUser user = vmsUserRepo.getUserDetails(msisdn);

		if (user == null) {

			log.info("Already Non-ctive Subscriber msisdn=" + msisdn);
			vmsReportRepo
					.insertIntoReports(new ReportData(msisdn, Constants.HLR_UNSUB, 0, channel, "Unknown User", msisdn));
			return "" + Constants.ALREADY_UNSUB;

		}

		ActivationConfig actConfig = activationConfigRepo.getActivationConfig(appId, user.getPackId());
		HLRResponse hlrResp = hlrRequestProcessor.processRequest(msisdn, Constants.HLR_UNSUB);

		if (hlrResp.getOutputMessage() != null && hlrResp.getOutputMessage().indexOf("SUCCESS") != -1) {
			vmsUserRepo.deleteUser(msisdn);
			if (channel != null && channel.equals("IVR"))
				smsUtil.sendSMS(msisdn, config.getIvrUnsubSuccessMsg(), actConfig.getPackName(), actConfig.getAmount());
			else
				smsUtil.sendSMS(msisdn, config.getUnsubSuccessMsgText(), actConfig.getPackName(),
						actConfig.getAmount());

			vmsReportRepo.insertIntoReports(new ReportData(msisdn, Constants.HLR_UNSUB, 1, msisdn, "success", msisdn));

		} else {
			log.info("Invalid HLR Response=" + hlrResp.getOutputMessage());
			vmsReportRepo.insertIntoReports(
					new ReportData(msisdn, Constants.HLR_UNSUB, 0, channel, "Invalid HLR Response", msisdn));
			return "" + Constants.FAILED_UNKNOWN_REASON;
		}
		return "" + 1;

	}

	@RequestMapping(value = "/api/sub", method = RequestMethod.GET)
	@ResponseBody
	public String vmsSubRequest(@RequestParam("msisdn") String msisdn,
			@RequestParam(value = "appid", defaultValue = "AGR") String appId,
			@RequestParam(value = "pack", required = false) String packId,
			@RequestParam(value = "channel", defaultValue = "IVR") String channel,
			@RequestParam(value = "lang", defaultValue = "1") String lang) {

		try {

			log.info("msisdn=" + msisdn + ",appid=" + appId + ",pack=" + packId + ",channel=" + channel);
			msisdn = msisdn.substring(msisdn.length() - 9);

			if ("AGR".equals(appId) || "MM".equals(appId)) {
				VmsUser user = vmsUserRepo.getUserDetails(msisdn);
				if (user != null) {
					return "" + Constants.ALREADY_SUB;
				}
			}

			if ("IVR".equals(channel)) {
				packId = getPackId(appId, packId);
			}

			ActivationConfig actConfig = activationConfigRepo.getActivationConfig(appId, packId);
			if (actConfig == null) {
				return "" + Constants.INVALID_PACK;
				// return "failed";
			}

			String ivrLang = "ldar";
			if (lang.equals("1"))
				ivrLang = "leng";
			else if (lang.equals("2"))
				ivrLang = "ldar";
			else if (lang.equals("3"))
				ivrLang = "lpas";
			else
				ivrLang = "ldar";

			String uuid = getUUID();
			String cerebroCreditCheckUrl = config.getCerebroCreditCheckApi();
			cerebroCreditCheckUrl = cerebroCreditCheckUrl + "UniqueId=" + uuid;
			cerebroCreditCheckUrl = cerebroCreditCheckUrl + "&MSISDN=" + msisdn;
			cerebroCreditCheckUrl = cerebroCreditCheckUrl + "&Username=" + config.getCerebroUserName();
			cerebroCreditCheckUrl = cerebroCreditCheckUrl + "&Password=" + config.getCerebroPassword();
			// Service ID Should be as per pack . ActConfig will have that information
			cerebroCreditCheckUrl = cerebroCreditCheckUrl + "&ServiceId=" + actConfig.getServiceId();
			cerebroCreditCheckUrl = cerebroCreditCheckUrl + "&Registration_Type=1";
			cerebroCreditCheckUrl = cerebroCreditCheckUrl + "&Registration_Cost=" + actConfig.getAmount();
			cerebroCreditCheckUrl = cerebroCreditCheckUrl + "&Renewal_Cost=" + actConfig.getAmount();
			cerebroCreditCheckUrl = cerebroCreditCheckUrl + "&Service_Duration=" + actConfig.getValidity();
			cerebroCreditCheckUrl = cerebroCreditCheckUrl + "&Registration_Channel=" + getChannelCode(channel);
			cerebroCreditCheckUrl = cerebroCreditCheckUrl + "&Obtain_Renewal_Consent=true";
			cerebroCreditCheckUrl = cerebroCreditCheckUrl + "&Language=" + ivrLang;
			cerebroCreditCheckUrl = cerebroCreditCheckUrl + "&Verification_Process_Result_URL="
					+ URLEncoder.encode(config.getCerebroCallbackURL(), "UTF-8");

			ActivationRequest req = new ActivationRequest();
			req.setMsisdn(msisdn);
			req.setAmount(actConfig.getAmount());
			req.setValidity(actConfig.getValidity());
			req.setChannel(channel);
			req.setAppid(appId);
			req.setPackId(packId);
			req.setServiceid(config.getVmsServiceId());
			req.setUuid(uuid);
			req.setLang(lang);

			activationRequestRepo.insertIntoActivationRequest(req);
			log.info(cerebroCreditCheckUrl);

			ApiResp resp = submitRequest(cerebroCreditCheckUrl);
			log.info(resp.toString());

			if (resp.getRespCode() == 200) {
				CerebroRespValues cbValue = new ObjectMapper().readValue(resp.getRespText(), CerebroRespValues.class);
				if (cbValue.isSufficientCredit()) {
					// StaticRefer.actRequestMap.put(req.getUuid(), req);
					int status = callVerificationApi(req);

					if (status == 1) {
						int respCode = waitForCallBack(req);
						log.info("Response To IVR |" + respCode);
						return "" + respCode;
					}

					log.info("Response To IVR |" + status);
					return "" + status;

				} else {

					log.info("Low Balance");
					smsUtil.sendSMS(req.getMsisdn(), config.getSubLowBalanceMsgText(), actConfig.getPackId(),
							actConfig.getAmount());

					vmsReportRepo.insertIntoReports(new ReportData(req.getMsisdn(), Constants.SUB_REQ, 0,
							req.getChannel(), "Low Balance", uuid));

					log.info("Response To IVR For Low Balance |" + Constants.LOW_BALANCE);
					return "" + Constants.LOW_BALANCE;

				}
			} else {

				vmsReportRepo.insertIntoReports(new ReportData(req.getMsisdn(), Constants.SUB_REQ, 0, req.getChannel(),
						"Invalid State-" + resp.getRespCode(), uuid));
				log.info("Response To IVR Invalid State |" + Constants.INVALID_STATE);
				return "" + Constants.INVALID_STATE;

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		log.info("Response To IVR  State |null");
		return null;
	}

	public int waitForCallBack(ActivationRequest req) {
		int waitTime = config.getCallBackWaitTime();
		try {
			while (waitTime > 0) {
				Thread.sleep(1000);
				log.info(req.getUuid() + "|Waiting for CallBack Time Left to wait = " + waitTime / 1000);
				waitTime = waitTime - 1000;
				CallBackData callbackData = StaticRefer.actRequestMap.remove(req.getUuid());
				if (callbackData != null && callbackData.getVerificationResultCode() == 1) {
					return Constants.SUCCESS;
				}
			}
			
		} catch (Exception exp) {
			exp.printStackTrace();
		}
		return Constants.FAILED_UNKNOWN_REASON;
	}

	public String getPackId(String appId, String packValue) {
		if (packValue == null)
			return "P3";

		if (packValue.equalsIgnoreCase("1"))
			return "P3";

		if (packValue.equalsIgnoreCase("2"))
			return "P2";

		else
			return packValue;
	}

	public String getUUID() {
		// UUID uuid = UUID.fromString("xxxxxxxx-xxxx-Mxxx-Nxxx-xxxxxxxxxxxx");
		return UUID.randomUUID().toString();
	}

	public ApiResp submitRequest(String url) {
		ApiResp resp = new ApiResp();
		resp.setRespCode(0);
		try {

			HttpClient httpClient = HttpClients.createDefault();
			HttpGet getRequest = new HttpGet(url);
			HttpResponse response = httpClient.execute(getRequest);
			int respCode = response.getStatusLine().getStatusCode();
			resp.setRespCode(respCode);
			if (respCode == 200) {
				resp.setRespText(EntityUtils.toString(response.getEntity()));
			} else {
				log.info("Response Code=" + response.toString());
			}

		} catch (Exception exp) {
			resp.setRespCode(-1);
			exp.printStackTrace();
		}
		return resp;
	}

	public int callVerificationApi(ActivationRequest req) {
		log.info("Inside callVerificationApi");
		HLRResponse hlrResp = hlrRequestProcessor.processRequest(req.getMsisdn(), Constants.HLR_SUB);

		if (hlrResp == null) {
			log.info("HLR Response is NUll , Please Check");
			return -1;
		}

		if (hlrResp.getOutputMessage().indexOf("Already have the service") != -1) {
			log.info("Alreay Subscriber for MCA, Resp=" + hlrResp.getOutputMessage());
			smsUtil.sendSMS(req.getMsisdn(), config.getMcaAlreaySubMsg(), req.getAppid(), req.getAmount());
			return Constants.MCA_ACTIVE;

		} else if (hlrResp.getOutputMessage().indexOf("SUCCESS") == -1) {

			log.info("HLR Response is invalid for sub , Resp=" + hlrResp.getOutputMessage());
			return Constants.FAILED_UNKNOWN_REASON;

		}

		String verificationApiURL = config.getCerebroSecondConsentApi();
		verificationApiURL = verificationApiURL + "UniqueId=" + req.getUuid();
		verificationApiURL = verificationApiURL + "&Username=" + config.getCerebroUserName();
		verificationApiURL = verificationApiURL + "&Password=" + config.getCerebroPassword();
		log.info(verificationApiURL);

		ApiResp resp = submitRequest(verificationApiURL);
		log.info(resp.toString());
		return 1;
	}
}
