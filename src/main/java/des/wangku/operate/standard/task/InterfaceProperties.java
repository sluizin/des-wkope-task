package des.wangku.operate.standard.task;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import des.wangku.operate.standard.utls.UtilsProperties;

/**
 * 系统级配置信息的提取
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public interface InterfaceProperties {
	static Logger logger = LoggerFactory.getLogger(InterfaceProperties.class);

	/**
	 * 从包外读取配置文件<br>
	 * 默认为mode/des-wkope-task-p0X.properties<br>
	 * 支持中文
	 * @return Properties
	 */
	public default Properties getProProperties() {
		Properties properties = new Properties();
		Object obj = this;
		AbstractTask t = (AbstractTask) obj;
		String filename = t.getModelProjectFileLeft() + ".properties";
		File file = new File(filename);
		if (!file.exists()) {
			logger.debug("未发现配置文件:" + filename);
			return properties;
		}
		if (!file.isFile()) return properties;
		try (InputStream is2 = new FileInputStream(file); InputStream in = new BufferedInputStream(is2); InputStreamReader isr = new InputStreamReader(in, "UTF-8");) {
			properties.load(isr);
			/*
			 * isr.close();
			 * is2.close();
			 * for (String key : properties.stringPropertyNames()) {
			 * logger.debug(key + ":::::" + properties.getProperty(key));
			 * logger.debug( "::::"+key+":");
			 * }
			 */
		} catch (IOException e) {
			e.printStackTrace();
		}
		return properties;
	}

	/**
	 * 从配置读取字符型数组 如: arr = "abc","ab,c","a,bc"
	 * @param key String
	 * @param def String[]
	 * @return String[]
	 */
	public default String[] getProPropArrayString(String key, String... def) {
		return UtilsProperties.getProPropArrayString(getProProperties(), key, def);
	}

	/**
	 * 从配置读取字符型数组 如: arr = "abc","ab,c","a,bc"
	 * @param properties Properties
	 * @param key String
	 * @return String[]
	 */
	public default String[] getProPropArrayString(Properties properties, String key) {
		return UtilsProperties.getProPropArrayString(getProProperties(), key);
	}

	/**
	 * 从配置读取数值型数组
	 * @param key String
	 * @param def int[]
	 * @return int[]
	 */
	public default int[] getProPropArrayInteger(String key, int... def) {
		return UtilsProperties.getProPropArrayInteger(getProProperties(), key, def);

	}

	/**
	 * 从配置读取数值型数组
	 * @param key String
	 * @return int[]
	 */
	public default int[] getProPropArrayInteger(String key) {
		return UtilsProperties.getProPropArrayInteger(getProProperties(), key);
	}

	/**
	 * 得到模块参数值内容<br>
	 * key为null时返回 def
	 * @param key String
	 * @param def boolean
	 * @return boolean
	 */
	public default boolean getProPropBoolean(String key, boolean def) {
		return UtilsProperties.getProPropBoolean(getProProperties(), key, def);
	}

	/**
	 * 得到模块参数值内容<br>
	 * key为null时返回 false
	 * @param key String
	 * @return boolean
	 */
	public default boolean getProPropBoolean(String key) {
		return UtilsProperties.getProPropBoolean(getProProperties(), key);
	}

	/**
	 * 得到模块参数值内容<br>
	 * key为null时返回 def
	 * @param key String
	 * @param def long
	 * @return long
	 */
	public default long getProPropLong(String key, long def) {
		return UtilsProperties.getProPropLong(getProProperties(), key, def);
	}

	/**
	 * 得到模块参数值内容<br>
	 * key为null时返回 0
	 * @param key String
	 * @return long
	 */
	public default long getProPropLong(String key) {
		return UtilsProperties.getProPropLong(getProProperties(), key);
	}

	/**
	 * 得到模块参数值内容<br>
	 * key为null时返回 def
	 * @param key String
	 * @param def int
	 * @return int
	 */
	public default int getProPropInteger(String key, int def) {
		return UtilsProperties.getProPropInteger(getProProperties(), key, def);
	}

	/**
	 * 得到模块参数值内容<br>
	 * key为null时返回 -1
	 * @param key String
	 * @return int
	 */
	public default int getProPropInteger(String key) {
		return UtilsProperties.getProPropInteger(getProProperties(), key);
	}

	/**
	 * 得到模块参数值内容<br>
	 * key为null时返回def
	 * @param key String
	 * @param def String
	 * @return String
	 */
	public default String getProPropValue(String key, String def) {
		return UtilsProperties.getProPropValue(getProProperties(), key, def);
	}

	/**
	 * 得到模块参数值内容<br>
	 * key为null时返回null
	 * @param key String
	 * @return String
	 */
	public default String getProPropValue(String key) {
		return UtilsProperties.getProPropValue(getProProperties(), key);
	}

	/**
	 * 判断配置信息中是否含有keywords的key
	 * @param keywords String[]
	 * @return boolean
	 */
	public default boolean isIndexOfKey(String... keywords) {
		return UtilsProperties.isIndexOfKey(getProProperties(), keywords);
	}
}
