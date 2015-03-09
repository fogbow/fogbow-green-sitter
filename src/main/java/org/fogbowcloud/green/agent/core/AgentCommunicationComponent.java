package org.fogbowcloud.green.agent.core;

import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.dom4j.tree.DefaultElement;
import org.jamppa.client.XMPPClient;
import org.jamppa.client.plugin.xep0077.XEP0077;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.PacketFilter;
import org.xmpp.packet.IQ;
import org.xmpp.packet.Packet;
import org.xmpp.packet.IQ.Type;


public class AgentCommunicationComponent {

	private static final String NAMESPACE = "org.fogbowcloud.green.IAmAlive";
	private static final Logger LOGGER = Logger.getLogger(AgentCommunicationComponent.class);

	private ScheduledExecutorService executor = Executors
			.newScheduledThreadPool(1);
	private Properties prop;
	private long sleepingTime;
	private XMPPClient client;

	public AgentCommunicationComponent(Properties prop) {
		try{
		this.prop = prop;
		this.client = new XMPPClient(this.prop.getProperty("xmpp.jid"),
				this.prop.getProperty("xmpp.password"),
				this.prop.getProperty("xmpp.host"), Integer.parseInt(this.prop
						.getProperty("xmpp.port")));
		this.sleepingTime = Long.parseLong(this.prop.getProperty("green.sleepingTime"));
		}catch (Exception e){
			LOGGER.fatal("The configuration file is not correct" + e);
		}
	}

	public Boolean init() {
		XEP0077 register = new XEP0077();
		try {
			this.client.registerPlugin(register);
			this.client.connect();
			register.createAccount(this.prop.getProperty("xmpp.jid"),
					this.prop.getProperty("xmpp.password"));
			this.client.login();
			this.client.process(false);
			LOGGER.info("connected to the server");

		} catch (Exception e) {
            LOGGER.fatal("It was not possible to connect to server " + e);
			return false;
		}
		client.getConnection().addPacketListener(new PacketListener() {
			@Override
			public void processPacket(Packet packet) {
				new TurnOff().suspend(prop.getProperty("green.TurnOffCommand"));
			}
		}, new PacketFilter() {
			@Override
			public boolean accept(Packet packet) {
				if (!packet.getFrom().toString()
						.equals(prop.getProperty("xmpp.component"))) {
					return false;
				}

				String ns = packet.getElement().element("query")
						.getNamespaceURI();

				if (!ns.equals("org.fogbowcloud.green.GoToBed")) {
					return false;
				}

				return true;
			}
		});
		return true;
	}

	public void sendIamAliveSignal() {
		IQ iq = new IQ(Type.get);
		iq.setTo(this.prop.getProperty("xmpp.component"));
		iq.getElement().addElement("query", NAMESPACE);

		DefaultElement query = (DefaultElement) iq.getElement()
				.elements("query").get(0);
		query.addElement("ip").setText(this.prop.getProperty("host.ip"));
		query.addElement("macAddress").setText(
				this.prop.getProperty("host.macAddress"));
		query.addElement("hostName")
				.setText(this.prop.getProperty("host.name"));
	}
	
	public void start() {
		executor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				sendIamAliveSignal();
			}
		}, 0, sleepingTime, TimeUnit.MILLISECONDS);
	}

}
