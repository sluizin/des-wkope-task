package des.wangku.operate.standard.subengineering.rank;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import des.wangku.operate.standard.utls.UtilsJsoupLink;
import des.wangku.operate.standard.utls.UtilsJsoupText;
import des.wangku.operate.standard.utls.UtilsThread;
import des.wangku.operate.standard.utls.UtilsVerification;
/**
 * 爱站权重
 * 
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class Rank_AiZhan {
	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(Rank_AiZhan.class);
	/**
	 * 得到爱站中的数量<br>
	 * 0:PC词数(默认)<br>
	 * 1:移动词数<br>
	 * 2:首页位置<br>
	 * 3:反链<br>
	 * 4:索引<br>
	 * 5:24小时收录<br>
	 * 6:一周收录<br>
	 * 7:一月收录<br>
	 * url为空或返回的值不是数值，则返回0
	 * @param url String
	 * @param numtype int
	 * @return int
	 */
	public static final int getRankCount(String url,int numtype) {
		if(url==null||url.length()==0)return 0;
		String urlId="{I}cc1";
		switch(numtype){
	    case 1 :
	    	urlId="{I}cc2";
	       break; 
	    case 2 :
	    	urlId="{I}shoulu1_baiduposition";
	       break; 
	    case 3 :
	    	urlId="{I}backlink";
	       break; 
	    case 4 :
	    	urlId="{I}baiduindex";
	       break; 
	    case 5 :
	    	urlId="{I}shoulu3_1days";
	    case 6 :
	    	urlId="{I}shoulu3_7days";
	    case 7 :
	    	urlId="{I}shoulu3_30days";
	       break; 
	    default : 
		}
		String weburl="https://www.aizhan.com/cha/" + url + "/";
		String val=UtilsJsoupText.getTextFirst(weburl, urlId);
		if(UtilsVerification.isNumeric(val)) {
			return Integer.parseInt(val);
		}
		return 0;
	}
	/**
	 * 得到某个域名的爱站权重的结果页数
	 * @param rankType String
	 * @param urlkey String
	 * @return int
	 */
	public static final int getInforPages(String rankType,String urlkey) {
		String urlpage = "https://baidurank.aizhan.com/" + rankType + "/" + urlkey + "/-1/0/1/position/1/";
		List<String> resultlist=UtilsJsoupLink.getHrefAll(urlpage, "{C}baidurank-pager|->|{T}ul");
		if (resultlist.size()==0)return 1;
		return resultlist.size();
	}
	/**
	 * 爱站页面用于抽取权重的功能
	 * @param doc Document
	 * @return List&lt;RankInforUnit&gt;
	 */
	public static final List<RankInforUnit> getInforList(Document doc) {
		List<RankInforUnit> list = new ArrayList<>();
		try {
			UtilsThread.ThreadSleep(100);
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
				RankInforUnit infor = new RankInforUnit();
				infor.key = text;
				infor.posid = textposid;
				list.add(infor);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}	
}
