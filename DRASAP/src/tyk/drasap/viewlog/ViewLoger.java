package tyk.drasap.viewlog;

import java.text.NumberFormat;

import org.apache.log4j.Category;

/**
 * Web����̃r���[�C���O�ł̃��O���o�͂���N���X�B
 * �x�X�A�r���[�C���O�Ńo�O�������������߁A���O���Ƃ邽�߂ɗp�ӂ����B
 * @author fumi
 */
public class ViewLoger {
	private static Category category = Category.getInstance(ViewLoger.class.getName());
	private static NumberFormat nf = NumberFormat.getInstance();

	/**
	 * �G���[Level�Ń��O����B
	 * ���������̓v�����g�����B
	 * @param drwgNo
	 * @param message
	 */	
	public static void error(String drwgNo, String message){
		error(drwgNo, message, true);
	}
	/**
	 * �G���[Level�Ń��O����B
	 * @param drwgNo
	 * @param message
	 * @param printMemoryCondition �����������v�����g����Ȃ� true
	 */
	public static void error(String drwgNo, String message, boolean printMemoryCondition){
		category.error(createMessage(drwgNo, message, printMemoryCondition));
	}
	/**
	 * �C���t�H���[�V����Level�Ń��O����B
	 * ���������̓v�����g�����B
	 * @param drwgNo
	 * @param message
	 */	
	public static void info(String drwgNo, String message){
		info(drwgNo, message, true);
	}
	/**
	 * �C���t�H���[�V����Level�Ń��O����B
	 * @param drwgNo
	 * @param message
	 * @param printMemoryCondition �����������v�����g����Ȃ� true
	 */
	public static void info(String drwgNo, String message, boolean printMemoryCondition){
		category.info(createMessage(drwgNo, message, printMemoryCondition));
	}
	/**
	 * �^����ꂽ�������ɁA���b�Z�[�W���쐬����B
	 * @param drwgNo
	 * @param message
	 * @param printMemoryCondition �����������v�����g����Ȃ� true
	 * @return
	 */
	private static String createMessage(String drwgNo, String message, boolean printMemoryCondition){
		StringBuffer sb = new StringBuffer();
		// Java ���z�}�V�����̋󂫃������̗ʂ�kb�P�ʂ�
		if(printMemoryCondition){
			sb.append(" Free=");
			sb.append(nf.format(Runtime.getRuntime().freeMemory()/1024));
			sb.append("kb; ");
			sb.append("Total=");
			sb.append(nf.format(Runtime.getRuntime().totalMemory()/1024));
			sb.append("kb; ");
			// 1.3.1�ł͖��Ή�
			//sb.append("Max=");
			//sb.append(nf.format(Runtime.getRuntime()..maxMemory()/1024));
			//sb.append("kb; ");
		}
		sb.append("�}��");
		sb.append(drwgNo);
		sb.append("; ");
		sb.append(message);
		
		return sb.toString();
	}

}
