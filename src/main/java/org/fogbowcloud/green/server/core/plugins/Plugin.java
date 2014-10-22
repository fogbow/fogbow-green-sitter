package org.fogbowcloud.green.server.core.plugins;

import java.util.List;

import org.fogbowcloud.green.server.core.Host;

public interface Plugin {
	public List<? extends Host> getHostInformation();
}
