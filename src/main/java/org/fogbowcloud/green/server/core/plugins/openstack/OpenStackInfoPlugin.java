package org.fogbowcloud.green.server.core.plugins.openstack;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.fogbowcloud.green.server.core.greenStrategy.Host;
import org.fogbowcloud.green.server.core.plugins.CloudInfoPlugin;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.compute.ext.ZoneService;
import org.openstack4j.model.compute.ext.AvailabilityZone;
import org.openstack4j.model.compute.ext.AvailabilityZone.NovaService;
import org.openstack4j.model.compute.ext.AvailabilityZone.ZoneState;
import org.openstack4j.model.compute.ext.Hypervisor;
import org.openstack4j.openstack.OSFactory;

public class OpenStackInfoPlugin implements CloudInfoPlugin {

	private String endpoint;
	private String username;
	private String password;
	private String tenantname;

	public OpenStackInfoPlugin(String endpoint, String username,
			String password, String tenantname) {
		this.endpoint = endpoint;
		this.username = username;
		this.password = password;
		this.tenantname = tenantname;
	}

	protected OSClient os() {
		return OSFactory.builder().endpoint(endpoint)
				.credentials(username, password).tenantName(tenantname)
				.authenticate();
	}
	
	public List<String> getHostsName() {
		List<? extends Hypervisor> hypervisors = os().compute().hypervisors()
				.list();
		List<String> hostsName = new LinkedList<String>();
		for (Hypervisor hypervisor : hypervisors) {
			hostsName.add(hypervisor.getHypervisorHostname());
		}
		return hostsName;
	}

	private HashMap<String, Integer> getAvailableRam() {
		List<? extends Hypervisor> hypervisors = os().compute().hypervisors()
				.list();
		HashMap<String, Integer> availableRam = new HashMap<String, Integer>();
		for (Hypervisor hypervisor : hypervisors) {
			availableRam.put(hypervisor.getHypervisorHostname(),
					Integer.valueOf(hypervisor.getFreeRam()));
		}
		return availableRam;
	}

	private HashMap<String, Integer> getAvailableCPU() {
		List<? extends Hypervisor> hypervisors = os().compute().hypervisors()
				.list();
		HashMap<String, Integer> availableCPU = new HashMap<String, Integer>();
		for (Hypervisor hypervisor : hypervisors) {
			availableCPU.put(hypervisor.getHypervisorHostname(),
					Integer.valueOf(hypervisor.getFreeDisk()));
		}
		return availableCPU;
	}

	private HashMap<String, Integer> getRunningVM() {
		List<? extends Hypervisor> hypervisors = os().compute().hypervisors()
				.list();
		HashMap<String, Integer> runningVM = new HashMap<String, Integer>();
		for (Hypervisor hypervisor : hypervisors) {
			runningVM.put(hypervisor.getHypervisorHostname(),
					Integer.valueOf(hypervisor.getRunningVM()));

		}
		return runningVM;
	}

	private static class NovaHost {
		boolean enabled;
		boolean running;

		public NovaHost(boolean enabled, boolean running) {
			this.enabled = enabled;
			this.running = running;
		}
	}

	private HashMap<String, NovaHost> getNovaState(List<String> hostsName) {
		ZoneService zones = os().compute().zones();
		HashMap<String, NovaHost> novaRunning = new HashMap<String, NovaHost>();
		List<? extends AvailabilityZone> availabilityZoneList = zones
				.list(true);

		for (AvailabilityZone availabilityZone : availabilityZoneList) {
			ZoneState zoneState = availabilityZone.getZoneState();
			if (zoneState.getAvailable()) {
				for (String host : hostsName) {
					try {
						Map<String, Map<String, ? extends NovaService>> hostsInAvailabilityZone = availabilityZone.getHosts();
						Map<String, ? extends NovaService> hostService = hostsInAvailabilityZone.get(host.toLowerCase());
						NovaService ns = hostService.get("nova-compute");
						if (ns != null) {
							String active = ns.getStatusActive();
							boolean available = ns.getAvailable();
							novaRunning.put(host,
									new NovaHost(available, active.equals("true")));
						}
					} catch (Exception e) {
						// Ignoring exceptions for hosts in unavailable zones
					}

				}
			}
		}
		return novaRunning;
	}

	public List<Host> getHostInformation() {
		List<String> hostsName = this.getHostsName();
		HashMap<String, Integer> runningVM = this.getRunningVM();
		HashMap<String, Integer> availableRam = this.getAvailableRam();
		HashMap<String, Integer> availableCPU = this.getAvailableCPU();

		HashMap<String, NovaHost> novaState = getNovaState(hostsName);

		List<Host> hosts = new LinkedList<Host>();
		for (String hostName : hostsName) {
			try {
				String name = hostName;
				int runningVMInTheHost = runningVM.get(hostName);
				boolean novaRunning = novaState.get(hostName).running;
				boolean novaEnable = novaState.get(hostName).enabled;
				long updateTime = new Date().getTime();
				int availableRamInTheHost = availableRam.get(hostName);
				int availableCPUInTheHost = availableCPU.get(hostName);
				Host host = new Host(name, runningVMInTheHost, novaEnable,
						novaRunning, updateTime, availableCPUInTheHost, 
						availableRamInTheHost);
				hosts.add(host);
			} catch (Exception e) {
				// Ignoring exceptions for hosts in unavailable zones
			}
		}
		return hosts;
	}

}