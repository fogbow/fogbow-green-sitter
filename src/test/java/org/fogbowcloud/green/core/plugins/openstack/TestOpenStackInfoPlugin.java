package org.fogbowcloud.green.core.plugins.openstack;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.fogbowcloud.green.server.core.greenStrategy.Host;
import org.fogbowcloud.green.server.core.plugins.openstack.OpenStackInfoPlugin;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.compute.ComputeService;
import org.openstack4j.api.compute.ext.HypervisorService;
import org.openstack4j.api.compute.ext.ZoneService;
import org.openstack4j.model.compute.ext.AvailabilityZones;
import org.openstack4j.model.compute.ext.AvailabilityZones.AvailabilityZone;
import org.openstack4j.model.compute.ext.AvailabilityZones.NovaService;
import org.openstack4j.model.compute.ext.Hypervisor;

public class TestOpenStackInfoPlugin {

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
		AvailabilityZones availabilityZones = Mockito
				.mock(AvailabilityZones.class);
		Mockito.when(compute.zones()).thenReturn(zoneService);
		Mockito.when(zoneService.getAvailabilityZones()).thenReturn(
				availabilityZones);
		Mockito.when(availabilityZones.getAvailabilityZoneList()).thenAnswer(
				new Answer<List<AvailabilityZone>>() {
					@Override
					public List<AvailabilityZone> answer(
							InvocationOnMock invocation) throws Throwable {
						return zones;
					}
				});
		return os;
	}

	@Test
	public void testNoZones() {
		OSClient osClient = createOSClientMock(new LinkedList<Hypervisor>(),
				new LinkedList<AvailabilityZones.AvailabilityZone>());
		OpenStackInfoPlugin plugin = new OpenStackInfoPlugin(
				osClient);
		List<Host> hosts = plugin.getHostInformation();
		Assert.assertTrue(hosts.isEmpty());
	}

	@Test
	public void testEmptyZones() {
		AvailabilityZoneImpl zone = new AvailabilityZoneImpl(
				new AvailabilityZoneImpl.ZoneStateImpl(true),
				new HashMap<String, HashMap<String, ? extends NovaService>>(),
				"Zone");
		LinkedList<AvailabilityZone> zones = new LinkedList<AvailabilityZones.AvailabilityZone>();
		zones.add(zone);

		OSClient osClient = createOSClientMock(new LinkedList<Hypervisor>(),
				zones);
		OpenStackInfoPlugin plugin = new OpenStackInfoPlugin(
				osClient);
		List<Host> hosts = plugin.getHostInformation();
		Assert.assertTrue(hosts.isEmpty());
	}

	@Test
	public void testUnavailableZone() {
		Map<String, HashMap<String, ? extends NovaService>> servicesPerHost = new HashMap<String, HashMap<String, ? extends NovaService>>();
		HashMap<String, NovaService> services = new HashMap<String, NovaService>();
		services.put("nova-compute", new AvailabilityZoneImpl.NovaServiceImpl("true", "true", new Date()));
		servicesPerHost.put("host1", services);
		
		HypervisorTestImpl hp = new HypervisorTestImpl();
		hp.setHostname("host1");
		hp.setRunningVM(1);
		LinkedList<Hypervisor>hpList = new LinkedList<Hypervisor>();
		hpList.add(hp);
		AvailabilityZoneImpl zone = new AvailabilityZoneImpl(
				new AvailabilityZoneImpl.ZoneStateImpl(false), servicesPerHost,
				"Zone");
		LinkedList<AvailabilityZone> zones = new LinkedList<AvailabilityZones.AvailabilityZone>();
		zones.add(zone);
		OSClient osClient = createOSClientMock(hpList, zones);
		OpenStackInfoPlugin plugin = new OpenStackInfoPlugin(
				osClient);
		List<Host> hosts = plugin.getHostInformation();
		Assert.assertTrue(hosts.isEmpty());
	}

	@Test
	public void testUnavailableAndEmptyZone() {
		AvailabilityZoneImpl zone = new AvailabilityZoneImpl(
				new AvailabilityZoneImpl.ZoneStateImpl(false),
				new HashMap<String, HashMap<String, ? extends NovaService>>(),
				"Zone");
		LinkedList<AvailabilityZone> zones = new LinkedList<AvailabilityZones.AvailabilityZone>();
		zones.add(zone);

		OSClient osClient = createOSClientMock(new LinkedList<Hypervisor>(),
				zones);
		OpenStackInfoPlugin plugin = new OpenStackInfoPlugin(
				osClient);
		List<Host> hosts = plugin.getHostInformation();
		Assert.assertTrue(hosts.isEmpty());
	}

	@Test
	public void testOneHost() {
		Map<String, HashMap<String, ? extends NovaService>> servicesPerHost = new HashMap<String, HashMap<String, ? extends NovaService>>();
		HashMap<String, NovaService> services = new HashMap<String, NovaService>();
		services.put("nova-compute", new AvailabilityZoneImpl.NovaServiceImpl("true", "true", new Date()));
		servicesPerHost.put("host1", services);
		
		HypervisorTestImpl hp = new HypervisorTestImpl();
		hp.setHostname("host1");
		hp.setRunningVM(1);
		LinkedList<Hypervisor>hpList = new LinkedList<Hypervisor>();
		hpList.add(hp);
		AvailabilityZoneImpl zone = new AvailabilityZoneImpl(
				new AvailabilityZoneImpl.ZoneStateImpl(true), servicesPerHost,
				"Zone");
		LinkedList<AvailabilityZone> zones = new LinkedList<AvailabilityZones.AvailabilityZone>();
		zones.add(zone);
		OSClient osClient = createOSClientMock(hpList, zones);
		OpenStackInfoPlugin plugin = new OpenStackInfoPlugin(
				osClient);
		List<Host> hosts = plugin.getHostInformation();
		Assert.assertEquals(1, hosts.size());
	}
	
	@Test
	public void testHostWithComputerinDifferentZones(){
		Map<String, HashMap<String, ? extends NovaService>> servicesPerHost = new HashMap<String, HashMap<String, ? extends NovaService>>();
		HashMap<String, NovaService> services = new HashMap<String, NovaService>();
		services.put("nova-compute", new AvailabilityZoneImpl.NovaServiceImpl("true", "true", new Date()));
		servicesPerHost.put("host1", services);
		HypervisorTestImpl hp = new HypervisorTestImpl();
		hp.setHostname("host1");
		hp.setRunningVM(1);
		LinkedList<Hypervisor>hpList = new LinkedList<Hypervisor>();
		hpList.add(hp);
		AvailabilityZoneImpl zone = new AvailabilityZoneImpl(
				new AvailabilityZoneImpl.ZoneStateImpl(true), servicesPerHost,
				"Zone");
		AvailabilityZoneImpl zone2 = new AvailabilityZoneImpl(
				new AvailabilityZoneImpl.ZoneStateImpl(true), servicesPerHost,
				"Zone");
		LinkedList<AvailabilityZone> zones = new LinkedList<AvailabilityZones.AvailabilityZone>();
		zones.add(zone);
		zones.add(zone2);
		OSClient osClient = createOSClientMock(hpList, zones);
		OpenStackInfoPlugin plugin = new OpenStackInfoPlugin(
				osClient);
		List<Host> hosts = plugin.getHostInformation();
		Assert.assertEquals(1, hosts.size());
	}

}
