package vms.api.controller;

import java.net.DatagramPacket;

import java.net.DatagramSocket;
import java.net.InetAddress;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import vms.api.bean.VmsUser;
import vms.api.db.VmsUserRepository;

@Controller
@CrossOrigin

public class SubController {

	private Logger log = LogManager.getRootLogger();

	@Autowired
	private VmsUserRepository vmsUserRepo;

	@RequestMapping(value = "/vms/sub", method = RequestMethod.GET)
	@ResponseBody
	public String vmsSubRequest(@RequestParam("msisdn") String msisdn, @RequestParam("pack") String packId,
			@RequestParam("lang") String lang) {
		
		log.info("Sub Request|msisdn=" + msisdn + ",pack=" + packId + ",lang=" + lang);
		String balanceVal = "-1";
		if (msisdn.startsWith("+93"))
			msisdn = msisdn.replaceFirst("\\+93", "");

		if (packId.equalsIgnoreCase("1"))
			packId = "P3";

		if (packId.equalsIgnoreCase("2"))
			packId = "P2";

		String reqStr = "SUB#" + msisdn + "#" + packId + "#" + lang + "#127.0.0.1#";
		VmsUser user = vmsUserRepo.getUserDetails(msisdn);
		if (user != null) {
			log.info("Already Active Subscriber msisdn=" + msisdn);
			return "5";
		}

		String resp = sendAndReceiveUdp(reqStr, "127.0.0.1", 9876);
		if (resp.indexOf("#") != -1) {
			String info[] = resp.split("#");
			log.info("Resp| Lang=" + lang + ",Msisdn=" + msisdn + ",Status=" + info[1]);
			return info[1];
		}
		return balanceVal;
	}

	/*
	 * @RequestMapping(value = "/vms/unsub", method = RequestMethod.GET)
	 * 
	 * @ResponseBody public String vmsUnSubRequest(@RequestParam("msisdn") String
	 * msisdn) { log.info("UnSub Request|msisdn=" + msisdn);
	 * 
	 * if (msisdn.startsWith("+93")) msisdn = msisdn.replaceFirst("\\+93", "");
	 * 
	 * String reqStr = "UNSUB#" + msisdn + "#127.0.0.1#"; VmsUser user =
	 * vmsUserRepo.getUserDetails(msisdn);
	 * 
	 * if (user == null) { log.info("Already Non-ctive Subscriber msisdn=" +
	 * msisdn); return "6"; }
	 * 
	 * String resp = sendAndReceiveUdp(reqStr, "127.0.0.1", 9876); if
	 * (resp.indexOf("#") != -1) { String info[] = resp.split("#");
	 * log.info("Resp|UnSub|Msisdn=" + msisdn + ",Status=" + info[1]); return
	 * info[1]; } return "-1"; }
	 */

	public String sendAndReceiveUdp(String data, String ip, int port) {

		try {
			DatagramSocket clientSocket = new DatagramSocket();
			data = data + clientSocket.getLocalPort();
			InetAddress IPAddress = InetAddress.getByName(ip);
			DatagramPacket sendPacket = new DatagramPacket(data.getBytes(), data.length(), IPAddress, port);
			clientSocket.send(sendPacket);
			log.info(data + " " + ip + ":" + port);

			clientSocket.setSoTimeout(30000);
			byte[] buffer = new byte[256];
			DatagramPacket recivePacket = new DatagramPacket(buffer, buffer.length);
			clientSocket.receive(recivePacket);
			String msg = new String(buffer, 0, recivePacket.getLength());
			log.info("Udp Recv=" + msg);
			clientSocket.close();
			return msg;

		} catch (Exception e) {
			log.info(e.getMessage());
		}
		return "" + 5;
	}
}
