package des.wangku.operate.standard.utls;

import java.text.NumberFormat;

/**
 * 数学统计
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 *
 */
public class UtilsMath {
	public static final NumberFormat numberFormat = NumberFormat.getInstance();
	public static final String getPerCent(float a,float b) {
		String result = numberFormat.format(a/ (float) b * 100); 
		return result;
	}
}
