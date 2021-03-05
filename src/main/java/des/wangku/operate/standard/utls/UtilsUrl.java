package des.wangku.operate.standard.utls;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import des.wangku.operate.standard.Pv;
/**
 * 针对url的一些基本操作
 * 
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class UtilsUrl {

	static final List<String> BadUrlKeyList = new ArrayList<>();
	static {
		BadUrlKeyList.add("http://www.google.com.hk");
		BadUrlKeyList.add("http://www.google.com");
	}

	/**
	 * 判断域名是否含有错误的关键字 null返回true
	 * @param url String
	 * @return boolean
	 */
	public static final boolean isBadUrl(String url) {
		if (url == null) return true;
		for (String e : BadUrlKeyList)
			if (url.indexOf(e) > -1) return true;
		return false;
	}

	/**
	 * 判断域名是否含有错误的关键字 null返回true
	 * @param url URL
	 * @return boolean
	 */
	public static final boolean isBadUrl(URL url) {
		if (url == null) return true;
		String u = url.toString();
		return isBadUrl(u);
	}

	/**
	 * 测试url
	 * @param url URL
	 * @return boolean
	 */
	public static final boolean isErrorUrl(URL url) {
		if (isBadUrl(url)) return true;
		return false;
	}

	/**
	 * 测试url
	 * @param url String
	 * @return boolean
	 */
	public static final boolean isErrorUrl(String url) {
		if (isBadUrl(url)) return true;
		return false;
	}

	/**
	 * 得到读取url时的间隔时间，特殊url，则需要间隔时间自定义
	 * @param url URL
	 * @return int
	 */
	public static final int getSheepTime(URL url) {
		if (url == null) return 0;
		int sleep = 0;
		String domain = url.getHost();
		/*
		 * 如果域名为百度，则人为设置等待时间 对外为 30000
		 * 本地测试为2秒
		 */
		if (domain.indexOf("baidu.com") > -1) {
			if (Pv.isBasicENV()) sleep = 200;
			else sleep = 30000;
		}
		return sleep;
	}
	public static void main(String[] args) {
		System.out.println("Hello World!:" + isBadUrl("http://www.google.com"));
		try {
			URL url = new URL("http://www.sina.com/abc/1.html");
			System.out.println("Hello World!:" + isBadUrl(url));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
}
