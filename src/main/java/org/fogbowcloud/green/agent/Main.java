package org.fogbowcloud.green.agent;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;

public class Main {
	private static final Logger LOGGER = Logger.getLogger(Main.class);

	public static Properties getProp(String path) throws IOException {
		Properties props = new Properties();
		FileInputStream file = new FileInputStream(path);
		props.load(file);
		return props;
	}
	
	private static void configureLog4j() {
		ConsoleAppender console = new ConsoleAppender();
		console.setThreshold(org.apache.log4j.Level.OFF);
		console.activateOptions();
		Logger.getRootLogger().addAppender(console);
	}
	
	public static void main(String[] args) {
		configureLog4j();
		try {
			Properties prop = getProp(args[0]);
			AgentCommunicationComponent acc = new AgentCommunicationComponent(prop);
			if (!acc.init())
				return;
			acc.start();
		} catch (Exception e) {
			LOGGER.fatal("You must provide as parameter the"
					+ " path for the configuration file", e);
		}
	}
}
