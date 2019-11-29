package des.wangku.operate.standard.dialog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;

import des.wangku.operate.standard.swt.ResultTable;
import des.wangku.operate.standard.task.InterfaceChanged;
import des.wangku.operate.standard.utls.UtilsDate;
import des.wangku.operate.standard.utls.UtilsDialogState;
import des.wangku.operate.standard.utls.UtilsSWTMessageBox;

import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
/**
 * 过滤时间对话框
 * 
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class FilteringTimeDialog extends Dialog {
	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(FilteringTimeDialog.class);
	Shell parent;
	protected Shell shell = null;
	ResultTable table = null;
	Group group = null;
	Group group_1 = null;
	Button checkNull = null;
	Button checkBadCode = null;
	Button checkBefore = null;
	DateTime dateTime = null;
	DateTime dateTime_1 = null;
	Button checkDate = null;
	int index = -1;
	InterfaceChanged eParent = null;

	public FilteringTimeDialog(Shell parent, int style, InterfaceChanged eParent, ResultTable table, int index) {
		super(parent, style);
		this.parent = parent;
		this.table = table;
		this.shell = new Shell(parent);
		this.eParent = eParent;
		this.index = index;
		shell.setText("过滤时间");
		shell.setSize(302, 203);

		group = new Group(shell, SWT.NONE);
		group.setText("过滤范围");
		group.setBounds(10, 0, 276, 168);

		checkNull = new Button(group, SWT.CHECK);
		checkNull.setBounds(10, 20, 33, 16);
		checkNull.setText("空");

		checkBadCode = new Button(group, SWT.CHECK);
		checkBadCode.setBounds(73, 20, 45, 16);
		checkBadCode.setText("乱码");

		checkBefore = new Button(group, SWT.CHECK);
		checkBefore.setBounds(133, 20, 45, 16);
		checkBefore.setText("之前");

		group_1 = new Group(group, SWT.NONE);
		group_1.setEnabled(false);
		group_1.setText("日期段");
		group_1.setBounds(10, 42, 246, 60);

		dateTime = new DateTime(group_1, SWT.BORDER);
		dateTime.setBounds(10, 22, 81, 23);

		dateTime_1 = new DateTime(group_1, SWT.BORDER);
		dateTime_1.setBounds(155, 22, 81, 23);

		Label label = new Label(group_1, SWT.CENTER);
		label.setBounds(110, 28, 26, 12);
		label.setText("--");

		Button button_submit = new Button(group, SWT.NONE);
		button_submit.setBounds(90, 136, 72, 22);
		button_submit.setText(" 移 除 ");
		button_submit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!UtilsSWTMessageBox.Confirm(parent.getShell(), "是否要过滤数据?")) return;
				parent.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						TableItem[] arrs = table.getItems();
						for (int i = arrs.length - 1; i >= 0; i--) {
							String value = arrs[i].getText(index);
							int state = UtilsDate.isDateState(value);
							if ((state == -1 || state == 0) && (checkNull.getSelection())) {
								logger.debug("Null delete:" + i);
								table.remove(i);
								continue;
							}
							if ((state == 1) && (checkBadCode.getSelection())) {
								logger.debug("BadCode delete:" + i);
								table.remove(i);
								continue;
							}
							if ((state == 2) && (checkBefore.getSelection())) {
								logger.debug("Before delete:" + i);
								table.remove(i);
								continue;
							}
							if (state == 3 && checkDate.getSelection()) {
								String date1 = UtilsDate.combination(dateTime.getYear() + "", (dateTime.getMonth() + 1) + "", dateTime.getDay() + "");
								String date2 = UtilsDate.combination(dateTime_1.getYear() + "", (dateTime_1.getMonth() + 1) + "", dateTime_1.getDay() + "");
								logger.debug("dateTime1:" + date1);
								logger.debug("dateTime2:" + date2);
								String datethis = UtilsDate.getDateString(value);
								if (UtilsDate.isEffectiveDateTime(datethis, date1, date2)) {
									logger.debug("EffectiveDateTime delete:" + i + "\t:" + value);
									table.remove(i);
								}
							}
						}
						eParent.changeAfter(null);
					}
				});
			}
		});

		checkDate = new Button(group, SWT.CHECK);
		checkDate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (checkDate.getSelection()) group_1.setEnabled(true);
				else group_1.setEnabled(false);
			}
		});
		checkDate.setBounds(197, 20, 69, 16);
		checkDate.setText("日期段");

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
		return null;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		UtilsDialogState.changeDialogCenter(parent, shell);

	}
}
