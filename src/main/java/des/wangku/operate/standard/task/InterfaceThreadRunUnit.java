package des.wangku.operate.standard.task;
/**
 * 线程运行单元
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 *
 */
public  interface  InterfaceThreadRunUnit {
	/**
	 * 单线程运行间隔，休息时间，单位毫秒，如未设置则为2000毫秒
	 * @return long
	 */
	public default long getSleepTime() {
		return 2000L;
	};
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
