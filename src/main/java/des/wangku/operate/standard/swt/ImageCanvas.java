package des.wangku.operate.standard.swt;

import java.net.URL;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wb.swt.SWTResourceManager;

public class ImageCanvas extends Canvas {  // 显示的图像
	private Image image;
	// 图像缩放比例
	private float zoom = 1f;

	/**
	 * @param parent Composite
	 * @param style int
	 * @param image 显示的图像，为null时不显示
	 */
	public ImageCanvas(Composite parent, int style, Image image) {
		super(parent, style);
		this.image = image;

		addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				// 调用重绘方法
				paintImage(e.gc);
			}
		});
	}

	/**
	 * 
	 * @param parent Composite
	 * @param style int
	 * @param url URL
	 */
	public ImageCanvas(Composite parent, int style, URL url) {
		this(parent, style, SWTResourceManager.getImage(url));
	}

	/**
	 * 重绘图像,窗口区域变化时都重新计算适合的显示位置，以保证图像居中完整显示
	 * @param gc
	 */
	protected void paintImage(GC gc) {
		if (null == image) return;
		zoom = fitZoom();
		Rectangle rect = getPaintRect();
		gc.drawImage(image, 0, 0, image.getBounds().width, image.getBounds().height, rect.x, rect.y, rect.width, rect.height);
	}

	/**
	 * 返回适合当前窗口尺寸完整显示图像的缩放比例,图像长宽都小于显示窗口时，则返回1
	 * @return
	 */
	private float fitZoom() {
		Point size = getSize();
		Rectangle imgSize = image.getBounds();
		if (imgSize.width < size.x && imgSize.height < size.y) return 1f;
		if (imgSize.width * size.y < imgSize.height * size.x) { return (float) size.y / imgSize.height; }
		return (float) size.x / imgSize.width;
	}

	/**
	 * 根据图像缩放比例返回图像在gc中的显示区域(居中显示)
	 * @return
	 */
	private Rectangle getPaintRect() {
		Point size = getSize();
		Rectangle imgSize = image.getBounds();
		if (zoom > 0) {
			imgSize.width *= zoom;
			imgSize.height *= zoom;
		}
		imgSize.x = (size.x - imgSize.width) / 2;
		imgSize.y = (size.y - imgSize.height) / 2;
		return imgSize;
	}

	@Override
	public void dispose() {
		super.dispose();
		image.dispose();
	}
}
