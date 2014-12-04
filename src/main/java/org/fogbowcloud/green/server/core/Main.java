package org.fogbowcloud.green.server.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.fogbowcloud.green.server.core.greenStrategy.DefaultGreenStrategy;

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
		} catch (IOException e) {
			
		}
		DefaultGreenStrategy gs = new DefaultGreenStrategy(prop);
		gs.start();
	}
}
