package org.bsc.jminix;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

@WebServlet(urlPatterns="/jmx/*")
@SuppressWarnings("serial")
public class JMinixServlet extends HttpServlet {

	
	private HttpServlet getJMinixServlet() {
		
		final BundleContext bundleContext = (BundleContext)getServletContext().getAttribute( BundleContext.class.getName() );
		
		if( bundleContext != null ) {
		
			@SuppressWarnings("unchecked")
			final ServiceReference<HttpServlet> ref = (ServiceReference<HttpServlet>) bundleContext.getServiceReference( "jminix.servlet" );
			
			HttpServlet servlet = bundleContext.getService(ref);

			if( servlet != null ) {
				return servlet;
			}
		}
		
		return this;
	}
	
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		getJMinixServlet().service(req, res);
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		System.out.println( "init(config)");
		super.init( config );
		getJMinixServlet().init(config);
	}

	@Override
	public void destroy() {
		System.out.println( "destroy");
		getJMinixServlet().destroy();
		super.destroy();
	}


}
