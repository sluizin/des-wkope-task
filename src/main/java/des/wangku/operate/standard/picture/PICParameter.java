package des.wangku.operate.standard.picture;

import java.awt.Color;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 参数
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class PICParameter {
	int width = 100;

	int height = 100;

	int x = 0;

	int y = 0;

	String bgcolor = "000000";

	public PICParameter() {

	}

	public PICParameter(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	/**
	 * 得到Color对象
	 * @return Color
	 */
	public Color getBgColor() {
		if (bgcolor == null || bgcolor.length() == 0) return null;
		try {
			Color c = new Color(Integer.parseInt(bgcolor, 16));
			return c;
		} catch (Exception f) {
			return null;
		}
	}

	public final String getBgcolor() {
		return bgcolor;
	}

	public final void setBgcolor(String bgcolor) {
		this.bgcolor = bgcolor;
	}

	public final int getHeight() {
		return height;
	}

	public final int getWidth() {
		return width;
	}

	public final int getX() {
		return x;
	}

	public final int getY() {
		return y;
	}

	public final void setHeight(int height) {
		this.height = height;
	}

	public final void setWidth(int width) {
		this.width = width;
	}

	public final void setX(int x) {
		this.x = x;
	}

	public final void setY(int y) {
		this.y = y;
	}

	/** 日志 */
	static final Logger logger = LoggerFactory.getLogger(PICParameter.class);

}
