package des.wangku.operate.standard.utls;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * 针对容器的工具方法集
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class UtilsSWTComposite {
	/** 日志 */
	static Logger logger = Logger.getLogger(UtilsSWTComposite.class);

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
}
