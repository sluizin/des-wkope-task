package des.wangku.operate.standard.subengineering;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import des.wangku.operate.standard.utls.UtilsJsoup;
import des.wangku.operate.standard.utls.UtilsMath;
import des.wangku.operate.standard.utls.UtilsThread;

public class BaiduWordsKeyRed {

	public static void main(String[] args) {
		String[] arr= {
				"KN95口罩CE认证技术文件technical files辅导",
				"检测认证行业也开创B2B商务模式",
				
		};
		for(String e:arr)
		System.out.println("percent:"+percent(e));
	}
	static final int percent(String key) {
		UtilsThread.ThreadSleep();
		String url="https://www.baidu.com/s?wd="+key;
		Elements es=UtilsJsoup.getElementAll(url, "result");
		int max=0;
		int len=key.length();
		for(Element e:es) {
			Element title=UtilsJsoup.getElementFirst(e, "t");
			Elements ess=UtilsJsoup.getElementAll(title, "{T}em");
			for(Element f:ess) {
				int v=UtilsMath.getPerCentInt(f.text().length(),len);
				System.out.println("em:"+f.text()+"\t"+len+"\\"+f.text().length()+"\t"+v);
				if(v>max)max=v;
			}
		}
		return max;
	}
}
