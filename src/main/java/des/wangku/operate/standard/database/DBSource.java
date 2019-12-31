package des.wangku.operate.standard.database;

import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import des.wangku.operate.standard.utls.UtilsProperties;

/**
 * 数据源 一些连接数据库的资源
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class DBSource {
	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(DBSource.class);
	/*
	public static SqlSessionFactory mybatisSessionFactory;
	static final boolean booleanLinkMysql = false;
	static {
		// 使用MyBatis提供的Resources类加载mybatis的配置文件
		if (booleanLinkMysql) {
			try {
				Reader reader = Resources.getResourceAsReader("database/mybatis/mybatis.cfg.xml");
				//构建sqlSession的工厂
				mybatisSessionFactory = new SqlSessionFactoryBuilder().build(reader);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
*/
	/**
	 * 创建能执行映射文件 database/mybatis/mybatis.cfg 中的sqlSession
	 * @return SqlSession
	 */
	public static SqlSession getMybatisSession() {
		boolean bool = false;
		if (!bool) return null;
		return getMybatisSession("mybatis.cfg.xml");
	}
	/**
	 * 创建能执行映射文件中的sqlSession
	 * database/mybatis/ 目录下的文件 mybatis.cfg.xml
	 * @param mybatisFile String
	 * @return SqlSession
	 */
	public static SqlSession getMybatisSession(String mybatisFile) {
		try {
			Reader reader = Resources.getResourceAsReader("database/mybatis/"+mybatisFile);
			SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(reader);
			return factory.openSession();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 通过database目录下的不同的properties得到Connection，以配置文件名为标准
	 * @param propFilename String
	 * @return Connection
	 */
	public static final Connection getMYSQL(String propFilename) {
		Connection conn = null;
		if (propFilename == null) return conn;
		try {
			Properties properties = UtilsProperties.getProPropertiesTaskJar("/database/" + propFilename + ".properties");
			if (properties != null) {
				String driver = UtilsProperties.getProPropValue(properties, "jdbc.driver");
				String url = UtilsProperties.getProPropValue(properties, "jdbc.url");
				String dbName = UtilsProperties.getProPropValue(properties, "jdbc.user");
				String password = UtilsProperties.getProPropValue(properties, "jdbc.pass");
				Class.forName(driver);
				conn = DriverManager.getConnection(url, dbName, password);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

	/**
	 * 得到本地某个数据库的Connection
	 * @param database String
	 * @return Connection
	 */
	public static final Connection getMYSQLDEVLocalhost(String database) {
		return getMYSQL("127.0.0.1", "3306", database, "root", "123456");
	}
	/**
	 * 连接数据库，其它帐号与密码为 库名+user
	 * @param ip String
	 * @param port String
	 * @param database String
	 * @return Connection
	 */
	public static final Connection getMYSQL(String ip, String port, String database) {
		return getMYSQL(ip,port,database,database+"user",database+"user");
	}
	/**
	 * 得到本地某个数据库的Connection
	 * @param ip String
	 * @param port String
	 * @param database String
	 * @param username String
	 * @param userpass String
	 * @return Connection
	 */
	public static final Connection getMYSQL(String ip, String port, String database, String username, String userpass) {
		Connection conn = null;
		try {
			Class.forName(MYSQLDRIVER);
			String url = "jdbc:mysql://" + ip + ":" + port + "/" + database + "?useUnicode=true&useSSL=false&characterEncoding=utf8&serverTimezone=GMT&autoReconnect=true";
			conn = DriverManager.getConnection(url, username, userpass);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}
	/** mysql的驱动 */
	private static final String MYSQLDRIVER = "com.mysql.jdbc.Driver";
}
