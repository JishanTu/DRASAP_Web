package tyk.drasap.common;

import java.io.File;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

/**
 * ���[�e�B���e�B���܂Ƃ߂��N���X�B
 * ���̃V�X�e���ł̂ݎg�p�������ȃ��[�e�B���e�B�B
 */
public class DrasapUtil {
	// ���\�b�hcreateCharArray�Ŏg�p����B�ǔԂ��`����������B
	// I(�A�C) O(�I�[) Q(�L���[) X(�G�b�N�X)�͋֎~�����B
	private final static String OIBAN_RULE = "0123456789ABCDEFGHJKLMNPRSTUVWYZ";

	// �}�ʃT�C�Y�̔�r������Ƃ��̊�ƂȂ�AHashMap
	// A1L,A2L��ǉ� '04.Mar.2 by Hirata
	// A3L��ǉ� '04.Oct.19 by Hirata
	// ** ���� ***************************
	// �����ύX�����ꍇ�A�K��decidePrintSizeForRequest()������������
	private static HashMap<String, String> drwgSizeMap = new HashMap<String, String>();
	static {
		drwgSizeMap.put("A4", "0");
		drwgSizeMap.put("A3", "1");
		drwgSizeMap.put("A3L", "2");
		drwgSizeMap.put("A2", "3");
		drwgSizeMap.put("A2L", "4");
		drwgSizeMap.put("A1", "5");
		drwgSizeMap.put("A1L", "6");
		drwgSizeMap.put("A0", "7");
		drwgSizeMap.put("A0L", "8");
	}

	// --------------------------------------------------------------- Method
	/**
	 * �ǔԂ�Ԃ����\�b�h�B
	 * @param fromChar
	 * @param toChar
	 * @return
	 */
	public static char[] createOibanArray(char fromChar, char toChar) {
		int fromIdx = OIBAN_RULE.indexOf(fromChar);// fromChar�̃C���f�b�N�X
		int toIdx = OIBAN_RULE.indexOf(toChar);// toChar�̃C���f�b�N�X

		if (fromIdx == -1 || toIdx == -1) {
			// �g�p�\�ȕ�����
			throw new IllegalArgumentException("�w��\�ȕ����� 0-9 �܂��� A-Z�ł��B(IOQX������)");
		}
		if (fromIdx > toIdx) {
			// from > to �ł͂Ȃ���
			throw new IllegalArgumentException("from <= to �ƂȂ�悤�Ɏw�肵�Ă��������B");
		}
		char[] oibanArray = new char[toIdx - fromIdx + 1];
		for (int i = 0; i < oibanArray.length; i++) {
			oibanArray[i] = OIBAN_RULE.charAt(fromIdx + i);
		}
		return oibanArray;
	}

	/**
	 * �}�ʃT�C�Y���r����B
	 * size1 > size2 �̂Ƃ����B
	 * size1 = size2 �̂Ƃ� 0�B
	 * size1 < size2 �̂Ƃ����B
	 * �}�ʃT�C�Y�Ƃ��ĕs���Ȃ��̂��w�肷��ƁAIllegalArgumentException����������
	 * @param size1 �}�ʃT�C�Y(A0L,A0,A1L,A1,A2L,A2,A3,A4)�̂����ꂩ�B
	 * @param size2
	 * @return
	 */
	public static int compareDrwgSize(String size1, String size2) {
		return convertDrwgSizeToInt(size1) - convertDrwgSizeToInt(size2);
	}

	/**
	 * �}�ʃT�C�Y��drwgSizeMap���g�p���āAint�ɕϊ��B
	 * �傫���}�ʂقǑ傫�������ƂȂ�B
	 * @param size
	 * @return
	 */
	private static int convertDrwgSizeToInt(String size) {
		String sizeInt = drwgSizeMap.get(size);
		if (sizeInt == null) {
			throw new IllegalArgumentException("�w�肵���}�ʃT�C�Y������������܂���B{" + size + "}");
		}
		return Integer.parseInt(sizeInt);
	}

	/**
	 * ���}�̃T�C�Y�A�w�肵���T�C�Y�A����������v�����^�̑g�ݍ��킹�ŁA
	 * �Q�l�}�o�͗p�e�[�u���̏o�̓T�C�Y�ɃZ�b�g����T�C�Y�����߂�B
	 * �o�̓T�C�Y�̎w�肪�����ꍇ�Anull��Ԃ��B
	 * @param drwgSize ���}�̃T�C�Y
	 * @param specifiedSize �w�肵���T�C�Y
	 * @param printer ����������v�����^
	 * @return �Q�l�}�o�͗p�e�[�u���̏o�̓T�C�Y�B�o�̓T�C�Y�̎w�肪�����ꍇ�Anull��Ԃ��B
	 */
	public static String decidePrintSizeForRequest(String drwgSize, String specifiedSize, Printer printer) {
		if ("ORG".equals(specifiedSize)) {
			// �������w�肳��Ă���ꍇ�A////////////////////////////////////////////
			if (printer.isPrintable(drwgSize)) {
				// �}�ʃT�C�Y������\�Ȃ�AORG��Ԃ�
				return specifiedSize;
			}
			if ("A0L".equals(drwgSize) && printer.isPrintableA0()) {
				// �}�ʃT�C�Y=A0L�ŁAA0������\�Ȃ�AA0��Ԃ�
				return "A0";
			}
			return null;// �o�̓T�C�Y�w��G���[
		}
		if (specifiedSize.endsWith("%")) {
			// �T�C�Y��'%'�ŏI����Ă���ꍇ�A////////////////////////////////////////////
			// 50%�ȊO�ɁA70.7%,35.4%,25%�ɂ��Ή�
			try {
				// ??%�T�C�Y�����߂�
				String sizeXX = convertDrwgSizeToXX(specifiedSize, drwgSize);
				if (printer.isPrintable(sizeXX)) {
					// ??%�T�C�Y������\�Ȃ�
					return sizeXX;
				}
				return null;// �o�̓T�C�Y�w��G���[
			} catch (IllegalArgumentException e) {
				// ??%�T�C�Y�����߂��Ȃ�
				return null;// �o�̓T�C�Y�w��G���[
			}
		}
		// A0-A4���w�肳��Ă���ꍇ�A/////////////////////////////////////////////
		String targetSize = specifiedSize;// �o�̓T�C�Y
		if (compareDrwgSize(drwgSize, specifiedSize) < 0) {
			// �}�ʃT�C�Y < �w��T�C�Y �Ȃ�A�}�ʃT�C�Y�ɒu��������
			targetSize = drwgSize;
		}
		// ���̃T�C�Y�� int �ɕϊ��E�E�Eswitch,case���g�p����������
		// switch,case���g�p���邱�Ƃɂ��A���̃T�C�Y������s�Ȃ�
		// ���̃v�����^�ŁA���ɍł��傫�Ȉ���\�ȃT�C�Y���߂�
		int targetSizeInt = convertDrwgSizeToInt(targetSize);
		switch (targetSizeInt) {
		case 8:
		case 7: // A0L�܂���A0�̂Ƃ�
			if (printer.isPrintableA0()) {
				return "A0";
			}
		case 5: // A1�̂Ƃ�
			if (printer.isPrintableA1()) {
				return "A1";
			}
		case 3: // A2�̂Ƃ�
			if (printer.isPrintableA2()) {
				return "A2";
			}
		case 1: // A3�̂Ƃ�
			if (printer.isPrintableA3()) {
				return "A3";
			}
		case 0: // A4�̂Ƃ�
			if (printer.isPrintableA4()) {
				return "A4";
			}
		default:
			return null;// �o�̓T�C�Y�w��G���[
		}
	}

	/**
	 * �w�肵���}�ʃT�C�Y���k�������T�C�Y��Ԃ��B
	 * �k���ł��Ȃ��ꍇ�AIllegalArgumentException ��Ԃ��B
	 * @param ratio �k���T�C�Y�B�w��\�Ȕ͈͂�{70.7%,50%,35.4%,25%}
	 * @param drwgSize A0L-A4
	 * @return
	 */
	public static String convertDrwgSizeToXX(String ratio, String drwgSize) {
		if ("70.7%".equals(ratio)) {
			if ("A0L".equals(drwgSize)) {
				return "A1L";
			}
			if ("A0".equals(drwgSize)) {
				return "A1";
			}
			if ("A1L".equals(drwgSize)) {
				return "A2L";
			}
			if ("A1".equals(drwgSize)) {
				return "A2";
			}
			if ("A2L".equals(drwgSize)) {
				return "A3L";
			}
			if ("A2".equals(drwgSize)) {
				return "A3";
			}
			if ("A3".equals(drwgSize)) {
				return "A4";
			}
			throw new IllegalArgumentException("�w�肵���}�ʃT�C�Y��70.7%�ɂ��邱�Ƃ͂ł��܂���B{" + drwgSize + "}");
		}
		if ("50%".equals(ratio)) {
			if ("A0L".equals(drwgSize)) {
				return "A2L";
			}
			if ("A0".equals(drwgSize)) {
				return "A2";
			}
			if ("A1L".equals(drwgSize)) {
				return "A3L";
			}
			if ("A1".equals(drwgSize)) {
				return "A3";
			}
			if ("A2".equals(drwgSize)) {
				return "A4";
			}
			throw new IllegalArgumentException("�w�肵���}�ʃT�C�Y��50%�ɂ��邱�Ƃ͂ł��܂���B{" + drwgSize + "}");
		}
		if ("35.4%".equals(ratio)) {
			if ("A0L".equals(drwgSize)) {
				return "A3L";
			}
			if ("A0".equals(drwgSize)) {
				return "A3";
			}
			if ("A1".equals(drwgSize)) {
				return "A4";
			}
			throw new IllegalArgumentException("�w�肵���}�ʃT�C�Y��35.4%�ɂ��邱�Ƃ͂ł��܂���B{" + drwgSize + "}");
		}
		if (!"25%".equals(ratio)) {
			throw new IllegalArgumentException("�w�肵���k���T�C�Y������������܂���B{" + ratio + "}");
		}
		if ("A0".equals(drwgSize)) {
			return "A4";
		}
		throw new IllegalArgumentException("�w�肵���}�ʃT�C�Y��25%�ɂ��邱�Ƃ͂ł��܂���B{" + drwgSize + "}");
	}

	/**
	 * �}�Ԃ��t�H�[�}�b�g����B
	 * 12���Ȃ�A���̂܂ܕԂ��B
	 * ����ȊO��XX-XXXXXXXX-X...�Ƃ��ĕԂ��B
	 * @param drwgNo
	 * @return
	 */
	public static String formatDrwgNo(String drwgNo) {
		if (drwgNo == null || drwgNo.length() == 12) {
			// 12�P�^�Ȃ炻�̂܂ܕԂ�
			return drwgNo;
		}
		// ����ȊO
		StringBuilder sb = new StringBuilder();
		if (drwgNo.length() <= 2) {
			// ������2�ȉ��Ȃ�
			sb.append(drwgNo);
		} else {
			// ������2��蒷��
			sb.append(drwgNo.substring(0, 2));
			sb.append('-');
			if (drwgNo.length() <= 10) {
				// ������10�ȉ��Ȃ�
				sb.append(drwgNo.substring(2));
			} else {
				// ������10��蒷���Ȃ�
				sb.append(drwgNo.substring(2, 10));
				sb.append('-');
				sb.append(drwgNo.substring(10));
			}
		}
		return sb.toString();
	}

	public static String createMessage(String msg, String arg0, String arg1, String arg2) {
		return createMessage(msg, arg0, arg1, arg2, null);
	}

	public static String createMessage(String msg, String arg0, String arg1) {
		return createMessage(msg, arg0, arg1, null, null);
	}

	public static String createMessage(String msg, String arg0) {
		return createMessage(msg, arg0, null, null, null);
	}

	public static String createMessage(String msg) {
		return createMessage(msg, null, null, null, null);
	}

	public static String createMessage(String msg, String arg0, String arg1, String arg2, String arg3) {
		if (msg == null) {
			return "";
		}
		if (arg0 != null) {
			int index = msg.indexOf("{0}");
			if (index > -1) {
				msg = msg.substring(0, index) + arg0 + msg.substring(index + 3);
			}
		}
		if (arg1 != null) {
			int index = msg.indexOf("{1}");
			if (index > -1) {
				msg = msg.substring(0, index) + arg1 + msg.substring(index + 3);
			}
		}
		if (arg2 != null) {
			int index = msg.indexOf("{2}");
			if (index > -1) {
				msg = msg.substring(0, index) + arg2 + msg.substring(index + 3);
			}
		}
		if (arg3 != null) {
			int index = msg.indexOf("{3}");
			if (index > -1) {
				msg = msg.substring(0, index) + arg3 + msg.substring(index + 3);
			}
		}
		return msg;
	}

	/**
	 * ���l�`�F�b�N
	 * @param src
	 * @return
	 */
	public static boolean isDigit(String src) {
		for (int i = 0; i < src.toCharArray().length; i++) {
			if (i == 0 && src.charAt(i) == '-' || src.charAt(i) == '.') {
				continue;
			}
			if (!Character.isDigit(src.charAt(i))) {
				return false;
			}
		}
		return true;
	}
	// --------------------------------------------------------------- for Test
	/**
	 * �e�X�g�̂��߂ł�
	 * @param args
	 */
	//	public static void main(String[] args){
	//		try{
	//			System.out.println(DrasapUtil.formatDrwgNo("1"));
	//			System.out.println(DrasapUtil.formatDrwgNo("12"));
	//			System.out.println(DrasapUtil.formatDrwgNo("123"));
	//			System.out.println(DrasapUtil.formatDrwgNo("123456789"));
	//			System.out.println(DrasapUtil.formatDrwgNo("1234567890"));
	//			System.out.println(DrasapUtil.formatDrwgNo("12345678901"));
	//			System.out.println(DrasapUtil.formatDrwgNo("123456789012"));
	//			System.out.println(DrasapUtil.formatDrwgNo("1234567890123"));
	//			System.exit(0);
	//
	//		} catch(Exception e){
	//			e.printStackTrace();
	//			System.exit(1);
	//		}
	//	}

	// 2013.07.09 yamagishi add. start
	/**
	 * NULL�̏ꍇ�A�󕶎���Ԃ�
	 * @see org.apache.commons.lang.StringUtils
	 */
	public static String defaultString(String str) {
		return str != null ? str : "";
	}

	// 2013.07.09 yamagishi add. end
	// 2020.03.16 yamamoto add. start
	/**
	 * �t�@�C������уt�H���_���폜����
	 * @param file
	 * @return boolean
	 */
	public static boolean deleteFile(File file) {
		if (!file.exists()) {
			return false;
		}

		if (file.isFile()) {
			return file.delete();
		}
		// �t�H���_�̏ꍇ�A�S�t�@�C�����폜
		File[] files = file.listFiles();
		for (File f : files) {
			// �ċA����
			DrasapUtil.deleteFile(f);

		}
		// ���t�H���_���폜
		return file.delete();
	}
	// 2020.03.16 yamamoto add. end

	/**
	 * �e���|�����̃t�H���_���擾���āA���݂��Ȃ��ꍇ�쐬����
	 * @param request
	 * @return
	 */
	public static String getRealTempPath(HttpServletRequest request) {
		String tempDirName = null;
		try {
			ServletContext context = request.getServletContext();
			tempDirName = context.getRealPath("temp");// �e���|�����̃t�H���_�̃t���p�X
			File tempDir = new File(tempDirName);
			if (!tempDir.exists()) {
				// �f�B���N�g�������݂��Ȃ��ꍇ�A�f�B���N�g�����쐬
				tempDir.mkdirs();
			}
		} catch (Exception e) {
		}
		return tempDirName;
	}

	/**
	 * �Ō�̃J���}����菜��
	 * @param str
	 * @return
	 */
	public static String removeLastComma(String str) {
		String val = StringUtils.isEmpty(str) ? "" : str;
		if (val.charAt(str.length() - 1) == ',') {
			return val.substring(0, val.length() - 1);
		}
		return val;
	}
}
