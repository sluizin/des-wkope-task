package des.wangku.operate.standard.utls;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.eclipse.swt.widgets.TableItem;

import com.alibaba.fastjson.JSONObject;

import des.wangku.operate.standard.swt.ResultTable;

/**
 * Table常用方法 工具
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 *
 */
public final class UtilsSWTTableUtils {
	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(UtilsSWTTableUtils.class);
	/**
	 * 把多行选择与选中记录放入粘贴板
	 * @param table ResultTable
	 * @return boolean
	 */
	public static final boolean copyMultiLineToClipboard(ResultTable table) {
		logger.debug("copyMultiline to Clipboard!!!");
		TableItem[] arr = UtilsSWTTable.getSelectCheckTableItemAllArray(table);
		if (arr.length == 0) return false;
		String itemString = tableItemToString(arr);
		UtilsClipboard.copy(itemString);
		return true;
	}

	/**
	 * 多行输出成字符串
	 * @param itemArr TableItem[]
	 * @return String
	 */
	public static final String tableItemToString(TableItem... itemArr) {
		if (itemArr == null || itemArr.length == 0) return null;
		StringBuilder sb = new StringBuilder();
		int i = 0, ii = 0, len;
		for (; ii < itemArr.length; ii++) {
			TableItem item = itemArr[ii];
			len = item.getParent().getColumnCount();
			if (ii > 0) sb.append(System.getProperty("line.separator"));
			for (i = 0; i < len; i++) {
				if (i > 0) sb.append('\t');
				sb.append(item.getText(i));
			}
		}
		return sb.toString();
	}
	/**
	 * 判断字符串是否为json格式
	 * @param content String
	 * @return boolean
	 */
	@SuppressWarnings("unused")
	public boolean isJson(String content){
	    try {
	        JSONObject jsonStr= JSONObject.parseObject(content);
	        return  true;
	   } catch (Exception e) {
	        return false;
	  }
	}
}
