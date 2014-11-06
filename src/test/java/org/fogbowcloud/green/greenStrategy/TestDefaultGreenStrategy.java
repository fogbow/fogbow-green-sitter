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
		DefaultGreenStrategy dgs = new DefaultGreenStrategy(osip);
		dgs.sendIdleHostsToBed();
		Assert.assertEquals(1, dgs.getNappingHosts().size());
	}
	
	@Test
	public void oneHostSleeping(){
		Host h1 = new Host ("host1", 0, true, true, 1800000,0,0);
		List <Host> hosts = new LinkedList <Host> ();
		hosts.add(h1);
		Date date = this.createDateMock(3600001);
		OpenStackInfoPlugin osip = this.createOpenStackInfoPluginMock(hosts);
		DefaultGreenStrategy dgs = new DefaultGreenStrategy(osip);
		dgs.sendIdleHostsToBed();
		dgs.setDate(date);
		dgs.sendIdleHostsToBed();
		Assert.assertEquals(1, dgs.getSleepingHosts().size());
		Assert.assertEquals(0, dgs.getNappingHosts().size());
	}
	
	@Test 
	public void noHosts(){
		List <Host> hosts = new LinkedList <Host> ();
		Date date = this.createDateMock(3600001);
		OpenStackInfoPlugin osip = this.createOpenStackInfoPluginMock(hosts);
		DefaultGreenStrategy dgs = new DefaultGreenStrategy(osip);
		dgs.setDate(date);
		dgs.sendIdleHostsToBed();
		Assert.assertEquals(0, dgs.getSleepingHosts().size());
		Assert.assertEquals(0, dgs.getSleepingHosts().size());
	}

	@Test
	public void wakeUp(){
		Host mustWake = new Host ("wake", 0, true, true, 1800000, 3, 8);
		Host stilSleep = new Host ("stil",0,true, true, 1800000, 3, 2);
		Host stilSleep2 = new Host ("stil2",0,true, true, 1800000, 1, 2);
		
		List <Host> hosts = new LinkedList <Host> ();
		hosts.add(mustWake);
		hosts.add(stilSleep);
		hosts.add(stilSleep2);
		
		Date date = this.createDateMock(3600001);
		OpenStackInfoPlugin osip = this.createOpenStackInfoPluginMock(hosts);
		DefaultGreenStrategy dgs = new DefaultGreenStrategy(osip);
		dgs.sendIdleHostsToBed();
		dgs.setDate(date);
		dgs.sendIdleHostsToBed();
		
		dgs.wakeUpSleepingHost(2, 4);
		
		List <Host> expetedResult = new LinkedList <Host> ();
		expetedResult.add(stilSleep);
		expetedResult.add(stilSleep2);
		
		Assert.assertArrayEquals(expetedResult.toArray(), dgs.getSleepingHosts().toArray());
	}
	
	@Test
	public void noHostSleeping(){
		List <Host> hosts = new LinkedList <Host> ();
		OpenStackInfoPlugin osip = this.createOpenStackInfoPluginMock(hosts);
		DefaultGreenStrategy dgs = new DefaultGreenStrategy(osip);
		
		dgs.sendIdleHostsToBed();
		dgs.sendIdleHostsToBed();
		
		Assert.assertEquals(0, dgs.getSleepingHosts().size());
	}
	
	@Test
	public void multipleWakableHosts(){
		Host mustWake = new Host ("wake", 0, true, true, 1800000, 3, 8);
		Host mustWake2 = new Host ("stil",0,true, true, 1800000, 3, 5);
	
		
		List <Host> hosts = new LinkedList <Host> ();
		hosts.add(mustWake);
		hosts.add(mustWake2);
		
		Date date = this.createDateMock(3600001);
		OpenStackInfoPlugin osip = this.createOpenStackInfoPluginMock(hosts);
		DefaultGreenStrategy dgs = new DefaultGreenStrategy(osip);
		dgs.sendIdleHostsToBed();
		dgs.setDate(date);
		dgs.sendIdleHostsToBed();
		
		dgs.wakeUpSleepingHost(2, 4);
		
		Assert.assertEquals(1, dgs.getSleepingHosts().size());
	}
	
}
