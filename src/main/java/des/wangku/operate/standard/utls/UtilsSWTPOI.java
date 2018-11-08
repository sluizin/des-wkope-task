package des.wangku.operate.standard.utls;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;

import des.wangku.operate.standard.dialog.TableOutputExcelParaDialog.ExcelParaClass;
import des.wangku.operate.standard.swt.ExcelCTabFolder;
import des.wangku.operate.standard.swt.ResultTable;
import des.wangku.operate.standard.task.InterfaceExcelChange;

/**
 * POI工具方法集
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class UtilsSWTPOI {
	/** 日志 */
	private static Logger logger = Logger.getLogger(UtilsSWTPOI.class);

	/**
	 * 保存workbook到model目录里的随机数文件，是否关闭Workbook
	 * @param saveFolder String
	 * @param shell Shell
	 * @param workbook Workbook
	 * @param isClose boolean
	 * @param isAlert boolean
	 * @return File
	 */
	public static File save(String saveFolder, Shell shell, Workbook workbook,boolean isClose, boolean isAlert) {
		//AbstractTask e=UtilsSWTTools.getParentObj(shell, AbstractTask.class);
		String proFolder = "";
		if (saveFolder != null) proFolder = saveFolder;
		File file = UtilsFile.mkModelRNDFile(proFolder, "xlsx");
		if (file == null) {
			if (isAlert) {
				logger.debug("文件生成失败");
				UtilsSWTMessageBox.Alert(shell, "文件生成失败");
			}
			return null;
		}
		boolean t = UtilsSWTPOI.save(workbook,isClose, file);
		if (t) {
			String filename = UtilsFile.getFileName(file);
			if (isAlert) {
				logger.debug("文件生成成功");
				UtilsSWTMessageBox.Alert(shell, filename + "生成成功");
			}
			return file;
		}
		if (isAlert) UtilsSWTMessageBox.Alert(shell, "文件生成失败");
		return null;
	}

	/**
	 * 保存Workbook到File，，是否关闭Workbook
	 * @param workbook Workbook
	 * @param isClose boolean
	 * @param file File
	 * @return boolean
	 */
	public static final boolean save(Workbook workbook,boolean isClose, File file) {
		try {
			if (workbook == null || file == null) return false;
			FileOutputStream fileoutputStream = new FileOutputStream(file);
			workbook.write(fileoutputStream);
			fileoutputStream.close();
			if(isClose)
			workbook.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 把table放入 Workbook 中 默认名称 "信息"
	 * @param workbook Workbook
	 * @param table ResultTable
	 */
	public static final void addWorkbookSheet(Workbook workbook, ResultTable table) {
		String sheetName = ExcelCTabFolder.getSheetName(table, "信息");
		UtilsSWTPOI.addWorkbookSheet(workbook, sheetName, table);
	}

	/**
	 * 把table放入 Workbook 中
	 * @param workbook Workbook
	 * @param sheetName String
	 * @param table ResultTable
	 */
	public static final void addWorkbookSheet(Workbook workbook, String sheetName, ResultTable table) {
		InterfaceExcelChange change = UtilsSWTTools.getParentInterfaceObj(table, InterfaceExcelChange.class);
		TableItem[] arr = table.getItems();
		boolean isHead = table.getHeaderVisible();
		List<List<String>> list = UtilsSWTTable.getTableItemList(isHead, table, arr);
		UtilsSWTPOI.addWorkbookSheet(workbook, sheetName, list, change);
	}

	/**
	 * 把list放入 Workbook 中
	 * @param workbook Workbook
	 * @param sheetName String
	 * @param list List&lt;List&lt;String&gt;&gt;
	 * @param change InterfaceExcelChange
	 */
	public static final void addWorkbookSheet(Workbook workbook, String sheetName, List<List<String>> list, InterfaceExcelChange change) {
		sheetName = (sheetName == null) ? "信息" : sheetName;
		Sheet sheet = workbook.createSheet(sheetName);
		CellStyle cellStyle = workbook.createCellStyle();
		// 设置这些样式  
		cellStyle.setAlignment(HorizontalAlignment.LEFT);
		cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		int maxCols = 0;
		for (int i = 0, len = list.size(); i < len; i++) {
			List<String> li = list.get(i);
			if (li.size() > maxCols) maxCols = li.size();
		}
		for (int i = 0, len = list.size(); i < len; i++) {
			List<String> li = list.get(i);
			Row row = sheet.createRow(i);
			for (int ii = 0, len2 = li.size(); ii < len2; ii++) {
				row.createCell(ii).setCellStyle(cellStyle);
				String value = li.get(ii);
				if (value == null) value = "";
				if (value.length() > 30000) value = value.substring(0, 30000);
				Cell cell = row.createCell(ii);
				cell.setCellValue(value);
			}
		}
		if (change != null) change.afterWork(sheet);
	}

	/**
	 * 通过文件名与sheetname得到sheet
	 * @param filename String
	 * @param sheetName String
	 * @return Sheet
	 */
	@SuppressWarnings({ "unused", "resource" })
	public static final Sheet getSheet(String filename, String sheetName) {
		try {
			File file = new File(filename);
			Workbook workbook = new XSSFWorkbook(file);
			if (workbook == null) return null;
			return workbook.getSheet(sheetName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param filename String
	 * @param list List&lt;List&lt;String&gt;&gt;
	 * @param sheetName String
	 * @param change InterfaceExcelChange
	 * @param epc ExcelParaClass
	 * @return boolean
	 */
	public static final boolean makeExcel(String filename, String sheetName, List<List<String>> list, InterfaceExcelChange change, ExcelParaClass epc) {
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("0");
		CellStyle cellStyle = workbook.createCellStyle();/* 设置这些样式 */
		cellStyle.setAlignment(HorizontalAlignment.LEFT);
		cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		int maxCols = 0;
		for (int i = 0, len = list.size(); i < len; i++) {
			List<String> li = list.get(i);
			if (li.size() > maxCols) maxCols = li.size();
		}
		for (int i = 0, len = list.size(); i < len; i++) {
			List<String> li = list.get(i);
			Row row = sheet.createRow(i);
			for (int ii = 0, len2 = li.size(); ii < len2; ii++) {
				row.createCell(ii).setCellStyle(cellStyle);
				String value = li.get(ii);
				if (value == null) value = "";
				if (value.length() > 30000) value = value.substring(0, 30000);
				Cell cell = row.createCell(ii);
				cell.setCellValue(value);
			}
		}
		if (change != null) change.afterWork(sheet);
		if (epc != null && (epc.getLockRows() > 0 || epc.getLockCols() > 0)) {/* 锁定某行列 */
			sheet.createFreezePane(epc.getLockCols(), epc.getLockRows());
		}
		workbook.setSheetName(0, sheetName == null ? "信息" : sheetName);
		return UtilsFile.writeWorkbookFile(filename, workbook);
	}
	/**
	 * 获取单元格各类型值，返回字符串类型<br>
	 * 如果没有单元格，则返回null<br>
	 * 如果有单元格，则返回值，如值为null，则返回def
	 * @param sheet Sheet
	 * @param x int
	 * @param y int
	 * @param isTrim boolean
	 * @param def String
	 * @return String
	 */
	public static String getCellValueByString(Sheet sheet,int x,int y,boolean isTrim, String def) {
		if (sheet == null) return null;
		Row row = sheet.getRow(x);
		if (row == null) return null;
		Cell cell = row.getCell(y);
		if (cell==null) return null;
		return getCellValueByString(cell,isTrim,def);
	}

	/**
	 * 获取单元格各类型值，返回字符串类型<br>
	 * 如果为null，则返回 空串 ""<br>
	 * 默认不过滤Trim
	 * @param cell Cell
	 * @return String
	 */
	public static String getCellValueByString(Cell cell) {
		return getCellValueByString(cell,false, "");
	}

	/**
	 * 获取单元格各类型值，返回字符串类型<br>
	 * 如果为null，则返回 空串 ""
	 * @param cell Cell
	 * @param isTrim boolean
	 * @return String
	 */
	public static String getCellValueByString(Cell cell,boolean isTrim) {
		return getCellValueByString(cell,isTrim, "");
	}

	/**
	 * 获取单元格各类型值，返回字符串类型<br>
	 * 如果为null，则返回 空串 def
	 * @param cell Cell
	 * @param isTrim boolean
	 * @param def String
	 * @return String
	 */
	public static String getCellValueByString(Cell cell,boolean isTrim, String def) {
		if (cell == null || cell.toString().length() == 0) return def;
		if (isTrim && cell.toString().trim().length() == 0) return def;
		CellType cellType = cell.getCellTypeEnum();
		if (cellType == null) return def;
		String value = "";
		switch (cellType) {
		case STRING:
			value = cell.getStringCellValue();
			break;
		case NUMERIC://判断日期类型
			if (HSSFDateUtil.isCellDateFormatted(cell)) {
				value = convertCellToString(cell);
				break;
			}
			double dd = cell.getNumericCellValue();
			value = decimalFormat.format(dd);
			break;
		case BOOLEAN:
			boolean bb = cell.getBooleanCellValue();
			value = String.valueOf(bb);
			break;
		default:
			return def;
		}
		if (value == null) return def;
		return isTrim?value.trim():value;
	}

	/**
	 * 获取单元格各类型值，返回字符串类型<br>
	 * 如果为null，则返回 空串 def
	 * @param cell Cell
	 * @param def double
	 * @return double
	 */
	public static double getCellValueByDouble(Cell cell, double def) {
		if (cell == null || cell.toString().trim().length() == 0) return def;
		CellType cellType = cell.getCellTypeEnum();
		if (cellType == null) return def;
		if (cellType != CellType.NUMERIC) return def;
		if (HSSFDateUtil.isCellDateFormatted(cell)) return def;
		return cell.getNumericCellValue();
	}

	/**
	 * 获取单元格各类型值，返回字符串类型<br>
	 * 如果为null，则返回 空串 def
	 * @param cell Cell
	 * @param def int
	 * @return int
	 */
	public static int getCellValueByInteger(Cell cell, int def) {
		if (cell == null || cell.toString().trim().length() == 0) return def;
		CellType cellType = cell.getCellTypeEnum();
		if (cellType == null) return def;
		if (cellType != CellType.NUMERIC) return def;
		if (HSSFDateUtil.isCellDateFormatted(cell)) return def;
		double d = cell.getNumericCellValue();
		if (d - (int) d < Double.MIN_VALUE) return (int) d;
		return def;
	}
	/**
	 * 设置单元格内容，如果没有单元格，则返回false<br>
	 * 如果isCreate为true，则直接建立单元格，并赋值
	 * @param sheet Sheet
	 * @param isCreate boolean
	 * @param x int
	 * @param y int
	 * @param value String
	 * @return boolean
	 */
	public static boolean setCellValue(Sheet sheet,boolean isCreate,int x,int y,String value) {
		if (sheet == null) return false;
		if(isCreate) {
			Cell cell=createCell(sheet,x,y);
			if (cell==null) return false;
			cell.setCellValue(value);
			return true;
		}
		Row row = sheet.getRow(x);
		if (row == null) return false;
		Cell cell = row.getCell(y);
		if (cell==null) return false;
		if (value.length() >= 32767) value = value.substring(0, 32766);
		cell.setCellValue(value);
		return true;
	}

	
	
	

	/**
	 * 添加新单元。并得到某个单元格
	 * @param x int
	 * @param y int
	 * @return Cell
	 */
	public static final Cell createCell(Sheet sheet,int x, int y) {
		if(sheet==null)return null;
		Row row = sheet.getRow(x);
		if (row == null) sheet.createRow(x);
		row = sheet.getRow(x);
		Cell cell = row.getCell(y);
		if (cell == null) row.createCell(y);
		return row.getCell(y);
	}
	
	
	static final DecimalFormat decimalFormat = new DecimalFormat("###################.###########");

	/**
	 * 分多种格式解析单元格的值
	 * @param cell 单元格
	 * @return 单元格的值
	 */
	public static final String convertCellToString(Cell cell) {
		/* 如果为null会抛出异常，应当返回空字符串 */
		if (cell == null) return "";
		/*
		 * POI对单元格日期处理很弱，没有针对的类型，日期类型取出来的也是一个double值，所以同样作为数值类型
		 * 解决日期2006/11/02格式读入后出错的问题，POI读取后变成“02-十一月-2006”格式
		 */
		if (cell.toString().contains("-") && UtilsVerification.isDate(cell.toString())) {
			String ans = "";
			try {
				ans = new SimpleDateFormat("yyyy/MM/dd").format(cell.getDateCellValue());
			} catch (Exception e) {
				ans = cell.toString();
			}
			return ans;
		}
		cell.setCellType(CellType.STRING);
		return cell.getStringCellValue();
	}

	
}
