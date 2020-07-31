package des.wangku.operate.standard.subengineering.dictionaries;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.List;

import des.wangku.operate.standard.utls.UtilsCode;
import des.wangku.operate.standard.utls.UtilsList;

/**
 * 字典 来自文本文件，按行检索
 * 
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class DictTxt {
	String dicfile=null;
	boolean isUtf8=true;
	String splitWord="\t";
	int lineval=0;
	public DictTxt() {
		
	}
	public List<String> getDictList(String content){
		List<String> list=new ArrayList<>();
		File file=new File(dicfile);
		try (RandomAccessFile randomFile = new RandomAccessFile(file, "r"); FileChannel filechannel = randomFile.getChannel();) {
			randomFile.seek(0);
			FileLock lock;
			do {
				lock = filechannel.tryLock(0L, Long.MAX_VALUE, true);
			} while (null == lock);
			Thread.sleep(10);
			while (randomFile.getFilePointer() < randomFile.length()) {
				String line = isUtf8 ? UtilsCode.changedLine(randomFile.readLine()) : randomFile.readLine();
				if (line == null) continue;
				String str = line.trim();
				if (str.length() == 0) continue;;
				String[] arrs=str.split(splitWord);
				if(arrs.length<=lineval)continue;
				String val=arrs[lineval];
				if(content.indexOf(str)>-1)	list.add(val);
			}
			if (lock != null) lock.release();
			randomFile.close();
		} catch (Exception e) {
		}
		if(list.size()<=1)return list;
		return UtilsList.getOrderListByLenDESC(list);
	}
}
