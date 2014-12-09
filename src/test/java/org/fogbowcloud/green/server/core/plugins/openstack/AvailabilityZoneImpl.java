package org.fogbowcloud.green.server.core.plugins.openstack;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.openstack4j.model.compute.ext.AvailabilityZones.AvailabilityZone;
import org.openstack4j.model.compute.ext.AvailabilityZones.NovaService;
import org.openstack4j.model.compute.ext.AvailabilityZones.ZoneState;

public class AvailabilityZoneImpl implements AvailabilityZone {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2420582290244591446L;
	
	private final ZoneState zoneState;
	private final Map<String, HashMap<String, ? extends NovaService>> hosts;
	private final String zoneName;
	
	public AvailabilityZoneImpl(ZoneState zoneState,
			Map<String, HashMap<String, ? extends NovaService>> hosts,
			String zoneName) {
		this.zoneState = zoneState;
		this.hosts = hosts;
		this.zoneName = zoneName;
	}

	@Override
	public ZoneState getZoneState() {
		return zoneState;
	}

	@Override
	public Map<String, HashMap<String, ? extends NovaService>> getHosts() {
		return hosts;
	}

	@Override
	public String getZoneName() {
		return zoneName;
	}
	
	static class ZoneStateImpl implements ZoneState {

		/**
		 * 
		 */
		private static final long serialVersionUID = 7619278708341940515L;
		private boolean available;

		public ZoneStateImpl(boolean available) {
			this.available = available;
		}
		
		@Override
		public boolean getAvailable() {
			return available;
		}
		
	}
	
	static class NovaServiceImpl implements NovaService {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private String available;
		private String active;
		private Date updateTime;
		
		public NovaServiceImpl(String available, String active, Date updateTime) {
			this.available = available;
			this.active = active;
			this.updateTime = updateTime;
		}

		@Override
		public String getAvailable() {
			return available;
		}

		@Override
		public String getStatusActive() {
			return active;
		}

		@Override
		public Date getUpdateTime() {
			return updateTime;
		}
		
	}

}
