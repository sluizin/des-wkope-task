package des.wangku.operate.standard.subengineering.news;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import des.wangku.operate.standard.utls.UtilsCpdetector;
import des.wangku.operate.standard.utls.UtilsJsoup;
import des.wangku.operate.standard.utls.UtilsReadURL;

/**
 * title, "标题"
 * source, "信息来源"
 * contenthtml, "网页内容"
 * datetime, "记录时间"
 * keywords, "关键字"
 * description, "描述"
 * contentpict, "图片集"
 * filterpict, "筛选图片"
 * picture, "记录图片"
 * summary, "记录简介"
 * lastModify, "发布时间"
 * href, "链接"
 * searchkey, "检索字"
 * engine, "引擎"
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class News_Infor {
	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(News_Infor.class);
	/** 是否需要html代码 */
	boolean ishtml = false;
	/** 是否需要script */
	boolean isscript = false;
	/** 是否需要style */
	boolean isstyle = false;
	/** 每个url的反应时间，单位毫秒 */
	int timeout = 20000;
	String href = null;

	public News_Infor() {

	}

	public News_Infor(boolean ishtml, boolean isscript, boolean isstyle, int timeout) {
		this.ishtml = ishtml;
		this.isscript = isscript;
		this.isstyle = isstyle;
		this.timeout = timeout;
	}

	/**
	 * 对链接地址进行读取分析
	 */
	public void analysisHref() {
		if (href == null || href.length() == 0) return;
		String lastModify = "", source = "", keywords = "", description = "", contenthtml = "", contentpict = "";
		try {
			URL url = new URL(href);
			URL newUrl = UtilsReadURL.getReadURL(url);
			if (newUrl == null) return;
			String code = UtilsCpdetector.getUrlEncode(newUrl);
			/* 发布时间 */
			Date lastModifyDate = UtilsReadURL.getLastModify(newUrl);
			lastModify = (lastModifyDate == null) ? "" : News_Consts.sdf.format(lastModifyDate);
			News_Mapping ee = News_Consts.getMappingIndexMin(newUrl);
			if (ee == null) {
				String jsonstrA = String.format(News_Consts.jsonstr, source, UtilsReadURL.getURLMasterKey(newUrl), code);
				logger.debug("find\t" + source + "\t" + newUrl.toString() + "\t未注册!!!!!" + code);
				logger.debug(jsonstrA);
				return;
			}
			if (!ee.isExist(newUrl)) return;
			ee.isscript=this.isscript;
			ee.isstyle=this.isstyle;
			code = ee.getCode(newUrl);
			Document doc = ee.getDoc(newUrl);
			if (doc == null) {
				logger.debug("find\t" + newUrl.toString() + "\t已注册，但未提取到数据!!!!!\tcode:" + code);
				return;
			}
			keywords = UtilsJsoup.getMetaContent(doc, "keywords");
			description = UtilsJsoup.getMetaContent(doc, "description");
			ee.Initialization(doc);
			String content = ee.getContent();
			contenthtml = ishtml ? content : UtilsJsoup.cleanHtml(content);//ee.getContent();
			contentpict = "";
			if (ee.targetElement != null) {
				Element target = ee.targetElement;
				Elements pics = target.select("img[src]");
				StringBuilder sb = new StringBuilder();
				for (Element eee : pics) {
					String picture = eee.attr("abs:src");
					if (sb.length() != 0) sb.append(';');
					if (picture.length() > 0) sb.append(picture);
				}
				contentpict = sb.toString();
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		map.put("lastModify", lastModify);
		map.put("keywords", keywords);
		map.put("description", description);
		map.put("contenthtml", contenthtml);
		map.put("contentpict", contentpict);
	}

	Map<String, Object> map = new HashMap<>();

	public final Map<String, Object> getMap() {
		return map;
	}

	public final void setMap(Map<String, Object> map) {
		this.map = map;
	}

	public void insertAttrib(String title, String href, String source, String datetime, String picture, String summary) {
		map.put("title", title);
		map.put("href", href);
		map.put("source", source);
		map.put("datetime", datetime);
		map.put("picture", picture);
		map.put("summary", summary);
	}

}
