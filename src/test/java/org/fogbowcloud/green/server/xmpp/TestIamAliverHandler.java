package org.fogbowcloud.green.server.xmpp;

import org.junit.Assert;
import org.junit.Test;
import org.xmpp.packet.IQ;

public class TestIamAliverHandler {

	@Test
	public void testTwoHosts() {
		IQ iq1 = new IQ();
		iq1.setTo("iamalive.test.com");
		iq1.getElement().addElement("query", "iamalive").addElement("content")
				.setText("123.456.789");
		IQ iq2 = new IQ();
		iq2.setTo("iamalive.test.com");
		iq2.getElement().addElement("query", "iamalive").addElement("content")
				.setText("123.756.759");

		IamAliveHandler iah = new IamAliveHandler("test", null);
		IQ result = iah.handle(iq1);
		iah.handle(iq2);
		
		System.out.println(result.toString());

	}
}
