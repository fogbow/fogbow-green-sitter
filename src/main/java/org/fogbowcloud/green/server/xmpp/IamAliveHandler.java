package org.fogbowcloud.green.server.xmpp;

import java.util.HashMap;
import java.util.Map;

import org.jamppa.component.handler.AbstractQueryHandler;
import org.xmpp.packet.IQ;

public class IamAliveHandler extends AbstractQueryHandler {

	private Map<String, String> JID_ID = new HashMap<String, String>();

	public IamAliveHandler(String namespace) {
		super(namespace);
	}
	
	@Override
	public IQ handle(IQ query) {
		// expected content: "hi-sitter jid@something.org 123.456.789"
		String originalContent = query.getElement().element("query")
				.elementText("content");

		String listContent[] = originalContent.split(" ");

		try {
			JID_ID.put(listContent[1], listContent[2]);
		} catch (Exception e) {
			IQ resultIQ = IQ.createResultIQ(query);
			resultIQ.getElement()
					.addElement("query", getNamespace())
					.addElement("content")
					.setText("Error, you query isn't formated properly. It must be formated like"+
					" 'hi-sitter jid@something.org 123.456.789' (hi-sitter, JID, IP)");
		}
		IQ resultIQ = IQ.createResultIQ(query);
		resultIQ.getElement().addElement("query", getNamespace())
				.addElement("content").setText("Ok-dear");

		return resultIQ;
	}

	public Map<String, String> getJID_ID() {
		return JID_ID;
	}

}
