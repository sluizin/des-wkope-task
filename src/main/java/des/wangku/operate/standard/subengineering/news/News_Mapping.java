package des.wangku.operate.standard.subengineering.news;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import des.wangku.operate.standard.utls.UtilsCpdetector;
import des.wangku.operate.standard.utls.UtilsReadURL;
import des.wangku.operate.standard.utls.UtilsConstsRequestHeader;

/**
 * 映射单元
 * {
 * "name": "现代金报",
 * "urlkey": ["dzb.jinbaonet.com/html/"],
 * "code":"gb2312",
 * "blockid": ["arttext"],
 * "blocklabel": ["<!----------文章部分开始---------->", "<!----------文章部分结束---------->"],
 * "blockindex": 0,
 * "blockmode--":"id[1]/class[2]/tag[4]/attkey[8]/attvalue[16] /unclear[1024]",
 * "blockmode":7,
 * "filterblockkey": []
 * }
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class News_Mapping {
	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(News_Mapping.class);
	boolean isscript = false;
	boolean isstyle = false;
	int timeout = 20000;
	String name = "";
	/** 规则url链接片断 */
	List<String> urlkey = new ArrayList<>(1);
	/** 编码 */
	String code = "";
	/** 板块标号 */
	public List<String> blockid = new ArrayList<>(1);
	/** 板块区间 */
	List<String> blocklabel = new ArrayList<>(2);
	/**
	 * 板块检索方式
	 * id[1]/class[2]/tag[4]/attkey[8]/attvalue[16] /unclear[1024]
	 */
	int blockmode = 7;
	/** 板块索引底标 */
	int blockindex = 0;
	/** 过滤id */
	List<String> filterblockkey = new ArrayList<>(1);
	/** 过滤属性与值 */
	List<String> filterblockattr = new ArrayList<>(0);

	/**
	 * 判断这个映射是否有效
	 * @return boolean
	 */
	public final boolean isSafe() {
		if (name == null || name.length() == 0) return false;
		if (urlkey.size() == 0) return false;
		if (blockid.size() == 0 && blocklabel.size() == 0 && blockindex < 0) return false;
		if (blockindex < 0) return false;
		return true;
	}

	public final String getName() {
		return name;
	}

	public final void setName(String name) {
		this.name = name;
	}

	public final List<String> getUrlkey() {
		return urlkey;
	}

	public final void setUrlkey(List<String> urlkey) {
		this.urlkey = urlkey;
	}

	public final List<String> getBlockid() {
		return blockid;
	}

	public final void setBlockid(List<String> blockid) {
		this.blockid = blockid;
	}

	public final List<String> getBlocklabel() {
		return blocklabel;
	}

	public final void setBlocklabel(List<String> blocklabel) {
		this.blocklabel = blocklabel;
	}

	public final List<String> getFilterblockkey() {
		return filterblockkey;
	}

	public final void setFilterblockkey(List<String> filterblockkey) {
		this.filterblockkey = filterblockkey;
	}

	public final String getCode() {
		return code;
	}

	public final void setCode(String code) {
		this.code = code;
	}

	public final int getBlockindex() {
		return blockindex;
	}

	public final void setBlockindex(int blockindex) {
		this.blockindex = blockindex;
	}

	public final int getBlockmode() {
		return blockmode;
	}

	public final void setBlockmode(int blockmode) {
		this.blockmode = blockmode;
	}

	public final List<String> getFilterblockattr() {
		return filterblockattr;
	}

	public final void setFilterblockattr(List<String> filterblockattr) {
		this.filterblockattr = filterblockattr;
	}

	@Override
	public String toString() {
		return "Mapping [" + (name != null ? "name=" + name + ", " : "") + (urlkey != null ? "urlkey=" + urlkey + ", " : "") + (code != null ? "code=" + code + ", " : "") + (blockid != null ? "blockid=" + blockid + ", " : "")
				+ (blocklabel != null ? "blocklabel=" + blocklabel + ", " : "") + "blockmode=" + blockmode + ", blockindex=" + blockindex + ", " + (filterblockkey != null ? "filterblockkey=" + filterblockkey + ", " : "")
				+ (filterblockattr != null ? "filterblockattr=" + filterblockattr : "") + "]";
	}

	/**
	 * 判断URL是否映射
	 * @param url URL
	 * @return boolean
	 */
	public final boolean isExist(URL url) {
		if (url == null) return false;
		String urlString = url.toString();
		for (int i = 0; i < urlkey.size(); i++)
			if (urlString.indexOf(urlkey.get(i)) > -1) return true;
		return false;
	}

	/**
	 * 得到最近的匹配 忽略大小写
	 * @param url URL
	 * @return int
	 */
	public final int isExistIndexMin(URL url) {
		if (url == null) return -1;
		String urlString = url.toString().toLowerCase();
		int min = -1;
		for (int i = 0; i < urlkey.size(); i++) {
			int index = urlString.indexOf(urlkey.get(i).toLowerCase());
			if (index > -1 && (min == -1 || min > index)) min = index;
		}
		return min;

	}

	/**
	 * 得到body对象
	 * @param content String
	 * @return Element
	 */
	public final Element getContentElement(String content) {
		return getElement(Jsoup.parse(content));
	}

	/**
	 * 新方法 得到内容值
	 * @param obj Element
	 * @return Element
	 */
	public final Element getElement(Element obj) {
		if (obj == null) return null;
		Element result = getExtract(obj);
		if (result == null) return null;
		if (!isscript) result = getRemoveScript(result);
		if (!isstyle) result = getRemoveStyle(result);
		return getElementFilter(result);
	}

	/**
	 * 移除对象中含有script标签的对象
	 * @param obj Element
	 * @return Element
	 */
	public static final Element getRemoveScript(Element obj) {
		Elements elScripts = obj.getElementsByTag("script");
		for (Element e : elScripts)
			e.remove();
		return obj;
	}

	/**
	 * 移除对象中含有style标签的对象
	 * @param obj Element
	 * @return Element
	 */
	public static final Element getRemoveStyle(Element obj) {
		Elements elStyle = obj.getElementsByTag("style");
		for (Element e : elStyle)
			e.remove();
		return obj;
	}

	/**
	 * 只提取
	 * @param obj Element
	 * @return Element
	 */
	public Element getExtract(Element obj) {
		String[] arr = {};
		String[] arrs = blockid.toArray(arr);
		/* 如果没有标识，只以下标进行提取 */
		if (blockid.size() == 0 && blocklabel.size() == 0 && blockindex > -1) {
			Elements els = obj.getAllElements();
			if (blockindex < els.size()) return els.get(blockindex);
			return null;
		}
		Set<Element> set = getBlockElementSet(this.blockmode, obj, arrs);/* 以id之类进行提取 */
		if (blocklabel.size() > 0) set.addAll(getTruncationBlock(obj, arrs));/* 以注释标识为截断进行提取 */
		if (this.blockindex >= set.size() || this.blockindex < 0) return null;/* 如果结果为空或提取下标为负，则返回null */
		int i = 0;
		for (Element e : set)
			if ((i++) == blockindex) return e;
		return null;
	}

	/**
	 * 过滤其它标记属性对象
	 * @param obj Element
	 * @return Element
	 */
	private final Element getElementFilter(Element obj) {
		if (obj == null) return obj;
		filterElementKey(obj);
		filterElementAttr(obj);
		return obj;
	}

	/**
	 * 过滤key
	 * @param obj Element
	 */
	final void filterElementKey(Element obj) {
		if (filterblockkey.size() == 0) return;
		String[] arr = {};
		String[] arrs = filterblockkey.toArray(arr);
		Set<Element> set = getBlockElementSet(ACC_MODEID + ACC_MODECLASS + ACC_MODETAG, obj, arrs);/* 以id之类进行提取 */
		for (Element e : set) {
			e.remove();
		}
	}

	/**
	 * 过滤指定属性的对象
	 * 当value为空时，只判断是否有属性名<br>
	 * 当value非空时，则判断即有属性名还有属性值的对象
	 * @param obj Element
	 */
	final void filterElementAttr(Element obj) {
		if (filterblockattr.size() == 0) return;
		for (int i = 0, len = filterblockattr.size(); i < len; i++) {
			String str = filterblockattr.get(i);
			JSONObject jobj = JSON.parseObject(str);
			for (Entry<String, Object> entry : jobj.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue().toString();
				logger.debug("filterElementAttr:" + key + ":" + value);
				Set<Element> set = getFilterElementAttr(obj, key, value);
				for (Element e : set)
					e.remove();
			}
		}
	}

	/**
	 * 得到标签内所有有属性与属性相应值的对象<br>
	 * 当value为空时，只判断是否有属性名<br>
	 * 当value非空时，则判断即有属性名还有属性值的对象
	 * @param obj Element
	 * @param key String
	 * @param value String
	 * @return Set<Element>
	 */
	final static Set<Element> getFilterElementAttr(Element obj, String key, String value) {
		Set<Element> set = new HashSet<Element>();
		if (value == null || value.length() == 0) set.addAll(obj.getElementsByAttribute(key));
		else set.addAll(obj.getElementsByAttributeValue(key, value));
		return set;
	}

	/**
	 * 得到编码。自定义 &gt; 自检索 &gt; 默认[utf-8]
	 * @param url URL
	 * @return String
	 */
	public final String getCode(URL url) {
		if (code != null && code.length() > 0) return code;
		String cpcode = UtilsCpdetector.getUrlEncode(url);
		if (cpcode != null) return cpcode;
		return "utf-8";
	}

	/**
	 * 提取jsoup &gt; Document socket &gt;URL 全部信息
	 * @param url URL
	 * @return Document
	 */
	public final Document getDoc(URL url) {
		String newCode = getCode(url);
		try {
			Connection connect = Jsoup.connect(url.toString()).headers(UtilsConstsRequestHeader.getRndHeadMap());//UtilsConsts.header_a
			Document document = connect.timeout(timeout).maxBodySize(0).get();
			if (document != null) return document;
		} catch (IOException e) {
			e.printStackTrace();
		}

		String content = UtilsReadURL.getSocketContent(url, newCode, timeout);
		if (content != null && content.length() > 0) return Jsoup.parse(content);

		content = UtilsReadURL.getUrlContent(url, newCode, timeout);
		if (content != null && content.length() > 0) return Jsoup.parse(content);
		return null;
	}

	/** 查找到的目标div的内容 */
	public Element targetElement = null;

	/**
	 * 初始化
	 * @param link Element 全部html代码
	 */
	public void Initialization(Element link) {
		if (targetElement == null) targetElement = getElement(link);
	}

	/**
	 * 新方法 得到内容值
	 * @return String
	 */
	public final String getContent() {
		if (targetElement == null) return "";
		return targetElement.html();
	}

	/** 查找方式 精确或模糊 */
	public static final int ACC_MODEUNCLEAR = 1024;
	/** 查找方向 ID */
	public static final int ACC_MODEID = 1;
	/** 查找方向 CLASS */
	public static final int ACC_MODECLASS = 2;
	/** 查找方向 标签名称 tag */
	public static final int ACC_MODETAG = 4;
	/** 查找方向 属性名称 attkey */
	public static final int ACC_MODEATTKEY = 8;
	/** 查找方向 属性值 attvalue */
	public static final int ACC_MODEATTVALUE = 16;

	/**
	 * 通过方式与关键字找出列表，不允许出现重复
	 * @param mode int
	 * @param obj Element
	 * @param key String
	 * @return Set&lt;Element&gt;
	 */
	public final static Set<Element> getBlockElementKey(int mode, Element obj, String key) {
		if (key == null || key.length() == 0) return new HashSet<Element>();
		Set<Element> set = new HashSet<Element>();
		Element link = null;
		/* 模糊查找 通过indexof */
		if ((mode & ACC_MODEUNCLEAR) > 0) {
			Elements elements = obj.getAllElements();
			for (int i = 0, len = elements.size(); i < len; i++) {
				link = elements.get(i);
				if ((mode & ACC_MODEID) > 0) {
					String id = link.id();
					if (id != null && id.indexOf(key) > -1) set.add(link);
				}
				if ((mode & ACC_MODECLASS) > 0) {
					String cla = link.className();
					if (cla != null && cla.indexOf(key) > -1) set.add(link);
				}
				if ((mode & ACC_MODETAG) > 0) {
					String tag = link.tagName();
					if (tag != null && tag.indexOf(key) > -1) set.add(link);
				}
				if ((mode & ACC_MODEATTKEY) > 0) {
					Attributes attrs = link.attributes();
					for (Attribute e : attrs) {
						if (e.getKey().indexOf(key) > -1) {
							set.add(link);
							break;
						}
					}
				}
				if ((mode & ACC_MODEATTVALUE) > 0) {
					Attributes attrs = link.attributes();
					for (Attribute e : attrs) {
						if (e.getValue().indexOf(key) > -1) {
							set.add(link);
							break;
						}
					}
				}
			}
			return set;
		}
		/* 精确查找 */
		if ((mode & ACC_MODEID) > 0) {
			link = obj.getElementById(key);
			if (link != null) set.add(link);
		}
		if ((mode & ACC_MODECLASS) > 0) set.addAll(obj.getElementsByClass(key));
		if ((mode & ACC_MODETAG) > 0) set.addAll(obj.getElementsByTag(key));
		if ((mode & ACC_MODEATTKEY) > 0) set.addAll(obj.getElementsByAttribute(key));
		if ((mode & ACC_MODEATTVALUE) > 0) {
			Elements elements = obj.getAllElements();
			for (int i = 0, len = elements.size(); i < len; i++) {
				link = elements.get(i);
				Attributes attrs = link.attributes();
				for (Attribute e : attrs) {
					if (e.getValue().equals(key)) {
						set.add(link);
						break;
					}
				}
			}
		}
		return set;
	}

	/**
	 * 从数组关键字中查找列表，不允许出现重复
	 * @param mode int
	 * @param obj Element
	 * @param arrs String[]
	 * @return Set&lt;Element&gt;
	 */
	public final static Set<Element> getBlockElementSet(int mode, Element obj, String... arrs) {
		Set<Element> set = new HashSet<Element>();
		for (int i = 0; i < arrs.length; i++)
			if (arrs.length > 0) set.addAll(getBlockElementKey(mode, obj, arrs[i]));
		return set;
	}

	/**
	 * 得到截断区块
	 * @param obj Element
	 * @param arrs String[]
	 * @return Set Element
	 */
	public final static Set<Element> getTruncationBlock(Element obj, String... arrs) {
		if (obj == null || arrs.length == 0) return new HashSet<Element>();
		String content = obj.html();
		int strLen = content.length();
		Set<Element> set = new HashSet<Element>();
		for (int i = 0, len = arrs.length; i < len; i = +2) {
			String a = arrs[i];
			String b = null;
			if (i < len) b = arrs[i + 1];
			int index1 = -1, index2 = -1;
			if (a != null && a.length() > 0) index1 = content.indexOf(a);
			if (b != null && b.length() > 0) index2 = content.indexOf(b);
			if (index1 == -1 && index2 == -1) continue;
			String newContent = null;
			if (index1 == -1) {
				if (index2 == -1) continue;
				else newContent = content.substring(0, index2);
			} else {
				if (index2 == -1) {
					newContent = content.substring(index1, strLen);
				} else {
					newContent = content.substring(index1, index2);
				}
			}
			if (newContent != null) set.add(Jsoup.parse(newContent));
		}
		return set;
	}

	public final boolean isIsscript() {
		return isscript;
	}

	public final void setIsscript(boolean isscript) {
		this.isscript = isscript;
	}

	public final boolean isIsstyle() {
		return isstyle;
	}

	public final void setIsstyle(boolean isstyle) {
		this.isstyle = isstyle;
	}

	public final int getTimeout() {
		return timeout;
	}

	public final void setTimeout(int timeout) {
		this.timeout = timeout;
	}

}
