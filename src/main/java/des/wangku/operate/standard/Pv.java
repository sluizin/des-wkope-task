package des.wangku.operate.standard;

import java.io.File;
//import java.net.URISyntaxException;
//import java.net.URL;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import des.wangku.operate.standard.desktop.DesktopConst;
import des.wangku.operate.standard.utls.UtilsPathFile;

/**
 * 运行参数判断，比如环境是开发还是线上
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class Pv {
	/** 日志 */
	static final Logger logger = LoggerFactory.getLogger(Pv.class);
	/**
	 * Env.DEV/Env.TEST/Env.ONLINE<br>
	 * 默认Env.ONLINE
	 */
	public static Env ACC_ENV = Env.ONLINE;

	/**
	 * 初始化
	 */
	public static final void Initialization() {
		String env = System.getProperty("env");//getString("env");
		if (env == null) {
			logger.debug("env:null");
			ACC_ENV = Env.ONLINE;
			return;
		}
		if (env.equalsIgnoreCase("dev")) ACC_ENV = Env.DEV;
	}

	/**
	 * 通过关键字查找环境环境变量里的值，没有查到，则返回null<br>
	 * Environment &gt; Argument
	 * @param key String
	 * @return String
	 */
	public static final String getString(String key) {
		if (key == null || key.length() == 0) return null;
		String val = null;
		val = getVal(System.getenv().entrySet(), key);
		if (val != null) return val;
		val = getVal(System.getProperties().entrySet(), key);
		return val;

		/*
		 * for (Iterator<?> it = System.getenv().entrySet().iterator(); it.hasNext();) {
		 * Object obj = it.next();
		 * if ((val = getValue(obj.toString(), key)) != null) return val;
		 * }
		 * for (Iterator<?> it = System.getProperties().entrySet().iterator(); it.hasNext();) {
		 * Object obj = it.next();
		 * if ((val = getValue(obj.toString(), key)) != null) return val;
		 * }
		 * return null;
		 */
	}

	/**
	 * 从Set中查找关键字
	 * @param set Set&lt;Entry&lt;T, T&gt;&gt:
	 * @param key String
	 * @return String
	 */
	private static final <T> String getVal(Set<Entry<T, T>> set, String key) {
		String val = null;
		for (Iterator<?> it = set.iterator(); it.hasNext();) {
			if ((val = getValue(it.next().toString(), key)) != null) return val;
		}
		return null;
	}

	/**
	 * env=dev<br>
	 * 通过某个key得到value，没有发现key则返回null
	 * @param value String
	 * @param key String
	 * @return String
	 */
	private static final String getValue(String value, String key) {
		if (value == null || value.length() == 0 || value.indexOf('=') == -1) return null;
		if (key == null || key.length() == 0) return null;
		String[] arr = value.split("=");
		if (arr[0].equals(key)) return arr[1];
		return null;
	}

	/**
	 * 环境
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 */
	public static enum Env {
		/** 开发环境 */
		DEV("dev"), /** 线上环境 */ONLINE("online");
		private String title;

		/**
		 * 初始化
		 * @param title String
		 */
		Env(String title) {
			this.title = title;
		}

		/**
		 * 返回 title
		 * @return String
		 */
		public String title() {
			return title;
		}
	}

	/**
	 * 得到绝对目录 跟环境变量有关
	 * @return String
	 */
	public static final String getJarBasicPath() {
		if (Pv.ACC_ENV == Env.DEV) return DesktopConst.DEVWorkSpaceLib;

		return UtilsPathFile.getProjectBasicPath();
		/*
		URL c = Pv.class.getClassLoader().getResource("");
		logger.debug(" Config.class.getClassLoader().getResource:" + c);
		if (c == null) {
			logger.debug("c.toURI().getPath():" + UtilsPathFile.getJarBasicPath());
			return UtilsPathFile.getJarBasicPath() + "";
		}
		try {
			File file = new File(c.toURI().getPath());
			String filePath = file.getAbsolutePath();//得到windows下的正确路径
			logger.debug("c.toURI().getPath():" + filePath);
			return filePath;
		} catch (URISyntaxException e) {
			return "";
		}*/
	}

	/**
	 * 文件输出目录的详细地址<br>
	 * c:/XXX/XXXX/output
	 * @return String
	 */
	public static final String getOutpoutCatalog() {
		return getCatalog(DesktopConst.ACC_OutputCatalog);
	}

	/**
	 * 文件目录的详细地址<br>
	 * @param catalog String
	 * @return String
	 */
	public static final String getCatalog(String catalog) {
		String path = Pv.getJarBasicPath() + "/" + catalog;
		File file = new File(path);
		if (!file.exists()) file.mkdirs();
		return path;
	}
}
