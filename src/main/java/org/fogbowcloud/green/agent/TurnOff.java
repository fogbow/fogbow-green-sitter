package org.fogbowcloud.green.agent;

import java.util.Properties;

import org.apache.log4j.Logger;

public class TurnOff {

	private static final Logger LOGGER = Logger.getLogger(TurnOff.class);
	private static final String DEFAULT_SUSPEND_COMMAND = "pm-suspend";
	private Properties prop;

	public TurnOff(Properties prop) {
		this.prop = prop;
	}

	public void suspend() {
		String command = prop.getProperty("green.TurnOffCommand");
		if (command == null || command.isEmpty()){
			command = DEFAULT_SUSPEND_COMMAND;
		}

		ProcessBuilder pb = new ProcessBuilder("sudo", "-S", command);
		try {
			Process process = pb.start();
			process.waitFor();
		} catch (Exception e) {
			LOGGER.warn("It was not possible to turn down this host", e);
		}
	}

}
