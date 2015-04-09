package org.fogbowcloud.green.server.communication;

import java.util.Properties;
import org.dom4j.Element;
import org.fogbowcloud.green.server.core.greenStrategy.DefaultGreenStrategy;
import org.jamppa.component.PacketSender;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.xmpp.packet.IQ;

public class TestServerCommunicationComponent {
	private Properties createProperties() {
		Properties prop = new Properties();
		prop.put("xmpp.jid", "123.456.255.255");
		prop.put("xmpp.password", "tellnoone");
		prop.put("xmpp.host", "localhost");
		prop.put("xmpp.port", "1");
		return prop;
	}

	@Test
	public void testCreationWakeUpCommand() {
		Properties prop = createProperties();
		prop.put("wol.broadcast.address", "123.456.255.255");
		ServerCommunicationComponent scc = new ServerCommunicationComponent(
				prop, Mockito.mock(DefaultGreenStrategy.class));
		ProcessBuilder pb = scc.createProcessBuilder("A1:B2:C3:D4:E5:67");
		Assert.assertEquals("powerwake", pb.command().get(0));
		Assert.assertEquals("-b", pb.command().get(1));
		Assert.assertEquals("123.456.255.255", pb.command().get(2));
		Assert.assertEquals("A1:B2:C3:D4:E5:67", pb.command().get(3));
	}

	private IQ matchIQ() {
		return Matchers.argThat(new ArgumentMatcher<IQ>() {
			@Override
			public boolean matches(Object argument) {
				if (!(argument instanceof IQ)) {
					return false;
				}
				IQ argIQ = (IQ) argument;
				if ((argIQ.getTo() == null)
						|| (!argIQ.getTo().toBareJID().equals("host@jid.com"))) {
					return false;
				}
				if (argIQ.getElement().element("query") == null) {
					return false;
				}
				Element query = argIQ.getElement().element("query");
				if (!query.getNamespaceURI().equals(
						"org.fogbowcloud.green.GoToBed")) {
					return false;
				}
				return true;
			}
		});
	}

	@Test
	public void testSendIdleHostsToBed() {
		Properties prop = createProperties();
		ServerCommunicationComponent scc = new ServerCommunicationComponent(
				prop, Mockito.mock(DefaultGreenStrategy.class));
		PacketSender packetSender = Mockito.mock(PacketSender.class);
		scc.setPacketSender(packetSender);
		scc.sendIdleHostToBed("host@jid.com");
		Mockito.verify(packetSender).sendPacket(matchIQ());
	}
}