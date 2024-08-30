package tyk.drasap.root;

import java.sql.Connection;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import jp.co.toyodakouki.IDDE;
import tyk.drasap.common.AdminSettingDB;
import tyk.drasap.common.CookieManage;
import tyk.drasap.common.DrasapInfo;
import tyk.drasap.common.DrasapPropertiesFactory;
import tyk.drasap.common.ErrorUtility;
import tyk.drasap.common.USER_ID_CONVERSION_DB;
import tyk.drasap.common.User;
import tyk.drasap.common.UserDB;
import tyk.drasap.common.UserException;
import tyk.drasap.errlog.ErrorLoger;
import tyk.drasap.springfw.action.BaseAction;
import tyk.drasap.springfw.form.BaseForm;
import tyk.drasap.springfw.utils.MessageSourceUtil;

/**
 * <PRE>
 * ���̃V�X�e������A�}�ʃr���[�C���O�@�\�𒼐ڎg�p���邽�߂̃��O�C���B
 * ���O�C���ɐ�������ΐ}�ʃr���[�C���O�@�\�֑J�ڂ���B
 * �p�����[�^(en_string)���󂯎��A�Í��������c�[��(IDDE)���g�p����ID���擾����B
 * ���̑��̕K�{�p�����[�^�Ƃ��āAdrwg_no�Asys_id������B
 *
 * en_string�E�E�E�Í������ꂽ���O�C��ID
 * drwg_no�E�E�E�}�ԁBDRASAP�f�[�^�x�[�X�ɓo�^���ꂽ�`���ŁB���s�̓n�C�t�������̌`���B
 * sys_id�E�E�E�˗������V�X�e���̃V�X�e��ID�B���O�ɋL�^����܂��B
 * </PRE>
 * @author fumi
 * �쐬�� 2005/03/03
 * �ύX�� $Date: 2005/03/18 04:33:21 $ $Author: fumi $
 */
@Controller
public class DirectLoginForPreviewAction extends BaseAction {
	// --------------------------------------------------------- Instance Variables
	// --------------------------------------------------------- Methods
	/**
	 * Method execute
	 * @param form
	 * @param request
	 * @param response
	 * @param errors
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/directLoginForPreview")
	public String execute(
			BaseForm form,
			HttpServletRequest request,
			HttpServletResponse response,
			Model errors)
			throws Exception {
		category.debug("start");
		// �܂�User���쐬����
		User user = new User(request.getRemoteAddr());
		HttpSession session = request.getSession();
		session.removeAttribute("user");// ���ł�session����폜����

		//ActionMessages errors = new ActionMessages();
		DrasapInfo drasapInfo = null;

		String id = "";
		String drwgNo = "";
		String sys_id = "";
		String user_id_col = "";
		try {
			// session����p�����[�^���擾���A��������
			String enString = (String) session.getAttribute("en_string");
			session.removeAttribute("en_string");// session����remove����
			category.debug("en_string = " + enString);
			id = IDDE.decode(enString);// ��������������
			category.debug("id = " + id);
			// �p�����[�^�[ sys_id
			sys_id = (String) session.getAttribute("sys_id");
			session.removeAttribute("sys_id");// session����remove����
			category.debug("sys_id = " + sys_id);
			user.setSys_id(sys_id);
			// �p�����[�^�[ drwg_no
			drwgNo = (String) session.getAttribute("drwgNo");
			category.debug("drwg_no = " + drwgNo);
			if (drwgNo == null || drwgNo.length() == 0) {
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("root.failed.invalid.dragno." + user.getLanKey(), new Object[] { "DRWG_NO is null" }, null));
				throw new UserException("drwgNo is null");
			}
			// �p�����[�^�[ user_id_col
			user_id_col = (String) session.getAttribute("user_id_col");
			session.removeAttribute("user_id_col");// session����remove����
			category.debug("user_id_col = " + user_id_col);

			session.setAttribute("default_css", "jp".equals(user.getLanKey()) ? "default.css" : "defaultEN.css");

			if ("-1".equals(id)) {
				// ���O�C��ID�ُ�
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("root.failed.decode." + user.getLanKey(), new Object[] { id, "���O�C��ID�ُ�" }, null));
			} else if ("-2".equals(id)) {
				// SYSpass�ُ�
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("root.failed.decode." + user.getLanKey(), new Object[] { id, "SYSpass�ُ�" }, null));
			} else {
				// id�����Ƀ��[�U�[�����擾���Auser�I�u�W�F�N�g�ɕt������B
				addUserInfo(user, id, user_id_col, errors);
				if (Objects.isNull(errors.getAttribute("message"))) {
					// �p�X���[�h�L�������`�F�b�N
					if (!isPasswordExpired(user, errors)) {
						// �V�X�e�������Ǘ��Ґݒ�}�X�^�[����擾
						drasapInfo = getDrasapInfo(user, errors);
					}
				}
			}
		} catch (Exception e) {
			// for ���[�U�[
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("root.failed.login.othersys." + user.getLanKey(), new Object[] { e.getMessage() }, null));
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.unexpected"), user.getSys_id());
			// for MUR
			category.error("���̃V�X�e������̃��O�C���Ɏ��s\n" + ErrorUtility.error2String(e));
		}

		if (!Objects.isNull(errors.getAttribute("message"))) {
			//saveErrors(request, errors);
			request.setAttribute("errors", errors);// �G���[��o�^
			category.debug("--> failed");
			return "failed";
		}
		// �N�b�L�[���猾��ݒ���擾
		CookieManage langCookie = new CookieManage();
		String lanKey = langCookie.getCookie(request, user, "Language");
		if (lanKey == null || lanKey.length() == 0) {
			lanKey = "Japanese";
		}
		user.setLanguage(lanKey);

		// ���[�U�[��񂪎擾�ł����� session�Ɋi�[����
		session.setAttribute("user", user);

		session.setAttribute("drasapInfo", drasapInfo);

		// DirectPreview�ɕK�v�ȃp�����[�^��request��setAttribute����
		request.setAttribute("usr_id", id);// ���[�U�[ID
		request.setAttribute("drwgNo", drwgNo);// �}��

		category.debug("--> success");
		return "success";
	}

	/**
	 * id�A�p�X���[�h�����Ƀ��[�U�[�����擾���Auser�I�u�W�F�N�g�ɕt������B
	 * �G���[�������errors�ɃG���[�o�^����B
	 * @param user
	 * @param id
	 * @param passwd
	 * @param errors
	 */
	private void addUserInfo(User user, String id, String user_id_col, Model errors) {
		Connection conn = null;
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(true);// ��g�����U�N�V����

			String drasapUserId = id;
			//�@���[�UID�ϊ��e�[�u���̃J���������w�肳��Ă�����DRASAP���[�U�h�c�ɕϊ�
			if (user_id_col != null && user_id_col.length() > 0) {
				drasapUserId = USER_ID_CONVERSION_DB.getDrasapUserId(user_id_col, id, conn);
			}

			if (UserDB.addUserInfo(user, drasapUserId.trim(), conn)) {
				// id����v
			} else {
				// ��v���Ȃ�
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("directlogin.undefined.drasapuserid." + user.getLanKey(), new Object[] { "Id" }, null));
				// for �V�X�e���Ǘ���
				ErrorLoger.error(user, this,
						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"), user.getSys_id());
				// for MUR
				category.error("���[�UID�ϊ��e�[�u����DRASAP���[�UID���o�^����Ă��܂���Buser_id=" + drasapUserId);
			}

		} catch (UserException e) {
			// for ���[�U�[
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("directlogin.undefined.extuserid." + user.getLanKey(), new Object[] { e.getMessage() }, null));
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"), user.getSys_id());
			// for MUR
			category.error("���[�UID�ϊ��e�[�u���ɊO���V�X�e���̃��[�UID���o�^����Ă��܂���\n" + ErrorUtility.error2String(e));
		} catch (Exception e) {
			// for ���[�U�[
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("root.failed.get.userinfo." + user.getLanKey(), new Object[] { e.getMessage() }, null));
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"), user.getSys_id());
			// for MUR
			category.error("���[�U�[���̎擾�Ɏ��s\n" + ErrorUtility.error2String(e));
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * �V�X�e�������擾����B
	 * �G���[�������errors�ɃG���[�o�^����B
	 * @param user
	 * @param errors
	 */
	private DrasapInfo getDrasapInfo(User user, Model errors) {
		Connection conn = null;
		DrasapInfo drasapInfo = null;
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(true);// ��g�����U�N�V����

			drasapInfo = AdminSettingDB.getDrasapInfo(conn);

		} catch (Exception e) {
			// for ���[�U�[
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("root.failed.get.drasapinfo." + user.getLanKey(), new Object[] { e.getMessage() }, null));
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"), user.getSys_id());
			// for MUR
			category.error("�V�X�e�����̎擾�Ɏ��s\n" + ErrorUtility.error2String(e));
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
			}
		}
		return drasapInfo;
	}

	/**
	 * �p�X���[�h�L�������`�F�b�N
	 * @param user
	 * @param errors
	 * @return true:�߂��� false:�߂��Ă��Ȃ�
	 */
	private boolean isPasswordExpired(User user, Model errors) {
		boolean result = false;
		Connection conn = null;
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(true);// ��g�����U�N�V����

			int ret = UserDB.checkPasswordExpiry(user, errors, conn);
			if (ret != 0) {
				String errMsg = ret == 1 ? "root.failed.password.notset." : "root.failed.password.expired.";
				// for ���[�U�[
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage(errMsg + user.getLanKey(), null, null));
				// for �V�X�e���Ǘ���
				if (ret == 1) {
					ErrorLoger.error(user, this,
							DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.password.notset"), user.getSys_id());
				} else {
					ErrorLoger.error(user, this,
							DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.password.expired"), user.getSys_id());
				}
				// for MUR
				category.error("�p�X���[�h�L���������߂����B�߂�l=[" + ret + "]\n");
				result = true;
			}
		} catch (Exception e) {
			// for ���[�U�[
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("root.failed.check.password.expiry." + user.getLanKey(), new Object[] { e.getMessage() }, null));
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"), user.getSys_id());
			// for MUR
			category.error("�p�X���[�h�L�������`�F�b�N�Ɏ��s\n" + ErrorUtility.error2String(e));
			result = true;
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
			}
		}
		return result;
	}
}
