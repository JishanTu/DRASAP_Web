package tyk.drasap.common;

/**
 * String�Ɋւ���`�F�b�N�A���`���s���N���X�B
 *
 * @version 2013/09/04 yamagishi
 */
public class StringCheck {
	/**
	 * �������trim���ĕԂ��B���̂Ƃ����p�X�y�[�X�����ł͂Ȃ��A�S�p�X�y�[�X��trim�ΏۂƂ���B
	 * @param sorce trim����镶����
	 * @return trim��������
	 */
	public static String trimWsp(String sorce) {
		int startIndex = -1;
		char[] ca = sorce.toCharArray();
		// �܂��擪�ʒu�����߂�
		for (int i = 0; i < ca.length; i++) {
			// ���p�X�y�[�X�A�S�p�X�y�[�X�łȂ����
			if (ca[i] != ' ' && ca[i] != '�@') {
				startIndex = i;
				break;// ���[�v�𔲂���
			}
		}
		if (startIndex == -1) {// �S��trim�ΏۂƂȂ����ꍇ
			return "";
		}
		// ���Ɍ���ʒu�����߂�
		int endIndex = ca.length;
		for (int i = ca.length - 1; i >= 0; i--) {
			// ���p�X�y�[�X�A�S�p�X�y�[�X�łȂ����
			if (ca[i] != ' ' && ca[i] != '�@') {
				endIndex = i + 1;
				break;// ���[�v�𔲂���
			}
		}
		return sorce.substring(startIndex, endIndex);
	}

	/**
	 * �S�p�̉p���A�L���𔼊p�ɂ���
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
				// �����ɂ���
				if ("�O".equals(sub)) {
					target.append("0");
				} else if ("�P".equals(sub)) {
					target.append("1");
				} else if ("�Q".equals(sub)) {
					target.append("2");
				} else if ("�R".equals(sub)) {
					target.append("3");
				} else if ("�S".equals(sub)) {
					target.append("4");
				} else if ("�T".equals(sub)) {
					target.append("5");
				} else if ("�U".equals(sub)) {
					target.append("6");
				} else if ("�V".equals(sub)) {
					target.append("7");
				} else if ("�W".equals(sub)) {
					target.append("8");
				} else if ("�X".equals(sub)) {
					target.append("9");
				} else if ("�`".equals(sub)) {// �啶���p���ɂ���
					target.append("A");
				} else if ("�a".equals(sub)) {
					target.append("B");
				} else if ("�b".equals(sub)) {
					target.append("C");
				} else if ("�c".equals(sub)) {
					target.append("D");
				} else if ("�d".equals(sub)) {
					target.append("E");
				} else if ("�e".equals(sub)) {
					target.append("F");
				} else if ("�f".equals(sub)) {
					target.append("G");
				} else if ("�g".equals(sub)) {
					target.append("H");
				} else if ("�h".equals(sub)) {
					target.append("I");
				} else if ("�i".equals(sub)) {
					target.append("J");
				} else if ("�j".equals(sub)) {
					target.append("K");
				} else if ("�k".equals(sub)) {
					target.append("L");
				} else if ("�l".equals(sub)) {
					target.append("M");
				} else if ("�m".equals(sub)) {
					target.append("N");
				} else if ("�n".equals(sub)) {
					target.append("O");
				} else if ("�o".equals(sub)) {
					target.append("P");
				} else if ("�p".equals(sub)) {
					target.append("Q");
				} else if ("�q".equals(sub)) {
					target.append("R");
				} else if ("�r".equals(sub)) {
					target.append("S");
				} else if ("�s".equals(sub)) {
					target.append("T");
				} else if ("�t".equals(sub)) {
					target.append("U");
				} else if ("�u".equals(sub)) {
					target.append("V");
				} else if ("�v".equals(sub)) {
					target.append("W");
				} else if ("�w".equals(sub)) {
					target.append("X");
				} else if ("�x".equals(sub)) {
					target.append("Y");
				} else if ("�y".equals(sub)) {
					target.append("Z");
				} else if ("��".equals(sub)) {// �q�����p���ɂ���
					target.append("a");
				} else if ("��".equals(sub)) {
					target.append("b");
				} else if ("��".equals(sub)) {
					target.append("c");
				} else if ("��".equals(sub)) {
					target.append("d");
				} else if ("��".equals(sub)) {
					target.append("e");
				} else if ("��".equals(sub)) {
					target.append("f");
				} else if ("��".equals(sub)) {
					target.append("g");
				} else if ("��".equals(sub)) {
					target.append("h");
				} else if ("��".equals(sub)) {
					target.append("i");
				} else if ("��".equals(sub)) {
					target.append("j");
				} else if ("��".equals(sub)) {
					target.append("k");
				} else if ("��".equals(sub)) {
					target.append("l");
				} else if ("��".equals(sub)) {
					target.append("m");
				} else if ("��".equals(sub)) {
					target.append("n");
				} else if ("��".equals(sub)) {
					target.append("o");
				} else if ("��".equals(sub)) {
					target.append("p");
				} else if ("��".equals(sub)) {
					target.append("q");
				} else if ("��".equals(sub)) {
					target.append("r");
				} else if ("��".equals(sub)) {
					target.append("s");
				} else if ("��".equals(sub)) {
					target.append("t");
				} else if ("��".equals(sub)) {
					target.append("u");
				} else if ("��".equals(sub)) {
					target.append("v");
				} else if ("��".equals(sub)) {
					target.append("w");
				} else if ("��".equals(sub)) {
					target.append("x");
				} else if ("��".equals(sub)) {
					target.append("y");
				} else if ("��".equals(sub)) {
					target.append("z");
				} else if ("�D".equals(sub)) {// �L���ɂ���
					target.append(".");
				} else if ("�C".equals(sub)) {
					target.append(",");
				} else if ("�^".equals(sub)) {
					target.append("/");
				} else if ("��".equals(sub)) {
					target.append("<");
				} else if ("��".equals(sub)) {
					target.append(">");
				} else if ("�H".equals(sub)) {
					target.append("?");
				} else if ("�Q".equals(sub)) {
					target.append("_");
				} else if ("�G".equals(sub)) {
					target.append(";");
				} else if ("�F".equals(sub)) {
					target.append(":");
				} else if ("�{".equals(sub)) {
					target.append("+");
				} else if ("��".equals(sub)) {
					target.append("*");
				} else if ("�p".equals(sub)) {
					target.append("}");
				} else if ("��".equals(sub)) {
					target.append("@");
				} else if ("�e".equals(sub)) {
					target.append("`");
				} else if ("�o".equals(sub)) {
					target.append("{");
				} else if ("�I".equals(sub)) {
					target.append("!");
				} else if ("��".equals(sub)) {
					target.append("#");
				} else if ("��".equals(sub)) {
					target.append("$");
				} else if ("��".equals(sub)) {
					target.append("%");
				} else if ("��".equals(sub)) {
					target.append("&");
				} else if ("�f".equals(sub)) {
					target.append("'");
				} else if ("�i".equals(sub)) {
					target.append("(");
				} else if ("�j".equals(sub)) {
					target.append(")");
				} else if ("�|".equals(sub)) {
					target.append("-");
				} else if ("�O".equals(sub)) {
					target.append("^");
				} else if ("��".equals(sub)) {
					target.append("=");
				} else if ("�b".equals(sub)) {
					target.append("|");
				} else if ("�@".equals(sub)) {
					target.append(" ");

				} else {// ����ȊO
					target.append(temp);
				}
			}
		}
		return target.toString();
	}

	/**
	 * �����R�[�h�����e��(ISO-8859-1)����UTF-8�ɕύX����
	 * @param src
	 * @return
	 */
	public static String latinToUtf8(String src) {
		try {
			// 2013.09.04 yamagishi modified. start
			// Filter�Ńf�t�H���g�����R�[�h���w�肳��Ă���ꍇ�́A�ϊ��s�v�B
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
