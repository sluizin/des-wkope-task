package des.wangku.operate.standard.utls;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import des.wangku.operate.standard.desktop.DesktopUtils;
import des.wangku.operate.standard.dialog.HelpDialog;
import des.wangku.operate.standard.task.AbstractTask;

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
		try {
			URL url = UtilsJar.getJarSourceURL(clazz, filename);/* "/update.info" */
			if (!UtilsVerification.isURL(url)) return;
			InputStream is = url.openStream();
			HelpDialog ver = new HelpDialog(shell, 0, is);
			ver.open();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * 输出框必须为数字
	 * @param text Text
	 * @return VerifyListener
	 */
	public static final VerifyListener getVerifyListener(Text text) {
		VerifyListener t = new VerifyListener() {
			public void verifyText(VerifyEvent event) {
				event.doit = false;
				char myChar = event.character;
				//if (text.getText().indexOf(".") == -1)
				if (myChar == '0' || myChar == '1' || myChar == '2' || myChar == '3' || myChar == '4' || myChar == '5' || myChar == '6' || myChar == '7' || myChar == '8' || myChar == '9' || myChar == '\b' || myChar == SWT.DEL)
					event.doit = true;
				if (myChar == '\b') event.doit = true;
			}
		};
		return t;
	}
	/**
	 * 向容器中的每个button按扭加载点击监听器，用于记录所有Text的内容，并保存进入到记忆文档中
	 * @param base AbstractTask
	 */
	public static final void button_remember_Input(AbstractTask base) {
		List<Button> list = UtilsSWTComposite.getCompositeSearchChildrenControl(Button.class, base.parentComposite);
		for (Button e : list) {
			e.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					List<Text> list2 = UtilsSWTComposite.getCompositeSearchChildrenControl(Text.class, base.parentComposite);
					for (Text f : list2) {
						System.out.println(f.hashCode() + ":" + f.getText());
					}
				}
			});
		}
	}

}
