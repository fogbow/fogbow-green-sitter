package org.fogbowcloud.green.server.communication;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.fogbowcloud.green.server.core.greenStrategy.GreenStrategy;
import org.jamppa.component.PacketSender;
import org.jamppa.component.XMPPComponent;
import org.xmpp.packet.IQ;
import org.xmpp.packet.IQ.Type;

public class ServerCommunicationComponent extends XMPPComponent {

	private static final Logger LOGGER = Logger
			.getLogger(ServerCommunicationComponent.class);
	private GreenStrategy gs;
	private Properties prop;
	private PacketSender packetSender;

	public ServerCommunicationComponent(Properties prop, GreenStrategy gs) {
		super(prop.getProperty("xmpp.jid"), prop.getProperty("xmpp.password"),
				prop.getProperty("xmpp.host"), Integer.parseInt(prop
						.getProperty("xmpp.port")));
		this.prop = prop;
		this.gs = gs;
		this.packetSender = this;
		addHandlers();
	}
	
	void setPacketSender(PacketSender packetSender) {
		this.packetSender = packetSender;
	}

	protected ProcessBuilder createProcessBuilder(String macAddress) {
		LOGGER.debug("Wake command: powerwake -b "
				+ prop.getProperty("wol.broadcast.address") + " " + macAddress);
		ProcessBuilder pb = new ProcessBuilder("powerwake", "-b",
				prop.getProperty("wol.broadcast.address"), macAddress);
		return pb;
	}

	public void wakeUpHost(String macAddress) throws IOException,
			InterruptedException {
		ProcessBuilder pb = createProcessBuilder(macAddress);
		Process process = pb.start();
		process.waitFor();
	}

	public void sendIdleHostToBed(String hostJID) {
		IQ iq = new IQ(Type.set);
		iq.setTo(hostJID);
		iq.getElement().addElement("query", "org.fogbowcloud.green.GoToBed");
		packetSender.sendPacket(iq);
	}

	private void addHandlers() {
		IAmAliveHandler alive = new IAmAliveHandler(gs);
		WakeUpRequestHandler wakeup = new WakeUpRequestHandler(gs);
		addGetHandler(alive);
		addSetHandler(wakeup);
	}

}
