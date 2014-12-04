package org.fogbowcloud.green.server.core.plugins;

import java.util.List;

import org.fogbowcloud.green.server.core.greenStrategy.Host;

public interface CloudInfoPlugin {
	
	public List<? extends Host> getHostInformation();
	
}
