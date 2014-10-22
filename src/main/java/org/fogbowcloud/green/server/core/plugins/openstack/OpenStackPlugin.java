package org.fogbowcloud.green.server.core.plugins.openstack;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.fogbowcloud.green.server.core.Host;
import org.fogbowcloud.green.server.core.plugins.Plugin;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.compute.ext.ZoneService;
import org.openstack4j.openstack.OSFactory;
import org.openstack4j.model.compute.ext.AvailabilityZones.AvailabilityZone;
import org.openstack4j.model.compute.ext.AvailabilityZones.NovaService;
import org.openstack4j.model.compute.ext.Hypervisor;

public class OpenStackPlugin implements Plugin {

	private OSClient os;
	private HashMap<String, Integer> runningVM;
	private LinkedList<String> hostsName;
	private HashMap<String, Boolean> novaEnable;
	private HashMap<String, Boolean> novaRunning;

	public OpenStackPlugin(String endpoint, String username,
			String password, String tenantname) {

		this(OSFactory.builder().endpoint(endpoint)
				.credentials(username, password).tenantName(tenantname)
				.authenticate());
	}

	public OpenStackPlugin(OSClient os) {
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

	private void setRunningVM() {
		List<? extends Hypervisor> hypervisors = os.compute().hypervisors()
				.list();
		this.runningVM = new HashMap<String, Integer>();
		for (Hypervisor hypervisor : hypervisors) {
			this.runningVM.put(hypervisor.getHypervisorHostname(), new Integer(
					hypervisor.getRunningVM()));
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
			for (String host : hostsName) {
				try {
					hostService = availabilityZone.getHosts().get(
							host.toLowerCase());
					ns = hostService.get("nova-compute");
					if ((hostService != null) && (ns != null)) {
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

	private void setNovaRunning(LinkedList<String> hostsName) {
		ZoneService zones = os.compute().zones();
		HashMap<String, ? extends NovaService> hostService;
		NovaService ns;
		this.novaRunning = new HashMap<String, Boolean>();
		List<? extends AvailabilityZone> availabilityZoneList = zones
				.getAvailabilityZones().getAvailabilityZoneList();

		for (AvailabilityZone availabilityZone : availabilityZoneList) {
			for (String host : hostsName) {
				try {
					hostService = availabilityZone.getHosts().get(
							host.toLowerCase());
					ns = hostService.get("nova-compute");
					if ((hostService != null) && (ns != null)) {
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

	public List<Host> getHostInformation() {
		this.setHostsName();
		this.setRunningVM();
		this.setNovaEnable(this.hostsName);
		this.setNovaRunning(this.hostsName);
		List<Host> hosts = new LinkedList<Host>();
		for (String hostName : this.hostsName) {
			try {
				String name = hostName;
				int runningVM = this.runningVM.get(hostName);
				boolean novaRunning = this.novaRunning.get(hostName);
				boolean novaEnable = this.novaEnable.get(hostName);
				Date updateTime = new Date();
				updateTime.getTime();
				Host host = new Host(name, runningVM, novaEnable, novaRunning,
						updateTime);
				hosts.add(host);
			} catch (Exception e) {
			}
		}
		return hosts;
	}


}