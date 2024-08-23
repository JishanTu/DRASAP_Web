package tyk.drasap.genzu_irai;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.log4j.Category;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import tyk.drasap.common.DataSourceFactory;
import tyk.drasap.common.DrasapPropertiesFactory;
import tyk.drasap.common.DrasapUtil;
import tyk.drasap.common.ErrorUtility;
import tyk.drasap.common.User;
import tyk.drasap.errlog.ErrorLoger;

/**
 * ���}�ɍ�ƈ˗����X�g(���}�ɒS����)��ʂ���A��������ʂɑΉ��B
 */
public class RequestPriAction extends Action {
	private static DataSource ds;
	
	private static Category category = Category.getInstance(RequestPriAction.class.getName());
	private String classId = "";
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
				HttpServletResponse response)
				throws Exception {
					
		Request_listForm request_resultForm = (Request_listForm) form;
		category.debug("RequestPriAction �X�^�[�g");
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		if(user == null){
			return mapping.findForward("timeout");
		}
		try{
			// �N���XID�擾
			classId = DrasapPropertiesFactory.getDrasapProperties(this).getProperty("tyk.drasap.genzu_irai.RequestPriAction");
		}catch(java.lang.Exception e){
				category.error("�v���p�e�B�t�@�C���̓Ǎ��݂Ɏ��s���܂���\n" + ErrorUtility.error2String(e));
		}
		category.debug("action = " + request_resultForm.action);
		if("button_print".equals(request_resultForm.action)){
			
			java.sql.Connection conn = null;
			java.sql.ResultSet rs = null;							// ���U���g�Z�b�g
            java.sql.Statement stmt = null;
			request_resultForm.printList = new ArrayList();
			try{
				conn = ds.getConnection();
				stmt = conn.createStatement();	// �f�[�^���݃`�F�b�N�p�R�l�N�V�����X�e�[�g�����g
			  	String strSql = "select JOB_REQUEST_TABLE.JOB_ID,JOB_NAME,GOUKI_NO,CONTENT,USER_NAME,DEPT_NAME,EXPAND_DRWG_NO,JOB_REQUEST_TABLE.ROW_NO,SEQ_NO" +
			  					" from JOB_REQUEST_TABLE,JOB_REQUEST_EXPAND_TABLE" +
			  					// ���������Ƀo�O����B'04.May.19�C�� by Hirata
			  					" where JOB_REQUEST_TABLE.JOB_ID = JOB_REQUEST_EXPAND_TABLE.JOB_ID(+)" +
								" and JOB_REQUEST_TABLE.ROW_NO = JOB_REQUEST_EXPAND_TABLE.ROW_NO(+)" + // ���������ɍs�ԍ��K�v
								" and JOB_REQUEST_EXPAND_TABLE.EXIST is null" + // ������݂̂�ΏۂƂ���
			  					" order by JOB_ID, ROW_NO, SEQ_NO";
				category.debug("sql = " + strSql);				
				rs = stmt.executeQuery(strSql);
				while(rs.next()){
					String job_id = rs.getString("JOB_ID");//�˗�ID
					String job_Name = rs.getString("JOB_NAME");//�˗����e
					String gouki = rs.getString("GOUKI_NO");//�����E���@
					String genzu = rs.getString("CONTENT");//���}���e
					String irai = rs.getString("USER_NAME");//�˗���
					String busyo = rs.getString("DEPT_NAME");//������
					String number = rs.getString("EXPAND_DRWG_NO");//�ԍ�
					if("�}�ʓo�^�˗�".equals(job_Name) || "�}�ʏo�͎w��".equals(job_Name)){
						// ���`����̂́A�}�ʓo�^�˗��A�}�ʏo�͎w���̂Ƃ��̂݁E�E�E�ύX '04/07/22 by Hirata
						
						//�ԍ���11���̏ꍇ�́u-�v��t����
						if(number != null){
							number = DrasapUtil.formatDrwgNo(number);	
						}
					}
	
					if("�}�ʓo�^�˗�".equals(job_Name) || "�}�ʏo�͎w��".equals(job_Name)){
						request_resultForm.printList.add(new RequestPriElement(job_id,job_Name,gouki,genzu,irai,busyo,number));
					}	
				}	
	
		  	}catch(Exception e){
				request_resultForm.listErrors.add("���}�ɍ�ƈ˗����X�g�̎擾�Ɏ��s���܂���\n" + e.getMessage());
				//for �V�X�e���Ǘ���
				ErrorLoger.error(user, this,DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));	
			  	//errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(classId, e.getMessage()));
			  	category.error("[" + classId + "]:���}�ɍ�ƈ˗����X�g�̍폜�Ɏ��s���܂���\n" + ErrorUtility.error2String(e));
				throw new Exception(e.getMessage());	
		  	}finally{
			  	try{
					if(stmt != null){
						stmt.close();stmt = null;
				  	}
			  	}catch(Exception e){}	
		  	}
			java.util.Calendar calendar = java.util.Calendar.getInstance();
			String today = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(calendar.getTime());// �{��
			request_resultForm.time = today;	
	
			//�A�N�Z�X���O���擾
			// ����p�̕\���ł́A�A�N�Z�X���O�͎c���Ȃ��B '04.May.17�C�� by Hirata
			//AccessLoger.loging(user, AccessLoger.FID_GENZ_REQ);
			category.debug("--> printer");
			return(mapping.findForward("printer"));				
		}
		
					
		return(mapping.findForward("success"));						
	}

}
