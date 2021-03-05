package des.wangku.operate.standard.subengineering.rank;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import des.wangku.operate.standard.utls.UtilsJsoup;
import des.wangku.operate.standard.utls.UtilsJsoupLink;
import des.wangku.operate.standard.utls.UtilsThread;

public class Rank_ChinaZ implements RankInterface {
	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(Rank_ChinaZ.class);
	String urlkey = null;
	String type = null;

	public Rank_ChinaZ() {

	}

	public Rank_ChinaZ(String urlkey, String type) {
		this.urlkey = urlkey;
		this.type = type;
	}
	@Override
	public RankInfor getRankInfor() {
		if(urlkey==null|| urlkey.length()==0)return null;
		RankInfor infor = new RankInfor();
		String urlBase = getUrlPage(type,urlkey,1);
		List<String> pageList = UtilsJsoupLink.getHrefAll(urlBase, "{C}_chinaz-rank-pag");
		if (pageList.size() <= 1) {
			infor.list.addAll(getInforList(urlBase));
			return infor;
		}
		UtilsThread.ThreadSleep(300);
		for (int i = 1; i <= pageList.size(); i++) {
			String urlpage = getUrlPage(type,urlkey,i);
			//String urlpage = "https://baidurank.aizhan.com/" + type + "/" + urlkey + "/-1/0/" + i + "/position/1/";
			infor.list.addAll(getInforList(urlpage));
		}
		
		
		return infor;
	}
	static final String getUrlPage(String type,String urlkey,int page) {
		String url = "http://rank.chinaz.com/"+urlkey+"-0---0-"+page;
		if("mobile".equals(type)) {
			url = "http://rank.chinaz.com/baidumobile/"+urlkey+"-0---0-"+page;
		}
		return url;
	}

	@Override
	public List<RankInforUnit> getInforList(String urlpage) {
		List<RankInforUnit> list = new ArrayList<>();
		UtilsThread.ThreadSleep(1000);
		Elements lis =UtilsJsoup.getElementAll(urlpage, "{C}_chinaz-rank-new5r|->|{C}_chinaz-rank-new5b");
		for(Element f:lis) {
			String text=null,textposid=null;
			Elements lis2=UtilsJsoup.getElementAll(f, "{T}li[0]|->|{T}a[0]");
			if(lis2.size()==0)continue;
			text=lis2.first().text();

			Elements lis3=UtilsJsoup.getElementAll(f, "{T}li[1]|->|{T}a[0]");
			if(lis3.size()>0) {
				textposid=lis3.first().text();
			}
			
			if(text==null)continue;
			if(textposid==null)textposid="";
			RankInforUnit infor = new RankInforUnit();
			infor.key = text;
			infor.posid = textposid;
			list.add(infor);
		}
		return list;
	}


	public static void main(String[] args) 
	{
		String urlkey="nrg.99114.com";
		String type="pc";
		Rank_ChinaZ rank=new Rank_ChinaZ(urlkey,type);
		RankInfor rankinfor=rank.getRankInfor();
		List<RankInforUnit> list=rankinfor.list;
		for(RankInforUnit e:list) {
			System.out.println(e.key+"\t"+e.posid);
		}
	}
	
	
	
}
