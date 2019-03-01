package des.wangku.operate.standard.utls;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import des.wangku.operate.standard.swt.ResultTable;
import des.wangku.operate.standard.task.InterfaceCollect;

/**
 * 针对Table的一些工具方法
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class UtilsSWTTable {
	/** 日志 */
	static final Logger logger = LoggerFactory.getLogger(UtilsSWTTable.class);

	/**
	 * 得到Table中的选中行，先鼠标选中行，再加上checked选中行
	 * @param table Table
	 * @return TableItem[]
	 */
	@Deprecated
	public static final TableItem[] getSelectTableItem(ResultTable table) {
		List<TableItem> list = new ArrayList<TableItem>();
		if (table.getSelection().length > 0) list = new ArrayList<TableItem>(Arrays.asList(table.getSelection()));
		TableItem[] all = table.getItems();
		for (int i = 0; i < all.length; i++) {
			TableItem e = all[i];
			if (e.getChecked() && (!list.contains(e))) list.add(e);
		}
		TableItem[] arrnull = {};
		return list.toArray(arrnull);
	}

	/**
	 * 得到Table中的选中行，得到鼠标选中行+checked选中行<br>
	 * 从小到大<br>
	 * @param table Table
	 * @return TableItem[]
	 */
	public static final TableItem[] getSelectCheckTableItemAllArray(ResultTable table) {
		List<TableItem> list = getSelectCheckTableItemAllList(table);
		TableItem[] arrnull = {};
		return list.toArray(arrnull);
	}

	/**
	 * 得到Table中的选中行，得到鼠标选中行+checked选中行<br>
	 * 从小到大<br>
	 * @param table Table
	 * @return List&lt;TableItem&gt;
	 */
	public static final List<TableItem> getSelectCheckTableItemAllList(ResultTable table) {
		int[] arrays = getSelectCheckTableItemAllSuffix(table);
		List<TableItem> list = new ArrayList<>(arrays.length);
		for (int i = 0, len = arrays.length; i < len; i++)
			list.add(table.getItem(arrays[i]));
		return list;
	}

	/**
	 * 判断选择与选中项中最小下标，如果没有则返回-1
	 * @param table Table
	 * @return int
	 */
	public static final int getSelectCheckTableItemAllSuffixMin(ResultTable table) {
		TableItem[] all = table.getItems();
		TableItem[] arrs = table.getSelection();
		for (int i = 0; i < all.length; i++) {
			TableItem e = all[i];
			if (e.getChecked() || isSelect(e, arrs)) return i;
		}
		return -1;
	}

	/**
	 * 得到Table中的选中行的下标编号，得到鼠标选中行+checked选中行<br>
	 * 从小到大<br>
	 * @param table Table
	 * @return int[]
	 */
	public static final int[] getSelectCheckTableItemAllSuffix(ResultTable table) {
		List<Integer> list = new ArrayList<>();
		TableItem[] arrs = table.getSelection();
		TableItem[] all = table.getItems();
		for (int i = 0; i < all.length; i++) {
			TableItem e = all[i];
			if (e.getChecked() || isSelect(e, arrs)) list.add(i);
		}
		int[] result = new int[list.size()];
		for (int i = 0, len = list.size(); i < len; i++)
			result[i] = list.get(i);
		return result;
	}

	/**
	 * 判断行是否在行组中
	 * @param e TableItem
	 * @param arrs TableItem[]
	 * @return boolean
	 */
	private static final boolean isSelect(TableItem e, TableItem... arrs) {
		if (e == null || arrs.length == 0) return false;
		for (int i = 0, len = arrs.length; i < len; i++)
			if (e.equals(arrs[i])) return true;
		return false;
	}

	/**
	 * 把表格换成list数组 是否需要头部信息
	 * @param isHead boolean
	 * @param table ResultTable
	 * @param itemArr TableItem[]
	 * @return List&lt;List&lt;String&gt;&gt;
	 */
	public static final List<List<String>> getTableItemList(boolean isHead, ResultTable table, TableItem... itemArr) {
		List<List<String>> list = new ArrayList<>();
		if (isHead) {
			/*
			 * List<String> ll = getTableHeadList(itemArr);
			 * if (ll != null) list.add(ll);
			 */
			list.add(UtilsSWTTable.getTableColumnList(table));
		}
		if (itemArr == null || itemArr.length == 0) return list;
		int i, ii, len;
		for (i = 0; i < itemArr.length; i++) {
			TableItem item = itemArr[i];
			len = item.getParent().getColumnCount();
			List<String> l = new ArrayList<String>(len);
			for (ii = 0; ii < len; ii++) {
				l.add(item.getText(ii));
			}
			list.add(l);
		}
		return list;
	}

	/**
	 * 得到头部列表，如果头部不显示则返回空列表
	 * @param table ResultTable
	 * @return List&lt;String&gt;
	 */
	public static final List<String> getTableColumnList(ResultTable table) {
		if (!table.getHeaderVisible()) return new ArrayList<>();
		List<String> list = new ArrayList<>(table.getColumnCount());
		TableColumn[] arr = table.getColumns();
		for (int i = 0; i < arr.length; i++)
			list.add(arr[i].getText());
		return list;
	}

	/**
	 * 得到TableItem的头部信息转成list<br>
	 * 如果table没有显示头部，则返回null
	 * @param itemArr TableItem[]
	 * @return List&lt;String&gt;
	 */
	public static final List<String> getTableHeadList(TableItem... itemArr) {
		if (itemArr == null || itemArr.length == 0) return null;
		TableItem first = itemArr[0];
		Table table = first.getParent();
		if (!table.getHeaderVisible()) return null;
		int len = table.getColumnCount();
		List<String> l = new ArrayList<String>(len);
		for (int i = 0; i < len; i++) {
			TableColumn e = table.getColumn(i);
			l.add(e.getText());
		}
		return l;
	}

	/**
	 * 插入list List&lt;String&gt; 第一位自动编号
	 * @param table ResultTable
	 * @param list List&lt;String&gt;
	 */
	public static synchronized void insertTableItem(ResultTable table, List<String> list) {
		if (list.size() == 0) return;
		TableItem item = new TableItem(table, SWT.LEFT);
		String[] arr = {};
		int max = table.getItemCount();
		list.add(0, "" + max);
		item.setText(list.toArray(arr));
	}

	/**
	 * 插入list List&lt;String&gt;第一位自动编号重新修改
	 * @param table ResultTable
	 * @param arrs String []
	 */
	public static synchronized void insertTableItem(ResultTable table, String[] arrs) {
		if (arrs.length == 0) return;
		TableItem item = new TableItem(table, SWT.LEFT);
		int max = table.getItemCount();
		arrs[0] = "" + max;
		item.setText(arrs);
	}

	/**
	 * 插入table，数组，第一位是否含有编号，如果含有则更新编号，如果没有则添加编号
	 * @param table ResultTable
	 * @param containID boolean
	 * @param arrs String []
	 */
	public static synchronized void insertTableItem(ResultTable table, boolean containID, String[] arrs) {
		if (arrs.length == 0) return;
		TableItem item = new TableItem(table, SWT.LEFT);
		int max = table.getItemCount();
		int len = arrs.length;
		if (!containID) len++;
		String[] newArray = new String[len];
		System.arraycopy(arrs, 0, newArray, len - arrs.length, arrs.length);
		newArray[0] = "" + max;
		item.setText(newArray);
	}

	/**
	 * 修改table背景色<br>
	 * 1:table<br>
	 * 2:TableItem<br>
	 * 4:cell
	 * @param table ResultTable
	 * @param ColorNull Color
	 * @param mod int
	 */
	public static void CleanTableBGColorAll(ResultTable table, Color ColorNull, int mod) {
		int cols = table.getColumnCount();
		if ((mod & 1) > 0) table.setBackground(ColorNull);/* 1 */
		for (int i = 0; i < table.getItemCount(); i++) {
			TableItem ee = table.getItem(i);
			if ((mod & 2) > 0) ee.setBackground(ColorNull); /* 2 */
			for (int ii = 0; ii < cols; ii++)
				if ((mod & 4) > 0) ee.setBackground(ii, ColorNull); /* 4 */
		}
	}

	/**
	 * 选中某行
	 * @param display Display
	 * @param table ResultTable
	 */
	public static final void setSelectedLine(Display display, ResultTable table) {
		if (table == null || table.getItemCount() == 0) return;
		TableItem[] items = table.getSelection();
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < items.length; i++)
					items[i].setChecked(true);
			}
		});
	}

	/**
	 * 选中某行
	 * @param table ResultTable
	 */
	public static final void setSelectedLine(ResultTable table) {
		if (table == null || table.getItemCount() == 0) return;
		TableItem[] items = table.getSelection();
		table.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < items.length; i++)
					items[i].setChecked(true);
			}
		});
	}
	/**
	 * 移除选中某行
	 * @param display Display getDisplay()
	 * @param shell Shell getShell()
	 * @param isConfirm boolean
	 * @param table ResultTable
	 */
	public static final void removeSelectedLine(Display display, Shell shell, boolean isConfirm, ResultTable table) {
		if (table == null || table.getItemCount() == 0) return;
		if (isConfirm && !UtilsSWTMessageBox.Confirm(shell, "是否要清除所选数据?")) return;
		logger.debug("removeSelectedLine!!!");
		@SuppressWarnings({ "static-access", "unused" })
		Point Point = display.getCurrent().getCursorLocation();
		int[] arrs = UtilsSWTTable.getSelectCheckTableItemAllSuffix(table);
		Arrays.sort(arrs);
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				for (int i = arrs.length - 1; i >= 0; i--)
					table.remove(arrs[i]);
			}
		});
		collectTable(table);
	}

	/**
	 * 对table按某个列，进行升或降序的排序
	 * @param table Table
	 * @param index int
	 * @param isAscend boolean
	 */
	static void StringItemsSorter(ResultTable table, int index, boolean isAscend) {
		Collator comparator = Collator.getInstance(Locale.getDefault());
		TableItem[] items = table.getItems();
		//使用冒泡法进行排序
		for (int i = 1; i < items.length; i++) {
			String str2value = items[i].getText(index);
			for (int j = 0; j < i; j++) {
				String str1value = items[j].getText(index);
				boolean isLessThan = comparator.compare(str2value, str1value) < 0;
				if ((isAscend && isLessThan) || (!isAscend && !isLessThan)) {
					String[] values = UtilsSWTTableSQL.get(table, items[i]);
					Object obj = items[i].getData();
					items[i].dispose();
					TableItem item = new TableItem(table, SWT.NONE, j);
					item.setText(values);
					item.setData(obj);
					items = table.getItems();
					break;
				}
			}
		}
	}

	/**
	 * 更新表格统计
	 * @param table ResultTable
	 */
	public static final void collectTable(ResultTable table) {
		InterfaceCollect parent = UtilsSWTTools.getParentInterfaceObj(table, InterfaceCollect.class);
		if (parent == null) return;
		parent.collect();
	}

	/**
	 * 把选中的多行的某列放入粘贴板
	 * @param display Display getDisplay()
	 * @param table ResultTable
	 * @param index int[]
	 */
	public static final void setSelectClipboardColumn(Display display, ResultTable table, int... index) {
		if (table.getSelection().length == 0) return;
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				Table newTable = getSelectSelectCheckTableItemAllTable(table, index);
				int maxColumnLen = newTable.getColumnCount();
				if (maxColumnLen == 0 || newTable.getItemCount() == 0) return;
				StringBuilder sb = new StringBuilder();
				TableItem[] items = newTable.getItems();
				for (int i = 0; i < items.length; i++) {
					if (sb.length() > 0) sb.append(System.getProperty("line.separator"));
					TableItem e = items[i];
					for (int ii = 0; ii < maxColumnLen; ii++) {
						if (ii > 0) sb.append('\t');
						sb.append(e.getText(ii));
					}
				}
				UtilsClipboard.copy(sb.toString());
			}
		});
	}

	/**
	 * 把table中的所有行选择
	 * @param display Display getDisplay()
	 * @param table ResultTable
	 */
	public static final void setSelectTableAll(Display display, ResultTable table) {
		if (table.getSelection().length == 0) return;
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				table.selectAll();
			}
		});
	}

	/**
	 * 把table中的所有选择行 按空格时
	 * @param display Display getDisplay()
	 * @param table ResultTable
	 */
	public static final void setSelectedSpace(Display display, ResultTable table) {
		if (table.getSelection().length == 0) return;
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				TableItem[] arr = table.getSelection();
				for (int i = 0; i < arr.length; i++) {
					TableItem e = arr[i];
					boolean check = e.getChecked();
					//logger.debug("change:"+i+" -> "+e.toString()+" ->"+check);
					e.setChecked(!check);
				}
			}
		});
	}

	/**
	 * 从table提取多列选中的数据组成新的table
	 * @param table ResultTable
	 * @param suffix int[]
	 * @return Table
	 */
	public static final Table getSelectSelectCheckTableItemAllTable(ResultTable table, int... suffix) {
		int[] arrs = UtilsSWTTable.getSelectCheckTableItemAllSuffix(table);
		//TableItem[] items = table.getItems();
		Table newTable = new Table(table, SWT.NONE);
		//TableColumn[] tcarrs=table.getColumns();
		int lenall = table.getColumnCount();
		int[] part = UtilsSWTTools.getIntersection(lenall, suffix);
		if (part.length == 0) return newTable;
		/*
		 * for (int i = 0; i < part.length; i++) {
		 * TableColumn tableColumn = new TableColumn(newTable, SWT.NONE);
		 * tableColumn.setText(table.getColumn(part[i]).getText());
		 * }
		 **/
		for (int i = 0; i < arrs.length; i++) {
			int value = arrs[i];
			TableItem ti = table.getItem(value);
			String[] vv = new String[part.length];
			int p = 0;
			for (int ii = 0; ii < part.length; ii++) {
				vv[p++] = ti.getText(part[ii]);
			}
			TableItem item = new TableItem(newTable, SWT.NONE);
			item.setText(vv);
		}
		return newTable;
	}

	/**
	 * 显示table里的内容
	 * @param table ResultTable
	 */
	public static void showTable(ResultTable table) {
		System.out.println("===================================================");
		int cols = table.getColumnCount();
		StringBuilder sb = new StringBuilder();
		TableColumn[] arrs = table.getColumns();
		for (int i = 0; i < arrs.length; i++) {
			TableColumn e = arrs[i];
			sb.append('[');
			sb.append(e.getText());
			sb.append("]\t");
		}
		System.out.println(sb.toString());
		TableItem[] arr = table.getItems();
		for (int i = 0; i < arr.length; i++) {
			TableItem e = arr[i];
			sb.setLength(0);
			for (int ii = 0; ii < cols; ii++) {
				sb.append('[');
				sb.append(e.getText(ii));
				sb.append("]\t");
			}
			System.out.println(sb.toString());
		}
		System.out.println("===================================================");
	}

	/**
	 * 清空Table，包括行与列
	 * @param table ResultTable
	 */
	public static final void CleanAllTable(ResultTable table) {
		TableItem[] arrs = table.getItems();
		for (int i = 0; i < arrs.length; i++)
			arrs[i].dispose();
		TableColumn[] arr = table.getColumns();
		for (int i = 0; i < arr.length; i++)
			arr[i].dispose();
		table.redraw();
	}

	/**
	 * 得到Table上鼠标所在位置的行数与列数
	 * @param table ResultTable
	 * @param point Point
	 * @return Point
	 */
	public static Point getTableCursorLocationPoint(ResultTable table, Point point) {
		if (table == null) return null;
		Composite parent = table.getParent();
		if (parent == null) return null;
		TableItem tableItem = table.getItem(point);
		if (tableItem == null) return null;
		Point p = new Point(-1, -1);
		for (int i = 0; i < table.getItemCount(); i++)
			if (table.getItem(i).equals(tableItem)) p.x = i;
		for (int i = 0; i < table.getColumnCount(); i++)
			if (tableItem.getBounds(i).contains(point)) {
				p.y = i;
				break;
			}
		return p;
	}

	/**
	 * 某个行列中的数据，如果没有数据，则返加上一行同列数据，非空
	 * @param table Table
	 * @param x int
	 * @param y int
	 * @return String
	 */
	public static String getColsValueUpDownNotNull(ResultTable table, int x, int y) {
		String value = UtilsSWTTableSQL.get(table, x, y);
		if (value != null && value.length() > 0) return value;
		int rows = table.getItemCount();
		for (int i = x; i >= 0; i--) {/* 向上寻找内容 */
			value = UtilsSWTTableSQL.get(table, i, y);
			if (value != null && value.length() > 0) return value;
		}
		for (int i = x + 1; i < rows; i++) {/* 向下寻找内容 */
			value = UtilsSWTTableSQL.get(table, i, y);
			if (value != null && value.length() > 0) return value;
		}
		return null;
	}

	/**
	 * 得到表格某列深度 是否过滤空格
	 * @param table Table
	 * @param y int
	 * @param isTrim boolean
	 * @return int
	 */
	public static final int getDepth(Table table, int y, boolean isTrim) {
		for (int i = table.getItemCount() - 1; i > -1; i--) {
			String value = table.getItem(i).getText(y);
			if (value == null || value.length() == 0) continue;
			if (isTrim && value.trim().length() == 0) continue;
			return i;
		}
		return table.getItemCount() - 1;
	}
	/**
	 * 得到表格最大/最小层数
	 * @param table Table
	 * @param isMax boolean
	 * @param isTrim boolean
	 * @return int
	 */
	public static final int getDepth(Table table, boolean isMax, boolean isTrim) {
		int len = table.getColumnCount();
		int extremum = 0;
		for (int i = 0; i < len; i++) {
			int deep = getDepth(table, i, isTrim);
			if (isMax) {/* 最大深度 */
				if (deep > extremum) extremum = deep;
			} else {/* 最小深度 */
				if (extremum==0 || deep < extremum) extremum = deep;
			}
		}
		return extremum;
	}
}
