package des.wangku.operate.standard.utls;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
/**
 * 针对jar包的操作的工具类
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 *
 */
public final class UtilsJar {
	/**
	 * 得到当前jar包中的资源输出
	 * @param clazz Class
	 * @param filename String
	 * @return  InputStream
	 */
	public static InputStream getJarInputStreamBase(Class<?> clazz, String filename) {
		try {
			InputStream is=clazz.getResourceAsStream(filename);
			return is;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 从clazz所在的jar包中定位某个资源文件，并输出URL
	 * @param clazz class ?
	 * @param filename String
	 * @return URL
	 */
	public static URL getJarSourceURL(Class<?> clazz, String filename) {
		URL url = clazz.getResource("");
		if (url == null) return null;
		String urlpath = url.toString();
		if (urlpath.indexOf('!') == -1) return null;
		String newpath = urlpath.substring(0, urlpath.indexOf('!') + 1) + filename;
		/*String urlstr="jar:file:Constants.WorkSpaceLib/model/des-wkope-task-aizhan-1.2.jar!/update.info";*/
		try {
			return new URL(newpath);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
