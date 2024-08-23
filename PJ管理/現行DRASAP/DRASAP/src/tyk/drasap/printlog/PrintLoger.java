package tyk.drasap.printlog;

import org.apache.log4j.Category;

import tyk.drasap.common.User;

/**
 * <PRE>
 * �Q�l�}�o�͂Ɋւ��Ẵ��M���O����N���X�B
 * �ߋ��ɉ��x���d�����āAPRINT_REQUEST_TABLE�ɏ������܂ꂽ���Ƃ�����A
 * ����ɑ΂��錴���ǋ����邽�߂̏����W��ړI�Ƃ���B
 * </PRE>
 * @author fumi
 * �쐬�� 2005/03/04
 * �ύX�� $Date: 2005/03/18 04:33:22 $ $Author: fumi $
 * @version 2013/06/24 yamagishi
 */
public class PrintLoger {
	private static Category category = Category.getInstance(PrintLoger.class.getName());
	// ���b�Z�[�W�̎��
	/** �N���C�A���g����̃��N�G�X�g���󂯂��^�C�~���O */
	public static String ACT_RECEIVE =	"Receive   ";
	/** Oracle�̃e�[�u���ɏ����o���^�C�~���O */
	public static String ACT_WRITE =		"Write     ";
	/** Oracle�̃e�[�u���̏����o���Ɏ��s�����^�C�~���O */
	public static String FAILED_WRITE =	"Failed    ";

// 2013.06.24 yamagishi modified. start
//	public static void info(String act, String userId){
//		info(act, userId, null);
//	}
//	/**
//	 * <PRE>
//	 * ���M���O����B
//	 * Log4J�ł́Ainfo���x���ŏo�͂���
//	 * </PRE>
//	 * @param act �����N������?
//	 * @param userId ���[�U�[ID
//	 * @param drwgNo �}�ԁBnull����
//	 */
//	public static void info(String act, String userId, String drwgNo){
//		// Log4J�ł́Ainfo���x���ŏo�͂���
//		category.info(createMessage(act, userId, drwgNo));
//	}
//	/**
//	 * <PRE>
//	 * ���M���O���郁�b�Z�[�W���쐬����B
//	 * YYMMDDHHMMSS�ɂ��ẮAlog4j.properties�Œ�`����B
//	 * </PRE>
//	 * @param act �����N������?
//	 * @param userId ���[�U�[ID
//	 * @param drwgNo �}�ԁBnull����
//	 * @return ���M���O���郁�b�Z�[�W
//	 */
//	private static String createMessage(String act, String userId, String drwgNo){
//		StringBuffer sbMsg = new StringBuffer(act);// �����N������?
//		sbMsg.append(',');
//		sbMsg.append(userId);// ���[�U�[ID
//		// �}�Ԃ��w�肳��Ă�����A�}�Ԃ����M���O����
//		if(drwgNo!=null){
//			sbMsg.append(',');
//			sbMsg.append(drwgNo);// �}��
//		}
//
//		return sbMsg.toString();
//	}
	public static void info(String act, User user){
		info(act, user, null);
	}
	/**
	 * <PRE>
	 * ���M���O����B
	 * Log4J�ł́Ainfo���x���ŏo�͂���
	 * </PRE>
	 * @param act �����N������?
	 * @param user ���[�U�[
	 * @param drwgNo �}�ԁBnull����
	 */
	public static void info(String act, User user, String drwgNo){
		// Log4J�ł́Ainfo���x���ŏo�͂���
		category.info(createMessage(act, user, drwgNo));
	}
	/**
	 * <PRE>
	 * ���M���O���郁�b�Z�[�W���쐬����B
	 * YYMMDDHHMMSS�ɂ��ẮAlog4j.properties�Œ�`����B
	 * </PRE>
	 * @param act �����N������?
	 * @param user ���[�U�[
	 * @param drwgNo �}�ԁBnull����
	 * @return ���M���O���郁�b�Z�[�W
	 */
	private static String createMessage(String act, User user, String drwgNo){
		StringBuffer sbMsg = new StringBuffer(act);// �����N������?
		sbMsg.append(',');
		sbMsg.append(user.getId());// ���[�U�[ID
		sbMsg.append(',');
		sbMsg.append(user.getDept());// �����R�[�h
		sbMsg.append(',');
		sbMsg.append(user.getDeptName());// ������
		// �}�Ԃ��w�肳��Ă�����A�}�Ԃ����M���O����
		if(drwgNo!=null){
			sbMsg.append(',');
			sbMsg.append(drwgNo);// �}��
		}

		return sbMsg.toString();
	}
// 2013.06.24 yamagishi modified. end

}
