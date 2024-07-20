package tyk.drasap.root;

import java.sql.Connection;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import tyk.drasap.common.AdminSettingDB;
import tyk.drasap.common.CookieManage;
import tyk.drasap.common.DrasapInfo;
import tyk.drasap.common.DrasapPropertiesFactory;
import tyk.drasap.common.ErrorUtility;
import tyk.drasap.common.User;
import tyk.drasap.common.UserDB;
import tyk.drasap.errlog.ErrorLoger;
import tyk.drasap.springfw.action.BaseAction;
import tyk.drasap.springfw.utils.MessageSourceUtil;

/**
 * ���O�C����Action�B
 */
@Controller
public class LoginAction extends BaseAction {
	// --------------------------------------------------------- Instance Variables
	// --------------------------------------------------------- Methods

	public LoginAction() {
		category.debug("start");
		category.debug("end");
	}

	/**
	 * Method execute
	 * @param form
	 * @param request
	 * @param response
	 * @param errors
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/login")
	public String execute(
			LoginForm form,
			HttpServletRequest request,
			HttpServletResponse response,
			Model errors)
			throws Exception {
		category.debug("start");
		LoginForm loginForm = form;
		// �܂�User���쐬����
		User user = new User(request.getRemoteAddr());

		HttpSession session = request.getSession();
		session.removeAttribute("user");// ���ł�session����폜����

		// �p�X���[�h�ύX�����Ńp�X���[�h�ύX��ʂ�����ꍇ��
		// session�Ɏc�邽�߂����ō폜
		session.removeAttribute("samePasswdId");

		//		int localPort = request.getLocalPort();
		//		int serverPort = request.getServerPort();
		//        String container = "weblogic";
		//        category.debug("container="+System.getenv("container"));
		//        if (System.getenv("container") != null) {
		//            container = "tomcat";
		//        }
		//		Properties appProp = DrasapPropertiesFactory.getDrasapProperties(
		//				new DataSourceFactory());
		//		try{
		//			ds = DataSourceFactory.getOracleDataSource(serverPort);
		//		} catch(Exception e){
		//			category.error("DataSource�̎擾�Ɏ��s\n" + ErrorUtility.error2String(e));
		//		}
		//		Properties pop = System.getProperties();
		//		category.debug("pop="+pop.toString());

		//		ActionMessages errors = new ActionMessages();
		// id�A�p�X���[�h�����Ƀ��[�U�[�����擾���Auser�I�u�W�F�N�g�ɕt������B
		addUserInfo(user, loginForm.getId(), loginForm.getPasswd(), errors);
		// �V�X�e�������Ǘ��Ґݒ�}�X�^�[����擾
		DrasapInfo drasapInfo = getDrasapInfo(user, errors);

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
		session.setAttribute("default_css", "jp".equals(user.getLanKey()) ? "default.css" : "defaultEN.css");

		// �p�X���[�h�ݒ�� + �L���������� < ���ݓ����̏ꍇ�̓p�X���[�h�ύX
		int ret = isChangePassword(user, errors);
		if (0 != ret) {
			if (1 == ret) {
				// �p�X���[�h�����[�UID�Ɠ����ꍇ��session�ɕێ�
				session.setAttribute("samePasswdId", "true");
			}
			// �J�ڌ���session�ɕێ�
			session.setAttribute("parentPage", "Login");
			category.debug("--> chgpasswd");
			return "chgpasswd";
		}
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
	private void addUserInfo(User user, String id, String passwd, Model errors) {
		Connection conn = null;
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(true);// ��g�����U�N�V����
			if (UserDB.addUserInfo(user, id, passwd, conn)) {
				// id,�p�X���[�h����v
			} else {
				// ��v���Ȃ�
				MessageSourceUtil.addAttribute(errors, "message",
						messageSource.getMessage("root.missmatch", new Object[] { "ID�܂���Password" }, null));
			}

		} catch (Exception e) {
			// for ���[�U�[
			MessageSourceUtil.addAttribute(errors, "message",
					messageSource.getMessage("root.failed.get.userinfo", new Object[] { e.getMessage() }, null));
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
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
			MessageSourceUtil.addAttribute(errors, "message",
					messageSource.getMessage("root.failed.get.drasapinfo", new Object[] { e.getMessage() }, null));
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
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
	 * �p�X���[�h�ύX�L���`�F�b�N
	 *
	 * @param user
	 * @param errors
	 * @return 0: �ύX�� <br/> 1: �ύX�L(�p�X���[�h�����[�UID) <br/> 2: �ύX�L(�L�������؂�)
	 * @throws Exception
	 */
	private int isChangePassword(User user, Model errors) throws Exception {

		/*
		 * ���݂̃p�X���[�h�`�F�b�N
		 */
		Connection conn = null;
		conn = ds.getConnection();
		conn.setAutoCommit(true);// ��g�����U�N�V����

		// �p�X���[�h�L�������`�F�b�N
		return UserDB.checkPasswordExpiry(user, errors, conn);
	}

}
