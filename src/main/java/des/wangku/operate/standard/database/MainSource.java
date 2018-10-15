package des.wangku.operate.standard.database;

import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.Logger;

import des.wangku.operate.standard.task.AbstractTask;
import des.wangku.operate.standard.utls.UtilsProperties;

/**
 * 数据源 一些连接数据库的资源
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class MainSource {
	/** 日志 */
	static Logger logger = Logger.getLogger(MainSource.class);
	public static SqlSessionFactory sessionFactory;
	static {
		try {
			//使用MyBatis提供的Resources类加载mybatis的配置文件
			Reader reader = Resources.getResourceAsReader("mybatis/mybatis.cfg.xml");
			//构建sqlSession的工厂
			sessionFactory = new SqlSessionFactoryBuilder().build(reader);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 创建能执行映射文件中sql的sqlSession
	 * @return SqlSession
	 */
	public static SqlSession getSession() {
		return sessionFactory.openSession();
	}

	/**
	 * 绩效系统的mysql
	 * @return Connection
	 */
	public static final Connection getConnWKjixiao() {
		Connection conn = null;
		try {
			Properties properties = AbstractTask.getProPropertiesTaskJar("/database/jixiaowk.properties");
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

}
