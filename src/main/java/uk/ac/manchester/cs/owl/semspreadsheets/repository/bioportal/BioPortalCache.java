/*******************************************************************************
 * Copyright (c) 2009-2014, University of Manchester
 *  
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/

package uk.ac.manchester.cs.owl.semspreadsheets.repository.bioportal;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class BioPortalCache {
	static class BioPortalCacheHolder {
		private static BioPortalCache INSTANCE = new BioPortalCache();
	}
	public static BioPortalCache getInstance() {
		return BioPortalCacheHolder.INSTANCE;
	}
	
	private static final Logger logger = Logger.getLogger(BioPortalCache.class);
	
	private static Properties bioportalCachedDetails;
	
	public String getFormat(String acronym) {
		return bioportalCachedDetails.getProperty(acronym);
	}
	
	public void storeFormat(String acronym, String format) {
		bioportalCachedDetails.setProperty(acronym, format);
	}
	
	void dumpStoredProperties() throws IOException {
		String file = BioPortalRepositoryItem.class.getResource("/bioportal-cache.properties").getFile();
    	FileOutputStream stream = new FileOutputStream(file);
    	logger.info("Updating BioPortal properties cache: "+file);
    	bioportalCachedDetails.store(stream,"some comments");
    	stream.close();
	}
	
	private BioPortalCache() {		
    		bioportalCachedDetails = new Properties();
        	try {
        		InputStream stream = BioPortalRepositoryAccessor.class.getResource("/bioportal-cache.properties").openStream();
        		bioportalCachedDetails.load(stream);
				stream.close();
			} catch (IOException e) {
				logger.error("Error reading bioportal properties file",e);
			}        
	}
	
	

}
