package des.wangku.operate.standard.swt;

import java.sql.ResultSet;
import java.sql.SQLException;
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
import des.wangku.operate.standard.task.InterfaceExcelChange;
import des.wangku.operate.standard.task.InterfaceTablesDialog;
import des.wangku.operate.standard.utls.UtilsArrays;
import des.wangku.operate.standard.utls.UtilsPOI;
import des.wangku.operate.standard.utls.UtilsSQL;
import des.wangku.operate.standard.utls.UtilsSWTTable;
import des.wangku.operate.standard.utls.UtilsSWTTableListener;
import des.wangku.operate.standard.utls.UtilsSWTTableSQL;
import des.wangku.operate.standard.utls.UtilsSWTTableUtils;
import des.wangku.operate.standard.utls.UtilsSWTTools;
import des.wangku.operate.standard.utls.UtilsString;

/**
 * 重构Table,用于保存列表，一般用于显示结果集 UI组件
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class ResultTable extends Table implements InterfaceExcelChange {
	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(ResultTable.class);
	/** 参数 */
	Properties properties = null;
	/** 此表名称 */
	String title = "";
	/** 附加对象 */
	Object additionalObj = null;
	/** 参数 */
	ResultTableParameter ectpara = null;
	/** 排序 */
	Boolean isAscend = true;
	/** 当前页数 */
	int page = 0;
	/** 父窗口 */
	Composite parent;
	/** 鼠标右键弹出菜单 */
	Menu menuResultTable = new Menu(this);
	/** 选中的列编号 */
	int selectColumn = -1;
	/** 选中的行编号 */
	int selectLine = -1;
	/** 选择列的弹出组，有效与无效 */
	MenuItem submenucolumn = null;
	/** 自已对象 */
	ResultTable base = this;
	/** 数据保存 */
	List<String[]> data = new ArrayList<>();
	/** ResultTable的标准样式 */
	public static final int ACC_ResultTableState = SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL;
	/** ResultTable的标准样式 单选 */
	public static final int ACC_ResultTableStateRadio = SWT.BORDER | SWT.RADIO | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL;

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
	 * 得到某行某列中的字符串
	 * @param x int
	 * @param y int
	 * @return String
	 */
	public String getString(int x, int y) {
		TableItem e = getItem(x);
		if (e == null) return null;
		return e.getText(y);
	}
	/**
	 * 得到某一行数据，输出为数组，如为null，则返回空字符串<br>
	 * 如果没有找到指定行，则返回空数组
	 * @param x int
	 * @return String[]
	 */
	public String[] getLine(int x) {
		if(x < 0)return new String[0];
		TableItem e = getItem(x);
		if (e == null) return new String[0];;
		int len = getColumnCount();
		String[] arr = new String[len];
		for (int i = 0; i < len; i++) {
			String val = e.getText(i);
			arr[i] = (val == null ? "" : val);
		}
		return arr;
	}

	/**
	 * 得到某列的所有值，如果有行号，则按行提取，如果没有行号，则返回所有行的某列
	 * @param col int
	 * @param linearr int
	 * @return List&lt;String&gt;
	 */
	public List<String> getStringColumn(int col, int... linearr) {
		List<String> list = new ArrayList<>();
		if (linearr.length > 0) for (int e : linearr) {
			String v = getString(e, col);
			list.add(v);
		}
		else {
			for (int i = 0, len = getItemCount(); i < len; i++) {
				String v = getString(i, col);
				list.add(v);
			}
		}
		return list;
	}

	/**
	 * 设置某行某列中的字符串 如果value为null，则添加空
	 * @param x int
	 * @param y int
	 * @param val String
	 */
	public synchronized void setString(int x, int y, String val) {
		TableItem e = getItem(x);
		if (e == null) return;
		e.setText(y, val == null ? "" : val);
	}

	/**
	 * 插入数据
	 * @param list List&lt;List&lt;String&gt;&gt;
	 */
	public void insertData(List<List<String>> list) {
		for (List<String> l : list) {
			addData(l);
		}
	}

	/**
	 * 插入多条数据
	 * @param rs ResultSet
	 */
	public void insertData(ResultSet rs) {
		try {
			while (rs.next()) {
				List<String> list = UtilsSQL.resultSetToList(rs);
				addData(list);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 插入数据<br>
	 * 过滤行标
	 * @param sheet Sheet
	 * @param filterArr int[]
	 */
	public void addData(Sheet sheet, int... filterArr) {
		if (sheet == null) return;
		for (int i = 0, len = sheet.getLastRowNum(); i <= len; i++) {
			Row row = sheet.getRow(i);
			if (row == null) continue;
			if (UtilsArrays.isfilterArr(i, filterArr)) continue;
			int rows = row.getLastCellNum();
			List<String> list = new ArrayList<>(rows);
			for (int ii = 0; ii <= rows; ii++) {
				Cell cell = row.getCell(ii);
				if (cell == null) {
					list.add("");
					continue;
				}
				String value = UtilsPOI.getCellValueByString(cell);
				if (value == null) value = "";
				list.add(value);
			}
			if (list.size() == 0) continue;
			UtilsSWTTableSQL.add(this, list);
		}
	}

	/**
	 * 插入数据
	 * @param arr String[]
	 */
	public void addData(String... arr) {
		UtilsSWTTableSQL.add(this, arr);
	}
	/**
	 * 插入数据，如果发现有相同数组的行，则返回false
	 * @param arrs String[]
	 */
	public boolean addDataDistinct(String...arrs) {
		String[] newArr = UtilsSWTTableSQL.getFormatArray(this, arrs);
		if(UtilsSWTTableSQL.isExist(this, newArr)) return false;
		UtilsSWTTableSQL.add(this, newArr);
		return true;
	}
	/**
	 * 判断字符串是否存在此表中
	 * @param arrs String[]
	 * @return boolean
	 */
	public boolean isExist(String...arrs) {
		return UtilsSWTTableSQL.isExist(this, arrs);
	}

	/**
	 * 插入数据
	 * @param list List&lt;String&gt;
	 */
	public void addData(List<String> list) {
		UtilsSWTTableSQL.add(this, list);
	}

	/**
	 * 插入数据
	 * @param rs ResultSet
	 */
	public void addData(ResultSet rs) {
		List<String> list = UtilsSQL.resultSetToList(rs);
		addData(list);
	}

	/**
	 * 添加新列，是否允许重复 是否同步
	 * @param isasync boolean
	 * @param columnName String
	 * @param repeat boolean
	 */
	public void addColumn(boolean isasync, String columnName, boolean repeat) {
		if (columnName == null) return;
		if (!repeat) {
			TableColumn[] arr = this.getColumns();
			for (TableColumn e : arr) {
				if (e.getText().equals(columnName)) return;
			}
		}
		if (!isasync) {
			addColumn(columnName);
			return;
		}
		getShell().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				addColumn(columnName);
			}
		});
	}

	/**
	 * 默认添加新列
	 * @param columnName String 列名
	 */
	public void addColumn(String columnName) {
		addColumn(columnName, 150);
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
	 * 判断是否含有此名称的列
	 * @param title String
	 * @return boolean
	 */
	public boolean isColumn(String title) {
		if (title == null) return false;
		TableColumn[] arrs = base.getColumns();
		for (TableColumn e : arrs) {
			if (e.getText().equals(title)) return true;
		}
		return false;
	}

	/**
	 * 自动添加新列
	 * @param size int
	 * @return boolean
	 */
	public boolean addColumnAuto(int size) {
		for (int i = 0; i < size; i++) {
			String columnName = getAutoTitle();
			if (columnName == null) return false;
			addColumn(columnName, 150);
		}
		return true;
	}

	/**
	 * 默认添加新列 有宽度
	 * @param columnName String 列名
	 * @param width int
	 * @return boolean
	 */
	public boolean addColumn(String columnName, int width) {
		if (columnName == null) return false;
		TableColumn e = ResultTable.getDefaultTableColumn(base, SWT.LEFT, width, columnName);
		ResultTable.setDefaultTableColumnPos(base, e);
		return true;
	}

	/**
	 * 初始化
	 */
	void Init() {
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
		this.addListener(SWT.MouseDoubleClick, adddListenerEdit());
		//redrawTable();
	}

	protected void checkSubclass() {

	}

	public final Boolean getIsAscend() {
		return isAscend;
	}

	public final void setIsAscend(Boolean isAscend) {
		this.isAscend = isAscend;
	}

	public final List<String[]> getData() {
		return data;
	}

	public final ResultTableParameter getEctpara() {
		return ectpara;
	}

	public final void setEctpara(String jsonStr) {
		this.ectpara = ResultTableParameter.getRTP(jsonStr);
	}

	public final void setEctpara(ResultTableParameter ectpara) {
		this.ectpara = ectpara;
	}

	/**
	 * 给table表格加上快捷键
	 * @param display Display
	 * @param shell Shell
	 * @param table Table
	 * @return KeyListener
	 */
	KeyListener addKeyListenerTable() {
		Display display = parent.getDisplay();
		Shell shell = parent.getShell();
		KeyListener t = new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.stateMask == SWT.CTRL && (e.keyCode >= '0' && e.keyCode <= '9')) {
					UtilsSWTTable.setSelectClipboardColumn(display, base, e.keyCode - 48);
				}
				/*
				 * if (e.keyCode == SWT.SPACE) {
				 * UtilsSWTTable.setSelectedSpace(display, table);
				 * }
				 */
				if (e.keyCode == SWT.DEL) {
					UtilsSWTTable.removeSelectedLine(display, shell, true, base);
				}
				if (e.stateMask == SWT.CTRL && (e.keyCode == 'f')) {
					InterfaceTablesDialog parent = UtilsSWTTools.getParentInterfaceObj(base, InterfaceTablesDialog.class);
					parent.setSearchDialog((new SearchResultTable(shell, 0)).putCombo(base));
					//parent.setSearchDialog(new SearchDialog(shell, 0, base));
					parent.getSearchDialog().open();
				}
				if (e.stateMask == SWT.CTRL && (e.keyCode == 'a' || e.keyCode == 'A')) {
					/*
					 * ExcelCTabFolder parentECTF = getParentExcelCTabFolder();
					 * if (parentECTF == null) {
					 * logger.debug("parentECTF:" + null);
					 * } else {
					 * logger.debug("parentECTF:" + parentECTF.filename);
					 * }
					 */
					UtilsSWTTable.setSelectTableAll(display, base);
				}
				if (e.stateMask == SWT.CTRL && (e.keyCode == 'c' || e.keyCode == 'C')) {
					if (!UtilsSWTTableUtils.copyMultiLineToClipboard(base)) return;
				}
				if (e.stateMask == SWT.CTRL && (e.keyCode == 'v' || e.keyCode == 'V')) {
					int point = UtilsSWTTable.getSelectCheckTableItemAllSuffixMin(base);
					logger.debug("point:" + point);
					UtilsSWTTableSQL.addClipboard(base, point);
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {

			}
		};
		return t;
	}

	/**
	 * 双击鼠标左键，修改单元内容
	 * @return Listener
	 */
	Listener adddListenerEdit() {
		Listener t = new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (event.button == 1) {	//按键不是左键跳出. 1左键,2中键,3右键
					Point point0 = new Point(event.x, event.y);
					Point loca = UtilsSWTTable.getTableCursorLocationPoint(base, point0);
					if (loca == null || loca.x == -1 || loca.y == -1) return;
					boolean readonly = ectpara.isReadonly(loca.y);
					String oldValue = UtilsSWTTableSQL.get(base, loca.x, loca.y);
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
	 * 鼠标在表格移动，触发此监听器，得到当前鼠标所在列号
	 * @return MouseMoveListener
	 */
	MouseMoveListener getListenerMouseMove() {
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

	/** 总数 */
	MenuItem Collect_Item = null;
	/** 选中项 指鼠标选择，但未选择 */
	MenuItem Collect_Item0 = null;
	/** 选择项 指check=true */
	MenuItem Collect_Item1 = null;

	/**
	 * 初始化鼠标右键弹出菜单列表
	 */
	void InitializationMenuItem(Menu parent) {
		MenuItem menuItemCheck = new MenuItem(parent, SWT.NONE);
		menuItemCheck.setText("选择行-选中");
		menuItemCheck.addListener(SWT.Selection, UtilsSWTTableListener.getListenerSelectedLine(this));

		MenuItem menuItemCheckAll = new MenuItem(parent, SWT.NONE);
		menuItemCheckAll.setText("选择行-全选");
		menuItemCheckAll.addSelectionListener(UtilsSWTTableListener.getListenerTableSelectAll(this, true));

		MenuItem menuItemUnCheckAll = new MenuItem(parent, SWT.NONE);
		menuItemUnCheckAll.setText("选择行-反选");
		menuItemUnCheckAll.addSelectionListener(UtilsSWTTableListener.getListenerTableRevSelectionAll(this));

		MenuItem menuItemCheckSameValueAll = new MenuItem(parent, SWT.NONE);
		menuItemCheckSameValueAll.setText("选择行-同值选");
		menuItemCheckSameValueAll.addSelectionListener(UtilsSWTTableListener.getListenerTableSameValueSelectionAll(this));

		MenuItem menuItemCheckLikeValueAll = new MenuItem(parent, SWT.NONE);
		menuItemCheckLikeValueAll.setText("选择行-含值选");
		menuItemCheckLikeValueAll.addSelectionListener(UtilsSWTTableListener.getListenerTableLikeValueSelectionAll(this));

		new MenuItem(parent, SWT.SEPARATOR);
		MenuItem menuItemRemove = new MenuItem(parent, SWT.NONE);
		menuItemRemove.setText("选择行-删除");
		menuItemRemove.addListener(SWT.Selection, UtilsSWTTableListener.getListenerRemoveSelectedLine(true, this));

		new MenuItem(parent, SWT.SEPARATOR);

		MenuItem menuItemRemoveCheckeAll = new MenuItem(parent, SWT.NONE);
		menuItemRemoveCheckeAll.setText("选择-取消选择");
		menuItemRemoveCheckeAll.addSelectionListener(UtilsSWTTableListener.getListenerTableSelectAll(this, false));

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
			public void widgetSelected(SelectionEvent e) {
				logger.debug("replace collect.....");

				MenuItem item1 = new MenuItem(menu_Collect, SWT.NONE);
				item1.setText("选择[3]");
				MenuItem item2 = new MenuItem(menu_Collect, SWT.NONE);
				item2.setText("选择[4]");
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				logger.debug("replace collect222.....");

			}

		});

		new MenuItem(parent, SWT.SEPARATOR);
		MenuItem menuItemAdd = new MenuItem(parent, SWT.NONE);
		menuItemAdd.setText("添加新空行");
		menuItemAdd.addSelectionListener(UtilsSWTTableListener.getListenerAddLine(base));

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
	 * 列操作，如列清空、列删除、列添加等，针对选中的列
	 * @param parent Menu
	 */
	void setMenuItemCheckColumn(Menu parent) {
		MenuItem menuColumnAdd = new MenuItem(parent, SWT.NONE);
		menuColumnAdd.setText("添加");
		menuColumnAdd.addSelectionListener(UtilsSWTTableListener.getListenerAddColumn(base));

		MenuItem menuColumnClean = new MenuItem(parent, SWT.NONE);
		menuColumnClean.setText("清空");
		menuColumnClean.addSelectionListener(UtilsSWTTableListener.getListenerCleanColumn(base));

		MenuItem menuColumnRemove = new MenuItem(parent, SWT.NONE);
		menuColumnRemove.setText("移除");
		menuColumnRemove.addSelectionListener(UtilsSWTTableListener.getListenerRemoveColumn(base));

	}

	/**
	 * 设置弹出菜单 - 汇总
	 * @param name
	 * @param index
	 */
	void setMenuItemCollect(Menu parent) {

	}

	/**
	 * 设置弹出菜单 - 导出
	 * @param parent Menu
	 */
	void setMenuItemCheckExport(Menu parent) {
		MenuItem menuItemCopyline = new MenuItem(parent, SWT.NONE);
		menuItemCopyline.setText("复制行-粘贴板-多行");
		menuItemCopyline.addListener(SWT.Selection, UtilsSWTTableListener.getListenerCopyClipboard(this));
		MenuItem menuItemCopyJson = new MenuItem(parent, SWT.NONE);
		menuItemCopyJson.setText("复制行-粘贴板-Json");
		menuItemCopyJson.addListener(SWT.Selection, UtilsSWTTableListener.getListenerCopyJson(this));
		MenuItem menuItemCopytoExcel = new MenuItem(parent, SWT.NONE);
		menuItemCopytoExcel.setText("复制行-文件-Excel");
		menuItemCopytoExcel.addListener(SWT.Selection, UtilsSWTTableListener.getListenerCopyToExcel(this, true));
	}

	/**
	 * 设置某一个单元格
	 * @param align int
	 * @param text String
	 * @param len int
	 */
	public void setTableColumn(int align, String text, int len) {
		TableColumn e = getDefaultTableColumn(this, align, len, text);
		setDefaultTableColumnPos(this, e);
	}

	/**
	 * 得到某列的属性名称，如果没有则返回null
	 * @param index int
	 * @return String
	 */
	public final String getTableColumnText(int index) {
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
	 * 设置默认列
	 * @param table ResultTable
	 * @param align int
	 * @param len int
	 * @param text String
	 * @return TableColumn
	 */
	public final static TableColumn getDefaultTableColumn(ResultTable table, int align, int len, String text) {
		TableColumn tc = new TableColumn(table, SWT.BORDER | SWT.NONE | SWT.FULL_SELECTION | SWT.MULTI);
		tc.setWidth(len);
		tc.setText(text);
		tc.setAlignment(align);
		tc.setMoveable(true);/* 设置表头可移动，默认为false */
		tc.setResizable(true);
		return tc;
	}

	/**
	 * 设置排序
	 * @param table ResultTable
	 * @param e TableColumn
	 */
	public static final void setDefaultTableColumnPos(ResultTable table, TableColumn e) {
		if (e == null) return;
		int index = -1;/* 当前列的下标，用于排序时使用 */
		TableColumn[] arrs = table.getColumns();
		for (int i = 0; i < arrs.length; i++)
			if (arrs[i].equals(e)) {
				index = i;
				break;
			}
		if (index == -1) return;
		e.addListener(SWT.Selection, UtilsSWTTableListener.getListenerColumnDoubleClick(table, index));
	}

	/**
	 * 移除上方所有空行
	 */
	public void removeNullRowsUp() {
		TableItem[] arrs = this.getItems();
		for (int i = 0; i < arrs.length; i++) {
			TableItem e = arrs[i];
			String value = e.getText();
			if (value == null || value.length() == 0) this.remove(i);
			else break;
		}
	}

	/**
	 * 移除下方所有空行
	 */
	public void removeNullRowsDown() {
		TableItem[] arrs = this.getItems();
		for (int i = arrs.length - 1; i >= 0; i--) {
			TableItem e = arrs[i];
			String value = e.getText();
			if (value == null || value.length() == 0) this.remove(i);
			else break;
		}
	}

	/**
	 * 移除所有空行
	 */
	public void removeNullRowsAll() {
		TableItem[] arrs = this.getItems();
		for (int i = arrs.length - 1; i >= 0; i--) {
			TableItem e = arrs[i];
			String value = e.getText();
			if (value == null || value.length() == 0) this.remove(i);
		}
	}

	/**
	 * 所有列自动设置宽度
	 */
	public void autoWidthMaxSize() {
		int len = this.getColumnCount();
		for (int i = 0; i < len; i++)
			autoWidthMaxSize(i);
	}

	/**
	 * 某列自动设置宽度
	 * @param y int
	 */
	public void autoWidthMaxSize(int y) {
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

	public final int getSelectColumn() {
		return selectColumn;
	}

	public final int getSelectLine() {
		return selectLine;
	}

	public final ExcelCTabFolder getParentExcelCTabFolder() {
		return UtilsSWTTools.getParentObj(this, ExcelCTabFolder.class);
	}

	/**
	 * 输入数据后进行初始化
	 */
	public void initialization() {
		setHeaderVisible(ectpara.isViewHead);
		setLinesVisible(true);
		if (ectpara.isAutoremoveDownNullRows) removeNullRowsDown();
		if (ectpara.isAutoremoveUpNullRows) removeNullRowsUp();
		if (ectpara.isAutoWidth) autoWidthMaxSize();
	}

	/**
	 * 得到某列的宽度
	 * @param p int
	 * @return int
	 */
	public int getColumnWidth(int p) {
		if (ectpara.widthArray == null || ectpara.widthArray.length == 0 || p < 0 || ectpara.widthArray.length <= p) return ectpara.defTCWidth;
		int value = ectpara.widthArray[p];
		if (value <= 0) return ectpara.defTCWidth;
		return value;
	}

	/**
	 * 通过查找ResultTable中的栏目名称中的标识[XXX]得到栏目的下标，如果没有查到，则返回-1<br>
	 * 规范格式:[102]XXX
	 * @param table ResultTable
	 * @param key String
	 * @return int
	 */
	public int getColumnsIndex(String key) {
		if (key == null) return -1;
		TableColumn[] arr = getColumns();
		for (int i = 0; i < arr.length; i++) {
			String title = arr[i].getText();
			if (title.indexOf("[" + key + "]") == 0) return i;
		}
		return -1;
	}

	/**
	 * 添加记录，如果point在[0---len-1]之间，则为插入，否则为添加
	 * @param point int
	 * @param arrs String[]
	 */
	public void addTableItem(int point, String... arrs) {
		TableItem item = null;
		if (point > -1 && point < getItemCount()) item = new TableItem(this, SWT.NONE, point);
		else item = new TableItem(this, SWT.NONE);
		item.setText(arrs);
	}

	/**
	 * 更新指定行列的内容，如果没有此行此列，则添加多行多列
	 * @param x int
	 * @param y int
	 * @param value String
	 */
	public final synchronized void make(int x, int y, String value) {
		if (value == null || x < 0 || y < 0) return;
		int colcount = getColumnCount();
		if (y >= colcount) {
			int size = y - colcount + 1;
			/* 添加列时失败，不能添加某列，可能会已经添加了几列 */
			if (!addColumnAuto(size)) return;
		}
		int rowcount = getItemCount();
		if (x >= rowcount) {
			int size = x - rowcount + 1;
			String[] arrs = new String[getColumnCount()];
			Arrays.fill(arrs, "");
			for (int i = 0; i < size; i++)
				addTableItem(-1, arrs);
		}
		//System.out.println("["+this.getItemCount()+","+this.getColumnCount()+"]->["+x+","+y+"]");
		setString(x, y, value);
	}

	@Override
	public void ChangeCell(int x, int y, Cell cell, int maxRows, int maxCols) {

	}

	@Override
	public void ChangeSheet(Sheet sheet) {
		/* 锁定前一行 */
		if (this.getHeaderVisible() && this.getColumnCount() > 0) sheet.createFreezePane(0, 1);

	}

	/**
	 * 得到check=true 下标
	 * @return int[]
	 */
	public int[] getCheckedTableItemSuffix() {
		List<Integer> list = new ArrayList<>();
		TableItem[] arr = this.getItems();
		for (int i = 0; i < arr.length; i++)
			if (arr[i].getChecked()) list.add(i);
		return list.stream().mapToInt(Integer::valueOf).toArray();
	}

	/**
	 * 得到check=true
	 * @return TableItem[]
	 */
	public TableItem[] getCheckedTableItem() {
		List<TableItem> list = new ArrayList<>();
		for (TableItem e : this.getItems()) {
			if (e.getChecked()) list.add(e);
		}
		TableItem[] arrs = {};
		return list.toArray(arrs);
	}

	/**
	 * 得到check=true行数
	 * @return int
	 */
	public int getChecked() {
		int count = 0;
		for (TableItem e : this.getItems()) {
			if (e.getChecked()) count++;
		}
		return count;
	}

	public final Object getAdditionalObj() {
		return additionalObj;
	}

	public final void setAdditionalObj(Object additionalObj) {
		this.additionalObj = additionalObj;
	}

	/**
	 * 设置ResultTable头部信息
	 * @param arrs String[]
	 */
	void mkResultTableHead(String... arrs) {
		String value = null;
		UtilsSWTTable.CleanAllTable(this);
		for (int i = 0; i < arrs.length; i++) {
			value = (arrs[i] == null) ? "" + i : arrs[i];
			if (ectpara.attrSuffix) value = "[" + i + "]" + value;
			int align = ectpara.getSWTAlign(i);
			setTableColumn(align, value, getColumnWidth(i));
		}
	}

	/**
	 * 更新内容里需要修改的信息
	 * @param x int
	 * @param y int
	 * @param obj Object
	 */
	public void inputValueDialogUpdate(int x, int y, Object obj) {
		if (obj == null) return;
		String newValue = (String) obj;
		String oldValue = UtilsSWTTableSQL.get(base, x, y);
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
		UtilsSWTTableSQL.update(base, x, y, newValue);
	}

	/**
	 * 得到某行的数据列表
	 * @param index int
	 * @return List&lt;String&gt;
	 */
	public List<String> toList(int index) {
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
	 * 把表格内容提取
	 * @param epc ExcelParaClass
	 * @return List&lt;List&lt;String&gt;&gt;
	 */
	public List<List<String>> toList(ExcelParaClass epc) {
		List<List<String>> list = new ArrayList<>();
		int size = this.getItemCount();
		if (epc != null && epc.isHead()) list.add(toColumnsList());
		for (int i = 0; i < size; i++)
			list.add(toList(i));
		return list;
	}

	/**
	 * 得到表格的表头列表
	 * @return List&lt;String&gt;
	 */
	public List<String> toColumnsList() {
		TableColumn[] arrs = this.getColumns();
		List<String> list = new ArrayList<>(arrs.length);
		for (TableColumn e : arrs) {
			list.add(e.getText());
		}
		return list;
	}

	/**
	 * 删除所有行与列
	 */
	public void disposeAll() {
		TableItem[] arrs = getItems();
		for (int i = 0; i < arrs.length; i++)
			arrs[i].dispose();
		TableColumn[] arr = getColumns();
		for (int i = 0; i < arr.length; i++)
			arr[i].dispose();
		redraw();
	}
}
