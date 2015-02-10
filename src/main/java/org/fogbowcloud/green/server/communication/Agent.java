package org.fogbowcloud.green.server.communication;

public class Agent {
	private String hostName;
	private String ip;
	private String jid;
	private String macAddress;
	
	public Agent(String hostName, String jid, String ip, String macAddress) {
		this.hostName = hostName;
		this.jid = jid;
		this.ip = ip;
		this.macAddress = macAddress;
	}

	public String getHostName() {
		return hostName;
	}

	public String getIp() {
		return ip;
	}

	public String getJid() {
		return jid;
	}
	
	public String getMacAdress(){
		return macAddress;
	}

}
