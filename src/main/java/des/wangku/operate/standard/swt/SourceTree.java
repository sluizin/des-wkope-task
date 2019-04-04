package des.wangku.operate.standard.swt;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;

/**
 * 重构Tree UI组件
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 * //@Deprecated
 */
public class SourceTree extends Tree {
	/** 深度 以0为开始级 */
	int deep;
	/** 提取终端中某个下标的字段属性 */
	int valueIndex;

	protected void checkSubclass() {

	}
	/**
	 * 构造函数
	 * @param parent Composite
	 * @param style int
	 * @param deep int
	 * @param valueIndex int
	 */
	public SourceTree(Composite parent, int style, int deep, int valueIndex) {
		super(parent, style);
		checkSubclass();
		this.deep = deep;
		this.valueIndex = valueIndex;
	}

	public final int getDeep() {
		return deep;
	}

	public final void setDeep(int deep) {
		this.deep = deep;
	}

	public final int getValueIndex() {
		return valueIndex;
	}

	public final void setValueIndex(int valueIndex) {
		this.valueIndex = valueIndex;
	}

}
