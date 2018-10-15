package des.wangku.operate.standard.swt;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;
import des.wangku.operate.standard.task.AbstractTask;
import des.wangku.operate.standard.utls.UtilsPathFile;
import des.wangku.operate.standard.utls.UtilsSWTPOI;
import des.wangku.operate.standard.utls.UtilsSWTTableSQL;
import des.wangku.operate.standard.utls.UtilsSWTTools;

/**
 * 自定义excel转成多页表格 UI组件
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class ExcelCTabFolder extends AbstractCTabFolder {
	/** 日志 */
	static Logger logger = Logger.getLogger(ExcelCTabFolder.class);
	/** excel文件 */
	String filename = null;
	/** excel */
	Workbook workbook = null;

	/**
	 * AbstractTask 项目组中的各项目自定义，配置信息与excel文件名相同 "des-wkope-task-XXXX.xlsx"
	 * @param parent Composite
	 * @param style int
	 */
	public ExcelCTabFolder(Composite parent, int style) {
		super(parent, style);
		structureInit(parent,style);
	}
	/**
	 * AbstractTask 项目组中的各项目自定义，配置信息与excel文件名相同 "des-wkope-task-XXXX.xlsx"
	 * @param parent Composite
	 * @param style int
	 * @param title String
	 */
	public ExcelCTabFolder(Composite parent, int style,String title) {
		super(parent, style,title);
		structureInit(parent,style);
	}
	/**
	 * 构造初始化
	 * @param parent Composite
	 * @param style int
	 */
	void structureInit(Composite parent,int style) {
		String filename = null;
		AbstractTask t = UtilsSWTTools.getParentObjSuperclass(parent, AbstractTask.class);
		if (t == null) return;
		this.properties = t.getProProperties();
		this.pc = t.getPc();
		filename = AbstractTask.ACC_PROHead + t.getMenuNameHead().toLowerCase() + ".xlsx";
		logger.debug("filename:"+filename);
		Init(parent, style, 1, filename);		
	}

	/**
	 * 直接录入配置信息
	 * @param parent Composite
	 * @param style int
	 * @param type int
	 * @param filename String
	 * @param properties Properties
	 */
	public ExcelCTabFolder(Composite parent, int style, int type, String filename, Properties properties) {
		super(parent, style, properties);
		Init(parent, style, type, filename);
	}

	/**
	 * 直接录入配置信息
	 * @param parent Composite
	 * @param style int
	 * @param title String
	 * @param type int
	 * @param filename String
	 * @param properties Properties
	 */
	public ExcelCTabFolder(Composite parent, int style,String title, int type, String filename, Properties properties) {
		super(parent, style,title, properties);
		Init(parent, style, type, filename);
	}

	/**
	 * 初始化
	 * @param parent Composite 父容器
	 * @param filename String excel文件绝对路径
	 */
	private void Init(Composite parent, int style, int type, String filename) {
		String newString = filename;
		switch (type) {
		case 1:
			newString = UtilsPathFile.getModelJarBasicPath() + "/" + filename;
		default:
		}
		this.filename = newString;
		makeCTabFolder();
		this.putPopMenu();
		this.setSelection(0);
	}

	public void makeCTabFolder() {
		if (filename == null) return;
		logger.debug("提取excel文件:" + filename);
		try {
			File file = new File(filename);
			workbook = new XSSFWorkbook(file);
			for (int sheetnum = 0; sheetnum < workbook.getNumberOfSheets(); sheetnum++) {
				ResultTable t = new ResultTable(this, ResultTable.ACC_ResultTableState, properties, workbook.getSheetName(sheetnum));
				makeCTabFolderFromExcel(this, t, workbook.getSheetAt(sheetnum));
				t.initialization();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (workbook != null) workbook.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (this.getChildren().length > 0) setSelection(0);
	}

	/**
	 * 把Sheet读取到CTabFolder中
	 * @param sheet Sheet
	 */
	void makeCTabFolderFromExcel(CTabFolder parent, ResultTable t, Sheet sheet) {
		String sheetname = sheet.getSheetName();
		CTabItem tbtmExcel = new CTabItem(parent, SWT.NONE);
		tbtmExcel.setText(sheetname);
		tbtmExcel.setControl(t);

		final int rowslen = sheet.getLastRowNum();/* 行数 */
		mkExcelResultTableHead(t, sheet);
		int cellLen = t.getColumnCount();
		List<String> list = new ArrayList<>(cellLen);
		for (int i = 0; i <= rowslen; i++) {
			if (i == t.getEctpara().headRowSuffix) continue;
			Row r = sheet.getRow(i);
			if (r == null) continue;
			list.clear();
			for (int ii = 0; ii < cellLen; ii++) {
				Cell cell = r.getCell(ii);
				String value = UtilsSWTPOI.getCellValueByCell(cell);
				if (t.getEctpara().isTrim) value = value.trim();
				list.add(value);
			}
			UtilsSWTTableSQL.add(t, list);
		}
	}

	/**
	 * 设置Excel ResultTable头部信息
	 * @param t ResultTable
	 * @param sheet Sheet
	 */
	void mkExcelResultTableHead(ResultTable t, Sheet sheet) {
		final int rowslen = sheet.getPhysicalNumberOfRows();//sheet.getLastRowNum();/* 行数 */
		if (rowslen == 0) return;
		boolean isBadSuffix = false;
		isBadSuffix = t.getEctpara().headRowSuffix < 0 || t.getEctpara().headRowSuffix >= rowslen;
		Row first = isBadSuffix ? sheet.getRow(0) : sheet.getRow(t.getEctpara().headRowSuffix);
		int cellLen = first.getLastCellNum();/* 列数 */
		String value = null;
		String[] newArray = new String[cellLen];
		for (int i = 0; i < cellLen; i++) {
			if (!isBadSuffix) value = UtilsSWTPOI.getCellValueByCell(first.getCell(i));
			else value = "" + i;
			newArray[i] = value;
		}
		t.mkResultTableHead(newArray);
	}

	@Override
	public void close() {
		try {
			if (workbook != null) workbook.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	public boolean resultTableUpdate(ResultTable base,int x, int y,String oldValue, String newValue) {
		return true;
	}

}
