package des.wangku.operate.standard.swt;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
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

import des.wangku.operate.standard.dialog.ImportExcelSheet;
import des.wangku.operate.standard.dialog.ImportExcelSheet.ImportSheetInfor;
import des.wangku.operate.standard.dialog.InputNewSheetDialog;
import des.wangku.operate.standard.dialog.InputNewSheetDialog.NewSheetInfor;
import des.wangku.operate.standard.utls.UtilsProperties;
import des.wangku.operate.standard.utls.UtilsSWTPOI;

/**
 * CTabFolder抽象类 配置信息
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
@SuppressWarnings("resource")
public abstract class AbstractCTabFolder extends CTabFolder implements InterfaceProperties, InterfaceResultTableUpdate {
	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(AbstractCTabFolder.class);
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
	/** 标准样式 */
	static final int styleStandard = SWT.BORDER | SWT.BOTTOM | SWT.FLAT;

	/* Button button = new Button(this, SWT.ARROW | SWT.TOP); */
	protected void checkSubclass() {

	}

	public AbstractCTabFolder(Composite parent, int style) {
		super(parent, style | styleStandard);
		checkSubclass();
		init();
	}

	public AbstractCTabFolder(Composite parent, int style, String title) {
		super(parent, style | styleStandard);
		checkSubclass();
		this.title = title;
		init();
	}

	public AbstractCTabFolder(Composite parent, int style, Properties properties) {
		super(parent, style | styleStandard);
		checkSubclass();
		this.properties = properties;
		this.shell = new Shell(parent.getShell());
		init();
	}

	public AbstractCTabFolder(Composite parent, int style, String title, Properties properties) {
		super(parent, style | styleStandard);
		checkSubclass();
		this.title = title;
		this.properties = properties;
		this.shell = new Shell(parent.getShell());
		init();
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
	 * Dispose对象时需要关闭的资源
	 */
	public abstract void close();

	/**
	 * 把信息读取到CTabFolder中
	 */
	public abstract void makeCTabFolder();

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
	 * 得到指定名称的 ResultTable
	 * @param name String
	 * @return ResultTable
	 */
	public ResultTable getResultTable(String name) {
		if (name == null) return null;
		CTabItem[] arrs = this.getItems();
		int len = arrs.length;
		for (int i = 0; i < len; i++) {
			Control econtrol = arrs[i].getControl();
			if (econtrol instanceof ResultTable) {
				if (arrs[i].getText().equals(name)) return (ResultTable) econtrol;
			}
		}
		return null;
	}

	/**
	 * 得到指定位的 ResultTable
	 * @param index int
	 * @return ResultTable
	 */
	public ResultTable getResultTable(int index) {
		ResultTable[] arrs = getResultTables();
		if (index < 0 || index >= arrs.length) return null;
		return arrs[index];
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
	 * 得到当前选听标题
	 * @return String
	 */
	public String getSelectTitle() {
		CTabItem target = this.getSelection();
		if (target == null) return null;
		return target.getText();
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
	 * 得到指定页的名称
	 * @param index int
	 * @return String
	 */
	public String getCTableTitle(int index) {
		String[] arrs=this.getCTableTitle();
		if(index<0 || index>=arrs.length)return null;
		return arrs[index];
	}
	/**
	 * 得到所有页的名称
	 * @return String[]
	 */
	public String[] getCTableTitle() {
		CTabItem[] arrs = this.getItems();
		int len = arrs.length;
		String[] result = new String[len];
		for (int i = 0; i < len; i++)
			result[i] = arrs[i].getText();
		return result;
	}

	/**
	 * 鼠标右键弹出菜单
	 */
	public void putPopMenu() {
		Menu menuCTableFolder = new Menu(this);
		setMenu(menuCTableFolder);
		MenuItem menuItemInput = new MenuItem(menuCTableFolder, SWT.NONE);
		menuItemInput.setText("导入Sheet");
		menuItemInput.addListener(SWT.Selection, getListenerCTabFolderimportSheet());
		MenuItem menuItemCheck = new MenuItem(menuCTableFolder, SWT.NONE);
		menuItemCheck.setText("添加Sheet");
		menuItemCheck.addListener(SWT.Selection, getListenerCTabFolderAddSheet());

		new MenuItem(menuCTableFolder, SWT.SEPARATOR);

		MenuItem subExport = new MenuItem(menuCTableFolder, SWT.CASCADE);
		subExport.setText("导出");
		Menu menu_Export = new Menu(this.getShell(), SWT.DROP_DOWN);
		subExport.setMenu(menu_Export);
		setMenuItemCheckExport(menu_Export);
		this.setSimple(true);
		setUnselectedCloseVisible(true);
		this.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
	}

	/**
	 * 设置弹出菜单 - 导出
	 * @param name
	 */
	private void setMenuItemCheckExport(Menu parent) {
		MenuItem menuItemCopytoExcel = new MenuItem(parent, SWT.NONE);
		menuItemCopytoExcel.setText("Excel-选中页");
		menuItemCopytoExcel.addListener(SWT.Selection, getListenerSelectSheetToExcel());

		MenuItem menuItemCopytoExcels = new MenuItem(parent, SWT.NONE);
		menuItemCopytoExcels.setText("Excel-所有页");
		menuItemCopytoExcels.addListener(SWT.Selection, getListenerCTabFolderToExcel());
	}

	/**
	 * 导入新的sheet
	 * @return Listener
	 */
	private Listener getListenerCTabFolderimportSheet() {
		Listener t = new Listener() {
			public void handleEvent(Event e) {
				ImportExcelSheet dialog = new ImportExcelSheet(shell, SWT.CENTER, base);
				Object obj= dialog.open();
				if(obj==null)return;
				ImportSheetInfor isinfor=(ImportSheetInfor)obj;
				//String sheetname = newSheetInfor.getSheetName();
				logger.debug("sheetname:" + isinfor.toString());
				isinfor.addSheet(base);
				//newSheetInfor.addSheet(base);
			}
		};
		return t;
	}
	/**
	 * 添加新的sheet
	 * @return Listener
	 */
	private Listener getListenerCTabFolderAddSheet() {
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
	 * 把多个table记录保存到excel中
	 * @return Listener
	 */
	private Listener getListenerCTabFolderToExcel() {
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
				File file = UtilsSWTPOI.save(pc.saveFolder, shell, workbook,true, true);
				if (file == null) return;
				logger.debug("copyCTabFolderToexcel:" + file.getAbsolutePath());
			}
		};
		return t;
	}

	/**
	 * 把CTabFolder中选中的sheet保存到excel
	 * @return Listener
	 */
	private Listener getListenerSelectSheetToExcel() {
		Listener t = new Listener() {
			public void handleEvent(Event e) {
				Workbook workbook = new XSSFWorkbook();
				CTabItem target = getSelection();
				Control c = target.getControl();
				if (!(c instanceof ResultTable)) return;
				ResultTable table = (ResultTable) c;
				UtilsSWTPOI.addWorkbookSheet(workbook, table);
				File file = UtilsSWTPOI.save(pc.saveFolder, shell, workbook,true, true);
				if (file == null) return;
				logger.debug("copySelectSheetToexcel:" + file.getAbsolutePath());
			}
		};
		return t;
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

		public final ParaClass setSaveFolder(String saveFolder) {
			this.saveFolder = saveFolder;
			return this;
		}

		public void init(Properties properties) {
			if (properties == null) return;
			saveFolder = UtilsProperties.getProPropValue(properties, "ECT_saveFolder", this.saveFolder);
		}

	}

	public final ParaClass getPc() {
		return pc;
	}
	/**
	 * 判断sheetname是否有title
	 * @param title String
	 * @return boolean
	 */
	public boolean isExistSheetName(String title) {
		String[] arr = getCTableTitle();
		for (int i = 0, len = arr.length; i < len; i++)
			if (arr[i].equals(title)) return true;
		return false;
	}

	public String[] getSheetNames() {
		return getCTableTitle();
	}

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

	@Override
	public Properties getProperties() {
		return this.properties;
	}

	public final String getTitle() {
		return title;
	}

	public final void setTitle(String title) {
		this.title = title;
	}
	/**
	 * 添加一个新的sheet， 返回ResultTable
	 * @param sheetname String
	 * @param jsonStr String
	 * @return ResultTable
	 */
	public ResultTable addResultTable(String sheetname,String jsonStr) {
		ResultTable e=addResultTable(sheetname);
		e.setEctpara(jsonStr);
		return e;
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
}
