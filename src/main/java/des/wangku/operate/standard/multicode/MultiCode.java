package des.wangku.operate.standard.multicode;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.google.zxing.EncodeHintType;

/**
 * 二维码
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class MultiCode {
	/**
	 * 图片默认配置
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.7
	 */
	public static class LogoParameter {
		int border = MCConst.ACC_QC_DEFAULT_BORDER;  // 默认边框宽度
		int logopercent = 20;
		String borderColor = "000000";    // 边框颜色
		int logoPart = 2; //  边框外围宽度
		String logoPath = null;    // Logo图片路径

		/**
		 * 二维码无参构造函数 默认设置Logo图片底色白色宽度2
		 */
		public LogoParameter() {
			this(null, MCConst.ACC_QC_DEFAULT_BORDERCOLOR, 20, 2);
		}

		public LogoParameter(String logoPath) {
			this.logoPath = logoPath;
		}

		/**
		 * 二维码有参构造函数
		 * @param borderColor 边框颜色
		 * @param logoPart 边框宽度
		 */
		public LogoParameter(String logoPath, String borderColor, int logopercent, int logoPart) {
			// 设置边框
			this.borderColor = borderColor;
			this.logoPath = logoPath;
			this.logopercent = logopercent;
			// 设置边框宽度
			this.logoPart = logoPart;
		}

		/**
		 * 获取边框
		 * @return 获取边框
		 */
		public int getBorder() {
			return border;
		}

		public final String getBorderColor() {
			return borderColor;
		}

		public BufferedImage getImageBuffered() {
			InputStream is2 = MultiCode.class.getResourceAsStream("/com/maqiao/was/multicode/qc/kg_logo.png");
			if (is2 == null) return null;
			try {
				return ImageIO.read(is2);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		/**
		 * 外围边宽
		 * @return 外围边宽
		 */
		public int getLogoPart() {
			return logoPart;
		}

		public final String getLogoPath() {
			return logoPath;
		}

		/**
		 * 介于40-20之间
		 * @return int
		 */
		public final int getLogopercent() {
			if (logopercent >= 41 || logopercent <= 20) return 20;
			return logopercent;
		}

		public final void setBorder(int border) {
			this.border = border;
		}

		public final void setBorderColor(String borderColor) {
			this.borderColor = borderColor;
		}

		public final void setLogoPart(int logoPart) {
			this.logoPart = logoPart;
		}

		public final void setLogoPath(String logoPath) {
			this.logoPath = logoPath;
		}

		public final void setLogopercent(int logopercent) {
			this.logopercent = logopercent;
		}

	}

	/**
	 * 图形码参数
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 */
	public static class MCParameter {
		String color = "000000";
		String content = null; // 二维码编码内容

		/** 编码类型 */
		int bcfIndex = 11;
		int width = 100;    // 生成图片宽度
		int height = 100;   //  生成图片高度
		/** 容错度 0 - 3 */
		int eclIndex = 0;
		LogoParameter logoParameter = null;  // logo图片参数

		/**
		 * 构造函数
		 */
		public MCParameter() {
		}

		/**
		 * 构造函数
		 */
		public MCParameter(String logoPath) {
			this.logoParameter = new LogoParameter(logoPath);
		}

		public final int getBcfIndex() {
			return bcfIndex;
		}

		public final String getColor() {
			return color;
		}

		public final String getContent() {
			return content;
		}

		public final int getEclIndex() {
			return eclIndex;
		}

		public final int getHeight() {
			if (height < MCConst.ACC_QC_Height_MIN) return MCConst.ACC_QC_Height_MIN;
			return height;
		}

		/**
		 * 获取 设置参数<br>
		 * 判断字符串长度<br>
		 * 如果字符串小于64位，则使用高纠错H
		 * 否则返回Q
		 * @return hints 设置参数
		 */
		public Map<EncodeHintType, ?> getHints() {
			Map<EncodeHintType, Object> hints = new HashMap<>();
			hints.put(EncodeHintType.ERROR_CORRECTION, MCUtils.getECLevel(eclIndex));
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			hints.put(EncodeHintType.MARGIN, 0);
			return hints;
		}
		public final LogoParameter getLogoParameter() {
			return logoParameter;
		}

		/**
		 * 得到宽度但限制最小值100
		 * @return int
		 */
		public final int getWidth() {
			if (width < MCConst.ACC_QC_Width_MIN) return MCConst.ACC_QC_Width_MIN;
			return width;
		}

		public boolean isLogoFlg() {
			if (logoParameter == null) return false;
			if (logoParameter.logoPath == null) return false;
			return true;
		}

		public final MCParameter setBcfIndex(int bcfIndex) {
			this.bcfIndex = bcfIndex;
			return this;
		}
		/*
		 * public BarcodeFormat getBarcodeformat() {
		 * return MCUtils.getBarcodeFormat(bcfIndex);
		 * }
		 */

		public final void setColor(String color) {
			this.color = color;
		}

		public final MCParameter setContent(String content) {
			this.content = content;
			return this;
		}

		public final MCParameter setEclIndex(int eclIndex) {
			this.eclIndex = eclIndex;
			return this;
		}

		public final void setHeight(int height) {
			this.height = height;
		}

		public final void setLogoParameter(LogoParameter logoParameter) {
			this.logoParameter = logoParameter;
		}

		public MCParameter setSize(int width, int height) {
			this.width = width;
			this.height = height;
			return this;
		}

		public final void setWidth(int width) {
			this.width = width;
		}

		/**
		 * 把本参数转成String字符串
		 * @return String
		 */
		public final String toJson() {
			return JSON.toJSONString(this);
		}
	}

	/** 日志 */
	static final Logger logger = LoggerFactory.getLogger(MultiCode.class);

	/**
	 * 得到Parameter的byte[]
	 * @param para Parameter
	 * @return byte[]
	 */
	public static final byte[] toByte(MCParameter para) {
		try {
			BufferedImage bim = MCUtils.getMC_CODEBufferedImage(para);// 生成二维码
			bim.flush();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ImageIO.write(bim, "png", out);
			return out.toByteArray();
		} catch (Exception e) {
			return new byte[0];
		}
	}

	public static final InputStream toIS(MCParameter para) {
		byte[] by = toByte(para);
		if (by.length == 0) return null;
		return new ByteArrayInputStream(by);
	}

	public static final InputStream toIS(String paraJson) {
		if (paraJson == null || paraJson.length() == 0) return null;
		MCParameter para = JSON.parseObject(paraJson, MCParameter.class);
		return toIS(para);
	}
}
