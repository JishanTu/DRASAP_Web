package tyk.drasap.common;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Exception�Ȃǂ̃��[�e�B���e�B�N���X
 */
public class ErrorUtility {
	/**
	 * Exception���󂯎���āAprintStackTrace��String�ɂ��ĕԂ�
	 */
	public static String error2String(Throwable ex) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);

		return sw.toString();
	}

}
