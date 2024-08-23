package tyk.drasap.errlog;

import org.apache.log4j.Category;

import tyk.drasap.common.DrasapPropertiesFactory;
import tyk.drasap.common.User;
/**
 * �G���[���O���o�͂���N���X�B
 * �ǂ�����ł����ʂɎg�p�����B
 */
public class ErrorLoger {
	private static Category category = Category.getInstance(ErrorLoger.class.getName());
	
	// ------------------------------------------- Method
	/**
	 * DRASAP�V�X�e���Ƃ��ẴG���[���O���o�͂���B
	 */
	public static void error(User user, Object screenObj, String errorId){
		// �uYYMMDDhhmmss,�v�ɂ��Ă�
		// "log4j.properties"�Ƀp�^�[���o�^����Ă���
		StringBuffer buff = new StringBuffer();
		buff.append(user.getHost());// �z�X�g��
		buff.append(',');
		buff.append(user.getId());// ���[�U�[ID
		buff.append(',');
		buff.append(user.getName());// ���O
		buff.append(',');
		buff.append(DrasapPropertiesFactory.getScreenId(screenObj));// ���ID
		buff.append(',');
		buff.append(errorId);// �G���[ID
		// Log4j�ł́Ainfo�Ƃ��ďo�͂���
		category.info(buff.toString());
	}
	/**
	 * DRASAP�V�X�e���Ƃ��ẴG���[���O���o�͂���B
	 */
	public static void error(User user, Object screenObj, String errorId, String sysId){
		// �uYYMMDDhhmmss,�v�ɂ��Ă�
		// "log4j.properties"�Ƀp�^�[���o�^����Ă���
		StringBuffer buff = new StringBuffer();
		buff.append(user.getHost());// �z�X�g��
		buff.append(',');
		buff.append(user.getId());// ���[�U�[ID
		buff.append(',');
		buff.append(user.getName());// ���O
		buff.append(',');
		buff.append(DrasapPropertiesFactory.getScreenId(screenObj));// ���ID
		buff.append(',');
		buff.append(errorId);// �G���[ID
		if(sysId!=null){
			buff.append(',');
			buff.append(sysId);
		}
		// Log4j�ł́Ainfo�Ƃ��ďo�͂���
		category.info(buff.toString());
	}

}
