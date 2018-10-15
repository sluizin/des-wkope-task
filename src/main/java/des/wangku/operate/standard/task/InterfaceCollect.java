package des.wangku.operate.standard.task;
/**
 * 更新汇总，一般用于添加监听时
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 *
 */
public interface InterfaceCollect {
	/**
	 * 汇总
	 * 更新操作后进行局部或全部的更新，一般用于统计信息的更新
	 */
	public void collect();
}
