package des.wangku.operate.standard.utls;

import java.io.IOException;
import java.net.URL;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 针对json方法的扩展
 * 
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class UtilsJsoupExt {

	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(UtilsJsoupExt.class);

	public static final int MODE_Jsoup = 1;

	public static final int MODE_Socket = 2;

	public static final int MODE_URL = 4;
	/**
	 * 判断Element是否为空<br>
	 * 以html()以字符串，即展示出来代码进行多次过滤的是空
	 * @param e Element
	 * @return boolean
	 */
	public static final boolean isEmptyHtml(Element e) {
		if (e == null) return true;
		String result = e.html().trim();
		result = UtilsHtmlFilter.filter(result);
		return result.length() != 0;
	}
	/**
	 * 判断Element是否为空<br>
	 * 以text()以字符串，即展示出来的是空
	 * @param e Element
	 * @return boolean
	 */
	public static final boolean isEmptyText(Element e) {
		if (e == null) return true;
		String result = e.text().trim();
		result = UtilsHtmlFilter.filter(result);/* 过滤掉多余的特殊字符*/
		return result.length() != 0;
	}

	/**
	 * 移除所有text为空的区块
	 * @param source Element
	 * @return Element
	 */
	public static final Element remove_TextEmptyAll(Element source) {
		return remove_TextEmptyAll(source, false);
	}

	/**
	 * 移除所有text为空的区块<br>
	 * 是否过滤所有&lt;&gt;标记
	 * @param source Element
	 * @param filterTags boolean
	 * @return Element
	 */
	public static final Element remove_TextEmptyAll(Element source, boolean filterTags) {
		Elements childen = source.getAllElements();
		for (Element e : childen) {
			if (e == null) continue;
			String result = UtilsHtmlFilter.filterHtmlSymbol(e.html()).trim();
			if (result.length() == 0) e.remove();
		}
		return source;
	}
	/**
	 * 移除所有text为空的子类区块
	 * @param source Element
	 * @return Element
	 */
	public static final Element remove_TextEmptyChild(Element source) {
		Elements childen = source.children();
		for (Element e : childen) {
			if (e.text().trim().length() == 0) e.remove();
		}
		return source;
	}

	/**
	 * 提取jsoup &gt; Document socket&gt;URL 全部信息
	 * @param url String
	 * @return Document
	 */
	public static final Document getDoc(String url) {
		if (UtilsConsts.isErrorUrl(url)) return null;
		try {
			URL url1 = new URL(url);
			return getDoc(url1);
		} catch (IOException e) {
			logger.debug("error:" + url);
			//e.printStackTrace();
		}
		return null;
	}

	/**
	 * 提取jsoup [1(MODE_Jsoup)] &gt; Document socket[2(MODE_Socket)] &gt; URL[4(MODE_URL)] 全部信息
	 * @param url String
	 * @param mode int
	 * @return Document
	 */
	public static final Document getDoc(String url, int mode) {
		if (UtilsConsts.isErrorUrl(url)) return null;
		try {
			URL url1 = new URL(url);
			return getDoc(url1, mode);
		} catch (IOException e) {
			logger.debug("error:" + url);
			//e.printStackTrace();
		}
		return null;
	}

	/**
	 * 从网址中某个长度的局部返回对象
	 * @param url String
	 * @param start String
	 * @param end String
	 * @return Document
	 */
	public static final Document getDoc(String url, String start, String end) {
		if (UtilsConsts.isErrorUrl(url)) return null;
		Document doc = getDoc(url);
		String head = UtilsReadURL.getUrlDomain(url);
		return getRebuildDoc(head, doc, start, end);
	}

	/**
	 * 从网址中某个长度的局部返回对象
	 * @param domain String
	 * @param doc Document
	 * @param start String
	 * @param end String
	 * @return Document
	 */
	public static final Document getRebuildDoc(String domain, Document doc, String start, String end) {
		if (doc == null) return null;
		String result = doc.html();
		String cut = UtilsString.cutString(result, start, end);
		if (cut == null) return null;
		return UtilsJsoupCase.getDocument(domain, cut);
	}
	/**
	 * 提取jsoup &gt; Document socket&gt;URL 全部信息
	 * @param url URL
	 * @return Document
	 */
	public static final Document getDoc(URL url) {
		if (url == null) return null;
		return getDoc(url, MODE_Jsoup + MODE_Socket + MODE_URL);
	}
	public static boolean isDynamicUrl = false;

	/**
	 * 重组日志内容
	 * @param url URL
	 * @param type String
	 * @param timeout int
	 * @return String
	 */
	private static final String getMessage(URL url, String type, int timeout) {
		return "[" + timeout + "]读取网址方式: " + type + "!\t" + url.toString();
	}

	/**
	 * 提取jsoup [1(MODE_Jsoup)] &gt; Document socket[2(MODE_Socket)] &gt; URL[4(MODE_URL)] 全部信息
	 * @param url URL
	 * @param mode int
	 * @return Document
	 */
	public static final Document getDoc(URL url, int mode) {
		if (UtilsConsts.isErrorUrl(url)) return null;
		if (isDynamicUrl) return getDocJS(url);
		int sleep = UtilsConsts.getSheepTime(url);
		return getDoc(url, null, sleep, false, mode, 30000);
	}

	/**
	 * 提取jsoup [1(MODE_Jsoup)] &gt; Document socket[2(MODE_Socket)] &gt; URL[4(MODE_URL)] 全部信息<br>
	 * sleetp 暂停时间:是否需要线程暂停多少毫秒，小于等于0时，则不暂停<br>
	 * istest 是否读取测试<br>
	 * @param url URL
	 * @param newCode String
	 * @param sleep int
	 * @param istest boolean
	 * @param mode int
	 * @param timeout int
	 * @return Document
	 */
	public static final Document getDoc(URL url, String newCode, int sleep, boolean istest, int mode, int timeout) {
		if (url == null) return null;
		if (UtilsConsts.isErrorUrl(url)) return null;
		if (sleep > 0) UtilsThread.ThreadSleep(sleep);
		if (istest && !UtilsReadURL.isConnection(url)) return null;
		if (UtilsShiftCompare.isCompare(mode, MODE_Jsoup)) {
			logger.debug(getMessage(url, "Jsoup", timeout));
			try {
				Connection connect = Jsoup.connect(url.toString()).headers(UtilsConsts.getRndHeadMap());//UtilsConsts.header_a);
				Document document = connect.timeout(timeout).maxBodySize(0).get();
				if (document != null) return document;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (newCode == null || newCode.length() == 0) newCode = UtilsJsoupCase.getCode(url);
		String domain = UtilsReadURL.getUrlDomain(url);
		if (UtilsShiftCompare.isCompare(mode, MODE_Socket)) {
			logger.debug(getMessage(url, "socket", timeout));
			String content = UtilsReadURL.getSocketContent(url, newCode, timeout);
			if (content != null && content.length() > 0) { return UtilsJsoupCase.getDocument(domain, content); }
		}
		if (UtilsShiftCompare.isCompare(mode, MODE_URL)) {
			logger.debug(getMessage(url, "UrlRead", timeout));
			String content = UtilsReadURL.getUrlContent(url, newCode, timeout);
			if (content != null && content.length() > 0) return UtilsJsoupCase.getDocument(domain, content);
		}
		return null;
	}

	/**
	 * 提取全部信息 运行JS
	 * @param url String
	 * @return Document
	 */
	public static final Document getDocJS(String url) {
		if (UtilsConsts.isErrorUrl(url)) return null;
		try {
			URL url1 = new URL(url);
			return getDocJS(url1);
		} catch (IOException e) {
			logger.debug("error:" + url);
			//e.printStackTrace();
		}
		return null;
	}

	/**
	 * 提取全部信息 运行JS
	 * @param url URL
	 * @return Document
	 */
	public static final Document getDocJS(URL url) {
		if (UtilsConsts.isErrorUrl(url)) return null;
		String content = UtilsReadURL.getReadUrlJsDefault(url.toString());
		String domain = UtilsReadURL.getUrlDomain(url);
		return UtilsJsoupCase.getDocument(domain, content);
	}

}
