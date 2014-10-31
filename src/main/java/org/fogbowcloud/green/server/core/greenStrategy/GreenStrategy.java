package org.fogbowcloud.green.server.core.greenStrategy;

public interface GreenStrategy {
	public void SendIdleHostsToBed();
	public void WakeUpSleepingHost(int minCPU, int minRAM);
}
