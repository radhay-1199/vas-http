package vms.api.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import vms.api.config.Config;
import vms.api.db.SmsTextRepository;
import vms.api.service.SmsUtil;

@Controller
@CrossOrigin
public class SmsController {
	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private Config config;

	@Autowired
	private SmsTextRepository smsTextRepo;

	@Autowired
	private SmsUtil smsUtil;

	@RequestMapping(value = "/sms/notify", method = RequestMethod.GET)
	@ResponseBody
	public String smsNotify(@RequestParam("to") String to, @RequestParam(value = "from", defaultValue = "") String from,
			@RequestParam(value = "msgCode") int msgCode, @RequestParam(value = "langCode") int langCode) {

		log.info("To=" + to + ",LangCode=" + langCode + ",msgCode=" + msgCode);

		to = "93" + to;
		//langCode = 1;
		
		String smsText = smsTextRepo.getMessageText(msgCode, langCode);
		
		if (smsText == null) {
			log.info("sms text not available for msgcode = " + msgCode + " and langCode=" + langCode);
			return "0";
		}

		String url = config.getSmsSubmitUrl();
		if( langCode != 1 ) {
			url = url + "&charset=utf-8&coding=2";
		}

		try {
			smsText = URLEncoder.encode(smsText, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		url = url.replaceAll("<TO>", to);
		url = url.replaceAll("<TEXT>", smsText);

		String resp = smsUtil.callURL(url);
		log.info("Kannel Resp=" + resp);
		resp = resp.toLowerCase();

		if (resp.indexOf("accept") != -1)
			return "1";
		else
			return "0";

	}

}
