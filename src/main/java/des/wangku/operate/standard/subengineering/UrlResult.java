package des.wangku.operate.standard.subengineering;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import des.wangku.operate.standard.utls.UtilsRegular;

/**
 * url状态，以及内容
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class UrlResult {
	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(UrlResult.class);
	URL url = null;
	String code = "utf-8";
	int timeout = 20000;
	String content = null;
	String returnCode = "";
	Document doc = null;
	Map<String, List<String>> map=null;
	/**
	 * 初始化
	 * @param url String
	 * @param code String
	 */
	public UrlResult(String url, String code) {
		if (code != null) this.code = code;
		try {
			this.url = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 读取URL 使用常规方法读取<br>
	 * 得到content和Document
	 */
	public void readURL() {
		if (url == null) return;
		try {
			HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
			urlcon.setConnectTimeout(timeout);
			urlcon.setReadTimeout(timeout);
			urlcon.connect();
			map = urlcon.getHeaderFields();
			returnCode = new Integer(urlcon.getResponseCode()).toString();
			StringBuilder sb = new StringBuilder(20);
			if (!returnCode.startsWith("2")) return;
			InputStream is = urlcon.getInputStream();
			InputStreamReader isr = new InputStreamReader(is, code);
			BufferedReader buffer = new BufferedReader(isr);
			String l = null;
			while ((l = buffer.readLine()) != null) {
				if (l.length() == 0) continue;
				sb.append(l);
				sb.append('\n');
			}
			buffer.close();
			is.close();
			content = sb.toString();
			doc = Jsoup.parse(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public int getKeyCount(String key) {
		if(key==null || key.length()==0)return 0;
		return UtilsRegular.getPatternCount(content, key);
	}
	public int getTagCountOR(String tag, TagAttr... arrs) {
		return getTagCountOR(doc, tag, arrs);
	}
	public int getTagCountAND(String tag, TagAttr... arrs) {
		return getTagCountAND(doc, tag, arrs);
	}
	public int getTagKeyCountAND(String tag, String...keyarrs) {
		List<TagAttr> list=new ArrayList<>();
		for(String e:keyarrs) {
			if(e==null || e.length()==0)continue;
			list.add(new TagAttr(e,"*"));
		}
		if(list.size()==0)return -1;
		TagAttr[] aa= {};
		return getTagCountAND(doc, tag, list.toArray(aa));
	}
	/**
	 * 得到标签多个属性，有一个属性则计数
	 * @param element Element
	 * @param tag String
	 * @param arrs TagAttr[]
	 * @return int
	 */
	public static final int getTagCountOR(Element element, String tag, TagAttr... arrs) {
		if (element == null) return -1;
		int count = 0;
		Elements arr = element.getElementsByTag(tag);
		if(arrs==null || arrs.length==0) {
			return arr.size();
		}
		for (Element e : arr) {
			for (TagAttr f : arrs) {
				if (f != null && f.compare(e)) {
					count++;
					break;
				}
			}
		}
		return count;
	}
	/**
	 * 得到标签的多个属性同时存在
	 * @param element Element
	 * @param tag String
	 * @param arrs TagAttr[]
	 * @return int
	 */
	public static final int getTagCountAND(Element element, String tag, TagAttr... arrs) {
		if (element == null) return -1;
		int count = 0;
		Elements arr = element.getElementsByTag(tag);
		loop:for (Element e : arr) {
			for (TagAttr f : arrs) {
				if(f == null)continue;
				if (!f.compare(e)) {
					continue loop;
				}
			}
			count++;
		}
		return count;
	}
	/**
	 * 标签属性以及值
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 */
	public static class TagAttr {
		String key = null;
		String value = null;

		public TagAttr(String key, String value) {
			this.key = key;
			this.value = value;
		}

		/**
		 * 对标签进行比较
		 * @param e Element
		 * @return boolean
		 */
		public boolean compare(Element e) {
			if (e == null || key == null) return false;
			boolean isexist = e.hasAttr(key);
			if (value == null && !isexist) return true;
			if (!isexist) return false;
			if (value == null) return false;
			String v = e.attr(key);
			if (value.equals("*")) return true;
			if (value.equals(v)) return true;
			return false;
		}

		@Override
		public String toString() {
			return "TagAttr [" + (key != null ? "key=" + key + ", " : "") + (value != null ? "value=" + value : "") + "]";
		}
		
	}
	public final String getReturnCode() {
		return returnCode;
	}
	public final void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}
	public final String getHeaderFieldsMap() {
		StringBuilder sb=new StringBuilder();
		   for (Map.Entry<String, List<String>> entry : map.entrySet()) {
               sb.append("Key : " + entry.getKey() + " ,Value : " + entry.getValue()+"\n");
           }
		   return sb.toString();
	}
	
}
