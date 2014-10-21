package org.fogbowcloud.green.server.core.plugins.openstack;

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

public class OpenStackCommunicationPlugin implements Plugin {

	private OSClient os;
	private HashMap<String, Integer> runningVM;
	private LinkedList<String> hostsName;
	private HashMap<String, HashMap<String, String>> novaStatus;

	public OpenStackCommunicationPlugin(String endpoint, String username,
			String password, String tenantname) {

		this(OSFactory.builder().endpoint(endpoint)
				.credentials(username, password).tenantName(tenantname)
				.authenticate());
	}

	public OpenStackCommunicationPlugin(OSClient os) {
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

	private void setNovaStatus(LinkedList<String> hostsName) {
		ZoneService zones = os.compute().zones();
		HashMap<String, ? extends NovaService> hostService;
		NovaService ns;
		this.novaStatus = new HashMap<String, HashMap<String, String>>();
		List<? extends AvailabilityZone> availabilityZoneList = zones
				.getAvailabilityZones().getAvailabilityZoneList();

		for (AvailabilityZone availabilityZone : availabilityZoneList) {
			for (String host : hostsName) {
				try {
					hostService = availabilityZone.getHosts().get(host.toLowerCase());
					ns = hostService.get("nova-compute");
					if ((hostService != null) && (ns != null)) {
						HashMap<String, String> service = new HashMap<String, String>();
						service.put(ns.getStatusActive(),ns.getAvailable());
						this.novaStatus.put(host, service);
					}
				} catch (Exception e) {
				}
			}
		}
	}

	public List<? extends Host> getHostInformation() {
		this.setHostsName();
		this.setRunningVM();
		this.setNovaStatus(hostsName);
		System.out.println(hostsName);
		System.out.println(novaStatus);
		return null;
	}

}