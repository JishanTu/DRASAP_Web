package tyk.drasap.root;


import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Category;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/** 
 * �ʒm�[�E�}�ʂ̍폜
 * @author Y.eto
 * �쐬��: 2006/05/10
 */
public class DirectSearchAction extends Action {
	private static Category category = Category.getInstance(DirectSearchAction.class.getName());
	// --------------------------------------------------------- Methods

	/** 
	 * Method execute
	 * @param ActionMapping mapping
	 * @param ActionForm form
	 * @param HttpServletRequest request
	 * @param HttpServletResponse response
	 * @return ActionForward
	 * @throws SQLException 
	 * @throws Exception
	 */
	public ActionForward execute(ActionMapping mapping,ActionForm form,
			HttpServletRequest request,	HttpServletResponse response) throws SQLException {
		category.debug("start");
		// �p�����[�^�� en_string ���󂯎��
		String enString = request.getParameter("en_string");
		// �p�����[�^�� drwg_no ���󂯎��
		String sys_id = request.getParameter("sys_id");
		//String[] attrArray = request.getParameterValues("DRWG_NO");
		//int len = attrArray.length;

		// en_string ��setAttribute����
		category.debug("enString = " + enString);
		if(enString != null){
			// ���_�C���N�g����ƁArequest��attribute�͏����Ă��܂��̂�
			// session�Ɋi�[����B
			request.getSession().setAttribute("en_string", enString);
		}
		if(sys_id != null){
			request.getSession().setAttribute("sys_id", sys_id);
		}
		
		// ���_�C���N�g����
		category.debug("redirect --> /directLoginForPreview.do");
		return new ActionForward("/directLoginForPreview.do", true);
	}
}
