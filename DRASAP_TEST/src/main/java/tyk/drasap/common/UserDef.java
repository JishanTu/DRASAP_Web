package tyk.drasap.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.validation.support.BindingAwareConcurrentModel;

import tyk.drasap.springfw.utils.MessageSourceUtil;

@Component
public class UserDef {

	private static Logger category = Logger.getLogger(UserDef.class.getName());

	/** �p�X���[�h�ŏ����� */
	public static final String PWD_MIN_LEN = "PWD_MIN_LEN";
	/** �p�X���[�h�g�������� */
	public static final String PWD_VAL_ROLE = "PWD_VAL_ROLE";
	/** �p�X���[�h�L���������� */
	public static final String PWD_LMT_DAY = "PWD_LMT_DAY";

	/** �p�X���[�h�ŏ�����(�f�t�H���g) */
	private static final int DEFALUT_PWD_MIN_LEN = 4;
	/** �p�X���[�h�g��������(�f�t�H���g) */
	private static final String DEFALUT_PWD_VAL_ROLE = "";
	/** �p�X���[�h�L����������(�f�t�H���g) */
	private static final int DEFALUT_PWD_LMT_DAY = 120;

	public UserDef() {
	}

	/**
	 * ���b�Z�[�W�t�@�C���̓ǂݍ���
	 *
	 * @param Filepath
	 * @param charSet
	 * @return
	 */
	public String loadMessage(String Filepath, Charset charSet) {
		String str = "";
		List<String> lines = null;
		Path path = null;
		StringBuilder sb = null;

		try {
			String apServerHome = DrasapPropertiesFactory.getFullPath("");
			path = Paths.get(apServerHome + Filepath);
			lines = Files.readAllLines(path, charSet);

			// �s��؂�ŃJ���}����ʂɕ\������邽�߁A��������
			sb = new StringBuilder();
			for (String s : lines) {
				// �s�̏I�[�ɉ��s�R�[�h�ǉ�
				sb.append(s + "&#010;");
			}
			str = sb.toString();

		} catch (NoSuchFileException e) {
			category.error(e);
		} catch (IOException e) {
			category.error(e);
		} catch (Exception e) {
			category.error(e);
		}

		return str;
	}

	/**
	 * �p�X���[�h��`�t�@�C������l�擾
	 * <br>�߂�l�͕ϊ��\�Ȓl��Ԃ�
	 *
	 * @param  errors
	 * @return HashMap
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public HashMap<String, String> getPasswdDefinition(Model errors) throws FileNotFoundException, IOException {
		MessageSource messageSource = MessageSourceUtil.getMessageSource();
		String passwdDefFile = DrasapPropertiesFactory.getFullPath("tyk.passwddef.passwd.path");
		int pwdMinLen = DEFALUT_PWD_MIN_LEN; // �p�X���[�h�ŏ�����
		String pwdValRole = DEFALUT_PWD_VAL_ROLE; // �p�X���[�h�g��������
		int pwdLmtDay = DEFALUT_PWD_LMT_DAY; // �p�X���[�h�L����������

		HashMap<String, String> passwdDefMap = new HashMap<>();
		BufferedReader reader = null;
		String lineData = null;
		String tmpValue = null;

		try {
			File file = new File(passwdDefFile);
			if (!file.exists()) {
				// �t�@�C�������݂��Ȃ��ꍇ�͋�t�@�C���쐬
				file.createNewFile();
			}

			// �t�@�C���ǂݍ���
			reader = new BufferedReader(new FileReader(passwdDefFile));

			while ((lineData = reader.readLine()) != null) {
				if (lineData.startsWith("#") || StringUtils.isBlank(lineData)) {
					continue;
				}
				// �p�X���[�h�ŏ�����
				if (lineData.indexOf(PWD_MIN_LEN + "=") == 0) {
					tmpValue = lineData.replace(PWD_MIN_LEN + "=", "");

					// ���ݒ�܂���10�i���̐���(���R����0)�ȊO�̏ꍇ�͏����l�ݒ�
					if (StringUtils.isNotEmpty(tmpValue) && NumberUtils.isDigits(tmpValue)) {
						try {
							pwdMinLen = Integer.parseInt(tmpValue);
							// �p�X���[�h�ŏ��������ő包���𒴂��Ȃ��悤�ɂ���
							if (pwdMinLen > UserDB.PASSWORD_MAX_LENGTH) {
								pwdMinLen = UserDB.PASSWORD_MAX_LENGTH;
							}
						} catch (NumberFormatException e) {
						}
					}
					continue;

				} // �p�X���[�h�g��������
				if (lineData.indexOf(PWD_VAL_ROLE + "=") == 0) {
					tmpValue = lineData.replace(PWD_VAL_ROLE + "=", "");

					// ���ݒ�̏ꍇ�͏����l�ݒ�
					if (StringUtils.isNotEmpty(tmpValue)) {
						pwdValRole = tmpValue;
					}
					continue;

				} // �p�X���[�h�L����������
				if (lineData.indexOf(PWD_LMT_DAY + "=") == 0) {
					tmpValue = lineData.replace(PWD_LMT_DAY + "=", "");

					// ���ݒ�܂���10�i���̐���(���R����0)�ȊO�̏ꍇ�͏����l�ݒ�
					if (StringUtils.isNotEmpty(tmpValue) && NumberUtils.isDigits(tmpValue)) {
						try {
							pwdLmtDay = Integer.parseInt(tmpValue);
						} catch (NumberFormatException e) {
						}
					}
					continue;
				}
			}

			passwdDefMap.put(PWD_MIN_LEN, Integer.toString(pwdMinLen));
			passwdDefMap.put(PWD_VAL_ROLE, pwdValRole);
			passwdDefMap.put(PWD_LMT_DAY, Integer.toString(pwdLmtDay));

		} catch (FileNotFoundException e) {
			MessageSourceUtil.addAttribute(errors, "message",
					messageSource.getMessage("search.failed.view.file.notound", new Object[] { e.getMessage() }, null));
			category.error(errors.toString());
			throw e;

		} catch (IOException e) {
			MessageSourceUtil.addAttribute(errors, "message", messageSource
					.getMessage("search.failed.view.file.IOExceltion", new Object[] { e.getMessage() }, null));
			category.error(errors.toString());
			throw e;

		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e1) {
			}
		}

		return passwdDefMap;
	}

	/**
	 * �p�X���[�h���񃁃b�Z�[�W�̎擾
	 *
	 * @return msg
	 */
	public String getPasswordConstraintMessage() {

		String msg = "";

		MessageSource messageSource = MessageSourceUtil.getMessageSource();
		BindingAwareConcurrentModel errors = new BindingAwareConcurrentModel();
		HashMap<String, String> passwdDefMap = null;

		try {
			// properties�擾
			ProfileString prop = null;
			try {
				prop = new ProfileString(this, "application.properties");
			} catch (FileNotFoundException e) {
				MessageSourceUtil.addAttribute(errors, "message",
						messageSource.getMessage("search.failed.view.file.notound", new Object[] { e.getMessage() },
								null));
				category.error(errors.toString());
				throw new UserException(e);

			} catch (IOException e) {
				MessageSourceUtil.addAttribute(errors, "message",
						messageSource.getMessage("search.failed.view.file.IOExceltion", new Object[] { e.getMessage() },
								null));
				category.error(errors.toString());
				throw new UserException(e);
			}

			try {
				// �p�X���[�h��`�t�@�C������l�擾
				UserDef userdef = new UserDef();
				passwdDefMap = userdef.getPasswdDefinition(errors);

			} catch (FileNotFoundException e) {
				// for MUR
				category.error("�p�X���[�h��`�t�@�C���擾�Ɏ��s\n" + ErrorUtility.error2String(e));
				throw new UserException(e);

			} catch (IOException e) {
				// for MUR
				category.error("�p�X���[�h��`�t�@�C���擾�Ɏ��s\n" + ErrorUtility.error2String(e));
				throw new UserException(e);
			}

			// �p�X���[�h�g��������`�F�b�N
			String pwdValRole = passwdDefMap.get(UserDef.PWD_VAL_ROLE);
			category.debug("pwdValRole=" + pwdValRole);
			category.error("pwdValRole=" + pwdValRole);

			List<String> strList = new ArrayList<>();
			char[] roleChars = pwdValRole.toCharArray();
			for (int i = 0; i < roleChars.length; i++) {
				char c = roleChars[i];

				// A: �啶���p��
				if (c == '\u0041') {
					strList.add(prop.getValue("chgpasswd.constraints.Uppercase"));
				}
				// a: �������p��
				else if (c == '\u0061') {
					strList.add(prop.getValue("chgpasswd.constraints.Lowercase"));
				}
				// 1: ����
				else if (c == '\u0031') {
					strList.add(prop.getValue("chgpasswd.constraints.Number"));
				}
				// K: �L��
				else if (c == '\u004b') {
					String symbols = prop.getValue("chgpasswd.input.allowedSymbols");
					String cons = prop.getValue("chgpasswd.constraints.Symbol");
					strList.add(MessageFormat.format(cons, symbols));
				}
			}

			if (strList.size() != 0) {
				// �p�X���[�h�g�������񂪐ݒ肳��Ă���ꍇ�̂݃��b�Z�[�W��Ԃ�
				String msgText = prop.getValue("chgpasswd.failed.combination.passwd");
				msg = MessageFormat.format(msgText, strList.toString());
			}
		} catch (UserException e) {
			// �G���[���O�o�͍ς݂̂��߉������Ȃ�
		}

		return msg;
	}
}
