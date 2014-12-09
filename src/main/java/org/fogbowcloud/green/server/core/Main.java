package org.fogbowcloud.green.server.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.fogbowcloud.green.server.core.greenStrategy.DefaultGreenStrategy;
import org.fogbowcloud.green.server.xmpp.GreenSitterXMPPComponent;

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
			GreenSitterXMPPComponent xmppComponent = new GreenSitterXMPPComponent(prop, gs);
			gs.start();
			xmppComponent.process(true);
		} catch (Exception e) {
			System.err.println("You must provide as parameter the"
					+ " path for the configuration file");
		}
	}
}
