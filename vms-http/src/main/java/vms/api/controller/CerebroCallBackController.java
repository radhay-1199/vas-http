package vms.api.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ch.qos.logback.core.encoder.EchoEncoder;
import vms.api.bean.ActivationConfig;
import vms.api.bean.ActivationRequest;
import vms.api.bean.CallBackData;
import vms.api.bean.CerebroCallBackResponse;
import vms.api.bean.HLRResponse;
import vms.api.bean.ReportData;
import vms.api.config.Config;
import vms.api.config.Constants;
import vms.api.config.StaticRefer;
import vms.api.db.ActivationConfigRepository;
import vms.api.db.ActivationRequestRepository;
import vms.api.db.VmsReportRepository;
import vms.api.db.VmsUserRepository;
import vms.api.service.ProcessHLRRequest;
import vms.api.service.SmsUtil;

@Controller
@CrossOrigin

public class CerebroCallBackController {

	private Logger log = LogManager.getRootLogger();

	@Autowired
	private Config config;

	@Autowired
	private ActivationRequestRepository activationRequestRepo;

	@Autowired
	private VmsUserRepository vmsUserRepo;

	@Autowired
	private VmsReportRepository vmsReportRepo;

	@Autowired
	private ProcessHLRRequest hlrRequestProcessor;

	@Autowired
	private SmsUtil smsUtil;

	@Autowired
	private ActivationConfigRepository activationConfigRepo;

	@RequestMapping(value = "/user/register", method = RequestMethod.GET)
	@ResponseBody
	
	public String vmsSubRequest(@RequestParam("msisdn") String msisdn , @RequestParam("validity") int validity ) {
		HLRResponse hlrResp = hlrRequestProcessor.processRequest(msisdn, Constants.HLR_SUB);
		if (hlrResp == null) {
			return ("HLR Response is NUll , Please Check");
		}

		if (hlrResp.getOutputMessage().indexOf("Already have the service") != -1) {
			return "Alreay Subscriber for MCA";
		} else if (hlrResp.getOutputMessage().indexOf("SUCCESS") == -1) {
			return ("HLR Response is invalid for sub , Resp=" + hlrResp.getOutputMessage());
		}

		ActivationRequest req = new ActivationRequest();
		req.setMsisdn(msisdn);
		req.setLang("1");
		req.setChannel("CC");
		req.setPackId("P3");
		req.setValidity(validity);
		insertIntoVMSUsers( req, false );
		log.info("User added|" + req.toString());
		return "Success";
		
	}

	@RequestMapping(value = "/consent/result", method = RequestMethod.GET)
	@ResponseBody
	public CerebroCallBackResponse vmsSubRequest(@RequestParam("UniqueId") String uuid,
			@RequestParam("Verification_Result_Code") int verificationResultCode,
			@RequestParam("Renewal_Consented") boolean renewalConsented) {

		log.info("CallBack UUID=" + uuid + ",Verification_Result_Code=" + verificationResultCode + ",Renewal_Consented="
				+ renewalConsented);

		CallBackData callBackData = new CallBackData();
		callBackData.setRenewalConsented(renewalConsented);
		callBackData.setVerificationResultCode(verificationResultCode);
		StaticRefer.actRequestMap.put(uuid, callBackData);

		CerebroCallBackResponse resp = new CerebroCallBackResponse();

		ActivationRequest req = activationRequestRepo.getActivationRequest(uuid);
		if (req == null) {

			log.info("Request not found for uuid=" + uuid);
			resp.setSuccess(false);
			return resp;
		}

		Date expireDate = null;
		if (verificationResultCode == 1) {
			if ("AGR".equals(req.getAppid()) || "MM".equals(req.getAppid())) {
				expireDate = insertIntoVMSUsers(req, renewalConsented);
			}
			resp.setSuccess(true);

		} else {

			HLRResponse hlrResp = hlrRequestProcessor.processRequest(req.getMsisdn(), Constants.HLR_UNSUB);
			log.info("Failed second consent for uuid=" + uuid);
			resp.setSuccess(false);

		}

		activationRequestRepo.updateHLRState(req.getMsisdn(), 0);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (expireDate != null)
			resp.setExpireDate(dateFormat.format(expireDate));
		return resp;

	}

	public Date insertIntoVMSUsers(ActivationRequest req, boolean renewal) {

		Date nextRenewal = addDayInDate(req.getValidity());
		vmsReportRepo
				.insertIntoReports(new ReportData(req.getMsisdn(), 1, 1, req.getChannel(), "success", req.getUuid()));

		vmsUserRepo.insertIntoUsers(req.getMsisdn(), req.getPackId(), nextRenewal, req.getLang(), req.getChannel(),
				renewal);

		ActivationConfig actConfig = activationConfigRepo.getActivationConfig( req.getAppid(), req.getPackId() );
		if (actConfig == null) {
			log.info("Activation Config missing for packId=" + req.getPackId());
			return nextRenewal;
		}
		smsUtil.sendSMS(req.getMsisdn(), config.getSubSuccessMsgText(), actConfig.getPackName(), req.getAmount());
		return nextRenewal;

	}

	public Date addDayInDate(int days) {

		Calendar cal = null;
		try {
			cal = Calendar.getInstance();
			cal.add(Calendar.DATE, days);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cal.getTime();
	}
}
