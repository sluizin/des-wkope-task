package des.wangku.operate.standard.utls;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

import des.wangku.operate.standard.database.DatabaseProperties;


/**
 * 读取配置信息
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class UtilsProperties {
	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(UtilsProperties.class);
	/**
	 * 从关键字中提取对象
	 * @param properties Properties
	 * @param clazz Class&lt;T&gt;
	 * @param key String
	 * @param <T> T
	 * @return T
	 */
	public static final <T> T getPropValueObject(Properties properties, Class<T> clazz,String key){
		String value=UtilsProperties.getProPropValue(properties, key);
		if(value==null) return null;
		return JSON.parseObject(value, clazz);
	}
	/**
	 * 从关键字中提取对象集合
	 * @param properties Properties
	 * @param clazz Class&lt;T&gt;
	 * @param key String
	 * @param <T> T
	 * @return List&lt;T&gt;
	 */
	public static <T> List<T> getPropValueList(Properties properties, Class<T> clazz,String key){
		String value=UtilsProperties.getProPropValue(properties, key);
		if(value==null)return new ArrayList<>();
		return JSON.parseArray(value, clazz);
	}

	/**
	 * 通过indexof(key)查找属性值，得到列表
	 * @param properties Properties
	 * @param isHead boolean
	 * @param keys String[]
	 * @return List&lt;String&gt;
	 */
	public static List<String> getPropMultiIndexofValue(Properties properties, boolean isHead, String... keys) {
		List<String> list = new ArrayList<>();
		if (properties == null || keys == null || keys.length == 0) return list;
		for (String value : properties.stringPropertyNames()) {
			for (String key : keys) {
				int p = value.indexOf(key);
				if (p == -1) continue;
				if ((isHead && p == 0) || (!isHead)) {
					list.add(properties.getProperty(value));
					break;
				}
			}
		}
		return list;
	}

	/**
	 * 从配置读取字符型数组 如: arr = "abc","ab,c","a,bc"
	 * @param properties Properties
	 * @param key String
	 * @param def String[]
	 * @return String[]
	 */
	public static final String[] getProPropArrayString(Properties properties, String key, String... def) {
		if (properties == null || key == null) return def;
		String value = getProPropValue(properties, key);
		if (value == null) return def;
		String[] arr = {};
		if (value.trim().length() == 0) return arr;
		return getArray(value);
	}

	/**
	 * 从配置读取字符型数组 如: arr = "abc","ab,c","a,bc"
	 * @param properties Properties
	 * @param key String
	 * @return String[]
	 */
	public static final String[] getProPropArrayString(Properties properties, String key) {
		String[] arr = {};
		if (properties == null || key == null) return arr;
		String value = getProPropValue(properties, key);
		if (value == null || value.trim().length() == 0) return arr;
		return getArray(value);
	}

	/**
	 * 字符串转成字符串数组，以,分隔
	 * @param value String
	 * @return String[]
	 */
	private static final String[] getArray(String value) {
		String[] arr = {};
		List<String> list = JSON.parseArray("[" + value + "]", String.class);
		return list.toArray(arr);

	}

	/**
	 * 从配置读取数值型数组
	 * @param properties Properties
	 * @param key String
	 * @param def int[]
	 * @return int[]
	 */
	public static final int[] getProPropArrayInteger(Properties properties, String key, int... def) {
		if (properties == null || key == null) return def;
		String value = getProPropValue(properties, key);
		if (value == null) return def;
		int[] arr = {};
		if (value.trim().length() == 0) return arr;
		return getArrayInt(value);

	}

	/**
	 * 从配置读取数值型数组
	 * @param properties Properties
	 * @param key String
	 * @return int[]
	 */
	public static final int[] getProPropArrayInteger(Properties properties, String key) {
		int[] arr = {};
		if (properties == null || key == null) return arr;
		String value = getProPropValue(properties, key);
		if (value == null || value.trim().length() == 0) return arr;
		return getArrayInt(value);
	}

	/**
	 * 字符串转成数值型数组
	 * @param value String
	 * @return int[]
	 */
	private static final int[] getArrayInt(String value) {
		String[] arrs = value.split(",");
		List<Integer> list = new ArrayList<>(arrs.length);
		for (int i = 0; i < arrs.length; i++) {
			String v = arrs[i].trim();
			list.add(Integer.parseInt(v));
		}
		int[] d = new int[list.size()];
		for (int i = 0; i < list.size(); i++)
			d[i] = list.get(i);
		return d;
	}

	/**
	 * 得到模块参数值内容<br>
	 * 如为真:on/true<br>
	 * 如为假:off/false<br>
	 * key为null时返回 def
	 * @param properties Properties
	 * @param key String
	 * @param def boolean
	 * @return boolean
	 */
	public static final boolean getProPropBoolean(Properties properties, String key, boolean def) {
		if (properties == null || key == null) return def;
		String value = getProPropValue(properties, key);
		if (value == null) return def;
		try {
			value=value.toLowerCase();
			if(value.equals("on"))return true;
			if(value.equals("off"))return false;
			return Boolean.parseBoolean(value);
		} catch (Exception e) {
			return def;
		}
	}

	/**
	 * 得到模块参数值内容<br>
	 * key为null时返回 false
	 * @param properties Properties
	 * @param key String
	 * @return boolean
	 */
	public static final boolean getProPropBoolean(Properties properties, String key) {
		if (properties == null || key == null) return false;
		String value = getProPropValue(properties, key);
		if (value == null) return false;
		try {
			return Boolean.parseBoolean(value);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 得到模块参数值内容<br>
	 * key为null时返回 def
	 * @param properties Properties
	 * @param key String
	 * @param def int
	 * @return int
	 */
	public static final int getProPropInteger(Properties properties, String key, int def) {
		if (properties == null || key == null) return def;
		String value = getProPropValue(properties, key);
		if (value == null) return def;
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return def;
		}
	}

	/**
	 * 得到模块参数值内容<br>
	 * key为null时返回 -1
	 * @param properties Properties
	 * @param key String
	 * @return int
	 */
	public static final int getProPropInteger(Properties properties, String key) {
		if (properties == null || key == null) return -1;
		String value = getProPropValue(properties, key);
		if (value == null) return -1;
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	/**
	 * 得到模块参数值内容<br>
	 * key为null时返回 0
	 * @param properties Properties
	 * @param key String
	 * @return long
	 */
	public static final long getProPropLong(Properties properties, String key) {
		if (properties == null || key == null) return 0;
		String value = getProPropValue(properties, key);
		if (value == null) return 0;
		try {
			return Long.parseLong(value);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	/**
	 * 得到模块参数值内容<br>
	 * key为null时返回 def
	 * @param properties Properties
	 * @param key String
	 * @param def long
	 * @return long
	 */
	public static final long getProPropLong(Properties properties, String key, long def) {
		if (properties == null || key == null) return def;
		String value = getProPropValue(properties, key);
		if (value == null) return def;
		try {
			return Long.parseLong(value);
		} catch (NumberFormatException e) {
			return def;
		}
	}

	/**
	 * 得到模块参数值内容<br>
	 * key为null时返回def
	 * @param properties Properties
	 * @param key String
	 * @param def String
	 * @return String
	 */
	public static final String getProPropValue(Properties properties, String key, String def) {
		if (properties == null || key == null) return def;
		String value = getProPropValue(properties, key);
		return (value == null)?def:value;
	}
	/**
	 * 判断配置信息文件中是否含有关键的值
	 * @param properties Properties
	 * @param key String
	 * @return boolean
	 */
	public static final boolean isExistProperties(Properties properties, String key) {
		if (properties == null || key == null) return false;		
		return properties.containsKey(key);
	}

	/**
	 * 得到模块参数值内容<br>
	 * key为null时返回null
	 * @param properties Properties
	 * @param key String
	 * @return String
	 */
	public static final String getProPropValue(Properties properties, String key) {
		if (properties == null || key == null) return null;
		String value=properties.getProperty(key);
		if(value == null) return null;
		return value.trim();
	}

	/**
	 * 判断配置信息中是否含有keywords的key
	 * @param properties Properties
	 * @param keywords String[]
	 * @return boolean
	 */
	public static final boolean isIndexOfKey(Properties properties, String... keywords) {
		Set<Object> keys = properties.keySet();//返回属性key的集合
		for (Object key : keys) {
			if (key == null) continue;
			String value = key.toString();
			for (int i = 0; i < keywords.length; i++)
				if (value.indexOf(keywords[i]) > -1) return true;
		}
		return false;
	}
	/**
	 * 从filename中提取Properties，如果失败，则返回false
	 * @param properties Properties
	 * @param filename String
	 * @return boolean
	 */
	public static final boolean loadProperties(Properties properties,String filename) {
		File file = new File(filename);
		if (!file.exists()) {
			logger.debug("未发现配置文件:" + filename);
			return false;
		}
		if (!file.isFile()) return false;
		try (InputStream is2 = new FileInputStream(file); InputStream in = new BufferedInputStream(is2); InputStreamReader isr = new InputStreamReader(in, "UTF-8");) {
			properties.load(isr);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * 从Taskjar中读取配置文件 用于保护配置信息，直接封装到exe文件中
	 * @param filename String
	 * @return Properties
	 */
	public static final Properties getProPropertiesTaskJar(String filename) {
		Properties properties = new Properties();
		try {
			InputStream is2 = UtilsJar.getJarInputStreamBase(DatabaseProperties.class, filename);
			properties.load(new InputStreamReader(is2, "UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return properties;
	}
}
