package org.fogbowcloud.green.server.communication;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.dom4j.Element;
import org.fogbowcloud.green.server.core.greenStrategy.GreenStrategy;
import org.jamppa.component.handler.AbstractQueryHandler;
import org.xmpp.packet.IQ;

public class WakeUpRequestHandler extends AbstractQueryHandler {

	private static final String NAMESPACE = "org.fogbowcloud.green.WakeUpRequest";
	private final ExecutorService executor = Executors.newFixedThreadPool(1);
	private final GreenStrategy gs;

	public WakeUpRequestHandler(GreenStrategy gs) {
		super(NAMESPACE);
		this.gs = gs;
	}

	@Override
	public IQ handle(IQ query) {
		// expected content format: the minimum CPU and the RAM (in GigaBytes)
		// capacity required (eg. "1 8")
		Element queryElement = query.getElement().element("query");
		final int minCPU = Integer.parseInt(queryElement.elementText("minCPU"));
		final int minRAM = Integer.parseInt(queryElement.elementText("minRAM"));

		executor.execute(new Runnable() {
			@Override
			public void run() {
				gs.wakeUpSleepingHost(minCPU, minRAM);
			}
		});

		IQ resultIQ = IQ.createResultIQ(query);

		return resultIQ;
	}

}
