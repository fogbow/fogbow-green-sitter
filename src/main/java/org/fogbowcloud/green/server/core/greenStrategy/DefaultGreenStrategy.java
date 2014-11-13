package org.fogbowcloud.green.server.core.greenStrategy;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.fogbowcloud.green.server.core.Host;
import org.fogbowcloud.green.server.core.plugins.CloudInfoPlugin;
import org.fogbowcloud.green.server.core.plugins.openstack.OpenStackInfoPlugin;

public class DefaultGreenStrategy implements GreenStrategy {

	private CloudInfoPlugin openStackPlugin;
	private List<? extends Host> allHosts;
	private List<Host> nappingHosts = new LinkedList<Host>();
	private List<Host> sleepingHosts = new LinkedList<Host>();
	private Date date;
	private long graceTime;

	public DefaultGreenStrategy(Properties greenProperties) {
		this.openStackPlugin = new OpenStackInfoPlugin(greenProperties
				.getProperty("openstackprop.endpoint").toString(), greenProperties
				.getProperty("openstackprop.username").toString(), greenProperties.get(
				"openstackprop.password").toString(), greenProperties.getProperty(
				"openstackprop.tenant").toString());
		this.date = new Date();
		this.graceTime = Long.parseLong(greenProperties.get("greenstrategyprop.gracetime").toString());
		System.out.println(graceTime);
	}

	public DefaultGreenStrategy(CloudInfoPlugin openStackPlugin, long graceTime) {
		this.openStackPlugin = openStackPlugin;
		this.graceTime = graceTime;
	}

	private void setAllHosts() {
		this.allHosts = this.openStackPlugin.getHostInformation();
	}
	
	public void setDate (Date date){
		this.date = date;
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
					long nowTime = date.getTime();
					if (!this.getSleepingHosts().contains(host)) {
						/*
						 * if there is more than a half hour that the host is
						 * napping than put it in sleeping host list
						 */
						if (nowTime - host.getUpdateTime() > this.graceTime) {
							// terá comando estilo
							// "xmpp, mande esse host dormir"
							this.getSleepingHosts().add(host);
							this.getNappingHosts().remove(host);
						}
					}
				}
			}
		}

	}

	public void wakeUpSleepingHost(int minCPU, int minRAM) {
		Collections.sort(this.sleepingHosts);
		for (Host host : this.getSleepingHosts()) {
			if (host.getAvailableCPU() >= minCPU) {
				if (host.getAvailableRAM() >= minRAM) {
					// terá comando como xmpp, acorde esse host
					this.sleepingHosts.remove(host);
					return;
				}
			} else {
				return;
			}
		}
	}

}
