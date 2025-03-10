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
	public static String trimWsp(String sorce) {
		int startIndex = -1;
		char[] ca = sorce.toCharArray();
		// まず先頭位置を求める
		for (int i = 0; i < ca.length; i++) {
			// 半角スペース、全角スペースでなければ
			if (ca[i] != ' ' && ca[i] != '　') {
				startIndex = i;
				break;// ループを抜ける
			}
		}
		if (startIndex == -1) {// 全てtrim対象となった場合
			return "";
		}
		// 次に後尾位置を求める
		int endIndex = ca.length;
		for (int i = ca.length - 1; i >= 0; i--) {
			// 半角スペース、全角スペースでなければ
			if (ca[i] != ' ' && ca[i] != '　') {
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
		StringBuilder target = new StringBuilder();
		if (sorce != null) {
			target = new StringBuilder(sorce.length());
			for (int i = 0; i < sorce.length(); i++) {
				String sub = sorce.substring(i, i + 1);
				String temp = sub;
				// 数字について
				if ("０".equals(sub)) {
					target.append("0");
				} else if ("１".equals(sub)) {
					target.append("1");
				} else if ("２".equals(sub)) {
					target.append("2");
				} else if ("３".equals(sub)) {
					target.append("3");
				} else if ("４".equals(sub)) {
					target.append("4");
				} else if ("５".equals(sub)) {
					target.append("5");
				} else if ("６".equals(sub)) {
					target.append("6");
				} else if ("７".equals(sub)) {
					target.append("7");
				} else if ("８".equals(sub)) {
					target.append("8");
				} else if ("９".equals(sub)) {
					target.append("9");
				} else if ("Ａ".equals(sub)) {// 大文字英字について
					target.append("A");
				} else if ("Ｂ".equals(sub)) {
					target.append("B");
				} else if ("Ｃ".equals(sub)) {
					target.append("C");
				} else if ("Ｄ".equals(sub)) {
					target.append("D");
				} else if ("Ｅ".equals(sub)) {
					target.append("E");
				} else if ("Ｆ".equals(sub)) {
					target.append("F");
				} else if ("Ｇ".equals(sub)) {
					target.append("G");
				} else if ("Ｈ".equals(sub)) {
					target.append("H");
				} else if ("Ｉ".equals(sub)) {
					target.append("I");
				} else if ("Ｊ".equals(sub)) {
					target.append("J");
				} else if ("Ｋ".equals(sub)) {
					target.append("K");
				} else if ("Ｌ".equals(sub)) {
					target.append("L");
				} else if ("Ｍ".equals(sub)) {
					target.append("M");
				} else if ("Ｎ".equals(sub)) {
					target.append("N");
				} else if ("Ｏ".equals(sub)) {
					target.append("O");
				} else if ("Ｐ".equals(sub)) {
					target.append("P");
				} else if ("Ｑ".equals(sub)) {
					target.append("Q");
				} else if ("Ｒ".equals(sub)) {
					target.append("R");
				} else if ("Ｓ".equals(sub)) {
					target.append("S");
				} else if ("Ｔ".equals(sub)) {
					target.append("T");
				} else if ("Ｕ".equals(sub)) {
					target.append("U");
				} else if ("Ｖ".equals(sub)) {
					target.append("V");
				} else if ("Ｗ".equals(sub)) {
					target.append("W");
				} else if ("Ｘ".equals(sub)) {
					target.append("X");
				} else if ("Ｙ".equals(sub)) {
					target.append("Y");
				} else if ("Ｚ".equals(sub)) {
					target.append("Z");
				} else if ("ａ".equals(sub)) {// 子文字英字について
					target.append("a");
				} else if ("ｂ".equals(sub)) {
					target.append("b");
				} else if ("ｃ".equals(sub)) {
					target.append("c");
				} else if ("ｄ".equals(sub)) {
					target.append("d");
				} else if ("ｅ".equals(sub)) {
					target.append("e");
				} else if ("ｆ".equals(sub)) {
					target.append("f");
				} else if ("ｇ".equals(sub)) {
					target.append("g");
				} else if ("ｈ".equals(sub)) {
					target.append("h");
				} else if ("ｉ".equals(sub)) {
					target.append("i");
				} else if ("ｊ".equals(sub)) {
					target.append("j");
				} else if ("ｋ".equals(sub)) {
					target.append("k");
				} else if ("ｌ".equals(sub)) {
					target.append("l");
				} else if ("ｍ".equals(sub)) {
					target.append("m");
				} else if ("ｎ".equals(sub)) {
					target.append("n");
				} else if ("ｏ".equals(sub)) {
					target.append("o");
				} else if ("ｐ".equals(sub)) {
					target.append("p");
				} else if ("ｑ".equals(sub)) {
					target.append("q");
				} else if ("ｒ".equals(sub)) {
					target.append("r");
				} else if ("ｓ".equals(sub)) {
					target.append("s");
				} else if ("ｔ".equals(sub)) {
					target.append("t");
				} else if ("ｕ".equals(sub)) {
					target.append("u");
				} else if ("ｖ".equals(sub)) {
					target.append("v");
				} else if ("ｗ".equals(sub)) {
					target.append("w");
				} else if ("ｘ".equals(sub)) {
					target.append("x");
				} else if ("ｙ".equals(sub)) {
					target.append("y");
				} else if ("ｚ".equals(sub)) {
					target.append("z");
				} else if ("．".equals(sub)) {// 記号について
					target.append(".");
				} else if ("，".equals(sub)) {
					target.append(",");
				} else if ("／".equals(sub)) {
					target.append("/");
				} else if ("＜".equals(sub)) {
					target.append("<");
				} else if ("＞".equals(sub)) {
					target.append(">");
				} else if ("？".equals(sub)) {
					target.append("?");
				} else if ("＿".equals(sub)) {
					target.append("_");
				} else if ("；".equals(sub)) {
					target.append(";");
				} else if ("：".equals(sub)) {
					target.append(":");
				} else if ("＋".equals(sub)) {
					target.append("+");
				} else if ("＊".equals(sub)) {
					target.append("*");
				} else if ("｝".equals(sub)) {
					target.append("}");
				} else if ("＠".equals(sub)) {
					target.append("@");
				} else if ("‘".equals(sub)) {
					target.append("`");
				} else if ("｛".equals(sub)) {
					target.append("{");
				} else if ("！".equals(sub)) {
					target.append("!");
				} else if ("＃".equals(sub)) {
					target.append("#");
				} else if ("＄".equals(sub)) {
					target.append("$");
				} else if ("％".equals(sub)) {
					target.append("%");
				} else if ("＆".equals(sub)) {
					target.append("&");
				} else if ("’".equals(sub)) {
					target.append("'");
				} else if ("（".equals(sub)) {
					target.append("(");
				} else if ("）".equals(sub)) {
					target.append(")");
				} else if ("−".equals(sub)) {
					target.append("-");
				} else if ("＾".equals(sub)) {
					target.append("^");
				} else if ("＝".equals(sub)) {
					target.append("=");
				} else if ("｜".equals(sub)) {
					target.append("|");
				} else if ("　".equals(sub)) {
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
	public static String latinToUtf8(String src) {
		try {
			// 2013.09.04 yamagishi modified. start
			// Filterでデフォルト文字コードが指定されている場合は、変換不要。
			//			src = new String(src.getBytes("ISO-8859-1"),"UTF-8");
			if (SetCharacterEncodingFilter.DEFAULT_ENCODING == null) {
				src = new String(src.getBytes("ISO-8859-1"), "UTF-8");
			}
			// 2013.09.04 yamagishi modified. end
		} catch (Exception e) {
			e.printStackTrace();
		}
		return src;
	}

}
