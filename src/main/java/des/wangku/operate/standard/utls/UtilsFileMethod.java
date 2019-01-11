package des.wangku.operate.standard.utls;

import java.io.File;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;

/**
 * 文件的一些方法
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class UtilsFileMethod {
	/** 日志 */
	static final Logger logger = LoggerFactory.getLogger(UtilsFileMethod.class);

	/**
	 * 通过文件得到文件全称
	 * @param file File
	 * @return String
	 */
	public static final String getFileName(File file) {
		if (file == null) return null;
		return getFileName(file.getAbsolutePath());
	}
	/**
	 * 通过文件的绝对地址得到文件全称
	 * @param filename String
	 * @return String
	 */
	public static final String getFileName(String filename) {
		int p = filename.lastIndexOf('\\');
		int p1 = filename.lastIndexOf('/');
		if (p1 > p) p = p1;
		if (p == -1) return null;
		return filename.substring(p + 1);
	}
	/**
	 * 得到文件名右侧字符串 如: abc_123.xlx 输出 123<br>
	 * 先移除扩展名，再按mid查最后一个位置，再得到右侧字符串
	 * @param file String
	 * @param mid String
	 * @return String
	 */
	public static final String getFileNameRight(String file, String mid) {
		int p = file.lastIndexOf(".");
		if (p > -1) file = file.substring(0, p);
		int index = file.lastIndexOf(mid);
		if (index == -1) return null;
		return file.substring(index + 1);
	}

}
