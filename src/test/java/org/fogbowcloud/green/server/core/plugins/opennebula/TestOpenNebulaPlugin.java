package org.fogbowcloud.green.server.core.plugins.opennebula;

import static org.junit.Assert.*;

import org.junit.Test;
import org.mockito.Mockito;
import org.opennebula.client.OneResponse;
import org.opennebula.client.host.Host;
import org.opennebula.client.host.HostPool;

public class TestOpenNebulaPlugin {

	private Host createONHostMock(String name, String runningVM, int state,
			String freeCPU, String freeRAM) {
		Host host = Mockito.mock(Host.class);
		Mockito.when(host.getName()).thenReturn(name);
		Mockito.when(host.xpath("HOST_SHARE/RUNNING_VMS"))
				.thenReturn(runningVM);
		Mockito.when(host.state()).thenReturn(state);
		Mockito.when(host.xpath("HOST_SHARE/FREE_CPU")).thenReturn(freeCPU);
		Mockito.when(host.xpath("HOST_SHARE/FREE_MEM")).thenReturn(freeRAM);
		return host;
	}

	private HostPool createHostPoolMock(int lengthHostPool) {
		HostPool hostPool = Mockito.mock(HostPool.class);
		OneResponse response = new OneResponse(true, "");
		Mockito.when(hostPool.info()).thenReturn(response);
		Mockito.when(hostPool.getLength()).thenReturn(lengthHostPool);
		
		for (int i = 0; i < lengthHostPool; i ++) {
			Host hostON = createONHostMock("host" + i, "1", 1, "2", "1024");
			Mockito.when(hostPool.item(i)).thenReturn(hostON);
		}
		return hostPool;
	}
	
	private ClientFactory createClientFactoryMock(int lengthHostPool) {
		ClientFactory cf = Mockito.mock(ClientFactory.class);
		HostPool hp = createHostPoolMock(lengthHostPool); 
		Mockito.when(cf.initializeHostPool()).thenReturn(hp);
		return cf;
	} 

	@Test
	public void testNoHosts() {
		OpenNebulaInfoPlugin onip = new OpenNebulaInfoPlugin("user",
				"password", "localhost/");
		assertEquals(0, onip.getHostInformation().size());
	}

	@Test
	public void testConvertingHost() {
		OpenNebulaInfoPlugin onip = new OpenNebulaInfoPlugin("user",
				"password", "localhost/");
		Host hostON = createONHostMock("host1", "1", 1, "2", "1024");
		org.fogbowcloud.green.server.core.greenStrategy.Host hostGS = onip
				.openNebulaHostToGreenSitterHost(hostON);
		assertEquals("host1", hostGS.getName());
		assertEquals(1, hostGS.getRunningVM());
		assertEquals(2, hostGS.getAvailableCPU());
		assertEquals(1024, hostGS.getAvailableRAM());
	}

	@Test
	public void testHostsInThePool() {
		OpenNebulaInfoPlugin onip = new OpenNebulaInfoPlugin("user", "password", "localhost");
		onip.setClientFactory(createClientFactoryMock(2));
		assertEquals(2, onip.getHostInformation().size());
	}

}
