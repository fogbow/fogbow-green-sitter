package org.fogbowcloud.green.greenStrategy;

import java.util.Date;
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
	
	private Date createDateMock(long Time){
		Date date = Mockito.mock(Date.class);
		Mockito.when(date.getTime()).thenReturn(Time);
		return date;	
	}
	
	@Test 
	public void oneHostNapping(){
		Host napping = new Host ("host1", 0, true, true, 0, 0, 0);
		List <Host> hosts = new LinkedList <Host> ();
		hosts.add(napping);
		OpenStackInfoPlugin osip = this.createOpenStackInfoPluginMock(hosts);
		DefaultGreenStrategy dgs = new DefaultGreenStrategy(osip, null);
		dgs.sendIdleHostsToBed();
		Assert.assertEquals(1, DefaultGreenStrategy.getNappingHosts().size());
	}
	
	@Test
	public void oneHostSleeping(){
		Host h1 = new Host ("host1", 0, true, true, 1800000,0,0);
		List <Host> hosts = new LinkedList <Host> ();
		hosts.add(h1);
		Date date = this.createDateMock(3600001);
		OpenStackInfoPlugin osip = this.createOpenStackInfoPluginMock(hosts);
		DefaultGreenStrategy dgs = new DefaultGreenStrategy(osip, date);
		dgs.sendIdleHostsToBed();
		
		Host h2 = new Host ("host1", 0, true, true,0,0,0);
		List <Host> hosts2 = new LinkedList <Host> ();
		hosts2.add(h2);
		OpenStackInfoPlugin osip2 = this.createOpenStackInfoPluginMock(hosts2);
		DefaultGreenStrategy dgs2 = new DefaultGreenStrategy(osip2, date);
		dgs2.sendIdleHostsToBed();
		Assert.assertEquals(1, DefaultGreenStrategy.getSleepingHosts().size());
	}

}
