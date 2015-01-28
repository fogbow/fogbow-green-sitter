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
		//expected content format: IP plus host name (eg. "123.456.789 bobo")
		String ip = query.getElement().element("query").elementText("ip");
		String hostName = query.getElement().element("query").elementText("hostName");
		String jid = query.getFrom().toString();
		
		gs.setAgentAddress(hostName, jid, ip);

		IQ resultIQ = IQ.createResultIQ(query);

		return resultIQ;
	}

}
