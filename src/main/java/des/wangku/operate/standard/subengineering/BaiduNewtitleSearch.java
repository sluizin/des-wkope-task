package des.wangku.operate.standard.subengineering;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import des.wangku.operate.standard.utls.UtilsJsoup;
import des.wangku.operate.standard.utls.UtilsJsoupConst;
import des.wangku.operate.standard.utls.UtilsString;
import des.wangku.operate.standard.utls.UtilsConstsRequestHeader;

/**
 * 从百度中提出某个新闻标题的位置或比例数
 * 
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class BaiduNewtitleSearch {
	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(BaiduNewtitleSearch.class);
	public static long BaiduSleepTime=200;
	/**
	 * 判断新闻标题是否在百度检索时，红色字体在所在的标题的百分比之中<br>
	 * 例: "abcd",20 即百度检索abcd时，所有列出的标题中是否有超出20%的情况，如果发现有一个超出20%，则返回false
	 * @param title String
	 * @param redMaxPercent float
	 * @return boolean
	 */
	public static boolean isExistNewstitle(String title,float redMaxPercent) {
		String url="https://www.baidu.com/s?wd="+title;
		logger.debug("search:"+url);

		try {
			if(BaiduSleepTime > 0)Thread.sleep(BaiduSleepTime);
			Connection connect = Jsoup.connect(url).headers(UtilsConstsRequestHeader.getRndHeadMap());//UtilsConsts.header_m);
			Document document = connect.timeout(20000).maxBodySize(0).get();
			if (document == null) return false;
			if(redMaxPercent<=0)return false;
			if(redMaxPercent>=100f)redMaxPercent=100f;
			Element left=document.getElementById("content_left");
			Elements results = left.select(".result");
			for(Element one:results) {
				Element h3 = one.getElementsByTag("h3").first();
				String nameall=h3.text().replaceAll(" ","");
				logger.debug(nameall);
				float lenSort=nameall.length();
				Elements ems=h3.getElementsByTag("em");
				float lenKey=0;
				for(Element em:ems) {
					lenKey+=em.text().length();
				}
				float per=(lenKey/lenSort)*100f;
				logger.debug(lenKey+"/"+lenSort+":"+per);
				if(per>=redMaxPercent)return false;
			}
			return true;
			

		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	/**
	* 判断新闻标题是否在百度检索时，红色字体在所在的标题的百分比之中<br>
	 * 例: "abcd",20 即百度检索abcd时，所有列出的标题中是否有超出20%的情况，如果发现有一个超出20%，则返回false
	 * @param key String
	 * @param repetionrate int
	 * @param page int
	 * @return boolean
	 */
	public static final boolean isRepetitionRate(String key,int repetionrate,int page) {
		if(key==null)return false;
		key=key.trim();
		if(key.length()==0)return false;
		String url="https://www.baidu.com/s?wd="+key;
		Elements es=UtilsJsoup.getElementAll(url, "{C}result"+UtilsJsoupConst.ACC_InPage+"{T}h3");
		for(Element e:es) {
			String text=e.text();
			if(UtilsString.repetitionRate(text, key)>=repetionrate)return true;
		}
		return false;
	}
	public static void main(String[] args) 
	{
		//System.out.println(isExistNewstitle("刷墙涂料有哪些 刷墙 涂料的分类有哪些",60.0f));
		String[] arr= {
				"利辛县亮盛服装*召回部分一次性防护口罩",
				"欧美订单减少 为了生存 越南服装**改卖口罩",
				"越南推动服装制造商生产口罩等个人防护设备",
				"注意了 布料口罩别乱卖 美国准入标准要出来了",
				"约旦口罩 防护服行业有潜力创造3.3万个工作岗位",
				"数十亿计的口罩 丢弃后都去哪儿了 ",
				"中产协发布口罩行业高质量发展倡议书",
				"欧盟继续对进口**设备和个人防护装备暂免关税和增值税",
				
		};
		for(String e:arr)
		System.out.println(isRepetitionRate(e,80,1));
	}
}
