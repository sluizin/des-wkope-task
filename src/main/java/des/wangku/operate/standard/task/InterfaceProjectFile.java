package des.wangku.operate.standard.task;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 项目文件，包含扩展文件的信息
 * 
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public interface InterfaceProjectFile extends InterfaceProjectTop {
	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(InterfaceProjectFile.class);
	/**
	 * 在model目录建立d:/XXX/XXX/model/{des-wkope-task-}XXXX.xlsx
	 * @param fileExt String
	 * @return File
	 */
	public default File mkModelFile(String fileExt) {
		return mkModelFile(null,fileExt);
	}
	/**
	 * 在model目录建立d:/XXX/XXX/model/{des-wkope-task-}XXXX.xlsx
	 * @param ext String
	 * @param fileExt String
	 * @return File
	 */
	public default File mkModelFile(String ext,String fileExt) {
		AbstractTask t = this.getProejectBase();
		if(t==null) return null;
		String path = t.getBaseSourceFile(ext,fileExt);
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
	public default String getProjectFileLeftHead() {
		return "";
	}
}
