package org.fogbowcloud.green.server.core.greenStrategy;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.fogbowcloud.green.server.core.Host;
import org.fogbowcloud.green.server.core.plugins.CloudInfoPlugin;
import org.fogbowcloud.green.server.core.plugins.openstack.OpenStackInfoPlugin;

;

public class DefaultGreenStrategy implements GreenStrategy {

	CloudInfoPlugin openStackPlugin;
	List<? extends Host> allHosts;
	List<Host> napingHosts = new LinkedList<Host>();
	List<Host> sleepingHosts = new LinkedList<Host>();

	Properties cloudProperties;

	public DefaultGreenStrategy(Properties cloudProperties) {
		this.cloudProperties = cloudProperties;
	}

	private void setAllHosts() {
		this.allHosts = this.openStackPlugin.getHostInformation();
	}

	private void setOpenStackPlugin() {
		openStackPlugin = new OpenStackInfoPlugin(cloudProperties.getProperty(
				"prop.endpoint").toString(), cloudProperties.getProperty(
				"prop.username").toString(), cloudProperties.get(
				"prop.password").toString(), cloudProperties.getProperty(
				"prop.tenant").toString());
	}

	public void setOpenStackPlugin(CloudInfoPlugin openStackPlugin) {
		this.openStackPlugin = openStackPlugin;
	}

	public void SendIdleHostsToBed() {
		this.setOpenStackPlugin();
		this.setAllHosts();

		for (Host host : this.allHosts) {
			if (host.isNovaEnable() && host.isNovaRunning()
					&& (host.getRunningVM() == 0)) {
				if (!this.napingHosts.contains(host))
					this.napingHosts.add(host);
			}
		}

		for (Host host : this.napingHosts) {

		}

	}

	public void WakeUpSleepingHost(int minCPU, int minRAM) {

	}
	


}
