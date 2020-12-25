package des.wangku.operate.standard.swt;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

import static des.wangku.operate.standard.Constants.UNSAFE;
import static des.wangku.operate.standard.Constants.CTabItemBaseOffset;
import static des.wangku.operate.standard.Constants.CTabItemIndexScale;
import static des.wangku.operate.standard.Constants.CTabFolderItemsOffset;

import des.wangku.operate.standard.dialog.SheetImportExcelDialog;
import des.wangku.operate.standard.dialog.SheetImportExcelDialog.ImportSheetInfor;
import des.wangku.operate.standard.dialog.SheetSummationDialog;
import des.wangku.operate.standard.dialog.InputNewSheetDialog;
import des.wangku.operate.standard.dialog.InputNewSheetDialog.NewSheetInfor;
import des.wangku.operate.standard.utls.UtilsArrays;
import des.wangku.operate.standard.utls.UtilsPOI;
import des.wangku.operate.standard.utls.UtilsProperties;
import des.wangku.operate.standard.utls.UtilsRnd;
import des.wangku.operate.standard.utls.UtilsSWTPOI;

/**
 * CTabFolder抽象类 配置信息
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public abstract class AbstractCTabFolder extends CTabFolder implements InterfaceProperties, InterfaceResultTableUpdate {
	/** 父容器 */
	Composite parent = null;
	/** 名称 */
	String title = "";
	/** 自身this */
	AbstractCTabFolder base = this;
	Shell shell = getShell();
	/** 参数 */
	Properties properties = null;
	/** 参数 */
	ParaClass pc = new ParaClass();
	/** 表格列表 */
	protected List<ControlClass> controlList = new ArrayList<>();
	Menu menuCTableFolder = null;

	MenuItem menuItemHidden = null;

	MenuItem menuItemIsHiddenChange = null;
	/**
	 * 构造函数
	 * @param parent Composite
	 * @param style int
	 */
	public AbstractCTabFolder(Composite parent, int style) {
		super(parent, style | styleStandard);
		checkSubclass();
		init();
	}

	/**
	 * 构造函数
	 * @param parent Composite
	 * @param style int
	 * @param properties Properties
	 */
	public AbstractCTabFolder(Composite parent, int style, Properties properties) {
		super(parent, style | styleStandard);
		checkSubclass();
		this.properties = properties;
		this.shell = new Shell(parent.getShell());
		init();
	}

	/**
	 * 构造函数
	 * @param parent Composite
	 * @param style int
	 * @param title String
	 */
	public AbstractCTabFolder(Composite parent, int style, String title) {
		super(parent, style | styleStandard);
		checkSubclass();
		this.title = title;
		init();
	}

	/**
	 * 构造函数
	 * @param parent Composite
	 * @param style int
	 * @param title String
	 * @param properties Properties
	 */
	public AbstractCTabFolder(Composite parent, int style, String title, Properties properties) {
		super(parent, style | styleStandard);
		checkSubclass();
		this.title = title;
		this.properties = properties;
		this.shell = new Shell(parent.getShell());
		init();
	}

	/**
	 * 添加一个新的sheet， 返回ResultTable
	 * @param sheetname String
	 * @return ResultTable
	 */
	public ResultTable addResultTable(String sheetname) {
		CTabItem tbtmExcel = new CTabItem(this, SWT.NONE);
		tbtmExcel.setText(sheetname);
		ResultTable t = new ResultTable(this, ResultTable.ACC_ResultTableState, properties, sheetname);
		tbtmExcel.setControl(t);
		return t;
	}

	/**
	 * 添加一个新的sheet， 返回ResultTable
	 * @param sheetname String
	 * @param jsonStr String
	 * @return ResultTable
	 */
	public ResultTable addResultTable(String sheetname, String jsonStr) {
		ResultTable e = addResultTable(sheetname);
		e.setEctpara(jsonStr);
		return e;
	}

	/* Button button = new Button(this, SWT.ARROW | SWT.TOP); */
	protected void checkSubclass() {

	}

	/**
	 * Dispose对象时需要关闭的资源
	 */
	public abstract void close();

	/**
	 * 交换标题位置
	 * @param x int
	 * @param y int
	 */
	@SuppressWarnings("restriction")
	public final void exchange(int x, int y) {
		if (!isReadIndex(x, y) || x==y) return;
		try {
			Object obj = UNSAFE.getObject(this, CTabFolderItemsOffset);/* CTabItem数组 */

			long x1 = CTabItemBaseOffset + x * CTabItemIndexScale;
			long y1 = CTabItemBaseOffset + y * CTabItemIndexScale;

			Object obj1 = UNSAFE.getObject(obj, x1);/* CTabItem数组 */
			Object obj2 = UNSAFE.getObject(obj, y1);/* CTabItem数组 */

			UNSAFE.putObject(obj, x1, obj2);
			UNSAFE.putObject(obj, y1, obj1);
			Method method = CTabFolder.class.getDeclaredMethod("updateFolder", int.class);/* 刷新 */
			method.setAccessible(true);
			method.invoke(this, 1);

		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}
	/**
	 * 表格交换位置
	 * @param x int
	 * @param y int
	 */
	public final void exChangeControl(int x, int y) {
		if (!isReadIndex(x, y) || x==y) return;
		Control con1 = getControl(x);
		Control con2 = getControl(y);
		ControlClass a = getControlClass(con1);
		ControlClass b = getControlClass(con2);
		if (a == null || b == null) return;
		ControlClass t = new ControlClass(a);
		a.input(b);
		b.input(t);
	}

	/**
	 * 得到排行的 Control
	 * @param x int
	 * @return Control
	 */
	public final Control getControl(int x) {
		if (x < 0) return null;
		CTabItem[] arrs = getItems();
		if (x >= arrs.length) return null;
		return arrs[x].getControl();
	}

	/**
	 * 得到Control所在的表格单元
	 * @param obj Control
	 * @return ControlClass
	 */
	public final ControlClass getControlClass(Control obj) {
		if (obj == null) return null;
		for (ControlClass ff : controlList)
			if (obj.equals(ff.e)) return ff;
		return null;
	}

	/**
	 * 得到下标所在的表格单元
	 * @param x int
	 * @return ControlClass
	 */
	public final ControlClass getControlClass(int x) {
		if (x < 0 || x >= controlList.size()) return null;
		return controlList.get(x);
	}

	/**
	 * 通过Control得到下标，如果没有找到，则返回-1
	 * @param obj Control
	 * @return int
	 */
	public final int getControlIndex(Control obj) {
		if (obj == null) return -1;
		CTabItem[] arrs = getItems();
		for (int i = 0; i < arrs.length; i++) {
			CTabItem e = arrs[i];
			Control econtrol = e.getControl();
			if (econtrol.equals(obj)) return i;
		}
		return -1;
	}

	/**
	 * 通过CTabItem得到下标，如果没有找到，则返回-1
	 * @param obj CTabItem
	 * @return int
	 */
	public final int getControlIndex(CTabItem obj) {
		if (obj == null) return -1;
		CTabItem[] arrs = getItems();
		for (int i = 0; i < arrs.length; i++)
			if (arrs[i].equals(obj)) return i;
		return -1;
	}

	/**
	 * 得到所有页的名称
	 * @return String[]
	 */
	public final String[] getCTableTitle() {
		CTabItem[] arrs = this.getItems();
		int len = arrs.length;
		String[] result = new String[len];
		for (int i = 0; i < len; i++)
			result[i] = arrs[i].getText();
		return result;
	}

	/**
	 * 得到指定页的名称
	 * @param index int
	 * @return String
	 */
	public final String getCTableTitle(int index) {
		String[] arrs = this.getCTableTitle();
		if (index < 0 || index >= arrs.length) return null;
		return arrs[index];
	}
	/**
	 * 自动修改单元状态值 显示/隐藏状态值
	 * @return Listener
	 */
	private Listener getListenerchangehidden() {
		Listener t = new Listener() {
			@Override
			public void handleEvent(Event event) {
				Control obj = getSelectionControl();
				if (obj == null) return;
				ControlClass e = getControlClass(obj);
				if (e == null) return;
				e.isHidden = !e.isHidden;
			}
		};
		return t;
	}
	/**
	 * 自动修改菜单 显示/隐藏
	 * @return MenuListener
	 */
	private MenuListener getListenerchangehiddenmenu() {
		MenuListener t = new MenuListener() {
			@Override
			public void menuHidden(MenuEvent e) {

			}

			@Override
			public void menuShown(MenuEvent e) {
				Control obj = getSelectionControl();
				if (obj == null) return;
				menuItemIsHiddenChange.setText(isHidenControl(obj) ? "显示" : "隐藏");
			}

		};
		return t;
	}

	/**
	 * 移动选中的sheet
	 * @return Listener
	 */
	private Listener getListenerControlMove(boolean isleft) {
		Listener t = new Listener() {
			public void handleEvent(Event e) {
				Control obj = getSelectionControl();
				if (obj == null) return;
				int x = getControlIndex(obj);
				int b = isleft ? x - 1 : x + 1;
				if (!isReadIndex(x, b)) return;
				exchange(x, b);
				exChangeControl(x, b);
			}
		};
		return t;
	}

	/**
	 * 添加新的sheet
	 * @return Listener
	 */
	private final Listener getListenerCTabFolderAddSheet() {
		Listener t = new Listener() {
			public void handleEvent(Event e) {
				InputNewSheetDialog dialog = new InputNewSheetDialog(shell, SWT.CENTER, base);
				NewSheetInfor newSheetInfor = (NewSheetInfor) dialog.open();
				String sheetname = newSheetInfor.getSheetName();
				logger.debug("sheetname:" + sheetname);
				newSheetInfor.addSheet(base);
			}
		};
		return t;
	}

	/**
	 * 导入新的sheet
	 * @return Listener
	 */
	private Listener getListenerCTabFolderimportSheet() {
		Listener t = new Listener() {
			public void handleEvent(Event e) {
				SheetImportExcelDialog dialog = new SheetImportExcelDialog(shell, SWT.CENTER, base);
				Object obj = dialog.open();
				if (obj == null) return;
				ImportSheetInfor isinfor = (ImportSheetInfor) obj;
				//String sheetname = newSheetInfor.getSheetName();
				logger.debug("sheetname:" + isinfor.toString());
				isinfor.addSheet(base);
				//newSheetInfor.addSheet(base);
			}
		};
		return t;
	}

	/**
	 * 把多个table记录保存到excel中
	 * @return Listener
	 */
	private final Listener getListenerCTabFolderToExcel() {
		Listener t = new Listener() {
			public void handleEvent(Event e) {
				ResultTable[] tables = getResultTables();
				if (tables.length == 0) return;
				Workbook workbook = new XSSFWorkbook();
				for (int i = 0; i < tables.length; i++) {
					ResultTable table = tables[i];
					String sheetName = getSheetName(table, "信息" + i);
					UtilsSWTPOI.addWorkbookSheet(workbook, sheetName, table);
				}
				File file = UtilsPOI.save(pc.saveFolder, shell, workbook, true, true);
				if (file == null) return;
				logger.debug("copyCTabFolderToexcel:" + file.getAbsolutePath());
			}
		};
		return t;
	}

	/**
	 * 把多个table记录保存到excel中<br>
	 * 把多个表保存在一个sheet中
	 * @return Listener
	 */
	private final Listener getListenerCTabFolderToExcelAccumulate() {
		Listener t = new Listener() {
			public void handleEvent(Event e) {
				ResultTable[] tables = getResultTables();
				if (tables.length == 0) return;
				SheetSummationDialog dialog = new SheetSummationDialog(shell, SWT.CENTER, base);
				String[] arrs = dialog.open();
				if (arrs == null|| arrs.length==0) return;
				ResultTable[] tablesCheck=getResultTablesByTitle(arrs);
				if (tablesCheck.length==0) return;
				Workbook workbook = new XSSFWorkbook();
				UtilsSWTPOI.addWorkbookSheet(workbook, "综合", tablesCheck);
				File file = UtilsPOI.save(pc.saveFolder, shell, workbook, true, true);
				if (file == null) return;
				logger.debug("copyCTabFolderMultiTableToexcelAccumulate:" + file.getAbsolutePath());
			}
		};
		return t;
	}

	/**
	 * 把CTabFolder中选中的sheet保存到excel
	 * @return Listener
	 */
	private final Listener getListenerSelectSheetToExcel() {
		Listener t = new Listener() {
			public void handleEvent(Event e) {
				CTabItem target = getSelection();
				Control c = target.getControl();
				if (!(c instanceof ResultTable)) return;
				ResultTable table = (ResultTable) c;
				Workbook workbook = new XSSFWorkbook();
				UtilsSWTPOI.addWorkbookSheet(workbook, table);
				File file = UtilsPOI.save(pc.saveFolder, shell, workbook, true, true);
				if (file == null) return;
				logger.debug("copySelectSheetToexcel:" + file.getAbsolutePath());
			}
		};
		return t;
	}

	/**
	 * 显示或隐藏表格
	 * @return Listener
	 */
	private Listener getListenerShowHiddenSheet() {
		Listener t = new Listener() {
			public void handleEvent(Event e) {
				boolean isSelection = menuItemHidden.getSelection();
				if (!isSelection) hidden();
				else showHidden();
			}
		};
		return t;
	}

	/**
	 * 得到一个空的表格名称 发现key同名，则以 key+(0~999)<br>
	 * 否则得到一个日期+随机(4位)的字符串
	 * @param key String
	 * @return String
	 */
	public final String getNewTabItemName(final String key) {
		if (key == null) return UtilsRnd.getNewFilenameNow(4, 4);
		String newKey = UtilsPOI.getSheetNameFormat(key);
		if (!isExistTitle(newKey)) return newKey;
		for (int i = 0; i <= 999; i++) {
			String name = newKey + "(" + i + ")";
			if (!isExistTitle(name)) return name;
		}
		return newKey + UtilsRnd.getNewFilenameNow(4, 4);
	}

	public final ParaClass getPc() {
		return pc;
	}

	@Override
	public Properties getProperties() {
		return this.properties;
	}

	/**
	 * 得到指定位的 ResultTable<br>
	 * 如果为null，则没有找到
	 * @param index int
	 * @return ResultTable
	 */
	public ResultTable getResultTable(int index) {
		ResultTable[] arrs = getResultTables();
		if (index < 0 || index >= arrs.length) return null;
		return arrs[index];
	}

	/**
	 * 得到指定名称的 ResultTable
	 * @param title String
	 * @return ResultTable
	 */
	public ResultTable getResultTable(String title) {
		if (title == null) return null;
		ResultTable[] arr=getResultTables();
		for(ResultTable e:arr)
			if(title.equals(e.title))return e;
		return null;
	}

	/**
	 * 得到当前对象的ResultTable数组
	 * @return ResultTable[]
	 */
	public ResultTable[] getResultTables() {
		CTabItem[] arrs = this.getItems();
		int len = arrs.length;
		List<Table> list = new ArrayList<>(len);
		for (int i = 0; i < len; i++) {
			Control econtrol = arrs[i].getControl();
			if (econtrol instanceof ResultTable) list.add((ResultTable) econtrol);
		}
		ResultTable[] newarr = {};
		return list.toArray(newarr);
	}

	/**
	 * 按照名称数组检索ResultTable数组
	 * @param titleArrs String[]
	 * @return ResultTable[]
	 */
	public ResultTable[] getResultTablesByTitle(String...titleArrs) {
		List<ResultTable> list=new ArrayList<>();
		ResultTable[] arr=getResultTables();
		for(ResultTable e:arr) {
			if(UtilsArrays.isExist(e.title, titleArrs))list.add(e);
		}
		return UtilsArrays.toArray(ResultTable.class, list);
	}

	/**
	 * 得到当前选中的单元
	 * @return Control
	 */
	public Control getSelectionControl() {
		CTabItem f = this.getSelection();
		if (f == null) return null;
		return f.getControl();
	}

	/**
	 * 得到当前选中的 ResultTable
	 * @return ResultTable
	 */
	public ResultTable getSelectResultTable() {
		CTabItem target = this.getSelection();
		if (target == null) return null;
		Control c = target.getControl();
		if (c == null) return null;
		if (!(c instanceof ResultTable)) return null;
		return (ResultTable) c;
	}

	/**
	 * 得到当前选听标题
	 * @return String
	 */
	public String getSelectTitle() {
		CTabItem target = this.getSelection();
		if (target == null) return null;
		return target.getText();
	}

	/**
	 * 得到控件的名称
	 * @return String
	 */
	public final String getTitle() {
		return title;
	}

	/**
	 * 隐藏表格，如果有名称相同的，则dispose
	 */
	private final void hidden() {
		CTabItem[] arrs = base.getItems();
		for (CTabItem f : arrs) {
			Control obj = f.getControl();
			for (ControlClass ff : controlList)
				if (obj.equals(ff.e) && ff.isHidden) f.dispose();
		}
	}

	/**
	 * 初始化
	 */
	private void init() {
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				System.out.println("DisposeListener 被调用");
				close();
			}
		});
	}

	/**
	 * 把CTablItem 插入到index中
	 * @param item CTabItem
	 * @param index int
	 */
	public void insertItem(CTabItem item, int index) {
		try {
			Method method = CTabFolder.class.getDeclaredMethod("createItem", CTabItem.class, int.class);
			method.setAccessible(true);
			method.invoke(this, item, index);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 判断是否存在隐藏表格
	 * @return boolean
	 */
	protected boolean isExistHiddenControl() {
		for (ControlClass f : controlList)
			if (f.isHidden) return true;
		return false;
	}

	/**
	 * 判断 title 是否有 title
	 * @param title String
	 * @return boolean
	 */
	public final boolean isExistSheetName(String title) {
		String[] arr = getCTableTitle();
		for (int i = 0, len = arr.length; i < len; i++)
			if (arr[i].equals(title)) return true;
		return false;
	}

	/**
	 * 是否存在名称的表格
	 * @param key String
	 * @return boolean
	 */
	public final boolean isExistTitle(final String key) {
		CTabItem[] arrs = this.getItems();
		for (CTabItem e : arrs)
			if (e.getText().equals(key)) return true;
		return false;
	}

	/**
	 * 判断Control对象是否是隐藏型
	 * @param obj Control
	 * @return boolean
	 */
	public final boolean isHidenControl(Control obj) {
		if (obj == null) return false;
		for (ControlClass ff : controlList)
			if (obj.equals(ff.e)) return ff.isHidden;
		return false;
	}

	/**
	 * 判断多个个下标是否是标准的数字
	 * @param arrs int[]
	 * @return boolean
	 */
	public final boolean isReadIndex(int... arrs) {
		int count = this.getItemCount();
		for (int x : arrs)
			if (x < 0 || x >= count) return false;
		return true;
	}

	/**
	 * 此页是否显示头部
	 * @return boolean
	 */
	public boolean isViewTableHead() {
		ResultTable first = this.getResultTable(0);
		if (first == null) return false;
		return first.getEctpara().isViewHead;
	}

	/**
	 * 把信息读取到CTabFolder中
	 */
	public abstract void makeCTabFolder();
	/**
	 * 打开单元。后面添加
	 * @param f ControlClass
	 */
	public void openControl(ControlClass f) {
		openControl(f, -1);
	}

	/**
	 * 打开单元。如果index==-1，则在后面添加。否则在指定位置添加
	 * @param f ControlClass
	 * @param index int
	 */
	public void openControl(ControlClass f, int index) {
		if (f == null) return;
		CTabItem tbtmExcel = null;
		if (index < 0 || index > getItemCount()) tbtmExcel = new CTabItem(this, SWT.NONE);
		else tbtmExcel = new CTabItem(this, SWT.NONE, index);
		tbtmExcel.setText(f.sheetname);
		tbtmExcel.setControl(f.e);
	}

	/**
	 * 打开所有的隐藏单元
	 */
	protected void openControlHidden() {
		for (int i = 0, len = controlList.size(); i < len; i++) {
			ControlClass f = controlList.get(i);
			if (f.isHidden) openControl(f, i);
		}
	}

	/**
	 * 打开所有的显示单元
	 */
	protected void openControlList() {
		for (int i = 0, len = controlList.size(); i < len; i++) {
			ControlClass f = controlList.get(i);
			if (!f.isHidden) openControl(f, -1);
		}
	}

	/**
	 * 鼠标右键弹出菜单
	 */
	public void putPopMenu() {
		menuCTableFolder = new Menu(this);
		setMenu(menuCTableFolder);

		MenuItem a = new MenuItem(menuCTableFolder, SWT.CASCADE);
		a.setText("选中当前页");
		Menu b = new Menu(this.getShell(), SWT.DROP_DOWN);
		a.setMenu(b);
		setMenuItemSelTable(b);
		b.addMenuListener(getListenerchangehiddenmenu());

		MenuItem c = new MenuItem(menuCTableFolder, SWT.CASCADE);
		c.setText("数据页通用");
		Menu d = new Menu(this.getShell(), SWT.DROP_DOWN);
		c.setMenu(d);
		setMenuItemTable(d);

		new MenuItem(menuCTableFolder, SWT.SEPARATOR);

		MenuItem e = new MenuItem(menuCTableFolder, SWT.CASCADE);
		e.setText("导出数据页");
		Menu f = new Menu(this.getShell(), SWT.DROP_DOWN);
		e.setMenu(f);
		setMenuItemCheckExport(f);

		this.setSimple(true);
		setUnselectedCloseVisible(true);
		this.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
	}

	/**
	 * 设置弹出菜单 - 导出
	 * @param parent Menu
	 */
	private void setMenuItemCheckExport(Menu parent) {
		MenuItem a = new MenuItem(parent, SWT.NONE);
		a.setText("Excel-选中页[单sheet]");
		a.addListener(SWT.Selection, getListenerSelectSheetToExcel());

		MenuItem b = new MenuItem(parent, SWT.NONE);
		b.setText("Excel-所有页[多sheet]");
		b.addListener(SWT.Selection, getListenerCTabFolderToExcel());

		MenuItem c = new MenuItem(parent, SWT.NONE);
		c.setText("Excel-综合页[多sheet合成]");
		c.addListener(SWT.Selection, getListenerCTabFolderToExcelAccumulate());
	}

	/**
	 * 设置弹出菜单 - 选中当数据页
	 * @param parent Menu
	 */
	private void setMenuItemSelTable(Menu parent) {
		MenuItem a = new MenuItem(parent, SWT.NONE);
		a.setText("前移");
		a.addListener(SWT.Selection, getListenerControlMove(true));
		MenuItem b = new MenuItem(parent, SWT.NONE);
		b.setText("后移");
		b.addListener(SWT.Selection, getListenerControlMove(false));
		menuItemIsHiddenChange = new MenuItem(parent, SWT.NONE);
		menuItemIsHiddenChange.addListener(SWT.Selection, getListenerchangehidden());
	}

	/**
	 * 设置弹出菜单 - 表格
	 * @param parent Menu
	 */
	private void setMenuItemTable(Menu parent) {
		if (isExistHiddenControl()) {
			menuItemHidden = new MenuItem(parent, SWT.NONE | SWT.CHECK);
			menuItemHidden.setText("显示隐藏页");
			menuItemHidden.addListener(SWT.Selection, getListenerShowHiddenSheet());
		}
		MenuItem b = new MenuItem(parent, SWT.NONE);
		b.setText("导入数据页");
		b.addListener(SWT.Selection, getListenerCTabFolderimportSheet());
		MenuItem c = new MenuItem(parent, SWT.NONE);
		c.setText("添加数据页");
		c.addListener(SWT.Selection, getListenerCTabFolderAddSheet());
	}

	public final void setTitle(String title) {
		this.title = title;
	}

	/**
	 * 显示隐藏表格
	 */
	private final void showHidden() {
		for (int i = 0, len = controlList.size(); i < len; i++) {
			ControlClass f = controlList.get(i);
			if (!f.isHidden) continue;
			if (base.isExistSheetName(f.sheetname)) continue;
			String name = getNewTabItemName(f.sheetname);
			f.sheetname = name;
			openControl(f, i);
		}
	}

	/**
	 * 表格单元
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 */
	public static final class ControlClass {
		/** 表格单元 */
		Control e = null;
		/** 表格名称 */
		String sheetname = "";
		/** 是否为隐藏型表格 */
		boolean isHidden = false;

		/**
		 * 构造函数
		 * @param f ControlClass
		 */
		public ControlClass(ControlClass f) {
			if (f == null) return;
			this.sheetname = f.sheetname;
			this.e = f.e;
			this.isHidden = f.isHidden;
		}

		/**
		 * 构造函数
		 * @param sheetname String
		 * @param e Control
		 * @param isHidden boolean
		 */
		public ControlClass(String sheetname, Control e, boolean isHidden) {
			this.sheetname = sheetname;
			this.e = e;
			this.isHidden = isHidden;
		}

		/**
		 * 导入数据
		 * @param f ControlClass
		 */
		public void input(ControlClass f) {
			if (f == null) return;
			this.sheetname = f.sheetname;
			this.e = f.e;
			this.isHidden = f.isHidden;
		}
	}

	/**
	 * 隐藏表格
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 */
	@Deprecated
	public static final class ControlHiddenClass {
		Control e = null;
		String sheetname = "";

		/**
		 * 构造函数
		 * @param sheetname String
		 * @param e Control
		 */
		public ControlHiddenClass(String sheetname, Control e) {
			this.sheetname = sheetname;
			this.e = e;
		}
	}

	/**
	 * 参数 - Excel导入到CTabFolder时需要修整的参数
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 */
	public static final class ParaClass {
		/** 输出的目录 如 P01 */
		String saveFolder = "";

		public final String getSaveFolder() {
			return saveFolder;
		}

		public void init(Properties properties) {
			if (properties == null) return;
			saveFolder = UtilsProperties.getProPropValue(properties, "ECT_saveFolder", this.saveFolder);
		}

		public final ParaClass setSaveFolder(String saveFolder) {
			this.saveFolder = saveFolder;
			return this;
		}

	}

	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(AbstractCTabFolder.class);

	/** 标准样式 */
	static final int styleStandard = SWT.BORDER | SWT.BOTTOM | SWT.FLAT;
	/** 隐藏的sheet表格 */
	//protected List<ControlHiddenClass> hiddenControlList = new ArrayList<>();


	/**
	 * 通过table的上级得到标题，如果没有找到，则返回 defSheetName<br>
	 * 表格的父容器一般为组或选项卡
	 * @param table ResultTable
	 * @param defSheetName String
	 * @return String
	 */
	public static final String getSheetName(ResultTable table, String defSheetName) {
		if (table == null) return defSheetName;
		Composite parent = table.getParent();
		if (parent == null) return defSheetName;
		/* 如果父容器是组，则返回组标题 */
		if (parent instanceof org.eclipse.swt.widgets.Group) {
			Group g = (Group) parent;
			return g.getText();
		}
		/* 如果父容器是选项卡，则返回选中的选项卡的标题 */
		if (parent instanceof org.eclipse.swt.custom.CTabFolder) {
			CTabFolder cTabItem = (CTabFolder) parent;
			CTabItem[] arrs = cTabItem.getItems();
			for (int i = 0; i < arrs.length; i++) {
				CTabItem e = arrs[i];
				Control econtrol = e.getControl();
				if (econtrol instanceof org.eclipse.swt.widgets.Table) {
					if (table.equals((Table) econtrol)) return e.getText();
				}
			}
		}
		return defSheetName;
	}


}
