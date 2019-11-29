package des.wangku.operate.standard.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
/**
 * 多选单列，可以上下移动，删除，但不允许左移右移
 * 
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class MultiTreeSingleColumn extends MultiTree {

	public MultiTreeSingleColumn(Composite parent,String...arrs) {
		super(parent,SWT.None,false);
		this.allowMoveUD();
		singleAutoAddArrays(arrs);
	}
	/**
	 * 设置数据
	 * @param arrs String[]
	 */
	public void setArrs(String... arrs) {
		singleAutoAddArrays(arrs);
	}

}
