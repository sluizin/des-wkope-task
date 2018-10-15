package des.wangku.operate.standard.listener;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

/**
 * 悬浮框
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class SupensionFrame implements MouseTrackListener {
	Display display = null;
	String message = "";
	/**
	 * 提示性悬浮框
	 * @param display Display
	 * @param message String
	 */
	public SupensionFrame(Display display, String message) {
		this.message = message;
		this.display = display;
	}

	public class SuspensionFrame implements ActionListener {
		JWindow window1 = new JWindow();
		JPanel panel = new JPanel();
		JLabel label01 = null;
		Color color;

		public SuspensionFrame(Point point, String message) {
			if (message == null) message = "";
			label01 = new JLabel(message);
			int width = label01.getFontMetrics(label01.getFont()).stringWidth(label01.getText());
			int height = label01.getFontMetrics(label01.getFont()).getHeight();
			window1.setLocation(point.x + 5, point.y + 5);
			window1.setSize(width + 20, height + 20);
			window1.getContentPane().setLayout(null);
			window1.getContentPane().add(panel);
			window1.setVisible(true);
			panel.setLayout(null);
			panel.setLocation(0, 0);
			panel.setSize(400, 300);
			label01.setLocation(5, 5);
			label01.setSize(width + 20, height + 20);

			panel.add(label01);
			color = new Color(52, 32, 63);
			label01.setForeground(Color.red);
			window1.setAlwaysOnTop(true);
		}

		@Override
		public void actionPerformed(ActionEvent e) {

		}

		public void close() {
			window1.dispose();
		}

	}

	SuspensionFrame s = null;

	@Override
	public void mouseEnter(MouseEvent e) {
		@SuppressWarnings("static-access")
		Point point = display.getCurrent().getCursorLocation();
		s = new SuspensionFrame(point, message);
	}

	@Override
	public void mouseExit(MouseEvent e) {
		s.close();

	}

	@Override
	public void mouseHover(MouseEvent e) {
	}

}
