package tyk.drasap.acslog;

import org.apache.log4j.Category;

import tyk.drasap.common.User;
/**
 * <PRE>
 * �A�N�Z�X���O���o�͂���N���X�B
 * �ǂ�����ł����ʂɎg�p�����B
 * '04.Nov.23 �}�Ԃ����O����̂ɑΉ�
 * 2005-Mar-4 �V�X�e��ID�ɑΉ�
 * </PRE>
 * �ŏI�ύX $Date: 2005/03/18 04:33:22 $ $Author: fumi $
 * @version 2013/06/24 yamagishi
 */
public class AccessLoger {
	private static Category category = Category.getInstance(AccessLoger.class.getName());
	// ���M���O�Ŏg�p����FunctionID
	public static String FID_SEARCH		= "01";// ����
	public static String FID_DISP_DRWG		="02";// �}�ʕ\��
	public static String FID_OUT_DRWG		= "03";// �o��
	public static String FID_GENZ_REQ		= "04";// ���}�ɍ�ƈ˗�
	public static String FID_GENZ_RES		= "05";// ���}�ɍ�Ɗ�������
	public static String FID_CHG_ACL		= "06";// �A�N�Z�X���ύX
	public static String FID_SAVE			= "07";// �ۑ�		// 2013.07.11 yamagishi add.
	public static String FID_DEL_DRWG		= "99";// �}�ԍ폜

	// ------------------------------------------- Method
	/**
	 * DRASAP�V�X�e���Ƃ��ẴA�N�Z�X���O���o�͂���B
	 * @param user ���[�U�[
	 * @param functionId �@�\ID
	 */
	public static void loging(User user, String functionId){
		// ���b�Z�[�W���쐬���A
		// Log4j�ł́Ainfo�Ƃ��ďo�͂���
		category.info(createLogMsg(user, functionId, null, null));
	}
	/**
	 * DRASAP�V�X�e���Ƃ��ẴA�N�Z�X���O���o�͂���B
	 * @param user ���[�U�[
	 * @param functionId �@�\ID
	 * @param drwgNo �}�� �n�C�t��������
	 */
	public static void loging(User user, String functionId, String drwgNo){
		// ���b�Z�[�W���쐬���A
		// Log4j�ł́Ainfo�Ƃ��ďo�͂���
		category.info(createLogMsg(user, functionId, drwgNo, null));
	}
	/**
	 * DRASAP�V�X�e���Ƃ��ẴA�N�Z�X���O���o�͂���B
	 * @param user ���[�U�[
	 * @param functionId �@�\ID
	 * @param drwgNo �}�� �n�C�t��������
	 * @param sysId �V�X�e��ID�Bnull�B
	 */
	public static void loging(User user, String functionId, String drwgNo, String sysId){
		// ���b�Z�[�W���쐬���A
		// Log4j�ł́Ainfo�Ƃ��ďo�͂���
		category.info(createLogMsg(user, functionId, drwgNo, sysId));
	}
	/**
	 * DRASAP�V�X�e���Ƃ��ẴA�N�Z�X���O���o�͂���B
	 * �z��̒����������M���O����B
	 * @param user ���[�U�[
	 * @param functionId �@�\ID
	 * @param drwgNoArray �}�� �n�C�t��������
	 */
	public static void loging(User user, String functionId, String[] drwgNoArray){
		// �z��̒����������M���O����B
		for(int i = 0; i < drwgNoArray.length; i++){
			loging(user, functionId, drwgNoArray[i]);
		}
	}
	public static void loging(User user, String functionId, String[] drwgNoArray, String sysId){
		// �z��̒����������M���O����B
		for(int i = 0; i < drwgNoArray.length; i++){
			loging(user, functionId, drwgNoArray[i], sysId);
		}
	}
	/**
	 * �A�N�Z�X���O�̃��b�Z�[�W���쐬����B
	 * �uYYMMDDhhmmss,�v�ɂ��Ă�
	 * "log4j.properties"�Ƀp�^�[���o�^����Ă���B
	 * @param user ���[�U�[
	 * @param functionId �@�\ID
	 * @param drwgNo �}�ԁBnull�B
	 * @param sysId �V�X�e��ID�Bnull�B2005-Mar-4�ǉ��B���̃V�X�e������̌Ăяo���ɑΉ����邽�߁B
	 * @return ���M���O�̂��߂̃��b�Z�[�W
	 */
	private static String createLogMsg(User user, String functionId, String drwgNo, String sysId){
		// �uYYMMDDhhmmss,�v�ɂ��Ă�
		// "log4j.properties"�Ƀp�^�[���o�^����Ă���
		StringBuffer buff = new StringBuffer();
		buff.append(user.getHost());// �z�X�g��
		buff.append(',');
		buff.append(user.getId());// ���[�U�[ID
		buff.append(',');
		buff.append(user.getName());// ���O
		buff.append(',');
// 2013.06.24 yamagishi add. start
		buff.append(user.getDeptName());// ������
		buff.append(',');
// 2013.06.24 yamagishi add. end
		buff.append(functionId);// �@�\ID
		// drwgNo������ꍇ�́A�Ō�ɂ���
		if(drwgNo!=null){
			buff.append(',');
			buff.append(drwgNo);
			// sysId������ꍇ�́A����ɕt��
			// 2005-Mar-4�ύX by Hirata.
			if(sysId!=null){
				buff.append(',');
				buff.append(sysId);
			}
		}
		return buff.toString();
	}

}
