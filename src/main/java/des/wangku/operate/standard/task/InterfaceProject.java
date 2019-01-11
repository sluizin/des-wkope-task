package des.wangku.operate.standard.task;
/**
 * 
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 *
 */
public interface InterfaceProject {
	/**
	 * 任务关闭时，关闭相应的资源信息
	 */
	public default void disposeResources() {
		
	};
	/**
	 * 启动条件 为null时加载模块<br>
	 * 一些资源的加载状态与安全判断等。如结果为null，则允许平台加载model<br>
	 * 如果前置的判断出现错误，则返回错误信息以供提示
	 * @return String 提示内容
	 */
	public default String precondition() {
		return null;
	}
	/**
	 * model加载后直接运行的前置程序
	 */
	public default void startup() {
		
	}
}
