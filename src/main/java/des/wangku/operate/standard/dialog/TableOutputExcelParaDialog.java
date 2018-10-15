package des.wangku.operate.standard.dialog;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import des.wangku.operate.standard.utls.UtilsDialogState;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Group;

/**
 * table导出到excel时，需要判断是否需要导出表头信息到excel顶部，是否要锁定标题行
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class TableOutputExcelParaDialog extends Dialog {
	/** 日志 */
	static Logger logger = Logger.getLogger(TableOutputExcelParaDialog.class);
	TableItem[] arr = {};
	Shell parent;
	protected Shell shell;
	ExcelParaClass result = null;
	Group group = null;
	Spinner spinner_rows = null;
	Spinner spinner_cols = null;
	Button button_ishead = null;

	public TableOutputExcelParaDialog(Shell parent, int style, TableItem[] arr) {
		super(parent, style);
		this.parent = parent;
		this.shell = new Shell(parent);
		this.arr = arr;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell.setText("设置Table导出Excel参数");
		shell.setSize(193, 116);
		UtilsDialogState.changeDialogCenter(parent, this.shell);

		button_ishead = new Button(shell, SWT.CHECK);
		button_ishead.setBounds(10, 20, 69, 16);
		button_ishead.setText("导入头部");

		Button button_submit = new Button(shell, SWT.NONE);
		button_submit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result = new ExcelParaClass();
				result.isHead = button_ishead.getSelection();
				result.lockRows = spinner_rows.getSelection();
				result.lockCols = spinner_cols.getSelection();
				shell.dispose();
			}
		});
		button_submit.setBounds(62, 59, 72, 22);
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
		if (arr.length > 0) {
			TableItem first = arr[0];
			spinner_rows.setMaximum(arr.length);
			int cols = first.getParent().getColumnCount();
			spinner_cols.setMaximum(cols);
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
	 * 是否需要头部信息，是否锁定信息
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 */
	public static final class ExcelParaClass {
		boolean isHead = false;
		int lockRows = 0;
		int lockCols = 0;

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

		@Override
		public String toString() {
			return "ExcelParaClass [isHead=" + isHead + ", lockRows=" + lockRows + ", lockCols=" + lockCols + "]";
		}

	}
}
