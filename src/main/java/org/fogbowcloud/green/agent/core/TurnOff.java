package org.fogbowcloud.green.agent.core;

import java.io.IOException;

public class TurnOff {

	public void hibernate(){
		String hibernateCommand="";
		String operatingSystem = System.getProperty("os.name");
		if ("Linux".equals(operatingSystem)
				|| "Mac OS X".equals(operatingSystem)) {
			hibernateCommand = "pm-hibernate";
		}
		//Suspend or hibernate?
		 ProcessBuilder pb =
				   new ProcessBuilder("sudo","-S", hibernateCommand);
		 try {
			pb.start();
		} catch (IOException e) {
			System.out.println("It was not possible to turn down");
		}
	}


}
