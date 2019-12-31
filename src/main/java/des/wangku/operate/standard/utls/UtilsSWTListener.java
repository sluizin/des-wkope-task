package des.wangku.operate.standard.utls;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import des.wangku.operate.standard.desktop.DesktopUtils;
import des.wangku.operate.standard.dialog.HelpDialog;

/**
 * SWT里对Listener的工具类
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class UtilsSWTListener {
	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(UtilsSWTListener.class);

	/**
	 * 退出
	 * @return Listener
	 */
	public static final Listener getListenerExist() {
		Listener t = new Listener() {
			public void handleEvent(Event e) {
				DesktopUtils.existProject();
				System.exit(0);
			}
		};
		return t;
	}

	/**
	 * 得到某类所在的jar包中显示的版本信息
	 * @param clazz Class
	 * @param filename String
	 * @return Listener
	 */
	public static final Listener getListenerShowVersion(Class<?> clazz, String filename) {
		Listener t = new Listener() {
			public void handleEvent(Event e) {
				if (clazz == null) return;
				showVersion(clazz, filename);
			}
		};
		return t;
	}

	private static final int ACC_Vers_Style = SWT.CLOSE | SWT.MIN | SWT.DIALOG_TRIM;

	/**
	 * 查看版本信息
	 * @param clazz Class&lt;?&gt;
	 * @param filename String
	 */
	public static final void showVersion(Class<?> clazz, String filename) {
		Display display = Display.getDefault();
		Shell shell = new Shell(display, ACC_Vers_Style);
		/* String path = basicClass.getProtectionDomain().getCodeSource().getLocation().getPath(); */
		URL url = UtilsJar.getJarSourceURL(clazz, filename);/* "/update.info" */
		HelpDialog ver = new HelpDialog(shell, 0, url);
		ver.open();
	}

	/**
	 * 输出框必须为数字
	 * @return VerifyListener
	 */
	public static final VerifyListener getVerifyListener() {
		VerifyListener t = new VerifyListener() {
			public void verifyText(VerifyEvent event) {
				event.doit = false;
				char myChar = event.character;
				//if (text.getText().indexOf(".") == -1)
				if (myChar == '0' || myChar == '1' || myChar == '2' || myChar == '3' || myChar == '4' || myChar == '5' || myChar == '6' || myChar == '7' || myChar == '8' || myChar == '9')
					event.doit = true;
				if (myChar == '\b' || myChar == SWT.DEL)
					event.doit = true;
			}
		};
		return t;
	}

	/**
	 * 判断Control中同类监听是否有相同的
	 * @param e Control
	 * @param eventType int
	 * @param g Listener
	 * @return boolean
	 */
	public static final boolean isListenerExist(Control e, int eventType, Listener g) {
		if (e == null || g == null) return false;
		Listener[] arrs = e.getListeners(eventType);
		for (Listener f : arrs)
			if (f.equals(g)) return true;
		return false;
	}

}
