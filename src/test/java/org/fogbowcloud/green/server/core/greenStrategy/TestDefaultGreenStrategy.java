package org.fogbowcloud.green.server.core.greenStrategy;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.fogbowcloud.green.server.communication.ServerCommunicationComponent;
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

	private DateWrapper createDateMock(long Time) {
		DateWrapper date = Mockito.mock(DateWrapper.class);
		Mockito.when(date.getTime()).thenReturn(Time);
		return date;
	}

	@Test
	public void testCheckHostLastSeen() {
		Host lost = new Host("lost", 0, true, true, 0, 0, 0);
		lost.setLastSeen(0);
		Host still = new Host("still", 0, true, true, 0, 0, 0);
		List<Host> hosts = new LinkedList<Host>();
		hosts.add(lost);
		hosts.add(still);
		OpenStackInfoPlugin osip = this.createOpenStackInfoPluginMock(hosts);
		DefaultGreenStrategy dgs = new DefaultGreenStrategy(osip, 1800000);
		DateWrapper date = this.createDateMock(0);
		dgs.setDateWrapper(date);
		dgs.receiveIamAliveInfo("lost", "lost@test.com", "123.456.789.10", "A1:B2:C3:D4:E5:67");
		dgs.receiveIamAliveInfo("still", "still@test.com", "123.456.789.10", "A1:B2:C3:D4:E5:67");
		dgs.setExpirationTime(1500000);
		dgs.checkExpiredHosts();
		DateWrapper newDate = this.createDateMock(1500001);
		dgs.setDateWrapper(newDate);
		dgs.receiveIamAliveInfo("still", "still@test.com", "123.456.789.10", "A1:B2:C3:D4:E5:67");
		dgs.checkExpiredHosts();
		Assert.assertEquals(1, dgs.getHostsAwake().size());
	}

	@Test
	public void testRetrievingHosts() {
		Host toBeFound = new Host("found", 0, true, true, 0, 0, 0);
		List<Host> hosts = new LinkedList<Host>();
		hosts.add(toBeFound);
		OpenStackInfoPlugin osip = this.createOpenStackInfoPluginMock(hosts);
		DefaultGreenStrategy dgs = new DefaultGreenStrategy(osip, 1800000);
		DateWrapper date = this.createDateMock(1500001);
		dgs.setDateWrapper(date);
		dgs.receiveIamAliveInfo("found", "test@test.com", "123.456.789.10",
				"A1:B2:C3:D4:E5:67");
		Assert.assertEquals(1, dgs.getHostsAwake().size());
		Assert.assertEquals("test@test.com", dgs.getHostsAwake().get(0)
				.getJid());
	}

	@Test
	public void testNoLoosingData() {
		// It tests if green strategy is loosing data while updating the hosts
		Host host1 = new Host("host1", 0, true, true, 0, 0, 0);
		Host host2 = new Host("host2", 0, true, true, 0, 0, 0);
		List<Host> hosts = new LinkedList<Host>();
		hosts.add(host2);
		hosts.add(host1);
		OpenStackInfoPlugin osip = this.createOpenStackInfoPluginMock(hosts);
		DefaultGreenStrategy dgs = new DefaultGreenStrategy(osip, 1800000);
		Host upHost1 = new Host("host1", 0, true, true, 0, 0, 0);
		Host upHost2 = new Host("host2", 0, true, true, 0, 0, 0);
		upHost1.setJid("test1@test.com");
		upHost2.setJid("test2@test.com");
		List<Host> updatedHosts = new LinkedList<Host>();
		updatedHosts.add(upHost1);
		updatedHosts.add(upHost2);
		dgs.setAllHosts(updatedHosts);
		DateWrapper newDate = this.createDateMock(1500001);
		dgs.setDateWrapper(newDate);
		dgs.updateAllHosts();
		Assert.assertEquals("test2@test.com", dgs.getHostsAwake().get(1)
				.getJid());
		Assert.assertEquals("test1@test.com", dgs.getHostsAwake().get(0)
				.getJid());
	}

	@Test
	public void testOneHostNapping() {
		Host napping = new Host("host1", 0, true, true, 0, 0, 0);
		List<Host> hosts = new LinkedList<Host>();
		hosts.add(napping);
		OpenStackInfoPlugin osip = this.createOpenStackInfoPluginMock(hosts);
		DefaultGreenStrategy dgs = new DefaultGreenStrategy(osip, 1800000);
		DateWrapper date = this.createDateMock(3600001);
		dgs.setDateWrapper(date);
		dgs.sendIdleHostsToBed();
		Assert.assertEquals(1, dgs.getHostsInGracePeriod().size());
	}

	@Test
	public void testOneHostSleeping() {
		Host h1 = new Host("host1", 0, true, true, 1800000, 0, 0);
		h1.setMacAddress("mac");
		List<Host> hosts = new LinkedList<Host>();
		hosts.add(h1);
		DateWrapper date = this.createDateMock(3600001);
		ServerCommunicationComponent gscc = Mockito
				.mock(ServerCommunicationComponent.class);
		try {
			Mockito.doNothing().when(gscc).wakeUpHost(h1.getMacAddress());
		} catch (IOException e) {
			//Ignorating exception because it always do nothing
		}
		OpenStackInfoPlugin osip = this.createOpenStackInfoPluginMock(hosts);
		DefaultGreenStrategy dgs = new DefaultGreenStrategy(osip, 1800000);
		dgs.setDateWrapper(date);
		dgs.setCommunicationComponent(gscc);
		dgs.sendIdleHostsToBed();
		h1.setNappingSince(1800000);
		dgs.sendIdleHostsToBed();
		Assert.assertEquals(1, dgs.getSleepingHosts().size());
		Assert.assertEquals(0, dgs.getHostsInGracePeriod().size());
	}

	@Test
	public void testUpdatingLostHost() {
		Host lost = new Host("lost", 0, true, true, 0, 0, 0);
		lost.setLastSeen(0);
		Host still = new Host("still", 0, true, true, 0, 0, 0);
		List<Host> hosts = new LinkedList<Host>();
		hosts.add(lost);
		hosts.add(still);
		OpenStackInfoPlugin osip = this.createOpenStackInfoPluginMock(hosts);
		DefaultGreenStrategy dgs = new DefaultGreenStrategy(osip, 1800000);
		DateWrapper date = this.createDateMock(1500001);
		dgs.setDateWrapper(date);
		dgs.receiveIamAliveInfo("host", "still@test.com", "123.456.789.10", "A1:B2:C3:D4:E5:67");
		dgs.setExpirationTime(1500000);
		dgs.checkExpiredHosts();
		dgs.updateAllHosts();
		Assert.assertEquals(1, dgs.getHostsAwake().size());
	}

	@Test
	public void testNoHosts() {
		List<Host> hosts = new LinkedList<Host>();
		DateWrapper date = this.createDateMock(3600001);
		OpenStackInfoPlugin osip = this.createOpenStackInfoPluginMock(hosts);
		DefaultGreenStrategy dgs = new DefaultGreenStrategy(osip, 1800000);
		dgs.setDateWrapper(date);
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

		DateWrapper date = this.createDateMock(3600001);
		OpenStackInfoPlugin osip = this.createOpenStackInfoPluginMock(hosts);
		DefaultGreenStrategy dgs = new DefaultGreenStrategy(osip, 1800000);
		dgs.setDateWrapper(date);
		ServerCommunicationComponent gscc = Mockito
				.mock(ServerCommunicationComponent.class);
		try {
			Mockito.doNothing().when(gscc).wakeUpHost("wake");
			Mockito.doNothing().when(gscc).wakeUpHost(mustWake.getMacAddress());
			Mockito.doNothing().when(gscc).wakeUpHost(stilSleep.getMacAddress());
			Mockito.doNothing().when(gscc).wakeUpHost(stilSleep2.getMacAddress());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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

		DateWrapper date = this.createDateMock(3600001);
		ServerCommunicationComponent gscc = Mockito
				.mock(ServerCommunicationComponent.class);
		try {
			Mockito.doNothing().when(gscc).wakeUpHost("wake");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		OpenStackInfoPlugin osip = this.createOpenStackInfoPluginMock(hosts);

		DefaultGreenStrategy dgs = new DefaultGreenStrategy(osip, 1800000);
		dgs.setDateWrapper(date);
		dgs.setCommunicationComponent(gscc);
		dgs.sendIdleHostsToBed();
		mustWake.setNappingSince(1800000);
		mustWake2.setNappingSince(1800000);
		dgs.sendIdleHostsToBed();

		Assert.assertEquals("wake", dgs.getSleepingHosts().peek().getName());

		dgs.wakeUpSleepingHost(2, 4);

		Assert.assertEquals(1, dgs.getSleepingHosts().size());
	}

}
