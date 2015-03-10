package org.fogbowcloud.green.server.core.greenStrategy;

import java.util.Date;
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

	private CloudInfoPlugin openStackPlugin;
	private List<? extends Host> allWakedHosts;
	private List<Host> lostHosts = new LinkedList<Host>();
	/*
	 * The List hosts in grace period is used to keep those hosts which are not
	 * been used by the system but not for too long
	 */
	private List<Host> hostsInGracePeriod = new LinkedList<Host>();
	private PriorityQueue<Host> sleepingHosts = new PriorityQueue<Host>();
	private ServerCommunicationComponent scc;

	private Date lastUpdatedTime;

	private long graceTime;
	private long sleepingTime;
	private long lostHostTime;

	private ScheduledExecutorService executorSendIdleHostsToBed = Executors
			.newScheduledThreadPool(1);
	private ScheduledExecutorService executorVerifyLastTimeSeen = Executors
			.newScheduledThreadPool(1);

	public DefaultGreenStrategy(Properties greenProperties) {
		this.openStackPlugin = new OpenStackInfoPlugin(greenProperties
				.getProperty("openstack.endpoint").toString(), greenProperties
				.getProperty("openstack.username").toString(), greenProperties
				.get("openstack.password").toString(), greenProperties
				.getProperty("openstack.tenant").toString());
		this.lastUpdatedTime = new Date();
		this.sleepingTime = Long.parseLong(greenProperties
				.getProperty("greenstrategy.sleeptime"));
		this.graceTime = Long.parseLong(greenProperties
				.getProperty("greenstrategy.gracetime"));
		this.lostHostTime = Long.parseLong(greenProperties
				.getProperty("greenstrategy.lostAgentTime"));
		this.allWakedHosts = this.openStackPlugin.getHostInformation();
	}

	/*
	 * Constructor used for tests
	 */
	protected DefaultGreenStrategy(CloudInfoPlugin openStackPlugin,
			long graceTime) {
		this.openStackPlugin = openStackPlugin;
		this.graceTime = graceTime;
		this.allWakedHosts = this.openStackPlugin.getHostInformation();
	}

	protected void setLostHostTime(long lostHostTime) {
		this.lostHostTime = lostHostTime;
	}

	protected void setAllHosts(List<Host> hosts) {
		this.allWakedHosts = hosts;
	}

	protected void setLostHosts(List<Host> hosts) {
		this.lostHosts = hosts;
	}

	protected void setDate(Date date) {
		this.lastUpdatedTime = date;
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

	public List<? extends Host> getAllWakedHosts() {
		return allWakedHosts;
	}

	public List<Host> getLostHosts() {
		return lostHosts;
	}

	protected void updateAllHosts() {
		List<Host> nowHosts = new LinkedList<Host>();
		nowHosts.addAll(this.allWakedHosts);
		this.allWakedHosts = this.openStackPlugin.getHostInformation();

		/*
		 * Solution for eliminating hosts that don't send an "I am alive signal"
		 * but still are in the cloud information
		 */
		for (Host host : this.allWakedHosts) {
			if (!nowHosts.contains(host)) {
				this.allWakedHosts.remove(host);
				if (!this.lostHosts.contains(host)) {
					this.lostHosts.add(host);
				}
				if (this.hostsInGracePeriod.contains(host)) {
					this.hostsInGracePeriod.remove(host);
				}
				if (this.sleepingHosts.contains(host)) {
					this.sleepingHosts.remove(host);
				}
			}
		}

		/*
		 * Solution for not loosing data when it is updated
		 */
		for (Host host : this.allWakedHosts) {
			Host fullHost = nowHosts.get(nowHosts.indexOf(host));
			host.setIp(fullHost.getIp());
			host.setJid(fullHost.getJid());
			host.setMacAddress(fullHost.getMacAddress());
			host.setNappingSince(fullHost.getNappingSince());
			host.setLastSeen(fullHost.getLastSeen());
		}
	}

	public void receiveIamAliveInfo(String hostName, String jid, String ip,
			String macAddress) {

		for (Host host : this.lostHosts) {
			if (this.lostHosts.contains(host)) {
				this.lostHosts.remove(host);
				LinkedList<Host> aux = new LinkedList<Host>();
				aux.addAll(this.allWakedHosts);
				aux.add(host);
				this.allWakedHosts = aux;
				LOGGER.info("Host " + host.getName() + " was found");
			}
		}

		for (Host host : this.allWakedHosts) {
			if (host.getName().equals(hostName)) {
				host.setJid(jid);
				host.setIp(ip);
				host.setMacAddress(macAddress);
				host.setLastSeen(lastUpdatedTime.getTime());
			}
		}
	}

	public void sendIdleHostsToBed() {
		this.updateAllHosts();

		for (Host host : this.allWakedHosts) {
			if (host.isNovaEnable() && host.isNovaRunning()
					&& (host.getRunningVM() == 0)) {
				if (!this.getHostsInGracePeriod().contains(host)) {
					host.setNappingSince(this.lastUpdatedTime.getTime());
					this.getHostsInGracePeriod().add(host);
				} else {
					long nowTime = this.lastUpdatedTime.getTime();
					if (!this.getSleepingHosts().contains(host)) {
						/*
						 * if there is more than a half hour that the host is
						 * napping than put it in sleeping host list
						 */
						if (nowTime - host.getNappingSince() > this.graceTime) {
							scc.sendIdleHostToBed(host.getMacAddress());
							LOGGER.info("Host " + host.getName()
									+ " was sent to bed");
							this.sleepingHosts.add(host);
						}
					}
				}
			}
		}
		for (Host host : sleepingHosts) {
			if (this.allWakedHosts.contains(host)) {
				this.allWakedHosts.remove(host);
			}
			if (this.hostsInGracePeriod.contains(host)) {
				this.hostsInGracePeriod.remove(host);
			}
		}
	}

	public void checkHostsLastSeen() {
		for (Host host : this.allWakedHosts) {
			if (this.lastUpdatedTime.getTime() - host.getLastSeen() > this.lostHostTime) {
				if (this.hostsInGracePeriod.contains(host)) {
					this.hostsInGracePeriod.remove(host);
				}
				this.lostHosts.add(host);
				LOGGER.info("Host " + host.getName() + " was found");
			}
		}
		for (Host host : this.lostHosts) {
			if (this.allWakedHosts.contains(host)) {
				this.allWakedHosts.remove(host);
			}
		}
	}

	public void wakeUpSleepingHost(int minCPU, int minRAM) {
		Host host = this.sleepingHosts.peek();

		if (host.getAvailableCPU() < minCPU) {
			return;
		}
		if (host.getAvailableRAM() >= minRAM) {
			this.scc.wakeUpHost(host.getName());
			this.sleepingHosts.remove(host);
		}
	}

	public void start() {
		executorVerifyLastTimeSeen.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				checkHostsLastSeen();
				LOGGER.info("Checked when hosts were seen");
			}
		}, 0, lostHostTime, TimeUnit.MILLISECONDS);

		executorSendIdleHostsToBed.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				sendIdleHostsToBed();
				LOGGER.info("Sent idle hosts to bed");
			}
		}, 0, sleepingTime, TimeUnit.MILLISECONDS);
	}
}