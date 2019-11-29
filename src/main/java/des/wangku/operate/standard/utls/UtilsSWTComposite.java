package des.wangku.operate.standard.utls;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * 针对容器的工具方法集，主要是提取子集
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class UtilsSWTComposite {
	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(UtilsSWTComposite.class);

	/**
	 * 得到某容器下的各子操作单元，以isEnable为标准，是否有效
	 * @param parent Composite
	 * @param isEnable boolean
	 * @return CVontrol[]
	 */
	public static final Control[] getCompositeChildrenEnable(Composite parent, boolean isEnable) {
		Control[] t = {};
		if (parent == null) return t;
		Control[] controls = parent.getChildren();
		List<Control> list = new ArrayList<>(controls.length);
		for (int i = 0; i < controls.length; i++) {
			Control e = controls[i];
			if (e.isEnabled() == isEnable) list.add(e);
		}
		return list.toArray(t);
	}

	/**
	 * 从容器中提取泛型对象，如果有则保存在list中<br>
	 * 如果还有类似于group性质的容器，则自动嵌套查找
	 * @param clazz Class&lt;?&gt;
	 * @param parent Composite
	 */
	public static final <T> List<T> getCompositeSearchChildrenControl(Class<?> clazz, Composite parent) {
		List<T> list = new ArrayList<>();
		compositeSearch(clazz, list, parent);
		return list;
	}

	/**
	 * 从容器中提取泛型对象，如果有则保存在list中<br>
	 * 如果还有类似于group性质的容器，则自动嵌套查找
	 * @param clazz Class&lt;?&gt;
	 * @param list List &lt;T&gt;
	 * @param parent Composite
	 */
	@SuppressWarnings("unchecked")
	private static final <T> void compositeSearch(Class<?> clazz, List<T> list, Composite parent) {
		if(clazz==null || parent==null)return;
		Control[] arrs = parent.getChildren();
		for (Control e : arrs) {
			if (e.getClass().equals(clazz)) {
				list.add((T) e);
			}
			if (e instanceof Composite) {
				Composite base = (Composite) e;
				compositeSearch(clazz, list, base);
			}
		}
	}

	/**
	 * 得到某容器下的各子操作单元，以类为标准
	 * @param parent Composite
	 * @param classzz Class&lt;?&gt;[]
	 * @return Control[]
	 */
	public static final Control[] getCompositeChildren(Composite parent, Class<?>... classzz) {
		Control[] t = {};
		if (parent == null || classzz == null) return t;
		Control[] controls = parent.getChildren();
		List<Control> list = new ArrayList<>(controls.length);
		for (Control e : controls)
			if (isInstanceClass(e, classzz)) list.add(e);
		return list.toArray(t);
	}

	/**
	 * 判断对象能否转换成类数组中的某个类
	 * @param obj Object
	 * @param classzz Class&lt;?&gt;[]
	 * @return boolean
	 */
	public static final boolean isInstanceClass(Object obj, Class<?>... classzz) {
		if (obj == null) return false;
		for (Class<?> e : classzz)
			if (e.isInstance(obj)) return true;
		return false;
	}
}
