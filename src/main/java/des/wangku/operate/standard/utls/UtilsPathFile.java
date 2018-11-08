package des.wangku.operate.standard.utls;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.List;

import org.apache.log4j.Logger;
import des.wangku.operate.standard.PV;
import des.wangku.operate.standard.PV.Env;

/**
 * 关于路径与文件的工具
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class UtilsPathFile {
	static Logger logger = Logger.getLogger(UtilsPathFile.class);

	/**
	 * 得到jar文件所在目录<br>
	 * 如本地eclipse运行时class文件，则:D:\Eclipse\eclipse-oxygen\Workspaces\des-wkope\build\classes\main<br>
	 * 生成jar文件时，则显示所在目录
	 * @return String
	 */
	public static String getProjectPath() {
		java.net.URL url = UtilsPathFile.class.getProtectionDomain().getCodeSource().getLocation();
		String filePath = null;
		try {
			filePath = java.net.URLDecoder.decode(url.getPath(), "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (filePath.endsWith(".jar")) filePath = filePath.substring(0, filePath.lastIndexOf("/") + 1);
		java.io.File file = new java.io.File(filePath);
		return file.getAbsolutePath();
	}

	/**
	 * 得到jar文件所在的目录
	 * @return String
	 */
	public static final String getJarBasicPath() {
		String filePath = System.getProperty("java.class.path");
		URL url = UtilsPathFile.class.getProtectionDomain().getCodeSource().getLocation();
		try {
			filePath = URLDecoder.decode(url.getPath(), "utf-8");// 转化为utf-8编码，支持中文
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (filePath.endsWith(".jar")) filePath = filePath.substring(0, filePath.lastIndexOf("/") + 1);
		File file = new File(filePath);
		filePath = file.getAbsolutePath();//得到windows下的正确路径
		return filePath;
	}

	/**
	 * 得到某个目录里所有的文件 indexof()==0
	 * @param list List &lt; String &gt; 
	 * @param path String
	 */
	public static final void getFilesNameList(List<String> list,String leftkeyword, String path) {
		File or = new File(path);
		File[] files = or.listFiles();
		if (files == null) return;
		for (File file : files) {
			if (file.isFile() && file.getName().indexOf(leftkeyword)==0) list.add(file.getAbsolutePath());
			if (file.isDirectory()) getFilesNameList(list,leftkeyword, file.getAbsolutePath());
		}
	}
	
	/**
	 * 得到某个目录里所有的jar文件
	 * @param jarList List &lt; String &gt; 
	 * @param path String
	 */
	public static final void getJarList(List<String> jarList, String path) {
		File or = new File(path);
		File[] files = or.listFiles();
		if (files == null) return;
		for (File file : files) {
			if (file.isFile() && file.getName().endsWith(".jar")) jarList.add(file.getAbsolutePath());
			if (file.isDirectory()) getJarList(jarList, file.getAbsolutePath());
		}
	}

	/**
	 * 得到model绝对目录
	 * @return String
	 */
	public static final String getModelJarBasicPath() {
		if (PV.ACC_ENV == Env.DEV) return "D:/Eclipse/eclipse-oxygen/Workspaces/des-wkope/build/libs/model";
		URL c = UtilsPathFile.class.getClassLoader().getResource("");
		logger.debug(" UtilsPathFile.class.getClassLoader().getResource:" + c);
		if (c == null) return UtilsPathFile.getJarBasicPath() + "/model";
		try {
			File file = new File(c.toURI().getPath());
			String filePath = file.getAbsolutePath();//得到windows下的正确路径
			logger.debug(" UtilsPathFile.class.getClassLoader().getResource c.toURI().getPath():" + filePath);
			return filePath + "/model";
		} catch (URISyntaxException e) {
			return "";
		}
	}
}
