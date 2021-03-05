package des.wangku.operate.standard.utls;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 针对POI的基本操作<br>
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
@SuppressWarnings({ "resource", "deprecation" })
public final class UtilsPOI {
	/** 格式化字符串 */
	static final DecimalFormat decimalFormat = new DecimalFormat("###################.###########");

	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(UtilsPOI.class);

	/**
	 * 添加合并单元格
	 * @param sheet Sheet
	 * @param regions CellRangeAddress[]
	 */
	public static final void addCellRangeAddress(Sheet sheet, CellRangeAddress... regions) {
		if (sheet == null || regions == null || regions.length == 0) return;
		CellStyle style = sheet.getWorkbook().createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER);// 居中
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		for (CellRangeAddress e : regions) {
			Cell cell = getCell(sheet, e.getFirstRow(), e.getFirstColumn());
			if (cell != null) cell.setCellStyle(style);/* 合并单元格后居中显示 */
			sheet.addMergedRegion(e);
		}
	}

	/**
	 * 向sheet中添加数据
	 * @param sheet Sheet
	 * @param list List&lt;List&lt;String&gt;&gt;
	 * @return List&lt;Row&gt;
	 */
	public static final List<Row> addSheet(Sheet sheet, List<List<String>> list) {
		List<Row> rowList = new ArrayList<>();
		if (sheet == null || list.size() == 0) return rowList;
		for (List<String> l : list) {
			Row row = addSheetRow(sheet, l);
			if (row != null) rowList.add(row);
		}
		return rowList;
	}

	/**
	 * 添加Sheet<br>
	 * 如果发现有此名称的sheet，则添加数据<br>
	 * 否则添加新的sheet<br>
	 * 返回sheet名称<br>
	 * @param wb Workbook
	 * @param sheetName String
	 * @param list List&lt;List&lt;String&gt;&gt;
	 * @return Sheet
	 */
	public static final Sheet addSheet(Workbook wb, String sheetName, List<List<String>> list) {
		return addSheet(wb, sheetName, list, null);
	}

	/**
	 * 添加Sheet<br>
	 * 如果发现有此名称的sheet，则添加数据<br>
	 * 否则添加新的sheet<br>
	 * 返回sheet名称<br>
	 * @param wb Workbook
	 * @param sheetName String
	 * @param list List&lt;List&lt;String&gt;&gt;
	 * @param regions CellRangeAddress[]
	 * @return Sheet
	 */
	public static final Sheet addSheet(Workbook wb, String sheetName, List<List<String>> list, CellRangeAddress[] regions) {
		if (wb == null || sheetName == null || sheetName.length() == 0) return null;
		String newSheetName = getSheetNameFormat(sheetName);
		Sheet sheet = wb.getSheet(newSheetName);
		if (sheet != null) {
			addSheet(sheet, list);
			addCellRangeAddress(sheet, regions);
			return sheet;
		}
		Sheet newsheet = wb.createSheet(newSheetName);
		addSheet(newsheet, list);
		addCellRangeAddress(newsheet, regions);
		return newsheet;
	}

	/**
	 * 添加数组为一行
	 * @param sheet Sheet
	 * @param list List&lt;String&gt;
	 * @return Row
	 */
	public static final Row addSheetRow(Sheet sheet, List<String> list) {
		String[] arr = {};
		return addSheetRow(sheet, list.toArray(arr));
	}

	/**
	 * 添加数组为一行
	 * @param sheet Sheet
	 * @param arrs String[]
	 * @return Row
	 */
	public static final Row addSheetRow(Sheet sheet, String... arrs) {
		if (arrs == null || arrs.length == 0) return null;
		int rows = sheet.getPhysicalNumberOfRows();
		Row row = sheet.createRow(rows);
		for (int i = 0; i < arrs.length; i++) {
			String value = arrs[i];
			Cell cell = row.createCell(i);
			cell.setCellValue(UtilsPOI.arrangementString(value));
		}
		return row;
	}

	/**
	 * 整理要输入的字符串<br>
	 * 如果字符串为null，则返回""<br>
	 * 如果字符串长度超出范围，则截取字符串
	 * @param value String
	 * @return String
	 */
	public static final String arrangementString(String value) {
		if (value == null) return "";
		if (value.length() >= 32767) return value.substring(0, 32766);
		return value;
	}

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
	 * 复制单元格
	 * @param srcCell
	 * @param distCell
	 * @param copyVal true则连同cell的内容一起复制
	 */
	public static void copyCell(Cell srcCell, Cell distCell, boolean copyVal) {
		CellStyle cs = srcCell.getCellStyle();
		CellStyle to = distCell.getCellStyle();
		copyCellStyle(cs, to);
		//样式
		distCell.setCellStyle(to);
		if (!copyVal) return;
		String val = getCellVal(srcCell);
		distCell.setCellValue(val);
	}

	/**
	 * 复制一个单元格样式到目的单元格样式
	 * @param fromStyle CellStyle
	 * @param toStyle CellStyle
	 */
	public static void copyCellStyle(CellStyle fromStyle, CellStyle toStyle) {
		//边框和边框颜色
		toStyle.setTopBorderColor(fromStyle.getTopBorderColor());
		toStyle.setBottomBorderColor(fromStyle.getBottomBorderColor());
		toStyle.setRightBorderColor(fromStyle.getRightBorderColor());
		toStyle.setLeftBorderColor(fromStyle.getLeftBorderColor());
		//背景和前景
		toStyle.setFillBackgroundColor(fromStyle.getFillBackgroundColor());
		toStyle.setFillForegroundColor(fromStyle.getFillForegroundColor());
		toStyle.setDataFormat(fromStyle.getDataFormat());
		//toStyle.setFont(fromStyle.getFont(null));
		toStyle.setHidden(fromStyle.getHidden());
		toStyle.setIndention(fromStyle.getIndention());//首行缩进
		toStyle.setLocked(fromStyle.getLocked());
		toStyle.setRotation(fromStyle.getRotation());//旋转
		toStyle.setWrapText(fromStyle.getWrapText());
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
	 * 复制某行
	 * @param fromRow Row
	 * @param toRow Row
	 * @param copyVal false
	 */
	public static final void copyRow(Row fromRow, Row toRow, boolean copyVal) {
		if (fromRow == null || toRow == null) return;
		for (Iterator<Cell> cellIt = fromRow.cellIterator(); cellIt.hasNext();) {
			Cell tmpCell = cellIt.next();
			Cell newCell = toRow.createCell(tmpCell.getColumnIndex());
			copyCell(tmpCell, newCell, copyVal);
		}
	}

	/**
	 * Sheet复制
	 * @param fromSheet Sheet
	 * @param toSheet Sheet
	 * @param copyVal boolean
	 */
	public static final void copySheet(Sheet fromSheet, Sheet toSheet, boolean copyVal) {
		//合并区域处理
		mergerRegion(fromSheet, toSheet);
		for (Iterator<Row> rowIt = fromSheet.rowIterator(); rowIt.hasNext();) {
			Row tmpRow = rowIt.next();
			Row newRow = toSheet.createRow(tmpRow.getRowNum());
			copyRow(tmpRow, newRow, copyVal);//行复制
		}
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
	 * 删除单元
	 * @param sheet Sheet
	 * @param cell Cell
	 * @return boolean
	 */
	public static final boolean delete(final Sheet sheet, Cell cell) {
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
	public static final boolean delete(final Sheet sheet, int x, int y) {
		if (sheet == null) return false;
		Cell cell = getCell(sheet, x, y);
		if (cell == null) return false;
		return delete(sheet, cell);
	}

	/**
	 * 删除单元
	 * @param sheet Sheet
	 * @param p Point
	 * @return boolean
	 */
	public static final boolean delete(final Sheet sheet, Point p) {
		return delete(sheet, p.x, p.y);
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
	 * 获取单元格<br>
	 * 如果没有单元格，则返回null<br>
	 * @param sheet Sheet
	 * @param x int
	 * @param y int
	 * @return Cell
	 */
	public static final Cell getCell(Sheet sheet, int x, int y) {
		if (sheet == null) return null;
		Row row = sheet.getRow(x);
		if (row == null) return null;
		return row.getCell(y);
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
	 * 复制单元格<br>
	 * 不进行运算
	 * @param fromCell Cell
	 * @param toCell Cell
	 * @return Cell
	 */
	public static final Cell getCellCopy(Cell fromCell, Cell toCell) {
		if (fromCell == null || toCell == null) return null;
		short newheight = fromCell.getRow().getHeight();
		short oldheight = toCell.getRow().getHeight();
		if (oldheight > newheight) newheight = oldheight;
		toCell.getRow().setHeight(newheight);
		String obj = getCellVal(fromCell);
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
	 * 得到单元格里对象
	 * @param cell Cell
	 * @return String
	 */
	public static Object getCellObject(Cell cell) {
		if (cell == null) return null;
		if (cell.toString().length() == 0) return "";
		switch (cell.getCellTypeEnum()) {
		case NUMERIC:
			if (HSSFDateUtil.isCellDateFormatted(cell)) { return convertCellToString(cell); }
			//double dd = cell.getNumericCellValue();
			//return decimalFormat.format(dd);
			return cell.getNumericCellValue();
		case STRING:
			return cell.getStringCellValue();
		case FORMULA:
			try {
				return cell.getNumericCellValue();
			} catch (Exception e) {
				try {
					return cell.getRichStringCellValue();
				} catch (Exception f) {
					return null;
				}
			}
		case BLANK:
			return null;
		case BOOLEAN:
			return cell.getBooleanCellValue();
		case ERROR:
			return cell.getRichStringCellValue();
		default:
			return null;
		}
	}

	/**
	 * 得到通用表格样式
	 * @param wb Workbook
	 * @return CellStyle
	 */
	public static final CellStyle getCellStyleBase(Workbook wb) {
		if (wb == null) return null;
		CellStyle cellStyle = wb.createCellStyle();/* 设置这些样式 */
		cellStyle.setAlignment(HorizontalAlignment.LEFT);
		cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		return cellStyle;
	}

	/**
	 * 修改CellStyle 有四个方面的边框和居中与上下居中设置
	 * @param cell Cell
	 * @return CellStyle
	 */
	public static final CellStyle getCellStyleStandard(Cell cell) {
		if (cell == null) return null;
		return getCellStyleStandard(cell.getSheet());
	}

	/**
	 * 修改CellStyle 有四个方面的边框和居中与上下居中设置
	 * @param style CellStyle
	 * @return CellStyle
	 */
	public static final CellStyle getCellStyleStandard(CellStyle style) {
		if (style == null) return null;
		style.setBorderBottom(BorderStyle.THIN); //下边框    
		style.setBorderLeft(BorderStyle.THIN);//左边框    
		style.setBorderTop(BorderStyle.THIN);//上边框    
		style.setBorderRight(BorderStyle.THIN);//右边框   
		style.setAlignment(HorizontalAlignment.CENTER);// 居中
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		return style;
	}

	/**
	 * 修改CellStyle 有四个方面的边框和居中与上下居中设置
	 * @param sheet Sheet
	 * @return CellStyle
	 */
	public static final CellStyle getCellStyleStandard(Sheet sheet) {
		if (sheet == null) return null;
		return getCellStyleStandard(sheet.getWorkbook());
	}

	/**
	 * 修改CellStyle 有四个方面的边框和居中与上下居中设置
	 * @param wb Workbook
	 * @return CellStyle
	 */
	public static final CellStyle getCellStyleStandard(Workbook wb) {
		if (wb == null) return null;
		return getCellStyleStandard(wb.createCellStyle());
	}

	/**
	 * 得到单元格里的字符串<br>
	 * @param cell Cell
	 * @return String
	 */
	public static final String getCellVal(Cell cell) {
		return getCell(cell, true);
	}

	/**
	 * 得到单元格里的字符串<br>
	 * @param cell Cell
	 * @return String
	 */
	public static final String getCellValFORMULA(Cell cell) {
		return getCell(cell, true);
	}
	public static final String getFORMULAVal(Cell cell) {
		CellType ct=cell.getCellTypeEnum();
		//System.out.println("error:"+ct);
		if (ct != CellType.FORMULA) return null;
		//System.out.println("error--ok:"+ct);
		try {
			return String.valueOf(cell.getNumericCellValue());
		} catch (IllegalStateException e) {
			//System.out.println("error--ok:"+ct);
			return null;
		}
		
	}
	/**
	 * 得到单元格里的字符串<br>
	 * 是否进行运算
	 * @param cell Cell
	 * @param isFORMULA boolean
	 * @return String
	 */
	public static String getCell(Cell cell, boolean isFORMULA) {
		if (cell == null) return null;
		if (cell.toString().length() == 0) return "";

		if (isFORMULA) {
			String val=getFORMULAVal(cell);
			if(val!=null)return val;
		}
		Object obj = getCellObject(cell);
		if (obj == null) return null;
		return String.valueOf(obj);
		/*
		 * switch(cell.getCellTypeEnum()) {
		 * case NUMERIC:
		 * if (HSSFDateUtil.isCellDateFormatted(cell)) {
		 * return convertCellToString(cell);
		 * }
		 * //double dd = cell.getNumericCellValue();
		 * //return decimalFormat.format(dd);
		 * return String.valueOf(cell.getNumericCellValue());
		 * case STRING:
		 * return String.valueOf(cell.getRichStringCellValue());
		 * case FORMULA:
		 * if(!isFORMULA) String.valueOf(cell.getRichStringCellValue());
		 * try {
		 * return String.valueOf(cell.getNumericCellValue());
		 * } catch (IllegalStateException e) {
		 * return String.valueOf(cell.getRichStringCellValue());
		 * }
		 * case BLANK:
		 * return null;
		 * case BOOLEAN:
		 * boolean bb = cell.getBooleanCellValue();
		 * return String.valueOf(bb);
		 * default:
		 * return null;
		 * }
		 */
	}

	/**
	 * 得到单元格里的字符串
	 * @param cell Cell
	 * @param def String
	 * @return String
	 */
	public static String getCellVal(Cell cell, String def) {
		if (cell == null || cell.toString().length() == 0) return def;
		String val = getCellVal(cell);
		if (val == null) return def;
		return val;
	}

	/**
	 * @param sheet Sheet
	 * @param x int
	 * @param y int
	 * @return String
	 */
	public static String getCellVal(Sheet sheet, int x, int y) {
		return getCellVal(sheet, x, y, "");
	}

	/**
	 * 从表中得到数值，如果为空或null，则返回def
	 * @param sheet Sheet
	 * @param x int
	 * @param y int
	 * @param def String
	 * @return String
	 */
	public static String getCellVal(Sheet sheet, int x, int y, String def) {
		Cell cell = getCell(sheet, x, y);
		if (cell == null) return null;
		return getCellVal(cell, def);
	}

	/**
	 * 得到单元格里的数值<br>
	 * 如果为null或空或其它错误，则返回0
	 * @param cell Cell
	 * @return int
	 */
	public static int getCellValInteger(Cell cell) {
		return getCellValInteger(cell, 0);
	}

	/**
	 * 得到单元格里的数值<br>
	 * @param cell Cell
	 * @param def int
	 * @return int
	 */
	public static int getCellValInteger(Cell cell, int def) {
		if (cell == null || cell.toString().length() == 0) return def;
		String val = getCellVal(cell);
		if (val == null) return def;
		if (!UtilsVerification.isNumeric(val)) return def;
		return Integer.parseInt(val);
	}

	/**
	 * 从表中得到数值，如果为空或null，则返回def<br>
	 * @param sheet Sheet
	 * @param x int
	 * @param y int
	 * @return int
	 */
	public static int getCellValInteger(Sheet sheet, int x, int y) {
		return getCellValInteger(sheet, x, y, 0);
	}

	/**
	 * 从表中得到数值，如果为空或null，则返回def<br>
	 * @param sheet Sheet
	 * @param x int
	 * @param y int
	 * @param def int
	 * @return int
	 */
	public static int getCellValInteger(Sheet sheet, int x, int y, int def) {
		Cell cell = getCell(sheet, x, y);
		if (cell == null) return def;
		return getCellValInteger(cell, def);
	}

	/**
	 * 获取单元格各类型值，返回字符串类型<br>
	 * 如果为null，则返回 空串 def
	 * @param cell Cell
	 * @param def double
	 * @return double
	 */
	public static final double getCellValueByDouble(Cell cell, double def) {
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
	public static final int getCellValueByInteger(final Cell cell, final int def) {
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
			if (UtilsArrays.isExist(i, filterColumns)) continue;
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
			String value = getCellVal(sheet, i, y, "");
			if (value == null || value.length() == 0) continue;
			if (isTrim && value.trim().length() == 0) continue;
			return i;
		}
		return 0;
	}

	/**
	 * 得到表格的最大宽度(列数)<br>
	 * 与getMaxWidth区别 此方法以getFirstRowNum 开始
	 * @param sheet Sheet
	 * @return int
	 */
	@Deprecated
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

	/**
	 * 得到一个空的sheet名称 发现key同名，则以 key+(0~999)<br>
	 * 否则得到一个日期+随机(4位)的字符串
	 * @param wb Workbook
	 * @param key String
	 * @return String
	 */
	public static final String getNewSheetName(final Workbook wb, final String key) {
		if (wb == null || key == null) return UtilsRnd.getNewFilenameNow(4, 4);
		String newKey = getSheetNameFormat(key);
		Sheet sheet = wb.getSheet(newKey);
		if (sheet == null) return newKey;
		for (int i = 0; i <= 999; i++) {
			String name = newKey + "(" + i + ")";
			sheet = wb.getSheet(name);
			if (sheet == null) return name;
		}
		return newKey + UtilsRnd.getNewFilenameNow(4, 4);
	}

	/**
	 * 得到sheet名字，如果sheetname为空，则使用"信息"作为key
	 * @param wb Workbook
	 * @param sheetName String
	 * @return String
	 */
	public static final String getNewSheetNameDefault(Workbook wb, String sheetName) {
		return getNewSheetName(wb, sheetName == null ? "信息" : sheetName);
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
			String value = getCellVal(sheet, i, y, "");
			if (value == null) continue;
			if (value.equals(key)) return i;
		}
		return -1;
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
			Workbook wb = new XSSFWorkbook(file);
			return wb.getSheetAt(sheetNum);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 通过文件名与sheetname得到sheet
	 * @param filename String
	 * @param sheetName String
	 * @return Sheet
	 */
	public static final Sheet getSheet(String filename, String sheetName) {
		if (filename == null || filename.length() == 0) return null;
		if (sheetName == null || sheetName.length() == 0) return null;
		try {
			File file = new File(filename);
			if (file == null || !file.isFile()) return null;
			Workbook wb = new XSSFWorkbook(file);
			return wb.getSheet(sheetName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 判断Excel文件中sheet的名字是否含有关键，如果不是含有状态，则判断index
	 * @param filename String
	 * @param keyword String
	 * @param isContain boolean
	 * @param index int
	 * @return int
	 */
	public static final int getSheetID(String filename, String keyword, boolean isContain, int index) {
		try {
			File file = new File(filename);
			if (file == null || !file.isFile()) return -1;
			Workbook wb = new XSSFWorkbook(file);
			for (int i = 0, len = wb.getNumberOfSheets(); i < len; i++) {
				Sheet sheet = wb.getSheetAt(i);
				String sheetname = sheet.getSheetName();
				if (isContain) {
					if (sheetname.indexOf(keyword) > -1) return i;
				} else {
					if (sheetname.indexOf(keyword) == index) return i;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
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
			Workbook wb = new XSSFWorkbook(file);
			for (int i = 0, len = wb.getNumberOfSheets(); i < len; i++) {
				Sheet sheet = wb.getSheetAt(i);
				if (sheet.getSheetName().indexOf(sheetName) > -1) return sheet;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 从某个目录中提取所有文件名头部为fileLeft的xls文件，并读出所有sheet名称中头部为sheetLeft的Sheet
	 * @param filePath String
	 * @param fileLeft String
	 * @param sheetLeft String
	 * @return List&lt;Sheet&gt;
	 */
	public static final List<Sheet> getSheetListLeftKey(String filePath, String fileLeft, String sheetLeft) {
		List<Sheet> list = new ArrayList<>();
		List<String> fileslist = UtilsPathFile.getFileslistSort(fileLeft, filePath);
		if (fileslist.size() == 0) return list;
		try {
			for (String e : fileslist) {
				System.out.println("e:" + e);
				File file = new File(e);
				if (file == null || !file.isFile()) continue;
				Workbook wb = new XSSFWorkbook(file);
				for (int i = 0, len = wb.getNumberOfSheets(); i < len; i++) {
					Sheet sheet = wb.getSheetAt(i);
					if (sheet.getSheetName().indexOf(sheetLeft) == 0) list.add(sheet);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 控制sheet名称不允许超出28个字符<br>
	 * 如key为null或空，则返回null 因为excel不允许使用空sheet名
	 * @param key String
	 * @return String
	 */
	public static final String getSheetNameFormat(String key) {
		if (key == null || key.length() == 0) return null;
		if (key.length() > 28) return key.substring(0, 28);
		return key;
	}

	/**
	 * 通过文件名与sheetNum得到sheet
	 * @param filename String
	 * @return Sheet[]
	 */
	public static final Sheet[] getSheets(String filename) {
		try {
			File file = new File(filename);
			if (file == null || !file.isFile()) return null;
			Workbook wb = new XSSFWorkbook(file);
			List<Sheet> list = new ArrayList<>();
			for (int i = 0, len = wb.getNumberOfSheets(); i < len; i++) {
				Sheet sheet = wb.getSheetAt(i);
				list.add(sheet);
			}
			Sheet[] arr = {};
			return list.toArray(arr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 把Row转在List&lt;String&gt;<br>
	 * 不进行运算
	 * @param row Row
	 * @return List&lt;String&gt;
	 */
	public static final List<String> getValuesList(Row row) {
		if (row == null) return new ArrayList<>();
		List<String> list = new ArrayList<>();
		for (int i = 0, len = row.getLastCellNum(); i <= len; i++) {
			Cell cell = row.getCell(i);
			if (cell == null) {
				list.add("");
				continue;
			}
			String value = getCellVal(cell);
			list.add(value);
		}
		return list;
	}

	/**
	 * 从sheet内容转成list格式
	 * @param sheet Sheet
	 * @return List&lt;List&lt;String&gt;&gt;
	 */
	public static final List<List<String>> getValuesList(Sheet sheet) {
		List<List<String>> list = new ArrayList<>();
		if (sheet == null) return list;
		for (int i = 0, len = sheet.getLastRowNum(); i <= len; i++) {
			Row row = sheet.getRow(i);
			List<String> newlist = getValuesList(row);
			list.add(newlist);
		}
		return list;
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
	 * 得到表格中各行中最大宽度(列数)<br>
	 * 与getMaxCellNum区别 此方法以 0 开始
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
	 * 判断sheet是否是隐藏型
	 * @param sheet Sheet
	 * @return boolean
	 */
	public static final boolean isHiddenSheet(Sheet sheet) {
		if (sheet == null) return false;
		return isHiddenSheet(sheet.getWorkbook(), sheet);
	}

	/**
	 * 判断sheet是否是隐藏型
	 * @param wb Workbook
	 * @param sheetIx int
	 * @return boolean
	 */
	public static final boolean isHiddenSheet(Workbook wb, int sheetIx) {
		if (wb == null || sheetIx < 0) return false;
		return isHiddenSheet(wb, wb.getSheetAt(sheetIx));
	}

	/**
	 * 判断sheet是否是隐藏型
	 * @param wb Workbook
	 * @param sheet Sheet
	 * @return boolean
	 */
	public static final boolean isHiddenSheet(Workbook wb, Sheet sheet) {
		if (wb == null) return false;
		if (sheet == null) return false;
		int sheetIx = wb.getSheetIndex(sheet);
		return wb.isSheetHidden(sheetIx);
	}

	/**
	 * 判断sheet是否是隐藏型
	 * @param wb Workbook
	 * @param sheetname String
	 * @return boolean
	 */
	public static final boolean isHiddenSheet(Workbook wb, String sheetname) {
		if (wb == null || sheetname == null || sheetname.length() == 0) return false;
		return isHiddenSheet(wb, wb.getSheet(sheetname));
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
	 * 复制原有sheet的合并单元格到新创建的sheet
	 * @param fromSheet Sheet 新创建sheet
	 * @param toSheet Sheet 原有的sheet
	 */
	public static final void mergerRegion(Sheet fromSheet, Sheet toSheet) {
		int sheetMergerCount = fromSheet.getNumMergedRegions();
		for (int i = 0; i < sheetMergerCount; i++) {
			CellRangeAddress mergedRegionAt = fromSheet.getMergedRegion(i);
			toSheet.addMergedRegion(mergedRegionAt);
		}
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
		if (!UtilsArrays.isSuffixValid(ax, ay, bx, by, tox, toy)) return false;
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
	 * 把单元移到指定单元。如移动的单元为空，则不移动
	 * @param sheet Sheet
	 * @param a Point
	 * @param b Point
	 * @return boolean
	 */
	public static final boolean move(final Sheet sheet, Point a, Point b) {
		if (sheet == null || a == null || b == null) return false;
		return move(sheet, a.x, a.y, b.x, b.y);
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
			String value = getCellVal(sheet, i - 1, y, "");
			if (value == null) value = "";
			setCellValue(sheet, true, i, y, value);
		}
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
			String value = getCellVal(sheet, i + 1, y, "");
			if (value == null) value = "";
			setCellValue(sheet, true, i, y, value);
		}
	}

	/**
	 * 保存workbook到model目录里的随机数文件，是否关闭Workbook
	 * @param saveFolder String
	 * @param shell Shell
	 * @param wb Workbook
	 * @param isClose boolean
	 * @param isAlert boolean
	 * @return File
	 */
	public static File save(String saveFolder, Shell shell, Workbook wb, boolean isClose, boolean isAlert) {
		//AbstractTask e=UtilsSWTTools.getParentObj(shell, AbstractTask.class);
		String proFolder = "";
		if (saveFolder != null) proFolder = saveFolder;
		File file = UtilsFile.mkOutputRNDFile(proFolder, "xlsx");
		if (file == null) {
			if (isAlert) {
				logger.debug("文件生成失败");
				UtilsSWTMessageBox.Alert(shell, "文件生成失败");
			}
			return null;
		}
		boolean t = save(wb, isClose, file);
		if (t) {
			String filename = UtilsFileMethod.getFileName(file);
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
	 * @param wb Workbook
	 * @param isClose boolean
	 * @param file File
	 * @return boolean
	 */
	public static final boolean save(Workbook wb, boolean isClose, File file) {
		if (wb == null || file == null) return false;
		try (FileOutputStream fileoutputStream = new FileOutputStream(file)) {
			wb.write(fileoutputStream);
			fileoutputStream.close();
			if (isClose) wb.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 以某个单元格，同列，向下合并空格，直至非空数据
	 * @param style CellStyle
	 * @param cell Cell
	 * @param x int
	 * @param y int
	 */
	public static final void setCellRangeAddressRows(CellStyle style, Cell cell, int x, int y) {
		if (cell == null) return;
		Sheet sheet = cell.getSheet();
		int downX = x;
		for (int i = x + 1; i <= sheet.getLastRowNum(); i++) {
			String value = getCellVal(sheet.getRow(i).getCell(y));
			if (value == null || value.length() == 0) downX = i;
			else break;
		}
		if (downX == x) return;
		/* 合并向下多行单元格 */
		CellRangeAddress region1 = new CellRangeAddress(x, downX, y, y);
		cell.getSheet().addMergedRegion(region1);
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
			String str = UtilsPOI.arrangementString((String) obj);
			cell.setCellValue(str);
			return true;
		}
		return false;
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
	public static final boolean setCellValue(Sheet sheet, boolean isCreate, int x, int y, String value) {
		if (sheet == null) return false;
		if (isCreate) {
			Cell cell = createCell(sheet, x, y);
			if (cell == null) return false;
			cell.setCellValue(UtilsPOI.arrangementString(value));
			return true;
		}
		Row row = sheet.getRow(x);
		if (row == null) return false;
		Cell cell = row.getCell(y);
		if (cell == null) return false;
		cell.setCellValue(UtilsPOI.arrangementString(value));
		return true;
	}

	/**
	 * 设置背景色
	 * @param style CellStyle
	 * @param ref HSSFColorPredefined
	 */
	public static void setStyleBGColor(CellStyle style, HSSFColorPredefined ref) {
		if (style == null) return;
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		if (ref == null) return;
		style.setFillForegroundColor(ref.getIndex());
	}

}
