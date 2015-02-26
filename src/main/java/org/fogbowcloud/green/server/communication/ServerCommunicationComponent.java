package org.fogbowcloud.green.server.communication;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fogbowcloud.green.server.core.greenStrategy.GreenStrategy;
import org.jamppa.component.XMPPComponent;
import org.xmpp.packet.IQ;
import org.xmpp.packet.IQ.Type;

public class ServerCommunicationComponent extends XMPPComponent {

	private GreenStrategy gs;

	public ServerCommunicationComponent(Properties prop, GreenStrategy gs) {
		super(prop.getProperty("xmpp.jid"), prop.getProperty("xmpp.password"),
				prop.getProperty("xmpp.host"), Integer.parseInt(prop
						.getProperty("xmpp.port")));
		this.gs = gs;
		addHandlers();
	}

	public void wakeUpHost(String macAddress) {
		try {
			ProcessBuilder pb = new ProcessBuilder("powerwake", macAddress);
			pb.start();
		} catch (IOException e) {
			Logger logger = Logger.getLogger("green.server");
			logger.log(Level.WARNING, "It was not possible to wake " + macAddress);
		}
	}

	public void sendIdleHostToBed(String host) {
		IQ iq = new IQ(Type.set);
		iq.setTo("asdas");
		iq.getElement().addElement("query", "org.fogbowcloud.green.GoToBed");
		send(iq);
	}

	private void addHandlers() {
		IAmAliveHandler alive = new IAmAliveHandler(gs);
		WakeUpRequestHandler wakeup = new WakeUpRequestHandler(gs);
		addGetHandler(alive);
		addGetHandler(wakeup);
	}

}
