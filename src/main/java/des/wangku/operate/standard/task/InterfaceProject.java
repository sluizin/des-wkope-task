package des.wangku.operate.standard.task;

/**
 * 项目启动时或结束时需要的一些方法
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public interface InterfaceProject {
	/**
	 * 任务关闭时，关闭相应的项目信息，在主程序中已定义
	 */
	public default void disposeProject() {

	};

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
	 * model加载后直接运行的前置程序<br>
	 * TaskObjectClass对象未注入<br>
	 * 加载第一步
	 */
	public default void startup() {

	}

	/**
	 * model加载后直接运行的前置程序<br>
	 * TaskObjectClass对象已注入，进行二次修改<br>
	 * 加载第二步<br>
	 * 注意:对象已经构造后才运行此方法<br>
	 */
	public default void afterLoadProject() {

	}
	/**
	 * 对象已经加载并构造后，刷新屏幕后运行此方法
	 */
	public default void afterRepaintComposite() {

	}
}
