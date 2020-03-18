package vms.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import vms.api.config.Config;
import vms.api.util.Utility;

@Controller
@CrossOrigin

public class UserController {
	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private Utility util;

	@Autowired
	private Config config;

	@RequestMapping(value = "/vms/mo", method = RequestMethod.GET)
	public ResponseEntity<?> vmsMoRequest(@RequestParam("from") String from, @RequestParam("to") String to,
			@RequestParam("text") String text) {
		log.info("From=" + from + ",to=" + to + ",msg=" + text);

		// from = from.substring(from.length()-10 );
		if (from.startsWith("+93"))
			from = from.replaceFirst("\\+93", "");

		if (config.getValidNumber() == null || config.getValidNumber().equals("-1")
				|| (from.equals(config.getValidNumber()))) {

			text = text.toUpperCase();
			String data = null;
			
			

			if (text.indexOf("UN") != -1) {
				data = "12345#12345#UNSUB#" + from + "#UN#";
			} else {
				text = text.replaceAll("SUB", "");
				text = text.replaceAll(" ", "");
				data = "12345#12345#SUB#" + from + "#" + text + "#";
			}
			util.sendOverUdp(data, config.getFwIp(), config.getFwPort());
		}
		return new ResponseEntity<>(HttpStatus.OK);
		
	}
	

}
