package org.fogbowcloud.green.server.core.greenStrategy;

public class Host implements Comparable<Host> {

	private String name;
	private int runningVM;
	private boolean novaRunning;
	private boolean novaEnable;
	private long cloudUpdatedTime;
	private int availableCPU;
	private int availableRAM;
	private String ip = null;
	private String jid = null;
	private String macAddress = null;
	private long lastSeen = 0;
	private long nappingSince = 0;

	public Host(String name, int runningVM, boolean novaRunning,
			boolean novaEnable, long updateTime, int availableCPU,
			int availableRAM) {
		this.name = name;
		this.runningVM = runningVM;
		this.novaRunning = novaRunning;
		this.novaEnable = novaEnable;
		this.cloudUpdatedTime = updateTime;
		this.availableCPU = availableCPU;
		this.availableRAM = availableRAM;
	}

	public int getAvailableCPU() {
		return availableCPU;
	}

	public int getAvailableRAM() {
		return availableRAM;
	}

	public String getName() {
		return name;
	}

	public int getRunningVM() {
		return runningVM;
	}

	public long getClodUpdatedTime() {
		return cloudUpdatedTime;
	}

	public boolean isNovaEnable() {
		return novaEnable;
	}

	public boolean isNovaRunning() {
		return novaRunning;
	}

	public void setAvailableCPU(int availableCPU) {
		this.availableCPU = availableCPU;
	}

	public void setRunningVM(int runningVM) {
		this.runningVM = runningVM;
	}

	public void setNovaRunning(boolean novaRunning) {
		this.novaRunning = novaRunning;
	}

	public void setCloudUpdatedTime(long updateTime) {
		this.cloudUpdatedTime = updateTime;
	}

	public void setAvailableRAM(int availableRAM) {
		this.availableRAM = availableRAM;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getJid() {
		return jid;
	}

	public void setJid(String jid) {
		this.jid = jid;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public long getLastSeen() {
		return lastSeen;
	}
	
	public void setLastSeen(long lastSeen) {
		this.lastSeen = lastSeen;
	}
	public long getNappingSince() {
		return nappingSince;
	}

	public void setNappingSince(long nappingSince) {
		this.nappingSince = nappingSince;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Host other = (Host) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Host [name=" + name + ", runningVM=" + runningVM
				+ ", novaRunning=" + novaRunning + ", novaEnable=" + novaEnable
				+ ", cloudUpdatedTime=" + cloudUpdatedTime + ", availableCPU="
				+ availableCPU + ", availableRAM=" + availableRAM + ", ip="
				+ ip + ", jid=" + jid + ", macAddress=" + macAddress
				+ ", lastSeen=" + lastSeen + "]";
	}

	@Override
	public int compareTo(Host host) {
		if (this.availableCPU != host.availableCPU) {
			return host.availableCPU - this.availableCPU;
		}
		/*
		 * if the hosts have the same CPU capacities, wake the one with the
		 * biggest RAM
		 */
		return host.availableRAM - this.availableRAM;
	}
}
