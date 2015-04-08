package org.fogbowcloud.green.agent;

import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class TestTurnOff {
	
	@Test
	public void testTurnDownCommandIsNull() {
		Properties prop = Mockito.mock(Properties.class);
		TurnOff turnOff = new TurnOff(prop);
		turnOff.startTurnOff();
		Assert.assertEquals("sudo", turnOff.getPb().command().get(0));
		Assert.assertEquals("-S", turnOff.getPb().command().get(1));
		Assert.assertEquals("pm-suspend", turnOff.getPb().command().get(2));
	}

	@Test
	public void testTurnDownCommandIsSettedandASudoOne() {
		Properties prop = Mockito.mock(Properties.class);
		Mockito.doReturn("pm-hibernate").when(prop)
				.getProperty("green.TurnOffCommand");
		TurnOff turnOff = new TurnOff(prop);
		turnOff.startTurnOff();
		Assert.assertEquals("sudo", turnOff.getPb().command().get(0));
		Assert.assertEquals("-S", turnOff.getPb().command().get(1));
		Assert.assertEquals("pm-hibernate", turnOff.getPb().command().get(2));
	}

	@Test
	public void testTurnDownCommandisNotInSudoers() {
		Properties prop = Mockito.mock(Properties.class);
		Mockito.doReturn("other-suspend-command").when(prop)
				.getProperty("green.TurnOffCommand");
		TurnOff turnOff = new TurnOff(prop);
		turnOff.startTurnOff();
		Assert.assertEquals("other-suspend-command", 
				turnOff.getPb().command().get(0));
	}
	
}
