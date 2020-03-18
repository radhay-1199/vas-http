package vms.api.service;

import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import vms.api.bean.ActivationRequest;
import vms.api.bean.HLRResponse;
import vms.api.config.Constants;
import vms.api.db.ActivationRequestRepository;

@Repository
public class RemoveHLRState {

	private Logger log = LogManager.getRootLogger();

	@Autowired
	private ActivationRequestRepository activationRequestRepo;

	@Autowired
	private ProcessHLRRequest hlrRequestProcessor;

	@Scheduled(fixedDelay = 15 * 60 * 1000)
	public void resetHLRState() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR, -6);
		List<ActivationRequest> hlrActiveReq = activationRequestRepo.getHLRActiveRequest(cal.getTime());
		for (ActivationRequest req : hlrActiveReq) {
			HLRResponse hlrResp = hlrRequestProcessor.processRequest(req.getMsisdn(), Constants.HLR_UNSUB);

			if (hlrResp == null) {
				log.info("HLR Response is NUll , Please Check");
				continue;
			}

			if (hlrResp.getOutputMessage().indexOf("SUCCESS") != -1) {
				activationRequestRepo.updateHLRState(req.getMsisdn(), 0);
			}
		}
	}
}
