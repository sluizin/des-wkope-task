package des.wangku.operate.standard.utls;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

/**
 * 解词通用方法
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class UtilsWordsAnalysis {

	public static final List<WordsPart> wlteaAnalyzer(String content) {
		return wlteaAnalyzer(content,true);
	}
	public static final List<WordsPart> wlteaAnalyzer(String content,boolean useSmart) {
		if(content==null || content.length()==0)return new ArrayList<>();
		IKSegmenter ik = new IKSegmenter(new StringReader(content), useSmart);
		return wlteaAnalyzer(content,ik);
	}
	
	
	
	public static final List<WordsPart> wlteaAnalyzer(final String content,IKSegmenter ik) {
		List<WordsPart> list = new ArrayList<>();
		if(content==null || content.length()==0)return list;
		Lexeme before = null;
		Lexeme lex = null;
		try {
			//System.out.println(("content.length():"+content.length()));
			while ((lex = ik.next()) != null) {
				String word = lex.getLexemeText();
				//System.out.println(("word:"+word));
				String left = appendSymbolLeft(content,before, lex);
				//System.out.println(("left:"+left));
				list.add(new WordsPart(left,word,""));
				before = lex;
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		try {
			int end = before.getEndPosition();
			if (end < content.length()) {
				//System.out.println(("end:"+end));
				//System.out.println(("content.length():"+content.length()));
				String str=content.substring(end);
				//System.out.println(("str:"+str));
				String left = appendWhiteSpace(str);
				list.add(new WordsPart(left,"",""));
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

		return list;
	}

	private static String appendSymbolLeft(String content, Lexeme before, Lexeme cur) {
		if (cur == null) return "";
		int point = 0;
		if (before != null) {/* 开始位置 */
			point = before.getEndPosition();
		}
		int start = cur.getBegin();
		if (start <= point) return "";
		//System.out.println("content:"+content.length());
		//System.out.println("point:"+point);
		//System.out.println("start:"+start);
		String str=content.substring(point, start);
		//System.out.println("str:"+str);
		return appendWhiteSpace(str);
	}

	private static String appendWhiteSpace(String src) {
		String dst = "";
		for (char c : src.toCharArray())
			dst += c;
		return dst;
	}

	/**
	 * 单词分解部分
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 */
	public static class WordsPart {
		String left = "";
		String word = "";
		String right = "";
		public WordsPart(String left,String word,String right) {
			this.left=left;
			this.word=word;
			this.right=right;
		}
		public final String getLeft() {
			return left;
		}

		public final void setLeft(String left) {
			this.left = left;
		}

		public final String getWord() {
			return word;
		}

		public final void setWord(String word) {
			this.word = word;
		}

		public final String getRight() {
			return right;
		}

		public final void setRight(String right) {
			this.right = right;
		}

	}
}
