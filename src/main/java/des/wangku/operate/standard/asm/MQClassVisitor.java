package des.wangku.operate.standard.asm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * ASM读取Class文件流
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class MQClassVisitor implements ClassVisitor, Opcodes {
	/** asm查找到的标准的父类 */
	static final String ACC_StandardTaskClass = "des/wangku/operate/standard/task/AbstractTask";
	static Logger logger = LoggerFactory.getLogger(MQClassVisitor.class);
	String classFile = null;

	@Override
	public void visit(int arg0, int arg1, String arg2, String arg3, String arg4, String[] arg5) {
		if (ACC_StandardTaskClass.equals(arg4)) classFile = arg2.replace('/', '.');
	}

	static final String AnnoProjectString = "Ldes/wangku/operate/standard/task/AnnoProjectTask;";
	Map<String, Object> map = new HashMap<>();

	@Override
	public AnnotationVisitor visitAnnotation(String arg0, boolean arg1) {
		if (AnnoProjectString.equals(arg0)) {
			AnnotationValueVisitor avv = new AnnotationValueVisitor();
			this.map = avv.map;
			return avv;
		}
		return null;
	}

	@Override
	public void visitAttribute(Attribute arg0) {

	}

	public static String filenameToClassname(final String filename) {
		return filename.substring(0, filename.lastIndexOf(".class")).replace('/', '.').replace('\\', '.');
	}

	@Override
	public void visitEnd() {

	}

	@Override
	public FieldVisitor visitField(int arg0, String arg1, String arg2, String arg3, Object arg4) {
		return null;
	}

	@Override
	public void visitInnerClass(String arg0, String arg1, String arg2, int arg3) {

	}

	boolean isStructure1 = false;
	boolean isStructure2 = false;
	static final String initArg1 = "<init>";
	static final String paraArg2_1 = "(Lorg/eclipse/swt/widgets/Composite;)V";
	static final String paraArg2_2 = "(Lorg/eclipse/swt/widgets/Composite;I)V";

	@Override
	public MethodVisitor visitMethod(int arg0, String arg1, String arg2, String arg3, String[] arg4) {
		//logger.debug("visit:arg0:" + arg0 + "\t" + "arg1:" + arg1 + "\t" + "arg2:" + arg2 + "\t" + "arg3:" + arg3 + "\t" + "arg4:" + arg4 + "\t");
		if (!initArg1.equals(arg1)) return null;
		if (paraArg2_1.equals(arg2)) isStructure1 = true;
		if (paraArg2_2.equals(arg2)) isStructure2 = true;
		return null;
	}

	@Override
	public void visitOuterClass(String arg0, String arg1, String arg2) {
		//logger.debug("visitOuterClass :arg0:"+arg0+"\t"+"arg1:"+arg1+"\t"+"arg2:"+arg2+"\t");

	}

	@Override
	public void visitSource(String arg0, String arg1) {
		//logger.debug("visitSource :arg0:"+arg0+"\t"+"arg1:"+arg1+"\t");

	}

	public static class AnnotationValueVisitor implements AnnotationVisitor, Opcodes {
		Map<String, Object> map = new HashMap<>();

		public AnnotationValueVisitor() {
		}

		public AnnotationValueVisitor(AnnotationVisitor annotationVisitor) {
		}

		@Override
		public void visit(String name, Object value) {
			map.put(name, value);
		}

		@Override
		public AnnotationVisitor visitAnnotation(String arg0, String arg1) {
			return null;
		}

		@Override
		public AnnotationVisitor visitArray(String arg0) {
			return null;
		}

		@Override
		public void visitEnd() {

		}

		@Override
		public void visitEnum(String arg0, String arg1, String arg2) {

		}
	}

	public String getClassFile() {
		return classFile;
	}

	public void setClassFile(String classFile) {
		this.classFile = classFile;
	}

	public Map<String, Object> getMap() {
		return map;
	}

	public void setMap(Map<String, Object> map) {
		this.map = map;
	}

	public boolean isStructure1() {
		return isStructure1;
	}

	public void setStructure1(boolean isStructure1) {
		this.isStructure1 = isStructure1;
	}

	public boolean isStructure2() {
		return isStructure2;
	}

	public void setStructure2(boolean isStructure2) {
		this.isStructure2 = isStructure2;
	}
	
}
