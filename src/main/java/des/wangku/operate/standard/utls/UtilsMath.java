package des.wangku.operate.standard.utls;

import java.text.NumberFormat;

/**
 * 数学统计
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class UtilsMath {
	public static final NumberFormat numberFormat = NumberFormat.getInstance();

	/**
	 * 百分比
	 * @param a float
	 * @param b float
	 * @return String
	 */
	public static final String getPerCent(float a, float b) {
		return numberFormat.format(a / (float) b * 100);
	}

	/**
	 * 百分比 过滤掉小数部分
	 * @param a float
	 * @param b float
	 * @return String
	 */
	public static final int getPerCentInt(float a, float b) {
		String result = getPerCent(a, b);
		int index = result.indexOf('.');
		if (index == 0) return 0;
		if (index > 0) result = result.substring(0, index);
		return Integer.parseInt(result);
	}

	/**
	 * 百分比 有小数部分，则加1
	 * @param a float
	 * @param b float
	 * @return String
	 */
	public static final int getPerCentIntCeil(float a, float b) {
		if(a==0)return 0;
		String result = getPerCent(a, b);
		int index = result.indexOf('.');
		if (index == 0) return 1;
		if (index > 0) {
			result = result.substring(0, index);
			return Integer.parseInt(result) + 1;
		} else {
			return Integer.parseInt(result);
		}
	}

	public static void main(String[] args) {
		int a = 50, b = 60;
		String result = getPerCent(a, b);
		System.out.println("Hello World!" + result);
		System.out.println("Hello World!" + getPerCentInt(a, b));
	}
}
