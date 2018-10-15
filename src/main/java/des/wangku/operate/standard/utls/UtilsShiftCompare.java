package des.wangku.operate.standard.utls;

import org.apache.log4j.Logger;

/**
 * Shift移位比较 int型
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class UtilsShiftCompare {
	/** 日志 */
	static Logger logger = Logger.getLogger(UtilsShiftCompare.class);
	/**
	 * 比较两个int是否含有关系 位比较关系
	 * @param value int
	 * @param arrs int[]
	 * @return boolean
	 */
	public static final boolean isCompare(int value, int... arrs) {
		for (int i = 0; i < arrs.length; i++)
			if ((value & arrs[i]) > 0) return true;
		return false;
	}
}
