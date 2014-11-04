package org.fogbowcloud.green.server.core;

public class Host{
	
	
	private String name;
	private int runningVM;
	private boolean novaRunning;
	private boolean novaEnable;
	private long updateTime;
	private int availableCPU;
	private int availableRAM;
	
	public Host(String name, int runningVM, boolean novaRunning,
			boolean novaEnable, long updateTime, int availableCPU, int availableRAM) {
		super();
		this.name = name;
		this.runningVM = runningVM;
		this.novaRunning = novaRunning;
		this.novaEnable = novaEnable;
		this.updateTime = updateTime;
		this.availableCPU = availableCPU;
		this.availableRAM =  availableRAM;
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

	public long getUpdateTime() {
		return updateTime;
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

	public void setAvailableRAM(int availableRAM) {
		this.availableRAM = availableRAM;
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
				+ ", updateTime=" + updateTime + ", availableCPU="
				+ availableCPU + ", availableRAM=" + availableRAM + "]";
	}
}
