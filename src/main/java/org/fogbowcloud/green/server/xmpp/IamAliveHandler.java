package org.fogbowcloud.green.server.xmpp;

import org.fogbowcloud.green.server.core.greenStrategy.GreenStrategy;
import org.jamppa.component.handler.AbstractQueryHandler;
import org.xmpp.packet.IQ;

public class IamAliveHandler extends AbstractQueryHandler {
	
	GreenStrategy gs;

	public IamAliveHandler(String namespace, GreenStrategy gs) {
		super(namespace);
		this.gs = gs;
	}

	@Override
	public IQ handle(IQ query) {
		// expected content format: "123.456.789"
		String IP = query.getElement().element("query").elementText("content");
		String JID = query.getFrom().toString();
		
		gs.setAgentAddress(JID, IP);

		IQ resultIQ;
		resultIQ = IQ.createResultIQ(query);
		resultIQ.getElement().addElement("query", getNamespace())
				.addElement("content").setText("Received from JID: " + JID + " IP:"+IP);

		return resultIQ;
	}

}
