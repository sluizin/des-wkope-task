package des.wangku.operate.standard.testing;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import des.wangku.operate.standard.subengineering.reediting.Filter;
import des.wangku.operate.standard.task.InterfaceThreadRunUnit;
import des.wangku.operate.standard.testing.Books17Utils.StyleClass;
import des.wangku.operate.standard.utls.UtilsFile;
import des.wangku.operate.standard.utls.UtilsJsoup;
import des.wangku.operate.standard.utls.UtilsStringFilter;
import des.wangku.operate.standard.utls.UtilsThread;
import des.wangku.operate.standard.utls.UtilsThread.UtilsInterfaceThreadUnit;

public class BooksReadUtils {
	static final String ACC_Path = "G:/Download/";

	static final void makebooks(String url) {
		UnitClass e=BooksReadUtils.getUC(url);
		if(e==null)return;
		Document doc=UtilsJsoup.getDoc(url);
		if(doc==null)return;
		String filename=UtilsJsoup.getElementAllFirstText(doc, e.keyName);
		//System.out.println("doc:"+doc.html());
		//System.out.println("e:"+e.keyLink);
		Element f=UtilsJsoup.getElementAllFirst(doc, e.keyLink);
		if(f==null) {
			System.out.println("f:null");
			return;
		}
		BooksReadUtils.make(e,url, filename, f.select("a"),true);
	}
	static final void make(UnitClass uc, String url, String filename, Elements lis,boolean ismove) {
		//System.out.println("filename:" + filename);
		//System.out.println("lis:" + lis.size());
		UtilsJsoup.ACC_DefaultTimeout = uc.timeout;
		filename = UtilsStringFilter.filterName(filename);
		if (Books17Utils.isexist(filename)) return;
		String pathfile = ACC_Path + filename + ".txt";
		File file = new File(pathfile);
		for (int i = 1, len = lis.size(); i < len; i++) {
			UtilsThread.ThreadSleep(uc.sleep);
			Element li = lis.get(i);
			String name = "";
			String content = "";
			name = li.text();
			name = Books17Utils.filter(name);
			//System.out.println("name:"+name);
			Elements hrefarr = li.select("a");
			if (hrefarr.size() == 0) continue;
			String href = hrefarr.get(0).attr("abs:href");
			Element condiv = UtilsJsoup.getElementAllFirst(href, uc.keyContent);
			if (condiv == null) continue;
			content = condiv.html();
			content = Filter.filter(content);
			//System.out.println("content:"+content);
			String newname = "第" + i + "章 " + name;
			UtilsFile.writeFile(file, Books17Utils.enter + Books17Utils.enter + Books17Utils.enter + newname + Books17Utils.enter + Books17Utils.enter + Books17Utils.enter);
			UtilsFile.writeFile(file, content);
			System.out.println(url + " -> " + filename + "[" + i + "/" + len + "] -> " + newname);
		}
		if(ismove) {
		 long length = file.length();
		 StyleClass sc = Books17Utils.getSC(length);
		 String to = ACC_Path + sc.key + "/" + filename + ".txt";
		 File endFile = new File(to);
		 file.renameTo(endFile);
		 System.out.println("移动到 -> " + to);
		}
		
	}

	static final class UnitClass {
		String urlKey = "";
		String keyName = "";
		String keyLink = "";
		String keyContent = "";
		long sleep = 10000;
		int timeout = 20000;

		public UnitClass(String urlKey, String keyName, String keyLink, String keyContent, long sleep, int timeout) {
			this.urlKey = urlKey;
			this.keyName = keyName;
			this.keyLink = keyLink;
			this.keyContent = keyContent;
			this.sleep = sleep;
			this.timeout = timeout;
		}
	}

	static final List<UnitClass> uclist = new ArrayList<>();
	static {
		uclist.add(new UnitClass("www.yshuwu.com", "{T}h1", "novel_list", "yuedu_zhengwen", 2000, 10000));
		uclist.add(new UnitClass("www.niumore.com", "{T}h1", "allchapter", "BookText", 2000, 5000));
		uclist.add(new UnitClass("www.gazww.com", "{T}h1", "正文\n      </dt>|</dl>", "content", 5000, 30000));
	}

	static final UnitClass getUC(String url) {
		if (url == null || url.length() == 0) return null;
		url = url.trim();
		for (UnitClass e : uclist)
			if (url.indexOf(e.urlKey) > -1) return e;
		return null;
	}
	
	static final void makelist(int maxThread,String...arr){
		List<UtilsInterfaceThreadUnit> newList = new ArrayList<>(maxThread);
		for (String e :arr)
			newList.add(new ReadWorkOne(e));
		
				
	}
	
	static final class ReadWorkOne implements UtilsInterfaceThreadUnit{
		String url= null;
		public ReadWorkOne(String url) {
			this.url=url;			
		}
		@Override
		public void run() {
				makebooks(url);
		}
		
	}
}
