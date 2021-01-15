package des.wangku.operate.standard.subengineering.reediting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import des.wangku.operate.standard.utls.UtilsFile;
import des.wangku.operate.standard.utls.UtilsList;

/**
 * 非法词
 * 
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class Badword {
	List<String> list=new ArrayList<>();
	public Badword(String filename) {
		if(filename==null)return;
		String content=UtilsFile.readFile(filename).toString();
		String[] arrs=content.split("\n");
		Collections.addAll(list, arrs);
		list=UtilsList.getOrderListByLenDESC(list);
	}
	public String getResult(String content) {
		List<String> resultlist=new LinkedList<>();
		for(String e:list) {
			int index=content.indexOf(e);
			if(index>0)resultlist.add(e);
		}
		System.out.println("find bad words:"+resultlist);
		for(String e:resultlist) {
			content=content.replaceAll(e, rep(e.length()));
		}
		return content;
	}
	public static final String rep(String key) {
		return rep(key.length());
	}
	public static final String rep(int len) {
		if(len<=0|| len>50)return "";
		char[] arr=new char[len];
		for(int i=0;i<len;i++)
			arr[i]='*';
		return new String(arr);
	}
}
