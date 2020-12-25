package des.wangku.operate.standard.swt;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import des.wangku.operate.standard.dialog.InputValueDialog;
import des.wangku.operate.standard.dialog.SearchResultTable;
import des.wangku.operate.standard.dialog.TableOutputExcelParaDialog.ExcelParaClass;
import des.wangku.operate.standard.task.InterfaceCollect;
import des.wangku.operate.standard.task.InterfaceExcelChange;
import des.wangku.operate.standard.task.InterfaceTablesDialog;
import des.wangku.operate.standard.utls.UtilsArrays;
import des.wangku.operate.standard.utls.UtilsClipboard;
import des.wangku.operate.standard.utls.UtilsList;
import des.wangku.operate.standard.utls.UtilsPOI;
import des.wangku.operate.standard.utls.UtilsSQL;
import des.wangku.operate.standard.utls.UtilsSWTTable;

import static des.wangku.operate.standard.utls.UtilsSWTTable.*;
import static des.wangku.operate.standard.utls.UtilsSWTTableListener.*;
import des.wangku.operate.standard.utls.UtilsSWTTableUtils;
import des.wangku.operate.standard.utls.UtilsSWTTools;
import des.wangku.operate.standard.utls.UtilsString;
import des.wangku.operate.standard.utls.UtilsVerification;

/**
 * 重构Table,用于保存列表，一般用于显示结果集 UI组件
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class ResultTable extends Table implements InterfaceExcelChange {
	/** 附加对象 */
	Object additionalObj = null;
	/** 自已对象 */
	ResultTable base = this;
	/** 总数 */
	MenuItem Collect_Item = null;
	/** 选中项 指鼠标选择，但未选择 */
	MenuItem Collect_Item0 = null;
	/** 选择项 指check=true */
	MenuItem Collect_Item1 = null;
	/** 数据保存 */
	List<String[]> data = new ArrayList<>();
	/** 参数 */
	ResultTableParameter ectpara = null;
	/** 排序 */
	Boolean isAscend = true;
	/** 鼠标右键弹出菜单 */
	Menu menuResultTable = new Menu(this);
	/** 当前页数 */
	int page = 0;
	/** 父窗口 */
	Composite parent;
	/** 参数 */
	Properties properties = null;
	/** 选中的列编号 */
	int selectColumn = -1;
	/** 选中的行编号 */
	int selectLine = -1;
	/** 选择列的弹出组，有效与无效 */
	MenuItem submenucolumn = null;
	/** 此表名称 */
	String title = "";

	/**
	 * 构造函数
	 * @param parent Composite
	 * @param style int
	 * @param properties Properties
	 * @param title String
	 */
	public ResultTable(Composite parent, int style, Properties properties, String title) {
		super(parent, style);
		checkSubclass();
		this.properties = properties;
		this.title = title;
		this.ectpara = ResultTableParameter.getRTP(properties, title);
		this.parent = parent;
		Init();
	}

	/**
	 * 添加list List &lt; String &gt;
	 * @param list List &lt; String &gt;
	 * @return boolean
	 */
	public final synchronized boolean add(List<String> list) {
		if (list.size() == 0) return false;
		String[] arr = {};
		add(list.toArray(arr));
		return true;
	}

	/**
	 * 插入数据
	 * @param rs ResultSet
	 * @param isall boolean
	 */
	public final synchronized void add(ResultSet rs, boolean isall) {
		if (rs == null) return;
		if (!isall) {
			List<String> list = UtilsSQL.resultSetToList(rs);
			add(list);
			return;
		}
		List<List<String>> list = UtilsSQL.resultSetToMulitList(rs);
		for (List<String> e : list)
			add(e);
	}

	/**
	 * 添加
	 * @param row Row
	 */
	public final synchronized void add(Row row) {
		add(row, null);
	}

	public final synchronized void add(Row row, ExcelCTabFolder ectf) {
		if (row == null) return;
		if (ectf == null) {
			add(UtilsPOI.getValuesList(row));
			return;
		}
		if (row.getZeroHeight() && !ectf.showHiddenRow) return;
		int cellLen = getColumnCount();
		List<String> list = new ArrayList<>(cellLen);
		Sheet sheet = row.getSheet();
		for (int i = 0; i <= cellLen; i++) {
			if (!ectf.showHiddenColumn && sheet.isColumnHidden(i)) continue;/* 如果不允许显示隐藏列，则过滤掉 */
			Cell cell = row.getCell(i);
			String value = UtilsPOI.getCellValueByString(cell, true);
			if (getEctpara().isTrim) value = value.trim();
			list.add(value);
		}
		if (ectf.isFilterBlankLines && UtilsList.isBlankLines(list)) return;
		add(list);
	}

	/**
	 * 添加数据<br>
	 * 过滤行标
	 * @param sheet Sheet
	 * @param filterArr int[]
	 */
	public final synchronized void add(Sheet sheet, int... filterArr) {
		if (sheet == null) return;
		add(sheet, filterArr, null);
	}

	/**
	 * 添加数据<br>
	 * 过滤行标
	 * @param sheet Sheet
	 * @param filterArr int[]
	 * @param ectf ExcelCTabFolder
	 */
	public final synchronized void add(Sheet sheet, int[] filterArr, ExcelCTabFolder ectf) {
		if (sheet == null) return;
		final int rowslen = sheet.getLastRowNum();/* 行数 */
		for (int i = 0; i <= rowslen; i++) {
			if (UtilsArrays.isfilterArr(i, filterArr)) continue;
			if (i == getEctpara().headRowSuffix) continue;
			Row row = sheet.getRow(i);
			if (row == null) continue;
			if (ectf == null) {
				add(row);
				continue;
			}
			if (row.getZeroHeight() && !ectf.showHiddenRow) continue;
			add(row, ectf);
		}

	}

	/**
	 * 添加list List&lt;String&gt;
	 * @param arrs String []
	 * @return boolean
	 */
	public final synchronized boolean add(String... arrs) {
		if (arrs.length == 0) return false;
		String[] newArr = getFormatArray(arrs);
		addTableItem(-1, newArr);
		return true;
	}

	/**
	 * 把粘贴板中的数据存入到表中
	 * @param point int
	 * @return boolean
	 */
	public final synchronized boolean addClipboard(int point) {
		String content = UtilsClipboard.getString();
		if (content == null || content.length() == 0) return false;
		/*
		 * 此处判断粘贴板内容格式，默认为text格式
		 * 如果是json，需要确定json格式，如果是xml，都需要确定格式
		 */
		String[] arr = content.split(System.getProperty("line.separator"));
		for (int i = 0; i < arr.length; i++) {
			String[] arrLine = arr[i].split("\t");
			insert(point, arrLine);
		}
		return true;
	}

	/**
	 * 添加新列，是否允许重复 是否同步
	 * @param isasync boolean
	 * @param name String
	 * @param repeat boolean
	 */
	public final void addColumn(boolean isasync, String name, boolean repeat) {
		if (name == null) return;
		if (!repeat) {
			TableColumn[] arr = this.getColumns();
			for (TableColumn e : arr) {
				if (e.getText().equals(name)) return;
			}
		}
		if (!isasync) {
			addColumn(name);
			return;
		}
		getShell().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				addColumn(name);
			}
		});
	}

	/**
	 * 默认添加新列
	 * @param name String 列名
	 */
	public final void addColumn(String name) {
		addColumn(name, 150);
	}

	/**
	 * 默认添加新列 有宽度
	 * @param columnName String 列名
	 * @param width int
	 * @return boolean
	 */
	public final boolean addColumn(String columnName, int width) {
		if (columnName == null) return false;
		TableColumn e = getDefaultTableColumn(SWT.LEFT, width, columnName);
		setDefaultTableColumnPos(e);
		return true;
	}

	/**
	 * 自动添加新列
	 * @param size int
	 * @return boolean
	 */
	public final boolean addColumnAuto(int size) {
		for (int i = 0; i < size; i++) {
			String name = getAutoTitle();
			if (name == null) return false;
			addColumn(name, 150);
		}
		return true;
	}

	/**
	 * 插入数据，如果发现有相同数组的行，则返回false
	 * @param arrs String[]
	 * @return boolean
	 */
	public final boolean addDistinct(String... arrs) {
		String[] newArr = getFormatArray(arrs);
		if (isExist(newArr)) return false;
		add(newArr);
		return true;
	}

	/**
	 * 给table表格加上快捷键
	 * @param display Display
	 * @param shell Shell
	 * @param table Table
	 * @return KeyListener
	 */
	 final KeyListener addKeyListenerTable() {
		Display display = parent.getDisplay();
		Shell shell = parent.getShell();
		KeyListener t = new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.stateMask == SWT.CTRL && (e.keyCode >= '0' && e.keyCode <= '9')) {
					setSelectClipboardColumn(display, base, e.keyCode - 48);
				}
				/*
				 * if (e.keyCode == SWT.SPACE) {
				 * UtilsSWTTable.setSelectedSpace(display, table);
				 * }
				 */
				if (e.keyCode == SWT.DEL) {
					removeSelectedCheckedLine(display, shell, true, base, true, true);
				}
				if (e.stateMask == SWT.CTRL && (e.keyCode == 'f')) {
					InterfaceTablesDialog parent = UtilsSWTTools.getParentInterfaceObj(base, InterfaceTablesDialog.class);
					parent.setSearchDialog((new SearchResultTable(shell, 0)).putCombo(base));
					//parent.setSearchDialog(new SearchDialog(shell, 0, base));
					parent.getSearchDialog().open();
				}
				if (e.stateMask == SWT.CTRL && (e.keyCode == 'a' || e.keyCode == 'A')) {
					setSelectTableAll(display, base);
				}
				if (e.stateMask == SWT.CTRL && (e.keyCode == 'c' || e.keyCode == 'C')) {
					if (!UtilsSWTTableUtils.copyMultiLineToClipboard(base)) return;
				}
				if (e.stateMask == SWT.CTRL && (e.keyCode == 'v' || e.keyCode == 'V')) {
					int point = getSelectCheckTableItemAllSuffixMin(base);
					logger.debug("point:" + point);
					base.addClipboard(point);
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {

			}
		};
		return t;
	}

	/**
	 * 添加批量数据
	 * @param list List &lt; List &lt; String&gt;&gt;
	 * @return int
	 */
	public final synchronized int addList(List<List<String>> list) {
		if (list == null) return -1;
		int count = 0;
		for (List<String> l : list)
			if (add(l)) count++;
		return count;
	}

	/**
	 * 双击鼠标左键，修改单元内容
	 * @return Listener
	 */
	private final Listener addListenerEdit() {
		Listener t = new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (event.button == 1) {	//按键不是左键跳出. 1左键,2中键,3右键
					Point point0 = new Point(event.x, event.y);
					Point loca = getTableCursorLocationPoint(base, point0);
					if (loca == null || loca.x == -1 || loca.y == -1) return;
					boolean readonly = ectpara.isReadonly(loca.y);
					String oldValue = base.get(loca.x, loca.y);
					InputValueDialog ind = new InputValueDialog(parent, 0, "设置内容", "内容", oldValue, readonly);
					Object obj = ind.open();
					inputValueDialogUpdate(loca.x, loca.y, obj);
					return;
				}
			}
		};
		return t;
	}

	/**
	 * 添加记录，添加多个空行
	 * @param rows int
	 */
	public final void addTableItem(int rows) {
		int colcount = getColumnCount();
		if (colcount == 0) return;
		String[] arrs = new String[colcount];
		Arrays.fill(arrs, "");
		for (int i = 0; i < rows; i++)
			addTableItem(-1, arrs);
	}

	public static final class TableItemJson {
		int index;
		boolean checked = false;
		String[] val = {};

		public TableItemJson(TableItem e) {
			this(UtilsSWTTable.getIndex(e), e.getChecked(), tableItemValToArray(e));
		}

		public TableItemJson(int index, boolean checked, String[] val) {
			this.index = index;
			this.checked = checked;
			this.val = val;
		}

		public static final List<TableItemJson> getList(TableItem... arr) {
			if (arr == null || arr.length == 0) return new ArrayList<>(0);
			List<TableItemJson> list = new ArrayList<>(arr.length);
			for (TableItem e : arr)
				list.add(new TableItemJson(e));
			return list;
		}

		public String toJson() {
			StringBuilder sb = new StringBuilder();
			sb.append("{");
			sb.append("\"index\":" + index + ",");
			sb.append("\"checked\":" + checked + ",");
			sb.append("\"val\":[");
			for (int i = 0, len = val.length; i < len; i++) {
				String v = val[i];
				if (v == null) v = "";
				sb.append("\"" + v + "\"");
				if (i < len - 1) sb.append(",");
			}
			sb.append("]");
			sb.append("}");
			return sb.toString();
		}

		public final int getIndex() {
			return index;
		}

		public final void setIndex(int index) {
			this.index = index;
		}

		public final boolean isChecked() {
			return checked;
		}

		public final void setChecked(boolean checked) {
			this.checked = checked;
		}

		public final String[] getVal() {
			return val;
		}

		public final void setVal(String[] val) {
			this.val = val;
		}
		
		
		
	}

	/**
	 * 添加记录，如果point在[0---len-1]之间，则为插入，否则为添加
	 * @param point int
	 * @param arrs String[]
	 */
	public final void addTableItem(int point, String... arrs) {
		TableItem item = null;
		if (point > -1 && point < getItemCount()) item = new TableItem(this, SWT.NONE, point);
		else item = new TableItem(this, SWT.NONE);
		item.setText(arrs);
	}

	/**
	 * 所有列自动设置宽度
	 */
	public final void autoWidthMaxSize() {
		int len = this.getColumnCount();
		for (int i = 0; i < len; i++)
			autoWidthMaxSize(i);
	}

	/**
	 * 某列自动设置宽度
	 * @param y int
	 */
	public final void autoWidthMaxSize(int y) {
		if (y < 0 || y >= this.getColumnCount()) return;
		int maxLen = 0;
		for (int i = 0, len = this.getItemCount(); i < len; i++) {
			TableItem row = this.getItem(i);
			Font font = row.getFont(y);
			int size = UtilsString.getStringWidth(row.getText(y), font);
			if (size > maxLen) maxLen = size;
		}
		if (maxLen > 0) this.getColumn(y).setWidth(maxLen);
	}

	@Override
	public void ChangeCell(int x, int y, Cell cell, int maxRows, int maxCols) {

	}

	@Override
	public final void ChangeSheet(Sheet sheet) {
		/* 锁定前一行 */
		if (this.getHeaderVisible() && this.getColumnCount() > 0) sheet.createFreezePane(0, 1);

	}

	protected void checkSubclass() {

	}

	/**
	 * 清空Table，包括行与列
	 */
	public final void cleanAllTable() {
		TableItem[] arrs = getItems();
		for (int i = 0; i < arrs.length; i++)
			arrs[i].dispose();
		TableColumn[] arr = getColumns();
		for (int i = 0; i < arr.length; i++)
			arr[i].dispose();
		redraw();
	}

	/**
	 * 更新表格统计
	 */
	public final void collectTable() {
		InterfaceCollect parent = UtilsSWTTools.getParentInterfaceObj(base, InterfaceCollect.class);
		if (parent == null) return;
		parent.collect();
	}

	/**
	 * 删除所有行与列
	 */
	public final void disposeAll() {
		TableItem[] arrs = getItems();
		for (int i = 0; i < arrs.length; i++)
			arrs[i].dispose();
		TableColumn[] arr = getColumns();
		for (int i = 0; i < arr.length; i++)
			arr[i].dispose();
		redraw();
	}

	/**
	 * 得到某行某列中的字符串
	 * @param x int
	 * @param y int
	 * @return String
	 */
	public final String get(int x, int y) {
		if (!isValid(x, y)) return null;
		TableItem ti = getItem(x);
		if (ti == null) return null;
		return ti.getText(y);
	}

	/**
	 * 得到某行某列中的字符串
	 * @param point Point
	 * @return String
	 */
	public final String get(Point point) {
		if (point == null) return null;
		return get(point.x, point.y);
	}

	public final Object getAdditionalObj() {
		return additionalObj;
	}

	/**
	 * 得到顺序随机id以sj0001以例<br>
	 * 数字范围: 1--9999之间
	 * @return String
	 */
	public final String getAutoTitle() {
		for (int i = 1; i < 10000; i++) {
			String newid = MultiTree.format(i + "", MultiTree.ACC_AutoIDNumLen);
			String id = MultiTree.ACC_AutoIDPrefix + newid;
			if (!isColumn(id)) return id;
		}
		return null;
	}

	/**
	 * 得到check=true行数
	 * @return int
	 */
	public final int getChecked() {
		return getCheckedAll().length;
	}

	/**
	 * 得到表格中选中的行号
	 * @return int[]
	 */
	public final int[] getCheckedAll() {
		return getCheckedAll(true);
	}

	/**
	 * 得到表格中选中的行号 是否选中
	 * @param checked boolean
	 * @return int[]
	 */
	public final int[] getCheckedAll(boolean checked) {
		int len = getItemCount();
		List<Integer> list = new ArrayList<>(len);
		for (int i = 0; i < len; i++) {
			TableItem e = getItem(i);
			if (e.getChecked() == checked) list.add(i);
		}
		return list.stream().mapToInt(Integer::valueOf).toArray();
	}

	/**
	 * 得到check=true
	 * @return TableItem[]
	 */
	public final TableItem[] getCheckedTableItem() {
		int[] arr = getCheckedAll();
		TableItem[] result = new TableItem[arr.length];
		for (int i = 0; i < arr.length; i++)
			result[i] = getItem(arr[i]);
		return result;
	}

	/**
	 * 得到check=true 下标
	 * @return int[]
	 */
	@Deprecated
	public final int[] getCheckedTableItemSuffix2() {
		List<Integer> list = new ArrayList<>();
		TableItem[] arr = this.getItems();
		for (int i = 0; i < arr.length; i++)
			if (arr[i].getChecked()) list.add(i);
		return list.stream().mapToInt(Integer::valueOf).toArray();
	}

	/**
	 * 得到表格中未选中的行号
	 * @return int[]
	 */
	public final int[] getCheckedUnAll() {
		return getCheckedAll(false);
	}

	/**
	 * 得到某列的数组，如果isChecked为null，则得到某列所有单元<br>
	 * 如果isChecked，为判断提取标签
	 * @param isChecked Boolean
	 * @param col int
	 * @return List&lt;String&gt;
	 */
	public final List<String> getColumnList(Boolean isChecked, int col) {
		if (col < 0 || col >= this.getColumnCount()) return new ArrayList<>();
		List<String> list = new ArrayList<>();
		if (isChecked != null) {
			int[] arr = this.getCheckedAll();
			for (int i = 0, len = getItemCount(); i < len; i++) {
				boolean isexist = UtilsArrays.isfilterArr(i, arr);
				if ((isChecked && isexist) || (!isChecked && !isexist)) {
					String v = get(i, col);
					list.add(v);
				}
			}
		} else {
			for (int i = 0, len = getItemCount(); i < len; i++) {
				String v = get(i, col);
				list.add(v);
			}
		}
		return list;
	}

	/**
	 * 得到某列的所有值，如果有行号，则按行提取，如果没有行号，则返回所有行的某列
	 * @param col int
	 * @param linearr int
	 * @return List&lt;String&gt;
	 */
	public final List<String> getColumnList(int col, int... linearr) {
		List<String> list = new ArrayList<>();
		if(col<0)return list;
		if (linearr.length > 0) for (int e : linearr) {
			String v = get(e, col);
			list.add(v);
		}
		else {
			if(getItemCount()==0)return list;
			for (int i = 0, len = getItemCount(); i < len; i++) {
				String v = get(i, col);
				list.add(v);
			}
		}
		return list;
	}
	/**
	 * 得到某列所有的值，以列为标准
	 * @param col int
	 * @return String[]
	 */
	public final String[] getColumnVal(int col) {
		int len=getItemCount();
		String[] arr=new String[len];
		for (int i = 0; i < len; i++) {
			String v = get(i, col);
			arr[i]=(v==null?"":v);
		}
		return arr;
	}

	/**
	 * 通过查找ResultTable中的栏目名称中的标识[XXX]得到栏目的下标，如果没有查到，则返回-1<br>
	 * 规范格式:[102]XXX
	 * @param key String
	 * @return int
	 */
	public final int getColumnsIndex(String key) {
		if (key == null) return -1;
		TableColumn[] arr = getColumns();
		for (int i = 0; i < arr.length; i++) {
			String title = arr[i].getText();
			if (title.indexOf("[" + key + "]") == 0) return i;
		}
		return -1;
	}

	/**
	 * 得到某列的宽度
	 * @param p int
	 * @return int
	 */
	public final int getColumnWidth(int p) {
		if (ectpara.widthArray == null || ectpara.widthArray.length == 0 || p < 0 || ectpara.widthArray.length <= p) return ectpara.defTCWidth;
		int value = ectpara.widthArray[p];
		if (value <= 0) return ectpara.defTCWidth;
		return value;
	}

	public final List<String[]> getData() {
		return data;
	}

	public final ResultTableParameter getEctpara() {
		return ectpara;
	}

	/**
	 * 得到扩展数组值，规则化
	 * @param arrs String[]
	 * @return String[]
	 */
	public final String[] getFormatArray(String... arrs) {
		int len = arrs.length;
		for (int i = 0; i < len; i++)
			arrs[i] = (arrs[i] == null ? "" : arrs[i]);
		String[] arr = new String[getColumnCount()];
		for (int i = 0; i < arr.length; i++)
			arr[i] = (i < arrs.length && arrs[i] != null) ? arrs[i] : "";
		return arr;
	}

	public final Boolean getIsAscend() {
		return isAscend;
	}

	/**
	 * 得到某一行数据，输出为数组，如为null，则返回空字符串<br>
	 * 如果没有找到指定行，则返回空数组
	 * @param x int
	 * @return String[]
	 */
	public final String[] getLine(int x) {
		if (x < 0) return new String[0];
		TableItem e = getItem(x);
		if (e == null) return new String[0];
		int len = getColumnCount();
		String[] arr = new String[len];
		for (int i = 0; i < len; i++) {
			String val = e.getText(i);
			arr[i] = (val == null ? "" : val);
		}
		return arr;
	}

	/**
	 * 鼠标在表格移动，触发此监听器，得到当前鼠标所在列号
	 * @return MouseMoveListener
	 */
	private final MouseMoveListener getListenerMouseMove() {
		MouseMoveListener t = new MouseMoveListener() {
			@Override
			public void mouseMove(MouseEvent e) {
				Point p = new Point(e.x, e.y);
				TableItem ee = base.getItem(p);
				if (ee == null) {
					selectLine = -1;
					selectColumn = -1;
					submenucolumn.setEnabled(false);
					return;
				}
				selectLine = -1;
				submenucolumn.setEnabled(true);
				for (int i = 0; i < base.getItemCount(); i++) {
					if (ee.equals(base.getItem(i))) {
						selectLine = i;
						break;
					}
				}
				for (int i = 0; i < base.getColumnCount(); i++)
					if (ee.getBounds(i).contains(p)) {
						selectColumn = i;
						break;
					}
			}
		};
		return t;
	}

	public final ExcelCTabFolder getParentExcelCTabFolder() {
		return UtilsSWTTools.getParentObj(this, ExcelCTabFolder.class);
	}

	/**
	 * 得到Table中的选中行的下标编号，得到鼠标选中行+checked选中行<br>
	 * 从小到大<br>
	 * @return int[]
	 */
	public final int[] getSelectCheckAll() {
		return getSelectedCheckedAll(true, true);
	}

	/**
	 * 得到Table中的选中行，得到鼠标选中行+checked选中行<br>
	 * 从小到大<br>
	 * @return TableItem[]
	 */
	public final TableItem[] getSelectCheckTableItemArray() {
		int[] arr = getSelectCheckAll();
		int len = arr.length;
		TableItem[] arrs = new TableItem[len];
		for (int i = 0; i < len; i++)
			arrs[i] = getItem(arr[i]);
		return arrs;
	}

	/**
	 * 得到Table中的选中行，得到鼠标选中行+checked选中行<br>
	 * 从小到大<br>
	 * @return List&lt;TableItem&gt;
	 */
	public final List<TableItem> getSelectCheckTableItemList() {
		return Arrays.asList(getSelectCheckTableItemArray());
	}

	public final int getSelectColumn() {
		return selectColumn;
	}

	/**
	 * 得到表格中选择的行号
	 * @return int[]
	 */
	public final int[] getSelectedAll() {
		return getSelectionIndices();
	}

	/**
	 * 从Table中提下选中的下标编号<br>
	 * isSelected==null或isChecked==null，则不提出相应的类型
	 * @param isSelected Boolean
	 * @param isChecked Boolean
	 * @return int[]
	 */
	public final int[] getSelectedCheckedAll(Boolean isSelected, Boolean isChecked) {
		int[] arr1 = {}, arr2 = {};
		if (isSelected != null && isSelected) arr1 = getSelectedAll();
		if (isChecked != null && isChecked) arr2 = getCheckedAll();
		return UtilsArrays.mergedistinct(arr1, arr2);
		/*
		 * if (isSelected == null) {
		 * if (isChecked == null) return arr;
		 * if (isChecked) return getCheckedAll();
		 * return arr;
		 * }
		 * if (isSelected) {
		 * if (isChecked == null) return getSelectedAll();
		 * if (isChecked) return UtilsArrays.mergedistinct(getSelectedAll(), getCheckedAll());
		 * return getSelectedAll();
		 * }
		 * if (isChecked == null) return arr;
		 * if (isChecked) return getCheckedAll();
		 * return arr;
		 */
	}

	/**
	 * 得到表格中未选择的行号
	 * @return int[]
	 */
	public final int[] getSelectedUnAll() {
		int len = getItemCount();
		List<Integer> list = new ArrayList<>(len);
		int[] selectarr = getSelectionIndices();
		for (int i = 0; i < len; i++) {
			if (!UtilsArrays.isfilterArr(i, selectarr)) list.add(i);
		}
		return list.stream().mapToInt(Integer::valueOf).toArray();
	}

	public final int getSelectLine() {
		return selectLine;
	}

	/**
	 * 得到某列的属性名称，如果没有则返回null
	 * @param index int
	 * @return String
	 */
	public final String getTableColumnText(int index) {
		if (index < 0) return null;
		TableColumn obj = this.getColumn(index);
		if (obj == null) return null;
		return obj.getText();
	}

	/**
	 * 宽度，列数
	 * @return int
	 */
	public final int getWidth() {
		return this.getColumnCount();
	}

	/**
	 * 初始化
	 */
	 final void Init() {
		this.addKeyListener(addKeyListenerTable());
		this.setMenu(menuResultTable);
		InitializationMenuItem(menuResultTable);
		menuResultTable.addMenuListener(new MenuListener() {
			@Override
			public void menuHidden(MenuEvent e) {
			}

			@Override
			public void menuShown(MenuEvent e) {
				if (Collect_Item != null) {
					Collect_Item.setText("总数\t[" + base.getItemCount() + "]");
				}
				if (Collect_Item0 != null) {
					Collect_Item0.setText("鼠标选中项\t[" + base.getSelectionCount() + "]");
				}
				if (Collect_Item1 != null) {
					Collect_Item1.setText("Check选择项\t[" + base.getChecked() + "]");
				}
			}
		});
		this.addMouseMoveListener(getListenerMouseMove());
		this.addListener(SWT.MouseDoubleClick, addListenerEdit());
		//redrawTable();
	}

	/**
	 * 输入数据后进行初始化
	 */
	public final void initialization() {
		setHeaderVisible(ectpara.isViewHead);
		setLinesVisible(true);
		if (ectpara.isAutoremoveDownNullRows) removeNullRowsDown();
		if (ectpara.isAutoremoveUpNullRows) removeNullRowsUp();
		if (ectpara.isAutoWidth) autoWidthMaxSize();
	}

	/**
	 * 初始化鼠标右键弹出菜单列表
	 */
	private final void InitializationMenuItem(Menu parent) {
		setMenuItemCheckSelect(parent);

		new MenuItem(parent, SWT.SEPARATOR);

		MenuItem submenuCollect = new MenuItem(parent, SWT.CASCADE);
		submenuCollect.setText("汇总");
		Menu menu_Collect = new Menu(this.getShell(), SWT.DROP_DOWN);
		submenuCollect.setMenu(menu_Collect);
		Collect_Item = new MenuItem(menu_Collect, SWT.NONE);
		Collect_Item0 = new MenuItem(menu_Collect, SWT.NONE);
		Collect_Item1 = new MenuItem(menu_Collect, SWT.NONE);

		submenuCollect.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				logger.debug("replace collect222.....");

			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				logger.debug("replace collect.....");
				MenuItem item1 = new MenuItem(menu_Collect, SWT.NONE);
				item1.setText("选择[3]");
				MenuItem item2 = new MenuItem(menu_Collect, SWT.NONE);
				item2.setText("选择[4]");
			}

		});

		new MenuItem(parent, SWT.SEPARATOR);

		addSelectionMenu(parent, "添加新空行", getListenerAddLine(base));

		new MenuItem(parent, SWT.SEPARATOR);

		submenucolumn = new MenuItem(parent, SWT.CASCADE);
		submenucolumn.setText("选择列");
		Menu menu_ColumnExport = new Menu(this.getShell(), SWT.DROP_DOWN);
		submenucolumn.setMenu(menu_ColumnExport);
		setMenuItemCheckColumn(menu_ColumnExport);

		new MenuItem(menuResultTable, SWT.SEPARATOR);

		MenuItem submenuExport = new MenuItem(parent, SWT.CASCADE);
		submenuExport.setText("选行-导出");
		Menu menu_Export = new Menu(this.getShell(), SWT.DROP_DOWN);
		submenuExport.setMenu(menu_Export);
		setMenuItemCheckExport(menu_Export);

	}

	/**
	 * 更新内容里需要修改的信息
	 * @param x int
	 * @param y int
	 * @param obj Object
	 */
	public final void inputValueDialogUpdate(int x, int y, Object obj) {
		if (obj == null) return;
		String newValue = (String) obj;
		String oldValue = get(x, y);
		if (newValue.equals(oldValue)) return;
		InterfaceResultTableUpdate parent = UtilsSWTTools.getParentInterfaceObj(base, InterfaceResultTableUpdate.class);
		if (parent != null) {
			/* 更新父类中的数据库信息，返回状态如为false，代表父类修改不成功，则不更新table中的信息 */
			boolean t = parent.resultTableUpdate(base, x, y, oldValue, newValue);
			if (!t) {
				logger.debug("更新父类resultTableUpdate失败，请检查父类update代码!如为空，则返回true");
				return;
			}
		}
		update(x, y, newValue);
	}

	/**
	 * 插入list List&lt;String&gt;
	 * @param y int
	 * @param arrs String[]
	 */
	public synchronized final void insert(int y, String... arrs) {
		if (y < 0 || y >= getItemCount()) {
			add(arrs);
			return;
		}
		addTableItem(-1, getFormatArray(arrs));
	}

	/**
	 * 插入数据
	 * @param list List&lt;List&lt;String&gt;&gt;
	 */
	public synchronized final void insertData(List<List<String>> list) {
		for (List<String> l : list) {
			add(l);
		}
	}

	/**
	 * 判断是否含有此名称的列
	 * @param title String
	 * @return boolean
	 */
	public final boolean isColumn(String title) {
		if (title == null) return false;
		TableColumn[] arrs = base.getColumns();
		for (TableColumn e : arrs) {
			if (e.getText().equals(title)) return true;
		}
		return false;
	}

	/**
	 * 判断字符串是否存在此表中
	 * @param arrs String[]
	 * @return boolean
	 */
	public final synchronized boolean isExist(String... arrs) {
		if (arrs.length == 0) return true;
		String[] newArr = getFormatArray(arrs);
		int rows = getItemCount();
		int len = getColumnCount();
		loop: for (int x = 0; x < rows; x++) {
			TableItem e = getItem(x);
			for (int y = 0; y < len; y++) {
				String val = e.getText(y);
				String v = newArr[y];
				if (!v.equals(val)) continue loop;
			}
			return true;
		}
		return false;
	}

	/**
	 * 判断某行是否选中
	 * @param rows int
	 * @return boolean
	 */
	public final boolean isSelected(int rows) {
		return UtilsArrays.isfilterArr(rows, getSelectionIndices());
	}

	/**
	 * 判断x与y是否有效<br>
	 * 返回true，则x与y都是有效的
	 * @param x int
	 * @param y int
	 * @return boolean
	 */
	public final boolean isValid(int x, int y) {
		if (x < 0 || x >= getItemCount()) return false;
		if (y < 0 || y >= getColumnCount()) return false;
		return true;
	}

	/**
	 * 设置ResultTable头部信息
	 * @param arrs String[]
	 */
	public final void mkResultTableHead(String... arrs) {
		String value = null;
		cleanAllTable();
		for (int i = 0; i < arrs.length; i++) {
			value = (arrs[i] == null) ? "" + i : arrs[i];
			if (ectpara.attrSuffix) value = "[" + i + "]" + value;
			int align = ectpara.getSWTAlign(i);
			setTableColumn(align, value, getColumnWidth(i));
		}
	}

	/**
	 * 判断某行是否为空，null或空字符串
	 * @param x int
	 * @return boolean
	 */
	public final boolean isNullRows(int x) {
		if (x < 0 || x >= getItemCount()) return false;
		TableItem e = this.getItem(x);
		if (e == null) return false;
		String value = e.getText();
		if (value == null || value.length() == 0) return true;
		int cols = this.getColumnCount();
		for (int i = 0; i < cols; i++) {
			String val = e.getText(i);
			if (val != null && val.length() > 0) return false;
		}
		return true;
	}

	/**
	 * 移除所有空行
	 */
	public final void removeNullRowsAll() {
		int size = this.getItems().length;
		List<Integer> list = new ArrayList<>(size);
		for (int i = size - 1; i >= 0; i--)
			if (isNullRows(i)) list.add(i);
		if (list.size() > 0) removeRows(list);
	}

	/**
	 * 移除下方所有空行
	 */
	public final void removeNullRowsDown() {
		int size = this.getItems().length;
		List<Integer> list = new ArrayList<>(size);
		for (int i = size - 1; i >= 0; i--) {
			if (isNullRows(i)) list.add(i);
			else break;
		}
		if (list.size() > 0) removeRows(list);
	}

	/**
	 * 移除上方所有空行
	 */
	public final void removeNullRowsUp() {
		int size = this.getItems().length;
		List<Integer> list = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			if (isNullRows(i)) list.add(i);
			else break;
		}
		if (list.size() > 0) removeRows(list);
	}

	/**
	 * 删除指定下标多行
	 * @param list List&lt;Integer&gt;
	 */
	public final synchronized void removeRows(List<Integer> list) {
		if (list == null || list.size() == 0) return;
		int[] arr = list.stream().mapToInt(Integer::valueOf).toArray();
		removeRows(arr);
	}

	/**
	 * 删除指定下标多行
	 * @param arrs int[]
	 */
	public final synchronized void removeRows(int... arrs) {
		if (arrs.length == 0) return;
		int[] narrs = UtilsArrays.mergedistinct(arrs);
		//Arrays.sort(arrs);
		getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				for (int i = narrs.length - 1; i >= 0; i--) {
					int index = narrs[i];
					TableItem e = base.getItem(index);
					System.out.println("remove:" + UtilsSWTTable.getTableItemJson(e));//getTableItemString
					remove(index);
				}
			}
		});
	}

	/**
	 * 设置某行某列中的字符串 如果value为null，则添加空
	 * @param x int
	 * @param y int
	 * @param val String
	 * @return boolean
	 */
	public final synchronized boolean set(int x, int y, String val) {
		if (!isValid(x, y)) return false;
		TableItem e = getItem(x);
		if (e == null) return false;
		e.getParent().getParent().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				e.setText(y, val == null ? "" : val);
			}
		});
		return true;
	}

	/**
	 * 设置某行某列中的字符串 如果value为null，则添加空
	 * @param point Point
	 * @param val String
	 * @return boolean
	 */
	public final synchronized boolean set(Point point, String val) {
		if (point == null) return false;
		return set(point.x, point.y, val);
	}

	public final void setAdditionalObj(Object additionalObj) {
		this.additionalObj = additionalObj;
	}

	/**
	 * 设置排序
	 * @param e TableColumn
	 */
	public final void setDefaultTableColumnPos(TableColumn e) {
		if (e == null) return;
		int index = -1;/* 当前列的下标，用于排序时使用 */
		TableColumn[] arrs = getColumns();
		for (int i = 0; i < arrs.length; i++)
			if (arrs[i].equals(e)) {
				index = i;
				break;
			}
		if (index == -1) return;
		e.addListener(SWT.Selection, getListenerColumnDoubleClick(base, index));
	}

	public final void setEctpara(ResultTableParameter ectpara) {
		this.ectpara = ectpara;
	}

	public final void setEctpara(String jsonStr) {
		this.ectpara = ResultTableParameter.getRTP(jsonStr);
	}

	public final void setIsAscend(Boolean isAscend) {
		this.isAscend = isAscend;
	}

	/**
	 * 设置弹出菜单 - 汇总
	 * @param name
	 * @param index
	 */
	void setMenuItemCollect(Menu parent) {

	}

	/**
	 * 设置某一个单元格
	 * @param align int
	 * @param text String
	 * @param len int
	 */
	public final void setTableColumn(int align, String text, int len) {
		TableColumn e = getDefaultTableColumn(align, len, text);
		setDefaultTableColumnPos(e);
	}

	/**
	 * 更新指定行列的内容，如果没有此行此列，则添加多行多列
	 * @param x int
	 * @param y int
	 * @param value String
	 */
	public final synchronized void setUpdate(int x, int y, String value) {
		if (value == null || x < 0 || y < 0) return;
		int colcount = getColumnCount();
		if (y >= colcount) {
			int size = y - colcount + 1;
			if (!addColumnAuto(size)) return;/* 添加列时失败，不能添加某列，可能会已经添加了几列 */
		}
		int rowcount = getItemCount();
		if (x >= rowcount) {
			int size = x - rowcount + 1;
			addTableItem(size);
		}
		set(x, y, value);
	}

	/**
	 * 汇总某列所有值的和
	 * @param y int
	 * @param filterLine int[]
	 * @return long
	 */
	public final synchronized long SortY(int y, int... filterLine) {
		long sort = 0;
		for (int i = 0, len = getItemCount(); i < len; i++) {
			if (UtilsString.isExist(i, filterLine)) continue;
			String value = get(i, y);
			if (!UtilsVerification.isNumeric(value)) continue;
			int v = Integer.parseInt(value);
			sort += v;
		}
		return sort;
	}

	/**
	 * 得到表格的表头列表
	 * @return List&lt;String&gt;
	 */
	public final List<String> toColumnsList() {
		TableColumn[] arrs = this.getColumns();
		List<String> list = new ArrayList<>(arrs.length);
		for (TableColumn e : arrs) {
			list.add(e.getText());
		}
		return list;
	}

	/**
	 * 把表格内容提取
	 * @param epc ExcelParaClass
	 * @return List&lt;List&lt;String&gt;&gt;
	 */
	public final List<List<String>> toList(ExcelParaClass epc) {
		List<List<String>> list = new ArrayList<>();
		int size = this.getItemCount();
		if (epc != null && epc.isHead()) list.add(toColumnsList());
		for (int i = 0; i < size; i++)
			list.add(toList(i));
		return list;
	}

	/**
	 * 得到某行的数据列表
	 * @param index int
	 * @return List&lt;String&gt;
	 */
	public final List<String> toList(int index) {
		List<String> list = new ArrayList<>();
		TableItem e = this.getItem(index);
		if (e == null) return list;
		int len = getColumnCount();
		for (int i = 0; i < len; i++) {
			String value = e.getText(i);
			if (value == null) value = "";
			list.add(value);
		}
		return list;
	}

	/**
	 * 更新
	 * @param x int
	 * @param y int
	 * @param value String
	 */
	public final synchronized void update(int x, int y, String value) {
		if (value == null) return;
		if (!isValid(x, y)) return;
		TableItem f = getItem(x);
		if (f == null) return;
		f.setText(y, value);
	}

	/** ResultTable的标准样式 */
	public static final int ACC_ResultTableState = SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL;

	/** ResultTable的标准样式 单选 */
	public static final int ACC_ResultTableStateRadio = SWT.BORDER | SWT.RADIO | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL;

	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(ResultTable.class);

	/**
	 * 设置默认列
	 * @param table ResultTable
	 * @param align int
	 * @param len int
	 * @param text String
	 * @return TableColumn
	 */
	public final TableColumn getDefaultTableColumn(int align, int len, String text) {
		TableColumn tc = new TableColumn(this, SWT.BORDER | SWT.NONE | SWT.FULL_SELECTION | SWT.MULTI);
		tc.setWidth(len);
		tc.setText(text);
		tc.setAlignment(align);
		tc.setMoveable(true);/* 设置表头可移动，默认为false */
		tc.setResizable(true);
		return tc;
	}

	/**
	 * 设置弹出菜单 - 列操作，如列清空、列删除、列添加等，针对选中的列
	 * @param parent Menu
	 */
	private final void setMenuItemCheckColumn(Menu parent) {
		addSelectionMenu(parent, "添加", getListenerAddColumn(base));
		addSelectionMenu(parent, "清空", getListenerCleanColumn(base));
		addSelectionMenu(parent, "移除", getListenerRemoveColumn(base));
	}

	/**
	 * 设置弹出菜单 - 导出
	 * @param parent Menu
	 */
	private final void setMenuItemCheckExport(Menu parent) {
		addSelectionMenu(parent, "复制行-粘贴板-多行", getListenerCopyClipboard(base));

		addSelectionMenu(parent, "复制行-粘贴板-Json", getListenerCopyJson(base));

		addSelectionMenu(parent, "复制行-粘贴板-Excel", getListenerCopyToExcel(base, true));
	}

	/**
	 * 设置弹出菜单 - 选择
	 * @param parent Menu
	 */
	private final void setMenuItemCheckSelect(Menu parent) {
		addSelectionMenu(parent, "选择行-选中", getListenerSelectedLine(base));

		addSelectionMenu(parent, "选择行-全选", getListenerTableSelectAll(base, true));

		addSelectionMenu(parent, "选择行-反选", getListenerTableRevSelectionAll(base));

		addSelectionMenu(parent, "选择行-同值选", getListenerTableSameValueSelectionAll(base));

		addSelectionMenu(parent, "选择行-含值选", getListenerTableLikeValueSelectionAll(base));

		new MenuItem(parent, SWT.SEPARATOR);

		addSelectionMenu(parent, "选择行-删除", getListenerRemoveSelectedCheckedLine(true, base, true, false));

		addSelectionMenu(parent, "选中/选择行-删除", getListenerRemoveSelectedCheckedLine(true, base, true, true));

		new MenuItem(parent, SWT.SEPARATOR);

		addSelectionMenu(parent, "选择-取消选择", getListenerTableSelectAll(base, false));

	}
}
