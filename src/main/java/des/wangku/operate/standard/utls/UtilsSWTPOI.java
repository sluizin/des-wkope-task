package des.wangku.operate.standard.utls;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.swt.graphics.Point;
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
	static Logger logger = Logger.getLogger(UtilsSWTPOI.class);

	/**
	 * 保存workbook到model目录里的随机数文件，是否关闭Workbook
	 * @param saveFolder String
	 * @param shell Shell
	 * @param workbook Workbook
	 * @param isClose boolean
	 * @param isAlert boolean
	 * @return File
	 */
	public static File save(String saveFolder, Shell shell, Workbook workbook, boolean isClose, boolean isAlert) {
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
		boolean t = UtilsSWTPOI.save(workbook, isClose, file);
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
	public static final boolean save(Workbook workbook, boolean isClose, File file) {
		try {
			if (workbook == null || file == null) return false;
			FileOutputStream fileoutputStream = new FileOutputStream(file);
			workbook.write(fileoutputStream);
			fileoutputStream.close();
			if (isClose) workbook.close();
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
	public static final Sheet getSheet(String filename, String sheetName) {
		try {
			File file = new File(filename);
			if (file == null || !file.isFile()) return null;
			@SuppressWarnings({ "resource" })
			Workbook workbook = new XSSFWorkbook(file);
			return workbook.getSheet(sheetName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 通过文件名与sheetNum得到sheet
	 * @param filename String
	 * @param sheetNum int
	 * @return Sheet
	 */
	public static final Sheet getSheet(String filename, int sheetNum) {
		try {
			File file = new File(filename);
			if (file == null || !file.isFile()) return null;
			@SuppressWarnings({ "resource" })
			Workbook workbook = new XSSFWorkbook(file);
			return workbook.getSheetAt(sheetNum);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 通过文件名与sheetName 的indexof得到sheet
	 * @param filename String
	 * @param sheetName String
	 * @return Sheet
	 */
	public static final Sheet getSheetIndexOf(String filename, String sheetName) {
		try {
			File file = new File(filename);
			if (file == null || !file.isFile()) return null;
			@SuppressWarnings("resource")
			Workbook workbook = new XSSFWorkbook(file);
			for (int i = 0, len = workbook.getNumberOfSheets(); i < len; i++) {
				Sheet sheet = workbook.getSheetAt(i);
				if (sheet.getSheetName().indexOf(sheetName) > -1) return sheet;
			}
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
	 * 获取单元格<br>
	 * 如果没有单元格，则返回null<br>
	 * @param sheet Sheet
	 * @param x int
	 * @param y int
	 * @return Cell
	 */
	public static Cell getCell(Sheet sheet, int x, int y) {
		if (sheet == null) return null;
		Row row = sheet.getRow(x);
		if (row == null) return null;
		return row.getCell(y);
	}

	/**
	 * 得到数值型
	 * @param sheet Sheet
	 * @param x int
	 * @param y int
	 * @param def int
	 * @return int
	 */
	public static int getCellValueInteger(Sheet sheet, int x, int y, int def) {
		Cell cell = getCell(sheet, x, y);
		if (cell == null) return def;
		String value = UtilsSWTPOI.getCellValueByString(cell, true, "" + def);
		return Integer.parseInt(value);
	}

	/**
	 * 得到excel运行函数结果
	 * @param sheet Sheet
	 * @param x int
	 * @param y int
	 * @param def String
	 * @return String
	 */
	public static String getCellValueFormula(Sheet sheet, int x, int y, String def) {
		Cell cell = getCell(sheet, x, y);
		if (cell == null) return def;
		String value = getCellValueFormula(cell);
		if (value == null) return def;
		return value;
	}

	/**
	 * 得到excel运行函数结果
	 * @param sheet Sheet
	 * @param x int
	 * @param y int
	 * @param def int
	 * @return int
	 */
	public static int getCellValueFormula(Sheet sheet, int x, int y, int def) {
		String value = getCellValueFormula(sheet, x, y, def + "");
		value = UtilsString.getLeftPoint(value);
		return Integer.parseInt(value);
	}

	/**
	 * 得到excel运行函数结果
	 * @param cell Cell
	 * @return String
	 */
	@SuppressWarnings("deprecation")
	public static String getCellValueFormula(Cell cell) {
		if (cell == null) return null;
		switch (cell.getCellType()) {
		case HSSFCell.CELL_TYPE_FORMULA:
			try {
				return String.valueOf(cell.getNumericCellValue());
			} catch (IllegalStateException e) {
				return String.valueOf(cell.getRichStringCellValue());
			}
		case HSSFCell.CELL_TYPE_NUMERIC:
			return String.valueOf(cell.getNumericCellValue());
		case HSSFCell.CELL_TYPE_STRING:
			return String.valueOf(cell.getRichStringCellValue());
		default:
			return null;
		}
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
	public static String getCellValueByString(Sheet sheet, int x, int y, boolean isTrim, String def) {
		Cell cell = getCell(sheet, x, y);
		if (cell == null) return null;
		return getCellValueByString(cell, isTrim, def);
	}

	/**
	 * 获取单元格各类型值，返回字符串类型<br>
	 * 如果为null，则返回 空串 ""<br>
	 * 默认不过滤Trim
	 * @param cell Cell
	 * @return String
	 */
	public static String getCellValueByString(Cell cell) {
		return getCellValueByString(cell, false, "");
	}

	/**
	 * 获取单元格各类型值，返回字符串类型<br>
	 * 如果为null，则返回 空串 ""
	 * @param cell Cell
	 * @param isTrim boolean
	 * @return String
	 */
	public static String getCellValueByString(Cell cell, boolean isTrim) {
		return getCellValueByString(cell, isTrim, "");
	}

	/**
	 * 获取单元格各类型值，返回字符串类型<br>
	 * 如果为null，则返回 空串 def
	 * @param cell Cell
	 * @param isTrim boolean
	 * @param def String
	 * @return String
	 */
	public static String getCellValueByString(Cell cell, boolean isTrim, String def) {
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
		return isTrim ? value.trim() : value;
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
	public static boolean setCellValue(Sheet sheet, boolean isCreate, int x, int y, String value) {
		if (sheet == null) return false;
		if (isCreate) {
			Cell cell = createCell(sheet, x, y);
			if (cell == null) return false;
			cell.setCellValue(value);
			return true;
		}
		Row row = sheet.getRow(x);
		if (row == null) return false;
		Cell cell = row.getCell(y);
		if (cell == null) return false;
		if (value.length() >= 32767) value = value.substring(0, 32766);
		cell.setCellValue(value);
		return true;
	}

	/**
	 * 添加新单元。并得到某个单元格
	 * @param sheet Sheet
	 * @param x int
	 * @param y int
	 * @return Cell
	 */
	public static final Cell createCell(Sheet sheet, int x, int y) {
		if (sheet == null) return null;
		Row row = sheet.getRow(x);
		if (row == null) sheet.createRow(x);
		row = sheet.getRow(x);
		Cell cell = row.getCell(y);
		if (cell == null) row.createCell(y);
		return row.getCell(y);
	}

	/**
	 * 删除某个单元格，并把同列，下面的行逐行上移，其它列不动
	 * @param sheet Sheet
	 * @param x int
	 * @param y int
	 * @param endindex int
	 */
	public static final void removeCellUP(Sheet sheet, int x, int y, int endindex) {
		if (sheet == null) return;
		int rowsLen = sheet.getLastRowNum();
		if (endindex > -1) rowsLen = endindex;
		for (int i = x; i <= rowsLen; i++) {
			if (i == rowsLen) {
				setCellValue(sheet, true, i, y, "");
				break;
			}
			String value = getCellValueByString(sheet, i + 1, y, false, "");
			if (value == null) value = "";
			setCellValue(sheet, true, i, y, value);
		}
	}

	/**
	 * 删除某个单元格，并把同列，上面的行逐行下移，其它列不动
	 * @param sheet Sheet
	 * @param x int
	 * @param y int
	 * @param endindex int
	 */
	public static final void removeCellDOWN(Sheet sheet, int x, int y, int endindex) {
		if (sheet == null) return;
		if (!isExistValue(sheet, x, y)) return;
		int top = sheet.getFirstRowNum();
		if (endindex > -1) top = endindex;
		for (int i = x; i >= top; i--) {
			if (i == top) {
				setCellValue(sheet, true, i, y, "");
				break;
			}
			String value = getCellValueByString(sheet, i - 1, y, false, "");
			if (value == null) value = "";
			setCellValue(sheet, true, i, y, value);
		}
	}

	/**
	 * 判断[x,y]是否存在在表中,允许Null
	 * @param sheet Sheet
	 * @param x int
	 * @param y int
	 * @return boolean
	 */
	public static final boolean isExistValue(Sheet sheet, int x, int y) {
		if (sheet == null) return false;
		if (x < sheet.getFirstRowNum() || x > sheet.getLastRowNum()) return false;
		Row row = sheet.getRow(x);
		if (row == null || y < row.getFirstCellNum() || y > row.getLastCellNum()) return false;
		return true;
	}

	/**
	 * 给sheet中null的cell填充value，是否填充所有[以整表为标准]<br>
	 * @param sheet Sheet
	 * @param value Object
	 * @param isFill boolean
	 */
	public static final void fill(Sheet sheet, Object value, boolean isFill) {
		if (sheet == null) return;
		int cellmax = getMaxCellNum(sheet);
		for (int i = 0, len = sheet.getLastRowNum(); i <= len; i++) {
			Row row = sheet.getRow(i);
			int size = 0;
			if (row == null && isFill) row = sheet.createRow(i);
			if (isFill) size = cellmax;
			else size = row == null ? 0 : row.getLastCellNum();
			for (int ii = 0; ii < size; ii++) {
				Cell cell = row.getCell(ii);
				if (cell != null) continue;
				Cell c = row.createCell(ii);
				//c.setCellValue(value);
				setCellValue(c, value);
			}
		}
	}

	/**
	 * 设置cell值，只支持基础类型与String/Date/Calendar/RichTextString
	 * @param cell Cell
	 * @param obj Object
	 * @return boolean
	 */
	public static final boolean setCellValue(Cell cell, Object obj) {
		if (cell == null || obj == null) return false;
		if (obj instanceof Boolean) {
			cell.setCellValue((Boolean) obj);
			return true;
		}
		if (obj instanceof Integer) {
			int p = (Integer) obj;
			cell.setCellValue(Double.valueOf(p));
			return true;
		}
		if (obj instanceof Short) {
			short p = (Short) obj;
			cell.setCellValue(Double.valueOf(p));
			return true;
		}
		if (obj instanceof Float) {
			float p = (Integer) obj;
			cell.setCellValue(Double.valueOf(p));
			return true;
		}
		if (obj instanceof Long) {
			long p = (Long) obj;
			cell.setCellValue(Double.valueOf(p));
			return true;
		}
		if (obj instanceof Double) {
			cell.setCellValue((Double) obj);
			return true;
		}
		if (obj instanceof Date) {
			cell.setCellValue((Date) obj);
			return true;
		}
		if (obj instanceof Calendar) {
			cell.setCellValue((Calendar) obj);
			return true;
		}
		if (obj instanceof RichTextString) {
			cell.setCellValue((RichTextString) obj);
			return true;
		}
		if (obj instanceof String) {
			cell.setCellValue((String) obj);
			return true;
		}
		return false;
	}

	/**
	 * 得到表格的最大宽度
	 * @param sheet Sheet
	 * @return int
	 */
	public static final int getMaxCellNum(Sheet sheet) {
		if (sheet == null) return -1;
		int max = -1;
		for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {
			Row row = sheet.getRow(i);
			if (row == null) continue;
			if (row.getLastCellNum() > max) max = row.getLastCellNum();
		}
		return max;
	}

	/**
	 * 把单元移到指定单元。如移动的单元为空，则不移动
	 * @param sheet Sheet
	 * @param a Point
	 * @param b Point
	 * @return boolean
	 */
	public static final boolean move(Sheet sheet, Point a, Point b) {
		if (sheet == null || a == null || b == null) return false;
		return move(sheet, a.x, a.y, b.x, b.y);
	}

	/**
	 * 把单元移到指定单元。如移动的单元为空，则不移动
	 * @param sheet Sheet
	 * @param ax int
	 * @param ay int
	 * @param bx int
	 * @param by int
	 * @return boolean
	 */
	public static final boolean move(Sheet sheet, int ax, int ay, int bx, int by) {
		if (sheet == null) return false;
		Cell fromCell = getCell(sheet, ax, ay);
		if (fromCell == null) return false;
		Row row = sheet.getRow(bx);
		if (row == null) sheet.createRow(bx);
		row = sheet.getRow(bx);
		Cell tocell = getCell(sheet, bx, by);
		if (tocell != null) row.removeCell(tocell);
		row.createCell(by);
		Cell toCell = getCell(sheet, bx, by);
		getCellCopy(fromCell, toCell);
		fromCell.getRow().removeCell(fromCell);
		return true;
	}

	/**
	 * 移动单元，是否允许null覆盖
	 * @param sheet Sheet
	 * @param isNullOver boolean
	 * @param ax int
	 * @param ay int
	 * @param bx int
	 * @param by int
	 * @return boolean
	 */
	public static final boolean move(Sheet sheet, boolean isNullOver, int ax, int ay, int bx, int by) {
		if (sheet == null) return false;
		Cell fromCell = getCell(sheet, ax, ay);
		if (fromCell != null) {
			Cell toCell = createCell(sheet, bx, by);
			getCellCopy(fromCell, toCell);
			delete(sheet, fromCell);
			return true;
		}
		if (!isNullOver) return true;
		Cell toCell = getCell(sheet, bx, by);
		if (toCell == null) return true;
		return delete(sheet, toCell);
	}

	/**
	 * 删除单元
	 * @param sheet Sheet
	 * @param cell Cell
	 * @return boolean
	 */
	public static final boolean delete(Sheet sheet, Cell cell) {
		if (sheet == null) return false;
		if (cell == null) return true;
		Row row = cell.getRow();
		row.removeCell(cell);
		return true;
	}

	/**
	 * 删除单元
	 * @param sheet Sheet
	 * @param x int
	 * @param y int
	 * @return boolean
	 */
	public static final boolean delete(Sheet sheet, int x, int y) {
		if (sheet == null) return false;
		Cell cell = getCell(sheet, x, y);
		return delete(sheet, cell);
	}

	/**
	 * 移动区块 a,b两点为块的左上角与右下角，to为要移动的左上角
	 * @param sheet Sheet
	 * @param isNullOver boolean
	 * @param ax int
	 * @param ay int
	 * @param bx int
	 * @param by int
	 * @param tox int
	 * @param toy int
	 * @return boolean
	 */
	public static final boolean move(Sheet sheet, boolean isNullOver, int ax, int ay, int bx, int by, int tox, int toy) {
		if (sheet == null) return false;
		if (!isSuffixValid(ax, ay, bx, by, tox, toy)) return false;
		for (int x = ax; x <= bx; x++) {
			for (int y = ay; y <= by; y++) {
				int nx = tox + (x - ax);
				int ny = toy + (y - ay);
				//logger.debug("[" + x + "," + y + "]->[" + nx + "," + ny + "]");
				move(sheet, isNullOver, x, y, nx, ny);
			}
		}
		return true;
	}

	/**
	 * 复制单元格
	 * @param fromCell Cell
	 * @param toCell Cell
	 * @return Cell
	 */
	@SuppressWarnings("deprecation")
	public static final Cell getCellCopy(Cell fromCell, Cell toCell) {
		if (fromCell == null || toCell == null) return null;
		short newheight = fromCell.getRow().getHeight();
		short oldheight = toCell.getRow().getHeight();
		if (oldheight > newheight) newheight = oldheight;
		toCell.getRow().setHeight(newheight);
		String obj = getCellValueByString(fromCell);
		toCell.setCellComment(fromCell.getCellComment());
		toCell.setCellStyle(fromCell.getCellStyle());
		toCell.setCellType(fromCell.getCellType());
		toCell.setCellType(fromCell.getCellTypeEnum());
		//logger.debug("obj：" + obj.toString());
		copyRange(fromCell, toCell);
		setCellValue(toCell, obj);
		return toCell;
	}

	/**
	 * 拷贝合并单元格信息，如果目标单元有单元合并，则返回
	 * @param fromCell Cell
	 * @param toCell Cell
	 */
	public static final void copyRange(Cell fromCell, Cell toCell) {
		CellRangeAddress range = getMergedRegion(fromCell);
		if (range == null) return;
		CellRangeAddress rangenew = getMergedRegion(toCell);
		if (rangenew != null) return;
		int xsize = range.getLastRow() - fromCell.getRowIndex();
		int ysize = range.getLastColumn() - fromCell.getColumnIndex();
		int nx1 = toCell.getRowIndex();
		int nx2 = nx1 + xsize;
		int ny1 = toCell.getColumnIndex();
		int ny2 = ny1 + ysize;
		CellRangeAddress region1 = new CellRangeAddress(nx1, nx2, ny1, ny2);
		toCell.getSheet().addMergedRegion(region1);
	}

	/**
	 * 删除区块内所有单元
	 * @param sheet Sheet
	 * @param ax int
	 * @param ay int
	 * @param bx int
	 * @param by int
	 * @return boolean
	 */
	public static final boolean remove(Sheet sheet, int ax, int ay, int bx, int by) {
		if (sheet == null) return false;
		for (int i = ax; i <= bx; i++) {
			Row row = sheet.getRow(i);
			if (row == null) continue;
			for (int ii = ay; ii <= by; ii++) {
				Cell cell = row.getCell(ii);
				if (cell == null) continue;
				row.removeCell(cell);
			}
		}
		return false;
	}

	/**
	 * 得到表格某列深度 是否过滤空格
	 * @param sheet Sheet
	 * @param y int
	 * @param isTrim boolean
	 * @return int
	 */
	public static final int getDepth(Sheet sheet, int y, boolean isTrim) {
		if (sheet == null) return 0;
		for (int i = sheet.getLastRowNum(); i > -1; i--) {
			if (sheet.getRow(i) == null) continue;
			if (sheet.getRow(i).getCell(y) == null) continue;
			String value = getCellValueByString(sheet, i, y, isTrim, "");
			if (value == null || value.length() == 0) continue;
			if (isTrim && value.trim().length() == 0) continue;
			return i;
		}
		return 0;
	}

	/**
	 * 得到表格某行宽度
	 * @param sheet Sheet
	 * @param x int
	 * @return int
	 */
	public static final int getWidth(Sheet sheet, int x) {
		if (sheet == null || sheet.getRow(x) == null) return 0;
		return sheet.getRow(x).getLastCellNum();
	}

	/**
	 * 得到表格中各行中最大宽度
	 * @param sheet Sheet
	 * @return int
	 */
	public static final int getWidthMax(Sheet sheet) {
		if (sheet == null) return 0;
		int width = 0;
		for (int i = 0; i <= sheet.getLastRowNum(); i++) {
			Row row = sheet.getRow(i);
			if (row == null) continue;
			if (row.getLastCellNum() > width) width = row.getLastCellNum();
		}
		return width;
	}

	/**
	 * 按批注内容查单元格
	 * @param sheet Sheet
	 * @param content String
	 * @param index int
	 * @return Cell
	 */
	public static final Cell getCellCommentByContent(Sheet sheet, String content, int index) {
		Cell[] arr = getCellCommentByContent(sheet, content);
		if (index < 0 || index > index) return null;
		return arr[index];
	}

	/**
	 * 按批注内容查单元格
	 * @param sheet Sheet
	 * @param content String
	 * @return Cell[]
	 */
	public static final Cell[] getCellCommentByContent(Sheet sheet, String content) {
		Cell[] arr = {};
		if (sheet == null || content == null) return arr;
		List<Cell> list = new ArrayList<>();
		for (int i = 0; i <= sheet.getLastRowNum(); i++) {
			Row row = sheet.getRow(i);
			if (row == null) continue;
			for (int ii = 0; ii <= row.getLastCellNum(); ii++) {
				Cell cell = row.getCell(ii);
				if (cell == null) continue;
				Comment comment = cell.getCellComment();
				if (comment == null) continue;
				if (content.equalsIgnoreCase(comment.getString().getString())) list.add(cell);
			}
		}
		if (list.size() > 0) return list.toArray(arr);
		return arr;
	}

	/**
	 * 按批注作者查单元格
	 * @param sheet Sheet
	 * @param author String
	 * @param index int
	 * @return Cell
	 */
	public static final Cell getCellCommentByAuthor(Sheet sheet, String author, int index) {
		Cell[] arr = getCellCommentByAuthor(sheet, author);
		if (index < 0 || index > index) return null;
		return arr[index];
	}

	/**
	 * 按批注作者查单元格，忽略大小写
	 * @param sheet Sheet
	 * @param author String
	 * @return Cell[]
	 */
	public static final Cell[] getCellCommentByAuthor(Sheet sheet, String author) {
		Cell[] arr = {};
		if (sheet == null || author == null) return arr;
		List<Cell> list = new ArrayList<>();
		for (int i = 0; i <= sheet.getLastRowNum(); i++) {
			Row row = sheet.getRow(i);
			if (row == null) continue;
			for (int ii = row.getFirstCellNum(); ii <= row.getLastCellNum(); ii++) {
				Cell cell = row.getCell(ii);
				if (cell == null) continue;
				Comment comment = cell.getCellComment();
				if (comment == null) continue;
				if (author.equalsIgnoreCase(comment.getAuthor())) list.add(cell);
			}
		}
		if (list.size() > 0) return list.toArray(arr);
		return arr;
	}

	/**
	 * 得到表格最大/最小层数
	 * @param sheet Sheet
	 * @param isMax boolean
	 * @param isTrim boolean
	 * @param filterColumns int[]
	 * @return int
	 */
	public static final int getDepth(Sheet sheet, boolean isMax, boolean isTrim, int... filterColumns) {
		int len = getWidthMax(sheet);
		int extremum = 0;
		for (int i = 0; i <= len; i++) {
			if (UtilsString.isExist(i, filterColumns)) continue;
			int deep = getDepth(sheet, i, isTrim);
			if (isMax) {/* 最大深度 */
				if (deep > extremum) extremum = deep;
			} else {/* 最小深度 */
				if (extremum == 0 || deep < extremum) extremum = deep;
			}
		}
		return extremum;
	}

	/**
	 * 在sheet里，按照key查行号
	 * @param sheet Sheet
	 * @param key String
	 * @param start int
	 * @param y int
	 * @return int
	 */
	public static final int getSearchRowsKeyEquals(Sheet sheet, String key, int start, int y) {
		if (sheet == null) return -1;
		int rowsLen = sheet.getLastRowNum();
		for (int i = start; i <= rowsLen; i++) {
			String value = getCellValueByString(sheet, i, y, false, "");
			if (value == null) continue;
			if (value.equals(key)) return i;
		}
		return -1;
	}

	/**
	 * 判断下标有效，不区别行与列，判断大于0
	 * @param arrs int[]
	 * @return boolean
	 */
	public static final boolean isSuffixValid(int... arrs) {
		for (int i : arrs)
			if (i < 0) return false;
		return true;
	}

	private static final DecimalFormat decimalFormat = new DecimalFormat("###################.###########");

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
			try {
				return new SimpleDateFormat("yyyy/MM/dd").format(cell.getDateCellValue());
			} catch (Exception e) {
				return cell.toString();
			}
		}
		cell.setCellType(CellType.STRING);
		return cell.getStringCellValue();
	}

	/**
	 * 判断单元格是否在合并单元格中，-1为不在，0为第一个元素，1为合并格中非左上角的单元格
	 * @param cell Cell
	 * @return int
	 */
	public static final int isMergedRegion(Cell cell) {
		if (cell == null) return -1;
		Sheet sheet = cell.getSheet();
		return isMergedRegion(sheet, cell.getRowIndex(), cell.getColumnIndex());
	}

	/**
	 * 判断单元格是否在合并单元格中，-1为不在，0为第一个元素，1为合并格中非左上角的单元格
	 * @param sheet Sheet
	 * @param x int
	 * @param y int
	 * @return int
	 */
	public static final int isMergedRegion(Sheet sheet, int x, int y) {
		int sheetMergeCount = sheet.getNumMergedRegions();
		for (int i = 0; i < sheetMergeCount; i++) {
			CellRangeAddress range = sheet.getMergedRegion(i);
			int y1 = range.getFirstColumn();
			int y2 = range.getLastColumn();
			int x1 = range.getFirstRow();
			int x2 = range.getLastRow();
			if (x >= x1 && x <= x2 && y >= y1 && y <= y2) {
				if (x == x1 && y == y1) return 0;
				else return 1;
			}
		}
		return -1;
	}

	/**
	 * 得到某个单元格所在的合并单元格
	 * @param cell Cell
	 * @return CellRangeAddress
	 */
	public static final CellRangeAddress getMergedRegion(Cell cell) {
		return getMergedRegion(cell.getSheet(), cell.getRowIndex(), cell.getColumnIndex());
	}

	/**
	 * 得到某个单元格所在的合并单元格
	 * @param sheet Sheet
	 * @param x int
	 * @param y int
	 * @return CellRangeAddress
	 */
	public static final CellRangeAddress getMergedRegion(Sheet sheet, int x, int y) {
		int sheetMergeCount = sheet.getNumMergedRegions();
		for (int i = 0; i < sheetMergeCount; i++) {
			CellRangeAddress range = sheet.getMergedRegion(i);
			int y1 = range.getFirstColumn();
			int y2 = range.getLastColumn();
			int x1 = range.getFirstRow();
			int x2 = range.getLastRow();
			if (x >= x1 && x <= x2 && y >= y1 && y <= y2) return range;
		}
		return null;
	}

}
