package org.fogbowcloud.green.server.core.plugins.openstack;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.fogbowcloud.green.server.core.greenStrategy.Host;
import org.fogbowcloud.green.server.core.plugins.CloudInfoPlugin;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.compute.ext.ZoneService;
import org.openstack4j.model.compute.ext.AvailabilityZones.AvailabilityZone;
import org.openstack4j.model.compute.ext.AvailabilityZones.NovaService;
import org.openstack4j.model.compute.ext.AvailabilityZones.ZoneState;
import org.openstack4j.model.compute.ext.Hypervisor;
import org.openstack4j.openstack.OSFactory;

public class OpenStackInfoPlugin implements CloudInfoPlugin {

	private OSClient os;
	private HashMap<String, Integer> runningVM;
	private HashMap<String, Integer> availableRam;
	private HashMap<String, Integer> availableCPU;

	public OpenStackInfoPlugin(String endpoint, String username,
			String password, String tenantname) {

		this(OSFactory.builder().endpoint(endpoint)
				.credentials(username, password).tenantName(tenantname)
				.authenticate());
	}

	protected OpenStackInfoPlugin(OSClient os) {
		this.os = os;
	}

	private List<String> getHostsName() {
		List<? extends Hypervisor> hypervisors = os.compute().hypervisors()
				.list();
		List<String> hostsName = new LinkedList<String>();
		for (Hypervisor hypervisor : hypervisors) {
			hostsName.add(hypervisor.getHypervisorHostname());
		}
		return hostsName;
	}

	private void setAvailableRam() {
		List<? extends Hypervisor> hypervisors = os.compute().hypervisors()
				.list();
		this.availableRam = new HashMap<String, Integer>();
		for (Hypervisor hypervisor : hypervisors) {
			this.availableRam.put(hypervisor.getHypervisorHostname(),
					Integer.valueOf(hypervisor.getFreeRam()));
		}
	}

	private void setAvailableCPU() {
		List<? extends Hypervisor> hypervisors = os.compute().hypervisors()
				.list();
		this.availableCPU = new HashMap<String, Integer>();
		for (Hypervisor hypervisor : hypervisors) {
			this.availableCPU.put(hypervisor.getHypervisorHostname(),
					Integer.valueOf(hypervisor.getFreeDisk()));
		}
	}

	private void setRunningVM() {
		List<? extends Hypervisor> hypervisors = os.compute().hypervisors()
				.list();
		this.runningVM = new HashMap<String, Integer>();
		for (Hypervisor hypervisor : hypervisors) {
			this.runningVM.put(hypervisor.getHypervisorHostname(),
					Integer.valueOf(hypervisor.getRunningVM()));

		}
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
		ZoneService zones = os.compute().zones();
		HashMap<String, NovaHost> novaRunning = new HashMap<String, NovaHost>();
		List<? extends AvailabilityZone> availabilityZoneList = zones
				.getAvailabilityZones().getAvailabilityZoneList();

		for (AvailabilityZone availabilityZone : availabilityZoneList) {
			ZoneState zoneState = availabilityZone.getZoneState();
			if (zoneState.getAvailable()) {
				for (String host : hostsName) {
					try {
						HashMap<String, ? extends NovaService> hostService = availabilityZone.getHosts().get(
								host.toLowerCase());
						NovaService ns = hostService.get("nova-compute");
						if (ns != null) {
							String active = ns.getStatusActive();
							String available = ns.getAvailable();
							novaRunning.put(host, new NovaHost(
									available.equals("true"), active.equals("true")));
						}
					} catch (Exception e) {
					}

				}
			}
		}
		return novaRunning;
	}
	
	public List<Host> getHostInformation() {
		List<String> hostsName = this.getHostsName();
		this.setRunningVM();
		this.setAvailableCPU();
		this.setAvailableRam();
		
		HashMap<String, NovaHost> novaState = getNovaState(hostsName);
		
		List<Host> hosts = new LinkedList<Host>();
		for (String hostName : hostsName) {
			try {
				String name = hostName;
				int runningVM = this.runningVM.get(hostName);
				boolean novaRunning = novaState.get(hostName).running;
				boolean novaEnable = novaState.get(hostName).enabled;
				long updateTime = new Date().getTime();
				int availableRam = this.availableRam.get(hostName);
				int availableCPU = this.availableCPU.get(hostName);
				Host host = new Host(name, runningVM, novaEnable, novaRunning,
						updateTime, availableRam, availableCPU);
				hosts.add(host);
			} catch (Exception e) {
				// Ignoring exceptions for hosts in unavailable zones
			}
		}
		return hosts;
	}

}