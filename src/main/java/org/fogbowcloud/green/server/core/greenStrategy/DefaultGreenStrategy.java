package org.fogbowcloud.green.server.core.greenStrategy;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.fogbowcloud.green.server.communication.GreenSitterCommunicationComponent;
import org.fogbowcloud.green.server.core.plugins.CloudInfoPlugin;
import org.fogbowcloud.green.server.core.plugins.openstack.OpenStackInfoPlugin;

public class DefaultGreenStrategy implements GreenStrategy {

	private CloudInfoPlugin openStackPlugin;
	private List<? extends Host> allHosts;
	private List<Host> nappingHosts = new LinkedList<Host>();
	private List<Host> sleepingHosts = new LinkedList<Host>();
	private GreenSitterCommunicationComponent gscc;

	private Date lastUpdatedTime;

	private long graceTime;
	private long sleepingTime;

	private ScheduledExecutorService executor = Executors
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
		this.graceTime = Long.parseLong(greenProperties.get(
				"greenstrategy.gracetime").toString());
	}

	public DefaultGreenStrategy(CloudInfoPlugin openStackPlugin, long graceTime) {
		this.openStackPlugin = openStackPlugin;
		this.graceTime = graceTime;
	}

	private void setAllHosts() {
		this.allHosts = this.openStackPlugin.getHostInformation();
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

	public void sendIdleHostsToBed() {
		this.setAllHosts();

		for (Host host : this.allHosts) {
			if (host.isNovaEnable() && host.isNovaRunning()
					&& (host.getRunningVM() == 0)) {
				if (!this.getNappingHosts().contains(host)) {
					this.getNappingHosts().add(host);
				} else {
					long nowTime = lastUpdatedTime.getTime();
					if (!this.getSleepingHosts().contains(host)) {
						/*
						 * if there is more than a half hour that the host is
						 * napping than put it in sleeping host list
						 */
						if (nowTime - host.getUpdateTime() > this.graceTime) {
							gscc.sendIdleHostToBed(host.getName());
							this.getSleepingHosts().add(host);
							this.getNappingHosts().remove(host);
						}
					}
				}
			}
		}

	}
	
	public void setCommunicationComponent(GreenSitterCommunicationComponent gscc) {
		this.gscc = gscc;
	}
	
	public void setAgentAddress(String hostName, String jid, String ip, String macAddress) {
		gscc.setAgentAddress(hostName, jid, ip, macAddress);
	}
	
	public void wakeUpSleepingHost(int minCPU, int minRAM) {
		Collections.sort(this.sleepingHosts);
		for (Host host : this.getSleepingHosts()) {
			if (host.getAvailableCPU() >= minCPU) {
				if (host.getAvailableRAM() >= minRAM) {
					gscc.wakeUpHost(host.getName());
					this.sleepingHosts.remove(host);
					return;
				}
			} else {
				return;
			}
		}
	}

	public void start() {
		executor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				sendIdleHostsToBed();
			}
		}, 0, sleepingTime, TimeUnit.MILLISECONDS);
	}
}
