package org.fogbowcloud.green.server.core.plugins.openstack;

import org.openstack4j.model.compute.ext.Hypervisor;

public class HypervisorTestImpl implements Hypervisor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4421687515550026956L;

	private String hostname;
	int runningVM;
	
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public void setRunningVM(int runningVM){
		this.runningVM = runningVM;
	}
	
	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getCurrentWorkload() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getLeastDiskAvailable() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getFreeDisk() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getFreeRam() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getHypervisorHostname() {
		return hostname;
	}

	@Override
	public String getHostIP() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getRunningVM() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getVirtualCPU() {
		return runningVM;
	}

	@Override
	public int getVirtualUsedCPU() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getLocalDisk() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getLocalDiskUsed() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getLocalMemory() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getLocalMemoryUsed() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Service getService() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public CPUInfo getCPUInfo() {
		// TODO Auto-generated method stub
		return null;
	}

}
