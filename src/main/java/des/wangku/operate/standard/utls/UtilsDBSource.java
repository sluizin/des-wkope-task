package des.wangku.operate.standard.utls;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;

import des.wangku.operate.standard.task.AbstractTask;

/**
 * 数据源的连接方式
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class UtilsDBSource {
	/** 日志 */
	static Logger logger = Logger.getLogger(UtilsDBSource.class);
	/**
	 * 得到数据库Connection
	 * @param driver String
	 * @param url String
	 * @param dbName String
	 * @param password String
	 * @return Connection
	 */
	public static final Connection getConnProperties(String driver, String url, String dbName, String password) {
		Connection conn = null;
		try {
			Class.forName(driver);
			if (dbName == null || dbName.length() == 0) conn = DriverManager.getConnection(url);
			else conn = DriverManager.getConnection(url, dbName, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

	/**
	 * 从配置文件中提取 Connection<br>
	 * 规则：<br>
	 * jdbc.driver.{key} = com.mysql.jdbc.Driver<br>
	 * jdbc.url.{key} = jdbc:mysql://127.0.0.1:3306/performance?useUnicode=true<br>
	 * jdbc.user.{key} = root<br>
	 * jdbc.pass.{key} = 123456789<br>
	 * @param properties Properties
	 * @param key String
	 * @return Connection
	 */
	public static final Connection getConnProperties(Properties properties, String key) {
		if (properties == null) return null;
		String driver = UtilsProperties.getProPropValue(properties, "jdbc.driver." + key, "");
		String url = UtilsProperties.getProPropValue(properties, "jdbc.url." + key, "");
		String dbName = UtilsProperties.getProPropValue(properties, "jdbc.user." + key, "");
		String password = UtilsProperties.getProPropValue(properties, "jdbc.pass." + key, "");
		return getConnProperties(driver, url, dbName, password);
	}

	/**
	 * 从项目的配置文件中提取 Connection<br>
	 * 规则：<br>
	 * jdbc.driver.{key} = com.mysql.jdbc.Driver<br>
	 * jdbc.url.{key} = jdbc:mysql://127.0.0.1:3306/performance?useUnicode=true<br>
	 * jdbc.user.{key} = root<br>
	 * jdbc.pass.{key} = 123456789<br>
	 * @param abstractTask AbstractTask
	 * @param key String
	 * @return Connection
	 */
	public static final Connection getConnProperties(AbstractTask abstractTask, String key) {
		return getConnProperties(abstractTask.getProProperties(), key);
	}

	/**
	 * @param abstractTask AbstractTask
	 * @param key String
	 * @return Database
	 */
	public static final Database getJackcessDatabase(AbstractTask abstractTask, String key) {
		return getJackcessDatabase(abstractTask.getProProperties(), key);
	}

	/**
	 * 得到access Database
	 * @param properties Properties
	 * @param key String
	 * @return Database
	 */
	public static final Database getJackcessDatabase(Properties properties, String key) {
		if (properties == null) return null;
		String url = UtilsProperties.getProPropValue(properties, "jdbc.fileurl." + key, "");
		return getJackcessDatabase(url);
	}

	/**
	 * 通过access文件得到Database
	 * @param url String
	 * @return Database
	 */
	public static final Database getJackcessDatabase(String url) {
		if (url == null || url.length() == 0) return null;
		File file = new File(url);
		try {
			return DatabaseBuilder.open(file);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 得到access所有的表名
	 * @param url String
	 * @return String[]
	 */
	public static final String[] getJackcessDatabaseTableAll(String url) {
		String[] arr = {};
		try {
			Database db = UtilsDBSource.getJackcessDatabase(url);
			Set<String> set = db.getTableNames();
			return set.toArray(arr);
		} catch (IOException e) {
			e.printStackTrace();
			return arr;
		}
	}

	/**
	 * 得到access所有的表名
	 * @param properties Properties
	 * @param key String
	 * @return String[]
	 */
	public static final String[] getJackcessDatabaseTableAll(Properties properties, String key) {
		String[] arr = {};
		try {
			Database db = UtilsDBSource.getJackcessDatabase(properties, key);
			Set<String> set = db.getTableNames();
			return set.toArray(arr);
		} catch (IOException e) {
			e.printStackTrace();
			return arr;
		}
	}

	public static void main(String[] args) {
		String fileurl = "d:/data/des-wkope-task-p000.accdb";
		File file = new File(fileurl);
		try {
			Database db = DatabaseBuilder.open(file);
			Set<String> set = db.getTableNames();
			for (String e : set) {
				System.out.println("e:" + e);
			}
			db.close();
			String driver = "net.ucanaccess.jdbc.UcanaccessDriver";
			Class.forName(driver);
			String url = "jdbc:ucanaccess://d:/data/des-wkope-task-p000.accdb;showschema=true;shutdown=true;memory=false";
			Connection conn = DriverManager.getConnection(url);
			Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

			String sql = "update web_DownLoad set Name='产品申报纪2' where ID=2 ";
			int t = stmt.executeUpdate(sql);
			System.out.println("t:" + t);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
