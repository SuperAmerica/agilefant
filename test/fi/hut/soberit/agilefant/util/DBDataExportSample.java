package fi.hut.soberit.agilefant.util;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

/**
 * Sample class for dbunit data export. Under construction(?)
 * 
 * @author mpmerila?
 */
public class DBDataExportSample {
	public static void main(String[] args) throws Exception {
		// database connection
		Class driverClass = Class
				.forName("org.dbunit.ext.mysql.MySqlConnection");
		Connection mysqlConnection = DriverManager
				.getConnection(
						"jdbc:mysql://localhost/agilefant?relaxAutoCommit\\=true&amp;autoReconnect\\=true&amp;useUnicode\\=true&amp;characterEncoding\\=utf-8&amp;autoReconnectForPools\\=true",
						"agilefant", "agilefant");
		IDatabaseConnection connection = new DatabaseConnection(mysqlConnection);
		// partial database export
		/*
		 * QueryDataSet partialDataSet = new QueryDataSet(connection);
		 * partialDataSet.addTable("FOO", "SELECT * FROM TABLE WHERE
		 * COL='VALUE'"); partialDataSet.addTable("BAR");
		 * FlatXmlDataSet.write(partialDataSet, new
		 * FileOutputStream("partial.xml"));
		 */

		// full database export
		IDataSet fullDataset = connection.createDataSet();
		FlatXmlDataSet.write(fullDataset, new FileOutputStream("full.xml"));
	}
}
