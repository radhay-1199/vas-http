package vms.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HLRController {
	Logger logger = LoggerFactory.getLogger(getClass());
	
	@GetMapping("/HLR_Proxy/Sub_CFNRCVMS")
	public ResponseEntity<?> HlrRespSub(@RequestParam String msisdn) {
		return new ResponseEntity("<MSISDN>"+msisdn+"</MSISDN><output>SUCCESS</output>", HttpStatus.OK);
	}
	@GetMapping("/HLR_Proxy/UNSub_CFNRCVMS")
	public ResponseEntity<?> HlrRespUnsub(@RequestParam String msisdn) {
		return new ResponseEntity("<MSISDN>"+msisdn+"</MSISDN><output>SUCCESS</output>", HttpStatus.OK);
	}
}
