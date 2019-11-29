package des.wangku.operate.standard.utls;

import java.awt.Color;

/**
 * 颜色的一些方法
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class UtilsColor {
	/**
	 * 把#0f0f0f 或 1e1e1e 转成颜色Color
	 * @param color String
	 * @return Color
	 */
	public static final Color getColor(String color) {
		if (color == null || (color.length() != 6 && color.length() != 7)) return null;
		boolean isColor = UtilsVerification.isColor(color);
		if (!isColor) return null;
		if (color.length() == 7) color = color.substring(1);
		return new Color(Integer.parseInt(color, 16));
	}
}
