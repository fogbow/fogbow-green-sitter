package org.fogbowcloud.green.server.core.greenStrategy;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.fogbowcloud.green.server.communication.ServerCommunicationComponent;
import org.fogbowcloud.green.server.core.plugins.CloudInfoPlugin;
import org.fogbowcloud.green.server.core.plugins.openstack.OpenStackInfoPlugin;

public class DefaultGreenStrategy implements GreenStrategy {

	private CloudInfoPlugin openStackPlugin;
	private List<? extends Host> allHosts;
	private List<Host> nappingHosts = new LinkedList<Host>();
	private List<Host> sleepingHosts = new LinkedList<Host>();
	private ServerCommunicationComponent scc;

	private Date lastUpdatedTime;

	private long graceTime;
	private long sleepingTime;
	private long lostHostTime;

	private ScheduledExecutorService executorSendIdleHostsToBed = Executors
			.newScheduledThreadPool(1);
	private ScheduledExecutorService executorVerifyLastTimeSeen = Executors
			.newScheduledThreadPool(1);

	public DefaultGreenStrategy(Properties greenProperties) {
		this.openStackPlugin = new OpenStackInfoPlugin(greenProperties
				.getProperty("openstack.endpoint").toString(), greenProperties
				.getProperty("openstack.username").toString(), greenProperties
				.get("openstack.password").toString(), greenProperties
				.getProperty("openstack.tenant").toString());
		this.lastUpdatedTime = new Date();
		this.sleepingTime = Long.parseLong(greenProperties
				.getProperty("greenstrategy.sleeptime"));
		this.graceTime = Long.parseLong(greenProperties.getProperty(
				"greenstrategy.gracetime"));
		this.lostHostTime = Long.parseLong(
				greenProperties.getProperty("greenstrategy.lostAgentTime"));
	}

	protected DefaultGreenStrategy(CloudInfoPlugin openStackPlugin, long graceTime) {
		this.openStackPlugin = openStackPlugin;
		this.graceTime = graceTime;
	}

	protected void setAllHosts() {
		//must write tests and implement a solution for not loosing host information while updated
		this.allHosts = this.openStackPlugin.getHostInformation();
	}
	
	protected void setLostHostTime(long lostHostTime) {
		this.lostHostTime = lostHostTime;
				
	}
	
	public void setCommunicationComponent(ServerCommunicationComponent gscc) {
		this.scc = gscc;
	}
	
	protected void setDate(Date date) {
		this.lastUpdatedTime = date;
	}

	public List<Host> getNappingHosts() {
		return nappingHosts;
	}

	public List<Host> getSleepingHosts() {
		return sleepingHosts;
	}
	public void receiveIamAliveInfo(String hostName, String jid, String ip, String macAddress) {
		for (Host host : this.allHosts){
			if (host.getName() == hostName){
				host.setJid(jid);
				host.setIp(ip);
				host.setMacAddress(macAddress);
			}
		}
	}

	public void sendIdleHostsToBed() {
		this.setAllHosts();

		for (Host host : this.allHosts) {
			if (host.isNovaEnable() && host.isNovaRunning()
					&& (host.getRunningVM() == 0)) {
				if (!this.getNappingHosts().contains(host)) {
					host.setNappingSince(this.lastUpdatedTime.getTime());
					this.getNappingHosts().add(host);
				} else {
					long nowTime = this.lastUpdatedTime.getTime();
					if (!this.getSleepingHosts().contains(host)) {
						/*
						 * if there is more than a half hour that the host is
						 * napping than put it in sleeping host list
						 */
						if (nowTime - host.getNappingSince() > this.graceTime) {
							scc.sendIdleHostToBed(host.getMacAddress());
							this.getSleepingHosts().add(host);
							this.getNappingHosts().remove(host);
						}
					}
				}
			}
		}

	}
	
	public void checkHostsLastSeen() {
		for (Host host : this.allHosts){
			if (host.getLastSeen() - this.lastUpdatedTime.getTime() >  this.lostHostTime){
				allHosts.remove(host);
				if (this.sleepingHosts.contains(host)){
					this.sleepingHosts.remove(host);
				}
				if (this.nappingHosts.contains(host)){
					this.nappingHosts.remove(host);
				}
			}
		}
	}
	
	public void wakeUpSleepingHost(int minCPU, int minRAM) {
		Collections.sort(this.sleepingHosts);
		for (Host host : this.sleepingHosts) {
			if (host.getAvailableCPU() >= minCPU) {
				if (host.getAvailableRAM() >= minRAM) {
					this.scc.wakeUpHost(host.getName());
					this.sleepingHosts.remove(host);
					return;
				}
			} else {
				return;
			}
		}
	}

	public void start() {
		executorVerifyLastTimeSeen.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				checkHostsLastSeen();
			}
		}, 0, lostHostTime, TimeUnit.MILLISECONDS);
		
		executorSendIdleHostsToBed.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				sendIdleHostsToBed();
			}
		}, 0, sleepingTime, TimeUnit.MILLISECONDS);
	}
}
