package org.fogbowcloud.green.core.plugins.openstack;

import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.compute.ComputeService;
import org.openstack4j.api.compute.ext.HypervisorService;
import org.openstack4j.model.compute.ext.Hypervisor;

public class TestOpenStackCommunicationPlugin {

	@Test
	public void testgetHostInformation() {
		final List<Hypervisor> hvs = new LinkedList<Hypervisor>();
		HypervisorTestImpl h = new HypervisorTestImpl();
		h.setHostname("hostname1");
		hvs.add(h);

		OSClient os = createComputeMock(hvs);

		OpenStackCommunicationPlugin oscp = new OpenStackCommunicationPlugin(os);
		List<? extends Hypervisor> hostInfo = oscp.getHostInformation();
		Assert.assertEquals(1, hostInfo.size());
		Assert.assertEquals("hostname1", hostInfo.get(0)
				.getHypervisorHostname());
	}

	private OSClient createComputeMock(final List<Hypervisor> hvs) {
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
		return os;
	}
}
