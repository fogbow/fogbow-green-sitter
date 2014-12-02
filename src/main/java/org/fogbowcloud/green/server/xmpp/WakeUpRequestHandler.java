package org.fogbowcloud.green.server.xmpp;

import org.jamppa.component.handler.AbstractQueryHandler;
import org.xmpp.packet.IQ;

public class WakeUpRequestHandler extends AbstractQueryHandler {

	public WakeUpRequestHandler(String namespace) {
		super(namespace);
	}

	@Override
	public IQ handle(IQ arg0) {
		return null;
	}

}
