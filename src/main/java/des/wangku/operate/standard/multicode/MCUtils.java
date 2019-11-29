package des.wangku.operate.standard.multicode;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import des.wangku.operate.standard.multicode.MultiCode.LogoParameter;
import des.wangku.operate.standard.multicode.MultiCode.MCParameter;
import des.wangku.operate.standard.utls.UtilsColor;

/**
 * 工具类
 * 
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class MCUtils {
	/** 日志 */
	static final Logger logger = LoggerFactory.getLogger(MCUtils.class);

	/**
	 * 0:L<br>
	 * 1:M<br>
	 * 2:Q<br>
	 * 3:H<br>
	 * default:L
	 * @param index int
	 * @return ErrorCorrectionLevel
	 */
	public static final ErrorCorrectionLevel getECLevel(int index) {
		//ErrorCorrectionLevel.L //  7%
		//ErrorCorrectionLevel.M //  15%
		//ErrorCorrectionLevel.Q //  25%
		//ErrorCorrectionLevel.H //  30%
		switch (index) {
		case 1:
			return ErrorCorrectionLevel.M;
		case 2:
			return ErrorCorrectionLevel.Q;
		case 3:
			return ErrorCorrectionLevel.H;
		default:
			return ErrorCorrectionLevel.L;
		}
	}
	/**
	 * 通过BarcodeFormat的枚举编号获取BarcodeFormat枚举<br>
	 * 默认返回:QR_CODE 11
	 * @param index int
	 * @return BarcodeFormat
	 */
	public static final BarcodeFormat getBarcodeFormat(int index) {
		if(index<0)return BarcodeFormat.QR_CODE;
		BarcodeFormat[] arrs=BarcodeFormat.values();
		if(index>arrs.length)return BarcodeFormat.QR_CODE;
		int i=0;
		for (BarcodeFormat e :arrs) { 
		   	if((i++)==index)return e; 
		}
		return BarcodeFormat.QR_CODE;
	}
	/**
	 * 通过BarcodeFormat的名称字符串获取BarcodeFormat枚举<br>
	 * 默认返回:QR_CODE
	 * @param value String
	 * @return BarcodeFormat
	 */
	public static final BarcodeFormat getBarcodeFormat(String value) {
		if(value==null || value.length()==0)return BarcodeFormat.QR_CODE;
		value=value.toLowerCase();
		for (BarcodeFormat e : BarcodeFormat.values()) { 
		   	if(e.toString().toLowerCase().equals(value))return e; 
		}
		return BarcodeFormat.QR_CODE;
	}
	/**
	 * 判断字符是否是url
	 * @param urlStr String
	 * @return URL
	 */
	public static synchronized URL isConnect(String urlStr) {
		if (urlStr == null || urlStr.length() <= 0) return null;
		HttpURLConnection con;
		int state = -1;
		try {
			URL url = new URL(urlStr);
			con = (HttpURLConnection) url.openConnection();
			state = con.getResponseCode();
			if (state != 200) return null;
			HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();
			urlconnection.connect();
			BufferedInputStream bis = new BufferedInputStream(urlconnection.getInputStream());
			String typeStream = HttpURLConnection.guessContentTypeFromStream(bis);
			if (urlconnection.getContentLengthLong() > MCConst.ACC_QC_PICTURE_CAPACITY_MAX) {
				logger.info("Error file capacity(" + MCConst.ACC_QC_PICTURE_CAPACITY_MAX + "):" + urlconnection.getContentLengthLong());
				return null;
			}
			if (typeStream.substring(0, 6).indexOf("image/") == 0) return url;
			logger.info("Error file type:" + HttpURLConnection.guessContentTypeFromStream(bis));
			return null;
		} catch (Exception ex) {
			logger.info(ex.toString());
		}
		return null;
	}

	/**
	 * 得到Color对象
	 * @return Color
	 */
	public static final Color getReadColor(String color,Color def) {
		Color colr = UtilsColor.getColor(color);
		return (colr == null) ?def: colr;
	}

	/**
	 * 二维码图片添加Logo
	 * @param imgbi 全图流
	 * @param logoParameter LogoParameter
	 * @throws Exception 异常上抛
	 */
	public static void addLogo(BufferedImage imgbi, LogoParameter logoParameter) throws Exception {
		if (imgbi == null || logoParameter == null || logoParameter.getLogoPath() == null) return;
		try {
			URL url = MCUtils.isConnect(logoParameter.logoPath);
			if (url == null) return;
			// 对象流传输
			BufferedImage image = imgbi;
			Graphics2D g = image.createGraphics();
			// 读取Logo图片
			BufferedImage logo = ImageIO.read(url);
			// 设置logo的大小,本人设置为二维码图片的20%,因为过大会盖掉二维码
			int percent = logoParameter.getLogopercent();
			int widthLogo = logo.getWidth(null) > image.getWidth() * percent / 100 ? (image.getWidth() * percent / 100) : logo.getWidth(null);
			int heightLogo = logo.getHeight(null) > image.getHeight() * percent / 100 ? (image.getHeight() * percent / 100) : logo.getWidth(null);
			// 计算图片放置位置
			// logo放在中心
			int x = (image.getWidth() - widthLogo) / 2;
			int y = (image.getHeight() - heightLogo) / 2;
			// 开始绘制图片
			g.drawImage(logo, x, y, widthLogo, heightLogo, null);
			g.drawRoundRect(x, y, widthLogo, heightLogo, 15, 15);
			g.setStroke(new BasicStroke(logoParameter.getBorder()));
			g.setColor(getReadColor(logoParameter.getBorderColor(),Color.BLACK));
			g.drawRect(x, y, widthLogo, heightLogo);
			g.dispose();
			logo.flush();
			image.flush();
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 生成图形码bufferedImage图片
	 * @param para Parameter
	 * @return BufferedImage
	 */
	public static BufferedImage getMC_CODEBufferedImage(MCParameter para) {
		// Google配置文件
		MultiFormatWriter multiFormatWriter = null;
		BitMatrix bm = null;
		BufferedImage image = null;
		try {
			multiFormatWriter = new MultiFormatWriter();
			BarcodeFormat bcf=MCUtils.getBarcodeFormat(para.getBcfIndex());
			// 参数顺序分别为：编码内容，编码类型，生成图片宽度，生成图片高度，设置参数  zxingconfig.getBarcodeformat()
			bm = multiFormatWriter.encode(para.getContent(), bcf, para.getWidth(), para.getHeight(), para.getHints());
			int w = bm.getWidth();
			int h = bm.getHeight();
			image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
			Color colr=getReadColor(para.getColor(),Color.BLACK);
			// 开始利用二维码数据创建Bitmap图片，分别设为黑白两色
			for (int x = 0; x < w; x++) {
				for (int y = 0; y < h; y++) {
					image.setRGB(x, y, bm.get(x, y) ? colr.getRGB() : Color.WHITE.getRGB());
				}
			}
			// 是否设置Logo图片
			if (para.isLogoFlg()) {
				addLogo(image, para.getLogoParameter());
			}
		} catch (Exception e) {
			logger.debug("error:" + e);
		}
		return image;
	}

}
