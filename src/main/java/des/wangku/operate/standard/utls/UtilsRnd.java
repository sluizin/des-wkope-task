package des.wangku.operate.standard.utls;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 随机数
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class UtilsRnd {
	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(UtilsRnd.class);
	static Random rd1 = new Random();
	private static final char[] ArrayNull = new char[0];

	/**
	 * 构造函数
	 */
	public UtilsRnd() {
		rd1 = new Random();

	}

	/**
	 * 得到0-9随机数<br>
	 * @return int
	 */
	public static final int getRndNum() {
		return rd1.nextInt(10);
	}

	/**
	 * 得到随机真假值
	 * @return boolean
	 */
	public static final boolean getRndBoolean() {
		return getRndNum() > 4;
	}

	/**
	 * 得到min-max之间的随机数
	 * @param min int
	 * @param max int
	 * @return int
	 */
	public static final int getRndInt(final int min, final int max) {
		return min + rd1.nextInt(max - min + 1);
	}

	/**
	 * 得到min-max之间的随机数
	 * @param min float
	 * @param max float
	 * @return float
	 */
	public static final float getRndFloat(final float min, final float max) {
		return min + rd1.nextFloat() * (max - min);
	}

	/**
	 * 得到min-max之间的随机数
	 * @param min long
	 * @param max long
	 * @return long
	 */
	public static final long getRndLong(final long min, final long max) {
		return min + rd1.nextLong() * (max - min);
	}

	/**
	 * 百分比，得到是否选中
	 * @param v float
	 * @return boolean
	 */
	public static final boolean getRndProbability100(float v) {
		if (v <= 0) return false;
		if (v >= 100) return true;
		float val = getRndFloat(0, 100f);
		if (val <= v) return true;
		return false;
	}

	/**
	 * 千分比，得到是否选中
	 * @param v float
	 * @return boolean
	 */
	public static final boolean getRndProbability1000(float v) {
		if (v <= 0) return false;
		if (v >= 1000) return true;
		float val = getRndFloat(0, 1000f);
		if (val <= v) return true;
		return false;
	}

	/**
	 * 通过输入多个数值，得到随机选中的下标
	 * @param arrs float[]
	 * @return int
	 */
	public static final int getRndProbability(float... arrs) {
		if (arrs.length == 0) return -1;
		float sort = 0f;
		for (float e : arrs)
			if (e > 0f) sort += e;
		if (sort == 0f) return -1;
		float val = getRndFloat(0, sort);
		for (int i = 0; i < arrs.length; i++) {
			float e = arrs[i];
			if (e <= 0f) continue;
			if ((val -= e) <= 0f) return i;
		}
		return -1;
	}

	/**
	 * outType:<br>
	 * 1://随机一位数字 0 - 9<br>
	 * 2://随机一位小写字母 a - z<br>
	 * 3://随机一位大写字母 A - Z<br>
	 * 4://随机一位数字或小写 0 - 9 a - z<br>
	 * 5://随机一位大或小写字母 a - z A-Z<br>
	 * default://随机一位数字、小写、大写字母 0 - 9 a - z A - Z<br>
	 * @param type int
	 * @return String
	 */
	public static final char getRndCharacter(final int type) {
		switch (type) {
		case 1:// 随机一位数字 0 - 9
			return (char) (getRndNum() + 48);
		case 2:// 随机一位小写字母 a - z
			return (char) getRndInt(97, 122);
		case 3:// 随机一位大写字母 A - Z
			return (char) getRndInt(65, 90);
		case 4:// 随机一位数字或小写 0 - 9 a - z
			if (getRndInt(1, 2) == 1) return (char) getRndNum();
			return (char) getRndInt(97, 122);
		case 5:// 随机一位大或小写字母 a - z A-Z
			if (getRndInt(1, 2) == 1) return (char) getRndInt(97, 122);
			return (char) getRndInt(97, 122);
		case 6:// 随机一位数字或小写 A - Z 0 - 9
			if (getRndInt(1, 2) == 1) return (char) getRndNum();
			return (char) getRndInt(97, 122);
		default:// 随机一位数字、小写、大写字母 0 - 9 a - z A - Z
			int i = getRndInt(1, 3);
			if (i == 1) return (char) getRndNum();
			if (i == 2) return (char) getRndInt(97, 122);
			return (char) getRndInt(97, 122);
		}
	}

	/**
	 * // 得到一个多少位的字串(String)<br>
	 * // 1:全是0-9<br>
	 * // 2:全是a-z<br>
	 * // 3:全是A-Z<br>
	 * // 4:全是0-9 a-z<br>
	 * // 5:全是a-z A-Z<br>
	 * // 6:全是A-Z 0-9<br>
	 * // 7:全是0-9 a-z A-Z<br>
	 * //
	 * @param len int
	 * @param type int
	 * @return char[]
	 */
	public static final char[] getRndCharacters(final int len, final int type) {
		if (len <= 0) return ArrayNull;
		final char[] newarray = new char[len];
		for (int i = 0; i < len; i++) {

			newarray[i] = getRndCharacter(type);
		}
		return newarray;
	}

	/**
	 * // 得到一个多少位的字串(String)<br>
	 * // 1:全是0-9<br>
	 * // 2:全是a-z<br>
	 * // 3:全是A-Z<br>
	 * // 4:全是0-9 a-z<br>
	 * // 5:全是a-z A-Z<br>
	 * // 6:全是A-Z 0-9<br>
	 * // 7:全是0-9 a-z A-Z<br>
	 * //
	 * @param len int
	 * @param type int
	 * @return String
	 */
	public static final String getRndString(final int len, final int type) {
		return new String(getRndCharacters(len, type));
	}

	/**
	 * 得到新文件名，以日期加N位其它符号组成<br>
	 * 20180601092822 az42<br>
	 * // 1:全是0-9<br>
	 * // 2:全是a-z<br>
	 * // 3:全是A-Z<br>
	 * // 4:全是0-9 a-z<br>
	 * // 5:全是a-z A-Z<br>
	 * // 6:全是A-Z 0-9<br>
	 * // 7:全是0-9 a-z A-Z<br>
	 * @param len int
	 * @param type int
	 * @return String
	 */
	public static final String getNewFilenameNow(final int len, final int type) {
		return UtilsDate.getDateTimeNow("yyyyMMddHHmmss") + getRndString(len, type);
	}
}
