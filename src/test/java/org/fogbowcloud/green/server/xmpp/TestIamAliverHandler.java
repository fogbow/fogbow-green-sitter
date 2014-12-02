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
				.setText("hi-sitter jid@test.com 123.456.789");
		IQ iq2 = new IQ();
		iq2.setTo("iamalive.test.com");
		iq2.getElement().addElement("query", "iamalive").addElement("content")
				.setText("hi-sitter jid2@test.com 123.756.759");

		IamAliveHandler iah = new IamAliveHandler("test");
		iah.handle(iq1);
		iah.handle(iq2);

		Assert.assertEquals(iah.getJID_ID().size(), 2);
	}

	@Test
	public void testWrongFormatation() {
		IQ iq1 = new IQ();
		iq1.setTo("iamalive.test.com");
		iq1.getElement().addElement("query", "iamalive").addElement("content")
				.setText("fdsaf");

		IamAliveHandler iah = new IamAliveHandler("test");
		IQ answerIQ = iah.handle(iq1);
		Assert.assertTrue(answerIQ
				.toString()
				.contains(
						"Error, you query isn't formated properly."
								+ " It must be formated like 'hi-sitter jid@something.org "
								+ "123.456.789' (hi-sitter, JID, IP)"));
	}
}
