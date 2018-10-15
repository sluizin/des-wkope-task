package des.wangku.operate.standard.task;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 进度提示框线程所需要的接口
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 *
 */
public interface InterfaceRunDialog {

	/** 线程停止 */
	AtomicBoolean isThreadBreak = new AtomicBoolean(false);
	/**
	 * 得到值
	 * @return boolean
	 */
	public default boolean getIsBreak() {
		return isThreadBreak.get();		
	}
	/**
	 * 修改值
	 * @param t boolean
	 */
	public default void setIsBreakChange(boolean t) {
		isThreadBreak.compareAndSet(!t, t);
	}

}
