/**
 *
 */
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

import org.apache.log4j.Category;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import tyk.drasap.common.DataSourceFactory;
import tyk.drasap.common.DrasapPropertiesFactory;
import tyk.drasap.common.ErrorUtility;
import tyk.drasap.common.ProfileString;
import tyk.drasap.common.User;
import tyk.drasap.common.UserDB;
import tyk.drasap.common.UserDef;
import tyk.drasap.common.UserException;
import tyk.drasap.errlog.ErrorLoger;

/**
 * @author tetsuya_yamamoto
 *
 */
public class ChangePasswdForm extends ActionForm {

	/**
	 *
	 */
	private static Category category = Category.getInstance(ChangePasswdAction.class.getName());
	private static DataSource ds;
	static{
		try{
			ds = DataSourceFactory.getOracleDataSource();
		} catch(Exception e){
			category.error("DataSource�̎擾�Ɏ��s\n" + ErrorUtility.error2String(e));
		}
	}

	private static final long serialVersionUID = 1L;

	private String oldpass;
	private String newpass;
	private String newPassConfirm;

	/**
	 *
	 */
	public ChangePasswdForm() {
		// TODO �����������ꂽ�R���X�g���N�^�[�E�X�^�u
	}

	/**
	 * Method validate
	 * @param ActionMapping mapping
	 * @param HttpServletRequest request
	 * @return ActionErrors
	 */
	public ActionErrors validate(ActionMapping mapping,HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();

		// �Z�b�V��������user���擾
		User user = (User)request.getSession().getAttribute("user");

		// session�^�C���A�E�g�̊m�F
		if(user == null){
			// �G���[���b�Z�[�W�͋�ŕԂ��B
			// JSP�̕��ŃG���[���b�Z�[�W�\���O��
			// �Z�b�V�����^�C���A�E�g�y�[�W�ɓ]�����邽�߁B
			errors.add(new ActionMessages());
			return errors;
		}

		try {
			// properties�擾
			ProfileString prop = null;
			try {
				prop = new ProfileString(this, "resources/application.properties");
			} catch (FileNotFoundException e) {
				errors.add(ActionMessages.GLOBAL_MESSAGE,
						new ActionMessage("search.failed.view.file.notound", e.getMessage()));
				category.error(errors.toString());
				throw new UserException();

			} catch (IOException e) {
				errors.add(ActionMessages.GLOBAL_MESSAGE,
						new ActionMessage("search.failed.view.file.IOExceltion", e.getMessage()));
				category.error(errors.toString());
				throw new UserException();
			}

			// �t�H�[���͂��ׂē��͍ς݂��H
			if(isInputPassword(errors, prop) == false) {
				// �����͍��ڂ�����ꍇ�̓`�F�b�N�I��
				throw new UserException();
			}

			// 2�o�C�g�����܂��͓��͕s�̋L���̓G���[
			char[] passChars = newpass.toCharArray();
			char[] symbols = prop.getValue("chgpasswd.input.allowedSymbols").toCharArray();
			for(char pass : passChars) {
				if (('\u0041' <= pass && pass <= '\u005a') // �啶�� A - Z
						|| ('\u0061' <= pass && pass <= '\u007a') // ������ a - z
						|| ('\u0030' <= pass && pass <= '\u0039')) { // ���� 0 - 9
					// OK ���̕�����
					continue;
				}
				// �L���`�F�b�N
				boolean chkFlg = false;
				for(char s : symbols) {
					if (s == pass) {
						// ���͉\�ȋL��
						chkFlg = true;
					}
				}
				if(!chkFlg) {
					// ���͕s�̋L��
					errors.add("illegalString",
							new ActionMessage("chgpasswd.failed.Illegal.passwd"));
					break;
				}
			}

			// ���݂̃p�X���[�h�`�F�b�N
			checkCurrentPassword(user, prop, errors);

			// �V�����p�X���[�h�͌��݂̃p�X���[�h�Ƃ͈قȂ邩�H
			if (oldpass.equals(newpass)) {
				// ��v
				errors.add("matcholdpassword",
						new ActionMessage("chgpasswd.failed.match.oldpassword"));
			}

			// �V�����p�X���[�h�Ɗm�F�p�X���[�h�͓������H
			if (!(newpass.equals(newPassConfirm))) {
				// �s��v
				errors.add("disagreement",
						new ActionMessage("chgpasswd.failed.missmatch.newpasswd"));
			}

			// ID��Password�������ꍇ�̓G���[
			if(newpass.equals(user.getId())) {
				// ID�ƈ�v
				errors.add("matchUserID", new ActionMessage("chgpasswd.failed.match.userId"));
			}

			// �p�X���[�h�������`�F�b�N
			checkPasswordIntegrity(errors, prop);

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
	private boolean isInputPassword (ActionErrors errors, ProfileString prop) {

		boolean inputFlag = true;

		// ���̓`�F�b�N oldpass
		if (oldpass == null || oldpass.length() == 0) {
			// ������
			errors.add("passwd", new ActionMessage("chgpasswd.required",
					"" + prop.getValue("chgpasswd.name.oldpasswd.jp"),
					"" + prop.getValue("chgpasswd.name.oldpasswd.en")));
			inputFlag = false;
		}
		// ���̓`�F�b�N newpass
		if (newpass == null || newpass.length() == 0) {
			// ������
			errors.add("newPass", new ActionMessage("chgpasswd.required",
					"" + prop.getValue("chgpasswd.name.newpasswd.jp"),
					"" + prop.getValue("chgpasswd.name.newpasswd.en")));
			inputFlag = false;

		}
		// ���̓`�F�b�N�E�E�ERe-enter
		if (newPassConfirm == null || newPassConfirm.length() == 0) {
			// ������
			errors.add("Re-enter", new ActionMessage("chgpasswd.required.re-enter"));
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
	private void checkCurrentPassword(User user, ProfileString prop, ActionErrors errors) throws UserException {
		Connection conn = null;
		String currentPassDB = null;
		try{
			conn = ds.getConnection();
			conn.setAutoCommit(true);// ��g�����U�N�V����

			currentPassDB = UserDB.getPassword(user.getId(), conn);

		} catch(Exception e){
			// for ���[�U�[
			errors.add(ActionMessages.GLOBAL_MESSAGE,
					new ActionMessage("root.failed.get.userinfo", e.getMessage()));
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this,
						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			// for MUR
			category.error("���[�U�[���̎擾�Ɏ��s\n" + ErrorUtility.error2String(e));
			throw new UserException(e);

		} finally {
			try{ conn.close(); } catch(Exception e) {}
		}

	    if(!oldpass.equals(currentPassDB)) {
	    	// ���݂̃p�X���[�h�s��v
			errors.add("currentPasswordMissmatch",
					new ActionMessage("chgpasswd.missmatch",
							"" + prop.getValue("chgpasswd.name.oldpasswd.jp"),
							"" + prop.getValue("chgpasswd.name.oldpasswd.en")));
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
	private void checkPasswordIntegrity(ActionErrors errors, ProfileString prop) throws UserException {

		HashMap<String, String> passwdDefMap = null;

		try {
			// �p�X���[�h��`�t�@�C������l�擾
			UserDef userdef = new UserDef();
			passwdDefMap = userdef.getPasswdDefinition(errors);

		} catch (FileNotFoundException e) {
			// for ���[�U�[
			errors.add(ActionMessages.GLOBAL_MESSAGE,
					new ActionMessage("root.failed.get.userinfo", e.getMessage()));
			// for MUR
			category.error("�p�X���[�h��`�t�@�C���擾�Ɏ��s\n" + ErrorUtility.error2String(e));
			throw new UserException(e);

		} catch (IOException e) {
			// for ���[�U�[
			errors.add(ActionMessages.GLOBAL_MESSAGE,
					new ActionMessage("root.failed.get.userinfo", e.getMessage()));
			// for MUR
			category.error("�p�X���[�h��`�t�@�C���擾�Ɏ��s\n" + ErrorUtility.error2String(e));
			throw new UserException(e);
		}

		// �p�X���[�h�ŏ������`�F�b�N
		int pwdMinLen = Integer.parseInt(passwdDefMap.get(UserDef.PWD_MIN_LEN));
		category.debug("pwdMinLen=" + pwdMinLen);

		if(newpass.length() < pwdMinLen) {
			// �p�X���[�h�ŏ������s��
			errors.add("MissingDigits",
					new ActionMessage("chgpasswd.failed.missingdigits.passwd", "" + pwdMinLen));
		}

		List<String> strList = new ArrayList();

		// �p�X���[�h�g��������`�F�b�N
		String pwdValRole = passwdDefMap.get(UserDef.PWD_VAL_ROLE);
		category.debug("pwdValRole=" + pwdValRole);

		boolean isPasswdComb = true;

		char[] roleChars = pwdValRole.toCharArray();
		for (int i = 0; i < roleChars.length; i++) {
			char c = roleChars[i];

			// A: �啶���p��
			if(c == '\u0041') {
				strList.add(prop.getValue("chgpasswd.constraints.Uppercase"));

				char[] newPassChars = newpass.toCharArray();
				int j = 0;
				for (j = 0; j < newPassChars.length; j++) {
					char pass = newPassChars[j];
					if (('\u0041' <= pass && pass <= '\u005a') ) { // �啶�� A - Z
						// �`�F�b�NOK
						break;
					}
				}

				if(j >= newPassChars.length) {
					// �`�F�b�NNG
					isPasswdComb = false;
				}
			}
			// a: �������p��
			else if(c == '\u0061') {
				strList.add(prop.getValue("chgpasswd.constraints.Lowercase"));

				char[] newPassChars = newpass.toCharArray();
				int j = 0;
				for (j = 0; j < newPassChars.length; j++) {
					char pass = newPassChars[j];
					if (('\u0061' <= pass && pass <= '\u007a') ) { // ������ a - z
						// �`�F�b�NOK
						break;
					}
				}

				if(j >= newPassChars.length) {
					// �`�F�b�NNG
					isPasswdComb = false;
				}
			}
			// 1: ����
			else if(c == '\u0031') {
				strList.add(prop.getValue("chgpasswd.constraints.Number"));

				char[] newPassChars = newpass.toCharArray();
				int j = 0;
				for (j = 0; j < newPassChars.length; j++) {
					char pass = newPassChars[j];
					if (('\u0030' <= pass && pass <= '\u0039') ) { // ���� 0 - 9
						// �`�F�b�NOK
						break;
					}
				}

				if(j >= newPassChars.length) {
					// �`�F�b�NNG
					isPasswdComb = false;
				}
			}
			// K: �L��
			else if(c == '\u004b') {
				String symbols = prop.getValue("chgpasswd.input.allowedSymbols");
				String cons = prop.getValue("chgpasswd.constraints.Symbol");
				strList.add(MessageFormat.format(cons, symbols));

				boolean chkFlg = false;
				// 2�o�C�g�����܂��͓��͉\�ȋL���ȊO�͓��͋֎~
				char[] passChars = newpass.toCharArray();
				char[] symbolChars = symbols.toCharArray();

				for(char pass : passChars) {
					if (('\u0041' <= pass && pass <= '\u005a') // �啶�� A - Z
							|| ('\u0061' <= pass && pass <= '\u007a') // ������ a - z
							|| ('\u0030' <= pass && pass <= '\u0039')) { // ���� 0 - 9
						// ���̕�����
						continue;
					}
					// �L���`�F�b�N
					for(char s : symbolChars) {
						if (s == pass) {
							// ���͉\�ȋL��
							chkFlg = true;
						}
					}
				}

				if(!chkFlg) {
					// �L������
					// �`�F�b�NNG
					isPasswdComb = false;
				}
			}
		}

		if(isPasswdComb == false) {
			// �p�X���[�h�g��������ᔽ
			errors.add("FailedPasswordIntegrity",
					new ActionMessage("chgpasswd.failed.combination.passwd", strList.toString()));
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
		this.oldpass = oldpasswd;
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
