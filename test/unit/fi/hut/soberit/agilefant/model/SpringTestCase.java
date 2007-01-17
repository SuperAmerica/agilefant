package fi.hut.soberit.agilefant.model;

import org.springframework.test.AbstractTransactionalSpringContextTests;
import org.springframework.util.FileCopyUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public abstract class SpringTestCase extends AbstractTransactionalSpringContextTests {
	
//	private static final String[] configFiles = {
//		"testApplicationContext.xml",
//		"testApplicationContext-daos.xml",
//		"applicationContext-actions.xml"};
	
	private static boolean isRunWithAnt() {
		return System.getProperty("MUURAHAINEN") != null;
	}
		
	
//	private static String[] getConfigFiles(String prefix) {
//		String[] a = new String[configFiles.length];
//		for(int i = 0; i < configFiles.length; i++) {
//			a[i] = prefix + configFiles[i];
//		}
//		return a;
//	}
	
	private static String getDatabaseType() throws FileNotFoundException{
		String userDir = System.getProperty("user.dir");
		String propertyFile = userDir + File.separator + "build.properties";
		File f = new File(propertyFile);
		if(!f.exists())
			throw new FileNotFoundException(propertyFile);
		FileInputStream fis = new FileInputStream(f);
		Properties p = new Properties();
		try {
			p.load(fis);
		} catch(IOException ie) {
			throw new FileNotFoundException();
		}
		return p.getProperty("database.type");
	}

	private static void copyPropertiesEtc() {
		String userDir = System.getProperty("user.dir");
		String fromDirStr = userDir + File.separator + "conf";
		String toDirStr = userDir + File.separator + "build" + File.separator + "WEB-INF";
//		System.out.println("to : " + toDirStr + ", from : " + fromDirStr);

		// create the directory to copy files to, if it's doesn't exist  
		File toDir = new File(toDirStr);
		if(!toDir.exists()) {
			toDir.mkdir();
		}
		File fromDir = new File(fromDirStr);
		for(File file : fromDir.listFiles()) {
//			if(file.toString().indexOf("properties") != -1 ) {
			if(file.isFile()) {
				File toFile = new File(toDirStr + File.separator + file.getName()); 
				try {
					FileCopyUtils.copy(file, toFile);
				} catch(IOException ie) {
					System.out.println("FileCopy didn't work.");
				}	
			}
		}
		
		// copy the right file for database.properties
		try {
			File fromFile = new File(toDirStr + File.separator + "database-fortests-" + getDatabaseType() + ".properties");
			File toFile = new File(toDirStr + File.separator + "database.properties");
			FileCopyUtils.copy(fromFile, toFile);
		} catch(FileNotFoundException fe) {
			System.out.println("build.properties not found");
		} catch(IOException ie) {
			System.out.println("IOException");
		}
		
	}
	
	@Override
	protected String[] getConfigLocations() {
		if(isRunWithAnt()) {
			System.out.println("Is run with ant. MUURAHAINEN.");
			// works with ant:
			return new String[]{"file:WEB-INF/applicationContext*.xml"}; // for testing with ant
//			return getConfigFiles("file:WEB-INF/");  // didn't work with Ant
		}
		else {
			// works with eclipse:
			copyPropertiesEtc();
			return new String[]{"file:conf/applicationContext*.xml"}; // for testing with ant
//			return getConfigFiles("file:conf/");
		}
	}

}
