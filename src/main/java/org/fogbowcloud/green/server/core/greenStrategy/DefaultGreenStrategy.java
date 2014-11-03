package org.fogbowcloud.green.server.core.greenStrategy;

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
	private List<Host> napingHosts = new LinkedList<Host>();
	private List<Host> sleepingHosts = new LinkedList<Host>();
	private Properties cloudProperties;

	public DefaultGreenStrategy(Properties cloudProperties) {
		this.cloudProperties = cloudProperties;
		openStackPlugin = new OpenStackInfoPlugin(cloudProperties.getProperty(
				"prop.endpoint").toString(), cloudProperties.getProperty(
				"prop.username").toString(), cloudProperties.get(
				"prop.password").toString(), cloudProperties.getProperty(
				"prop.tenant").toString());
	}

	public DefaultGreenStrategy(CloudInfoPlugin openStackPlugin) {
		this.openStackPlugin = openStackPlugin;
	}

	private void setAllHosts() {
		this.allHosts = this.openStackPlugin.getHostInformation();
	}

	public List<Host> getNapingHosts() {
		return napingHosts;
	}

	public List<Host> getSleepingHosts() {
		return sleepingHosts;
	}

	public void SendIdleHostsToBed() {
		this.setAllHosts();
	
		for (Host host : this.allHosts) {
			if (host.isNovaEnable() && host.isNovaRunning()
					&& (host.getRunningVM() == 0)) {
				if (!this.napingHosts.contains(host))
					this.napingHosts.add(host);
			}
		}

		for (Host host : this.napingHosts) {
			Date d = new Date();
			d.getTime();
		}
		
	}

	public void WakeUpSleepingHost(int minCPU, int minRAM) {

	}

}
