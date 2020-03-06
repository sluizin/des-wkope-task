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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import des.wangku.operate.standard.Pv;
import des.wangku.operate.standard.Pv.Env;

/**
 * 关于路径与文件的工具
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class UtilsPathFile {
	static Logger logger = LoggerFactory.getLogger(UtilsPathFile.class);

	/**
	 * 从指定目录中查找出目录名左侧关键字的File 只返回一个File
	 * @param path String
	 * @param leftKey String
	 * @param isDeep boolean
	 * @return File
	 */
	public static final File getCatalogNameLeftFile(String path, String leftKey, boolean isDeep) {
		if (leftKey == null) return null;
		File or = new File(path);
		File[] files = or.listFiles();
		if (files == null || files.length == 0) return null;
		for (File file : files) {
			if (!file.isDirectory()) continue;
			if (file.getName().indexOf(leftKey) == 0) return file;
		}
		/* 如果同级没有发现，可以考虑深层查找 */
		if (!isDeep) return null;
		for (File file : files) {
			if (!file.isDirectory()) continue;
			File t = getCatalogNameLeftFile(file.getAbsolutePath(), leftKey, isDeep);
			if (t != null) return t;
		}
		return null;
	}

	/**
	 * 从指定目录中查找出目录名左侧关键字的目录名 返回null，则没有找到
	 * @param path String
	 * @param leftKey String
	 * @param isDeep boolean
	 * @return String
	 */
	public static final String getCatalogNameLeftFilename(String path, String leftKey, boolean isDeep) {
		File file=getCatalogNameLeftFile(path,leftKey,isDeep);
		if(file==null)return null;
		return file.getName();
	}

	/**
	 * 得到某个目录里所有的文件
	 * @param list List &lt; String &gt;
	 * @param path String
	 */
	public static final void getFileList(List<String> list, String path) {
		File or = new File(path);
		File[] files = or.listFiles();
		if (files == null) return;
		for (File file : files) {
			if (file.isFile()) list.add(file.getAbsolutePath());
			if (file.isDirectory()) getFileList(list, file.getAbsolutePath());
		}
	}
	/**
	 * 得到某个目录里所有的jar文件
	 * @param path String
	 * @param fileExt String
	 * @return List&lt;String&gt;
	 */
	public static final List<String> getFileList(String path, String fileExt) {
		List<String> list = new ArrayList<String>();
		getFileList(list, path,fileExt);
		return list;
	}
	/**
	 * 得到某个目录里所有的扩展名文件
	 * @param jarList List &lt; String &gt;
	 * @param path String
	 * @param fileExt String
	 */
	public static final void getFileList(List<String> jarList, String path, String fileExt) {
		if(path==null)return;
		File file = new File(path);
		if(!file.exists())return;
		if(!file.isDirectory())return;
		File[] files = file.listFiles();
		if (files == null) return;
		for (File f : files) {
			if (f.isFile() && f.getName().endsWith("." + fileExt)) jarList.add(f.getAbsolutePath());
			if (f.isDirectory()) getFileList(jarList, f.getAbsolutePath(), fileExt);
		}
	}

	/**
	 * 得到某个目录里所有的文件中的第一个 indexof()==0 如没找到，则返回空list<br>
	 * 按名称进行了排序
	 * @param leftkeyword String
	 * @param path String
	 * @return List&lt;String&gt;
	 */
	public static final List<String> getFileslistSort(String leftkeyword, String path) {
		List<String> fileslist = new ArrayList<>();
		getFilesNameList(fileslist, leftkeyword, path);
		if (fileslist.size() == 0) return fileslist;
		Collections.sort(fileslist, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});
		return fileslist;
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
		List<String> fileslist = getFileslistSort(leftkeyword, path);
		if (fileslist.size() == 0 || index < 0 || index >= fileslist.size()) return null;
		return fileslist.get(index);
	}

	/**
	 * 得到某个目录里所有的文件中的第一个 indexof()==0 如没找到，则返回null<br>
	 * 按名称进行了排序
	 * @param leftkeyword String
	 * @param path String
	 * @return String
	 */
	public static final String getFilesNameFirst(String leftkeyword, String path) {
		List<String> fileslist = getFileslistSort(leftkeyword, path);
		if (fileslist.size() == 0) return null;
		return fileslist.get(0);
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
	 * 得到jar文件所在的目录
	 * @return String
	 */
	public static final String getJarBasicPath() {
		String filePath = System.getProperty("java.class.path");
		logger.debug("JarBasicPath :"+filePath);
		URL url = UtilsPathFile.class.getProtectionDomain().getCodeSource().getLocation();
		try {
			filePath = URLDecoder.decode(url.getPath(), "utf-8");// 转化为utf-8编码，支持中文
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (filePath.endsWith(".jar")) filePath = filePath.substring(0, filePath.lastIndexOf("/") + 1);
		File file = new File(filePath);
		filePath = file.getAbsolutePath();//得到windows下的正确路径
		logger.debug("JarBasicPath:"+filePath);
		return filePath;
	}

	/**
	 * 得到主项目目录中某个目录，如果是开发环境，则返回 devpath
	 * @param Catalog String
	 * @param devpath String
	 * @return String
	 */
	public static final String getJarBasicPathCatalog(String Catalog,String devpath) {
		if (Pv.ACC_ENV == Env.DEV) return devpath;
		URL c = UtilsPathFile.class.getClassLoader().getResource("");
		if (c == null) {
			String path=UtilsPathFile.getJarBasicPath() + "/"+Catalog;
			logger.debug(" UtilsPathFile.class.getClassLoader().getResource c.toURI().getPath():" + path);
			return path;
		}
		try {
			File file = new File(c.toURI().getPath());
			String filePath = file.getAbsolutePath();//得到windows下的正确路径
			logger.debug(" UtilsPathFile.class.getClassLoader().getResource c.toURI().getPath():" + filePath);
			return filePath + "/"+Catalog;
		} catch (URISyntaxException e) {
			return e.toString();
		}
	}

	/**
	 * 得到jar文件所在目录<br>
	 * 如本地eclipse运行时class文件，则:{constants.DEVWorkSpaceMainProject}\build\classes\main<br>
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
}
