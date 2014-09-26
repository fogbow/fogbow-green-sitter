package org.fogbowcloud.green.server.core.plugins.openstack.copy;

import java.util.List;

import org.openstack4j.api.OSClient;
import org.openstack4j.openstack.OSFactory;
import org.openstack4j.model.compute.ext.Hypervisor;

public class OpenStackCommunicationPlugin {

	private OSClient os;

	public OpenStackCommunicationPlugin(String endpoint, String username,
			String password, String tenantname) {

		this(OSFactory.builder().endpoint(endpoint)
				.credentials(username, password).tenantName(tenantname)
				.authenticate());

	}

	public OpenStackCommunicationPlugin(OSClient os) {
		this.os = os;
	}

	public List<? extends Hypervisor> getHostInformation() {
		return os.compute().hypervisors().list();
	}


}