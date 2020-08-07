package des.wangku.operate.standard.testing;

import java.io.File;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import des.wangku.operate.standard.subengineering.reediting.Filter;
import des.wangku.operate.standard.testing.Books17Utils.StyleClass;
import des.wangku.operate.standard.utls.UtilsFile;
import des.wangku.operate.standard.utls.UtilsJsoup;

public class Books17 {
	static final String ACC_Path="G:/Download/";
	
	static final void makefile(String url) {
		if(url==null || url.trim().length()==0)return;
		List<Element> ullist=UtilsJsoup.getAllElements(url, "liebiao");
		if(ullist.size()==0) {
			System.out.println(url+"\t没发现具体内容章节！");
			return;
		}
		String filename=Books17Utils.getfilename(url);
		if(filename==null) {
			System.out.println(url+"\t没发现名称！");
			return;
		}
		
		String pathfile=ACC_Path+filename+".txt";
		File file=new File(pathfile);
		if(file.exists()) {
			System.out.println("发现同名文件\t"+pathfile);
			return;
		}
		if(Books17Utils.isexist(filename))return;
		
		Elements lis=ullist.get(0).select("li");
		make(url,filename,lis);
		
		
	}
	static final void make(String url,String filename,Elements lis) {
		if(Books17Utils.isexist(filename))return;
		String pathfile=ACC_Path+filename+".txt";
		File file=new File(pathfile);
		for(int i=1,len=lis.size();i<len;i++) {
			try{
				Thread.sleep(5000);
				}catch(Exception e){
				}
			
			Element li=lis.get(i);
			String name="";
			String content="";
			name=li.text();
			name=Books17Utils.filter(name);
			Elements hrefarr=li.select("a");
			if(hrefarr.size()==0)continue;
			String href=hrefarr.get(0).attr("abs:href");
			List<Element> contentlist=UtilsJsoup.getAllElements(href, "content");
			if(contentlist.size()==0)continue;
			Element condiv=contentlist.get(0);
			Elements pp=condiv.select("p");
			if(pp.size()>0) {
			Element first=pp.get(0);
			first.remove();
			}
			content=condiv.html();
			content=Filter.filterscript(content);
			content=Filter.filterscriptvar(content);
			content = Filter.filterTag(content,"img");
			content=Books17Utils.filter(content);
			content=content.replaceAll("2u2u2u","");
			content=content.replaceAll("4f4f4f","");
			content=content.replaceAll("\\\\u[a-zA-Z0-9]{4}","");
			content=content.replaceAll("[0-9]{4}年[0-9]{1,2}月[0-9]{1,2}日","");
			
			content=Filter.filterHtmlEscapecharacter(content);
			
			
			content=Filter.filterHtmlSymbol(content);
			String newname="第"+i+"章 "+name;
			UtilsFile.writeFile(pathfile, Books17Utils.enter+Books17Utils.enter+Books17Utils.enter+newname+Books17Utils.enter+Books17Utils.enter+Books17Utils.enter);
			UtilsFile.writeFile(pathfile, content);
			System.out.println(url+" -> "+filename+" -> "+newname);
		}
		long length = file.length();
		StyleClass sc=Books17Utils.getSC(length);
		String to=ACC_Path+sc.key+"/"+filename+".txt";
		File endFile=new File(to);
		file.renameTo(endFile);
		System.out.println("移动到 -> "+to);
	}
	public static void main2(String[] args) 
	{
		String[] arr= {
				"",
				"",
				"",
				"",
				"",
				"",
				"",
				"",
				"",
				"",
				"",
				"",
				""
				
		};
		for(String e:arr) {
			if(e==null ||e.length()==0)continue;
			String url=Books17Utils.getReadurl(e);
			if(url==null ||url.length()==0)continue;
			System.out.println("URL:"+url);
			makefile(url);
		}
		
		
	}
	//http://www.17books.net/book/113480.html
	//http://www.17books.net/113/113480/
	//"http://www.17books.net/2/2589/",
	//"http://www.17books.net/0/879/",

	public static void main(String[] args) {
		String sortfile=ACC_Path+"sortall.txt";
		int min=131,max=135;
		for(int i=min;i<=max;i++) {
			String url="http://www.17books.net/fenlei/7_"+i+".html";
			System.out.println(i+":->"+url+"\t=============================================================");
			List<Element> listfirst=UtilsJsoup.getAllElements(url, "alist");
			if(listfirst.size()==0) {
				System.out.println("未查到列表=============================");
				continue;
			}
			Element lidiv=listfirst.get(0);
			//List<Element> list=UtilsJsoup.getAllElements(url, "alistbox");
			Elements list=lidiv.select("div[id=alistbox]");
			System.out.println(url+"\t数量:"+list.size());
			for(Element li:list) {
				String line="";
				String filename="";
				int count=0;
				Elements titles=li.getElementsByClass("title");
				if(titles.size()==0) {
					System.out.println(url+"\t未发现标题");
					continue;
				}
				Element title=titles.first();
				Elements hrefarrs=title.select("a");
				if(hrefarrs.size()==0) {
					System.out.println(url+"\t未发现标题的链接");
					continue;
				}
				Element href=hrefarrs.first();
				filename=Books17Utils.filter(href.text());

				if(Books17Utils.isexist(filename)) {
					System.out.println("发现文件\t"+filename+"\t"+url);
					continue;
				}
				
				String ahref=href.attr("abs:href");
				String listhrefReal=Books17Utils.getReadurl(ahref);

				List<Element> ullist=UtilsJsoup.getAllElements(listhrefReal, "liebiao");
				if(ullist.size()==0) {
					System.out.println(url+"\t没查到列表0");
					continue;
				}
				Elements lis=ullist.get(0).select("li");
				count=lis.size();
				System.out.println(i+"\t"+url+"\t章节"+count);
				
				if(filename.length()==0) {
					System.out.println(url+"\t标题长度为0");
					continue;
				}
				if(count>100) {
					String intro="";
					Elements intros=li.getElementsByClass("intro");
					if(titles.size()>0) intro=intros.first().text();
					
					line=filename+"\t"+count+"\t"+listhrefReal+"\t"+intro+Books17Utils.enter;
					UtilsFile.writeFile(sortfile, line);
					continue;
				}
				if(listhrefReal.length()>0) {
					System.out.println("make:"+url);
					make(url,filename,lis);
					
					//makefile(listhrefReal);
				}
				System.out.println("------------------------------------------------");
			}
			
		}
		
		
		
	}
}
