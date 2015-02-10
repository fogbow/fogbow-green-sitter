package org.fogbowcloud.green.server.core.greenStrategy;

import org.fogbowcloud.green.server.communication.GreenSitterCommunicationComponent;

public interface GreenStrategy  {
	
	public void sendIdleHostsToBed();
	
	public void wakeUpSleepingHost(int minCPU, int minRAM);
	
	public void setAgentAddress(String Name, String JID, String IP, String macAddress);
	
	public void setCommunicationComponent(GreenSitterCommunicationComponent gscc);
	
}
