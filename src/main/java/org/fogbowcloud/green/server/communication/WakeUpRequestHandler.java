package org.fogbowcloud.green.server.communication;

import org.dom4j.Element;
import org.fogbowcloud.green.server.core.greenStrategy.GreenStrategy;
import org.jamppa.component.handler.AbstractQueryHandler;
import org.xmpp.packet.IQ;

public class WakeUpRequestHandler extends AbstractQueryHandler {
	
	private static final String NAMESPACE = "org.fogbowcqueryloud.green.WakeUpRequest";
	private final GreenStrategy gs;

	public WakeUpRequestHandler(GreenStrategy gs) {
		super(NAMESPACE);
		this.gs = gs;
	}

	@Override
	public IQ handle(IQ query) {
		//expected content format: the minimum CPU and the RAM (in GigaBytes) capacity required (eg. "1 8")
		Element queryElement = query.getElement().element("query");
		int minCPU = Integer.parseInt(queryElement.elementText("minCPU"));
		int minRAM = Integer.parseInt(queryElement.elementText("minRAM"));
	
		gs.wakeUpSleepingHost(minCPU, minRAM);
		
		IQ resultIQ = IQ.createResultIQ(query);

		return resultIQ;
	}

}
