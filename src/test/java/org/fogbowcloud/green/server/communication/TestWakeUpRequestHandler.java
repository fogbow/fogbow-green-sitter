package org.fogbowcloud.green.server.communication;

import org.junit.Assert;

import org.dom4j.Element;
import org.fogbowcloud.green.server.core.greenStrategy.DefaultGreenStrategy;
import org.junit.Test;
import org.mockito.Mockito;
import org.xmpp.packet.IQ;
import org.xmpp.packet.IQ.Type;

public class TestWakeUpRequestHandler {
	
	private DefaultGreenStrategy createGreenStrategyMock(int minCPU, int minRAM) {
		DefaultGreenStrategy gs = Mockito.mock(DefaultGreenStrategy.class);
		Mockito.doNothing().when(gs).wakeUpSleepingHost(minCPU, minRAM);
		return gs;
	}
	
	@Test
	public void testHandlingWithRequest() {
		IQ iq = new IQ(Type.set);
		iq.setTo("green.test.com");
		Element query = iq.getElement().addElement("query");
        query.addElement("minCPU").setText(Integer.toString(1));
        query.addElement("minRAM").setText(Integer.toString(1024));
        WakeUpRequestHandler wurh = new WakeUpRequestHandler(this.createGreenStrategyMock(1, 1024));
        IQ result = wurh.handle(iq);
        Assert.assertEquals(result.getFrom().toString(), "green.test.com");
	}

}
