package org.fogbowcloud.green.server.xmpp;

import java.util.Properties;

import org.fogbowcloud.green.server.core.greenStrategy.GreenStrategy;
import org.jamppa.component.XMPPComponent;

public class GreenSitterXMPPComponent extends XMPPComponent {

	private GreenStrategy gs;

	public GreenSitterXMPPComponent(Properties prop, GreenStrategy gs) {
		super(prop.getProperty("xmpp.jid"), prop.getProperty("xmpp.password"), 
				prop.getProperty("xmpp.host"), Integer.parseInt(prop.getProperty("xmpp.port")));
		this.gs = gs;
		addHandlers();
	}

	private void addHandlers() {
		IAmAliveHandler alive = new IAmAliveHandler(gs);
		WakeUpRequestHandler wakeup = new WakeUpRequestHandler();
		addGetHandler(alive);
		addGetHandler(wakeup);
	}

}
