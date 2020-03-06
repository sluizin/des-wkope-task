package des.wangku.operate.standard.utls;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 扩展jar系统
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class UtilsExtLibs {
	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(UtilsExtLibs.class);
	/**
	 * 判断jar文件是否有效，是否存在，是否为文件，是否可读
	 * @param filename String
	 * @return boolean
	 */
	public static final boolean isJarFileEffective(String filename) {
		if (filename == null) return false;
		if (!filename.endsWith(".jar")) return false;
		File file = new File(filename);
		if ((!file.exists()) || (!file.isFile())) return false;
		if(!file.canRead())return false;
		return true;
	}
	/**
	 * 扩展某个jar目录
	 * @param filename String
	 */
	public static final void addSystemExtLibsFile(String filename) {
		if(!isJarFileEffective(filename))return;
		String path = System.getProperty("java.library.path");
		if (path == null) path = "";
		logger.debug("java.library.path:"+path);
		StringBuilder sb = new StringBuilder(path.length()+20);
		sb.append(path);
		if (!path.endsWith(";")) sb.append(";");
		sb.append(filename);
		sb.append(';');
		logger.debug("java.library.path:"+sb.toString());
		System.setProperty("java.library.path", sb.toString());
	}

	/**
	 * 扩展某个jar文件
	 * @param catalog String
	 */
	public static final void addSystemExtLibsCatalog(String catalog) {
		List<String> jarList = UtilsExtLibs.getJarList(catalog);
		for(String e:jarList) {
			addSystemExtLibsFile(e);
		}
	}

	/**
	 * 得到某个目录里所有的jar文件
	 * @param path String
	 * @return List&lt;String&gt;
	 */
	public static final List<String> getJarList(String path) {
		List<String> jarList = new ArrayList<String>();
		UtilsPathFile.getFileList(jarList, path,"jar");
		return jarList;
	}
}
