package des.wangku.operate.standard.utls;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;

import info.monitorenter.cpdetector.io.ASCIIDetector;
import info.monitorenter.cpdetector.io.ByteOrderMarkDetector;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.JChardetFacade;
import info.monitorenter.cpdetector.io.ParsingDetector;
import info.monitorenter.cpdetector.io.UnicodeDetector;

/**
 * 查看url编码
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class UtilsCpdetector {

	private static CodepageDetectorProxy detector;
	static {

		/*
		 * detector是探测器，它把探测任务交给具体的探测实现类的实例完成。
		 * cpDetector内置了一些常用的探测实现类，这些探测实现类的实例可以通过add方法 加进来，如ParsingDetector、
		 * JChardetFacade、ASCIIDetector、UnicodeDetector。
		 * detector按照“谁最先返回非空的探测结果，就以该结果为准”的原则返回探测到的
		 * 字符集编码。使用需要用到三个第三方JAR包：antlr.jar、chardet.jar和jargs-1.0.jar
		 * cpDetector是基于统计学原理的，不保证完全正确。
		 */
		detector = CodepageDetectorProxy.getInstance();
		/*
		 * ParsingDetector可用于检查HTML、XML等文件或字符流的编码,构造方法中的参数用于
		 * 指示是否显示探测过程的详细信息，为false不显示。
		 */
		detector.add(new ParsingDetector(false));
		detector.add(new ByteOrderMarkDetector());
		/*
		 * JChardetFacade封装了由Mozilla组织提供的JChardet，它可以完成大多数文件的编码
		 * 测定。所以，一般有了这个探测器就可满足大多数项目的要求，如果你还不放心，可以
		 * 再多加几个探测器，比如下面的ASCIIDetector、UnicodeDetector等。
		 * 用到antlr.jar、chardet.jar
		 */
		detector.add(JChardetFacade.getInstance());
		// ASCIIDetector用于ASCII编码测定
		detector.add(ASCIIDetector.getInstance());
		// UnicodeDetector用于Unicode家族编码的测定
		detector.add(UnicodeDetector.getInstance());
	}

	/**
	 * 获取URL的编码
	 * @param url 网页URL
	 * @return 网页编码
	 */
	public static synchronized String getUrlEncode(URL url) {
		java.nio.charset.Charset charset = null;
		try {
			charset = detector.detectCodepage(url);
		} catch (Exception ex) {
			return null;
		}
		if (charset == null) return null;
		String c = charset.name();
		if (c == null) return null;
		return charset.name();
	}
	/**
	 * 判断编码格式方法
	 * @param file String
	 * @return String
	 */
	public static  String getFilecharset(String file) {
		if(file==null||file.length()==0)return null;
		return getFilecharset(new File(file));
	}
	/**
	 * 判断编码格式方法
	 * @param file File
	 * @return String
	 */
	public static  String getFilecharset(File file) {
		if(file==null || !file.isFile())return null;
		try {
			 return getFilecharset(new ByteArrayInputStream(FileUtils.readFileToByteArray(file)));
		} catch (IOException e) {
            e.printStackTrace();
            return null;
        }
		
	}
	/**
	 * 判断编码格式方法
	 * @param byteArrayInputStream ByteArrayInputStream
	 * @return String
	 */
	public static  String getFilecharset(ByteArrayInputStream byteArrayInputStream) {
	    if (byteArrayInputStream == null) return null;
	    String charset = "GBK";
	    byte[] first3Bytes = new byte[3];
	    try {
	        boolean checked = false;
	        BufferedInputStream bis = new BufferedInputStream(byteArrayInputStream);
	        bis.mark(0);
	        int read = bis.read(first3Bytes, 0, 3);
	        if (read == -1) {
	            return charset; //文件编码为 ANSI
	        } else if (first3Bytes[0] == (byte) 0xFF
	                && first3Bytes[1] == (byte) 0xFE) {
	            charset = "UTF-16LE"; //文件编码为 Unicode
	            checked = true;
	        } else if (first3Bytes[0] == (byte) 0xFE
	                && first3Bytes[1] == (byte) 0xFF) {
	            charset = "UTF-16BE"; //文件编码为 Unicode big endian
	            checked = true;
	        } else if (first3Bytes[0] == (byte) 0xEF
	                && first3Bytes[1] == (byte) 0xBB
	                && first3Bytes[2] == (byte) 0xBF) {
	            charset = "UTF-8"; //文件编码为 UTF-8
	            checked = true;
	        }
	        bis.reset();
	        if (!checked) {
	            @SuppressWarnings("unused")
				int loc = 0;
	            while ((read = bis.read()) != -1) {
	                loc++;
	                if (read >= 0xF0)
	                    break;
	                if (0x80 <= read && read <= 0xBF) // 单独出现BF以下的，也算是GBK
	                    break;
	                if (0xC0 <= read && read <= 0xDF) {
	                    read = bis.read();
	                    if (0x80 <= read && read <= 0xBF) // 双字节 (0xC0 - 0xDF)
	                        // (0x80
	                        // - 0xBF),也可能在GB编码内
	                        continue;
	                    else
	                        break;
	                } else if (0xE0 <= read && read <= 0xEF) {// 也有可能出错，但是几率较小
	                    read = bis.read();
	                    if (0x80 <= read && read <= 0xBF) {
	                        read = bis.read();
	                        if (0x80 <= read && read <= 0xBF) {
	                            charset = "UTF-8";
	                            break;
	                        } else
	                            break;
	                    } else
	                        break;
	                }
	            }
	        }
	        bis.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    finally {
	        try {
	            byteArrayInputStream.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    return charset;
	}

}
