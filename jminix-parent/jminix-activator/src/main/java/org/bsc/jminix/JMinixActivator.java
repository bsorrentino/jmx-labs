package org.bsc.jminix;

import java.util.Dictionary;

import org.jminix.console.servlet.MiniConsoleServlet;
import org.jminix.console.tool.StandaloneMiniConsole;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * 
 * @author softphone
 *
 */
public class JMinixActivator implements BundleActivator {

	private static final Dictionary<String, Object> EMPTY_DICTIONARY = new java.util.Hashtable<String,Object>();
	
	/**
	 * 
	 */
	public void start(BundleContext context) throws Exception {

		boolean standalone = Boolean.valueOf(context.getProperty( "jminix.standalone") );
	
		if( standalone ) {

			int port = 8088;
			
			try {
				port = Integer.valueOf(context.getProperty( "jminix.standalone.port") );
			}
			catch( NumberFormatException e ) {
				port = 8088;
			}
			new StandaloneMiniConsole(port);

		}
		else {
			
			context.registerService( "jminix.servlet", new MiniConsoleServlet(), EMPTY_DICTIONARY );
		}
	}

	public void stop(BundleContext context) throws Exception {

	}

}
