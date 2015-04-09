package org.fogbowcloud.green.agent;

import java.util.Properties;

import org.apache.log4j.Logger;

public class TurnOff {

	private static final Logger LOGGER = Logger.getLogger(TurnOff.class);
	private static final String DEFAULT_SUSPEND_COMMAND = "pm-suspend";
	private Properties prop;
	private ProcessBuilder pb;

	public TurnOff(Properties prop) {
		this.prop = prop;
	}
	
	protected ProcessBuilder getPb() {
		return pb;
	}
	
	protected void startTurnOff() {
		String command = prop.getProperty("green.TurnOffCommand");
		if (command == null || command.isEmpty()){
			command = DEFAULT_SUSPEND_COMMAND;
		} else if (!command.equals("pm-hibernate") && 
				!command.equals("pm-powersave")) {
			LOGGER.warn("this comand may not be in our sudoers file, "
					+ "it will not be executed as sudo");
			pb = new ProcessBuilder(command);
			return;
		}
		pb = new ProcessBuilder("sudo", "-S", command);
	}

	public void suspend() {
		startTurnOff();
		try {
			Process process = pb.start();
			process.waitFor();
		} catch (Exception e) {
			LOGGER.warn("It was not possible to turn down this host", e);
		}
	}

}
