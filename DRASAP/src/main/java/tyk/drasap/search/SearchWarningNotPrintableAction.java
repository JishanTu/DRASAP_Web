package tyk.drasap.search;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.log4j.Category;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import tyk.drasap.common.DataSourceFactory;
import tyk.drasap.common.DrasapPropertiesFactory;
import tyk.drasap.common.ErrorUtility;
import tyk.drasap.common.Printer;
import tyk.drasap.common.User;
import tyk.drasap.errlog.ErrorLoger;

/**
 * �������ʂ���A�o�͎w���������Ƃ��ɁATiff�ȊO��I�����Ă����Ƃ��ɁA
 * �m�F����A�N�V�����B
 * @author fumi
 * �쐬��: 2004/01/19
 */
public class SearchWarningNotPrintableAction extends Action {
	private static DataSource ds;
	private static Category category = Category.getInstance(SearchWarningNotPrintableAction.class.getName());
	static{
		try{
			ds = DataSourceFactory.getOracleDataSource();
		} catch(Exception e){
			category.error("DataSource�̎擾�Ɏ��s\n" + ErrorUtility.error2String(e));
		}
	}
	// --------------------------------------------------------- Methods

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
		ActionMessages errors = new ActionMessages();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		// session�^�C���A�E�g�̊m�F
		if(user == null){
			return mapping.findForward("timeout");
		}
		
		SearchWarningNotPrintableForm notPrintableForm = (SearchWarningNotPrintableForm) form;
		// act�����ɂ�鏈���̐؂蕪��
		if("continue".equals(notPrintableForm.getAct())){
			// �p������
			SearchResultForm searchResultForm = (SearchResultForm) session.getAttribute("searchResultForm");
			int requestCount = requestPrint(searchResultForm, user, errors);
			if(errors.isEmpty()){
				// �����������b�Z�[�W��
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("search.success.print.request." + user.getLanKey(),
								String.valueOf(requestCount)));
			}
			saveErrors(request, errors);
			category.debug("--> search_error");
			return  mapping.findForward("search_error");
		}
		// ���̂܂܌��̌������ʂɖ߂�
		category.debug("--> backResult");
		return mapping.findForward("backResult");
	}
	/**
	 * �Q�l�}�̏o�͎w��������B�Q�l�}�o�͈˗��e�[�u���ɏ����o���B
	 * @param searchResultForm
	 * @param user
	 * @param errors
	 * @return �w����������
	 */
	private int requestPrint(SearchResultForm searchResultForm, User user, ActionMessages errors){
		// �ŏ��ɑI�����ꂽ�v�����^�̃I�u�W�F�N�g���擾
		Printer selectedPrinter = null;
		for(int i = 0; i < user.getEnablePrinters().size(); i++){
			Printer printer = (Printer) user.getEnablePrinters().get(i);
			if(printer.getId().equals(searchResultForm.getOutputPrinter())){
				selectedPrinter = printer;
				break;
			}
		}
		//
		int cnt = 0;
		Connection conn = null;
		try{
			conn = ds.getConnection();
			cnt = PrintRequestDB.insertRequest(searchResultForm.getSearchResultList(), selectedPrinter, user, conn);
			
		} catch(Exception e){
			// for ���[�U�[
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("search.failed.print.request.jp",e.getMessage()));
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this,
						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			// for MUR
			category.error("�Q�l�}�̏o�͎w���Ɏ��s\n" + ErrorUtility.error2String(e));
		} finally {
			try{ conn.close(); } catch(Exception e) {}
		}
		return cnt;
	}

}
