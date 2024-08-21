package tyk.drasap.root;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import tyk.drasap.common.AdminSettingDB;
import tyk.drasap.common.CookieManage;
import tyk.drasap.common.DataSourceFactory;
import tyk.drasap.common.DrasapInfo;
import tyk.drasap.common.DrasapPropertiesFactory;
import tyk.drasap.common.ErrorUtility;
import tyk.drasap.common.User;
import tyk.drasap.common.UserDB;
import tyk.drasap.common.UserDef;
import tyk.drasap.errlog.ErrorLoger;

/**
 * ���O�C����Action�B
 */
public class LoginAction extends Action {
	private static DataSource ds;
	private static Category category = Category.getInstance(LoginAction.class.getName());
	static{
		try{
			ds = DataSourceFactory.getOracleDataSource();
		} catch(Exception e){
			category.error("DataSource�̎擾�Ɏ��s\n" + ErrorUtility.error2String(e));
		}
	}
	// --------------------------------------------------------- Methods

	public LoginAction () {
		category.debug("start");
		category.debug("end");
	}
	/**
	 * Method execute
	 * @param ActionMapping mapping
	 * @param ActionForm form
	 * @param HttpServletRequest request
	 * @param HttpServletResponse response
	 * @return ActionForward
	 * @throws Exception
	 */
	public ActionForward execute(ActionMapping mapping,ActionForm form,
			HttpServletRequest request,	HttpServletResponse response) throws Exception {
		category.debug("start");
		LoginForm loginForm = (LoginForm) form;
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

		ActionMessages errors = new ActionMessages();
		// id�A�p�X���[�h�����Ƀ��[�U�[�����擾���Auser�I�u�W�F�N�g�ɕt������B
		addUserInfo(user, loginForm.getId(), loginForm.getPasswd(), errors);
		// �V�X�e�������Ǘ��Ґݒ�}�X�^�[����擾
		DrasapInfo drasapInfo = getDrasapInfo(user, errors);

		//
		if(errors.isEmpty()){
			// �N�b�L�[���猾��ݒ���擾
			CookieManage langCookie = new CookieManage();
			String lanKey = langCookie.getCookie (request, user, "Language");
			if (lanKey == null || lanKey.length() == 0) lanKey = "Japanese";
			user.setLanguage (lanKey);
			// ���[�U�[��񂪎擾�ł����� session�Ɋi�[����
			session.setAttribute("user", user);
			session.setAttribute("drasapInfo", drasapInfo);
			session.setAttribute("default_css", user.getLanKey().equals("jp")?"default.css":"defaultEN.css");

			// �p�X���[�h�ݒ�� + �L���������� < ���ݓ����̏ꍇ�̓p�X���[�h�ύX
			int ret = isChangePassword(user, errors);
			if(0 != ret ) {
				if (1 == ret) {
					// �p�X���[�h�����[�UID�Ɠ����ꍇ��session�ɕێ�
					session.setAttribute("samePasswdId", "true");
				}
				// �J�ڌ���session�ɕێ�
				session.setAttribute("parentPage", "Login");
				category.debug("--> chgpasswd");
				return mapping.findForward("chgpasswd");
			}

			category.debug("--> success");
			return mapping.findForward("success");

		} else {
			saveErrors(request, errors);// �G���[��o�^
			category.debug("--> failed");
			return mapping.findForward("failed");
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
	private void addUserInfo(User user, String id, String passwd, ActionMessages errors){
		Connection conn = null;
		try{
			conn = ds.getConnection();
			conn.setAutoCommit(true);// ��g�����U�N�V����
			if(UserDB.addUserInfo(user, id, passwd, conn)){
				// id,�p�X���[�h����v
			} else {
				// ��v���Ȃ�
				errors.add(ActionMessages.GLOBAL_MESSAGE,
						new ActionMessage("root.missmatch", "ID�܂���Password"));
			}

		} catch(Exception e){
			// for ���[�U�[
			errors.add(ActionMessages.GLOBAL_MESSAGE,
					new ActionMessage("root.failed.get.userinfo", e.getMessage()));
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this,
						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			// for MUR
			category.error("���[�U�[���̎擾�Ɏ��s\n" + ErrorUtility.error2String(e));
		} finally {
			try{ conn.close(); } catch(Exception e) {}
		}
	}

	/**
	 * �V�X�e�������擾����B
	 * �G���[�������errors�ɃG���[�o�^����B
	 * @param user
	 * @param errors
	 */
	private DrasapInfo getDrasapInfo(User user, ActionMessages errors){
		Connection conn = null;
		DrasapInfo drasapInfo = null;
		try{
			conn = ds.getConnection();
			conn.setAutoCommit(true);// ��g�����U�N�V����

			drasapInfo = AdminSettingDB.getDrasapInfo(conn);

		} catch(Exception e){
			// for ���[�U�[
			errors.add(ActionMessages.GLOBAL_MESSAGE,
					new ActionMessage("root.failed.get.drasapinfo", e.getMessage()));
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this,
						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			// for MUR
			category.error("�V�X�e�����̎擾�Ɏ��s\n" + ErrorUtility.error2String(e));
		} finally {
			try{ conn.close(); } catch(Exception e) {}
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
	private int isChangePassword(User user, ActionMessages errors) throws Exception {

		/*
		 * ���݂̃p�X���[�h�`�F�b�N
		 */
	    Connection conn = null;
		conn = ds.getConnection();
		conn.setAutoCommit(true);// ��g�����U�N�V����

		String userId = user.getId();
	    String currentPass = UserDB.getPassword(userId, conn);

	    if(StringUtils.isEmpty(currentPass) || userId.equals(currentPass)) {
	    	// �p�X���[�h���ݒ� �������� �p�X���[�h�����[�UID�Ɠ���
	    	return 1;
	    }

		/*
		 *  �p�X���[�h�L�������̊m�F
		 */
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar passwdUpCalendar = Calendar.getInstance();

		// �p�X���[�h��`�t�@�C������l�擾
		UserDef userdef = new UserDef();
		HashMap<String, String> passwdDefMap = userdef.getPasswdDefinition(errors);

		// �p�X���[�h�L�����������`�F�b�N
		int pwdLmtDay = Integer.parseInt(passwdDefMap.get(UserDef.PWD_LMT_DAY));

		// ���ݓ����̎擾
		Calendar nowCal = Calendar.getInstance();

		// �����b���N���A
	    nowCal.clear(Calendar.MINUTE);
	    nowCal.clear(Calendar.SECOND);
	    nowCal.clear(Calendar.MILLISECOND);
	    nowCal.set(Calendar.HOUR_OF_DAY, 0);

	    // �p�X���[�h�ݒ���擾
	    Date pwdUpDate = user.getPasswdUpdDate();

	    if(pwdUpDate == null ) {
	    	// �p�X���[�h�ݒ�������ݒ�̏ꍇ�͍Đݒ�Ώ�
	    	return 2;
	    }

		passwdUpCalendar.setTime(pwdUpDate); // DATE -> Calendar

		// �p�X���[�h�ݒ�� + �L����������
		passwdUpCalendar.add(Calendar.DATE, pwdLmtDay);

		Date passwdLimitDate = passwdUpCalendar.getTime(); // Calendar -> DATE
		Date nowDate = nowCal.getTime(); // Calendar -> DATE

		category.debug("Now Date=" + sdf.format(nowDate));
		category.debug("Password Limit Date=" + sdf.format(passwdLimitDate));

		// �p�X���[�h�ݒ�� + �L���������� < ���ݓ����̏ꍇ�̓p�X���[�h�ύX
		if(nowDate.after(passwdLimitDate)) {
			return 2;
		}

		return 0;
	}

}
