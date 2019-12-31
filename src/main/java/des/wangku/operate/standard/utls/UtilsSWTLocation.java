package des.wangku.operate.standard.utls;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 在SWT中得到相应的定位信息
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class UtilsSWTLocation {
	/** 日志 */
	static final Logger logger = LoggerFactory.getLogger(UtilsSWTLocation.class);

	/**
	 * 嵌套得到父窗口的定位信息
	 * @param e Control
	 * @return Point
	 */
	public static final Point getParentLocation(Control e) {
		Point point = new Point(0, 0);
		getParentLocation(e.getParent(), point);
		return point;
	}

	/**
	 * 嵌套得到父窗口的定位信息
	 * @param t Composite
	 * @param point Point
	 */
	private static final void getParentLocation(Composite t, Point point) {
		if (t == null) return;
		if (t instanceof Shell) return;
		point.x += t.getLocation().x;
		point.y += t.getLocation().y;
		getParentLocation(t.getParent(), point);
	}

}
