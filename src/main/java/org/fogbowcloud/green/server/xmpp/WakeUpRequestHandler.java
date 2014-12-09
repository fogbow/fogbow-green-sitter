package org.fogbowcloud.green.server.xmpp;

import org.jamppa.component.handler.AbstractQueryHandler;
import org.xmpp.packet.IQ;

public class WakeUpRequestHandler extends AbstractQueryHandler {
	
	private static final String NAMESPACE = "org.fogbowcloud.green.WakeUpRequest";

	public WakeUpRequestHandler() {
		super(NAMESPACE);
	}

	@Override
	public IQ handle(IQ arg0) {
		return null;
	}

}
