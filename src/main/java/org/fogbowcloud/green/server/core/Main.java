package org.fogbowcloud.green.server.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fogbowcloud.green.server.communication.ServerCommunicationComponent;
import org.fogbowcloud.green.server.core.greenStrategy.DefaultGreenStrategy;

public class Main {

	public static Properties getProp(String path) throws IOException {
		Properties props = new Properties();
		FileInputStream file = new FileInputStream(path);
		props.load(file);
		return props;
	}

	public static void main(String[] args) {
		try {
			Properties prop = getProp(args[0]);
			DefaultGreenStrategy gs = new DefaultGreenStrategy(prop);
			ServerCommunicationComponent scc = new ServerCommunicationComponent(
					prop, gs);
			gs.setCommunicationComponent(scc);
			gs.start();
			scc.process(true);
		} catch (Exception e) {
			Logger logger = Logger.getLogger("green.server");
			logger.log(Level.WARNING, "You must provide as parameter the"
					+ " path for the configuration file");
		}
	}
}
