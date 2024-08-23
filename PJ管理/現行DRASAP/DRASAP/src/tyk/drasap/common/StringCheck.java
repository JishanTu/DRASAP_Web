package tyk.drasap.common;

/**
 * String‚ÉŠÖ‚·‚éƒ`ƒFƒbƒNA®Œ`‚ğs‚¤ƒNƒ‰ƒXB
 *
 * @version 2013/09/04 yamagishi
 */
public class StringCheck {
	/**
	 * •¶š—ñ‚ğtrim‚µ‚Ä•Ô‚·B‚±‚Ì‚Æ‚«”¼ŠpƒXƒy[ƒX‚¾‚¯‚Å‚Í‚È‚­A‘SŠpƒXƒy[ƒX‚àtrim‘ÎÛ‚Æ‚·‚éB
	 * @param sorce trim‚³‚ê‚é•¶š—ñ
	 * @return trim‚µ‚½Œ‹‰Ê
	 */
	public static String trimWsp(String sorce){
		int startIndex = -1;
		char[] ca = sorce.toCharArray();
		// ‚Ü‚¸æ“ªˆÊ’u‚ğ‹‚ß‚é
		for(int i = 0; i < ca.length; i++){
			// ”¼ŠpƒXƒy[ƒXA‘SŠpƒXƒy[ƒX‚Å‚È‚¯‚ê‚Î
			if(ca[i] != ' ' && ca[i] != '@'){
				startIndex = i;
				break;// ƒ‹[ƒv‚ğ”²‚¯‚é
			}
		}
		if(startIndex == -1){// ‘S‚Ätrim‘ÎÛ‚Æ‚È‚Á‚½ê‡
			return "";
		}
		// Ÿ‚ÉŒã”öˆÊ’u‚ğ‹‚ß‚é
		int endIndex = ca.length;
		for(int i = ca.length - 1; i >= 0; i--){
			// ”¼ŠpƒXƒy[ƒXA‘SŠpƒXƒy[ƒX‚Å‚È‚¯‚ê‚Î
			if(ca[i] != ' ' && ca[i] != '@'){
				endIndex = i + 1;
				break;// ƒ‹[ƒv‚ğ”²‚¯‚é
			}
		}
		return sorce.substring(startIndex, endIndex);
	}
	/**
	 * ‘SŠp‚Ì‰p”A‹L†‚ğ”¼Šp‚É‚·‚é
	 * @param sorce
	 * @return
	 */
	public static String changeDbToSbAscii(String sorce) {
		StringBuffer target = new StringBuffer(sorce.length());
		if(sorce != null){
			for(int i = 0; i < sorce.length(); i++){
				String sub = sorce.substring(i, i + 1);
				String temp = sub;
				// ”š‚É‚Â‚¢‚Ä
				if(sub.equals("‚O")){
					target.append("0");
				} else if(sub.equals("‚P")){
					target.append("1");
				} else if(sub.equals("‚Q")){
					target.append("2");
				} else if(sub.equals("‚R")){
					target.append("3");
				} else if(sub.equals("‚S")){
					target.append("4");
				} else if(sub.equals("‚T")){
					target.append("5");
				} else if(sub.equals("‚U")){
					target.append("6");
				} else if(sub.equals("‚V")){
					target.append("7");
				} else if(sub.equals("‚W")){
					target.append("8");
				} else if(sub.equals("‚X")){
					target.append("9");
				} else if(sub.equals("‚`")){// ‘å•¶š‰pš‚É‚Â‚¢‚Ä
					target.append("A");
				} else if(sub.equals("‚a")){
					target.append("B");
				} else if(sub.equals("‚b")){
					target.append("C");
				} else if(sub.equals("‚c")){
					target.append("D");
				} else if(sub.equals("‚d")){
					target.append("E");
				} else if(sub.equals("‚e")){
					target.append("F");
				} else if(sub.equals("‚f")){
					target.append("G");
				} else if(sub.equals("‚g")){
					target.append("H");
				} else if(sub.equals("‚h")){
					target.append("I");
				} else if(sub.equals("‚i")){
					target.append("J");
				} else if(sub.equals("‚j")){
					target.append("K");
				} else if(sub.equals("‚k")){
					target.append("L");
				} else if(sub.equals("‚l")){
					target.append("M");
				} else if(sub.equals("‚m")){
					target.append("N");
				} else if(sub.equals("‚n")){
					target.append("O");
				} else if(sub.equals("‚o")){
					target.append("P");
				} else if(sub.equals("‚p")){
					target.append("Q");
				} else if(sub.equals("‚q")){
					target.append("R");
				} else if(sub.equals("‚r")){
					target.append("S");
				} else if(sub.equals("‚s")){
					target.append("T");
				} else if(sub.equals("‚t")){
					target.append("U");
				} else if(sub.equals("‚u")){
					target.append("V");
				} else if(sub.equals("‚v")){
					target.append("W");
				} else if(sub.equals("‚w")){
					target.append("X");
				} else if(sub.equals("‚x")){
					target.append("Y");
				} else if(sub.equals("‚y")){
					target.append("Z");
				} else if(sub.equals("‚")){// q•¶š‰pš‚É‚Â‚¢‚Ä
					target.append("a");
				} else if(sub.equals("‚‚")){
					target.append("b");
				} else if(sub.equals("‚ƒ")){
					target.append("c");
				} else if(sub.equals("‚„")){
					target.append("d");
				} else if(sub.equals("‚…")){
					target.append("e");
				} else if(sub.equals("‚†")){
					target.append("f");
				} else if(sub.equals("‚‡")){
					target.append("g");
				} else if(sub.equals("‚ˆ")){
					target.append("h");
				} else if(sub.equals("‚‰")){
					target.append("i");
				} else if(sub.equals("‚Š")){
					target.append("j");
				} else if(sub.equals("‚‹")){
					target.append("k");
				} else if(sub.equals("‚Œ")){
					target.append("l");
				} else if(sub.equals("‚")){
					target.append("m");
				} else if(sub.equals("‚")){
					target.append("n");
				} else if(sub.equals("‚")){
					target.append("o");
				} else if(sub.equals("‚")){
					target.append("p");
				} else if(sub.equals("‚‘")){
					target.append("q");
				} else if(sub.equals("‚’")){
					target.append("r");
				} else if(sub.equals("‚“")){
					target.append("s");
				} else if(sub.equals("‚”")){
					target.append("t");
				} else if(sub.equals("‚•")){
					target.append("u");
				} else if(sub.equals("‚–")){
					target.append("v");
				} else if(sub.equals("‚—")){
					target.append("w");
				} else if(sub.equals("‚˜")){
					target.append("x");
				} else if(sub.equals("‚™")){
					target.append("y");
				} else if(sub.equals("‚š")){
					target.append("z");
				} else if(sub.equals("D")){// ‹L†‚É‚Â‚¢‚Ä
					target.append(".");
				} else if(sub.equals("C")){
					target.append(",");
				} else if(sub.equals("^")){
					target.append("/");
				} else if(sub.equals("ƒ")){
					target.append("<");
				} else if(sub.equals("„")){
					target.append(">");
				} else if(sub.equals("H")){
					target.append("?");
				} else if(sub.equals("Q")){
					target.append("_");
				} else if(sub.equals("G")){
					target.append(";");
				} else if(sub.equals("F")){
					target.append(":");
				} else if(sub.equals("{")){
					target.append("+");
				} else if(sub.equals("–")){
					target.append("*");
				} else if(sub.equals("p")){
					target.append("}");
				} else if(sub.equals("—")){
					target.append("@");
				} else if(sub.equals("e")){
					target.append("`");
				} else if(sub.equals("o")){
					target.append("{");
				} else if(sub.equals("I")){
					target.append("!");
				} else if(sub.equals("”")){
					target.append("#");
				} else if(sub.equals("")){
					target.append("$");
				} else if(sub.equals("“")){
					target.append("%");
				} else if(sub.equals("•")){
					target.append("&");
				} else if(sub.equals("f")){
					target.append("'");
				} else if(sub.equals("i")){
					target.append("(");
				} else if(sub.equals("j")){
					target.append(")");
				} else if(sub.equals("|")){
					target.append("-");
				} else if(sub.equals("O")){
					target.append("^");
				} else if(sub.equals("")){
					target.append("=");
				} else if(sub.equals("b")){
					target.append("|");
				} else if(sub.equals("@")){
					target.append(" ");

				} else {// ‚»‚êˆÈŠO
					target.append(temp);
				}
			}
		}
		return target.toString();
	}
	/**
	 * •¶šƒR[ƒh‚ğƒ‰ƒeƒ“(ISO-8859-1)‚©‚çUTF-8‚É•ÏX‚·‚é
	 * @param src
	 * @return
	 */
	public static String latinToUtf8(String src){
		try{
// 2013.09.04 yamagishi modified. start
			// Filter‚ÅƒfƒtƒHƒ‹ƒg•¶šƒR[ƒh‚ªw’è‚³‚ê‚Ä‚¢‚éê‡‚ÍA•ÏŠ·•s—vB
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
