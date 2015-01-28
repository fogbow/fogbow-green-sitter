package org.fogbowcloud.green.server.xmpp;

import org.fogbowcloud.green.server.core.greenStrategy.GreenStrategy;
import org.jamppa.component.handler.AbstractQueryHandler;
import org.xmpp.packet.IQ;

public class WakeUpRequestHandler extends AbstractQueryHandler {
	
	private static final String NAMESPACE = "org.fogbowcloud.green.WakeUpRequest";
	private final GreenStrategy gs;

	public WakeUpRequestHandler(GreenStrategy gs) {
		super(NAMESPACE);
		this.gs = gs;
	}

	@Override
	public IQ handle(IQ query) {
		//expected content format: the minimum CPU and the RAM (in GigaBytes) capacity required (eg. "1 8")
		
		int minCPU = Integer.parseInt(query.getElement().element("query").elementText("cpu"));
		int minRAM = Integer.parseInt(query.getElement().element("query").elementText("ram"));
	
		gs.wakeUpSleepingHost(minCPU, minRAM);
		
		IQ resultIQ = IQ.createResultIQ(query);

		return resultIQ;
	}

}
