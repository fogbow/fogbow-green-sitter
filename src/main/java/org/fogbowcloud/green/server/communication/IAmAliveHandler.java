package org.fogbowcloud.green.server.communication;

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
		//expected content format: IP plus MAC Address plus host name (eg. "123.456.789 a1:2b:3c:d4:45:67 bobo")
		String ip = query.getElement().element("query").elementText("ip");
		String hostName = query.getElement().element("query").elementText("hostName");
		String jid = query.getFrom().toString();
		String macAddress = query.getElement().element("query").elementText("macAddress");
		
		gs.receiveIamAliveInfo(hostName, jid, ip, macAddress);

		IQ resultIQ = IQ.createResultIQ(query);

		return resultIQ;
	}

}
