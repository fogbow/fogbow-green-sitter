package org.fogbowcloud.green.server.core.greenStrategy;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.fogbowcloud.green.server.communication.ServerCommunicationComponent;
import org.fogbowcloud.green.server.core.plugins.CloudInfoPlugin;
import org.fogbowcloud.green.server.core.plugins.openstack.OpenStackInfoPlugin;

public class DefaultGreenStrategy implements GreenStrategy {

	private static final Logger LOGGER = Logger
			.getLogger(DefaultGreenStrategy.class);

	private CloudInfoPlugin cloudInfoPlugin;
	private List<Host> hostsAwake = new LinkedList<Host>();
	/*
	 * The List hosts in grace period is used to keep those hosts which are not
	 * been used by the system but not for too long
	 */
	private List<Host> hostsInGracePeriod = new LinkedList<Host>();
	private PriorityQueue<Host> sleepingHosts = new PriorityQueue<Host>();
	private ServerCommunicationComponent scc;

	private DateWrapper dateWrapper;

	private long graceTime;
	private long expirationTime;
	private int threadTime;
	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			try {
				checkExpiredHosts();
				sendIdleHostsToBed();
			} catch (Exception e) {
				LOGGER.warn("Exception thrown at the main thread", e);
			}
		}
	};

	protected ScheduledExecutorService executorService = Executors
			.newScheduledThreadPool(1);

	public DefaultGreenStrategy(Properties prop) {
		this.cloudInfoPlugin = new OpenStackInfoPlugin(prop
				.getProperty("openstack.endpoint").toString(), prop
				.getProperty("openstack.username").toString(), prop
				.get("openstack.password").toString(), prop
				.getProperty("openstack.tenant").toString());
		this.dateWrapper = new DateWrapper();
		this.graceTime = Long.parseLong(prop
				.getProperty("greenstrategy.gracetime")) * 1000;
		this.expirationTime = Long.parseLong(prop
				.getProperty("greenstrategy.expirationtime")) * 1000;
		if (prop.getProperty("green.threadTime") != null) {
			this.threadTime = Integer.parseInt(prop
					.getProperty("green.threadTime"));
		}
		else{
			this.threadTime = 60;
		}
	}

	/*
	 * Constructor used for tests
	 */
	protected DefaultGreenStrategy(CloudInfoPlugin openStackPlugin,
			long graceTime) {
		this.cloudInfoPlugin = openStackPlugin;
		this.graceTime = graceTime;
	}

	protected void setExpirationTime(long lostHostTime) {
		this.expirationTime = lostHostTime;
	}
	
	protected void setExecutorService(ScheduledExecutorService executorService) {
		this.executorService = executorService;
	}
	
	protected void setRunnable(Runnable runnable) {
		this.runnable = runnable;
	}

	protected void setAllHosts(List<Host> hosts) {
		this.hostsAwake = hosts;
	}

	protected void setDateWrapper(DateWrapper dateWrapper) {
		this.dateWrapper = dateWrapper;
	}
	protected int getThreadTime() {
		return threadTime;
	}

	public void setCommunicationComponent(ServerCommunicationComponent gscc) {
		this.scc = gscc;
	}

	public List<Host> getHostsInGracePeriod() {
		return hostsInGracePeriod;
	}

	public PriorityQueue<Host> getSleepingHosts() {
		return sleepingHosts;
	}

	public List<? extends Host> getHostsAwake() {
		return hostsAwake;
	}

	protected void updateAllHosts() {
		
		LOGGER.info("Updating host info at the local cloud...");
		
		List<? extends Host> cloudInfo = this.cloudInfoPlugin.getHostInformation();

		/*
		 * Solution for not loosing data when it is updated
		 */
		for (Host hostCloudInfo : cloudInfo) {
			for (Host host : this.hostsAwake) {
				if (host.getName().equals(hostCloudInfo.getName())) {
					host.setAvailableCPU(hostCloudInfo.getAvailableCPU());
					host.setAvailableRAM(hostCloudInfo.getAvailableRAM());
					host.setRunningVM(hostCloudInfo.getRunningVM());
					host.setNovaRunning(hostCloudInfo.isComputeComponentRunning());
					host.setEnabled(hostCloudInfo.isEnabled());
					host.setCloudUpdatedTime(dateWrapper.getTime());
				}
			}
		}
	}

	public void receiveIamAliveInfo(String hostName, String jid, String ip,
			String macAddress) {

		LOGGER.info("Received IAmAlive from " + hostName + ", JID: " + jid + ", MAC: " + macAddress + ", IP: " +ip);
		
		Host hostToUpdate = null;
		for (Host host : this.hostsAwake) {
			if (host.getName().equals(hostName)) {
				hostToUpdate = host;
			}
		}
		if (hostToUpdate == null) {
			hostToUpdate = new Host(hostName);
			this.hostsAwake.add(hostToUpdate);
		}
		hostToUpdate.setJid(jid);
		hostToUpdate.setIp(ip);
		hostToUpdate.setMacAddress(macAddress);
		hostToUpdate.setLastSeen(dateWrapper.getTime());
	}

	public void sendIdleHostsToBed() {
		this.updateAllHosts();

		LOGGER.info("Will send idle hosts to bed. Hosts' status: "
				+ this.hostsAwake);
		for (Host host : this.hostsAwake) {
			if (host.isEnabled() && host.isComputeComponentRunning()
					&& (host.getRunningVM() == 0)) {
				if (!this.getHostsInGracePeriod().contains(host)) {
					host.setNappingSince(this.dateWrapper.getTime());
					LOGGER.info("Host " + host.getName() + " in grace period");
					this.hostsInGracePeriod.add(host);
				} else {
					long nowTime = this.dateWrapper.getTime();
					if (!this.getSleepingHosts().contains(host)) {
						/*
						 * if there is more than a half hour that the host is
						 * napping than put it in sleeping host list
						 */
						if (nowTime - host.getNappingSince() > this.graceTime) {
							scc.sendIdleHostToBed(host.getJid());
							LOGGER.info("Host " + host.getName() + " was sent to bed");
							this.sleepingHosts.add(host);
						}
					}
				}
			}
		}
		
		for (Host host : sleepingHosts) {
			this.hostsAwake.remove(host);
			this.hostsInGracePeriod.remove(host);
		}
	}

	public void checkExpiredHosts() {
		List<Host> hostsToRemove = new LinkedList<Host>();
		for (Host host : this.hostsAwake) {
			if (this.dateWrapper.getTime() - host.getLastSeen() > this.expirationTime) {
				hostsToRemove.add(host);
			}
		}
		for (Host hostToRemove : hostsToRemove) {
			LOGGER.info(hostToRemove.getJid() + " has expired.");
			this.hostsAwake.remove(hostToRemove);
			this.hostsInGracePeriod.remove(hostToRemove);
		}
	}

	public void wakeUpSleepingHost(int minCPU, int minRAM) {
		Host host = this.sleepingHosts.peek();
		if (host == null) { 
			LOGGER.info("There is no host sleeping at the moment.");
			return;
		}
		try {
			if (host.getAvailableCPU() < minCPU) {
				LOGGER.info("Tried to wake hosts but no hosts were found");
				return;
			}
			if (host.getAvailableRAM() >= minRAM) {
				this.scc.wakeUpHost(host.getMacAddress());
				this.sleepingHosts.remove(host);
				LOGGER.info("Waked " + host);
			}

		} catch (IOException e) {
			LOGGER.warn("It was not possible to wake " + host +
					"it may be lost or there is a network problem" + e);
		} catch (InterruptedException e) {
			LOGGER.warn("It was not possible to wake " + host +
					"The proccess was interrupted" + e);
		}
	}
	
	public void start() {
		executorService.scheduleWithFixedDelay(this.runnable, 0, threadTime, TimeUnit.SECONDS);
	}

}
