package des.wangku.operate.standard.utls;

import java.util.List;
import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import des.wangku.operate.standard.swt.ResultTable;

/**
 * get、update、insert
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class UtilsSWTTableSQL {
	/** 日志 */
	static final Logger logger = Logger.getLogger(UtilsSWTTableSQL.class);

	/**
	 * 得到某行数组
	 * @param item TableItem
	 * @return String[]
	 */
	public static final synchronized String[] get(TableItem item) {
		String[] strs = {};
		if (item == null) return strs;
		ResultTable table = (ResultTable) item.getParent();
		return get(table, item);
	}

	/**
	 * 得到某行数组
	 * @param table ResultTable
	 * @param item TableItem
	 * @return String[]
	 */
	public static final synchronized String[] get(ResultTable table, TableItem item) {
		String[] strNull = {};
		if (table == null) return strNull;
		int count = table.getColumnCount();
		String[] strs = new String[count];
		for (int i = 0; i < count; i++)
			strs[i] = item.getText(i);
		return strs;
	}

	/**
	 * 得到table中的某个值
	 * @param e ResultTable
	 * @param x int
	 * @param y int
	 * @return String
	 */
	public static final synchronized String get(ResultTable e, int x, int y) {
		if (e == null) return null;
		if (x < 0 || x >= e.getItemCount()) return null;
		if (y < 0 || y >= e.getColumnCount()) return null;
		//return e.getData().get(x)[y];
		return e.getItem(x).getText(y);
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
	 * @param e ResultTable
	 * @param x int
	 * @param y int
	 * @param value String
	 */
	public static final synchronized void update(ResultTable e, int x, int y, String value) {
		if (e == null || value == null) return;
		if (x < 0 || x >= e.getItemCount()) return;
		if (y < 0 || y >= e.getColumnCount()) return;
		//e.getData().get(x)[y]=value;
		//e.redrawTable();
		e.getItem(x).setText(y, value);
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
		//e.getData().get(x)[y]=value;
		//e.redrawTable();
	}
	/**
	 * 更新
	 * @param e TableItem
	 * @param column int
	 * @param value String
	 */
	public static final synchronized void updateDDD(TableItem e, int column, String value) {
		if (e == null) return;
		String[] arrs = get(e);
		int len = arrs.length;
		if (column < 0 || column >= len) return;
		arrs[column] = value;
		e.setText(arrs);
	}

	/**
	 * 插入list List&lt;String&gt;
	 * @param table ResultTable
	 * @param point int
	 * @param arrs String[]
	 */
	public static synchronized void insert(ResultTable table, int point, String... arrs) {
		if (point < 0 || point >= table.getItemCount()) {
			add(table, arrs);
			return;
		}
		String[] newArr=getFormatArray(table,arrs);
		//table.getData().add(point,newArr);
		//table.redrawTable();
		table.addTableItem(point, newArr);
		/*
		setText(item, arrs);*/
	}

	/**
	 * 添加list List &lt; String &gt; 
	 * @param table ResultTable
	 * @param list List &lt; String &gt; 
	 */
	public static synchronized void add(ResultTable table, List<String> list) {
		if (list.size() == 0) return;
		String[] arr = {};
		add(table, list.toArray(arr));
	}

	/**
	 * 添加list List&lt;String&gt;
	 * @param table ResultTable
	 * @param arrs String []
	 */
	public static synchronized void add(ResultTable table, String... arrs) {
		if (arrs.length == 0) return;
		String[] newArr=getFormatArray(table,arrs);
		//table.getData().add(newArr);
		//table.redrawTable();
		table.addTableItem(-1,newArr);
		//setText(item, arrs);
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
	/**
	 * 得到扩展数组值，规则化
	 * @param table ResultTable
	 * @param arrs String[]
	 * @return String[]
	 */
	private static final String[] getFormatArray(ResultTable table, String... arrs) {
		String[] arr = new String[table.getColumnCount()];
		for (int i = 0; i < arr.length; i++)
			arr[i] = (i < arrs.length && arrs[i] != null) ? arrs[i] : "";
		return arr;
	}

	/**
	 * 把粘贴板中的数据存入到表中
	 * @param table ResultTable
	 * @param point int
	 */
	public static synchronized void addClipboard(ResultTable table, int point) {
		String content = UtilsClipboard.getString();
		if (content == null || content.length() == 0) return;
		/*
		 * 此处判断粘贴板内容格式，默认为text格式
		 * 如果是json，需要确定json格式，如果是xml，都需要确定格式
		 */
		String[] arr = content.split(System.getProperty("line.separator"));
		for (int i = 0; i < arr.length; i++) {
			String[] arrLine = arr[i].split("\t");
			insert(table, point, arrLine);
		}

	}
}
