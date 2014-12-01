package org.fogbowcloud.green.server.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.fogbowcloud.green.server.core.greenStrategy.GreenStrategyThread;

public class Main {

	public static Properties getProp(String path) throws IOException {
		Properties props = new Properties();
		FileInputStream file = new FileInputStream(path);
		props.load(file);
		return props;
	}

	public static void main(String[] args) {
		Properties prop = new Properties();
		try {
			prop = getProp(args[0]);
		} catch (Exception e) {
		}
		GreenStrategyThread gs = new GreenStrategyThread(prop);
		gs.start();
		while (true) {
			try {
				long sleepingTime = Long.parseLong(prop
						.getProperty("greenstrategyprop.sleeptime"));
				Thread.sleep(sleepingTime);
				gs.run();
			} catch (Exception e) {
			}
		}
	}
}
