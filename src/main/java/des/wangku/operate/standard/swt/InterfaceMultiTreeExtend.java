package des.wangku.operate.standard.swt;

import org.eclipse.swt.widgets.TreeItem;

/**
 * MultiTree树型UI中选中单个时输出的接口
 * 
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public interface InterfaceMultiTreeExtend {
	/**
	 * 选中MultiTree中的TreeItem后进行后续程序
	 * @param e TreeItem
	 * @return boolean
	 */
	public default boolean multiTreeSelectedAfter(TreeItem e) {
		return false;
	}
	/**
	 * 双击MultiTree中的TreeItem后进行后续程序
	 * @param e TreeItem
	 * @return boolean
	 */
	public default boolean multiTreeMouseDoubleClick(TreeItem e) {
		return false;
	}
}
