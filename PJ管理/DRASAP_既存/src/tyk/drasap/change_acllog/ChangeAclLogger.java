package tyk.drasap.change_acllog;

import static tyk.drasap.common.DrasapUtil.defaultString;

import org.apache.log4j.Category;

import tyk.drasap.common.AclUpload;
import tyk.drasap.common.User;
/**
 * <PRE>
 * �A�N�Z�X���x���ύX���O���o�͂���N���X�B
 * �ǂ�����ł����ʂɎg�p�����B
 * </PRE>
 * @author 2013/07/11 yamagishi
 */
public class ChangeAclLogger {
	private static Category category = Category.getInstance(ChangeAclLogger.class.getName());

	// ------------------------------------------- Method
	/**
	 * DRASAP�V�X�e���Ƃ��ẴA�N�Z�X���x���ύX���O���o�͂���B
	 * @param user ���[�U�[
	 * @param aclUpdateNo �Ǘ�NO
	 * @param drwgNo �}��
	 * @param preUpdateAc �ύX�O�A�N�Z�X���x��
	 * @param preUpdateAclName �ύX�O�A�N�Z�X���x����
	 * @param postUpdateAcl �ύX��A�N�Z�X���x��
	 * @param postUpdateAclName �ύX��A�N�Z�X���x����
	 * @param message ���b�Z�[�W
	 */
	public static void logging(User user, String aclUpdateNo, String drwgNo,
			String preUpdateAcl, String preUpdateAclName, String postUpdateAcl, String postUpdateAclName, String message) {
		// ���b�Z�[�W���쐬���A
		// Log4j�ł́Ainfo�Ƃ��ďo�͂���
		category.info(createLogMsg(user, aclUpdateNo, drwgNo, preUpdateAcl, preUpdateAclName, postUpdateAcl, postUpdateAclName, message));
	}

	/**
	 * DRASAP�V�X�e���Ƃ��ẴA�N�Z�X���x���ύX���O���o�͂���B
	 * @param user ���[�U�[
	 * @param aclUpload ACL�A�b�v���[�h�f�[�^�e�[�u���̃I�u�W�F�N�g
	 */
	public static void logging(User user, AclUpload aclUpload) {
		// ���b�Z�[�W���쐬���A
		// Log4j�ł́Ainfo�Ƃ��ďo�͂���
		String aclUpdateNo = aclUpload.getAclUpdateNo();
		String drwgNo = aclUpload.getItemNoShort();
		String preUpdateAcl = aclUpload.getPreUpdateAcl();
		String preUpdateAclName = aclUpload.getPreUpdateAclName();
		String postUpdateAcl = aclUpload.getPostUpdateAcl();
		String postUpdateAclName = aclUpload.getPostUpdateAclName();
		String message = aclUpload.getMessage();
		category.info(createLogMsg(user, aclUpdateNo, drwgNo, preUpdateAcl, preUpdateAclName, postUpdateAcl, postUpdateAclName, message));
	}

	/**
	 * �A�N�Z�X���x���ύX���O�̃��b�Z�[�W���쐬����B
	 * �uYYMMDDhhmmss,�v�ɂ��Ă�
	 * "log4j.properties"�Ƀp�^�[���o�^����Ă���B
	 * @param aclUpdateNo �Ǘ�NO
	 * @param user ���[�U�[
	 * @param drwgNo �}��
	 * @param preUpdateAcl �ύX�O�A�N�Z�X���x��
	 * @param preUpdateAclName �ύX�O�A�N�Z�X���x����
	 * @param postUpdateAcl �ύX��A�N�Z�X���x��
	 * @param postUpdateAclName �ύX��A�N�Z�X���x����
	 * @param message ���b�Z�[�W
	 * @return ���M���O�̂��߂̃��b�Z�[�W
	 */
	private static String createLogMsg(User user, String aclUpdateNo, String drwgNo,
			String preUpdateAcl, String preUpdateAclName, String postUpdateAcl, String postUpdateAclName, String message) {

		// �uYYMMDDhhmmss,�v�ɂ��Ă�
		// "log4j.properties"�Ƀp�^�[���o�^����Ă���
		StringBuffer buff = new StringBuffer();
		buff.append(aclUpdateNo);			// �Ǘ�No
		buff.append(',');
		buff.append(user.getId());			// ���[�U�[ID
		buff.append(',');
		buff.append(user.getName());		// ����
		buff.append(',');
		buff.append(drwgNo);				// �}��
		buff.append(',');
		String procedure = (message != null && message.length() > 0) ? "����" : "�X�V";
		buff.append(procedure);				// ���u
		buff.append(',');
		if (preUpdateAcl != null && preUpdateAcl.length() > 0) {
			buff.append(preUpdateAcl);		// �ύX�O�A�N�Z�X���x��
		}
		if (preUpdateAclName != null && preUpdateAclName.length() > 0) {
			buff.append('�i');
			buff.append(preUpdateAclName);	// �ύX�O�A�N�Z�X���x����
			buff.append('�j');
		}
		buff.append(',');
		if (postUpdateAcl != null && postUpdateAcl.length() > 0) {
			buff.append(postUpdateAcl);		// �ύX��A�N�Z�X���x��
		}
		if (postUpdateAclName != null && postUpdateAclName.length() > 0) {
			buff.append('�i');
			buff.append(postUpdateAclName);	// �ύX��A�N�Z�X���x����
			buff.append('�j');
		}
		buff.append(',');
		buff.append(defaultString(message)); // ���b�Z�[�W
		return buff.toString();
	}
}
