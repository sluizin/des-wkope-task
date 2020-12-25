package des.wangku.operate.standard.dialog;

import java.util.Arrays;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import des.wangku.operate.standard.swt.AbstractCTabFolder;
import des.wangku.operate.standard.swt.ResultTable;
import des.wangku.operate.standard.utls.UtilsDialogState;
import des.wangku.operate.standard.utls.UtilsSWTListener;
import des.wangku.operate.standard.utls.UtilsSWTMessageBox;

/**
 * 添加新的sheet
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */

public class InputNewSheetDialog extends Dialog {

	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(InputNewSheetDialog.class);
	protected Shell shlsheet;
	AbstractCTabFolder parentExcel;
	NewSheetInfor result = new NewSheetInfor();
	private Text text;
	private Text text_rows;
	private Text text_cols;

	public InputNewSheetDialog(Shell parent, int style, AbstractCTabFolder parentExcel) {
		super(parent, style);
		this.shlsheet = new Shell(parent);
		this.parentExcel = parentExcel;
		shlsheet.setText("设置新Sheet");
		shlsheet.setSize(213, 140);
		UtilsDialogState.changeDialogCenter(parent, shlsheet);

		text = new Text(shlsheet, SWT.BORDER);
		text.setBounds(55, 21, 121, 18);
		Label label = new Label(shlsheet, SWT.NONE);
		label.setAlignment(SWT.RIGHT);
		label.setBounds(10, 24, 39, 12);
		label.setText("名称:");

		Button button = new Button(shlsheet, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String sheetName = text.getText().trim();
				if (sheetName.length() == 0) {
					UtilsSWTMessageBox.Alert(shlsheet, "sheet名称不允许为空!");
					return;
				}
				boolean isExist = parentExcel.isExistSheetName(sheetName);
				if (isExist) {
					UtilsSWTMessageBox.Alert(shlsheet, "发现同名sheet!");
					return;
				}
				result.sheetName = sheetName;

				String rows = text_rows.getText().trim();
				if (rows.length() == 0) {
					UtilsSWTMessageBox.Alert(shlsheet, "行数不为空!");
					return;
				}
				result.rows = Integer.parseInt(rows);

				String cols = text_cols.getText().trim();
				if (cols.length() == 0) {
					UtilsSWTMessageBox.Alert(shlsheet, "列数不为空!");
					return;
				}
				result.cols = Integer.parseInt(cols);

				shlsheet.dispose();
			}
		});
		button.setBounds(55, 83, 72, 22);
		button.setText("确定");

		Label label_1 = new Label(shlsheet, SWT.NONE);
		label_1.setAlignment(SWT.RIGHT);
		label_1.setBounds(10, 48, 39, 12);
		label_1.setText("行:");

		text_rows = new Text(shlsheet, SWT.BORDER);
		text_rows.setText("0");
		text_rows.setTextLimit(3);
		text_rows.setBounds(57, 45, 39, 18);
		text_rows.addVerifyListener(UtilsSWTListener.getVerifyListener());

		Label label_2 = new Label(shlsheet, SWT.NONE);
		label_2.setAlignment(SWT.RIGHT);
		label_2.setBounds(106, 48, 25, 12);
		label_2.setText("列:");

		text_cols = new Text(shlsheet, SWT.BORDER);
		text_cols.setText("0");
		text_cols.setTextLimit(3);
		text_cols.setBounds(137, 45, 39, 18);
		text_cols.addVerifyListener(UtilsSWTListener.getVerifyListener());
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {

	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shlsheet.open();
		shlsheet.layout();
		Display display = getParent().getDisplay();
		while (!shlsheet.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * 新添加的sheet的信息
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 */
	public static final class NewSheetInfor {
		String sheetName = "";
		int rows = 0;
		int cols = 0;

		public final String getSheetName() {
			return sheetName;
		}

		public final void setSheetName(String sheetName) {
			this.sheetName = sheetName;
		}

		public final int getRows() {
			return rows;
		}

		public final void setRows(int rows) {
			this.rows = rows;
		}

		public final int getCols() {
			return cols;
		}

		public final void setCols(int cols) {
			this.cols = cols;
		}

		/**
		 * 向ExcelCTabFolder添加sheet
		 * @param base ExcelCTabFolder
		 */
		public void addSheet(AbstractCTabFolder base) {
			if (base == null || sheetName == null || sheetName.length() == 0) return;
			String[] titleArrs = base.getCTableTitle();
			for (int i = 0; i < titleArrs.length; i++)
				if (titleArrs[i].equals(sheetName)) return;
			CTabItem tbtmExcel = new CTabItem(base, SWT.NONE);
			tbtmExcel.setText(sheetName);
			ResultTable table = new ResultTable(base, ResultTable.ACC_ResultTableState,base.getProperties(),sheetName);
			table.setHeaderVisible(base.isViewTableHead());
			table.setLinesVisible(true);
			tbtmExcel.setControl(table);
			for (int i = 0; i < this.cols; i++)
				table.setTableColumn(SWT.LEFT,i + "", 180);
			String[] arrs = new String[cols];
			Arrays.fill(arrs, "");
			for (int i = 0; i < this.rows; i++)
				table.add(arrs);
		}
	}
}
