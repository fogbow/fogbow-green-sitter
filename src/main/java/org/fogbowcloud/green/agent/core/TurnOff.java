package org.fogbowcloud.green.agent.core;

import java.io.IOException;
import java.util.logging.Level;

import java.util.logging.Logger;

public class TurnOff {

	public void hibernate() {
		String hibernateCommand = "";
		String operatingSystem = System.getProperty("os.name");
		if ("Linux".equals(operatingSystem)
				|| "Mac OS X".equals(operatingSystem)) {
			// Suspend or hibernate?
			hibernateCommand = "pm-suspend";
		}
		ProcessBuilder pb = new ProcessBuilder("sudo", "-S", hibernateCommand);
		try {
			pb.start();
		} catch (IOException e) {
			Logger logger = Logger.getLogger("green.agent");
			logger.log(Level.WARNING,
					"It was not possible to turn down this host");
		}
	}

}
