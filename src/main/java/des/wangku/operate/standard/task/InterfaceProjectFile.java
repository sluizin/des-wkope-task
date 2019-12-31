package des.wangku.operate.standard.task;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 
 * 
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public interface InterfaceProjectFile {
	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(InterfaceProjectFile.class);
	/**
	 * 在model目录建立d:/XXX/XXX/model/{des-wkope-task-}XXXX.xlsx
	 * @param fileExt String
	 * @return File
	 */
	public default File mkModelFile(String fileExt) {
		Object obj = this;
		if(!(obj instanceof AbstractTask)) return null;
		AbstractTask t = (AbstractTask) obj;
		String path = t.getBaseSourceFile(fileExt);
		File p = new File(path);
		if(!p.exists()) {
			try {
				p.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		if(p.isFile())return p;
		return null;
	}
}
