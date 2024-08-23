package tyk.drasap.viewlog;

import java.text.NumberFormat;

import org.apache.log4j.Category;

/**
 * Webからのビューイングでのログを出力するクラス。
 * 度々、ビューイングでバグが発生したため、ログをとるために用意した。
 * @author fumi
 */
public class ViewLoger {
	private static Category category = Category.getInstance(ViewLoger.class.getName());
	private static NumberFormat nf = NumberFormat.getInstance();

	/**
	 * エラーLevelでログする。
	 * メモリ情報はプリントされる。
	 * @param drwgNo
	 * @param message
	 */	
	public static void error(String drwgNo, String message){
		error(drwgNo, message, true);
	}
	/**
	 * エラーLevelでログする。
	 * @param drwgNo
	 * @param message
	 * @param printMemoryCondition メモリ情報をプリントするなら true
	 */
	public static void error(String drwgNo, String message, boolean printMemoryCondition){
		category.error(createMessage(drwgNo, message, printMemoryCondition));
	}
	/**
	 * インフォメーションLevelでログする。
	 * メモリ情報はプリントされる。
	 * @param drwgNo
	 * @param message
	 */	
	public static void info(String drwgNo, String message){
		info(drwgNo, message, true);
	}
	/**
	 * インフォメーションLevelでログする。
	 * @param drwgNo
	 * @param message
	 * @param printMemoryCondition メモリ情報をプリントするなら true
	 */
	public static void info(String drwgNo, String message, boolean printMemoryCondition){
		category.info(createMessage(drwgNo, message, printMemoryCondition));
	}
	/**
	 * 与えられた情報を元に、メッセージを作成する。
	 * @param drwgNo
	 * @param message
	 * @param printMemoryCondition メモリ情報をプリントするなら true
	 * @return
	 */
	private static String createMessage(String drwgNo, String message, boolean printMemoryCondition){
		StringBuffer sb = new StringBuffer();
		// Java 仮想マシン内の空きメモリの量をkb単位で
		if(printMemoryCondition){
			sb.append(" Free=");
			sb.append(nf.format(Runtime.getRuntime().freeMemory()/1024));
			sb.append("kb; ");
			sb.append("Total=");
			sb.append(nf.format(Runtime.getRuntime().totalMemory()/1024));
			sb.append("kb; ");
			// 1.3.1では未対応
			//sb.append("Max=");
			//sb.append(nf.format(Runtime.getRuntime()..maxMemory()/1024));
			//sb.append("kb; ");
		}
		sb.append("図番");
		sb.append(drwgNo);
		sb.append("; ");
		sb.append(message);
		
		return sb.toString();
	}

}
