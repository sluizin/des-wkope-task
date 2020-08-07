package des.wangku.operate.standard.subengineering;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import des.wangku.operate.standard.utls.UtilsJsoup;
import des.wangku.operate.standard.utls.UtilsString;

/**
 * 爱站排名搜索排名
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class AIBaidurank {
	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(AIBaidurank.class);
	public static final String intervalkey=",";
	/**
	 * 
	 * 通过要查询的网址与类型得到结果 以","查询多个结果集<br>
	 * type:baidu / mobile
	 * @param type String
	 * @param urlArrs String
	 * @return List&lt;AIInfor&gt;
	 */
	public static final List<AIInfor> getBaidurankMultiList(String type,String urlArrs) {
		List<AIInfor> list=new ArrayList<>();
		String[] arrs=urlArrs.split(intervalkey);
		for(String url:arrs) {
			if(url.length()==0)continue;
			AIInfor e=getBaidurankList(url,type);
			list.add(e);
		}
		return list;
	}
	/**
	 * 通过要查询的网址与类型得到结果<br>
	 * type:baidu / mobile
	 * @param urlPath String
	 * @param type String
	 * @return AIInfor
	 */
	public static final AIInfor getBaidurankList(String urlPath, String type) {
		AIInfor aiInfor=new AIInfor();
		try {
			String aizhan = "https://baidurank.aizhan.com/" + type + "/" + urlPath + "/-1/0/1/position/1/";
			logger.debug("aizhan:"+aizhan);
			URL url = new URL(aizhan);
			Document doc = UtilsJsoup.getDoc(url, 1 + 2);
			if (doc == null) return aiInfor;
			String[] removeclass= {};
			String[] arrs = UtilsJsoup.getHrefTagArrayByClass(doc,true,removeclass, "baidurank-pager");
			if (arrs.length <= 1) {
				aiInfor.list.addAll(getInforList(aizhan, urlPath, type));
			}
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			for (int i = 1; i <= arrs.length; i++) {
				String aizhan2 = "https://baidurank.aizhan.com/" + type + "/" + urlPath + "/-1/0/" + i + "/position/1/";
				aiInfor.list.addAll(getInforList(aizhan2, urlPath, type));
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return aiInfor;
	}
	/**
	 * 通过aizhan地址，得到列表
	 * @param aizhan  String
	 * @param urlPath String
	 * @param type String
	 * @return List &lt;  AIInforUnit &gt; 
	 */
	public static final List<AIInforUnit> getInforList(String aizhan, String urlPath, String type) {
		List<AIInforUnit> list = new ArrayList<AIInforUnit>();
		try {
			Thread.sleep(1000);
			//logger.debug("urlPath:" + aizhan);
			URL url = new URL(aizhan);
			Document doc = UtilsJsoup.getDoc(url, 1 + 2);
			Elements lis = doc.getElementsByClass("baidurank-list");
			if (lis.size() == 0) return list;
			Element div = lis.get(0);
			Elements trs = div.select("table").select("tr");
			for (int i = 1; i < trs.size(); i++) {
				int first = 0;
				if (i == 1) first = 1;
				Elements tds = trs.get(i).select("td");
				if (tds.size() <= 2) break;
				String text = tds.get(first).text();
				String textposid = tds.get(first + 1).text();
				AIInforUnit infor = new AIInforUnit();
				infor.key=text;
				infor.posid=textposid;
				list.add(infor);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 只有url和type
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 *
	 */
	public static class AIInfor {
		String url;
		String type;
		List<AIInforUnit> list = new ArrayList<>();
		public final String getUrl() {
			return url;
		}
		public final void setUrl(String url) {
			this.url = url;
		}
		public final String getType() {
			return type;
		}
		public final void setType(String type) {
			this.type = type;
		}
		public final List<AIInforUnit> getList() {
			return list;
		}
		public final void setList(List<AIInforUnit> list) {
			this.list = list;
		}
		public final String getPosid(String key) {
			for(AIInforUnit e:list) {
				if(e.key.equals(key))return e.posid;
			}
			return "--";
		}
		
	}
	/**
	 * 只有key和排名文字
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 *
	 */
	public static class AIInforUnit {
		String key;
		String posid;

		public final String getKey() {
			return key;
		}

		public final void setKey(String key) {
			this.key = key;
		}

		public final String getPosid() {
			return posid;
		}

		public final int getPosidInteger() {
			return UtilsString.getNumbersIntTemplateAIZhan(posid);
		}
		
		public final void setPosid(String posid) {
			this.posid = posid;
		}

	}

}
