package org.fogbowcloud.green.agent;

import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

public class TestTurnOff {
	
	@Test
	public void testTurnDownCommandIsNull() {
		Properties prop = new Properties();
		TurnOff turnOff = new TurnOff(prop);
		turnOff.startTurnOff();
		Assert.assertEquals("sudo", turnOff.getPb().command().get(0));
		Assert.assertEquals("-S", turnOff.getPb().command().get(1));
		Assert.assertEquals("pm-suspend", turnOff.getPb().command().get(2));
	}

	@Test
	public void testTurnDownCommandIsSettedandASudoOne() {
		Properties prop = new Properties();
		prop.put("green.TurnOffCommand", "pm-hibernate");
		TurnOff turnOff = new TurnOff(prop);
		turnOff.startTurnOff();
		Assert.assertEquals("sudo", turnOff.getPb().command().get(0));
		Assert.assertEquals("-S", turnOff.getPb().command().get(1));
		Assert.assertEquals("pm-hibernate", turnOff.getPb().command().get(2));
	}

	@Test
	public void testTurnDownCommandisNotInSudoers() {
		Properties prop = new Properties();
		prop.put("green.TurnOffCommand", "other-suspend-command");
		TurnOff turnOff = new TurnOff(prop);
		turnOff.startTurnOff();
		Assert.assertEquals("other-suspend-command", 
				turnOff.getPb().command().get(0));
	}
	
}
