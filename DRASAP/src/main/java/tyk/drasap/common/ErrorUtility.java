package tyk.drasap.common;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Exceptionなどのユーティリティクラス
 */
public class ErrorUtility {
	/**
	 * Exceptionを受け取って、printStackTraceをStringにして返す
	 */
	public static String error2String(Throwable ex) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);

		return sw.toString();
	}

}
