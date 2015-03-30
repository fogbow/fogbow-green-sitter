package org.fogbowcloud.green.server.core.plugins.openstack;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.fogbowcloud.green.server.core.greenStrategy.Host;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.compute.ComputeService;
import org.openstack4j.api.compute.ext.HypervisorService;
import org.openstack4j.api.compute.ext.ZoneService;
import org.openstack4j.model.compute.ext.AvailabilityZone;
import org.openstack4j.model.compute.ext.AvailabilityZone.NovaService;
import org.openstack4j.model.compute.ext.Hypervisor;

public class TestOpenStackPlugin {
	private OSClient createOSClientMock(final List<Hypervisor> hvs,
			final List<AvailabilityZone> zones) {
		OSClient os = Mockito.mock(OSClient.class);
		ComputeService compute = Mockito.mock(ComputeService.class);
		HypervisorService hypervisor = Mockito.mock(HypervisorService.class);
		Mockito.when(os.compute()).thenReturn(compute);
		Mockito.when(compute.hypervisors()).thenReturn(hypervisor);
		Mockito.when(hypervisor.list()).thenAnswer(
				new Answer<List<Hypervisor>>() {
					@Override
					public List<Hypervisor> answer(InvocationOnMock invocation)
							throws Throwable {
						return hvs;
					}
				});
		ZoneService zoneService = Mockito.mock(ZoneService.class);
		Mockito.when(compute.zones()).thenReturn(zoneService);
		Mockito.when(zoneService.list(true)).thenAnswer(new Answer<List<AvailabilityZone>>() {
			@Override
			public List<AvailabilityZone> answer(InvocationOnMock invocation)
					throws Throwable {
				return zones;
			}
		});
		return os;
	}
	
	private OpenStackInfoPlugin createMockPlugin(final List<Hypervisor> hvs,
			final List<AvailabilityZone> zones) {
		OSClient osClient = createOSClientMock(hvs, zones);
		OpenStackInfoPlugin plugin = Mockito.spy(new OpenStackInfoPlugin(null, null, null, null));
		Mockito.doReturn(osClient).when(plugin).os();
		return plugin;
	}

	@Test
	public void testNoZones() {
		OpenStackInfoPlugin plugin = createMockPlugin(new LinkedList<Hypervisor>(),
				new LinkedList<AvailabilityZone>());
		List<Host> hosts = plugin.getHostInformation();
		Assert.assertTrue(hosts.isEmpty());
	}

	@Test
	public void testEmptyZones() {
		AvailabilityZoneImpl zone = new AvailabilityZoneImpl(
				new AvailabilityZoneImpl.ZoneStateImpl(true),
				new HashMap<String, Map<String, ? extends NovaService>>(),
				"Zone");
		LinkedList<AvailabilityZone> zones = new LinkedList<AvailabilityZone>();
		zones.add(zone);
		OpenStackInfoPlugin plugin = createMockPlugin(new LinkedList<Hypervisor>(),
				zones);
		List<Host> hosts = plugin.getHostInformation();
		Assert.assertTrue(hosts.isEmpty());
	}

	@Test
	public void testUnavailableZone() {
		Map<String, Map<String, ? extends NovaService>> servicesPerHost = new HashMap<String, Map<String, ? extends NovaService>>();
		HashMap<String, NovaService> services = new HashMap<String, NovaService>();
		services.put("nova-compute", new AvailabilityZoneImpl.NovaServiceImpl(
				true, "true", new Date()));
		servicesPerHost.put("host1", services);
		HypervisorTestImpl hp = new HypervisorTestImpl();
		hp.setHostname("host1");
		hp.setRunningVM(1);
		LinkedList<Hypervisor> hpList = new LinkedList<Hypervisor>();
		hpList.add(hp);
		AvailabilityZoneImpl zone = new AvailabilityZoneImpl(
				new AvailabilityZoneImpl.ZoneStateImpl(false), servicesPerHost,
				"Zone");
		LinkedList<AvailabilityZone> zones = new LinkedList<AvailabilityZone>();
		zones.add(zone);
		OpenStackInfoPlugin plugin = createMockPlugin(hpList, zones);
		List<Host> hosts = plugin.getHostInformation();
		Assert.assertTrue(hosts.isEmpty());
	}

	@Test
	public void testUnavailableAndEmptyZone() {
		AvailabilityZoneImpl zone = new AvailabilityZoneImpl(
				new AvailabilityZoneImpl.ZoneStateImpl(false),
				new HashMap<String, Map<String, ? extends NovaService>>(),
				"Zone");
		LinkedList<AvailabilityZone> zones = new LinkedList<AvailabilityZone>();
		zones.add(zone);
		OpenStackInfoPlugin plugin = createMockPlugin(new LinkedList<Hypervisor>(),
				zones);
		List<Host> hosts = plugin.getHostInformation();
		Assert.assertTrue(hosts.isEmpty());
	}

	@Test
	public void testOneHost() {
		Map<String, Map<String, ? extends NovaService>> servicesPerHost = new HashMap<String, Map<String, ? extends NovaService>>();
		HashMap<String, NovaService> services = new HashMap<String, NovaService>();
		services.put("nova-compute", new AvailabilityZoneImpl.NovaServiceImpl(
				true, "true", new Date()));
		servicesPerHost.put("host1", services);
		HypervisorTestImpl hp = new HypervisorTestImpl();
		hp.setHostname("host1");
		hp.setRunningVM(1);
		LinkedList<Hypervisor> hpList = new LinkedList<Hypervisor>();
		hpList.add(hp);
		AvailabilityZoneImpl zone = new AvailabilityZoneImpl(
				new AvailabilityZoneImpl.ZoneStateImpl(true), servicesPerHost,
				"Zone");
		LinkedList<AvailabilityZone> zones = new LinkedList<AvailabilityZone>();
		zones.add(zone);
		OpenStackInfoPlugin plugin = createMockPlugin(hpList, zones);
		List<Host> hosts = plugin.getHostInformation();
		Assert.assertEquals(1, hosts.size());
	}

	@Test
	public void testHostWithComputerinDifferentZones() {
		Map<String, Map<String, ? extends NovaService>> servicesPerHost = new HashMap<String, Map<String, ? extends NovaService>>();
		HashMap<String, NovaService> services = new HashMap<String, NovaService>();
		services.put("nova-compute", new AvailabilityZoneImpl.NovaServiceImpl(
				true, "true", new Date()));
		servicesPerHost.put("host1", services);
		HypervisorTestImpl hp = new HypervisorTestImpl();
		hp.setHostname("host1");
		hp.setRunningVM(1);
		LinkedList<Hypervisor> hpList = new LinkedList<Hypervisor>();
		hpList.add(hp);
		AvailabilityZoneImpl zone = new AvailabilityZoneImpl(
				new AvailabilityZoneImpl.ZoneStateImpl(true), servicesPerHost,
				"Zone");
		AvailabilityZoneImpl zone2 = new AvailabilityZoneImpl(
				new AvailabilityZoneImpl.ZoneStateImpl(true), servicesPerHost,
				"Zone");
		LinkedList<AvailabilityZone> zones = new LinkedList<AvailabilityZone>();
		zones.add(zone);
		zones.add(zone2);
		OpenStackInfoPlugin plugin = createMockPlugin(hpList, zones);
		List<Host> hosts = plugin.getHostInformation();
		Assert.assertEquals(1, hosts.size());
	}
}