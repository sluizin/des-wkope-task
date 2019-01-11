package des.wangku.operate.standard.swt;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.eclipse.swt.graphics.Point;

import des.wangku.operate.standard.utls.UtilsSWTPOI;
import des.wangku.operate.standard.utls.UtilsString;

/**
 * 针对POI中的sheet进行扩展
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class POISheetExt {
	/** 日志 */
	private static Logger logger = LoggerFactory.getLogger(POISheetExt.class);
	Sheet sheet;

	@SuppressWarnings("unused")
	private POISheetExt() {

	}

	public POISheetExt(Sheet sheet) {
		this.sheet = sheet;
	}

	/**
	 * 设置单元格内容，如果没有单元格，则返回false<br>
	 * 如果isCreate为true，则直接建立单元格，并赋值
	 * @param isCreate boolean
	 * @param x int
	 * @param y int
	 * @param value String
	 * @return boolean
	 */
	public boolean setCellValue(boolean isCreate, int x, int y, String value) {
		if (sheet == null) return false;
		if (isCreate) {
			Cell cell = createCell(x, y);
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
	 * @param x int
	 * @param y int
	 * @return Cell
	 */
	public final Cell createCell(int x, int y) {
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
	 * @param x int
	 * @param y int
	 * @param endindex int
	 */
	public final void removeCellUP(int x, int y, int endindex) {
		if (sheet == null) return;
		int rowsLen = sheet.getLastRowNum();
		if (endindex > -1) rowsLen = endindex;
		for (int i = x; i <= rowsLen; i++) {
			if (i == rowsLen) {
				setCellValue(true, i, y, "");
				break;
			}
			String value = UtilsSWTPOI.getCellValueByString(sheet, i + 1, y, false, "");
			if (value == null) value = "";
			setCellValue(true, i, y, value);
		}
	}

	/**
	 * 删除某个单元格，并把同列，上面的行逐行下移，其它列不动
	 * @param x int
	 * @param y int
	 * @param endindex int
	 */
	public final void removeCellDOWN(int x, int y, int endindex) {
		if (sheet == null) return;
		if (!UtilsSWTPOI.isExistValue(sheet, x, y)) return;
		int top = sheet.getFirstRowNum();
		if (endindex > -1) top = endindex;
		for (int i = x; i >= top; i--) {
			if (i == top) {
				setCellValue(true, i, y, "");
				break;
			}
			String value = UtilsSWTPOI.getCellValueByString(sheet, i - 1, y, false, "");
			if (value == null) value = "";
			setCellValue(true, i, y, value);
		}
	}

	/**
	 * 给sheet中null的cell填充value，是否填充所有[以整表为标准]<br>
	 * @param sheet Sheet
	 * @param value Object
	 * @param isFill boolean
	 */
	public final void fill(Object value, boolean isFill) {
		if (sheet == null) return;
		int cellmax = getMaxCellNum();
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
				UtilsSWTPOI.setCellValue(c, value);
			}
		}
	}

	/**
	 * 得到表格的最大宽度
	 * @return int
	 */
	public final int getMaxCellNum() {
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
	 * @param a Point
	 * @param b Point
	 * @return boolean
	 */
	public final boolean move(Point a, Point b) {
		if (sheet == null || a == null || b == null) return false;
		return move(a.x, a.y, b.x, b.y);
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
	public final boolean move(int ax, int ay, int bx, int by) {
		if (sheet == null) return false;
		Cell fromCell = UtilsSWTPOI.getCell(sheet, ax, ay);
		if (fromCell == null) return false;
		Row row = sheet.getRow(bx);
		if (row == null) sheet.createRow(bx);
		row = sheet.getRow(bx);
		Cell tocell = UtilsSWTPOI.getCell(sheet, bx, by);
		if (tocell != null) row.removeCell(tocell);
		row.createCell(by);
		Cell toCell = UtilsSWTPOI.getCell(sheet, bx, by);
		UtilsSWTPOI.getCellCopy(fromCell, toCell);
		fromCell.getRow().removeCell(fromCell);
		return true;
	}

	/**
	 * 移动单元，是否允许null覆盖
	 * @param isNullOver boolean
	 * @param ax int
	 * @param ay int
	 * @param bx int
	 * @param by int
	 * @return boolean
	 */
	public final boolean move(boolean isNullOver, int ax, int ay, int bx, int by) {
		if (sheet == null) return false;
		Cell fromCell = UtilsSWTPOI.getCell(sheet, ax, ay);
		if (fromCell == null) {
			if (isNullOver) {
				Cell toCell = UtilsSWTPOI.getCell(sheet, bx, by);
				if (toCell == null) {
					return true;
				} else {
					delete(toCell);
				}
			} else {
				return true;
			}
		} else {
			Cell toCell = createCell(bx, by);
			UtilsSWTPOI.getCellCopy(fromCell, toCell);
			delete(fromCell);
			return true;
		}

		return true;
	}

	/**
	 * 删除单元
	 * @param cell Cell
	 * @return boolean
	 */
	public final boolean delete(Cell cell) {
		if (sheet == null) return false;
		if (cell == null) return true;
		Row row = cell.getRow();
		row.removeCell(cell);
		return true;

	}

	/**
	 * 删除单元
	 * @param x int
	 * @param y int
	 * @return boolean
	 */
	public final boolean delete(int x, int y) {
		if (sheet == null) return false;
		Cell cell = UtilsSWTPOI.getCell(sheet, x, y);
		return delete(cell);
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
	public final boolean move(boolean isNullOver, int ax, int ay, int bx, int by, int tox, int toy) {
		if (sheet == null) return false;
		if (!UtilsSWTPOI.isSuffixValid(ax, ay, bx, by, tox, toy)) return false;
		for (int x = ax; x <= bx; x++) {
			for (int y = ay; y <= by; y++) {
				int nx = tox + (x - ax);
				int ny = toy + (y - ay);
				logger.debug("[" + x + "," + y + "]->[" + nx + "," + ny + "]");
				move(isNullOver, x, y, nx, ny);
			}
		}
		return true;
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
	public final int getDepth(int y, boolean isTrim) {
		if (sheet == null) return 0;
		for (int i = sheet.getLastRowNum(); i > -1; i--) {
			if (sheet.getRow(i) == null) continue;
			if (sheet.getRow(i).getCell(y) == null) continue;
			String value = UtilsSWTPOI.getCellValueByString(sheet, i, y, isTrim, "");
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
	public final int getWidth(int x) {
		if (sheet == null || sheet.getRow(x) == null) return 0;
		return sheet.getRow(x).getLastCellNum();
	}

	/**
	 * 得到表格中各行中最大宽度
	 * @return int
	 */
	public final int getWidthMax() {
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
	 * 得到表格最大/最小层数
	 * @param isMax boolean
	 * @param isTrim boolean
	 * @param filterColumns int[]
	 * @return int
	 */
	public final int getDepth(boolean isMax, boolean isTrim, int... filterColumns) {
		int len = getWidthMax();
		int extremum = 0;
		for (int i = 0; i <= len; i++) {
			if (UtilsString.isExist(i, filterColumns)) continue;
			int deep = getDepth(i, isTrim);
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
	 * @param key String
	 * @param start int
	 * @param y int
	 * @return int
	 */
	public final int getSearchRowsKeyEquals(String key, int start, int y) {
		if (sheet == null) return -1;
		int rowsLen = sheet.getLastRowNum();
		for (int i = start; i <= rowsLen; i++) {
			String value = UtilsSWTPOI.getCellValueByString(sheet, i, y, false, "");
			if (value == null) continue;
			if (value.equals(key)) return i;
		}
		return -1;
	}

}
