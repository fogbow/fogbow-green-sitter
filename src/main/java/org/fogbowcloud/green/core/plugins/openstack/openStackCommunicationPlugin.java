package org.fogbowcloud.green.core.plugins.openstack;


import java.util.List;


import org.openstack4j.api.OSClient;
import org.openstack4j.openstack.OSFactory;
import org.openstack4j.model.compute.ext.Hypervisor;


public class openStackCommunicationPlugin {
	
	
	OSClient os;
	
	public openStackCommunicationPlugin(String endpoint, String username, String password, String tenantname){
		 
		try{
		os = OSFactory.builder()
		            .endpoint(endpoint)
		            .credentials(username,password)
		            .tenantName(tenantname)
		            .authenticate();}
		catch (Exception e){}
	
	}
	
	public List getHostInformation (){
		return os.compute().hypervisors().list();
		
	}

}