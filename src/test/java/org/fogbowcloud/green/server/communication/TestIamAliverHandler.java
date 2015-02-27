package org.fogbowcloud.green.server.communication;

import org.dom4j.tree.DefaultElement;
import org.fogbowcloud.green.server.communication.IAmAliveHandler;
import org.fogbowcloud.green.server.core.greenStrategy.DefaultGreenStrategy;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;

public class TestIamAliverHandler {

	private IQ createIQMock(String jidAdress) {
		JID jid = new JID(jidAdress);
		IQ querry = Mockito.mock(IQ.class);
		Mockito.when(querry.getFrom()).thenReturn(jid);
		Mockito.when(querry.getElement())
				.thenReturn(new DefaultElement("name"));
		return querry;
	}

	@Test
	public void testOneHost() {
		final String IP = "123.456.789";
		final String JID = "fulano@teste.com";
		
		IQ iq = this.createIQMock(JID);
		Mockito.when(iq.getType()).thenReturn(IQ.Type.get);
		iq.setTo("iamalive.test.com");
		iq.getElement().addElement("query", "org.fogbowcloud.green.IAmAlive")
				.addElement("content").setText(IP);
		
		DefaultGreenStrategy gs = Mockito.mock(DefaultGreenStrategy.class);
		Mockito.doNothing().when(gs).receiveIamAliveInfo("nothing",JID, IP, null);
		
		IAmAliveHandler iah = new IAmAliveHandler(gs);
		IQ result = iah.handle(iq);
	  		
		Assert.assertEquals("result", result.getType().name());
	}
}
