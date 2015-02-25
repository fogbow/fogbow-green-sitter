package org.fogbowcloud.green.server.communication;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Properties;

import org.dom4j.tree.DefaultElement;
import org.fogbowcloud.green.server.communication.Agent;
import org.fogbowcloud.green.server.core.greenStrategy.GreenStrategy;
import org.jamppa.component.XMPPComponent;
import org.xmpp.packet.IQ;
import org.xmpp.packet.IQ.Type;

public class ServerCommunicationComponent extends XMPPComponent {

	private GreenStrategy gs;
	private LinkedList<Agent> listAgent = new LinkedList<Agent>();

	public ServerCommunicationComponent(Properties prop, GreenStrategy gs) {
		super(prop.getProperty("xmpp.jid"), prop.getProperty("xmpp.password"),
				prop.getProperty("xmpp.host"), Integer.parseInt(prop
						.getProperty("xmpp.port")));
		this.gs = gs;
		addHandlers();
	}

	public void setAgentAddress(String hostName, String jid, String ip,
			String macAddress) {
		Agent agent = new Agent(hostName, jid, ip, macAddress);
		listAgent.add(agent);
	}

	public void wakeUpHost(String macAddress) {
		try {
			ProcessBuilder pb = new ProcessBuilder("powerwake", macAddress);
			pb.start();
		} catch (IOException e) {
			System.err.println("It was not possible to wake " + macAddress);
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
