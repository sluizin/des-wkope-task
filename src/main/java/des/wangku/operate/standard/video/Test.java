package des.wangku.operate.standard.video;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.github.sarxos.webcam.WebcamUtils;
import com.github.sarxos.webcam.util.ImageUtils;

public class Test {
    private static int    num    = 0;

    public static void main(String[] args) throws IOException
    {
        final Webcam webcam = Webcam.getDefault();
        webcam.setViewSize(WebcamResolution.VGA.getSize());
        WebcamPanel panel = new WebcamPanel(webcam);
        panel.setFPSDisplayed(true);
        panel.setDisplayDebugInfo(true);
        panel.setImageSizeDisplayed(true);
        panel.setMirrored(true);

        final JFrame window = new JFrame("摄像头");
        window.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosed(WindowEvent e)
            {
                webcam.close();
                window.dispose();
            }
        });
        // window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final JButton button = new JButton("截图");
        window.add(panel, BorderLayout.CENTER);
        window.add(button, BorderLayout.SOUTH);
        window.setResizable(true);
        window.pack();
        window.setVisible(true);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                button.setEnabled(false);
                String fileName = "D://" + num;
                WebcamUtils.capture(webcam, fileName, ImageUtils.FORMAT_PNG);
                
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run()
                    {
                        JOptionPane.showMessageDialog(null, "截图成功");
                        button.setEnabled(true);
                        num++;
                        return;
                    }
                });
            }
        });
    }
}
