package tyk.drasap.search;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Category;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import tyk.drasap.common.DrasapPropertiesFactory;
import tyk.drasap.common.ErrorUtility;
import tyk.drasap.common.User;
import tyk.drasap.errlog.ErrorLoger;

/**
 * �������ʂ��t�@�C���o�͂���
 * @author fumi
 * �쐬��: 2004/01/19
 */
public class OutAttrCsvAction extends Action {
	private static Category category = Category.getInstance(OutAttrCsvAction.class.getName());
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
		HttpServletRequest request,HttpServletResponse response) throws Exception {
		category.debug("start");
		// �����i�K
		ActionMessages errors = new ActionMessages();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
//		DrasapInfo drasapInfo = (DrasapInfo) session.getAttribute("drasapInfo");
		ServletContext context = getServlet().getServletContext();
		String tempDirName = context.getRealPath("temp");// �e���|�����̃t�H���_�̃t���p�X
		// session�^�C���A�E�g�̊m�F
		if(user == null){
			return mapping.findForward("timeout");
		}

		SearchResultForm searchResultForm = (SearchResultForm) session.getAttribute("searchResultForm");
		String outAllFlug = (String)request.getAttribute("OUT_CSV_ALL");// �S������?
		// �܂�CSV�t�@�C�����쐬����
		String outFileName = tempDirName + File.separator + session.getId() + "_" + (new Date().getTime());
		File outFile = new File(outFileName);
		writeAttrCsv(outFile, searchResultForm,
					(outAllFlug!= null && outAllFlug.equals("true")), // null�łȂ��A"true"�̂Ƃ���
					 user, errors);// CSV�쐬
		if(!errors.isEmpty()){
			// CSV�t�@�C���쐬�ŃG���[���������Ă�����
			saveErrors(request, errors);
			category.debug("--> error");
			return mapping.findForward("error");
			
		} else {
			String streamFileName = "SeachResult.csv";// �w�b�_�ɃZ�b�g����t�@�C����
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition","attachment;" +
				" filename=" + new String(streamFileName.getBytes("Windows-31J"),"ISO8859_1"));
			//���̂܂܂��Ɠ��{�ꂪ������
			//response.setHeader("Content-Disposition","attachment; filename=" + fileName);
			response.setContentLength((int)outFile.length());
	
			BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(outFile));
			int c;
			while ((c = in.read()) != -1){
				out.write(c);
			}
			in.close();
			out.flush();
			out.close();
			//
			category.debug("out.close()");
			// CSV�t�@�C�����폜����
			if(outFile.delete()){
				category.debug(outFileName + " ���폜����");				
			}
			return null;
		}
	}
	/**
	 * �w�肵���t�@�C���Ƃ��āA�������ʂ�CSV�`���ō쐬����B
	 * �쐬�Ɏ��s�����ꍇ�Aerrors�ɏ����o���B
	 * @param outFile �o�͐�̃t�@�C��
	 * @param searchResultForm
	 * @param allAttr �S�����Ȃ�true
	 * @param user
	 * @param errors
	 */
	private void writeAttrCsv(File outFile, SearchResultForm searchResultForm, boolean allAttr,
							User user, ActionMessages errors){
		OutputStreamWriter out = null;
		try{
			// �o�̓X�g���[���̏���
			out = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(outFile)),
											"Windows-31J");
//											"UTF-8");
			searchResultForm.writeAttrCsv(out, allAttr);
			//
		} catch(Exception e){
			// for ���[�U�[
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("search.failed.csv." + user.getLanKey(), e.getMessage()));
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this,
						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.csv"));
			// for MUR
			category.error("�������ʂ̃t�@�C���o�͂Ɏ��s\n" + ErrorUtility.error2String(e));
			
		} finally {
			try{ out.close(); } catch(Exception e){}
		}
		
	}
	

}
