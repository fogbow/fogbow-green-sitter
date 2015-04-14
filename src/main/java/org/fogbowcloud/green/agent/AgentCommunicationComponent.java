package org.fogbowcloud.green.agent;

import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;
import org.jamppa.client.XMPPClient;
import org.jamppa.client.plugin.xep0077.XEP0077;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.xmpp.packet.IQ;
import org.xmpp.packet.IQ.Type;
import org.xmpp.packet.JID;
import org.xmpp.packet.Packet;

public class AgentCommunicationComponent {

	private static final String NAMESPACE = "org.fogbowcloud.green.IAmAlive";
	private static final Logger LOGGER = Logger
			.getLogger(AgentCommunicationComponent.class);

	private ScheduledExecutorService executor = Executors
			.newScheduledThreadPool(1);
	private Properties prop;
	private int threadTime;
	private XMPPClient client;
	XEP0077 register = new XEP0077();

	public AgentCommunicationComponent(Properties prop) {
		try {
			this.prop = prop;
			this.client = new XMPPClient(this.prop.getProperty("xmpp.jid"),
					this.prop.getProperty("xmpp.password"),
					this.prop.getProperty("xmpp.host"),
					Integer.parseInt(this.prop.getProperty("xmpp.port")));
			if (this.prop.getProperty("green.threadTime") == null) {
				this.threadTime = 60;
			}
			else{
				this.threadTime = Integer.parseInt(this.prop
						.getProperty("green.threadTime"));
			}
		
		} catch (Exception e) {
			LOGGER.fatal("The configuration file is not correct", e);
		}
	}
	
	protected void setClient(XMPPClient client) {
		this.client = client;
	}
	
	protected void setRegister(XEP0077 register) {
		this.register = register;
	}
	
	protected int getThreadTime() {
		return threadTime;
	}
	
	protected static PacketFilter createPacketFilter(final String componentAddress) {
		return new PacketFilter() {
			@Override
			public boolean accept(Packet packet) {
				JID from = packet.getFrom();
				if (from == null) {
					return false;
				}
				if (!from.toString().equals(componentAddress)) {
					return false;
				}
				if (packet.getError() != null) {
					LOGGER.fatal("IAmAlive packet returned an error: " + packet.toXML());
					return false;
				}
				if ((packet.getElement() == null) || (packet.getElement().element("query") == null)) {
					LOGGER.fatal("There is no query element in the response packet");
					return false;
				}
				Element queryEl = packet.getElement().element("query");
				String ns = queryEl.getNamespaceURI();
				if (ns == null) {
					LOGGER.fatal("The namespace of the query was null: " + ns);
					return false;
				}
				if (!ns.equals("org.fogbowcloud.green.GoToBed")) {
					LOGGER.fatal("Query element has a different namespace: " + ns);
					return false;
				}
				return true;
			}
		};
	}
	
	protected static PacketListener createPacketListener(final TurnOff turnOff) {
		return new PacketListener() {
			@Override
			public void processPacket(Packet packet) {
				turnOff.suspend();
			}
		};
	}

	public Boolean init() {
		try {
			this.client.registerPlugin(register);
			this.client.connect();
		} catch (Exception e) {
			LOGGER.fatal("It was not possible to connect to server", e);
			e.printStackTrace();
			return false;
		}
		try {
			register.createAccount(this.prop.getProperty("xmpp.jid"),
					this.prop.getProperty("xmpp.password"));
		} catch (XMPPException e) {
			LOGGER.warn("It was not possible to create the account", e);
		}
		try {
			this.client.login();
		} catch (XMPPException e) {
			LOGGER.fatal("It was not possible to login", e);
			return false;
		}
		this.client.process(false);
		LOGGER.info("connected to the server");
		client.getConnection().addPacketListener(
				createPacketListener(new TurnOff(prop)), 
				createPacketFilter(prop.getProperty("xmpp.component")));
		return true;
	}

	public IQ sendIamAliveSignal() {	
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
		client.getConnection().sendPacket(iq);
		LOGGER.info("IAmAlive signal sent to " + iq.getTo());
		return iq;
	}

	public void start() {
		executor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				sendIamAliveSignal();
			}
		}, 0, threadTime, TimeUnit.SECONDS);
	}

}
