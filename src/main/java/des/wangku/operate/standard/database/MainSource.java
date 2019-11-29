package des.wangku.operate.standard.database;

import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
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
public class MainSource {
	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(MainSource.class);
	public static SqlSessionFactory sessionFactory;
	static final boolean booleanLinkMysql = false;
	static {
		//使用MyBatis提供的Resources类加载mybatis的配置文件
		if (booleanLinkMysql) {
			try {
				Reader reader = Resources.getResourceAsReader("mybatis/mybatis.cfg.xml");
				//构建sqlSession的工厂
				sessionFactory = new SqlSessionFactoryBuilder().build(reader);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 创建能执行映射文件中sql的sqlSession
	 * @return SqlSession
	 */
	public static SqlSession getSession() {
		if (booleanLinkMysql) return sessionFactory.openSession();
		return null;
	}

	/**
	 * 绩效系统的mysql
	 * @return Connection
	 */
	public static final Connection getConnWKjixiao() {
		Connection conn = null;
		try {
			Properties properties = UtilsProperties.getProPropertiesTaskJar("/database/jixiaowk.properties");
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
	 *通过database目录下的不同的properties得到Connection，以配置文件名为标准
	 * @param propFile String
	 * @return Connection
	 */
	public static final Connection getConnection(String propFile) {
		Connection conn = null;
		if (propFile == null) return conn;
		try {
			Properties properties = UtilsProperties.getProPropertiesTaskJar("/database/" + propFile + ".properties");
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
	public static void main(String[] args) 
	{
		String sql=getS("11884","2018-12-1","2018-12-14");//"select count(*) from article ";
		//Connection conn=getConnection("wk_info");
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String url="jdbc:mysql://192.168.6.146:3306/wk_info?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT&autoReconnect=true";
			String dbName="rostat";
			String password="rostat-0409";
			Connection conn = DriverManager.getConnection(url, dbName, password);
		if(conn!=null) {
				Statement state=conn.createStatement();
				ResultSet rs = state.executeQuery(sql);
	            while (rs.next()) {
	            	System.out.println("sort:"+rs.getInt(1));
	            }
				conn.close();
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Hello World!");
	}
	public static final String getS(String stie_id,String date1,String date2) {
		String sql="select count(*) from article where site_id="+stie_id+" and (datediff(add_time,'"+date1+" 00:00:00')>=0 and datediff('"+date2+" 23:59:59',add_time)>=0)";
		System.out.println("sql:"+sql);
		return sql;
	}
}
