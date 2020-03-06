package des.wangku.operate.standard.subengineering;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.poi.ss.util.CellRangeAddress;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import des.wangku.operate.standard.utls.UtilsExcel;
import des.wangku.operate.standard.utls.UtilsExcel.SheetClass;
import des.wangku.operate.standard.utls.UtilsFile;
import des.wangku.operate.standard.utls.UtilsJsoup;
import des.wangku.operate.standard.utls.UtilsReadURL;
import des.wangku.operate.standard.utls.UtilsRnd;

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
	public static final BakeClass getBaiKeTagInfo(String name){
		BakeClass out=new BakeClass(name,"","");
		String url="https://baike.baidu.com/item/"+name;
		String content=UtilsReadURL.getReadUrlDisJs(url);
		Document doc=Jsoup.parse(content);/* 查出简介 */
		if(doc==null)return out;
		Elements contents=doc.getElementsByClass("main-content");
		if(contents==null || contents.size()==0)return out;
		Element maincontent=contents.first();
		Elements els=maincontent.getElementsByClass("lemma-summary");
		String html="";
		content="";
		if(els.size()>0) {
			html=els.first().html();
			content=els.first().text();
		}
		out.html=html;
		out.text=content;
		
		String[] removeClass= {
				"excellent-icon","lemma-summary","city-guide",
				"basic-info","lemmaWgt-lemmaCatalog","lemmaWgt-promotion-leadPVBtn",
				"lemmaWgt-lemmaTitle","lemmaWgt-declaration","promotion-declaration",
				"top-tool","anchor-list",
				"album-list","tashuo-bottom",
				"lemma-reference","side-content","configModuleBanner"
				};
		UtilsJsoup.removeClassElement(maincontent, removeClass);
		String[] removeID= {"hotspotmining_s"};
		UtilsJsoup.removeIDElement(maincontent, removeID);
		
				
		
		List<BakeClass> list=new ArrayList<>();
		Elements level2s=maincontent.getElementsByClass("level-2");
		for(Element level2:level2s) {
			Element elsTitle=level2.getElementsByClass("title-text").first();
			UtilsJsoup.removeClassElement(elsTitle, "title-prefix");
			String key2=elsTitle.text();
			
			Element next=level2.nextElementSibling();
			StringBuilder sbHtml=new StringBuilder();
			StringBuilder sbText=new StringBuilder();
			BakeClass level_2=new BakeClass(key2,"","");
			if(next!=null) {
				while(next!=null) {
					if(next.hasClass("level-2"))break;
					if(next.hasClass("level-3")) {
						Element elselevel3=next.getElementsByClass("title-text").first();
						UtilsJsoup.removeClassElement(elselevel3, "title-prefix");
						String key3=elselevel3.text();
						BakeClass level_3=new BakeClass(key3,"","");
						
						

						StringBuilder sbHtml3=new StringBuilder();
						StringBuilder sbText3=new StringBuilder();
						
						
						Element next3=next.nextElementSibling();
						if(next3!=null) {
							while(next3!=null) {
								if(next3.hasClass("level-2")||next3.hasClass("level-3"))break;

								sbText3.append(next3.text().trim()+"\n\n");
								sbHtml3.append(next3.html());
								
								
								next3=next3.nextElementSibling();
							}
						}
						level_3.html=sbHtml3.toString();
						level_3.text=sbText3.toString();
						
						level_2.subList.add(level_3);
					}

					sbText.append(next.text().trim()+"\n\n");
					sbHtml.append(next.html());
					next=next.nextElementSibling();
				}
			}
			level_2.html=sbHtml.toString();
			level_2.text=sbText.toString();
			list.add(level_2);
			
		}
		out.subList=list;
		/*
		Elements arrs=maincontent.children();
		for(Element e:arrs) {
			if(e.hasClass("para-title") && e.hasClass("level-2") && e.parent()==maincontent) {
				Elements titles=e.getElementsByClass("title-text");
				if(titles.size()==0)continue;
				Element elsTitle=titles.first();
				UtilsJsoup.removeClassElement(elsTitle, "title-prefix");
				String key=elsTitle.text();
				Element next=e.nextElementSibling();
				if(next!=null) {
					StringBuilder sb=new StringBuilder();
					StringBuilder sbhtml=new StringBuilder();
					while(isPara(next)) {
						sb.append(next.text().trim()+"\n\n");
						sbhtml.append(next.html());
						next=next.nextElementSibling();
					}
					content=sb.toString();
					html=sbhtml.toString();
				}
				list.add(new BaiKeyInfo(key, html,content));
			}
		}*/
		UtilsFile.writeFile("e:/source.txt", out.toString());
		return out;
	}
	static final class BakeClass{
		String name="";
		String html="";
		String text="";
		List<BakeClass> subList=new ArrayList<>();
		public BakeClass(String name,String html,String text) {
			this.name=name;
			this.html=html;
			this.text=text;
		}
		@Override
		public String toString() {
			return "BakeClass [name=" + name + ", html=" + html + ", text=" + text + ", subList=" + subList + "]";
		}
		
	}
	static final List<BakeClass> getParaList(Element maincontent){
		List<BakeClass> list=new ArrayList<>();
		if(maincontent==null)return list;

		
		return list;
		
	}
	public static final boolean isPara(Element e) {
		if(e==null)return false;
		if(e.hasClass("para")||e.hasClass("para-list") ||e.hasClass("anchor-list"))return true;
		if(e.hasClass("para-title")) {
			Set<String> set=e.classNames();
			for(String f:set) {
				if(f.indexOf("level-")>-1) {
					if(!(f.equals("level-1")||f.equals("level-2")))return true;
				}
			}
		}
		if(e.hasClass("album-list"))return false;
		
		return true;
	}
	static final String[] areaarrs= {"北京","上海","天津","重庆",
			"哈尔滨","长春","沈阳","呼和浩特",
			"石家庄","乌鲁木齐","兰州","西宁",
			"西安","银川","郑州","济南","太原",
			"合肥","武汉","长沙","南京","成都",
			"贵阳","昆明","南宁","拉萨","杭州",
			"南昌","广州","福州","海口","台北",
			"香港","澳门"
			};

	public static void main(String[] args) {
		String filename="e:/"+UtilsRnd.getNewFilenameNow(4, 1)+".xlsx";
		List<SheetClass> listSheet=new ArrayList<>();
		List<List<String>> listFist=new ArrayList<>();
		List<CellRangeAddress> drList=new ArrayList<>();
		for(int ii=0;ii<areaarrs.length;ii++) {
			String e=areaarrs[ii];
			/* 第一行 地区名 */
			List<String> list_0=new ArrayList<>();
			list_0.add(e);
			listFist.add(list_0);
			/************************************/
			BakeClass ff=getBaiKeTagInfo(e);
			List<BakeClass> list=ff.subList;
			/* 第二行 主类名 */
			int first=listFist.size();
			List<String> list_1=new ArrayList<>();
			for(BakeClass f:list) {
				list_1.add(f.name);
				int sort2=f.subList.size();
				if(sort2>1) {
					int p=list_1.size()-1;
					drList.add(new CellRangeAddress(first, first, p, p+sort2-1));
				}
				for(int i=0;i<sort2-1;i++)list_1.add("");
			}
			listFist.add(list_1);
			/* 第二行，子类别 */
			List<String> list_2=new ArrayList<>();
			for(BakeClass f:list) {
				int sort2=f.subList.size();
				if(sort2==0) {
					list_2.add("");
					continue;
				}
				List<BakeClass> listp=f.subList;
				for(BakeClass bc:listp) {
					list_2.add(bc.name);
				}
			}
			listFist.add(list_2);
			/* 第三行具体内容 */
			List<String> list_3=new ArrayList<>();
			for(BakeClass f:list) {
				int sort2=f.subList.size();
				if(sort2==0) {
					list_3.add(f.text);
					continue;
				}
				List<BakeClass> listp=f.subList;
				for(BakeClass bc:listp) {
					list_3.add(bc.text);
				}
			}
			listFist.add(list_3);
			/* 添加空行 */
			List<String> list_space=new ArrayList<>();
			list_space.add("");
			listFist.add(list_space);
			listFist.add(list_space);
			
		}
		/* 添加一个sheet页 */
		SheetClass aa=new SheetClass("所有",listFist);
		aa.setRegionsList(drList);
		listSheet.add(aa);
		UtilsExcel.addWorkbookSheet(filename, listSheet);
		
	}
	public static void main2(String[] args) 
	{
		String[] arrs= {"北京","上海","天津","重庆",
				"哈尔滨","长春","沈阳","呼和浩特",
				"石家庄","乌鲁木齐","兰州","西宁",
				"西安","银川","郑州","济南","太原",
				"合肥","武汉","长沙","南京","成都",
				"贵阳","昆明","南宁","拉萨","杭州",
				"南昌","广州","福州","海口","台北",
				"香港","澳门"
				};
		/**
		 * 
		 */
		String filename="e:/"+UtilsRnd.getNewFilenameNow(4, 1)+".xlsx";
		List<SheetClass> ll=new ArrayList<>();
		for(String e:arrs) {
			BakeClass ff=getBaiKeTagInfo(e);
			List<BakeClass> list=ff.subList;
			List<List<String>> list2=new ArrayList<>();

			List<CellRangeAddress> drList=new ArrayList<>();
			
			/* 第一行 主类别 */
			List<String> list_1=new ArrayList<>();
			for(BakeClass f:list) {
				list_1.add(f.name);
				int sort2=f.subList.size();
				if(sort2>1) {
					int p=list_1.size()-1;
					drList.add(new CellRangeAddress(0, 0, p, p+sort2-1));
				}
				for(int i=0;i<sort2-1;i++)list_1.add("");
			}
			list2.add(list_1);
			
			/* 第二行，子类别 */
			List<String> list_2=new ArrayList<>();
			for(BakeClass f:list) {
				int sort2=f.subList.size();
				if(sort2==0) {
					list_2.add("");
					continue;
				}
				List<BakeClass> listp=f.subList;
				for(BakeClass bc:listp) {
					list_2.add(bc.name);
				}
			}
			list2.add(list_2);
			
			
			/* 第三行具体内容 */
			List<String> list_3=new ArrayList<>();
			for(BakeClass f:list) {
				int sort2=f.subList.size();
				if(sort2==0) {
					list_3.add(f.text);
					continue;
				}
				List<BakeClass> listp=f.subList;
				for(BakeClass bc:listp) {
					list_3.add(bc.text);
				}
			}
			list2.add(list_3);
			/* 添加一个sheet页 */
			SheetClass aa=new SheetClass(e,list2);
			aa.setRegionsList(drList);
			ll.add(aa);
		}
		UtilsExcel.addWorkbookSheet(filename, ll);
	}	
	
	
}
