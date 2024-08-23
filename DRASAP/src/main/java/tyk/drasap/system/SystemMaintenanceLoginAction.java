package tyk.drasap.system;

import static tyk.drasap.common.DrasapPropertiesFactory.BEA_HOME;
import static tyk.drasap.common.DrasapPropertiesFactory.CATALINA_HOME;
import static tyk.drasap.common.DrasapPropertiesFactory.OCE_AP_SERVER_BASE;
import static tyk.drasap.common.DrasapPropertiesFactory.OCE_AP_SERVER_HOME;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import tyk.drasap.common.AdminSettingDB;
import tyk.drasap.common.CsvItemStrList;
import tyk.drasap.common.DrasapInfo;
import tyk.drasap.common.DrasapPropertiesFactory;
import tyk.drasap.common.User;
import tyk.drasap.common.UserDB;
import tyk.drasap.errlog.ErrorLoger;
import tyk.drasap.springfw.action.BaseAction;
import tyk.drasap.springfw.utils.MessageSourceUtil;

/**
 * ���O�C����Action�B
 *
 * @version 2013/06/14 yamagishi
 */
@Controller
public class SystemMaintenanceLoginAction extends BaseAction {
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
	@PostMapping("/systemMaintenanceLogin")
	public String execute(
			SystemMaintenanceLoginForm form,
			HttpServletRequest request,
			HttpServletResponse response,
			Model errors)
			throws Exception {
		//category.debug("start");
		SystemMaintenanceLoginForm loginForm = form;
		User user = new User(request.getRemoteAddr());
		HttpSession session = request.getSession();
		session.removeAttribute("user");// ���ł�session����폜����
		//ActionMessages errors = new ActionMessages();

		// id�A�p�X���[�h�`�F�b�N
		chkPasswd(loginForm.getId(), loginForm.getPasswd(), errors);
		// id�A�p�X���[�h�����Ƀ��[�U�[�����擾���Auser�I�u�W�F�N�g�ɕt������B
		addUserInfo(user, loginForm.getId(), loginForm.getPasswd(), errors);
		// �V�X�e�������Ǘ��Ґݒ�}�X�^�[����擾
		DrasapInfo drasapInfo = getDrasapInfo(user, errors);

		if (Objects.isNull(errors.getAttribute("message"))) {
			// ���[�U�[��񂪎擾�ł����� session�Ɋi�[����
			session.setAttribute("user", user);
			session.setAttribute("drasapInfo", drasapInfo);
			//category.debug("--> success");
			return "success";
		}

		//saveErrors(request, errors);
		request.setAttribute("errors", errors);// �G���[��o�^
		//category.debug("--> failed");
		return "failed";
	}

	/**
	 * id�A�p�X���[�h�����Ƀ��[�U�[�����擾���Auser�I�u�W�F�N�g�ɕt������B
	 * �G���[�������errors�ɃG���[�o�^����B
	 * @param user
	 * @param id
	 * @param passwd
	 * @param errors
	 */
	private void chkPasswd(String id, String passwd, Model errors) {
		CsvItemStrList delDwgPs;
		// 2013.06.14 yamagishi modified. start
		//		String beaHome = System.getenv("BEA_HOME");
		//		if (beaHome == null) beaHome = System.getenv("OCE_BEA_HOME");
		//		if (beaHome == null) beaHome = DrasapPropertiesFactory.getDrasapProperties(this).getProperty("oce.BEA_BASE");
		//		String passwdFile = beaHome + DrasapPropertiesFactory.getDrasapProperties(this).getProperty("tyk.csvdef.delDwgPs.path");
		String apServerHome = System.getenv(BEA_HOME);
		if (apServerHome == null) {
			apServerHome = System.getenv(CATALINA_HOME);
		}
		if (apServerHome == null) {
			apServerHome = System.getenv(OCE_AP_SERVER_HOME);
		}
		if (apServerHome == null) {
			apServerHome = DrasapPropertiesFactory.getDrasapProperties(this).getProperty(OCE_AP_SERVER_BASE);
		}
		String passwdFile = apServerHome + DrasapPropertiesFactory.getDrasapProperties(this).getProperty("tyk.csvdef.delDwgPs.path");
		// 2013.06.14 yamagishi modified. end
		try {
			delDwgPs = new CsvItemStrList(passwdFile);
			if (delDwgPs.searchLineData(id) != null) {
				if (!delDwgPs.searchLineData(id).get(1).equals(passwd)) {
					MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("system.maintenanceLogin.passMissmatch", new Object[] { "Password" }, null));
				}
			} else {
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("system.maintenanceLogin.userNotFound", new Object[] { id }, null));

			}

		} catch (FileNotFoundException e) {
			// for ���[�U�[
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("system.maintenanceLogin.FileNotFound", new Object[] { passwdFile }, null));
			// for MUR
			//			category.error("���[�U�[���̎擾�Ɏ��s\n" + ErrorUtility.error2String(e));
			return;
		} catch (IOException e) {
			// for ���[�U�[
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("root.failed.get.userinfo.jp", new Object[] { e.getMessage() }, null));
			// for MUR
			//			category.error("���[�U�[���̎擾�Ɏ��s\n" + ErrorUtility.error2String(e));
			return;
		}
	}

	/**
	 * id�A�p�X���[�h�����Ƀ��[�U�[�����擾���Auser�I�u�W�F�N�g�ɕt������B
	 * �G���[�������errors�ɃG���[�o�^����B
	 * @param user
	 * @param id
	 * @param passwd
	 * @param errors
	 */
	private void addUserInfo(User user, String id, String passwd, Model errors) {
		Connection conn = null;
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(true);// ��g�����U�N�V����
			if (UserDB.addUserInfo(user, id, conn)) {
				// id,�p�X���[�h����v
			} else {
				// ��v���Ȃ�
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("root.missmatch." + user.getLanKey(), new Object[] { "Id�܂���Password" }, null));
			}

		} catch (Exception e) {
			// for ���[�U�[
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("root.failed.get.userinfo." + user.getLanKey(), new Object[] { e.getMessage() }, null));
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			// for MUR
			//			category.error("���[�U�[���̎擾�Ɏ��s\n" + ErrorUtility.error2String(e));
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
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			// for MUR
			//			category.error("�V�X�e�����̎擾�Ɏ��s\n" + ErrorUtility.error2String(e));
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
			}
		}
		return drasapInfo;
	}
}
