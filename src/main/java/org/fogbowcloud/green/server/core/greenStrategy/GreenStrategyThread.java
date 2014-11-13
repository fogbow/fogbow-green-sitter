package org.fogbowcloud.green.server.core.greenStrategy;

import java.util.Properties;

public class GreenStrategyThread extends Thread {

	private Properties prop;
	
	public GreenStrategyThread(Properties prop){
		this.prop = prop;
	}
	
	public void run() {
		DefaultGreenStrategy gs = new DefaultGreenStrategy(prop);
		gs.sendIdleHostsToBed();
	}
}
