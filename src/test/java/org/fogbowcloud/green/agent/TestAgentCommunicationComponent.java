package org.fogbowcloud.green.agent;

import java.util.Properties;

import org.dom4j.Element;
import org.jamppa.client.XMPPClient;
import org.jamppa.client.plugin.xep0077.XEP0077;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.Packet;

public class TestAgentCommunicationComponent {
	
	private Properties createProperties() {
		Properties prop = new Properties();
		prop.put("xmpp.jid", "jid@testagent.com");
		prop.put("xmpp.password", "tellnoone");
		prop.put("xmpp.host", "localhost");
		prop.put("xmpp.port", "23");
		prop.put("host.ip", "123.456.78.9");
		prop.put("host.name", "host");
		prop.put("host.macAddress", "A1:B2:C3:D4:E5:67");
		return prop;
	}
	
	@Test
	public void testThreadTimeIsNotSetted() {
		Properties prop = createProperties();
		AgentCommunicationComponent acc = new AgentCommunicationComponent(prop);
		Assert.assertEquals(60, acc.getThreadTime());
	}
	
	@Test
	public void testThreadTimeIstSetted() {
		Properties prop = createProperties();
		prop.put("green.threadTime", "50");
		AgentCommunicationComponent acc = new AgentCommunicationComponent(prop);
		Assert.assertEquals(50, acc.getThreadTime());
	}
	
	@Test
	public void testSendIamAliveSignal() {
		XMPPClient client = Mockito.mock(XMPPClient.class);
		Mockito.doReturn(Mockito.mock(XMPPConnection.class)).when(client)
				.getConnection();
		Properties prop = createProperties();
		AgentCommunicationComponent acc =
				new AgentCommunicationComponent(prop);
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
		Properties prop = new Properties();
		AgentCommunicationComponent acc = new AgentCommunicationComponent(prop);
		acc.setClient(client);
		Assert.assertEquals(false, acc.init());
	}
	
	@Test
	public void testAccountAlreadyCreated () throws XMPPException {
		XMPPException e = new XMPPException();
		XEP0077 register = Mockito.mock(XEP0077.class);
		Properties prop = createProperties();
;		XMPPClient client = Mockito.mock(XMPPClient.class);
		Mockito.doReturn(Mockito.mock(XMPPConnection.class)).when(client).getConnection();
		Mockito.doThrow(e).when(register).createAccount("jid@testagent.com", "tellnoone");
		AgentCommunicationComponent acc = new AgentCommunicationComponent(prop);
		acc.setRegister(register);
		acc.setClient(client);
		Assert.assertEquals(true, acc.init());
	}
	
	@Test
	public void testPacketFromUnexpectedSource() {
		Packet packet = Mockito.mock(Packet.class);
		JID jid = Mockito.mock(JID.class);
		Mockito.doReturn(jid).when(packet).getFrom();
		Mockito.doReturn("otherComponent.com").when(jid).toString();
		PacketFilter pf = AgentCommunicationComponent
				.createPacketFilter("green.server.com"); 
		Assert.assertEquals(false, pf.accept(packet));
	}
	
	@Test
	public void testPacketWithoutFrom() {
		Packet packet = Mockito.mock(Packet.class);
		JID jid = null;
		Mockito.doReturn(jid).when(packet).getFrom();
		PacketFilter pf = AgentCommunicationComponent
				.createPacketFilter("green.server.com");
		Assert.assertEquals(false, pf.accept(packet));
	}
	
	@Test
	public void testPacketWithNullQuerryElement() {
		Packet packet = Mockito.mock(Packet.class);
		JID jid = Mockito.mock(JID.class);
		Mockito.doReturn(jid).when(packet).getFrom();
		Mockito.doReturn("green.server.com").when(jid).toString();
		PacketFilter pf = AgentCommunicationComponent
				.createPacketFilter("green.server.com");
		Assert.assertEquals(false, pf.accept(packet));
	}
	
	@Test
	public void testNoQuery() {
		Packet packet = Mockito.mock(Packet.class);
		JID jid = Mockito.mock(JID.class);
		Mockito.doReturn(jid).when(packet).getFrom();
		Mockito.doReturn(Mockito.mock(Element.class)).when(packet).getElement();
		Mockito.doReturn("green.server.com").when(jid).toString();
		PacketFilter pf = AgentCommunicationComponent
				.createPacketFilter("green.server.com");
		Assert.assertEquals(false, pf.accept(packet));
	}
	
	@Test
	public void testNsIsNull() {
		Packet packet = Mockito.mock(Packet.class);
		JID jid = Mockito.mock(JID.class);
		Mockito.doReturn(jid).when(packet).getFrom();
		Element e = Mockito.mock(Element.class);
		Mockito.doReturn(e).when(packet).getElement();
		Element elementQuery = Mockito.mock(Element.class);
		Mockito.doReturn(elementQuery).when(e).element("query");
		Mockito.doReturn("green.server.com").when(jid).toString();
		PacketFilter pf = AgentCommunicationComponent
				.createPacketFilter("green.server.com");
		Assert.assertEquals(false, pf.accept(packet));
	}
	
	@Test
	public void testNsIsNotCorrect() {
		Packet packet = Mockito.mock(Packet.class);
		JID jid = Mockito.mock(JID.class);
		Mockito.doReturn(jid).when(packet).getFrom();
		Element e = Mockito.mock(Element.class);
		Mockito.doReturn(e).when(packet).getElement();
		Element elementQuery = Mockito.mock(Element.class);
		Mockito.doReturn(elementQuery).when(e).element("query");
		Mockito.doReturn("othernamespace").when(elementQuery).getNamespaceURI();
		Mockito.doReturn("green.server.com").when(jid).toString();
		PacketFilter pf = AgentCommunicationComponent
				.createPacketFilter("green.server.com");
		Assert.assertEquals(false, pf.accept(packet));
	}
	
	@Test
	public void testPacketIsCorrect() {
		Packet packet = Mockito.mock(Packet.class);
		JID jid = Mockito.mock(JID.class);
		Mockito.doReturn(jid).when(packet).getFrom();
		Element e = Mockito.mock(Element.class);
		Mockito.doReturn(e).when(packet).getElement();
		Element elementQuery = Mockito.mock(Element.class);
		Mockito.doReturn(elementQuery).when(e).element("query");
		Mockito.doReturn("org.fogbowcloud.green.GoToBed").when(elementQuery).getNamespaceURI();
		Mockito.doReturn("green.server.com").when(jid).toString();
		PacketFilter pf = AgentCommunicationComponent
				.createPacketFilter("green.server.com");
		Assert.assertEquals(true, pf.accept(packet));
	}
	
	@Test
	public void testCallTurnOff() {
		TurnOff turnOff = Mockito.mock(TurnOff.class);
		PacketListener pl = AgentCommunicationComponent.
				createPacketListener(turnOff);
		pl.processPacket(Mockito.mock(Packet.class));
		Mockito.verify(turnOff).suspend();
	}
}
