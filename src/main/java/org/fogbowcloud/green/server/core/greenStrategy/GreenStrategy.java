package org.fogbowcloud.green.server.core.greenStrategy;

public interface GreenStrategy  {
	
	public void sendIdleHostsToBed();
	
	public void wakeUpSleepingHost(int minCPU, int minRAM);
	
	public void setAgentAddress(String JID, String IP);
	
}
