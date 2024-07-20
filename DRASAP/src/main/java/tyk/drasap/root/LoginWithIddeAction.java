package tyk.drasap.root;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import jp.co.toyodakouki.IDDE;

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
import tyk.drasap.errlog.ErrorLoger;
/**
 * �V���O���T�C���I���̂��߂̃��O�C���B�|�[�^������̃��O�C���ɑΉ����邽�߁B
 * �p�����[�^(en_string)���󂯎��A�Í��������c�[��(IDDE)���g�p����ID���擾����B
 * ���j���[������ɁA���ڃt�@���N�V�����ɑJ�ڂ��邽�߂ɁA�p�����[�^fn��ǉ��B'04.May.13
 * @version 2013/07/25 yamagishi
 */
public class LoginWithIddeAction extends Action {
	private static DataSource ds;
	private static Category category = Category.getInstance(LoginWithIddeAction.class.getName());
	static{
		try{
			ds = DataSourceFactory.getOracleDataSource();
		} catch(Exception e){
			category.error("DataSource�̎擾�Ɏ��s\n" + ErrorUtility.error2String(e));
		}
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
		// �܂�User���쐬����
		User user = new User(request.getRemoteAddr());
		HttpSession session = request.getSession();
		session.removeAttribute("user");// ���ł�session����폜����
		category.debug("session ID="+session.getId());
		ActionMessages errors = new ActionMessages();
		DrasapInfo drasapInfo = null;

		// session����p�����[�^���擾���A��������
		String enString = (String) session.getAttribute("en_string");
		session.removeAttribute("en_string");// session����remove����
		category.debug("en_string = "+ enString);
		if (enString == null) {
			return mapping.findForward("timeout");
		}
		String id = IDDE.decode(enString);// ��������������
		category.debug("id = "+ id);
		// �p�����[�^ fn��ǉ�
		String fn = (String) session.getAttribute("fn");
		session.removeAttribute("fn");// session����remove����
		if (fn == null) {
			return mapping.findForward("timeout");
		}
		category.debug("fn = "+ fn);

		if(id.equals("-1")){
			// ���O�C��ID�ُ�
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("root.failed.decode." + user.getLanKey(),id,"���O�C��ID�ُ�"));
		} else if(id.equals("-2")){
			// SYSpass�ُ�
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("root.failed.decode." + user.getLanKey(),id,"SYSpass�ُ�"));
		} else {
//			int serverPort = request.getServerPort();
//			category.debug("serverPort="+Integer.toString(serverPort));
//			try{
//				ds = DataSourceFactory.getOracleDataSource(serverPort);
//			} catch(Exception e){
//				category.error("DataSource�̎擾�Ɏ��s\n" + ErrorUtility.error2String(e));
//			}
			// id�����Ƀ��[�U�[�����擾���Auser�I�u�W�F�N�g�ɕt������B
			addUserInfo(user, id, errors);

			// �V�X�e�������Ǘ��Ґݒ�}�X�^�[����擾
			drasapInfo = getDrasapInfo(user, errors);
		}

		if(errors.isEmpty()){
			// �N�b�L�[���猾��ݒ���擾���Auser�I�u�W�F�N�g�ɕt������B
			CookieManage langCookie = new CookieManage();
			String lanKey = langCookie.getCookie (request, user, "Language");
			if (lanKey == null || lanKey.length() == 0) lanKey = "Japanese";
			user.setLanguage (lanKey);

			// ���[�U�[��񂪎擾�ł����� session�Ɋi�[����
			session.setAttribute("user", user);
			session.setAttribute("drasapInfo", drasapInfo);
			session.setAttribute("default_css", user.getLanKey().equals("jp")?"default.css":"defaultEN.css");
			// �p�����[�^fn�ɂ���ď����𕪂���B'04.May.13�ύX
			// fn = 1-4 �́A�O���[�o���t�H���[�h���g�p����B
			if("1".equals(fn)){
				// fn = 1 �Ȃ�@�}�ʌ����@
				category.debug("--> search_search_Main");
				return mapping.findForward("search_search_Main");

			} else if("2".equals(fn)){
				// fn = 2 �Ȃ�@���}�ɍ�ƈ˗��@
				category.debug("--> genzu_irai_request");
				return mapping.findForward("genzu_irai_request");

			} else if("3".equals(fn)){
				// fn = 3 �Ȃ�@���}�ɍ�ƈ˗��ڍׁ@
				category.debug("--> genzu_irai_request_ref");
				return mapping.findForward("genzu_irai_request_ref");

			} else if("4".equals(fn)){
				// fn = 4 �Ȃ�@���}�ɍ�ƈ˗����X�g
				category.debug("--> genzu_irai_request_list");
				return mapping.findForward("genzu_irai_request_list");

// 2013.07.25 yamagishi add. start
			} else if ("5".equals(fn)) {
				// fn = 5 �Ȃ�@�A�N�Z�X���x���ꊇ�X�V
				category.debug("--> acl_batch_update");
				return mapping.findForward("acl_batch_update");
			} else if ("6".equals(fn)) {
				// fn = 6 �Ȃ�@�A�N�Z�X���x���X�V����
				category.debug("--> acl_updated_result");
				return mapping.findForward("acl_updated_result");
// 2013.07.25 yamagishi add. end

			} else{
				// ����ȊO�̏ꍇ�̓��j���[��
				category.debug("--> success");
				return mapping.findForward("success");

			}

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
	private void addUserInfo(User user, String id, ActionMessages errors){
		Connection conn = null;
		try{
			conn = ds.getConnection();
			conn.setAutoCommit(true);// ��g�����U�N�V����
			if(UserDB.addUserInfo(user, id, conn)){
				// id����v
			} else {
				// ��v���Ȃ�
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("root.missmatch."+user.getLanKey(),"Id"));
			}

		} catch(Exception e){
			// for ���[�U�[
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("root.failed.get.userinfo." + user.getLanKey(),e.getMessage()));
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
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("root.failed.get.drasapinfo.jp",e.getMessage()));
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

}
