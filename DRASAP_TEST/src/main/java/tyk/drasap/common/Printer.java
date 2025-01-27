package tyk.drasap.common;

/**
 * プリンターを表す。
 * プリンターリスト(PRINTER_LIST)に対応する。
 */
public class Printer {
	String id = "";// プリンタID
	String name = "";// プリンタ名
	String displayName = "";// 表示名
	boolean display;// 原図庫作業依頼で表示するならtrue
	String maxSize = "";// 最大出力サイズ
	boolean printableA0;// A0が印刷可能なら true
	boolean printableA1;// A1
	boolean printableA2;// A2
	boolean printableA3;// A3
	boolean printableA4;// A4
	boolean printableA0L;// A0L
	boolean printableA1L;// A1L
	boolean printableA2L;// A2L
	boolean printableA3L;// A3L '04.Oct.19追加
	String clasify;// 区分
	// ------------------------------------------------------- コンストラクタ

	/**
	 * コンストラクタ
	 */
	public Printer(String newId, String newName, String newDisplayName, String newDisplayFlag,
			String newMaxSize,
			String newPrintableA0, String newPrintableA1, String newPrintableA2, String newPrintableA3,
			String newPrintableA4, String newPrintableA0L, String newPrintableA1L, String newPrintableA2L,
			String newPrintableA3L,
			String newClasify) {
		id = newId;
		name = newName;
		displayName = newDisplayName;
		if (displayName == null) {
			displayName = "";
		}
		display = "1".equals(newDisplayFlag);
		maxSize = newMaxSize;
		printableA0 = "1".equals(newPrintableA0);
		printableA1 = "1".equals(newPrintableA1);
		printableA2 = "1".equals(newPrintableA2);
		printableA3 = "1".equals(newPrintableA3);
		printableA4 = "1".equals(newPrintableA4);
		printableA0L = "1".equals(newPrintableA0L);
		printableA1L = "1".equals(newPrintableA1L);
		printableA2L = "1".equals(newPrintableA2L);
		printableA3L = "1".equals(newPrintableA3L);
		clasify = newClasify;
	}

	/**
	 * 指定したサイズが印刷可能か?
	 * @param printSize A0-A4, A0L-A2L
	 * @return 印刷可能なら true
	 */
	public boolean isPrintable(String printSize) {
		if ("A0".equals(printSize)) {
			return printableA0;
		}
		if ("A1".equals(printSize)) {
			return printableA1;
		}
		if ("A2".equals(printSize)) {
			return printableA2;
		}
		if ("A3".equals(printSize)) {
			return printableA3;
		}
		if ("A4".equals(printSize)) {
			return printableA4;
		}
		if ("A0L".equals(printSize)) {
			return printableA0L;
		}
		if ("A1L".equals(printSize)) {
			return printableA1L;
		}
		if ("A2L".equals(printSize)) {
			return printableA2L;
		}
		if ("A3L".equals(printSize)) {
			return printableA3L;
		}
		throw new IllegalArgumentException("指定した図面サイズが正しくありません。{" + printSize + "}");
	}

	/**
	 * EUCプリンタなら true。
	 * 判定はclassifyで行う。
	 * @return
	 */
	public boolean isEucPrinter() {
		return "EUC".equals(clasify);
	}

	// ------------------------------------------------------- Method

	// ------------------------------------------------------- getter,setter
	/**
	 * @return
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @return
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return
	 */
	public boolean isDisplay() {
		return display;
	}

	/**
	 * @return
	 */
	public String getMaxSize() {
		return maxSize;
	}

	/**
	 * @return
	 */
	public boolean isPrintableA0() {
		return printableA0;
	}

	/**
	 * @return
	 */
	public boolean isPrintableA0L() {
		return printableA0L;
	}

	/**
	 * @return
	 */
	public boolean isPrintableA1() {
		return printableA1;
	}

	/**
	 * @return
	 */
	public boolean isPrintableA1L() {
		return printableA1L;
	}

	/**
	 * @return
	 */
	public boolean isPrintableA2() {
		return printableA2;
	}

	/**
	 * @return
	 */
	public boolean isPrintableA2L() {
		return printableA2L;
	}

	/**
	 * @return
	 */
	public boolean isPrintableA3() {
		return printableA3;
	}

	/**
	 * @return
	 */
	public boolean isPrintableA4() {
		return printableA4;
	}

	/**
	 * @return printableA3L を戻します。
	 */
	public boolean isPrintableA3L() {
		return printableA3L;
	}
}
