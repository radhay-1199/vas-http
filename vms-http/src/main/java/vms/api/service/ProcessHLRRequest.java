package vms.api.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vms.api.bean.*;
import vms.api.db.*;
import vms.api.config.Config;
import vms.api.config.Constants;

@Service
public class ProcessHLRRequest {
	private Logger log = LogManager.getRootLogger();

	@Autowired
	private Config config;

	@Autowired
	private HttpUtil httpUtil;

	public HLRResponse parseResponse(String resp) {
		try {

			log.info("Resp=" + resp);
			String msisdn = null;
			String output2 = null;
			String output = null;

			if (resp.indexOf("<MSISDN>") != -1)
				msisdn = resp.substring(resp.indexOf("<MSISDN>") + 8, resp.indexOf("</MSISDN>"));

			if (resp.indexOf("<output>") != -1)
				output = resp.substring(resp.indexOf("<output>") + 8, resp.indexOf("</output>"));

			if (resp.indexOf("<output2>") != -1)
				output2 = resp.substring(resp.indexOf("<output2>") + 9, resp.indexOf("</output2>"));

			HLRResponse hlrResp = new HLRResponse();
			hlrResp.setMsisdn(msisdn);
			if (output != null) {
				hlrResp.setOutputMessage(output);
			}
			if (output2 != null) {
				hlrResp.setOutput2Message(output2);
			}
			return hlrResp;

		} catch (Exception exp) {
			exp.printStackTrace();
		}
		return null;
	}

	public HLRResponse processRequest(String msisdn, int action) {

		HLRResponse hlrResp = null;
		String uri = null;
		if (action == Constants.HLR_SUB)
			uri = config.getSubApiURL();
		else if (action == Constants.HLR_UNSUB)
			uri = config.getUnSubApiURL();

		if (msisdn.startsWith("93"))
			uri = uri.replaceAll("<MSISDN>", msisdn);
		else
			uri = uri.replaceAll("<MSISDN>", "93" + msisdn);

		log.info(uri);
		
		String respStr = httpUtil.submitRequest(uri);
		if (respStr != null) {
			hlrResp = parseResponse(respStr);
		}
		if (hlrResp != null) {
			log.info("HLR Resp=" + hlrResp.getOutputMessage());
		}
		return hlrResp;
	}
}
