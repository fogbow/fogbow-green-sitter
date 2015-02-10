package org.fogbowcloud.green.server.communication;

public class Agent {
	private String hostName;
	private String ip;
	private String jid;
	
	public Agent(String hostName, String jid, String ip) {
		this.hostName = hostName;
		this.jid = jid;
		this.ip = ip;
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

}
