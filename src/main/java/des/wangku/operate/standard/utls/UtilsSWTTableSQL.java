package des.wangku.operate.standard.utls;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/**
 * 针对Table的操作
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class UtilsSWTTableSQL {
	/** 日志 */
	static final Logger logger = LoggerFactory.getLogger(UtilsSWTTableSQL.class);

	/**
	 * 得到某行数组
	 * @param item TableItem
	 * @return String[]
	 */
	public static final synchronized String[] get(TableItem item) {
		String[] strs = {};
		if (item == null) return strs;
		Table table = item.getParent();
		int count = table.getColumnCount();
		String[] strs2 = new String[count];
		for (int i = 0; i < count; i++) {
			strs2[i] = item.getText(i);
		}
		return strs2;

	}

	/**
	 * 得到某行中的某列
	 * @param e TableItem
	 * @param y int
	 * @return String
	 */
	public static final synchronized String getColumnValue(TableItem e, int y) {
		if (e == null) return null;
		String[] arrs = get(e);
		if (y < 0 || y >= arrs.length) return null;
		return arrs[y];
	}

	/**
	 * 更新
	 * @param e Table
	 * @param x int
	 * @param y int
	 * @param value int
	 */
	public static final synchronized void update(Table e, int x, int y, int value) {
		update(e,x,y,value+"");
	}
	/**
	 * 更新
	 * @param e Table
	 * @param x int
	 * @param y int
	 * @param value String
	 */
	public static final synchronized void update(Table e, int x, int y, String value) {
		if (e == null || value == null) return;
		if (x < 0 || x >= e.getItemCount()) return;
		if (y < 0 || y >= e.getColumnCount()) return;
		e.getItem(x).setText(y, value);
	}

	/**
	 * 添加字符串数组进入TableItem中。如果出现null为填充为""
	 * @param item TableItem
	 * @param arrs String[]
	 */
	@SuppressWarnings("unused")
	private static final synchronized void setText(TableItem item, String... arrs) {
		if (item == null || arrs == null || arrs.length == 0) return;
		String[] arr = new String[item.getParent().getColumnCount()];
		for (int i = 0; i < arr.length; i++)
			arr[i] = (i < arrs.length && arrs[i] != null) ? arrs[i] : "";
		item.setText(arr);

	}

}
