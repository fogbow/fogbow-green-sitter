package org.fogbowcloud.green.server.core.greenStrategy;

import java.util.Properties;

public class GreenStrategyThread extends Thread {

	// A half hour, 1800000
	final static long sleepingTime = 1800000;
	private Properties prop;
	
	public GreenStrategyThread(Properties prop){
		this.prop = prop;
	}
	
	public void run() {
		try {
			sleep(sleepingTime);
		} catch (Exception e) {
		}
		DefaultGreenStrategy gs = new DefaultGreenStrategy(prop);
		gs.sendIdleHostsToBed();
	}
}
