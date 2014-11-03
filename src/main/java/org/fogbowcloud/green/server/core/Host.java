package org.fogbowcloud.green.server.core;

import java.util.Date;

public class Host{
	
	private String name;
	private int runningVM;
	private boolean novaRunning;
	private boolean novaEnable;
	private Date updateTime;
	private int availableCPU;
	private int availableRAM;
	
	public Host(String name, int runningVM, boolean novaRunning,
			boolean novaEnable, Date updateTime, int availableCPU, int availableRAM) {
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

	public void setAvailableCPU(int availableCPU) {
		this.availableCPU = availableCPU;
	}

	public int getAvailableRAM() {
		return availableRAM;
	}

	public void setAvailableRAM(int availableRAM) {
		this.availableRAM = availableRAM;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public String getName() {
		return name;
	}

	public int getRunningVM() {
		return runningVM;
	}


	public boolean isNovaRunning() {
		return novaRunning;
	}

	public boolean isNovaEnable() {
		return novaEnable;
	}

	@Override
	public String toString() {
		return "Host [name=" + name + ", runningVM=" + runningVM
				+ ", novaRunning=" + novaRunning + ", novaEnable=" + novaEnable
				+ ", updateTime=" + updateTime + ", availableCPU="
				+ availableCPU + ", availableRAM=" + availableRAM + "]";
	}
}
