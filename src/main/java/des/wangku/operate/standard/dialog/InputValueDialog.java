package des.wangku.operate.standard.dialog;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import des.wangku.operate.standard.utls.UtilsDialogState;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

/**
 * 输入名称
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class InputValueDialog extends Dialog {
	/** 日志 */
	static Logger logger = Logger.getLogger(InputValueDialog.class);
	protected Shell shell;
	String result = null;
	String oldValue = "";
	Text text = null;

	public InputValueDialog(Shell parent, int style, String shellText, String labelText, String oldValue, boolean readonly) {
		super(parent, style);
		this.shell = new Shell(parent);
		String title = shellText;
		if (readonly) title = title + "(只读)";
		shell.setText(title);
		shell.setSize(286, 129);
		UtilsDialogState.changeDialogCenter(parent, this.shell);
		this.oldValue = oldValue;
		Button button = new Button(shell, SWT.NONE);
		button.setBounds(101, 61, 101, 22);
		button.setText("确定");
		button.setVisible(!readonly);
		int height = 18;
		int textstype = SWT.BORDER;
		if (isMulti()) {
			shell.setSize(286, 158);
			button.setLocation(101, 100);
			textstype = SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.WRAP;
			height = 70;
		}
		text = new Text(shell, textstype);
		text.setBounds(45, 21, 222, height);
		if (oldValue != null) text.setText(oldValue);

		Label label = new Label(shell, SWT.NONE);
		label.setAlignment(SWT.RIGHT);
		label.setBounds(0, 24, 39, 12);
		label.setText(labelText);

		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result = text.getText().trim();
				shell.dispose();
			}
		});
	}

	/**
	 * 判断是否含有换行，先判断是否长度大于20，如果大于20则多行显示
	 * @return boolean
	 */
	boolean isMulti() {
		if (oldValue.length() > 20) return true;
		if (oldValue.indexOf('\n') != -1) return true;
		return false;
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
}
