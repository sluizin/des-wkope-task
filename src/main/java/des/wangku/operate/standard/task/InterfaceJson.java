package des.wangku.operate.standard.task;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import des.wangku.operate.standard.utls.UtilsFile;

/**
 * 系统级json信息的提取
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public interface InterfaceJson {
	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(InterfaceJson.class);

	/**
	 * 从包外读取项目json文件<br>
	 * 默认为mode/des-wkope-task-p0X.json<br>
	 * 支持中文<br>
	 * @return String
	 */
	public default String getProJson() {
		Object obj = this;
		AbstractTask t = (AbstractTask) obj;
		String json = "";
		String filename = t.getModelProjectFileLeft() + ".json";
		File file = new File(filename);
		if (!file.exists() || !file.isFile()) {
			logger.debug("未发现项目Json文件:" + filename);
			return json;
		}
		try (InputStream is2 = new FileInputStream(file); InputStream in = new BufferedInputStream(is2);) {
			json = UtilsFile.readFile(in).toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return json;
	}

	/**
	 * 从包外读取项目json文件<br>
	 * 默认为mode/des-wkope-task-p0X.json<br>
	 * 支持中文<br>
	 * 从此对象中提取某个关键字的字符串<br>
	 * @param key String
	 * @return String
	 */
	public default String getProJsonValue(String key) {
		String json = getProJson();
		if (json == null || json.length() == 0) return "";
		JSONObject obj = JSONObject.parseObject(json);
		if (obj == null) return "";
		return obj.getString(key);
	}

}
