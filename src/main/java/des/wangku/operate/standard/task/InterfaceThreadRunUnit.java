package des.wangku.operate.standard.task;

/**
 * 线程运行单元<br>
 * {@link InterfaceThreadRunUnit#getSleepTime()} 线程间隔默认2秒<br>
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public interface InterfaceThreadRunUnit {
	/**
	 * 单线程运行间隔，休息时间，单位毫秒，如未设置则为2000毫秒
	 * @return long
	 */
	public default long getSleepTime() {
		return 2000L;
	}
	/**
	 * 线程组自动编组<br>
	 * 默认为0，如果单元数组中含有不同的多个group值，则按照group个类进行分组并分配线程<br>
	 * 此分配方法低于人为指定线程值或线程Spinner<br>
	 * 如未分配线程数，则以此方法为分配方式
	 * @return int
	 */
	public default int getGroup() {
		return 0;
	}

	/**
	 * 每一个线程运行完此节点后进行汇总工作
	 */
	public default void autoCollect() {

	}

	/**
	 * 单元在线程中运行
	 */
	public void run();

	/**
	 * 单元线程运行完成后，进行赋值操作
	 */
	public void show();

	/**
	 * 得到弹出窗口中的文字内容
	 * @return String
	 */
	public String getKeyword();
}
