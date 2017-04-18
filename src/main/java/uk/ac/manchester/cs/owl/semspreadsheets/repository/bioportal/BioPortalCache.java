/*******************************************************************************
 * Copyright (c) 2009, 2017, The University of Manchester
 *
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 *  
 *******************************************************************************/

package uk.ac.manchester.cs.owl.semspreadsheets.repository.bioportal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Logger;

public class BioPortalCache {
	static class BioPortalCacheHolder {
		private static BioPortalCache INSTANCE = new BioPortalCache();
	}
	public static BioPortalCache getInstance() {
		return BioPortalCacheHolder.INSTANCE;
	}
	
	public static String getUserBioportalCache() {
	    return System.getProperty("user.home") + File.separator + ".bioportal_cache";
	}
	
	private static final String CACHE_NAME = "/bioportal_cache";
	
	private static final Logger logger = Logger.getLogger(BioPortalCache.class);
	
	private static Properties bioportalCachedDetails;
	
	public String getFormat(String acronym) {
		return bioportalCachedDetails.getProperty(acronym);
	}
	
	public void storeFormat(String acronym, String format) {
		bioportalCachedDetails.setProperty(acronym, format);
	}
	
	void dumpStoredProperties() throws IOException {
		logger.info("Storing cache in " + getUserBioportalCache());
    	FileOutputStream stream = new FileOutputStream(getUserBioportalCache());
    	bioportalCachedDetails.store(stream,null);
    	stream.close();
    	logger.info("Stored");
	}
	
	private BioPortalCache() {
		bioportalCachedDetails = new Properties();
		File userCache = new File(getUserBioportalCache());
		try {
			InputStream stream = null;
			if (userCache.exists() && !userCache.isDirectory()) {
				stream = new FileInputStream(userCache);
			} else {
				URL resource = BioPortalRepositoryAccessor.class.getResource(CACHE_NAME);
				if (resource != null) {
					stream = resource.openStream();
				}
			}
			bioportalCachedDetails.load(stream);
			stream.close();

		} catch (IOException e) {
			logger.error("Error reading bioportal properties file", e);
		}
	}	
	

}
