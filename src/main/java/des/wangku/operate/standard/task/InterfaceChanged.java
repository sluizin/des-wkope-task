package des.wangku.operate.standard.task;
/**
 * 操作弹出框或其它操作完成后，需要更新的信息
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 *
 */
public interface InterfaceChanged {
	/**
	 * 操作弹出框或其它操作完成后，需要更新的信息
	 * @param obj Object
	 */
	public void changeAfter(Object obj);
}
