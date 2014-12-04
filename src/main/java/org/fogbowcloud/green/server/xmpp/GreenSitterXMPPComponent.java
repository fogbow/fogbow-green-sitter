package org.fogbowcloud.green.server.xmpp;

import org.fogbowcloud.green.server.core.greenStrategy.GreenStrategy;
import org.jamppa.component.XMPPComponent;

public class GreenSitterXMPPComponent extends XMPPComponent {
	   
	  IamAliveHandler alive;
	  WakeUpRequestHandler wakeup;
	
	   public GreenSitterXMPPComponent(String jid, String password, String server,
	            int port, GreenStrategy gs) {
	        super(jid, password, server, port);
	        
	        addHandlers();
	    }

	    private void addHandlers() {
	        addGetHandler(alive);
	        addGetHandler(wakeup);
	    }

}
