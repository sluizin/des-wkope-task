package des.wangku.operate.standard.utls;

import java.applet.Applet;
import java.applet.AudioClip;

/**
 * 播放音乐
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class UtilsMusic {
	/**
	 * 播放jar中的mav文件
	 * @param file String
	 */
	public static final void play(String file) {
		play(UtilsMusic.class, file);
	}

	/**
	 * 播放jar中的mav文件
	 * @param clazz Class&lt;?&gt;
	 * @param file String
	 */
	public static final void play(Class<?> clazz, String file) {
		AudioClip sound1 = Applet.newAudioClip(UtilsJar.getJarSourceURL(clazz, file));
		sound1.play();
	}
}
