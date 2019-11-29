package des.wangku.operate.standard.utls;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;

/**
 * 验证码图片
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class UtilsValidateCode {
	public static final List<String> ACC_FilterKey = new ArrayList<>();
	static {
		ACC_FilterKey.add("SUNJ");
		ACC_FilterKey.add("SUNJI");
		ACC_FilterKey.add("SUNJIA");
	}
	// 图片的宽度。
	private int width = 160;
	// 图片的高度。
	private int height = 40;
	// 验证码字符个数
	private int codeCount = 5;
	// 验证码干扰线数
	private int lineCount = 150;
	// 验证码
	private String code = null;
	// 验证码图片Buffer
	private BufferedImage buffImg = null;
	// 验证码范围,去掉0(数字)和O(拼音)容易混淆的(小写的1和L也可以去掉,大写不用了)
	private static final char[] codeSequence = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

	/**
	 * 默认构造函数,设置默认参数
	 */
	public UtilsValidateCode() {
		this(160,40, 5, 150);
	}

	/**
	 * @param width 图片宽
	 * @param height 图片高
	 */
	public UtilsValidateCode(int width, int height) {
		this(width,height, 5, 150);
	}

	/**
	 * @param width 图片宽
	 * @param height 图片高
	 * @param codeCount 字符个数
	 * @param lineCount 干扰线条数
	 */
	public UtilsValidateCode(int width, int height, int codeCount, int lineCount) {
		this.width = width;
		this.height = height;
		this.codeCount = codeCount;
		this.lineCount = lineCount;
		this.createCode();
	}

	/**
	 * 建立图片
	 */
	public void createCode() {
		int x = 0, fontHeight = 0, codeY = 0;
		int red = 0, green = 0, blue = 0;
		x = width / (codeCount + 2);//每个字符的宽度(左右各空出一个字符)
		fontHeight = height - 2;//字体的高度
		codeY = height - 4;
		// 图像buffer
		buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = buffImg.createGraphics();
		/*
		 * // 将图像背景填充为白色
		 * g.setColor(Color.WHITE);
		 * g.fillRect(0, 0, width, height);
		 */
		// 增加下面代码使得背景透明
		buffImg = g.getDeviceConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT);
		g.dispose();
		g = buffImg.createGraphics();
		// 背景透明代码结束
		// 画图BasicStroke是JDK中提供的一个基本的画笔类,我们对他设置画笔的粗细，就可以在drawPanel上任意画出自己想要的图形了。  
		g.setColor(new Color(255, 0, 0));
		g.setStroke(new BasicStroke(1f));
		g.fillRect(128, 128, width, height);
		// 生成随机数
		Random random = new Random();
		//设置字体类型、字体大小、字体样式
		Font font = new Font("微软雅黑", Font.PLAIN, fontHeight);
		g.setFont(font);
		for (int i = 0; i < lineCount; i++) {
			// 设置随机开始和结束坐标
			int xs = random.nextInt(width);//x坐标开始
			int ys = random.nextInt(height);//y坐标开始
			int xe = xs + random.nextInt(width / 8);//x坐标结束
			int ye = ys + random.nextInt(height / 8);//y坐标结束
			// 产生随机的颜色值，让输出的每个干扰线的颜色值都将不同。
			red = random.nextInt(255);
			green = random.nextInt(255);
			blue = random.nextInt(255);
			g.setColor(new Color(red, green, blue));
			g.drawLine(xs, ys, xe, ye);
		}
		// randomCode记录随机产生的验证码
		StringBuffer randomCode = new StringBuffer();
		// 随机产生codeCount个字符的验证码。
		while (true) {
			for (int i = 0; i < codeCount; i++) {
				String strRand = String.valueOf(codeSequence[random.nextInt(codeSequence.length)]);
				// 产生随机的颜色值，让输出的每个字符的颜色值都将不同。
				red = random.nextInt(255);
				green = random.nextInt(255);
				blue = random.nextInt(255);
				//指定某种颜色
				//g.setColor(new Color(252, 145, 83));
				g.setColor(new Color(red, green, blue));
				g.drawString(strRand, (i + 1) * x, codeY);
				// 将产生的四个随机数组合在一起。
				randomCode.append(strRand);
			}
			String codetemp = randomCode.toString();
			if (!isFilterCode(codetemp)) break;
		}
		// 将四位数字的验证码保存到Session中。
		code = randomCode.toString();
	}

	/**
	 * 判断是否为特殊过滤字符串
	 * @param code String
	 * @return boolean
	 */
	public static final boolean isFilterCode(String code) {
		return ACC_FilterKey.contains(code);
	}
	/**
	 * 添加多个过滤字符串
	 * @param arrs String[]
	 */
	public static final void addFilter(String... arrs) {
		if (arrs.length == 0) return;
		for (String e : arrs) {
			if (!ACC_FilterKey.contains(e)) ACC_FilterKey.add(e);
		}
	}


	/**
	 * 写文件
	 * @param path String
	 */
	public void write(String path) {
		try {
			OutputStream sos = new FileOutputStream(path);
			this.write(sos);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 写输出流
	 * @param os OutputStream
	 */
	public void write(OutputStream os) {
		try {
			ImageIO.write(buffImg, "png", os);
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取InputStream
	 * @return InputStream
	 */
	public InputStream getInputStream() {
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			ImageIO.write(buffImg, "png", os);
			InputStream is = new ByteArrayInputStream(os.toByteArray());
			return is;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取byte[]
	 * @return byte[]
	 */
	public byte[] getByteArray() {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];
		int n = 0;
		try {
			InputStream input = getInputStream();
			while (-1 != (n = input.read(buffer))) {
				output.write(buffer, 0, n);
			}
			return output.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			byte[] na = {};
			return na;
		}
	}

	/**
	 * 获取base64字符串
	 * @return String
	 */
	public String toBase64() {
		String base = Base64.encodeBase64String(getByteArray());
		return base;
	}

	public BufferedImage getBuffImg() {
		return buffImg;
	}

	public String getCode() {
		return code;
	}

	/**
	 * 测试函数,默认生成到d盘
	 * @param args String[]
	 */
	public static void main(String[] args) {
		UtilsValidateCode vCode = new UtilsValidateCode(160, 40, 4, 150);
		String path = "D:/" + new Date().getTime() + ".png";
		System.out.println(vCode.getCode() + " >" + path);
		vCode.write(path);
	}
}
