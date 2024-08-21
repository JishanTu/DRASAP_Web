package tyk.drasap.root;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Category;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
/**
 * LoginWithIddeAction�ɂȂ����߂́A�O�����B
 * �ړI�̓u���E�U��URL�o�[�ɈÍ����\�������̂�h�����߁B
 * RequestParameter�� en_string,fn ���󂯂�B
 * en_string�E�E�E�Í������ꂽ���O�C��ID
 * fn�E�E�E�t�@���N�V����
 */
public class LoginWithIddePreAction extends Action {
	private static Category category = Category.getInstance(LoginWithIddePreAction.class.getName());
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
		// �p�����[�^�� en_string ���󂯎��
		String enString = request.getParameter("en_string");
		// �p�����[�^fn��ǉ� '04.May.13
		String fn = request.getParameter("fn");

		// en_string ��setAttribute����
		category.debug("enString = " + enString);
		if(enString != null){
			// ���_�C���N�g����ƁArequest��attribute�͏����Ă��܂��̂�
			// session�Ɋi�[����B
			request.getSession().setAttribute("en_string", enString);
		} else {
			return mapping.findForward("timeout");
		}
		if(fn != null){
			request.getSession().setAttribute("fn", fn);
		}

		// ���_�C���N�g����
		category.debug("redirect --> /loginWithIdde.do");
//		return new ActionForward("/loginWithIdde.do", true);
		return mapping.findForward("success");
	}

}
