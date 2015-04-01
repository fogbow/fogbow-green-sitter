package org.fogbowcloud.green.agent;

import java.util.Properties;

import org.junit.Assert;
import org.jamppa.client.XMPPClient;
import org.jivesoftware.smack.XMPPConnection;
import org.junit.Test;
import org.mockito.Mockito;
import org.xmpp.packet.IQ;

public class TestAgentCommunicationComponent {


	private XMPPClient createXMPPClientMock() {
		XMPPClient client = Mockito.mock(XMPPClient.class);
		Mockito.doReturn(Mockito.mock(XMPPConnection.class)).when(client)
				.getConnection();
		return client;
	}

	private Properties createPropMock(String hostName, String ip, 
			String macAddress) {
		Properties prop = Mockito.mock(Properties.class);
		Mockito.doReturn(ip).when(prop).getProperty("host.ip");
		Mockito.doReturn(hostName).when(prop).getProperty("host.name");
		Mockito.doReturn(macAddress).when(prop).getProperty("host.macAddress");
		return prop;
	}

	@Test
	public void testSendIamAliveSignal() {
		XMPPClient client = createXMPPClientMock();
		Properties prop = createPropMock("host", "123.456.78.9", "A1:B2:C3:D4:E5:67");
		AgentCommunicationComponent acc = new AgentCommunicationComponent(prop);
		acc.setClient(client);
		IQ expectedIQ = acc.sendIamAliveSignal();
		Assert.assertEquals(expectedIQ.getElement().element("query").elementText("ip"),"123.456.78.9");
		Assert.assertEquals(expectedIQ.getElement().element("query").elementText("hostName"),"host");
		Assert.assertEquals(expectedIQ.getElement().element("query").elementText("macAddress"),"A1:B2:C3:D4:E5:67");
	}

}
