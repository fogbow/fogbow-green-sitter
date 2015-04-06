package org.fogbowcloud.green.agent;

import java.util.Properties;

import org.junit.Assert;
import org.jamppa.client.XMPPClient;
import org.jamppa.client.plugin.xep0077.XEP0077;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.junit.Test;
import org.mockito.Mockito;
import org.xmpp.packet.IQ;

public class TestAgentCommunicationComponent {

	@Test
	public void testSendIamAliveSignal() {
		XMPPClient client = Mockito.mock(XMPPClient.class);
		Mockito.doReturn(Mockito.mock(XMPPConnection.class)).when(client)
				.getConnection();
		Properties prop = Mockito.mock(Properties.class);
		Mockito.doReturn("123.456.78.9").when(prop).getProperty("host.ip");
		Mockito.doReturn("host").when(prop).getProperty("host.name");
		Mockito.doReturn("A1:B2:C3:D4:E5:67").when(prop)
				.getProperty("host.macAddress");
		AgentCommunicationComponent acc = new AgentCommunicationComponent(prop);
		acc.setClient(client);
		IQ expectedIQ = acc.sendIamAliveSignal();
		Assert.assertEquals(expectedIQ.getElement().element("query")
				.elementText("ip"), "123.456.78.9");
		Assert.assertEquals(expectedIQ.getElement().element("query")
				.elementText("hostName"), "host");
		Assert.assertEquals(expectedIQ.getElement().element("query")
				.elementText("macAddress"), "A1:B2:C3:D4:E5:67");
	}

	@Test
	public void testInitConnectException() throws XMPPException {
		XMPPClient client = Mockito.mock(XMPPClient.class);
		XMPPException e = new XMPPException();
		Mockito.doThrow(e).when(client).connect();
		Mockito.doThrow(e).when(client).registerPlugin(new XEP0077());
		Properties prop = Mockito.mock(Properties.class);
		AgentCommunicationComponent acc = new AgentCommunicationComponent(prop);
		acc.setClient(client);
		Assert.assertEquals(false, acc.init());
	}
	
}
