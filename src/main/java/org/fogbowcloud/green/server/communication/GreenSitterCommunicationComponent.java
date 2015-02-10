package org.fogbowcloud.green.server.communication;

import java.util.LinkedList;
import java.util.Properties;

import org.fogbowcloud.green.server.communication.Agent;
import org.fogbowcloud.green.server.core.greenStrategy.GreenStrategy;
import org.jamppa.component.XMPPComponent;

public class GreenSitterCommunicationComponent extends XMPPComponent {

	private GreenStrategy gs;
	private LinkedList<Agent> listAgent = new LinkedList<Agent>(); 

	public GreenSitterCommunicationComponent(Properties prop, GreenStrategy gs) {
		super(prop.getProperty("xmpp.jid"), prop.getProperty("xmpp.password"), 
				prop.getProperty("xmpp.host"), Integer.parseInt(prop.getProperty("xmpp.port")));
		this.gs = gs;
		addHandlers();
	}
	
	public void setAgentAddress(String hostName, String jid, String ip, String macAddress) {
		Agent agent = new Agent(hostName, jid, ip, macAddress);
		listAgent.add(agent);
	}
	
	public void sendHostToBed(String host){
		
	}
	
	public void wakeUpHost(String host){
		
	}

	private void addHandlers() {
		IAmAliveHandler alive = new IAmAliveHandler(gs);
		WakeUpRequestHandler wakeup = new WakeUpRequestHandler(gs);
		addGetHandler(alive);
		addGetHandler(wakeup);
	}

}
