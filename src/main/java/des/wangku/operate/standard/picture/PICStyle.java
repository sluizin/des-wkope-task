package des.wangku.operate.standard.picture;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 图片样式
 * 
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class PICStyle {
	/** 日志 */
	static final Logger logger = LoggerFactory.getLogger(PICStyle.class);
	/**
	 * 画圆，是否需要透明
	 * @param buffImg BufferedImage
	 * @param transparent boolean
	 * @return BufferedImage
	 */
	public static final BufferedImage Graphics2DPictureCircular(final BufferedImage buffImg, final boolean transparent) {
		int width = buffImg.getWidth();
		int height = buffImg.getHeight();
		int TYPE = BufferedImage.TYPE_INT_RGB;
		if (transparent) TYPE = BufferedImage.TYPE_4BYTE_ABGR;
		BufferedImage bi = new BufferedImage(width, height, TYPE);
		Ellipse2D.Double shape = new Ellipse2D.Double(0, 0, width, height);
		Graphics2D g = bi.createGraphics();
		if (!transparent) {
			g.setComposite(AlphaComposite.getInstance(10, 1.0f));
			g.fill(new Rectangle(width, height));
		}
		g.setClip(shape);
		g.drawImage(buffImg, 0, 0, null);
		g.dispose();
		return bi;
	}	
	
	
	
	public static final BufferedImage Graphics2DPicture5PStar(final BufferedImage buffImg) {
		int width = buffImg.getWidth();
		int height = buffImg.getHeight();
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		@SuppressWarnings("unused")
		Ellipse2D.Double shape = new Ellipse2D.Double(0, 0, width, height);
		Graphics2D g = bi.createGraphics();
		g.drawImage(buffImg, 0, 0, null);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

		
		g.setPaint(Color.WHITE);
		g.fillRect(0, 0, width, height);

		//10个顶点的坐标
		//注意：这里是以中心为原点，X轴向右，Y轴向上的坐标轴，标准的半径为1
		Point2D.Double pA=new Point2D.Double(0, 1);
		Point2D.Double pB=new Point2D.Double(-0.95106, 0.30902);
		Point2D.Double pC=new Point2D.Double(-0.58779, -0.80902);
		Point2D.Double pD=new Point2D.Double(0.58779, -0.80902);
		Point2D.Double pE=new Point2D.Double(0.95106, 0.30902);
		Point2D.Double pF=new Point2D.Double(0, -0.38197);
		Point2D.Double pG=new Point2D.Double(0.36328, -0.11804);
		Point2D.Double pH=new Point2D.Double(0.22452, 0.30902);
		Point2D.Double pI=new Point2D.Double(-0.22452, 0.30902);
		Point2D.Double pJ=new Point2D.Double(-0.36328, -0.11804);
		
		Point2D.Double[] points= {pA,pI,pB,pJ,pC,pF,pD,pG,pE,pH};

		Rectangle rect=new Rectangle(30,30,200,200);
		int radius_x=rect.width/2;
		int radius_y=rect.height/2;
		
		for(Point2D.Double point:points)
		{
			point.x=rect.getCenterX()+radius_x*point.x;
			point.y=rect.getCenterY()-radius_y*point.y;  //坐标反转
			
		}
		
		//圆五角星
		Path2D outline=new Path2D.Double();
		outline.moveTo(points[0].x, points[0].y);
		for(int i=1;i<points.length;i++)
		{
			outline.lineTo(points[i].x, points[i].y);
		}
		outline.closePath();
		g.setPaint(Color.RED);
		g.fill(outline);
		//画图
		g.setPaint(Color.GREEN);
		Shape circle=new Ellipse2D.Double(rect.x, rect.y, rect.width, rect.height);
		g.draw(circle);

		
		
		//g.setClip(shape);
		//g.drawImage(buffImg, 0, 0, null);
		//g.dispose();
		return bi;
		
		
		
	
	}
	/**
	 * 把图片比例缩放
	 * @param buffImg BufferedImage
	 * @param p PicPointState
	 * @return BufferedImage
	 */
	public static BufferedImage Graphics2DPictureAlignment(BufferedImage buffImg, PICParameter p) {
		int TYPE = BufferedImage.TYPE_4BYTE_ABGR;
		if (p.bgcolor != null) TYPE = BufferedImage.TYPE_INT_RGB;
		BufferedImage bi = new BufferedImage(p.width, p.height, TYPE);
		Graphics2D g = bi.createGraphics();
		Color col=p.getBgColor();
		if (col != null) {
			g.setColor(col);
			g.fillRect(0, 0, p.width, p.height);
		}
		g.drawImage(buffImg, p.x, p.y, p.width, p.height, null);
		g.dispose();
		return bi;
	}
	public static void main(String[] args) 
	{
		System.out.println("Hello World!");
		String filename="f:/abc.files/Img438943925.png";
		try {
		BufferedImage logo = ImageIO.read(new File(filename));
		//BufferedImage t=Graphics2DPictureCircular(logo,true);
		BufferedImage t=Graphics2DPicture5PStar(logo);
		System.out.println("t:"+t.getWidth());
		File file =new File("f:/abc.files/Img438943925_2.png");
		ImageIO.write(t, "png", file); 
		} catch (Exception ex) {
			logger.info(ex.toString());
		}
		 
	}
}
