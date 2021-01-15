package des.wangku.operate.standard.testing;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import des.wangku.operate.standard.testing.Books17Utils.StyleClass;
import des.wangku.operate.standard.utls.UtilsArrays;
import des.wangku.operate.standard.utls.UtilsCollect;
import des.wangku.operate.standard.utls.UtilsCollect.CollectGroup;
import des.wangku.operate.standard.utls.UtilsFile;
import des.wangku.operate.standard.utls.UtilsHtmlFilter;
import des.wangku.operate.standard.utls.UtilsJsoup;
import des.wangku.operate.standard.utls.UtilsJsoupExt;
import des.wangku.operate.standard.utls.UtilsJsoupLink;
import des.wangku.operate.standard.utls.UtilsList;
import des.wangku.operate.standard.utls.UtilsReadURL;
import des.wangku.operate.standard.utls.UtilsStringFilter;
import des.wangku.operate.standard.utls.UtilsThread;
import des.wangku.operate.standard.utls.UtilsThread.UtilsInterfaceThreadUnit;

import static des.wangku.operate.standard.utls.UtilsConsts.ACC_ENTER;


public class BooksReadUtils {
	static final String ACC_Path = "G:/Download/";
	static final String ACC_SavePath = ACC_Path + "save/";
	static final String ACC_LineSize=ACC_ENTER + ACC_ENTER + ACC_ENTER;
	static final void makebooks(String url) {
		UnitClass e=BooksReadUtils.getUC(url);
		if(e==null)return;
		Document doc=UtilsJsoupExt.getDoc(url);
		if(doc==null)return;
		String filename=UtilsJsoup.getElementFirstText(doc, e.keyName);
		filename=getFilename(e,filename);
		//System.out.println("doc:"+doc.html());
		System.out.println("filename:"+filename);
		Element f=UtilsJsoup.getElementFirst(doc, e.keyLink);
		if(f==null) {
			System.out.println("f:null");
			return;
		}
		BooksReadUtils.make(e,url, filename, f.select("a"),true);
	}
	static final void make(UnitClass uc, String url, String filename, Elements lis,boolean ismove) {
		filename = UtilsStringFilter.filterName(filename);
		if (isexist(filename)) return;
		String pathfile = ACC_Path + filename + ".txt";
		System.out.println("pathfile:"+pathfile);
		File file = new File(pathfile);
		/*-
		String fileContent=null;
		boolean isExistFile=file.exists();
		System.out.println("isExistFile:"+isExistFile);
		if(isExistFile) {
			fileContent=UtilsFile.readFileToStringByMaxLen(file, 100);
			System.out.println("fileContent:"+fileContent);
		}*/
		for (int i = 0, len = lis.size(); i < len; i++) {
			int index=i+1;
			Element li = lis.get(i);
			String name = "";
			String content = "";
			name = li.text();
			//name = Books17Utils.filter(name);
			//System.out.println("name:"+name);
			String newname = "第" + index + "章 " + name;
			String line1 = ACC_LineSize + newname + ACC_LineSize;
			/*
			if(isExistFile) {
				String newname2 = "第" + i + "章 " + name;
				if(fileContent.indexOf(newname2)>-1) {
					System.out.println("发现章节:"+name);
					continue;
					
				}
				
				
			}else{
				isExistFile=false;
			}
*/
			UtilsThread.ThreadSleep(uc.sleep);
			Elements hrefarr = li.select("a");
			if (hrefarr.size() == 0) continue;
			String href = hrefarr.get(0).attr("abs:href");
			Element condiv = UtilsJsoup.getElementFirst(href, uc.keyContent);
			if (condiv == null) continue;
			UtilsJsoup.remove(condiv, uc.keyContentRemove);
			content = condiv.html();
			content = UtilsHtmlFilter.filter(content);
			//System.out.println("content:"+content);
			UtilsFile.writeFile(file,line1);
			UtilsFile.writeFile(file, content);
			System.out.println(url + " -> " + filename + "[" + index + "/" + len + "] -> " + newname);
		}
		if(ismove) movefile(file);
		/*
		{
		 long length = file.length();
		 StyleClass sc = Books17Utils.getSC(length);
		 String to = ACC_SavePath + sc.key + "/" + filename + ".txt";
		 File endFile = new File(to);
		 file.renameTo(endFile);
		 System.out.println("移动到 -> " + to);
		}*/
		
	}
	public static void movefile(File file) {
		if(file==null || !file.exists())return;
		 long length = file.length();
		 StyleClass sc = Books17Utils.getSC(length);
		 String filename=file.getName();
		 String to = ACC_SavePath + sc.key + "/" + filename;
		 File endFile = new File(to);
		 file.renameTo(endFile);
		 System.out.println("移动到 -> " + to);
		
	}

	static final class UnitClass {
		String urlKey = "";
		String keyName = "";
		String keyNameRemove ="";
		String keyLink = "";
		String keyContent = "";
		String keyContentRemove="";
		String downfilehrefkey=null;
		boolean downfilefilebase=true;
		long sleep = 10000;
		int timeout = 20000;

		public UnitClass(String urlKey, String keyName,String keyNameRemove, 
				String keyLink, String keyContent,String keyContentRemove,String downfilehrefkey,boolean downfilefilebase, long sleep, int timeout) {
			this.urlKey = urlKey;
			this.keyName = keyName;
			this.keyNameRemove=keyNameRemove;
			this.keyLink = keyLink;
			this.keyContent = keyContent;
			this.keyContentRemove=keyContentRemove;
			this.downfilehrefkey=downfilehrefkey;
			this.downfilefilebase=downfilefilebase;
			this.sleep = sleep;
			this.timeout = timeout;
		}
	}

	static final List<UnitClass> uclist = new ArrayList<>();
	static {
		uclist.add(new UnitClass("www.yshuwu.com", "{T}h1","", "novel_list", "yuedu_zhengwen","",null,true, 2000, 10000));
		uclist.add(new UnitClass("www.niumore.com", "{T}h1","", "allchapter", "BookText","",null,true, 2000, 5000));
		uclist.add(new UnitClass("www.gazww.com", "{T}h1","", "》正文|</dl>", "content","",null,true, 2000, 10000));
		uclist.add(new UnitClass("www.riluu.com", "{T}h1","", "list", "content","",null,true, 2000, 5000));
		uclist.add(new UnitClass("m.e7007.net/mulu/", "novel-title","目录", "novel-text-list", "rd-txt","bottem",null,true, 2000, 5000));
		uclist.add(new UnitClass("www.book9.info/book", "{T}h1","", "全文阅读|</dl>||》正文|</dl>", "content","","/modules/article/txtarticle.php?id=",true, 2000, 10000));
		uclist.add(new UnitClass("www.haxixs.com/files/article/html/", "{T}h1","《,》", "chapterlist", "BookText","",null,true, 2000, 10000));
		uclist.add(new UnitClass("www.haxixs.com/files/article/info/", "{T}h1","《,》", "", "","",".txt",true, 2000, 10000));
		uclist.add(new UnitClass("www.blpv.cn/", "{T}h1","", "liebiao", "content","",null,true, 2000, 10000) );
	
	}

	static final UnitClass getUC(String url) {
		if (url == null || url.length() == 0) return null;
		url = url.trim();
		for (UnitClass e : uclist)
			if (url.indexOf(e.urlKey) > -1) return e;
		return null;
	}
	
	public static final void makelist(int maxThread,String...arr){
		String[] valArr=UtilsArrays.getFilterNullBlankValue(arr);

		List<String> keyList=UtilsList.distinct(valArr);
		
		List<CollectGroup> newlist=new ArrayList<>();
		for(String e:keyList) {
			UnitFirstClass f=new UnitFirstClass(e);
			if(f.isnew || f.lis.size()>0)newlist.add(f);
		}
		
		List<List<CollectGroup>> list2 = UtilsCollect.groupAverage(newlist, maxThread, false);
		UtilsCollect.showList(list2);
		
		if(BooksRead.isgo) {
			List<List<UtilsInterfaceThreadUnit>> workList2 = new ArrayList<>(list2.size());
			for(List<CollectGroup> list3:list2) {
				List<UtilsInterfaceThreadUnit> newlist2=new ArrayList<>();
				for(CollectGroup f:list3) {
					UnitFirstClass g=(UnitFirstClass)f;
					ReadWorkOne t=new ReadWorkOne(g);
					newlist2.add(t);
				}
				workList2.add(newlist2);
			}
			
			UtilsThread.startThreadWork(workList2);
		}
		/*
		List<UtilsInterfaceThreadUnit> workList = new ArrayList<>(maxThread);
		for (String e :valArr)
			workList.add(new ReadWorkOne(e));
		UtilsThread.startThreadWork(workList, maxThread);
		*/
	}
	static final String getFilename(UnitClass e,String filename) {
		if(e.keyNameRemove!=null && e.keyNameRemove.trim().length()>0) {
			String remove=e.keyNameRemove.trim();
			String[] arr=remove.split(",");
			for(String ee:arr)
				filename = filename.replaceAll(ee,"");
			
		}
		return filename;
	}
	static final class UnitFirstClass implements CollectGroup{
		String url;
		UnitClass e=null;
		String filename;
		boolean isnew=true;
		Elements lis=new Elements();
		public UnitFirstClass(String url) {
			this.url=url;
			e=BooksReadUtils.getUC(this.url);
			if(e==null) {
				System.out.println("e:null\t"+url);
				isnew=false;
				return;
			};
			Document doc=UtilsJsoupExt.getDoc(this.url);
			if(doc==null) {
				System.out.println("doc:null\t"+url);
				isnew=false;
				return;
			};
			this.filename=UtilsJsoup.getElementFirstText(doc, e.keyName);
			this.filename=getFilename(e,filename);
			if (isexist(filename)) {
				isnew=false;
				return;
			}
			Element f=UtilsJsoup.getElementFirst(doc, e.keyLink);
			if(f==null) {
				System.out.println("keyLink\t"+e.keyLink);
				System.out.println("f:null\t"+url);
				isnew=false;
				return;
			}
			lis=f.select("a");
		}

		@Override
		public int number() {
			return  lis.size();
		}

		@Override
		public String toString() {
			return "[" + filename + "/"+url+"]";
		}
		
		
		
		
	}
	
	static final class ReadWorkOne implements UtilsInterfaceThreadUnit{
		String url= null;
		UnitFirstClass uc=null;
		public ReadWorkOne(String url) {
			this.url=url;
		}
		public ReadWorkOne(UnitFirstClass uc) {
			this.uc=uc;			
		}
		@Override
		public void run() {
			if(uc==null) {makebooks(url);return;}
			String keydownfilehref=uc.e.downfilehrefkey;
			if(keydownfilehref!=null && keydownfilehref.length()>0) {
				String itemurl=uc.url;
				List<String> list= UtilsJsoupLink.getHrefAllIndexOf(itemurl,keydownfilehref);
				if(list.size()==0)return;
				String urldownfile=list.get(0);
				File file = null;
				if(uc.e.downfilefilebase)
				file=UtilsReadURL.downfile(urldownfile, ACC_Path);
				else {
					String filename=uc.filename;
					file=UtilsReadURL.downfile(urldownfile,filename, ACC_Path);
				}
					
				BooksReadUtils.movefile(file);
				
				return;
			}
			BooksReadUtils.make(uc.e,uc.url, uc.filename, uc.lis,true);
		}
		
	}
	static final boolean isexist(String filename) {
		File file = UtilsFile.getFilePath(ACC_SavePath, filename + ".txt");
		//if (file != null) 
			//logger.debug("发现同名文件\t" + file+"\t"+file.lastModified());
		return file != null;
		/*
		 * for(StyleClass e:sclist) {
		 * String key=e.key;
		 * String p=Books17.ACC_Path+key+"/"+filename+".txt";
		 * File existfile=new File(p);
		 * if(existfile.exists()) {
		 * System.out.println("发现同名文件\t"+p);
		 * return true;
		 * }
		 * }
		 * return false;
		 */
	}
	public static final void donwfile(String url) {
		UnitClass ee=BooksReadUtils.getUC(url);
		if(ee==null)return;
		Document doc = UtilsJsoupExt.getDoc(url);
		if(doc==null)return;
		String filename=UtilsJsoup.getElementFirstText(doc, ee.keyName)+".txt";
		if (isexist(filename)) return;
		List<String> list=UtilsJsoupLink.getHrefAllIndexOf(doc, ee.downfilehrefkey);
		if(list.size()==0)return;
		String downfileurl=list.get(0);
		//System.out.println("down:"+downfileurl);
		File file=UtilsReadURL.downfile(downfileurl,filename+".txt",BooksReadUtils.ACC_Path );
		BooksReadUtils.movefile(file);
	}
	

	public static void main2(String[] args) 
	{
		String url="https://www.book9.info/modules/article/search.php?";
		url+="searchkey=%D1%DE%CA%B7&searchtype=articlename&page=1";
		List<String> list=UtilsJsoupLink.getHrefAll(url, "odd");
		System.out.println();
		for(String e:list)
			System.out.println("\""+e+"\",");
	}

	public static void main(String[] args) 
	{
		//String url="https://www.book9.info/xiaoshuo/6/586.html";
		int index=552;
		int forword=5;
		for(int i=index;i>index-forword;i--) {
			String url="https://www.book9.info/xiaoshuo/6/"+i+".html";
			Element content=UtilsJsoup.getElementFirst(url, "newscontent");
			Element contentleft=content.getElementsByClass("l").first();
			System.out.println("//"+url);
			Elements lis=contentleft.select("li");
			for(Element e:lis) {
				Element t=e.getElementsByClass("s2").first();
				Element t1=e.getElementsByClass("s3").first();
				String con=t1==null?"":t1.text();
				String name=t.text();
				String href=t.select("a[href]").first().attr("abs:href");
				System.out.println("\""+href+"\",\t   /*"+name+"\t"+con+"*/");	
	
			}
		}
	}
	
}
