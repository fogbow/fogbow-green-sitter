package org.fogbowcloud.green.agent.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TurnOff {

	private static String executeCommand(String command) {
		StringBuilder sb = new StringBuilder();
		String[] commands = new String[] { "/bin/sh", "-c", command };
		try {
			Process proc = new ProcessBuilder(commands).start();
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(
					proc.getInputStream()));

			BufferedReader stdError = new BufferedReader(new InputStreamReader(
					proc.getErrorStream()));

			String s = null;
			while ((s = stdInput.readLine()) != null) {
				sb.append(s);
				sb.append("\n");
			}

			while ((s = stdError.readLine()) != null) {
				sb.append(s);
				sb.append("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	public void hibernate() throws RuntimeException, IOException {
		String hibernateCommand="";
		String operatingSystem = System.getProperty("os.name");
		if ("Linux".equals(operatingSystem)
				|| "Mac OS X".equals(operatingSystem)) {
			hibernateCommand = "/usr/bin/dbus-send --system --print-reply --dest='org.freedesktop.UPower' "
					+ "/org/freedesktop/UPower org.freedesktop.UPower.Suspend";
		}
	}


}
