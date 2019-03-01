package des.wangku.operate.standard.subengineering;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import des.wangku.operate.standard.utls.UtilsReadURL;

/**
 * 百度百科标签内容
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 *
 */
public class BaiduBaiKe {
	
	public static final String getBaiKeTagInfo(String name,boolean isSummary,String tagName) {
		String url="https://baike.baidu.com/item/"+name;
		String content=UtilsReadURL.getReadUrlDisJs(url);
		Document doc=Jsoup.parse(content);/* 查出简介 */
		if(isSummary) {
			Elements els=doc.getElementsByClass("lemma-summary");
			if(els.size()==0) {
				System.out.println("未查出简介，请检查关键字");
				return "";
			}
			Element e=els.first();
			return e.text();			
		}

		Elements arrs=doc.getElementsByClass("para-title");
		for(Element e:arrs) {
			if(e.text().indexOf(tagName)>-1) {	
				Element next=e.nextElementSibling();
				StringBuilder sb=new StringBuilder();
				while(next.hasClass("para")) {
					sb.append(next.text()+"\n");
					 next=next.nextElementSibling();
				}
				return sb.toString();
			}			
		}
		return "";
		
	}
}
