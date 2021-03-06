package des.wangku.operate.standard.testing;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import des.wangku.operate.standard.utls.UtilsFile;
import des.wangku.operate.standard.utls.UtilsJsoup;
import des.wangku.operate.standard.utls.UtilsStringFilter;

public class Books17Utils {
	/** 日志 */
	static final Logger logger = LoggerFactory.getLogger(Books17Utils.class);

	static final String[] ACC_catalog = { "短篇", "中篇", "长篇", "超长篇" };

	public static class StyleClass {
		public String key = null;
		long min = 0;
		long max = 0;

		public StyleClass(String key, long min, long max) {
			this.key = key;
			this.min = min;
			this.max = max;
		}
	}

	static final List<StyleClass> sclist = new ArrayList<>();
	static {
		sclist.add(new StyleClass("短篇", 0, 100 * 1024));
		sclist.add(new StyleClass("中篇", 100 * 1024, 1 * 1024 * 1024));
		sclist.add(new StyleClass("长篇", 1 * 1024 * 1024, 2 * 1024 * 1024));
		sclist.add(new StyleClass("超长篇", 2 * 1024 * 1024, -1));
	}

	public static final StyleClass getSC(long len) {
		for (StyleClass e : Books17Utils.sclist)
			if ((e.max == -1 && len > e.min) || (e.max > -1 && len > e.min && len <= e.max)) return e;
		return sclist.get(0);
	}

	static final String enter = System.getProperty("line.separator");

	static final String getReadurl(String url) {
		if (url == null || url.length() == 0) return url;
		if (url.indexOf("/book/") < 0) return url;
		List<Element> list = UtilsJsoup.getElementAll(url, "btopt");
		if (list.size() == 0) return "";
		Element f = list.get(0);
		Elements as = f.select("a[href]");
		if (as.size() == 0) return "";
		Element g = as.first();
		String href = g.attr("abs:href");
		return href;
	}


	static final String filter(String content) {
		content = content.replace('〇', 'o');
		content = content.toLowerCase();
		content = content.replaceAll("c0m", "com");
		content = content.replaceAll("2u2u2u.com", "");
		content = content.replaceAll("4f4f4f.com", "");
		return content;
	}

	static final String getfilename(String url) {
		String filename;
		List<Element> ullist2 = UtilsJsoup.getElementAll(url, "infot");
		if (ullist2.size() == 0) return null;
		filename = ullist2.get(0).text();
		filename = filter(filename);
		filename = UtilsStringFilter.filterName(filename);
		return filename;
	}

	static final boolean isexist(String filename) {
		File file = UtilsFile.getFilePath(Books17.ACC_Path, filename + ".txt");
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
}
