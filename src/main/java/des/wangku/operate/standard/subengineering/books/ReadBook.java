package des.wangku.operate.standard.subengineering.books;

import java.io.File;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import des.wangku.operate.standard.subengineering.reediting.Filter;
import des.wangku.operate.standard.utls.UtilsFile;
import des.wangku.operate.standard.utls.UtilsJsoup;

/**
 * 读取url，并提取内容
 * 
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class ReadBook {
	static final String ACC_Enter=System.getProperty("line.separator");

	static final void makefile(String url,String path) {
		String title=null;
		List<Element> list=UtilsJsoup.getElementAll(url, "infot");
		if(list.size()==0)return;	
		title=list.get(0).text();
		String filename=path+"/"+title+".txt";
		File file=new File(filename);
		if(file.exists()) {
			System.out.println("发现同名文件！！！");
			return;
		}
		list=UtilsJsoup.getElementAll(url, "liebiao");
		if(list.size()==0)return;
		Elements els=list.get(0).select("li");
		for(int i=1,len=els.size();i<len;i++) {
			Element li=els.get(i);
			String name="";
			String content="";
			name=li.text();
			Elements hrefarr=li.select("a");
			if(hrefarr.size()==0)continue;
			String href=hrefarr.get(0).attr("abs:href");
			List<Element> contentlist=UtilsJsoup.getElementAll(href, "content");
			if(contentlist.size()==0)continue;
			Element condiv=contentlist.get(0);
			Element first=condiv.select("p").get(0);
			first.remove();
			content=condiv.html();
			content=Filter.filterscript(content);
			content=Filter.filterscriptvar(content);
			content = Filter.filterTag(content,"img");
			content=Filter.filterHtmlEscapecharacter(content);
			content=Filter.filterHtmlSymbol(content);
			String newname="第"+i+"章 "+name;
			UtilsFile.writeFile(filename, ACC_Enter+ACC_Enter+ACC_Enter+newname+ACC_Enter+ACC_Enter+ACC_Enter);
			UtilsFile.writeFile(filename, content);
			System.out.println(newname);
		}
	}
}
