package org.fogbowcloud.green.server.core.greenStrategy;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.fogbowcloud.green.server.communication.ServerCommunicationComponent;
import org.fogbowcloud.green.server.core.greenStrategy.DefaultGreenStrategy;
import org.fogbowcloud.green.server.core.greenStrategy.Host;
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

	private Date createDateMock(long Time) {
		Date date = Mockito.mock(Date.class);
		Mockito.when(date.getTime()).thenReturn(Time);
		return date;
	}

	@Test
	public void testCheckHostLastSeen() {
	    Host lost = new Host ("lost", 0, true, true, 0, 0, 0);
	    lost.setLastSeen(0);
	    Host still = new Host ("still", 0, true, true, 0, 0, 0);
	    still.setLastSeen(1500000);
		List<Host> hosts = new LinkedList<Host>();
        hosts.add(lost);
        hosts.add(still);
		OpenStackInfoPlugin osip = this.createOpenStackInfoPluginMock(hosts);
		DefaultGreenStrategy dgs = new DefaultGreenStrategy(osip, 1800000);
		Date date = this.createDateMock(1500001);
		dgs.setDate(date);
		dgs.setLostHostTime(1500000);
		dgs.checkHostsLastSeen();
        Assert.assertEquals(1, dgs.getAllWakedHosts().size());
	}

	@Test
	public void testOneHostNapping() {
		Host napping = new Host("host1", 0, true, true, 0, 0, 0);
		List<Host> hosts = new LinkedList<Host>();
		hosts.add(napping);
		OpenStackInfoPlugin osip = this.createOpenStackInfoPluginMock(hosts);
		DefaultGreenStrategy dgs = new DefaultGreenStrategy(osip, 1800000);
		Date date = this.createDateMock(3600001);
		dgs.setDate(date);
		dgs.sendIdleHostsToBed();
		Assert.assertEquals(1, dgs.getNappingHosts().size());
	}

	@Test
	public void testOneHostSleeping() {
		Host h1 = new Host("host1", 0, true, true, 1800000, 0, 0);
		h1.setMacAddress("mac");
		List<Host> hosts = new LinkedList<Host>();
		hosts.add(h1);
		Date date = this.createDateMock(3600001);
		ServerCommunicationComponent gscc = Mockito
				.mock(ServerCommunicationComponent.class);
		Mockito.doNothing().when(gscc).wakeUpHost(h1.getMacAddress());
		OpenStackInfoPlugin osip = this.createOpenStackInfoPluginMock(hosts);
		DefaultGreenStrategy dgs = new DefaultGreenStrategy(osip, 1800000);
		dgs.setDate(date);
		dgs.setCommunicationComponent(gscc);
		dgs.sendIdleHostsToBed();
		h1.setNappingSince(1800000);
		dgs.sendIdleHostsToBed();
		Assert.assertEquals(1, dgs.getSleepingHosts().size());
		Assert.assertEquals(0, dgs.getNappingHosts().size());
	}
	
	@Test
	public void testUpdatingLostHost() {
	    Host lost = new Host ("lost", 0, true, true, 0, 0, 0);
	    lost.setLastSeen(0);
	    Host still = new Host ("still", 0, true, true, 0, 0, 0);
	    still.setLastSeen(1500000);
		List<Host> hosts = new LinkedList<Host>();
        hosts.add(lost);
        hosts.add(still);
		OpenStackInfoPlugin osip = this.createOpenStackInfoPluginMock(hosts);
		DefaultGreenStrategy dgs = new DefaultGreenStrategy(osip, 1800000);
		Date date = this.createDateMock(1500001);
		dgs.setDate(date);
		dgs.setLostHostTime(1500000);
		dgs.checkHostsLastSeen();
		dgs.setAllHosts();
		Assert.assertEquals(1, dgs.getAllWakedHosts().size());
		Assert.assertEquals(1, dgs.getLostHosts().size());
	}

	@Test
	public void testNoHosts() {
		List<Host> hosts = new LinkedList<Host>();
		Date date = this.createDateMock(3600001);
		OpenStackInfoPlugin osip = this.createOpenStackInfoPluginMock(hosts);
		DefaultGreenStrategy dgs = new DefaultGreenStrategy(osip, 1800000);
		dgs.setDate(date);
		dgs.sendIdleHostsToBed();
		Assert.assertEquals(0, dgs.getSleepingHosts().size());
		Assert.assertEquals(0, dgs.getSleepingHosts().size());
	}

	@Test
	public void testWakeUp() {
		Host mustWake = new Host("wake", 0, true, true, 1800000, 3, 8);
		Host stilSleep = new Host("stil", 0, true, true, 1800000, 3, 2);
		Host stilSleep2 = new Host("stil2", 0, true, true, 1800000, 1, 2);

		List<Host> hosts = new LinkedList<Host>();
		hosts.add(mustWake);
		hosts.add(stilSleep);
		hosts.add(stilSleep2);

		Date date = this.createDateMock(3600001);
		OpenStackInfoPlugin osip = this.createOpenStackInfoPluginMock(hosts);
		DefaultGreenStrategy dgs = new DefaultGreenStrategy(osip, 1800000);
		dgs.setDate(date);
		ServerCommunicationComponent gscc = Mockito
				.mock(ServerCommunicationComponent.class);
		Mockito.doNothing().when(gscc).wakeUpHost("wake");
		Mockito.doNothing().when(gscc).wakeUpHost(mustWake.getMacAddress());
		Mockito.doNothing().when(gscc).wakeUpHost(stilSleep.getMacAddress());
		Mockito.doNothing().when(gscc).wakeUpHost(stilSleep2.getMacAddress());

		dgs.setCommunicationComponent(gscc);
		dgs.sendIdleHostsToBed();
		mustWake.setNappingSince(1800000);
		stilSleep.setNappingSince(1800000);
		stilSleep2.setNappingSince(1800000);
		dgs.sendIdleHostsToBed();

		dgs.wakeUpSleepingHost(2, 4);

		List<Host> expetedResult = new LinkedList<Host>();
		expetedResult.add(stilSleep);
		expetedResult.add(stilSleep2);

		Assert.assertArrayEquals(expetedResult.toArray(), dgs
				.getSleepingHosts().toArray());
	}

	@Test
	public void testNoHostSleeping() {
		List<Host> hosts = new LinkedList<Host>();
		OpenStackInfoPlugin osip = this.createOpenStackInfoPluginMock(hosts);
		DefaultGreenStrategy dgs = new DefaultGreenStrategy(osip, 1800000);
		dgs.sendIdleHostsToBed();
		dgs.sendIdleHostsToBed();

		Assert.assertEquals(0, dgs.getSleepingHosts().size());
	}

	@Test
	public void testMultipleWakableHosts() {
		Host mustWake = new Host("wake", 0, true, true, 1800000, 3, 8);
		Host mustWake2 = new Host("stil", 0, true, true, 1800000, 3, 5);

		List<Host> hosts = new LinkedList<Host>();
		hosts.add(mustWake);
		hosts.add(mustWake2);

		Date date = this.createDateMock(3600001);
		ServerCommunicationComponent gscc = Mockito
				.mock(ServerCommunicationComponent.class);
		Mockito.doNothing().when(gscc).wakeUpHost("wake");
		OpenStackInfoPlugin osip = this.createOpenStackInfoPluginMock(hosts);

		DefaultGreenStrategy dgs = new DefaultGreenStrategy(osip, 1800000);
		dgs.setDate(date);
		dgs.setCommunicationComponent(gscc);
		dgs.sendIdleHostsToBed();
		mustWake.setNappingSince(1800000);
		mustWake2.setNappingSince(1800000);
		dgs.sendIdleHostsToBed();
		dgs.wakeUpSleepingHost(2, 4);

		Assert.assertEquals(1, dgs.getSleepingHosts().size());
	}

}
