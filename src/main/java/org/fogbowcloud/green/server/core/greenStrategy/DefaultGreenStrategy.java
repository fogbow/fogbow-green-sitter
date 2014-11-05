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
	private static List<Host> nappingHosts = new LinkedList<Host>();
	private static List<Host> sleepingHosts = new LinkedList<Host>();
	private Date date;

	public DefaultGreenStrategy(Properties cloudProperties) {
		this.openStackPlugin = new OpenStackInfoPlugin(cloudProperties
				.getProperty("prop.endpoint").toString(), cloudProperties
				.getProperty("prop.username").toString(), cloudProperties.get(
				"prop.password").toString(), cloudProperties.getProperty(
				"prop.tenant").toString());
		this.date = new Date();
	}

	public DefaultGreenStrategy(CloudInfoPlugin openStackPlugin, Date date) {
		this.openStackPlugin = openStackPlugin;
		this.date = date;
	}

	private void setAllHosts() {
		this.allHosts = this.openStackPlugin.getHostInformation();
	}

	public static List<Host> getNappingHosts() {
		return nappingHosts;
	}

	public static List<Host> getSleepingHosts() {
		return sleepingHosts;
	}

	public void sendIdleHostsToBed() {
		this.setAllHosts();

		for (Host host : this.allHosts) {
			if (host.isNovaEnable() && host.isNovaRunning()
					&& (host.getRunningVM() == 0)) {
				if (!DefaultGreenStrategy.getNappingHosts().contains(host)) {
					DefaultGreenStrategy.getNappingHosts().add(host);
				} else {
					long nowTime = date.getTime();
					if (!DefaultGreenStrategy.getSleepingHosts().contains(host)) {
						/*
						 * if there is more than a half hour that the host is
						 * napping than put it in sleeping host list
						 */
						if (nowTime - host.getUpdateTime() > 1800000) {
							// terá comando estilo
							// "xmpp, mande esse host dormir"
							DefaultGreenStrategy.getSleepingHosts().add(host);
							DefaultGreenStrategy.getNappingHosts().remove(host);
						}
					}
				}
			}
		}

	}

	public void wakeUpSleepingHost(int minCPU, int minRAM) {
		Collections.sort(DefaultGreenStrategy.sleepingHosts);
		for (Host host : DefaultGreenStrategy.getSleepingHosts()) {
			if (host.getAvailableCPU() >= minCPU) {
				if (host.getAvailableRAM() > minRAM) {
					// terá comando como xmpp, acorde esse host
					sleepingHosts.remove(host);
				}
			} else {
				return;
			}
		}
	}

}
