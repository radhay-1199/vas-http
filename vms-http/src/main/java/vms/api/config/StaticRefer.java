package vms.api.config;

import java.util.concurrent.ConcurrentHashMap;
import vms.api.bean.CallBackData;

public class StaticRefer {
	public static volatile ConcurrentHashMap<String, CallBackData> actRequestMap = new ConcurrentHashMap<String, CallBackData>();
}
