package des.wangku.operate.standard.task;

import java.util.ArrayList;
import java.util.List;

/**
 * 多线程运行时可以使用的接口
 * 
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public interface InterfaceMultiThreadOnRun {
	public static final int ACC_MAXThreadCount=30;
	/** 运行时暂时关闭，除关闭已经打开的单元外，可以额外控制(可以打开已经关闭的单元)，运行此方法前所有对象已关闭，可以额外打开 */
	public default void multiThreadOnRun() {
		
	}

	/** 运行完成后打开有效性,除打开已经关闭的单元外。可以额外控制 */
	public default void multiThreadOnRunEnd() {
		
	}
	/**
	 * 得到线程总数
	 * @return List&lt;InterfaceThreadRunUnit&gt;
	 */
	public default List<InterfaceThreadRunUnit> getECTFThreadRunUnitList() {
		List<InterfaceThreadRunUnit> workList = new ArrayList<>();
		return workList;
	}
}
