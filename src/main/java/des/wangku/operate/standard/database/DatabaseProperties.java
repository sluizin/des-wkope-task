package des.wangku.operate.standard.database;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import des.wangku.operate.standard.utls.UtilsJar;
/**
 * 读取配置文件，权限本地jar包含的配置信息
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 *
 */
public class DatabaseProperties {
	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(DatabaseProperties.class);
	/**
	 * 
	 * @param filename String
	 * @return Properties
	 */
	public static final Properties getProProperties(String filename) {
		Properties properties = new Properties();
		try {
			InputStream is2=UtilsJar.getJarInputStreamBase(DatabaseProperties.class, filename);
			properties.load(new InputStreamReader(is2, "UTF-8"));
		} catch (IOException e) {
			logger.debug("未发现配置文件，或读取出现错误");
			e.printStackTrace();
		}
		return properties;
	}


}
