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

	public DefaultGreenStrategy(Properties cloudProperties) {
		this.openStackPlugin = new OpenStackInfoPlugin(cloudProperties
				.getProperty("prop.endpoint").toString(), cloudProperties
				.getProperty("prop.username").toString(), cloudProperties.get(
				"prop.password").toString(), cloudProperties.getProperty(
				"prop.tenant").toString());
		this.date = new Date();
	}

	public DefaultGreenStrategy(CloudInfoPlugin openStackPlugin) {
		this.openStackPlugin = openStackPlugin;
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
						if (nowTime - host.getUpdateTime() > 1800000) {
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
