package org.fogbowcloud.green.agent.core;

import java.io.IOException;
import java.util.logging.Level;

import java.util.logging.Logger;

public class TurnOff {

	public void suspend(String command) {
		String suspendCommand = "pm-suspend";
		
		if (command != ""){
			suspendCommand = command;
		}

		ProcessBuilder pb = new ProcessBuilder("sudo", "-S", suspendCommand);
		try {
			pb.start();
		} catch (IOException e) {
			Logger logger = Logger.getLogger("green.agent");
			logger.log(Level.WARNING,
					"It was not possible to turn down this host");
		}
	}

}
