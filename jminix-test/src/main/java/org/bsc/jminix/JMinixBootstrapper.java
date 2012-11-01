package org.bsc.jminix;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServlet;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;

import de.kalpatec.pojosr.framework.launch.BundleDescriptor;
import de.kalpatec.pojosr.framework.launch.ClasspathScanner;
import de.kalpatec.pojosr.framework.launch.PojoServiceRegistry;
import de.kalpatec.pojosr.framework.launch.PojoServiceRegistryFactory;

@WebListener
public class JMinixBootstrapper implements ServletContextListener {

	/**
	 * 
	 */
	public void contextDestroyed(ServletContextEvent ctx) {

		final BundleContext bundleContext = (BundleContext) ctx.getServletContext().getAttribute( BundleContext.class.getName() );
		
		if( bundleContext != null ) {
		
			Bundle[] bundles = bundleContext.getBundles();
			
			if( bundles != null ) {
				
				for( Bundle bundle : bundles ) {
					
					try {
						bundle.stop();
					} catch (BundleException e) {
						
						System.out.printf( "error stopping bundle = [%s]\n", bundle);
						
						// TODO log information
					}
				}
			}
		}
	}

	/**
	 * 
	 */
	public void contextInitialized(ServletContextEvent ctx) {
		
		try {
				
			java.io.File bundleFolder = new java.io.File( ctx.getServletContext().getRealPath("WEB-INF/bundle") );
			
			System.out.printf( "bundleFolder=[%s]\n", bundleFolder);
			
			java.io.File[] files = bundleFolder.listFiles( new FilenameFilter() {
				
				@Override
				public boolean accept(File file, String name) {
					return name.endsWith(".jar");
				}
			});
			
			java.net.URL[] urls = new java.net.URL[ files.length ];
			for( int i=0; i < files.length; ++i ) {
				urls[i] = files[i].toURI().toURL();
			}
			URLClassLoader cl = new URLClassLoader( urls, getClass().getClassLoader() );
			
			ClasspathScanner cps = new ClasspathScanner(cl);
			
			java.util.List<BundleDescriptor> bundles = cps.scanForBundles();
			
			for( BundleDescriptor bd : bundles ) {
				
				System.out.printf( "bundle [%s]\n", bd.toString());
			}
			
			
			Map<String,Object> config = new HashMap<String,Object>();
			config.put(PojoServiceRegistryFactory.BUNDLE_DESCRIPTORS, new ClasspathScanner().scanForBundles());
	
			ServiceLoader<PojoServiceRegistryFactory> loader = ServiceLoader.load(PojoServiceRegistryFactory.class);
	
			PojoServiceRegistry registry = loader.iterator().next().newPojoServiceRegistry(config);
			
			registry.startBundles(bundles);

			ctx.getServletContext().setAttribute( BundleContext.class.getName(), registry.getBundleContext());
			
			//
			// TEST JMINIX SERVICE
			//
			
			@SuppressWarnings("unchecked")
			final ServiceReference<HttpServlet> ref = registry.getServiceReference( "jminix.servlet" );
			
			HttpServlet servlet = (HttpServlet) registry.getService(ref);
			
			System.out.printf( "servlet service [%s] - [%s]\n", servlet.getClass().getName(), servlet.getServletInfo());
			
		
		}
		catch( Throwable e ) {
			e.printStackTrace();
		}

	}

}
