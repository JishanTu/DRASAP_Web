package tyk.drasap.change_passwd;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import tyk.drasap.common.DataSourceFactory;
import tyk.drasap.common.DrasapPropertiesFactory;
import tyk.drasap.common.ErrorUtility;
import tyk.drasap.common.User;
import tyk.drasap.common.UserDB;
import tyk.drasap.errlog.ErrorLoger;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;

public class ChangePasswdAction extends Action {
	private static DataSource ds;
	private static Category category = Category.getInstance(ChangePasswdAction.class.getName());
	static{
		try{
			ds = DataSourceFactory.getOracleDataSource();
		} catch(Exception e){
			category.error("DataSource�̎擾�Ɏ��s\n" + ErrorUtility.error2String(e));
		}
	}

	public ActionForward execute(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception
	{
		category.debug("ChangePassword start");

		ChangePasswdForm passwdForm = (ChangePasswdForm) form;
		ActionMessages errors = new ActionMessages();

		HttpSession session = request.getSession();

		// �Z�b�V��������user���擾
		User user = (User)session.getAttribute("user");

		// session�^�C���A�E�g�̊m�F
		if(user == null){
			return mapping.findForward("timeout");
		}

		Connection conn = null;
		try{
			conn = ds.getConnection();
			conn.setAutoCommit(false); // �����R�~�b�g���Ȃ�

//			category.debug("oldpass=" + passwdForm.getOldpass());
//			category.debug("newpass=" + passwdForm.getNewpass());

			// �p�X���[�h�X�V
			UserDB.updatePassword(user.getId(), passwdForm.getNewpass(), conn);

		} catch(Exception e){
			// for ���[�U�[
			errors.add(ActionMessages.GLOBAL_MESSAGE,
					new ActionMessage("root.failed.get.userinfo." + user.getLanKey(),e.getMessage()));
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this,
						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			// for MUR
			category.error("���[�U�[���̎擾�Ɏ��s\n" + ErrorUtility.error2String(e));
			throw e;
		} finally {
			try{ conn.close(); } catch(Exception e) {}
		}

		String page = (String)session.getAttribute("parentPage");
		String forward = "";
		if(errors.isEmpty()){
			if(StringUtils.isEmpty(page) || "Search".equals(page)) {
				// �J�ڌ����}�ʌ�����ʂ̏ꍇ
				category.debug("--> success");
				forward = "successFromSearch";
			} else if ("Login".equals(page)) {
				// �J�ڌ������O�C����ʂ̏ꍇ
				category.debug("--> success");
				forward = "successFromLogin";
			}
			// session����폜
			session.removeAttribute("samePasswdId");
//			session.removeAttribute("parentPage");
		} else {
			category.debug("--> failed");
			// ���s�̏ꍇ�̓p�X���[�h�ύX��ʂɑJ��
			forward = "failed";
		}

		return mapping.findForward(forward);

	}
}
