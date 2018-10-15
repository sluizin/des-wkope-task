package des.wangku.operate.standard.swt;

import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;

import des.wangku.operate.standard.utls.UtilsProperties;

/**
 * 参数
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class ResultTableParameter implements InterfaceProperties {
	/** 日志 */
	static Logger logger = Logger.getLogger(ResultTableParameter.class);
	Properties properties = null;
	/** sheet名称 */
	String title = null;
	/** 是否需要显示表头 */
	boolean isViewHead = true;
	/** 第N行是否为头 所有sheet统一 */
	int headRowSuffix;
	/** 宽度设置 */
	int[] widthArray = {};
	/** 设置默认宽度 120 */
	int defTCWidth = 150;
	/** 是否自适应变宽度 */
	boolean isAutoWidth = false;
	/** 是否自动删除多余行 上方 */
	boolean isAutoremoveUpNullRows = false;
	/** 是否自动删除多余行 下方 */
	boolean isAutoremoveDownNullRows = false;
	/** 是否去掉首尾空格 */
	boolean isTrim = false;
	/** 只读列，不允许修改 */
	int[] readonlySuffix = {};
	/** 属性是否需要下标 如::[0]XXX */
	boolean attrSuffix = false;

	public ResultTableParameter() {

	}

	public ResultTableParameter(ResultTable obj) {
		this.title = obj.title;
		this.properties = obj.properties;
	}

	public ResultTableParameter(Properties properties, String title) {
		this.title = title;
		this.properties = properties;
	}

	public final String getTitle() {
		return title;
	}

	public final void setTitle(String title) {
		this.title = title;
	}

	public final boolean isViewHead() {
		return isViewHead;
	}

	public final void setViewHead(boolean isViewHead) {
		this.isViewHead = isViewHead;
	}

	public final int getHeadRowSuffix() {
		return headRowSuffix;
	}

	public final void setHeadRowSuffix(int headRowSuffix) {
		this.headRowSuffix = headRowSuffix;
	}

	public final int[] getWidthArray() {
		return widthArray;
	}

	public final void setWidthArray(int[] widthArray) {
		this.widthArray = widthArray;
	}

	public final int getDefTCWidth() {
		return defTCWidth;
	}

	public final void setDefTCWidth(int defTCWidth) {
		this.defTCWidth = defTCWidth;
	}

	public final boolean isAutoWidth() {
		return isAutoWidth;
	}

	public final void setAutoWidth(boolean isAutoWidth) {
		this.isAutoWidth = isAutoWidth;
	}

	public final boolean isAutoremoveUpNullRows() {
		return isAutoremoveUpNullRows;
	}

	public final void setAutoremoveUpNullRows(boolean isAutoremoveUpNullRows) {
		this.isAutoremoveUpNullRows = isAutoremoveUpNullRows;
	}

	public final boolean isAutoremoveDownNullRows() {
		return isAutoremoveDownNullRows;
	}

	public final void setAutoremoveDownNullRows(boolean isAutoremoveDownNullRows) {
		this.isAutoremoveDownNullRows = isAutoremoveDownNullRows;
	}

	public final boolean isTrim() {
		return isTrim;
	}

	public final void setTrim(boolean isTrim) {
		this.isTrim = isTrim;
	}

	@Override
	public Properties getProperties() {
		return properties;
	}

	public final int[] getReadonlySuffix() {
		return readonlySuffix;
	}

	public final void setReadonlySuffix(int[] readonlySuffix) {
		this.readonlySuffix = readonlySuffix;
	}

	public final boolean isAttrSuffix() {
		return attrSuffix;
	}

	public final void setAttrSuffix(boolean attrSuffix) {
		this.attrSuffix = attrSuffix;
	}

	/**
	 * 判断列下标是否只读
	 * @param index int
	 * @return boolean
	 */
	public final boolean isReadonly(int index) {
		for (int ff : readonlySuffix)
			if (ff == index) return true;
		return false;
	}

	/**
	 * 获取ECT_Para_0参数
	 * @param properties Properties
	 * @param title String
	 * @return ResultTableParameter
	 */
	public static final ResultTableParameter getRTP(Properties properties, String title) {
		if (properties == null || title == null) return new ResultTableParameter(properties, title);
		List<String> list = UtilsProperties.getPropMultiIndexofValue(properties, true, "ECT_Para_");
		for (String str : list) {
			ResultTableParameter f = JSON.parseObject(str, ResultTableParameter.class);
			if (f != null && title.equals(f.title)) { return f; }
		}
		return new ResultTableParameter(properties, title);
	}

	/**
	 * 从json串中获取参数
	 * @param jsonStr String
	 * @param title String
	 * @return ResultTableParameter
	 */
	public static final ResultTableParameter getRTP(String jsonStr,String title) {
		if (jsonStr == null || jsonStr.length() == 0) return new ResultTableParameter(null, title);
		logger.debug("=="+jsonStr);
		ResultTableParameter f = JSON.parseObject(jsonStr, ResultTableParameter.class);
		if (f != null) return f;
		return new ResultTableParameter(null, title);
	}
	/**
	 * 从json串中获取参数
	 * @param jsonStr String
	 * @return ResultTableParameter
	 */
	public static final ResultTableParameter getRTP(String jsonStr) {
		return getRTP(jsonStr, null);
	}
}
