package tyk.drasap.common;

import java.io.File;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

/**
 * ユーティリティをまとめたクラス。
 * このシステムでのみ使用しそうなユーティリティ。
 */
public class DrasapUtil {
	// メソッドcreateCharArrayで使用する。追番を定義した文字列。
	// I(アイ) O(オー) Q(キュー) X(エックス)は禁止文字。
	private final static String OIBAN_RULE = "0123456789ABCDEFGHJKLMNPRSTUVWYZ";

	// 図面サイズの比較をするときの基準となる、HashMap
	// A1L,A2Lを追加 '04.Mar.2 by Hirata
	// A3Lを追加 '04.Oct.19 by Hirata
	// ** 注意 ***************************
	// これを変更した場合、必ずdecidePrintSizeForRequest()を見直すこと
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
	 * 追番を返すメソッド。
	 * @param fromChar
	 * @param toChar
	 * @return
	 */
	public static char[] createOibanArray(char fromChar, char toChar) {
		int fromIdx = OIBAN_RULE.indexOf(fromChar);// fromCharのインデックス
		int toIdx = OIBAN_RULE.indexOf(toChar);// toCharのインデックス

		if (fromIdx == -1 || toIdx == -1) {
			// 使用可能な文字か
			throw new IllegalArgumentException("指定可能な文字は 0-9 または A-Zです。(IOQXを除く)");
		}
		if (fromIdx > toIdx) {
			// from > to ではないか
			throw new IllegalArgumentException("from <= to となるように指定してください。");
		}
		char[] oibanArray = new char[toIdx - fromIdx + 1];
		for (int i = 0; i < oibanArray.length; i++) {
			oibanArray[i] = OIBAN_RULE.charAt(fromIdx + i);
		}
		return oibanArray;
	}

	/**
	 * 図面サイズを比較する。
	 * size1 > size2 のとき正。
	 * size1 = size2 のとき 0。
	 * size1 < size2 のとき負。
	 * 図面サイズとして不正なものを指定すると、IllegalArgumentExceptionが発生する
	 * @param size1 図面サイズ(A0L,A0,A1L,A1,A2L,A2,A3,A4)のいずれか。
	 * @param size2
	 * @return
	 */
	public static int compareDrwgSize(String size1, String size2) {
		return convertDrwgSizeToInt(size1) - convertDrwgSizeToInt(size2);
	}

	/**
	 * 図面サイズをdrwgSizeMapを使用して、intに変換。
	 * 大きい図面ほど大きい数字となる。
	 * @param size
	 * @return
	 */
	private static int convertDrwgSizeToInt(String size) {
		String sizeInt = drwgSizeMap.get(size);
		if (sizeInt == null) {
			throw new IllegalArgumentException("指定した図面サイズが正しくありません。{" + size + "}");
		}
		return Integer.parseInt(sizeInt);
	}

	/**
	 * 原図のサイズ、指定したサイズ、印刷したいプリンタの組み合わせで、
	 * 参考図出力用テーブルの出力サイズにセットするサイズを求める。
	 * 出力サイズの指定が悪い場合、nullを返す。
	 * @param drwgSize 原図のサイズ
	 * @param specifiedSize 指定したサイズ
	 * @param printer 印刷したいプリンタ
	 * @return 参考図出力用テーブルの出力サイズ。出力サイズの指定が悪い場合、nullを返す。
	 */
	public static String decidePrintSizeForRequest(String drwgSize, String specifiedSize, Printer printer) {
		if ("ORG".equals(specifiedSize)) {
			// 原寸を指定されている場合、////////////////////////////////////////////
			if (printer.isPrintable(drwgSize)) {
				// 図面サイズが印刷可能なら、ORGを返す
				return specifiedSize;
			}
			if ("A0L".equals(drwgSize) && printer.isPrintableA0()) {
				// 図面サイズ=A0Lで、A0が印刷可能なら、A0を返す
				return "A0";
			}
			return null;// 出力サイズ指定エラー
		}
		if (specifiedSize.endsWith("%")) {
			// サイズが'%'で終わっている場合、////////////////////////////////////////////
			// 50%以外に、70.7%,35.4%,25%にも対応
			try {
				// ??%サイズを求める
				String sizeXX = convertDrwgSizeToXX(specifiedSize, drwgSize);
				if (printer.isPrintable(sizeXX)) {
					// ??%サイズが印刷可能なら
					return sizeXX;
				}
				return null;// 出力サイズ指定エラー
			} catch (IllegalArgumentException e) {
				// ??%サイズを求められない
				return null;// 出力サイズ指定エラー
			}
		}
		// A0-A4を指定されている場合、/////////////////////////////////////////////
		String targetSize = specifiedSize;// 出力サイズ
		if (compareDrwgSize(drwgSize, specifiedSize) < 0) {
			// 図面サイズ < 指定サイズ なら、図面サイズに置き換える
			targetSize = drwgSize;
		}
		// そのサイズを int に変換・・・switch,caseを使用したいため
		// switch,caseを使用することにより、そのサイズが印刷不可なら
		// そのプリンタで、次に最も大きな印刷可能なサイズが戻る
		int targetSizeInt = convertDrwgSizeToInt(targetSize);
		switch (targetSizeInt) {
		case 8:
		case 7: // A0LまたはA0のとき
			if (printer.isPrintableA0()) {
				return "A0";
			}
		case 5: // A1のとき
			if (printer.isPrintableA1()) {
				return "A1";
			}
		case 3: // A2のとき
			if (printer.isPrintableA2()) {
				return "A2";
			}
		case 1: // A3のとき
			if (printer.isPrintableA3()) {
				return "A3";
			}
		case 0: // A4のとき
			if (printer.isPrintableA4()) {
				return "A4";
			}
		default:
			return null;// 出力サイズ指定エラー
		}
	}

	/**
	 * 指定した図面サイズを縮小したサイズを返す。
	 * 縮小できない場合、IllegalArgumentException を返す。
	 * @param ratio 縮小サイズ。指定可能な範囲は{70.7%,50%,35.4%,25%}
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
			throw new IllegalArgumentException("指定した図面サイズを70.7%にすることはできません。{" + drwgSize + "}");
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
			throw new IllegalArgumentException("指定した図面サイズを50%にすることはできません。{" + drwgSize + "}");
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
			throw new IllegalArgumentException("指定した図面サイズを35.4%にすることはできません。{" + drwgSize + "}");
		}
		if (!"25%".equals(ratio)) {
			throw new IllegalArgumentException("指定した縮小サイズが正しくありません。{" + ratio + "}");
		}
		if ("A0".equals(drwgSize)) {
			return "A4";
		}
		throw new IllegalArgumentException("指定した図面サイズを25%にすることはできません。{" + drwgSize + "}");
	}

	/**
	 * 図番をフォーマットする。
	 * 12桁なら、そのまま返す。
	 * それ以外はXX-XXXXXXXX-X...として返す。
	 * @param drwgNo
	 * @return
	 */
	public static String formatDrwgNo(String drwgNo) {
		if (drwgNo == null || drwgNo.length() == 12) {
			// 12ケタならそのまま返す
			return drwgNo;
		}
		// それ以外
		StringBuilder sb = new StringBuilder();
		if (drwgNo.length() <= 2) {
			// 長さが2以下なら
			sb.append(drwgNo);
		} else {
			// 長さが2より長い
			sb.append(drwgNo.substring(0, 2));
			sb.append('-');
			if (drwgNo.length() <= 10) {
				// 長さが10以下なら
				sb.append(drwgNo.substring(2));
			} else {
				// 長さが10より長いなら
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
	 * 数値チェック
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
	 * テストのためです
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
	 * NULLの場合、空文字を返す
	 * @see org.apache.commons.lang.StringUtils
	 */
	public static String defaultString(String str) {
		return str != null ? str : "";
	}

	// 2013.07.09 yamagishi add. end
	// 2020.03.16 yamamoto add. start
	/**
	 * ファイルおよびフォルダを削除する
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
		// フォルダの場合、全ファイルを削除
		File[] files = file.listFiles();
		for (File f : files) {
			// 再帰処理
			DrasapUtil.deleteFile(f);

		}
		// 自フォルダを削除
		return file.delete();
	}
	// 2020.03.16 yamamoto add. end

	/**
	 * テンポラリのフォルダを取得して、存在しない場合作成する
	 * @param request
	 * @return
	 */
	public static String getRealTempPath(HttpServletRequest request) {
		String tempDirName = null;
		try {
			ServletContext context = request.getServletContext();
			tempDirName = context.getRealPath("temp");// テンポラリのフォルダのフルパス
			File tempDir = new File(tempDirName);
			if (!tempDir.exists()) {
				// ディレクトリが存在しない場合、ディレクトリを作成
				tempDir.mkdirs();
			}
		} catch (Exception e) {
		}
		return tempDirName;
	}

	/**
	 * 最後のカンマを取り除く
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
