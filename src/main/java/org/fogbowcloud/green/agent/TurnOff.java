package org.fogbowcloud.green.agent;

import java.io.IOException;

import org.apache.log4j.Logger;

public class TurnOff {

	private static final Logger LOGGER = Logger.getLogger(TurnOff.class);
	private static final String DEFAULT_SUSPEND_COMMAND = "pm-suspend";
	
	public void suspend(String command) {
		if (command == null || command.isEmpty()){
			command = DEFAULT_SUSPEND_COMMAND;
		}

		ProcessBuilder pb = new ProcessBuilder("sudo", "-S", command);
		try {
			pb.start();
		} catch (IOException e) {
			LOGGER.warn("It was not possible to turn down this host", e);
		}
	}

}
