package tyk.drasap.common;

import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * ���̃V�X�e���̃v���p�e�B�ł���drasap.properties��ǂݍ���ŕԂ��B
 * �ŏ���1�x�����ǂݍ��ށB
 *
 * @version 2013/06/13 yamagishi
 */
@Component
public class DrasapPropertiesFactory {
	private static Properties drasapProp = null;
	private final static java.lang.String TARGET_FILE_NAME = "drasap.properties";
	// 2013.06/13 yamagishi add. start
	public static final String BEA_HOME = "BEA_HOME";
	public static final String CATALINA_HOME = "CATALINA_HOME";
	public static final String OCE_AP_SERVER_HOME = "OCE_AP_SERVER_HOME";
	public static final String OCE_AP_SERVER_BASE = "oce.AP_SERVER_BASE";
	// 2013.06/13 yamagishi add. end

	private static String serverHomePath = null;

	public DrasapPropertiesFactory() {
		super();
		try {
			if (serverHomePath == null) {
				serverHomePath = System.getenv(BEA_HOME);
			}
			if (serverHomePath == null) {
				serverHomePath = System.getenv(CATALINA_HOME);
			}
			if (serverHomePath == null) {
				serverHomePath = System.getenv(OCE_AP_SERVER_HOME);
			}
			if (serverHomePath == null) {
				serverHomePath = DrasapPropertiesFactory.getDrasapProperties(this).getProperty(OCE_AP_SERVER_BASE);
			}
		} catch (Exception e) {
			return;
		}
	}

	// ------------------------------------------------- Method
	/**
	 * �N���X���[�_�[���g�p���āA���̃V�X�e���̃v���p�e�B�ł���drasap.properties��ǂݍ���ŕԂ��B
	 * �ŏ���1�x�����ǂݍ��ށB
	 * @param obj ���̃I�u�W�F�N�g�̃N���X���[�_�[���g�p����
	 */
	public static Properties getDrasapProperties(Object obj) {
		if (drasapProp == null) {
			try {
				drasapProp = new Properties();
				drasapProp.load(obj.getClass().getClassLoader().getResourceAsStream(TARGET_FILE_NAME));
			} catch (Exception e) {
				// ��O������������Anull���Z�b�g����
				drasapProp = null;
			}
		}
		return drasapProp;
	}

	/**
	 * �w�肵���I�u�W�F�N�g�̉��ID���擾����B
	 * @param screenObj
	 * @return ���ID
	 */
	public static String getScreenId(Object screenObj) {
		// �V�X�e���̃v���p�e�B���擾���āA
		// �I�u�W�F�N�g�̊��S�N���X�����L�[�ɂ��āA���ID���擾
		return getDrasapProperties(screenObj).getProperty(screenObj.getClass().getName());
	}

	public static String getFullPath(String key) {
		if (StringUtils.isBlank(key)) {
			return serverHomePath;
		}
		return serverHomePath + DrasapPropertiesFactory.getDrasapProperties(new DrasapPropertiesFactory()).getProperty(key);
	}
}
