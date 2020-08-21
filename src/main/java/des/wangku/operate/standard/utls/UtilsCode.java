package des.wangku.operate.standard.utls;

import java.io.StringReader;
/**
 * 编码转换
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 *
 */
public final class UtilsCode {
	/**
	 * RandomAccessFile RandomAccessFile读出时，转换成UTF-8
	 * @param line String
	 * @return String
	 */
	public static final String changedLine(final String line) {
		return changedLine(line,"UTF-8");
	}
	/**
	 * RandomAccessFile RandomAccessFile读出时，转换编码
	 * @param line String
	 * @param code String
	 * @return String
	 */
	public static final String changedLine(final String line,String code) {
		if (line == null) return null;
		try {
			byte buf[] = new byte[1];
			byte[] byteArray = new byte[line.length()];
			StringReader aStringReader = new StringReader(line);
			int character;
			int i = 0;
			while ((character = aStringReader.read()) != -1)
				byteArray[i++] = buf[0] = (byte) character;
			return new String(byteArray, code);
		} catch (Exception e) {
		}
		return null;
	}
}
