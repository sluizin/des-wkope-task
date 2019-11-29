package des.wangku.operate.standard.dialog;

import java.io.InputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import des.wangku.operate.standard.desktop.DesktopConst;
import des.wangku.operate.standard.utls.UtilsDialogState;
import des.wangku.operate.standard.utls.UtilsFile;

/**
 * 帮助时弹出的窗口
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class HelpDialog extends Dialog {
	Shell parent;
	protected Shell shell = null;
	private Text text;

	int width = 442;

	int height = 266;

	/**
	 * 构造函数
	 * @param parent Shell
	 * @param style int
	 */
	public HelpDialog(Shell parent, int style) {
		super(parent, style);
		structure(parent, style);
		initialization();
	}

	/**
	 * 构造函数 内容使用流进行显示
	 * @param parent Shell
	 * @param style int
	 * @param is InputStream
	 */
	public HelpDialog(Shell parent, int style, InputStream is) {
		super(parent, style);
		structure(parent, style);
		initialization();
		setTextContent(is);
	}

	/**
	 * 构造函数 内容使用字符串进行显示
	 * @param parent Shell
	 * @param style int
	 * @param content String
	 */
	public HelpDialog(Shell parent, int style, String content) {
		super(parent, style);
		structure(parent, style);
		initialization();
		if (content != null) text.setText(content);
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		UtilsDialogState.changeDialogCenter(parent, shell);
	}

	/**
	 * 返回高度，不能低于50
	 * @return int
	 */
	public final int getHeight() {
		if (height < 50) return 50;
		return height;
	}

	/**
	 * 返回宽度，不能低于50
	 * @return int
	 */
	public final int getWidth() {
		if (width < 50) return 50;
		return width;
	}

	/**
	 * 初始化
	 */
	void initialization() {
		shell.setText(DesktopConst.ACC_AuthorTitle);
		shell.setSize(getWidth(), getHeight());
		shell.setImage(DesktopConst.ACC_Shell);
		text = new Text(shell, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		text.setBounds(10, 10, getWidth() - 25, getHeight() - 40);
		shell.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				logger.warn("缷载弹出窗口");
			}

		});
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
	 * 设置sheet宽高
	 * @param width int
	 * @param height int
	 * @return HelpDialog
	 */
	public final HelpDialog setSize(int width, int height) {
		this.width = width;
		this.height = height;
		return this;
	}

	/**
	 * 设置内容 文件流
	 * @param is InputStream
	 */
	public void setTextContent(InputStream is) {
		if (is == null) return;
		String Content = UtilsFile.readFile(is).toString();
		text.setText(Content);
	}

	/**
	 * 设置内容 文件名
	 * @param filename String
	 */
	public void setTextContent(String filename) {
		if (filename == null) return;
		String Content = UtilsFile.readFile(filename).toString();
		text.setText(Content);
	}

	/**
	 * 前期构造函数
	 * @param parent Shell
	 * @param style int
	 */
	public void structure(Shell parent, int style) {
		this.parent = parent;
		shell = new Shell(parent);
	}

	/** 日志 */
	static final Logger logger = LoggerFactory.getLogger(HelpDialog.class);
}
