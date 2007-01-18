package fi.hut.soberit.agilefant.util;

import org.springframework.test.AbstractTransactionalSpringContextTests;
import org.springframework.util.FileCopyUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public abstract class SpringTestCase extends AbstractTransactionalSpringContextTests {
	@Override
	protected String[] getConfigLocations() {
		return new String[]{"file:WEB-INF/applicationContext*.xml"};
	}
}
