package des.wangku.operate.standard.utls;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
	 * 得到某个目录里所有的文件 indexof()==0<br>
	 * 按名称进行排序
	 * @param list List &lt; String &gt;
	 * @param leftkeyword String
	 * @param path String
	 */
	public static final void getFilesNameList(List<String> list, String leftkeyword, String path) {
		File or = new File(path);
		File[] files = or.listFiles();
		if (files == null) return;
		Arrays.sort(files, new Comparator<File>() {
			@Override
			public int compare(File o1, File o2) {
				if (o1.isDirectory() && o2.isFile()) return -1;
				if (o1.isFile() && o2.isDirectory()) return 1;
				return o1.getName().compareTo(o2.getName());
			}
		});
		for (File file : files) {
			if (file.isFile() && file.getName().indexOf(leftkeyword) == 0) list.add(file.getAbsolutePath());
			if (file.isDirectory()) getFilesNameList(list, leftkeyword, file.getAbsolutePath());
		}
	}

	/**
	 * 得到某个目录里所有的文件中的第一个 indexof()==0 如没找到，则返回null<br>
	 * 按名称进行了排序
	 * @param leftkeyword String
	 * @param path String
	 * @return String
	 */
	public static final String getFilesNameFirst(String leftkeyword, String path) {
		List<String> fileslist = new ArrayList<>();
		getFilesNameList(fileslist, leftkeyword, path);
		if (fileslist.size() == 0) return null;
		Collections.sort(fileslist, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});
		return fileslist.get(0);
	}

	/**
	 * 得到某个目录里所有的文件中的第N个 indexof()==0 如没找到，则返回null<br>
	 * 按名称进行了排序
	 * @param leftkeyword String
	 * @param path String
	 * @param index int
	 * @return String
	 */
	public static final String getFilesName(String leftkeyword, String path, int index) {
		List<String> fileslist = new ArrayList<>();
		getFilesNameList(fileslist, leftkeyword, path);
		if (fileslist.size() == 0 || index < 0 || index >= fileslist.size()) return null;
		Collections.sort(fileslist, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});
		return fileslist.get(index);
	}

	/**
	 * 得到某个目录里所有的扩展名文件
	 * @param jarList List &lt; String &gt;
	 * @param path String
	 * @param fileExt String
	 */
	public static final void getFileList(List<String> jarList, String path,String fileExt) {
		File or = new File(path);
		File[] files = or.listFiles();
		if (files == null) return;
		for (File file : files) {
			if (file.isFile() && file.getName().endsWith("."+fileExt)) jarList.add(file.getAbsolutePath());
			if (file.isDirectory()) getFileList(jarList, file.getAbsolutePath(),fileExt);
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
		//logger.debug(" UtilsPathFile.class.getClassLoader().getResource:" + c);
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
