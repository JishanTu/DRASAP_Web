package tyk.drasap.errlog;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import tyk.drasap.common.DrasapPropertiesFactory;
import tyk.drasap.common.User;

/**
 * �G���[���O���o�͂���N���X�B
 * �ǂ�����ł����ʂɎg�p�����B
 */
public class ErrorLoger {
	private static Logger category = Logger.getLogger(ErrorLoger.class.getName());

	// ------------------------------------------- Method
	/**
	 * DRASAP�V�X�e���Ƃ��ẴG���[���O���o�͂���B
	 */
	public static void error(User user, Object screenObj, String errorId) {
		error(user, screenObj, errorId, null);
	}

	/**
	 * DRASAP�V�X�e���Ƃ��ẴG���[���O���o�͂���B
	 */
	public static void error(User user, Object screenObj, String errorId, String sysId) {
		error(user, screenObj, errorId, sysId, null);
	}

	/**
	 * DRASAP�V�X�e���Ƃ��ẴG���[���O���o�͂���B
	 */
	public static void error(User user, Object screenObj, String errorId, String sysId, String extuserid) {
		String userId = user.getId();
		if (StringUtils.isEmpty(userId)) {
			userId = StringUtils.isEmpty(extuserid) ? "" : extuserid;
		}
		// �uYYMMDDhhmmss,�v�ɂ��Ă�
		// "log4j.properties"�Ƀp�^�[���o�^����Ă���
		StringBuilder buff = new StringBuilder();
		buff.append(user.getHost());// �z�X�g��
		buff.append(',');
		buff.append(userId);// ���[�U�[ID
		buff.append(',');
		buff.append(user.getName());// ���O
		buff.append(',');
		buff.append(DrasapPropertiesFactory.getScreenId(screenObj));// ���ID
		buff.append(',');
		buff.append(errorId);// �G���[ID
		if (sysId != null) {
			buff.append(',');
			buff.append(sysId);
		}
		// Log4j�ł́Ainfo�Ƃ��ďo�͂���
		category.info(buff.toString());
	}

}
