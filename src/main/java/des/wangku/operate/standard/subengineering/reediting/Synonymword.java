package des.wangku.operate.standard.subengineering.reediting;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import des.wangku.operate.standard.utls.UtilsFile;
import des.wangku.operate.standard.utls.UtilsRnd;

/**
 * 同义词
 * 
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class Synonymword {
	String filename=null;
	File file= null;
	String[] array= {};
	public Synonymword(String filename) {
		if(filename==null)return;
		File f=new File(filename);
		if(!f.exists())return;
		this.file=f;
		init0();
	}
	public Synonymword(File file) {
		if(!file.exists())return;
		this.file=file;
		init0();
	}
	void init0() {
		this.filename=file.getAbsolutePath();
		init();		
	}
	void init() {
		if(filename==null)return;
		String content=UtilsFile.readFile(filename).toString();
		array=content.split("\n");
	}
	public Set<String> getResult(String...keyArray) {
		Set<String> set = new TreeSet<>();
		for(String line:array) {
			if(isExist(line,keyArray)) {
				set.addAll(decomposeWordLine(line,keyArray));
			}			
		}
		return set;
	}
	public String getResultRndValue(String...keyArray) {
		Set<String> set = getResult(keyArray);
		int len=set.size();
		if(len==0)return null;
		int index=UtilsRnd.getRndInt(0,len-1);
		String[] a= {};
		a=set.toArray(a);
		return a[index];
	}
	public static void main(String[] args) 
	{
		System.out.println("Hello World!");
		String file="D:\\Eclipse\\eclipse-oxygen\\Workspaces\\des-wkope\\build\\libs\\model\\des-wkope-task-p016_synonymword.txt";
		Synonymword s=new Synonymword(file);
		String key="严重";
		Set<String>set=s.getResult(key);
		for(String e:set) {
			System.out.print(e+"\t");
		}
		System.out.println();
		System.out.println(s.getResultRndValue(key));
	}
	static final boolean isExist(String line ,String... array) {
		if(line==null || array.length==0)return false;
		for(String key:array)
			if(line.indexOf(","+key+",")>-1) 
				return true;
		return false;
	}

	/**
	 * 分解word字符串，把以,为间隔的字符串分成list，并过滤空
	 * @param line String
	 * @return List&lt;String&gt;
	 */
	static final List<String> decomposeWordLine(String line,String...filtwords) {
		if (line == null || line.trim().length() == 0) return new ArrayList<>();
		String[] arrs = line.split(",");
		List<String> list = new ArrayList<>(arrs.length);
		loop:for (String e : arrs) {
			if (e == null) continue;
			e = e.trim();
			if (e.length() == 0) continue;
			for(String f:filtwords) {
				if(e.equals(f))continue loop;
			}
			list.add(e);
		}
		return list;
	}
}
