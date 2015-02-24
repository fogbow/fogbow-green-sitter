package org.fogbowcloud.green.agent.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

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
			AgentCommunicationComponent acc = new AgentCommunicationComponent(prop);
			acc.sendIamAliveSignal();
		} catch (Exception e) {
			System.err.println("You must provide as parameter the"
					+ " path for the configuration file");
		}
	}
}
