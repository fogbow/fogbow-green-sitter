package org.fogbowcloud.green.greenStrategy;

import java.util.LinkedList;
import java.util.List;

import org.fogbowcloud.green.server.core.Host;
import org.fogbowcloud.green.server.core.greenStrategy.DefaultGreenStrategy;
import org.fogbowcloud.green.server.core.plugins.openstack.OpenStackInfoPlugin;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class TestDefaultGreenStrategy {

	private OpenStackInfoPlugin createOpenStackInfoPluginMock(List<Host> hosts) {
		OpenStackInfoPlugin osip = Mockito.mock(OpenStackInfoPlugin.class);
		Mockito.when(osip.getHostInformation()).thenReturn(hosts);
		return osip;
	}
	
	@Test 
	public void oneHostNapping(){
		Host napping = new Host ("host1", 0, true, true, null, 0, 0);
		List <Host> hosts = new LinkedList <Host> ();
		hosts.add(napping);
		OpenStackInfoPlugin osip = this.createOpenStackInfoPluginMock(hosts);
		DefaultGreenStrategy dgs = new DefaultGreenStrategy(osip);
		dgs.SendIdleHostsToBed();
		Assert.assertEquals(1, dgs.getNapingHosts().size());
	}

}
