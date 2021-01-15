package des.wangku.operate.standard.subengineering.reediting;

import java.io.StringReader;
import java.util.List;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import des.wangku.operate.standard.utls.UtilsRnd;
import des.wangku.operate.standard.utls.UtilsWordsAnalysis;
import des.wangku.operate.standard.utls.UtilsWordsAnalysis.WordsPart;

/**
 * 对内容的再编辑
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class Reediting {
	/** 概率最高100% */
	float chance = 100f;
	Synonymword s = null;
	String content = null;
	String newcontent = null;

	public boolean isChange() {
		return UtilsRnd.getRndProbability100(getChance());
	}

	public final float getChance() {
		if (chance < 0) return 0f;
		if (chance > 100f) return 100f;
		return chance;
	}

	public final void setChance(float chance) {
		if (chance < 0) {
			this.chance = 0f;
		} else if (chance > 100f) {
			this.chance = 100f;
		} else {
			this.chance = chance;
		}
	}

	public Reediting(String synonymFile) {
		s = new Synonymword(synonymFile);
	}

	public Reediting(String content, String synonymFile) {
		this.content = content;
		s = new Synonymword(synonymFile);
	}

	public void change() {
		if (content == null || s == null) return;
		List<WordsPart> list = UtilsWordsAnalysis.wlteaAnalyzer(content);
		StringBuilder sb = new StringBuilder();
		for (WordsPart e : list) {
			String changeword = s.getResultRndValue(e.getWord());
			if (changeword != null && isChange()) e.setWord(changeword);
			sb.append(e.getLeft() + e.getWord() + e.getRight());
		}
		newcontent = sb.toString();
	}
	public String getNewsChangeContent(String content) {
		this.content=content;
		change();
		return newcontent;
	}
	void change2() {
		if (content == null || s == null) return;
		IKSegmenter ik = new IKSegmenter(new StringReader(content), true);
		StringBuilder sb = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		try {
			Lexeme before = null;
			Lexeme lex = null;
			while ((lex = ik.next()) != null) {
				String word = lex.getLexemeText();
				sb2.append(word).append(" ");
				String changeword = s.getResultRndValue(word);
				String newword = word;
				if (changeword != null) {
					newword = changeword;
				}
				String left = appendSymbolLeft(before, lex);
				//System.out.println(left);
				sb.append(left + newword);
				before = lex;
			}
			int end = before.getEndPosition();
			String right = "";
			if (end < content.length()) right = appendWhiteSpace(content.substring(end));
			//System.out.println("right:" + right);
			sb.append(right);
			//System.out.println("sb2:" + sb2.toString());
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		newcontent = sb.toString();
	}

	public String appendSymbolLeft(Lexeme before, Lexeme cur) {
		if (cur == null) return "";
		int point = 0;
		if (before != null) {/* 开始位置 */
			point = before.getEndPosition();
		}
		int start = cur.getBegin();
		if (start == point) return "";
		return appendWhiteSpace(content.substring(point, start));
	}

	public static String appendWhiteSpace(String src) {
		String dst = "";
		for (char c : src.toCharArray()) {
			dst += c;
		}
		return dst;
	}

	public final String getNewcontent() {
		return newcontent;
	}

	public final void setContent(String content) {
		this.content = content;
	}

	public static void main(String[] args) {
		String file = "D:\\Eclipse\\eclipse-oxygen\\Workspaces\\des-wkope\\build\\libs\\model\\des-wkope-task-p016_synonymword.txt";
		Reediting s = new Reediting(file);
		s.setChance(50f);
		String[] arrs = { "..这个错误的说的是路径的问题，因为从这个路径找不到文件,。.", "中国-世界卫生组织新冠肺炎联合专家考察组在京举行新闻发布会，公布了近日在中国四地开展联合调查的结果", "考察组外方组长、世卫组织总干事高级顾问布鲁斯·艾尔沃德表示，中国所采取的策略改变了新增确诊病例快速攀升的曲线，能够说明这一点的最简单、直接的就是数据。",
				"非常感谢。晚上好，女士们、先生们！在发言之前，我想首先借此机会代表我个人、代表考察团、代表我的同事们向在疫情中失去亲人、朋友和同事的中国人民表示深切慰问。在我们此行过程中，我一次又一次地目睹和体会到", "在讲具体的工作内容之前，我想表达两点谢意。首先我要感谢中国政府以及世界卫生组织高层领导，能够委以我们如此重任，" };
		for (String e : arrs) {
			s.setContent(e);
			System.out.println(e);
			s.change();
			String newcontent = s.getNewcontent();
			System.out.println(newcontent);

		}
	}
}
