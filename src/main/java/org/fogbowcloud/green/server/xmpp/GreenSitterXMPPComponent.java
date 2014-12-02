package org.fogbowcloud.green.server.xmpp;

import java.util.Map;

import org.jamppa.component.XMPPComponent;

public class GreenSitterXMPPComponent extends XMPPComponent {
	   
	  IamAliveHandler alive = new IamAliveHandler("Agent");
	  WakeUpRequestHandler wakeup = new WakeUpRequestHandler("Manager");
	
	   public GreenSitterXMPPComponent(String jid, String password, String server,
	            int port) {
	        super(jid, password, server, port);
	        addHandlers();
	    }

	    private void addHandlers() {
	        addGetHandler(alive);
	        addGetHandler(wakeup);
	    }
	    
	    public Map<String, String> getJID_IP(){
			return alive.getJID_ID();
	    }
}
