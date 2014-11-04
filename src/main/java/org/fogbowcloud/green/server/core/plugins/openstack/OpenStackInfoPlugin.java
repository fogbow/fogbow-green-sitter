package org.fogbowcloud.green.server.core.plugins.openstack;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.fogbowcloud.green.server.core.Host;
import org.fogbowcloud.green.server.core.plugins.CloudInfoPlugin;
import org.openstack4j.model.compute.ext.AvailabilityZones.ZoneState;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.compute.ext.ZoneService;
import org.openstack4j.openstack.OSFactory;
import org.openstack4j.model.compute.ext.AvailabilityZones.AvailabilityZone;
import org.openstack4j.model.compute.ext.AvailabilityZones.NovaService;
import org.openstack4j.model.compute.ext.Hypervisor;

public class OpenStackInfoPlugin implements CloudInfoPlugin {

	private OSClient os;
	private HashMap<String, Integer> runningVM;
	private LinkedList<String> hostsName;
	private HashMap<String, Boolean> novaEnable;
	private HashMap<String, Boolean> novaRunning;
	private HashMap<String, Integer> availableRam;
	private HashMap<String, Integer> availableCPU;

	public OpenStackInfoPlugin(String endpoint, String username,
			String password, String tenantname) {

		this(OSFactory.builder().endpoint(endpoint)
				.credentials(username, password).tenantName(tenantname)
				.authenticate());
	}

	public OpenStackInfoPlugin(OSClient os) {
		this.os = os;
	}

	private void setHostsName() {
		List<? extends Hypervisor> hypervisors = os.compute().hypervisors()
				.list();
		this.hostsName = new LinkedList<String>();

		for (Hypervisor hypervisor : hypervisors) {
			hostsName.add(hypervisor.getHypervisorHostname());
		}
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

	private void setNovaEnable(LinkedList<String> hostsName) {
		ZoneService zones = os.compute().zones();
		HashMap<String, ? extends NovaService> hostService;
		NovaService ns;
		this.novaEnable = new HashMap<String, Boolean>();
		List<? extends AvailabilityZone> availabilityZoneList = zones
				.getAvailabilityZones().getAvailabilityZoneList();

		for (AvailabilityZone availabilityZone : availabilityZoneList) {
			ZoneState zoneState = availabilityZone.getZoneState();
			if (zoneState.getAvailable()) {
				for (String host : hostsName) {
					try {
						hostService = availabilityZone.getHosts().get(
								host.toLowerCase());
						ns = hostService.get("nova-compute");
						if (ns != null) {
							String s = ns.getAvailable();
							if (s.equals("true"))
								this.novaEnable.put(host, true);
							else
								this.novaEnable.put(host, false);
						}
					} catch (Exception e) {
					}

				}
			}
		}
	}

	private void setNovaRunning(LinkedList<String> hostsName) {
		ZoneService zones = os.compute().zones();
		HashMap<String, ? extends NovaService> hostService;
		NovaService ns;
		this.novaRunning = new HashMap<String, Boolean>();
		List<? extends AvailabilityZone> availabilityZoneList = zones
				.getAvailabilityZones().getAvailabilityZoneList();

		for (AvailabilityZone availabilityZone : availabilityZoneList) {
			ZoneState zoneState = availabilityZone.getZoneState();
			if (zoneState.getAvailable()) {
				for (String host : hostsName) {
					try {
						hostService = availabilityZone.getHosts().get(
								host.toLowerCase());
						ns = hostService.get("nova-compute");
						if (ns != null) {
							String s = ns.getStatusActive();
							if (s.equals("true"))
								this.novaRunning.put(host, true);
							else
								this.novaRunning.put(host, false);
						}
					} catch (Exception e) {
					}

				}
			}
		}
	}

	public List<Host> getHostInformation() {
		this.setHostsName();
		this.setRunningVM();
		this.setAvailableCPU();
		this.setAvailableRam();
		this.setNovaEnable(this.hostsName);
		this.setNovaRunning(this.hostsName);
		List<Host> hosts = new LinkedList<Host>();
		for (String hostName : this.hostsName) {
			try {
				String name = hostName;
				int runningVM = this.runningVM.get(hostName);
				boolean novaRunning = this.novaRunning.get(hostName);
				boolean novaEnable = this.novaEnable.get(hostName);
				long updateTime = new Date().getTime();
				int availableRam = this.availableRam.get(hostName);
				int availableCPU = this.availableCPU.get(hostName);
				Host host = new Host(name, runningVM, novaEnable, novaRunning,
						updateTime, availableRam, availableCPU);
				hosts.add(host);
			} catch (Exception e) {
			}
		}
		return hosts;
	}

}