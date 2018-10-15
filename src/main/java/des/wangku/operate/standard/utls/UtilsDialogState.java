package des.wangku.operate.standard.utls;

import java.awt.Toolkit;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;

/**
 * 窗口工具类
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class UtilsDialogState {
	/** 日志 */
	static Logger logger = Logger.getLogger(UtilsDialogState.class);

	/**
	 * 针对窗口初始化定位，在父窗口的中间位置显示
	 * @param parent Control
	 * @param shell Control
	 */
	public static void changeDialogCenter(Control parent, Control shell) {
		Rectangle parentBounds = parent.getBounds();
		Rectangle shellBounds = shell.getBounds();
		int x = parentBounds.x;
		int y = parentBounds.y;
		if (parentBounds.width > shellBounds.width) x = parentBounds.x + (parentBounds.width - shellBounds.width) / 2;
		if (parentBounds.height > shellBounds.height) y = parentBounds.y + (parentBounds.height - shellBounds.height) / 2;
		/*
		logger.debug("parent:["+ parentBounds.x+","+parentBounds.y+","+parentBounds.width+","+shellBounds.height+"]");
		logger.debug("shell:["+ x+","+y+","+shellBounds.width+","+shellBounds.height+"]");
		*/
		shell.setLocation(x, y);
	}

	/**
	 * 针对窗口初始化定位，在屏幕中间位置显示
	 * @param shell Control
	 */
	public static void changeDialogCenter(Control shell) {
		int screenH = Toolkit.getDefaultToolkit().getScreenSize().height;/* 获取屏幕高度和宽度 */
		int screenW = Toolkit.getDefaultToolkit().getScreenSize().width;
		int shellH = shell.getBounds().height; /* 获取对象窗口高度和宽度 */
		int shellW = shell.getBounds().width;
		if (shellH > screenH) shellH = screenH;/* 如果对象窗口高度超出屏幕高度，则强制其与屏幕等高 */
		if (shellW > screenW) shellW = screenW;/* 如果对象窗口宽度超出屏幕宽度，则强制其与屏幕等宽 */
		shell.setLocation(((screenW - shellW) / 2), ((screenH - shellH) / 2));/* 定位对象窗口坐标 */
	}
}
