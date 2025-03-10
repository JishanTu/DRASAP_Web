package tyk.drasap.common;

/**
 * Stringに関するチェック、整形を行うクラス。
 *
 * @version 2013/09/04 yamagishi
 */
public class StringCheck {
	/**
	 * 文字列をtrimして返す。このとき半角スペースだけではなく、全角スペースもtrim対象とする。
	 * @param sorce trimされる文字列
	 * @return trimした結果
	 */
	public static String trimWsp(String sorce){
		int startIndex = -1;
		char[] ca = sorce.toCharArray();
		// まず先頭位置を求める
		for(int i = 0; i < ca.length; i++){
			// 半角スペース、全角スペースでなければ
			if(ca[i] != ' ' && ca[i] != '　'){
				startIndex = i;
				break;// ループを抜ける
			}
		}
		if(startIndex == -1){// 全てtrim対象となった場合
			return "";
		}
		// 次に後尾位置を求める
		int endIndex = ca.length;
		for(int i = ca.length - 1; i >= 0; i--){
			// 半角スペース、全角スペースでなければ
			if(ca[i] != ' ' && ca[i] != '　'){
				endIndex = i + 1;
				break;// ループを抜ける
			}
		}
		return sorce.substring(startIndex, endIndex);
	}
	/**
	 * 全角の英数、記号を半角にする
	 * @param sorce
	 * @return
	 */
	public static String changeDbToSbAscii(String sorce) {
		StringBuffer target = new StringBuffer(sorce.length());
		if(sorce != null){
			for(int i = 0; i < sorce.length(); i++){
				String sub = sorce.substring(i, i + 1);
				String temp = sub;
				// 数字について
				if(sub.equals("０")){
					target.append("0");
				} else if(sub.equals("１")){
					target.append("1");
				} else if(sub.equals("２")){
					target.append("2");
				} else if(sub.equals("３")){
					target.append("3");
				} else if(sub.equals("４")){
					target.append("4");
				} else if(sub.equals("５")){
					target.append("5");
				} else if(sub.equals("６")){
					target.append("6");
				} else if(sub.equals("７")){
					target.append("7");
				} else if(sub.equals("８")){
					target.append("8");
				} else if(sub.equals("９")){
					target.append("9");
				} else if(sub.equals("Ａ")){// 大文字英字について
					target.append("A");
				} else if(sub.equals("Ｂ")){
					target.append("B");
				} else if(sub.equals("Ｃ")){
					target.append("C");
				} else if(sub.equals("Ｄ")){
					target.append("D");
				} else if(sub.equals("Ｅ")){
					target.append("E");
				} else if(sub.equals("Ｆ")){
					target.append("F");
				} else if(sub.equals("Ｇ")){
					target.append("G");
				} else if(sub.equals("Ｈ")){
					target.append("H");
				} else if(sub.equals("Ｉ")){
					target.append("I");
				} else if(sub.equals("Ｊ")){
					target.append("J");
				} else if(sub.equals("Ｋ")){
					target.append("K");
				} else if(sub.equals("Ｌ")){
					target.append("L");
				} else if(sub.equals("Ｍ")){
					target.append("M");
				} else if(sub.equals("Ｎ")){
					target.append("N");
				} else if(sub.equals("Ｏ")){
					target.append("O");
				} else if(sub.equals("Ｐ")){
					target.append("P");
				} else if(sub.equals("Ｑ")){
					target.append("Q");
				} else if(sub.equals("Ｒ")){
					target.append("R");
				} else if(sub.equals("Ｓ")){
					target.append("S");
				} else if(sub.equals("Ｔ")){
					target.append("T");
				} else if(sub.equals("Ｕ")){
					target.append("U");
				} else if(sub.equals("Ｖ")){
					target.append("V");
				} else if(sub.equals("Ｗ")){
					target.append("W");
				} else if(sub.equals("Ｘ")){
					target.append("X");
				} else if(sub.equals("Ｙ")){
					target.append("Y");
				} else if(sub.equals("Ｚ")){
					target.append("Z");
				} else if(sub.equals("ａ")){// 子文字英字について
					target.append("a");
				} else if(sub.equals("ｂ")){
					target.append("b");
				} else if(sub.equals("ｃ")){
					target.append("c");
				} else if(sub.equals("ｄ")){
					target.append("d");
				} else if(sub.equals("ｅ")){
					target.append("e");
				} else if(sub.equals("ｆ")){
					target.append("f");
				} else if(sub.equals("ｇ")){
					target.append("g");
				} else if(sub.equals("ｈ")){
					target.append("h");
				} else if(sub.equals("ｉ")){
					target.append("i");
				} else if(sub.equals("ｊ")){
					target.append("j");
				} else if(sub.equals("ｋ")){
					target.append("k");
				} else if(sub.equals("ｌ")){
					target.append("l");
				} else if(sub.equals("ｍ")){
					target.append("m");
				} else if(sub.equals("ｎ")){
					target.append("n");
				} else if(sub.equals("ｏ")){
					target.append("o");
				} else if(sub.equals("ｐ")){
					target.append("p");
				} else if(sub.equals("ｑ")){
					target.append("q");
				} else if(sub.equals("ｒ")){
					target.append("r");
				} else if(sub.equals("ｓ")){
					target.append("s");
				} else if(sub.equals("ｔ")){
					target.append("t");
				} else if(sub.equals("ｕ")){
					target.append("u");
				} else if(sub.equals("ｖ")){
					target.append("v");
				} else if(sub.equals("ｗ")){
					target.append("w");
				} else if(sub.equals("ｘ")){
					target.append("x");
				} else if(sub.equals("ｙ")){
					target.append("y");
				} else if(sub.equals("ｚ")){
					target.append("z");
				} else if(sub.equals("．")){// 記号について
					target.append(".");
				} else if(sub.equals("，")){
					target.append(",");
				} else if(sub.equals("／")){
					target.append("/");
				} else if(sub.equals("＜")){
					target.append("<");
				} else if(sub.equals("＞")){
					target.append(">");
				} else if(sub.equals("？")){
					target.append("?");
				} else if(sub.equals("＿")){
					target.append("_");
				} else if(sub.equals("；")){
					target.append(";");
				} else if(sub.equals("：")){
					target.append(":");
				} else if(sub.equals("＋")){
					target.append("+");
				} else if(sub.equals("＊")){
					target.append("*");
				} else if(sub.equals("｝")){
					target.append("}");
				} else if(sub.equals("＠")){
					target.append("@");
				} else if(sub.equals("‘")){
					target.append("`");
				} else if(sub.equals("｛")){
					target.append("{");
				} else if(sub.equals("！")){
					target.append("!");
				} else if(sub.equals("＃")){
					target.append("#");
				} else if(sub.equals("＄")){
					target.append("$");
				} else if(sub.equals("％")){
					target.append("%");
				} else if(sub.equals("＆")){
					target.append("&");
				} else if(sub.equals("’")){
					target.append("'");
				} else if(sub.equals("（")){
					target.append("(");
				} else if(sub.equals("）")){
					target.append(")");
				} else if(sub.equals("−")){
					target.append("-");
				} else if(sub.equals("＾")){
					target.append("^");
				} else if(sub.equals("＝")){
					target.append("=");
				} else if(sub.equals("｜")){
					target.append("|");
				} else if(sub.equals("　")){
					target.append(" ");

				} else {// それ以外
					target.append(temp);
				}
			}
		}
		return target.toString();
	}
	/**
	 * 文字コードをラテン(ISO-8859-1)からUTF-8に変更する
	 * @param src
	 * @return
	 */
	public static String latinToUtf8(String src){
		try{
// 2013.09.04 yamagishi modified. start
			// Filterでデフォルト文字コードが指定されている場合は、変換不要。
//			src = new String(src.getBytes("ISO-8859-1"),"UTF-8");
			if (SetCharacterEncodingFilter.DEFAULT_ENCODING == null) {
				src = new String(src.getBytes("ISO-8859-1"), "UTF-8");
			}
// 2013.09.04 yamagishi modified. end
		}catch(Exception e){
			e.printStackTrace();
		}
		return src;
	}

}
