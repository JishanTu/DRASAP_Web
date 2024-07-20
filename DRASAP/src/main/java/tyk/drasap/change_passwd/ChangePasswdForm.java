package tyk.drasap.change_passwd;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import tyk.drasap.common.DataSourceFactory;
import tyk.drasap.common.DrasapPropertiesFactory;
import tyk.drasap.common.ErrorUtility;
import tyk.drasap.common.ProfileString;
import tyk.drasap.common.User;
import tyk.drasap.common.UserDB;
import tyk.drasap.common.UserDef;
import tyk.drasap.common.UserException;
import tyk.drasap.errlog.ErrorLoger;
import tyk.drasap.springfw.form.BaseForm;
import tyk.drasap.springfw.utils.MessageSourceUtil;

/**
 * @author tetsuya_yamamoto
 *
 */
@Component
public class ChangePasswdForm extends BaseForm {
	/**
	 *
	 */
	private static Logger category = Logger.getLogger(ChangePasswdAction.class.getName());
	private static DataSource ds;
	static {
		try {
			ds = DataSourceFactory.getOracleDataSource();
		} catch (Exception e) {
			category.error("DataSource�̎擾�Ɏ��s\n" + ErrorUtility.error2String(e));
		}
	}

	private String oldpass;
	private String newpass;
	private String newPassConfirm;

	/**
	 *
	 */
	public ChangePasswdForm() {
	}

	/**
	 * Method validate
	 * @param ActionMapping mapping
	 * @param HttpServletRequest request
	 * @return ActionErrors
	 */
	@Override
	public Model validate(HttpServletRequest request, Model errors, MessageSource messageSource) {
		//ActionErrors errors = new ActionErrors();

		// �Z�b�V��������user���擾
		User user = (User) request.getSession().getAttribute("user");

		// session�^�C���A�E�g�̊m�F
		if (user == null) {
			// �G���[���b�Z�[�W�͋�ŕԂ��B
			// JSP�̕��ŃG���[���b�Z�[�W�\���O��
			// �Z�b�V�����^�C���A�E�g�y�[�W�ɓ]�����邽�߁B
			MessageSourceUtil.addAttribute(errors, "message", "");
			return errors;
		}

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
				throw new UserException();

			} catch (IOException e) {
				MessageSourceUtil.addAttribute(errors, "message",
						messageSource.getMessage("search.failed.view.file.IOExceltion", new Object[] { e.getMessage() },
								null));
				category.error(errors.toString());
				throw new UserException();
			}

			// �t�H�[���͂��ׂē��͍ς݂��H
			if (isInputPassword(errors, prop, messageSource) == false) {
				// �����͍��ڂ�����ꍇ�̓`�F�b�N�I��
				throw new UserException();
			}

			// 2�o�C�g�����܂��͓��͕s�̋L���̓G���[
			char[] passChars = newpass.toCharArray();
			char[] symbols = prop.getValue("chgpasswd.input.allowedSymbols").toCharArray();
			for (char pass : passChars) {
				if ('\u0041' <= pass && pass <= '\u005a' // �啶�� A - Z
						|| '\u0061' <= pass && pass <= '\u007a' // ������ a - z
						|| '\u0030' <= pass && pass <= '\u0039') { // ���� 0 - 9
					// OK ���̕�����
					continue;
				}
				// �L���`�F�b�N
				boolean chkFlg = false;
				for (char s : symbols) {
					if (s == pass) {
						// ���͉\�ȋL��
						chkFlg = true;
						break;
					}
				}
				if (!chkFlg) {
					// ���͕s�̋L��
					MessageSourceUtil.addAttribute(errors, "illegalString",
							messageSource.getMessage("chgpasswd.failed.Illegal.passwd", null, null));
					break;
				}
			}

			// ���݂̃p�X���[�h�`�F�b�N
			checkCurrentPassword(user, prop, errors, messageSource);

			// �V�����p�X���[�h�͌��݂̃p�X���[�h�Ƃ͈قȂ邩�H
			if (oldpass.equals(newpass)) {
				// ��v
				MessageSourceUtil.addAttribute(errors, "matcholdpassword",
						messageSource.getMessage("chgpasswd.failed.match.oldpassword", null, null));
			}

			// �V�����p�X���[�h�Ɗm�F�p�X���[�h�͓������H
			if (!newpass.equals(newPassConfirm)) {
				// �s��v
				MessageSourceUtil.addAttribute(errors, "disagreement",
						messageSource.getMessage("chgpasswd.failed.missmatch.newpasswd", null, null));
			}

			// ID��Password�������ꍇ�̓G���[
			if (newpass.equals(user.getId())) {
				// ID�ƈ�v
				MessageSourceUtil.addAttribute(errors, "matchUserID",
						messageSource.getMessage("chgpasswd.failed.match.userId", null, null));
			}

			// �p�X���[�h�������`�F�b�N
			checkPasswordIntegrity(errors, prop, messageSource);

		} catch (UserException e) {
			category.error(errors.toString());
		}

		return errors;
	}

	/**
	 * �t�H�[���͂��ׂē��͍ς݂��H
	 *
	 * @param errors
	 * @param prop
	 * @return
	 */
	private boolean isInputPassword(Model errors, ProfileString prop, MessageSource messageSource) {

		boolean inputFlag = true;

		// ���̓`�F�b�N oldpass
		if (oldpass == null || oldpass.length() == 0) {
			// ������
			MessageSourceUtil.addAttribute(errors, "passwd", messageSource.getMessage("chgpasswd.required",
					new Object[] { "" + prop.getValue("chgpasswd.name.oldpasswd.jp"),
							"" + prop.getValue("chgpasswd.name.oldpasswd.en") },
					null));
			inputFlag = false;
		}
		// ���̓`�F�b�N newpass
		if (newpass == null || newpass.length() == 0) {
			// ������
			MessageSourceUtil.addAttribute(errors, "newPass", messageSource.getMessage("chgpasswd.required",
					new Object[] { "" + prop.getValue("chgpasswd.name.newpasswd.jp"),
							"" + prop.getValue("chgpasswd.name.newpasswd.en") },
					null));
			inputFlag = false;

		}
		// ���̓`�F�b�N�E�E�ERe-enter
		if (newPassConfirm == null || newPassConfirm.length() == 0) {
			// ������
			MessageSourceUtil.addAttribute(errors, "Re-enter",
					messageSource.getMessage("chgpasswd.required.re-enter", null, null));
			inputFlag = false;
		}

		return inputFlag;
	}

	/**
	 * ���݂̃p�X���[�h�`�F�b�N
	 *
	 * @param user
	 * @param prop
	 * @param errors
	 * @throws UserException
	 */
	private void checkCurrentPassword(User user, ProfileString prop, Model errors, MessageSource messageSource) throws UserException {
		Connection conn = null;
		String currentPassDB = null;
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(true);// ��g�����U�N�V����

			currentPassDB = UserDB.getPassword(user.getId(), conn);

		} catch (Exception e) {
			// for ���[�U�[
			MessageSourceUtil.addAttribute(errors, "message",
					messageSource.getMessage("root.failed.get.userinfo", new Object[] { e.getMessage() }, null));
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			// for MUR
			category.error("���[�U�[���̎擾�Ɏ��s\n" + ErrorUtility.error2String(e));
			throw new UserException(e);

		} finally {
			try {
				conn.close();
			} catch (Exception e) {
			}
		}

		if (!oldpass.equals(currentPassDB)) {
			// ���݂̃p�X���[�h�s��v
			MessageSourceUtil.addAttribute(errors, "currentPasswordMissmatch",
					messageSource.getMessage("chgpasswd.missmatch",
							new Object[] { "" + prop.getValue("chgpasswd.name.oldpasswd.jp"),
									"" + prop.getValue("chgpasswd.name.oldpasswd.en") },
							null));
			throw new UserException(errors.toString());
		}
	}

	/**
	 * �p�X���[�h�������`�F�b�N
	 *
	 * @param errors
	 * @param prop
	 * @throws UserException
	 */
	private void checkPasswordIntegrity(Model errors, ProfileString prop, MessageSource messageSource) throws UserException {

		HashMap<String, String> passwdDefMap = null;

		try {
			// �p�X���[�h��`�t�@�C������l�擾
			UserDef userdef = new UserDef();
			passwdDefMap = userdef.getPasswdDefinition(errors);

		} catch (FileNotFoundException e) {
			// for ���[�U�[
			MessageSourceUtil.addAttribute(errors, "message",
					messageSource.getMessage("root.failed.get.userinfo", new Object[] { e.getMessage() }, null));
			// for MUR
			category.error("�p�X���[�h��`�t�@�C���擾�Ɏ��s\n" + ErrorUtility.error2String(e));
			throw new UserException(e);

		} catch (IOException e) {
			// for ���[�U�[
			MessageSourceUtil.addAttribute(errors, "message",
					messageSource.getMessage("root.failed.get.userinfo", new Object[] { e.getMessage() }, null));
			// for MUR
			category.error("�p�X���[�h��`�t�@�C���擾�Ɏ��s\n" + ErrorUtility.error2String(e));
			throw new UserException(e);
		}

		// �p�X���[�h�ŏ������`�F�b�N
		int pwdMinLen = Integer.parseInt(passwdDefMap.get(UserDef.PWD_MIN_LEN));
		category.debug("pwdMinLen=" + pwdMinLen);

		if (newpass.length() < pwdMinLen) {
			// �p�X���[�h�ŏ������s��
			MessageSourceUtil.addAttribute(errors, "MissingDigits",
					messageSource.getMessage("chgpasswd.failed.missingdigits.passwd", new Object[] { "" + pwdMinLen },
							null));
		}

		List<String> strList = new ArrayList<>();

		// �p�X���[�h�g��������`�F�b�N
		String pwdValRole = passwdDefMap.get(UserDef.PWD_VAL_ROLE);
		category.debug("pwdValRole=" + pwdValRole);

		boolean isPasswdComb = true;

		char[] roleChars = pwdValRole.toCharArray();
		for (int i = 0; i < roleChars.length; i++) {
			char c = roleChars[i];

			// A: �啶���p��
			if (c == '\u0041') {
				strList.add(prop.getValue("chgpasswd.constraints.Uppercase"));

				char[] newPassChars = newpass.toCharArray();
				int j = 0;
				for (j = 0; j < newPassChars.length; j++) {
					char pass = newPassChars[j];
					if ('\u0041' <= pass && pass <= '\u005a') { // �啶�� A - Z
						// �`�F�b�NOK
						break;
					}
				}

				if (j >= newPassChars.length) {
					// �`�F�b�NNG
					isPasswdComb = false;
				}
			}
			// a: �������p��
			else if (c == '\u0061') {
				strList.add(prop.getValue("chgpasswd.constraints.Lowercase"));

				char[] newPassChars = newpass.toCharArray();
				int j = 0;
				for (j = 0; j < newPassChars.length; j++) {
					char pass = newPassChars[j];
					if ('\u0061' <= pass && pass <= '\u007a') { // ������ a - z
						// �`�F�b�NOK
						break;
					}
				}

				if (j >= newPassChars.length) {
					// �`�F�b�NNG
					isPasswdComb = false;
				}
			}
			// 1: ����
			else if (c == '\u0031') {
				strList.add(prop.getValue("chgpasswd.constraints.Number"));

				char[] newPassChars = newpass.toCharArray();
				int j = 0;
				for (j = 0; j < newPassChars.length; j++) {
					char pass = newPassChars[j];
					if ('\u0030' <= pass && pass <= '\u0039') { // ���� 0 - 9
						// �`�F�b�NOK
						break;
					}
				}

				if (j >= newPassChars.length) {
					// �`�F�b�NNG
					isPasswdComb = false;
				}
			}
			// K: �L��
			else if (c == '\u004b') {
				String symbols = prop.getValue("chgpasswd.input.allowedSymbols");
				String cons = prop.getValue("chgpasswd.constraints.Symbol");
				strList.add(MessageFormat.format(cons, symbols));

				boolean chkFlg = false;
				// 2�o�C�g�����܂��͓��͉\�ȋL���ȊO�͓��͋֎~
				char[] passChars = newpass.toCharArray();
				char[] symbolChars = symbols.toCharArray();

				for (char pass : passChars) {
					if ('\u0041' <= pass && pass <= '\u005a' // �啶�� A - Z
							|| '\u0061' <= pass && pass <= '\u007a' // ������ a - z
							|| '\u0030' <= pass && pass <= '\u0039') { // ���� 0 - 9
						// ���̕�����
						continue;
					}
					// �L���`�F�b�N
					for (char s : symbolChars) {
						if (s == pass) {
							// ���͉\�ȋL��
							chkFlg = true;
							break;
						}
					}
				}

				if (!chkFlg) {
					// �L������
					// �`�F�b�NNG
					isPasswdComb = false;
				}
			}
		}

		if (isPasswdComb == false) {
			// �p�X���[�h�g��������ᔽ
			MessageSourceUtil.addAttribute(errors, "FailedPasswordIntegrity",
					messageSource.getMessage("chgpasswd.failed.combination.passwd", new Object[] { strList.toString() },
							null));
			throw new UserException(strList.toString());
		}
	}

	// --------------------------------------------------------- getter,setter
	/**
	 * Returns the oldpasswd.
	 * @return String
	 */
	public String getOldpass() {
		return oldpass;
	}

	/**
	 * Set the oldpasswd.
	 * @param oldpasswd The passwd to set
	 */
	public void setOldpass(String oldpasswd) {
		oldpass = oldpasswd;
	}

	/**
	 * Returns the newpass.
	 * @return String
	 */
	public String getNewpass() {
		return newpass;
	}

	/**
	 * Set the newpass.
	 * @param newpass The passwd to set
	 */
	public void setNewpass(String newpass) {
		this.newpass = newpass;
	}

	/**
	 * Returns the newpass.
	 * @return String
	 */
	public String getNewPassConfirm() {
		return newPassConfirm;
	}

	/**
	 * Set the newpass.
	 * @param newpass The passwd to set
	 */
	public void setNewPassConfirm(String newPassConfirm) {
		this.newPassConfirm = newPassConfirm;
	}
}
