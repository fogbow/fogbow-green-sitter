package org.fogbowcloud.green.server.core.plugins.opennebula;

import org.apache.log4j.Logger;
import org.opennebula.client.Client;
import org.opennebula.client.ClientConfigurationException;
import org.opennebula.client.host.HostPool;

public class ClientFactory {
	
	private static final Logger LOGGER = Logger
			.getLogger(ClientFactory.class);
	
	private String userPasswordTuple;
	private String endPoint;
	
	public ClientFactory(String user, String password, String endPoint) {
		this.endPoint = endPoint;
		this.userPasswordTuple = user + ":" + password;
	}
	
	private Client createOneClient() throws ClientConfigurationException {
		return new Client(userPasswordTuple, endPoint);
	}
	
	public HostPool initializeHostPool() {
		try {
			HostPool hostPool = new HostPool(createOneClient());
			return hostPool;
		} catch (ClientConfigurationException e) {
			LOGGER.fatal("Authentication failed", e);
		}
		return null;
	}
}
