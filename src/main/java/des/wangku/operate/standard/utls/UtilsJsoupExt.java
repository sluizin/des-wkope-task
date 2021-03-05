package des.wangku.operate.standard.utls;

import static des.wangku.operate.standard.utls.UtilsJsoupConst.ACC_JsoupRuleCutIntervalSplit;
import static des.wangku.operate.standard.utls.UtilsJsoupConst.isCutInterval;
import static des.wangku.operate.standard.utls.UtilsFile.ACC_ShowLogger_WriteFileInforLen;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 针对json方法的扩展
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class UtilsJsoupExt {

	
	public static boolean isDynamicUrl = false;

	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(UtilsJsoupExt.class);

	public static final int MODE_Jsoup = 1;

	public static final int MODE_Socket = 2;

	public static final int MODE_URL = 4;

	/**
	 * 以|To|进行区分切块
	 * @param e Document
	 * @param key String
	 * @return Element
	 */
	public static final Document getDocu(Document e, String key) {
		if (key == null || key.length() == 0) return null;
		if (!isCutInterval(key)) return null;
		String[] arrs = key.split(ACC_JsoupRuleCutIntervalSplit);
		Document doc = UtilsJsoupExt.getRebuildDoc(e.baseUri(), e, arrs[0], arrs[1]);
		if (doc == null) return null;
		return doc;
	}
	/**
	 * 提取jsoup &gt; Document socket&gt;URL 全部信息
	 * @param url String
	 * @return Document
	 */
	public static final Document getDoc(String url) {
		if (UtilsUrl.isErrorUrl(url)) return null;
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
		if (UtilsUrl.isErrorUrl(url)) return null;
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
		if (UtilsUrl.isErrorUrl(url)) return null;
		Document doc = getDoc(url);
		String head = UtilsReadURL.getUrlDomain(url);
		return getRebuildDoc(head, doc, start, end);
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

	/**
	 * 提取jsoup [1(MODE_Jsoup)] &gt; Document socket[2(MODE_Socket)] &gt; URL[4(MODE_URL)] 全部信息
	 * @param url URL
	 * @param mode int
	 * @return Document
	 */
	public static final Document getDoc(URL url, int mode) {
		if (UtilsUrl.isErrorUrl(url)) return null;
		if (isDynamicUrl) return getDocJS(url);
		int sleep = UtilsUrl.getSheepTime(url);
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
	public static final Document getDoc(URL url,String newCode, int sleep, boolean istest, int mode, int timeout) {
		if (url == null) return null;
		if (UtilsUrl.isErrorUrl(url)) return null;
		if (sleep > 0) UtilsThread.ThreadSleep(sleep);
		if (istest && !UtilsReadURL.isConnection(url)) return null;
		if (UtilsShiftCompare.isCompare(mode, MODE_Jsoup)) {
			logger.debug(getMessage(url, "Jsoup", timeout));
			//Map<String, String> headers=UtilsConstsRequestHeader.getRndHeadMap();
			try {
				Map<String, String> headers=UtilsConstsRequestHeader.getRndHeadMap();
				Connection connect = Jsoup.connect(url.toString()).headers(headers);//UtilsConsts.header_a);
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
			if (content != null && content.length() > 0) { return getDocument(domain, content); }
		}
		if (UtilsShiftCompare.isCompare(mode, MODE_URL)) {
			logger.debug(getMessage(url, "UrlRead", timeout));
			String content = UtilsReadURL.getUrlContent(url, newCode, timeout);
			if (content != null && content.length() > 0) return getDocument(domain, content);
		}
		return null;
	}

	/**
	 * 提取全部信息 运行JS
	 * @param url String
	 * @return Document
	 */
	public static final Document getDocJS(String url) {
		if (UtilsUrl.isErrorUrl(url)) return null;
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
		if (UtilsUrl.isErrorUrl(url)) return null;
		String content = UtilsReadURL.getReadUrlJsDefault(url.toString());
		String domain = UtilsReadURL.getUrlDomain(url);
		return getDocument(domain, content);
	}

	/**
	 * 重组Docment
	 * @param content String
	 * @return Document
	 */
	public static final Document getDocument(String content) {
		if(content==null || content.length()==0)return null;
		String cut = content.substring(0, ACC_ShowLogger_WriteFileInforLen);
		logger.debug("重组Document:"+cut);
		return getDocument(null, content);
	}

	/**
	 * 重组Docment
	 * @param domain String
	 * @param content String
	 * @return Document
	 */
	public static final Document getDocument(String domain, String content) {
		if (content == null) return null;
		if (domain == null) return Jsoup.parse(content);
		return Jsoup.parse(content, domain);
	}

	/**
	 * 重组日志内容
	 * @param url URL
	 * @param type String
	 * @return String
	 */
	static final String getMessage(URL url, String type) {
		return "读取网址方式: " + type + "!\t" + url.toString();
	}
	/**
	 * 重组日志内容
	 * @param url URL
	 * @param type String
	 * @param timeout int
	 * @return String
	 */
	static final String getMessage(URL url, String type, int timeout) {
		return "[" + timeout + "]读取网址方式: " + type + "!\t" + url.toString();
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
		return getDocument(domain, cut);
	}

	/**
	 * 判断e是否在source中 Html
	 * @param source Element
	 * @param e Element
	 * @return boolean
	 */
	public static final boolean isContain(Element source, Element e) {
		if (source == null || e == null) return false;
		String eString = e.html();
		if (source.html().equals(eString)) return true;
		Elements es = source.getAllElements();
		for (Element f : es)
			if (eString.equals(f.html())) return true;
		return false;
	}

	/**
	 * 判断e是否在source中 Text
	 * @param source Element
	 * @param e Element
	 * @return boolean
	 */
	public static final boolean isContainText(Element source, Element e) {
		if (source == null || e == null) return false;
		String eString = e.text();
		if (source.text().equals(eString)) return true;
		Elements es = source.getAllElements();
		for (Element f : es)
			if (eString.equals(f.text())) return true;
		return false;
	}

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
		result = UtilsHtmlFilter.filter(result);/* 过滤掉多余的特殊字符 */
		return result.length() != 0;
	}
	/**
	 * 清除Element内所有节点的属性
	 * @param es Elements
	 * @return Elements
	 */
	public static final Elements clearAllAttributes(Elements es) {
		if(es==null)return es;
		for(Element f:es)
			clearAllAttributes(f);
		return es;
	}
	/**
	 * 清除Element内所有节点的属性
	 * @param e Element
	 * @return Element
	 */
	public static final Element clearAllAttributes(Element e) {
		if(e==null)return e;
		Elements es=e.getAllElements();
		for(Element f:es)
			f.clearAttributes();
		return e;
	}
	/**
	 * 把多个Element合并成一个Element<br>
	 * 主Element为空，再依次添加Element<br>
	 * 是否过滤相同，或含有的Element
	 * @param isDistinct boolean
	 * @param arr Element[]
	 * @return Element
	 */
	public static final Element merge(boolean isDistinct, Element... arr) {
		if (arr.length == 0) return null;
		Tag tag = null;
		for (Element e : arr) {
			if (e == null) continue;
			if (tag != null) break;
			tag = e.tag();
			break;
		}
		if (tag == null) return null;
		Element result = new Element(tag, "", null);
		for (Element e : arr) {
			if (e == null) continue;
			if (isDistinct && isContain(result, e)) continue;
			result.appendChild(e);
		}
		return result;
	}

	/**
	 * 把多个Element合并成一个Element<br>
	 * 主Element为空，再依次添加Element
	 * @param arr Element[]
	 * @return Element
	 */
	public static final Element merge(Element... arr) {
		return merge(false, arr);
	}

	/**
	 * 把多个Element合并成一个Element<br>
	 * 主Element为空，再依次添加Element
	 * @param es Elements
	 * @return Element
	 */
	public static final Element merge(Elements es) {
		if (es.size() == 0) return null;
		Element[] arr = new Element[0];
		arr = es.toArray(arr);
		return merge(arr);
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
}
