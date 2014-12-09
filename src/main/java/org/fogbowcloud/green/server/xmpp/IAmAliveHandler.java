package org.fogbowcloud.green.server.xmpp;

import org.fogbowcloud.green.server.core.greenStrategy.GreenStrategy;
import org.jamppa.component.handler.AbstractQueryHandler;
import org.xmpp.packet.IQ;

public class IAmAliveHandler extends AbstractQueryHandler {
	
	private static final String NAMESPACE = "org.fogbowcloud.green.IAmAlive";
	private final GreenStrategy gs;

	public IAmAliveHandler(GreenStrategy gs) {
		super(NAMESPACE);
		this.gs = gs;
	}

	@Override
	public IQ handle(IQ query) {
		//expected content format: "123.456.789"
		String ip = query.getElement().element("query").elementText("content");
		String jid = query.getFrom().toString();
		
		gs.setAgentAddress(jid, ip);

		IQ resultIQ;
		resultIQ = IQ.createResultIQ(query);
		resultIQ.getElement().addElement("query", getNamespace())
				.addElement("content").setText("Received from JID: " + jid + " IP: "+ip);

		return resultIQ;
	}

}
