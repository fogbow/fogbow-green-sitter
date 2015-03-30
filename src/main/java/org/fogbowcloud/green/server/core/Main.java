package org.fogbowcloud.green.server.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.fogbowcloud.green.server.communication.ServerCommunicationComponent;
import org.fogbowcloud.green.server.core.greenStrategy.DefaultGreenStrategy;

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
			DefaultGreenStrategy gs = new DefaultGreenStrategy(prop);
			ServerCommunicationComponent scc = new ServerCommunicationComponent(
					prop, gs);
			gs.setCommunicationComponent(scc);
			gs.start();
			scc.connect();
			scc.process(true);
			LOGGER.info("Green Server started");
		} catch (Exception e) {
			LOGGER.fatal("You must provide as parameter the"
					+ " path for the configuration file", e);
		}
	}
}
