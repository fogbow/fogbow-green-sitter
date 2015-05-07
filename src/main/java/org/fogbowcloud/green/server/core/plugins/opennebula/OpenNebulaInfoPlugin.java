package org.fogbowcloud.green.server.core.plugins.opennebula;

import java.util.LinkedList;
import java.util.List;

import org.fogbowcloud.green.server.core.greenStrategy.DateWrapper;
import org.fogbowcloud.green.server.core.greenStrategy.Host;
import org.fogbowcloud.green.server.core.plugins.CloudInfoPlugin;
import org.opennebula.client.host.HostPool;

public class OpenNebulaInfoPlugin implements CloudInfoPlugin {
	
	private ClientFactory clientFactory;
	
	public OpenNebulaInfoPlugin(String user, String password, String endPoint) {
		clientFactory = new ClientFactory(user, password, endPoint);
	}
	
	protected void setClientFactory(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}

	protected Host openNebulaHostToGreenSitterHost(
			org.opennebula.client.host.Host openNebulaHost) {
		openNebulaHost.info();
		String hostName = openNebulaHost.getName();
		int runningVM = Integer.parseInt(openNebulaHost
				.xpath("HOST_SHARE/RUNNING_VMS"));
		boolean computeComponentRunning = true;
		boolean enabled;
		if (openNebulaHost.state() == 1 || openNebulaHost.state() == 2) {
			enabled = true;
		} else {
			enabled = false;
		}
		DateWrapper datew = new DateWrapper();
		int availableCPU = Integer.parseInt(openNebulaHost
				.xpath("HOST_SHARE/FREE_CPU"));
		int availableRAM = Integer.parseInt(openNebulaHost
				.xpath("HOST_SHARE/FREE_MEM"));

		return new Host(hostName, runningVM, computeComponentRunning, enabled,
				datew.getTime(), availableCPU, availableRAM);
	}

	@Override
	public List<? extends Host> getHostInformation() {
		List<Host> greenSitterHosts = new LinkedList<Host>();
		HostPool hostPool = clientFactory.initializeHostPool();
		if (hostPool != null) {
			hostPool.info();
			for (int i = 0; i < hostPool.getLength(); i++) {
				org.opennebula.client.host.Host openNebulaHost = (org.opennebula.client.host.Host) hostPool
						.item(i);
				greenSitterHosts
						.add(openNebulaHostToGreenSitterHost(openNebulaHost));
			}
		}
		return greenSitterHosts;
	}
}