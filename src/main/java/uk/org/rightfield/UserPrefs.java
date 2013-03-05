package uk.org.rightfield;

import java.io.File;
import java.util.prefs.Preferences;

public class UserPrefs {
	private final static String LAST_FILE_LOCATION = "last_file_loc";	
	private static Preferences prefs = Preferences.userRoot();
	
	public static String getLastFileLocation() {
		return prefs.get(LAST_FILE_LOCATION,null);
	}
	
	public static void setLastFileLocation(String path) {
		prefs.put(LAST_FILE_LOCATION, path);
	}
	
	public static void setLastFileLocation(File file) {
		if (file!=null) {
			if (!file.isDirectory()) {
				file=file.getParentFile();
			}
			setLastFileLocation(file.getAbsolutePath());
		}
		
	}
		
}
