package des.wangku.operate.standard.utls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * SWT提示
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class UtilsSWTMessageBox {
	/**
	 * 警告与提示
	 * @param shell Shell
	 * @param text String
	 */
	public static final void Alert(Shell shell, String text) {
		Alert(shell, "警告与提示", text);
	}

	/**
	 * 提示信息
	 * @param shell Shell
	 * @param text String
	 * @param message String
	 */
	public static final void Alert(Shell shell, String text, String message) {
		if (shell == null) return;
		MessageBox dialog = new MessageBox(shell, SWT.OK | SWT.ICON_INFORMATION);
		dialog.setText(text);
		dialog.setMessage(message);
		dialog.open();
	}

	/**
	 * 确认提示框
	 * @param shell Shell
	 * @param text String
	 * @return boolean
	 */
	public static final boolean Confirm(Shell shell, String text) {
		return Confirm(shell, "操作确认", text);
	}

	/**
	 * 确认提示框
	 * @param shell Shell
	 * @param text String
	 * @param message String
	 * @return boolean
	 */
	public static final boolean Confirm(Shell shell, String text, String message) {
		MessageBox dialog = new MessageBox(shell, SWT.OK | SWT.CANCEL);
		dialog.setText(text);
		dialog.setMessage(message);
		return dialog.open() == SWT.OK;

	}
}
