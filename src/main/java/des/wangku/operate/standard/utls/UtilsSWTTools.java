package des.wangku.operate.standard.utls;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.internal.SWTEventListener;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TypedListener;

/**
 * SWT中的一些常用方法
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class UtilsSWTTools {
	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(UtilsSWTTools.class);

	/**
	 * 在数据组中顺序更改 PaintListener SWT.Paint
	 * @param arr PaintListener[]
	 * @param canvas Canvas
	 */
	public static final void changePaintListener(PaintListener[] arr, Canvas canvas) {
		final int arrlen = arr.length;
		if (arrlen == 0 || canvas == null) return;
		Listener[] canvasArray = canvas.getListeners(SWT.Paint);
		int point = -1;
		int i = 0, ii = 0;
		loop: for (int len = canvasArray.length; i < len; i++) {
			Listener l = canvasArray[i];
			if (l instanceof TypedListener) {
				SWTEventListener n = ((TypedListener) l).getEventListener();
				if (n instanceof PaintListener) {
					PaintListener t = (PaintListener) n;
					for (ii = 0; ii < arrlen; ii++) {
						if (t.equals(arr[ii])) {
							point = ii;
							break loop;
						}
					}
				}
			}
		}
		if (point > -1) canvas.removePaintListener(arr[point]);
		if (point == -1 || (point >= (arrlen - 1))) canvas.addPaintListener(arr[0]);
		else canvas.addPaintListener(arr[point + 1]);
		canvas.redraw();
	}
	/**
	 * 从某个对象反推父对象找到相应的接口
	 * @param obj Composite
	 * @param clazz Class &lt; T &gt;  接口
	 * @param <T> T
	 * @return T t
	 */
	@SuppressWarnings("unchecked")
	public static final <T> T getParentInterfaceObj(Composite obj, Class<T> clazz) {
		Composite parent = obj;
		while (parent != null) {
			if (isInterface(parent.getClass(), clazz)) {
				T ee = (T) parent;
				return ee;
			}
			parent = parent.getParent();
		}
		return null;
	}
	/**
	 * 从某个对象反推父对象的父类
	 * @param obj Composite
	 * @param clazz Class  &lt; T &gt; 
	 * @param <T> T
	 * @return T t
	 */
	@SuppressWarnings("unchecked")
	public static final <T> T getParentObjSuperclass(Composite obj, Class<T> clazz) {
		Composite parent = obj;
		while (parent != null) {
			if (parent.getClass().getGenericSuperclass().equals(clazz)) {
				T ee = (T) parent;
				return ee;
			}
			parent = parent.getParent();
		}
		return null;
	}
	/**
	 * 从某个对象反推父对象
	 * @param obj Composite
	 * @param clazz Class&lt;T&gt;
	 * @param <T> T
	 * @return T
	 */
	@SuppressWarnings("unchecked")
	public static final <T> T getParentObj(Composite obj, Class<T> clazz) {
		Composite parent = obj;
		while (parent != null) {
			if (parent.getClass().equals(clazz)) {
				T ee = (T) parent;
				return ee;
			}
			parent = parent.getParent();
		}
		return null;
	}

	/**
	 * 判断此类是否含有接口，允许向父类查和接口的父接口查
	 * @param c Class
	 * @param interfaceClass Class&lt;?&gt;
	 * @return boolean
	 */
	public static final boolean isInterface(final Class<?> c, final Class<?> interfaceClass) {
		if (!interfaceClass.isInterface()) return false;
		Class<?>[] face = c.getInterfaces();
		for (int i = 0, len = face.length; i < len; i++)
			if (face[i].getName().equals(interfaceClass.getName())) {
				return true;
			} else {
				Class<?>[] face1 = face[i].getInterfaces();
				for (int x = 0, len2 = face1.length; x < len2; x++)
					if (face1[x].getName().equals(interfaceClass.getName())) return true;
					else if (isInterface(face1[x], interfaceClass)) return true;
			}
		if (null != c.getSuperclass()) return isInterface(c.getSuperclass(), interfaceClass);
		return false;
	}

	/**
	 * 判断下标是否在后面的数组中
	 * @param value int
	 * @param Array int[]
	 * @return boolean
	 */
	public static final boolean isExistSuffix(int value, int... Array) {
		if (Array == null) return false;
		for (int i = 0; i < Array.length; i++)
			if (Array[i] == value) return true;
		return false;
	}

	/**
	 * 判断后面的数组是否在len之间
	 * @param len int
	 * @param array int[]
	 * @return int[]
	 */
	public static final int[] getIntersection(int len, int... array) {
		int[] newArray = new int[array.length];
		int p = 0;
		for (int i = 0; i < array.length; i++)
			if (array[i] >= 0 && array[i] < len) newArray[p++] = array[i];
		int[] newArray2 = new int[p];
		if (p == 0) return newArray2;
		System.arraycopy(newArray, 0, newArray2, 0, p);
		return newArray2;
	}
}
