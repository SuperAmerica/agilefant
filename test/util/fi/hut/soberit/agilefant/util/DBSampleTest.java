package fi.hut.soberit.agilefant.util;

import org.dbunit.DatabaseTestCase;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import java.sql.DriverManager;
import java.sql.Connection;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import java.io.FileInputStream;

/**
 * Sample test for DatabaseTestCase of DBUnit.
 * Under construction(?)
 * @author mpmerila
 */
public class DBSampleTest extends DatabaseTestCase {

	public DBSampleTest(String name)
    {
		super(name);
    }
	
	@Override
	protected IDatabaseConnection getConnection() throws Exception {
		Class driverClass = Class.forName("org.dbunit.ext.mysql.MySqlConnection");
		Connection mysqlConnection = DriverManager.getConnection(
				"jdbc:mysql://localhost/agilefant?relaxAutoCommit\\=true&amp;autoReconnect\\=true&amp;useUnicode\\=true&amp;characterEncoding\\=utf-8&amp;autoReconnectForPools\\=true", "agilefant", "agilefant");
		return new DatabaseConnection(mysqlConnection);
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(new FileInputStream("full.xml"));
	}
}
