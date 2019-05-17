package des.wangku.operate.standard.dialog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import des.wangku.operate.standard.swt.ResultTable;
import des.wangku.operate.standard.utls.UtilsDialogState;

import org.eclipse.swt.widgets.Button;

import java.lang.reflect.Array;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Sheet;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Combo;

/**
 * table导出到excel时，需要判断是否需要导出表头信息到excel顶部，是否要锁定标题行
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class TableOutputExcelParaDialog extends Dialog {
	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(TableOutputExcelParaDialog.class);
	Shell parent;
	protected Shell shell;
	ExcelParaClass result = null;
	Group group = null;
	Spinner spinner_rows = null;
	Spinner spinner_cols = null;
	Button button_ishead = null;
	Button button_border = null;
	Combo combo_color = null;

	Object parentObj = null;

	public TableOutputExcelParaDialog(Shell parent, int style, Object parentObj) {
		super(parent, style);
		this.parent = parent;
		this.shell = new Shell(parent);
		this.parentObj = parentObj;
	}

	public final void setParentObj(Object parentObj) {
		this.parentObj = parentObj;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell.setText("设置Table导出Excel参数");
		shell.setSize(193, 157);
		UtilsDialogState.changeDialogCenter(parent, this.shell);

		button_ishead = new Button(shell, SWT.CHECK);
		button_ishead.setBounds(10, 10, 69, 16);
		button_ishead.setText("导入头部");
		button_ishead.setSelection(true);
		Button button_submit = new Button(shell, SWT.NONE);
		button_submit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result = new ExcelParaClass();
				result.isHead = button_ishead.getSelection();
				result.lockRows = spinner_rows.getSelection();
				result.lockCols = spinner_cols.getSelection();
				result.isBorder = button_border.getSelection();
				if (result.isBorder) {
					result.color = IndexedColors.fromInt(combo_color.getSelectionIndex());
				} else {
					result.color = null;
				}
				shell.dispose();
			}
		});
		button_submit.setBounds(59, 100, 72, 22);
		button_submit.setText("导出");

		group = new Group(shell, SWT.NONE);
		group.setText("锁定行列数");
		group.setBounds(94, 0, 89, 50);

		spinner_rows = new Spinner(group, SWT.BORDER | SWT.READ_ONLY);
		spinner_rows.setBounds(10, 19, 32, 21);
		spinner_rows.setToolTipText("锁定行数");

		spinner_cols = new Spinner(group, SWT.BORDER | SWT.READ_ONLY);
		spinner_cols.setBounds(48, 19, 32, 21);
		spinner_cols.setToolTipText("锁定列数");

		button_border = new Button(shell, SWT.CHECK);
		button_border.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (button_border.getSelection()) {
					combo_color.setEnabled(true);
				} else {
					combo_color.setEnabled(false);
				}
			}
		});
		button_border.setBounds(10, 32, 72, 16);
		button_border.setText("边框");

		combo_color = new Combo(shell, SWT.READ_ONLY);
		combo_color.setEnabled(false);
		combo_color.setBounds(10, 56, 87, 21);
		//combo_color.add(ExcelParaClass.defBorderColor.name());
		IndexedColors[] arrs = IndexedColors.values();
		for (int i = 0; i < arrs.length; i++) {
			IndexedColors e = arrs[i];
			combo_color.add(e.name());
			if (e == ExcelParaClass.defBorderColor) {
				combo_color.select(i);
			}
		}
		setSpinnerMax();

	}

	/**
	 * 设置行数与列数的最大值
	 */
	void setSpinnerMax() {
		if (parentObj == null) return;
		if (parentObj instanceof ResultTable) {
			ResultTable table = (ResultTable) parentObj;
			TableItem[] arr = table.getItems();
			if (arr.length > 0) {
				spinner_rows.setMaximum(arr.length);
				spinner_cols.setMaximum(table.getColumnCount());
			}
			return;
		}
		if (parentObj.getClass().isArray()) {
			int length = Array.getLength(parentObj);
			TableItem[] os = new TableItem[length];
			for (int i = 0; i < os.length; i++) {
				Object obj = Array.get(parentObj, i);
				if (!(obj instanceof TableItem)) return;
				os[i] = (TableItem) obj;
			}
			if (os.length > 0) {
				spinner_rows.setMaximum(os.length);
				spinner_cols.setMaximum(os[0].getParent().getColumnCount());
			}
			return;
		}
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * 倒出EXCEL时需要的参数
	 * 是否需要头部信息，是否锁定信息<br>
	 * 是否需要边框
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 */
	public static final class ExcelParaClass {
		boolean isHead = false;
		int lockRows = 0;
		int lockCols = 0;
		boolean isBorder = false;
		IndexedColors color = null;

		public final boolean isHead() {
			return isHead;
		}

		public final void setHead(boolean isHead) {
			this.isHead = isHead;
		}

		public final int getLockRows() {
			return lockRows;
		}

		public final void setLockRows(int lockRows) {
			this.lockRows = lockRows;
		}

		public final int getLockCols() {
			return lockCols;
		}

		public final void setLockCols(int lockCols) {
			this.lockCols = lockCols;
		}

		public final boolean isBorder() {
			return isBorder;
		}

		public final void setBorder(boolean isBorder) {
			this.isBorder = isBorder;
		}

		@Override
		public String toString() {
			return "ExcelParaClass [isHead=" + isHead + ", lockRows=" + lockRows + ", lockCols=" + lockCols + "]";
		}

		/**
		 * 对Sheet依据参数进行美化工作
		 * @param sheet Sheet
		 */
		public void makeStyle(Sheet sheet) {
			if (sheet == null) return;
			/* 锁定某行列 */
			if ((lockRows > 0 || lockCols > 0)) {
				sheet.createFreezePane(lockCols, lockRows);
			}
		}

		static final BorderStyle defBorderStyle = BorderStyle.THIN;
		static final IndexedColors defBorderColor = IndexedColors.BLACK;

		/**
		 * 对Cell依据参数进行美化工作
		 * @param cell Cell
		 */
		public void makeStyle(Cell cell) {
			if (cell == null) return;
			if (isBorder) {
				CellStyle style = cell.getSheet().getWorkbook().createCellStyle();
				style.setBorderTop(defBorderStyle);
				style.setBorderBottom(defBorderStyle);
				style.setBorderLeft(defBorderStyle);
				style.setBorderRight(defBorderStyle);
				if (color != null) {
					style.setTopBorderColor(color.index);
					style.setBottomBorderColor(color.index);
					style.setLeftBorderColor(color.index);
					style.setRightBorderColor(color.index);
				}
				cell.setCellStyle(style);
			}

		}
	}
}
