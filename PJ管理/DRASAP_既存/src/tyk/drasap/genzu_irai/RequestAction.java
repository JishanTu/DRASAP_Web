package tyk.drasap.genzu_irai;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import tyk.drasap.acslog.AccessLoger;
import tyk.drasap.common.DataSourceFactory;
import tyk.drasap.common.DrasapPropertiesFactory;
import tyk.drasap.common.DrasapUtil;
import tyk.drasap.common.ErrorUtility;
import tyk.drasap.common.JobReqSeqDB;
import tyk.drasap.common.Printer;
import tyk.drasap.common.User;
import tyk.drasap.errlog.ErrorLoger;

/**
 * ���}�ɍ�ƈ˗���Action
 */
@SuppressWarnings("deprecation")
public class RequestAction extends Action {
	private static DataSource ds;
	private static Category category = Category.getInstance(RequestAction.class.getName());
	private String classId = "";
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
	public ActionForward execute(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		RequestForm requestForm = (RequestForm) form;
		category.debug("RequestAction �X�^�[�g");
		ActionErrors errors = new ActionErrors();//�G���[���e��\������
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		if(user == null){
			return mapping.findForward("timeout");
		}

		try{
			// �N���XID�擾
			classId = DrasapPropertiesFactory.getDrasapProperties(this).getProperty("tyk.drasap.genzu_irai.RequestAction");
		}catch(java.lang.Exception e){
			category.error("�v���p�e�B�t�@�C���̓Ǎ��݂Ɏ��s���܂���\n" + ErrorUtility.error2String(e));
		}
		category.debug("action = " + requestForm.action);

		if("button_irai".equals(requestForm.action)){ //���}�ɍ�ƈ˗�������
			if("".equals(requestForm.irai)){
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.irai.required"));//�˗��敪��I�����Ă�������
				saveErrors(request, errors);
				return(mapping.findForward("success"));
			}
			//�f�[�^�̃`�F�b�N
			CheckData(requestForm, form, errors, request);
			if(errors.isEmpty()){
				String irai = "";
				if("�}�ʓo�^�˗�".equals(requestForm.irai)){
					irai = "A";
				}else if("�}�ʏo�͎w��".equals(requestForm.irai)){
					irai = "B";
				}else if("���}�ؗp�˗�".equals(requestForm.irai)){
					irai = "C";
				}else if("�}�ʈȊO�ĕt�˗�".equals(requestForm.irai)){
					irai = "D";
				}

				String zumenAll = "0";//���ׂĂ̐}�Ԃ��o�^�ς݂��ǂ����̃`�F�b�N�B1�̏ꍇ�͂��ׂēo�^�ς�
				java.sql.Connection conn= null;
				conn = ds.getConnection();
				String job_Id =JobReqSeqDB.getJobId(irai, conn);//���}�ɍ�ƈ˗��ł̃W���uID���擾����
				conn.close();
				conn = null;
				java.util.Calendar calendar = java.util.Calendar.getInstance();
				String day = new java.text.SimpleDateFormat("yyyy/MM/dd").format(calendar.getTime());//�{���̓��t
				try{
					conn = ds.getConnection();
					//�K���R�~�b�g���g�p���A�������x����Repeatable Read�ȏ��
					conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
					// �g�����U�N�V�����������x����Repeatable Read
					conn.setAutoCommit(false);		// �����R�~�b�g���I�t��

					// �}�ʏo�͎w���̂Ƃ�
					// �o�͐悪��p�v�����^�Ȃ�v�����^�X�^���v,EUC�Ȃ�EUC�v�����^�X�^���v��
					// user����擾����true�ł���΃X�^���v�t���O���u1�v�ɂ���
					if("B".equals(irai)){
						// �X�^���v�t���O������������
						requestForm.printer_flag = "0";

						// �w�肳�ꂽ�v�����^�����[�U�[�̎g�p�\�ȃv�����^����擾����
						for(int i = 0; i < user.getEnablePrinters().size(); i++){
							Printer printer = (Printer)user.getEnablePrinters().get(i);
							if(printer.getId().equals(requestForm.syutu)){// �v�����^��������
								if(printer.isEucPrinter()){
									// �w�肵���v�����^��EUC�Ȃ�
									if(user.isEucStamp()){
										requestForm.printer_flag = "1";
									} else {
										requestForm.printer_flag = "0";
									}
								} else {
									// ��p�v�����^�Ȃ�
									if(user.isPltrStamp()){
										requestForm.printer_flag = "1";
									} else {
										requestForm.printer_flag = "0";
									}
								}
								break;// for���[�v�𔲂���
							}
						}
					}else{
						requestForm.printer_flag = "0";
						requestForm.syutu = "";//�o�͐�
					}

					if("on".equals(requestForm.hiddenNo1)){ //1�s�ڂ̓��e���˗��ǉ�����
						String kaisiNo = requestForm.kaisiNo1;//�J�n�ԍ�1
						String syuryoNo = requestForm.syuuryouNo1;//�I���ԍ�1
						if("A".equals(irai) || "B".equals(irai)){//�}�ʓo�^�˗��A�}�ʏo�͎w���̏ꍇ�͐}�ʂ�W�J���邱�Ƃ��ł���
							String gyoNo = "1";//�s
							doSitei(requestForm,errors,user,irai,request,kaisiNo,syuryoNo,zumenAll,gyoNo, job_Id,conn);
							zumenAll = requestForm.zumenAll;//�W�J�����}�ʂ��S�ēo�^�ς݂̏ꍇ�͍�Ɗ����̂��߁u1�v
						}
						if(errors.isEmpty()){
							if("".equals(kaisiNo)){//�J�n�ԍ����Ȃ��A�I���ԍ��݂̂̏ꍇ�͊J�n�ԍ��ɓo�^����
								kaisiNo = syuryoNo;
								syuryoNo = "";
							}
							doInsert1(requestForm, errors, user, irai, zumenAll, kaisiNo, syuryoNo,job_Id, day,conn);
						}
					}
					if("on".equals(requestForm.hiddenNo2)){//2�s�ڂ̓��e���˗��o�^����
						String kaisiNo = requestForm.kaisiNo2;//�J�n�ԍ�2
						String syuryoNo = requestForm.syuuryouNo2;//�I���ԍ�2
						if("A".equals(irai) || "B".equals(irai)){//�}�ʓo�^�˗��A�}�ʏo�͎w���̏ꍇ�͐}�ʂ�W�J���邱�Ƃ��ł���
							String gyoNo = "2";//�s
							doSitei(requestForm,errors,user,irai,request,kaisiNo,syuryoNo,zumenAll,gyoNo,job_Id,conn);
							zumenAll = requestForm.zumenAll;//�W�J�����}�ʂ��S�ēo�^�ς݂̏ꍇ�͍�Ɗ����̂��߁u1�v
						}
						if(errors.isEmpty()){
							if("".equals(kaisiNo)){//�J�n�ԍ����Ȃ��I���ԍ��݂̂̏ꍇ�͊J�n�ԍ��ɓo�^����
								kaisiNo = syuryoNo;
								syuryoNo = "";
							}
							doInsert2(requestForm, errors, user, irai, zumenAll, kaisiNo, syuryoNo, job_Id, day, conn);
						}
					}
					if("on".equals(requestForm.hiddenNo3)){ //3�s�ڂ̓��e���˗��o�^����
						String kaisiNo = requestForm.kaisiNo3;//�J�n�ԍ�3
						String syuryoNo = requestForm.syuuryouNo3;//�I���ԍ�3
						if("A".equals(irai) || "B".equals(irai)){//�}�ʓo�^�˗��A�}�ʏo�͎w���̏ꍇ�͐}�ʂ�W�J���邱�Ƃ��ł���
							String gyoNo = "3";//�s
							doSitei(requestForm,errors,user,irai,request,kaisiNo,syuryoNo,zumenAll,gyoNo,job_Id,conn);
							zumenAll = requestForm.zumenAll;//�W�J�����}�ʂ��S�ēo�^�ς݂̏ꍇ�͍�Ɗ����̂��߁u1�v
						}
						if(errors.isEmpty()){
							if("".equals(kaisiNo)){//�J�n�ԍ����Ȃ��I���ԍ��݂̂̏ꍇ�͊J�n�ԍ��ɓo�^����
								kaisiNo = syuryoNo;
								syuryoNo = "";
							}
							doInsert3(requestForm, errors, user, irai, zumenAll, kaisiNo, syuryoNo, job_Id, day, conn);
						}
					}
					if("on".equals(requestForm.hiddenNo4)){//4�s�ڂ̓��e���˗��o�^����
						String kaisiNo = requestForm.kaisiNo4;//�J�n�ԍ�4
						String syuryoNo = requestForm.syuuryouNo4;//�I���ԍ�4
						if("A".equals(irai) || "B".equals(irai)){//�}�ʓo�^�˗��A�}�ʏo�͎w���̏ꍇ�͐}�ʂ�W�J���邱�Ƃ��ł���
							String gyoNo = "4";//�s
							doSitei(requestForm,errors,user,irai,request,kaisiNo,syuryoNo,zumenAll,gyoNo,job_Id,conn);
							zumenAll = requestForm.zumenAll;//�W�J�����}�ʂ��S�ēo�^�ς݂̏ꍇ�͍�Ɗ����̂��߁u1�v
						}
						if(errors.isEmpty()){
							if("".equals(kaisiNo)){//�J�n�ԍ����Ȃ��I���ԍ��݂̂̏ꍇ�͊J�n�ԍ��ɓo�^����
								kaisiNo = syuryoNo;
								syuryoNo = "";
							}
							doInsert4(requestForm, errors, user, irai, zumenAll, kaisiNo, syuryoNo, job_Id, day, conn);
						}
					}
					if("on".equals(requestForm.hiddenNo5)){//5�s�ڂ̓��e���˗��o�^����
						String kaisiNo = requestForm.kaisiNo5;//�J�n�ԍ�5
						String syuryoNo = requestForm.syuuryouNo5;//�I���ԍ�5
						if("A".equals(irai) || "B".equals(irai)){//�}�ʓo�^�˗��A�}�ʏo�͎w���̏ꍇ�͐}�ʂ�W�J���邱�Ƃ��ł���
							String gyoNo = "5";//�s
							doSitei(requestForm,errors,user,irai,request,kaisiNo,syuryoNo,zumenAll,gyoNo,job_Id,conn);
							zumenAll = requestForm.zumenAll;//�W�J�����}�ʂ��S�ēo�^�ς݂̏ꍇ�͍�Ɗ����̂��߁u1�v
						}
						if(errors.isEmpty()){
							if("".equals(kaisiNo)){//�J�n�ԍ����Ȃ��I���ԍ��݂̂̏ꍇ�͊J�n�ԍ��ɓo�^����
								kaisiNo = syuryoNo;
								syuryoNo = "";
							}
							doInsert5(requestForm, errors, user, irai, zumenAll, kaisiNo, syuryoNo,job_Id, day, conn);
						}
					}
					if(errors.isEmpty()){
						if("on".equals(requestForm.hiddenNo1) || "on".equals(requestForm.hiddenNo2) || "on".equals(requestForm.hiddenNo3) || "on".equals(requestForm.hiddenNo4) || "on".equals(requestForm.hiddenNo5)){
							category.debug("�R�~�b�g");
							conn.commit();	// �R�~�b�g
							//�A�N�Z�X���O���擾
							AccessLoger.loging(user, AccessLoger.FID_GENZ_REQ);
							category.debug("�˗��o�^�����܂���");
							errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("touroku.irai"));
							saveErrors(request, errors);
						}
					}else{
						if(conn != null){
							category.debug("���[���o�b�N");
							conn.rollback();
							conn.setAutoCommit(true);		// �����R�~�b�g���I����
						}
					}

				}catch(Exception e){
					errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.outer.exception", e.getMessage()));
					saveErrors(request, errors);
					category.error("�\�z�O�̗�O���������܂���\n" + ErrorUtility.error2String(e));
					try{
						if(conn != null){
							category.debug("���[���o�b�N");
							conn.rollback();
							conn.setAutoCommit(true);		// �����R�~�b�g���I����
						}
					}catch(java.sql.SQLException se2){
						// SQL���s���܂����B
						errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(classId, se2.getMessage()));
						category.error("���[���o�b�N�Ɏ��s���܂���" + ErrorUtility.error2String(se2));
					}

				}finally{
					// CLOSE����
					try{
						conn.setAutoCommit(true);		// �����R�~�b�g���I����
						conn.close();
					}catch (Exception e){}
				}
			}

		}else{
			// �����f�[�^�̎擾
			setFormData(requestForm, user, errors);
		}

		return(mapping.findForward("success"));
	}

	/**
	 * �t�H�[���̃v���_�E�����e���Z�b�g����
	 * @param requestForm
	 * @param user
	 * @param errors
	 */
	private void setFormData(RequestForm requestForm, User user, ActionErrors errors){
		//�˗����e���Z�b�g����
		requestForm.iraiList = new ArrayList();//�˗����e
		requestForm.iraiList.add("");
		if(user.isReqImport()){
			requestForm.iraiList.add("�}�ʓo�^�˗�");
		}
		//�}�ʏo�͎w���@�\���폜���ꂽ���߁A���j���[�ɕ\�����Ȃ� 2018/03/28
		//if(user.isReqPrint()){
		//	requestForm.iraiList.add("�}�ʏo�͎w��");
		//}
		if(user.isReqCheckout()){
			requestForm.iraiList.add("���}�ؗp�˗�");
		}
		if(user.isReqOther()){
			requestForm.iraiList.add("�}�ʈȊO�ĕt�˗�");
		}

		//�o�͐���Z�b�g����
		requestForm.list = new ArrayList();
		for(int i = 0; i < user.getEnablePrinters().size(); i ++){
			tyk.drasap.common.Printer e = (tyk.drasap.common.Printer)user.getEnablePrinters().get(i);
			if(e.isDisplay()){
				String id = e.getId();//�v�����^ID
				String name = e.getDisplayName();//�\����
				requestForm.list.add(new RequestElement(id, name));
			}
		}

		//���}���e���Z�b�g����
		requestForm.genzuNameList = new ArrayList();
		requestForm.genzuNameList.add("");
		// ���ڂ�S�ʓI�Ɍ����� by hirata '04.Jul.19
		requestForm.genzuNameList.add("�d�l��");
		requestForm.genzuNameList.add("���얾��");
		requestForm.genzuNameList.add("���i����");
		requestForm.genzuNameList.add("���J�֌W�ꎮ");
		requestForm.genzuNameList.add("�d�C��H�}�ȊO�ꎮ");
		requestForm.genzuNameList.add("�d�C��H�}");
		requestForm.genzuNameList.add("���F�}");

		//�k���̃R���{
		requestForm.syukusyouList = new ArrayList();
		requestForm.syukusyouList.add("0");
		requestForm.syukusyouList.add("1");

		//�T�C�Y�̃R���{
		requestForm.saizuList = new ArrayList();
		requestForm.saizuList.add("");
		requestForm.saizuList.add("A1");
		requestForm.saizuList.add("A2");
		requestForm.saizuList.add("A3");
		requestForm.saizuList.add("A4");
		// �}�ʏo�͎w���ɂ�����A�T�C�Y�̒ǉ� '04.Oct.14 by Hirata
		requestForm.saizuList.add("70.7");
		requestForm.saizuList.add("50");
		requestForm.saizuList.add("35.4");
		requestForm.saizuList.add("25");
	}

/*
 * �˗����e���}�ʓo�^�˗��܂��͐}�ʏo�͎w���̏ꍇ�A�}�ʂ�W�J����B
 *
 */
	private void doSitei(RequestForm requestForm, ActionErrors errors, User user, String irai, HttpServletRequest request, String kaisiNo, String syuryoNo, String zumenAll, String gyoNo, String job_Id, Connection conn) throws Exception{

		requestForm.zumenAll = "0";//�}�ʓW�J�������ɂ��ׂēo�^�ς݂ł����1
		//�J�n�ԍ��A�I���ԍ��̂ǂ��炩�����͂���Ă���ꍇ�͕K���J�n�ԍ��ɓo�^����
		if("".equals(kaisiNo) || "".equals(syuryoNo)){
			String kai_syuNo = "";//�J�n�ԍ�
			if("".equals(syuryoNo)){
				kai_syuNo = kaisiNo;
			}else{
				kai_syuNo = syuryoNo;
			}
			boolean lenFlag = true;//�J�n�ԍ���12���Ŗ�����??�����邩�t���O�����Ă�
			if(kai_syuNo.length() == 11 || kai_syuNo.length() == 12){
				if(kai_syuNo.length() == 11){
					if("?".equals(kai_syuNo.substring(10,11))){
						kai_syuNo = kai_syuNo.substring(0,10);
					}
				}else if(kai_syuNo.length() == 12){
					if("??".equals(kai_syuNo.substring(10,12))){
						kai_syuNo = kai_syuNo.substring(0,10);
						lenFlag = false;
					}
				}

				java.sql.Statement stmt = null;//�f�[�^�[���݃`�F�b�N�p�R�l�N�V�����X�e�[�g�����g
				java.sql.ResultSet rs = null;
				String touroku = "";//�o�^�L��
				ArrayList zubanList = new ArrayList();
				int count = 0;//�ŐV�}�Ԃ����邩
				try{
					stmt = conn.createStatement();
					String strSql ="select DRWG_NO,LATEST_FLAG from INDEX_DB where DRWG_NO like '"+kai_syuNo+"%' and LATEST_FLAG = '1'";
					rs = stmt.executeQuery(strSql);

					while(rs.next()){
						String drwg_no = rs.getString("DRWG_NO");//�}��
						String flag = rs.getString("LATEST_FLAG");//�ŐV�}�Ԃ��ǂ���
						zumenAll = "1";
						requestForm.zumenAll = "1";//�}�Ԃ͓o�^�ς�
						touroku = "1";//�o�^�ς�
						count++;
						zubanList.add(new RequestResultElement(drwg_no, flag, touroku));
					}
					if(count == 0){//�ŐV�}�Ԃ��Ȃ�
						String drwg_no = "";//�}��
						if(lenFlag == false){//�}�Ԃ�??������
							drwg_no = kai_syuNo + "??";
							drwg_no = drwg_no.substring(0,12);
						}else{
							if(kai_syuNo.length() == 12){
								drwg_no = kai_syuNo;
							}else{
								drwg_no = kai_syuNo + "?";
								drwg_no = drwg_no.substring(0,11);//�}�Ԃ�?������
							}
						}
						String flag = "";//�ŐV�}�ԂłȂ�
						touroku = "";//�o�^����ĂȂ�
						zumenAll = "0";
						requestForm.zumenAll = "0";//�}�Ԃ̓o�^�͂Ȃ�
						zubanList.add(new RequestResultElement(drwg_no, flag, touroku));
					}

				}catch(java.sql.SQLException e){
					//for �V�X�e���Ǘ���
					ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
					errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(classId, e.getMessage()));
					category.error("[" + classId + "]:���}�ɍ�ƈ˗��̏����Ɏ��s���܂���\n" + ErrorUtility.error2String(e));
					throw new Exception(e.getMessage());
				}finally{
					try{// CLOSE����
						stmt.close();stmt = null;
						rs.close();rs = null;
					}catch (Exception e){}
				}

				RequestResultElement[] items = (RequestResultElement[])zubanList.toArray(new RequestResultElement[0]);
				String seq = "";//�V�[�P���X�ԍ�
				int r = 0;
				for(int i = 0; i < items.length; i++){
					String drwg_no = items[i].getDrwgNo();//�}��
					if(!"?".equals(drwg_no.substring(10,11))){//�J�n�ԍ��̐}�Ԃ��������e�[�u���ɑ��݂��邩
						doHani_s(requestForm, errors, conn, drwg_no, user);
					}
					String tourokuNo = items[i].getTouroku();//�o�^�L��
					if("1".equals(requestForm.hani_s)){//�J�n�ԍ����o�^�ς݂ł����1
						tourokuNo = "1";
						requestForm.zumenAll = "1";
					}
					//�V�[�P���X�ԍ����擾����
					if(r != 10){
						if(r == 0){
							seq = "01";
							r++;
						}else{
							seq = "0" + r;
						}
						r++;
					}else{
						seq = "10";
					}

					//���}�ɍ�ƈ˗��W�J�e�[�u���ɒǉ�����
					doInsert(conn, seq, job_Id, gyoNo, drwg_no, tourokuNo, user, errors);
					requestForm.hani_s = "";//�J�n�ԍ�
				}

			}else{
				category.debug("�ŐV�ݕ�No�̎w���11��,12���݂̂ł�");
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.hani_leng_l.check"));
				saveErrors(request, errors);
			}

		}else if(!"".equals(kaisiNo) && !"".equals(syuryoNo)){
			//�J�n�ԍ��ƏI���ԍ������͂���Ă���
			if(errors.isEmpty()){
				if(kaisiNo.substring(0,10).equals(syuryoNo.substring(0,10))){
					//�J�n�A�I���ԍ��͈͎̔w��(10���܂�OK)
					category.debug("*** �J�n�I���͈͎̔w��(10���܂�OK)");
					int kai = kaisiNo.charAt(10);//�J�n�ԍ�
					int syu = syuryoNo.charAt(10);//�I���ԍ�
					//�J�n�ԍ�<�I���ԍ��łȂ���΂Ȃ�Ȃ�
					if(kai < syu && !"?".equals(kaisiNo.substring(10,11)) && !"?".equals(syuryoNo.substring(10,11))){
						java.sql.Statement stmt = null;//�f�[�^�[���݃`�F�b�N�p�R�l�N�V�����X�e�[�g�����g
						java.sql.ResultSet rs = null;
						String hani = kaisiNo.substring(0,10);//10���܂ł̐}�ԂōŐV�}�Ԃ����邩����������ׂ�10���ɂ���
						String touroku = "";//�o�^�L��
						ArrayList zubanList = new ArrayList();//�}�Ԃ�����Ίi�[����
						try{
							stmt = conn.createStatement();
							String strSql ="select DRWG_NO,LATEST_FLAG from INDEX_DB where DRWG_NO like '"+hani+"%'";
							rs = stmt.executeQuery(strSql);

							while(rs.next()){
								String drwgNo = rs.getString("DRWG_NO");//�}��
								String flag = rs.getString("LATEST_FLAG");//�ŐV�}�Ԃ��ǂ���
								if("1".equals(flag)){
									touroku = "1";
								}
								zubanList.add(new RequestResultElement(drwgNo,flag,touroku));
							}

						}catch(java.sql.SQLException e){
							//for �V�X�e���Ǘ���
						    ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
							errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(classId, e.getMessage()));
							category.error("[" + classId + "]:���}�ɍ�ƈ˗��̏����Ɏ��s���܂���\n" + ErrorUtility.error2String(e));
							throw new Exception(e.getMessage());
						}finally{
							try{// CLOSE����
								stmt.close();stmt = null;
								rs.close();rs = null;
							}catch (Exception e){}
						}

						RequestResultElement[] items = (RequestResultElement[])zubanList.toArray(new RequestResultElement[0]);
						int r = 0;
						int countt = 0;
						char fro = kaisiNo.substring(10,11).charAt(0);//�J�n�ԍ��̖���
						char to = syuryoNo.substring(10,11).charAt(0);//�I���ԍ��̖���
						char[] oiban = DrasapUtil.createOibanArray(fro, to);//�ǔԂ��擾

						String seq = "";//�V�[�P���X�ԍ�
						for(int i = 0; i < oiban.length; i++){
							char oiban1 = oiban[i];//�ǔ�
							String oi = kaisiNo.substring(0,10) + oiban1;//�}��
							boolean flagName = true;//�}�Ԃ��o�^�ς݂Œǉ��������̃`�F�b�N
							for(int j = 0; j < items.length; j++){
								String drwg_no = items[j].getDrwgNo();
								if(oi.equals(drwg_no)){ //�����}�Ԃ��������ꍇ�ɊJ�n�A�I���͈͓̔��ł���Γo�^�L�����u1�v�ɂ���
									int drwgNo_n = drwg_no.charAt(10);
									if(kai <= drwgNo_n && drwgNo_n <= syu){ //�}�Ԃ��J�n�A�I���ԍ��͈̔͂̒��̂��̂��ǂ���
										String tourokuNo = "1";//�o�^�L��
										//�V�[�P���X�ԍ����擾����
										if(r < 10){
											if(r == 0){
												seq = "01";
												r++;
											}else{
												seq = "0" + r;
											}
											r++;
										}else{
											seq = new Integer(r).toString();
											r++;
										}

										//���}�ɍ�ƈ˗��W�J�e�[�u���ɒǉ�����
										doInsert(conn, seq, job_Id, gyoNo, drwg_no, tourokuNo, user, errors);
									}
									flagName = false;
									break;
								}
							}

							if(flagName == true){ //�o�^�̂Ȃ��}�Ԃ����}�ɍ�ƈ˗��W�J�e�[�u���ɒǉ�����
								hani = oi;//�ԍ�
								//�V�[�P���X�ԍ����擾����
								if(r < 10){
									if(r == 0){
										seq = "01";
										r++;
									}else{
										seq = "0" + r;
									}
									r++;
								}else{
									seq = new Integer(r).toString();
									r++;
								}
								String hanisitei = "";
								//���}�ɍ�ƈ˗��W�J�e�[�u���ɒǉ�����
								doNew_Insert(conn, seq, job_Id, gyoNo, hani, hanisitei, user, errors);
								countt++;
							}
						}

						if(countt == 0){
							requestForm.zumenAll = "1";//�W�J�����f�[�^���S�ēo�^�ς�
						}
					}else{
						category.debug("�K���J�n�ԍ� < �I���ԍ��Ŏw�肵�Ă�������");
						errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.hanisitei_s.required"));//�K���J�n�ԍ� < �I���ԍ��Ŏw�肵�Ă�������
						saveErrors(request, errors);
					}

				}else if(kaisiNo.substring(0,9).equals(syuryoNo.substring(0,9))){
					//�J�n�A�I���ԍ��͈͎̔w��(9���܂�OK)
					category.debug("*** �J�n�I���͈͎̔w��(9���܂�OK) *****");
					int kai = kaisiNo.charAt(9);//�J�n
					int syu = syuryoNo.charAt(9);//�I��

					if(kai < syu && !"??".equals(kaisiNo.substring(9,11)) && !"??".equals(syuryoNo.substring(9,11))){
						String hani = kaisiNo.substring(0,9);//9���܂ł̐}��

						java.sql.Statement stmt = null;//�f�[�^�[���݃`�F�b�N�p�R�l�N�V�����X�e�[�g�����g
						java.sql.ResultSet rs = null;
						String touroku = "";//�o�^�L��
						ArrayList zubanList = new ArrayList();
						int sitei = 0;
						try{
							stmt = conn.createStatement();
							String strSql ="select DRWG_NO,LATEST_FLAG from INDEX_DB where DRWG_NO like '"+hani+"%' and LATEST_FLAG = '1'";
							rs = stmt.executeQuery(strSql);

							while(rs.next()){
								String drwgNo = rs.getString("DRWG_NO");//�}��
								String flag = rs.getString("LATEST_FLAG");//�ŐV�}�Ԃ��ǂ���
								if("1".equals(flag)){
									touroku = "1";
									sitei++;
								}else{
									touroku = "";
								}
								zubanList.add(new RequestResultElement(drwgNo, flag, touroku));
							}

						}catch(java.sql.SQLException e){
							//for �V�X�e���Ǘ���
						    ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
							errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(classId, e.getMessage()));
							category.error("[" + classId + "]:���}�ɍ�ƈ˗��̏����Ɏ��s���܂���\n" + ErrorUtility.error2String(e));
							throw new Exception(e.getMessage());
						}finally{
							try{// CLOSE����
								stmt.close();stmt = null;
								rs.close();rs = null;
							}catch (Exception e){}
						}

						RequestResultElement[] items = (RequestResultElement[])zubanList.toArray(new RequestResultElement[0]);
						int r = 0;
						String seq = "";//�V�[�P���X�ԍ�
						char fro = kaisiNo.substring(9,10).charAt(0);//�J�n�ԍ��̖���
						char to = syuryoNo.substring(9,10).charAt(0);//�I���ԍ��̖���
						char[] oiban = DrasapUtil.createOibanArray(fro, to);//�ǔԂ��擾

						String hani_s = "";//�J�n�ԍ�
						String hani_e = "";//�I���ԍ�
						if(!"?".equals(kaisiNo.substring(10,11))){
							hani_s = kaisiNo;
						}
						if(!"?".equals(syuryoNo.substring(10,11))){
							hani_e = syuryoNo;
						}

						int countt = 0;
						for(int t = 0; t < oiban.length; t++){
							char oiban1 = oiban[t];//�ǔ�
							if(t == 0){
								if("".equals(hani_s)){
									//�J�n�ԍ��̖�����?
									hani = hani.substring(0,9) + oiban1;
								}else{
									//�J�n�ԍ����w��̏ꍇ
									hani = hani_s;
									doHani_s(requestForm, errors, conn, hani,user);
								}
							}else if(t == oiban.length-1){
								if("".equals(hani_e)){
									//�I���ԍ��̖�����?
									hani = hani.substring(0,9) + oiban1;
								}else{
									//�I���ԍ����w��
									hani = hani_e;
									doHani_e(requestForm, errors, conn, hani, user);
								}
							}else{
								hani = hani.substring(0,9) + oiban1;
							}

							boolean flagName = true;//�����}�Ԃ�����A�o�^�������̃`�F�b�N
							for(int k = 0; k < items.length; k++){
								String drwg_no = items[k].getDrwgNo();
								if(drwg_no.substring(0,10).equals(hani) || drwg_no.equals(hani)){
									int drwgNo_s = drwg_no.charAt(9);
									if(kai <= drwgNo_s && drwgNo_s <= syu){ //�}�Ԃ��J�n�A�I���ԍ��̒��ɂ��邩
										String tourokuNo = items[k].getTouroku();//�o�^�L��
										//�V�[�P���X�ԍ����擾����
										if(r < 10){
											if(r == 0){
												seq = "01";
												r++;
											}else{
												seq = "0" + r;
											}
											r++;
										}else{
											seq = new Integer(r).toString();
											r++;
										}
										//���}�ɍ�ƈ˗��W�J�e�[�u���ɒǉ�����
										doInsert(conn, seq, job_Id, gyoNo, drwg_no, tourokuNo,user, errors);
									}
									flagName = false;
									break;
								}
							}

							if(flagName == true){ //�o�^�ς݂̐}�ԂłȂ�
								hani = hani + "?";
								hani = hani.substring(0,11);
								String hanisitei = "";
								if("1".equals(requestForm.hani_s) || "1".equals(requestForm.hani_e)){ //�J�n�ԍ��܂��͏I���ԍ��̐}�Ԃ����݂��邩
									hanisitei = "1";//�o�^�L��
								}
								//�V�[�P���X�ԍ����擾����
								if(r < 10){
									if(r == 0){
										seq = "01";
										r++;
									}else{
										seq = "0" + r;
									}
									r++;
								}else{
									seq = new Integer(r).toString();
									r++;
								}
								//�o�^�̂Ȃ��}�Ԃ����}�ɍ�ƈ˗��W�J�e�[�u���ɒǉ�����
								doNew_Insert(conn, seq, job_Id, gyoNo, hani, hanisitei, user, errors);
								if("".equals(hanisitei)){
									countt++;
								}
							}
							requestForm.hani_s = "";//�J�n�ԍ������݂��邩�̃`�F�b�N�p
							requestForm.hani_e = "";//�I���ԍ������݂��邩�̃`�F�b�N�p
						}

						if(countt == 0){
							requestForm.zumenAll = "1";//�W�J�����}�ʂ��S�ēo�^�ς�
						}
					}else{
						category.debug("�K���J�n�ԍ� < �I���ԍ��Ŏw�肵�Ă�������");
						errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.hanisitei_s.required"));//�K���J�n�ԍ� < �I���ԍ��Ŏw�肵�Ă�������
						saveErrors(request, errors);
					}
				}else{
					category.debug("�}�Ԃ̐擪9���������łȂ��ꍇ�͔͈͎w��͂ł��Ȃ�");
					errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.hanisitei.required"));//�}�Ԃ̐擪9���������łȂ��ꍇ�͔͈͎w��͂ł��Ȃ�
					saveErrors(request, errors);
				}
			}
		}
	}


	private void doHani_s(RequestForm requestForm, ActionErrors errors, Connection conn, String hani, User user) throws Exception{
		//�J�n�ԍ��̐}�Ԃ��������e�[�u���ɑ��݂��邩
		java.sql.Statement stmt = null;//�f�[�^�[���݃`�F�b�N�p�R�l�N�V�����X�e�[�g�����g
		java.sql.ResultSet rs = null;
		requestForm.hani_s = "";//�J�n�ԍ���

		try{
			stmt = conn.createStatement();
			String strSql ="select DRWG_NO,LATEST_FLAG from INDEX_DB where DRWG_NO = '"+hani+"'";

			rs = stmt.executeQuery(strSql);
			if(rs.next()){
//				String drwgNo = rs.getString("DRWG_NO");//�}��
	//			String flag = rs.getString("LATEST_FLAG");//�ŐV�}�Ԃ��ǂ���
				requestForm.hani_s = "1";
			}

		}catch(java.sql.SQLException e){
			ErrorLoger.error(user, this,DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(classId, e.getMessage()));
			category.error("[" + classId + "]:���}�ɍ�ƈ˗��̏����Ɏ��s���܂���\n" + ErrorUtility.error2String(e));
			throw new Exception(e.getMessage());
		}finally{
			try{// CLOSE����
				stmt.close();stmt = null;
				rs.close();rs = null;
			}catch (Exception e){}
		}
	}

	private void doHani_e(RequestForm requestForm, ActionErrors errors, Connection conn, String hani, User user) throws Exception{
		//�I���ԍ��̐}�Ԃ��������e�[�u���ɑ��݂��邩
		java.sql.Statement stmt = null;//�f�[�^�[���݃`�F�b�N�p�R�l�N�V�����X�e�[�g�����g
		java.sql.ResultSet rs = null;
		requestForm.hani_e = "";

		try{
			stmt = conn.createStatement();
			String strSql ="select DRWG_NO,LATEST_FLAG from INDEX_DB where DRWG_NO = '"+hani+"'";

			rs = stmt.executeQuery(strSql);
			if(rs.next()){
//				String drwgNo = rs.getString("DRWG_NO");//�}��
//				String flag = rs.getString("LATEST_FLAG");//�ŐV�}�Ԃ��ǂ���
				requestForm.hani_e = "1";
			}

		}catch(java.sql.SQLException e){
			ErrorLoger.error(user, this,DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(classId, e.getMessage()));
			category.error("[" + classId + "]:���}�ɍ�ƈ˗��̏����Ɏ��s���܂���\n" + ErrorUtility.error2String(e));
			throw new Exception(e.getMessage());
		}finally{
			try{// CLOSE����
				stmt.close();stmt = null;
				rs.close();rs = null;
			}catch (Exception e){}
		}
	}

	/**
	 * 1�s�ڂ�}������
	 * @param requestForm
	 * @param errors
	 * @param user
	 * @param irai
	 * @param zumenAll
	 * @param kaisiNo
	 * @param syuryoNo
	 * @param job_Id
	 * @param day
	 * @param conn
	 * @throws Exception
	 */
	private void doInsert1(RequestForm requestForm, ActionErrors errors, User user, String irai, String zumenAll,
					String kaisiNo, String syuryoNo, String job_Id, String day, Connection conn) throws Exception{

		if("".equals(kaisiNo)){
			kaisiNo = syuryoNo;//�I���ԍ��ɋL������Ă��J�n�ԍ����Ȃ���ΊJ�n�ԍ��ɋL������
			syuryoNo = "";
		}
		String sagyo_bi = "";//��Ɠ���
		String status = "";//��Ԑ��ڃX�e�[�^�X
		if("1".equals(zumenAll)){
			//�}�ʏo�͎w���̂Ƃ��̂ݍ�ƃX�e�[�^�X=1�ƂȂ�ꍇ�́uSET�v����͂���
			if("B".equals(irai)){
				status = "SET";
			}
			sagyo_bi = day;
		}
		//�˗����e�����}�ؗp�˗��܂��͐}�ʈȊO�ĕt�˗��̏ꍇ�ō�ƃX�e�[�^�X(JOB_STAT)��0�̏ꍇ�͏�Ԑ��ڃX�e�[�^�X(TRANSIT_STAT)�ɁuSET�v���L������
		if("0".equals(zumenAll) && "C".equals(irai) || "D".equals(irai)){
			status = "SET";
		}

		if(requestForm.syutu == null){//�o�͐�
			requestForm.syutu = "";
		}
		java.sql.Statement stmt = null;//�f�[�^�[���݃`�F�b�N�p�R�l�N�V�����X�e�[�g�����g
		java.sql.ResultSet rs = null;
		try{
			stmt = conn.createStatement();
			String strSql1 ="insert into JOB_REQUEST_TABLE(JOB_ID,JOB_NAME,ROW_NO,REQUEST,GOUKI_NO,CONTENT,START_NO," +
							"END_NO,REQUEST_DATE,COPIES,STAMP_FLAG,SCALE_MODE," +
							"SCALE_SIZE,PRINTER_ID,JOB_STAT,JOB_DATE,OUTPUT_STAT,USER_ID,USER_NAME,DEPT_NAME,TRANSIT_STAT)" +
							" values('"+job_Id+"', '"+requestForm.irai+"', '1','"+irai+"', '"+requestForm.gouki1+"','"+requestForm.genzu1+"','"+kaisiNo+"','"+
							syuryoNo+"',sysdate,'"+requestForm.busuu1+"','"+requestForm.printer_flag+"','"+requestForm.syukusyou1+"','"+
							requestForm.size1+"','"+requestForm.syutu+"','"+zumenAll+"','"+sagyo_bi+"','0','"+user.getId()+"','"+user.getName()+"','"+user.getDeptName()+"','"+status+"')";
			stmt.executeUpdate(strSql1);
		}catch(java.sql.SQLException e){
			/** ���̃��\�b�h���ŁA�G���[���b�Z�[�W�̏����͕s�v
			ErrorLoger.error(user, this,DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(classId, e.getMessage()));
			category.error("[" + classId + "]:���}�ɍ�ƈ˗��̒ǉ��Ɏ��s���܂���\n" + ErrorUtility.error2String(e));
			*/
			throw e;

		}finally{
			try{ // CLOSE����
				stmt.close();stmt = null;
				rs.close();rs = null;
			}catch (Exception e){}
		}
	}
	/**
	 * 2�s�ڂ̑}��
	 * @param requestForm
	 * @param errors
	 * @param user
	 * @param irai
	 * @param zumenAll
	 * @param kaisiNo
	 * @param syuryoNo
	 * @param job_Id
	 * @param day
	 * @param conn
	 * @throws Exception
	 */
	private void doInsert2(RequestForm requestForm, ActionErrors errors, User user, String irai, String zumenAll, String kaisiNo, String syuryoNo, String job_Id, String day, Connection conn) throws Exception{

		if("".equals(kaisiNo)){
			kaisiNo = syuryoNo;//�I���ԍ��ɋL������Ă��J�n�ԍ����Ȃ���ΊJ�n�ԍ��ɋL������
			syuryoNo = "";
		}
		String sagyo_bi = "";//��Ɠ���
		String status = "";//��Ԑ��ڃX�e�[�^�X
		if("1".equals(zumenAll)){
			//�}�ʏo�͎w���̂Ƃ��̂ݍ�ƃX�e�[�^�X=1�ƂȂ�ꍇ�́uSET�v����͂���
			if("B".equals(irai)){
				status = "SET";
			}
			sagyo_bi = day;
		}
		//�˗����e�����}�ؗp�˗��܂��͐}�ʈȊO�ĕt�˗��̏ꍇ�ō�ƃX�e�[�^�X(JOB_STAT)��0�̏ꍇ�͏�Ԑ��ڃX�e�[�^�X(TRANSIT_STAT)�ɁuSET�v���L������
		if("0".equals(zumenAll) && "C".equals(irai) || "D".equals(irai)){
			status = "SET";
	  	}
		if(requestForm.syutu == null){//�o�͐�
			requestForm.syutu = "";
		}
		java.sql.Statement stmt2 = null;//�f�[�^�[���݃`�F�b�N�p�R�l�N�V�����X�e�[�g�����g
		java.sql.ResultSet rs2 = null;
		try{
			stmt2 = conn.createStatement();
			String strSql2 = "insert into JOB_REQUEST_TABLE(JOB_ID,JOB_NAME,ROW_NO,REQUEST,GOUKI_NO," +
							"CONTENT,START_NO,END_NO,REQUEST_DATE,COPIES," +
							"STAMP_FLAG,SCALE_MODE,SCALE_SIZE,PRINTER_ID,JOB_STAT,JOB_DATE,"+
							"OUTPUT_STAT,USER_ID,USER_NAME,DEPT_NAME,TRANSIT_STAT)" +
							" values('"+job_Id+"', '"+requestForm.irai+"', '2','"+irai+"', '"+requestForm.gouki2+"','"+
							requestForm.genzu2+"','"+kaisiNo+"','"+syuryoNo+"',sysdate,'"+requestForm.busuu2+"','"+
							requestForm.printer_flag+"','"+requestForm.syukusyou2+"','"+requestForm.size2+"','"+requestForm.syutu+"','"+
							zumenAll+"','"+sagyo_bi+"','0','"+user.getId()+"','"+user.getName()+"','"+user.getDeptName()+"','"+status+"')";

			stmt2.executeUpdate(strSql2);
		}catch(java.sql.SQLException e){
			/** ���̃��\�b�h���ŁA�G���[���b�Z�[�W�̏����͕s�v
			ErrorLoger.error(user, this,DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(classId, e.getMessage()));
			category.error("[" + classId + "]:���}�ɍ�ƈ˗��̒ǉ��Ɏ��s���܂���\n" + ErrorUtility.error2String(e));
			*/
			throw e;

		}finally{
			try{ // CLOSE����
				stmt2.close();stmt2 = null;
				rs2.close();rs2 = null;
			}catch (Exception e){}
		}
	}
	/**
	 * 3�s�ڂ̑}��
	 * @param requestForm
	 * @param errors
	 * @param user
	 * @param irai
	 * @param zumenAll
	 * @param kaisiNo
	 * @param syuryoNo
	 * @param job_Id
	 * @param day
	 * @param conn
	 * @throws Exception
	 */
	private void doInsert3(RequestForm requestForm, ActionErrors errors, User user, String irai, String zumenAll, String kaisiNo, String syuryoNo, String job_Id, String day, Connection conn) throws Exception{

		if("".equals(kaisiNo)){
			kaisiNo = syuryoNo;//�I���ԍ��ɋL������Ă��J�n�ԍ����Ȃ���ΊJ�n�ԍ��ɋL������
			syuryoNo = "";
		}
		String sagyo_bi = "";//��Ɠ���
		String status = "";//��Ԑ��ڃX�e�[�^�X
		if("1".equals(zumenAll)){
			//�}�ʏo�͎w���̂Ƃ��̂ݍ�ƃX�e�[�^�X=1�ƂȂ�ꍇ�́uSET�v����͂���
			if("B".equals(irai)){
				status = "SET";
			}
			sagyo_bi = day;
		}
		//�˗����e�����}�ؗp�˗��܂��͐}�ʈȊO�ĕt�˗��̏ꍇ�ō�ƃX�e�[�^�X(JOB_STAT)��0�̏ꍇ�͏�Ԑ��ڃX�e�[�^�X(TRANSIT_STAT)�ɁuSET�v���L������
		if("0".equals(zumenAll) && "C".equals(irai) || "D".equals(irai)){
			status = "SET";
		}
		if(requestForm.syutu == null){//�o�͐�
			requestForm.syutu = "";
		}
		java.sql.Statement stmt3 = null;//�f�[�^�[���݃`�F�b�N�p�R�l�N�V�����X�e�[�g�����g
		java.sql.ResultSet rs3 = null;
		try{
			stmt3 = conn.createStatement();
			String strSql3 = "insert into JOB_REQUEST_TABLE(JOB_ID,JOB_NAME,ROW_NO,REQUEST,GOUKI_NO," +
							"CONTENT,START_NO,END_NO,REQUEST_DATE,COPIES," +
							"STAMP_FLAG,SCALE_MODE,SCALE_SIZE,PRINTER_ID,JOB_STAT,JOB_DATE,"+
							"OUTPUT_STAT,USER_ID,USER_NAME,DEPT_NAME,TRANSIT_STAT)" +
							" values('"+job_Id+"', '"+requestForm.irai+"', '3','"+irai+"', '"+requestForm.gouki3+"','"+
							requestForm.genzu3+"','"+kaisiNo+"','"+syuryoNo+"',sysdate,'"+requestForm.busuu3+"','"+
							requestForm.printer_flag+"','"+requestForm.syukusyou3+"','"+requestForm.size3+"','"+requestForm.syutu+"','"+
							zumenAll+"','"+sagyo_bi+"','0','"+user.getId()+"','"+user.getName()+"','"+user.getDeptName()+"','"+status+"')";

			stmt3.executeUpdate(strSql3);
		}catch(java.sql.SQLException e){
			/** ���̃��\�b�h���ŁA�G���[���b�Z�[�W�̏����͕s�v
			ErrorLoger.error(user, this,DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(classId, e.getMessage()));
			category.error("[" + classId + "]:���}�ɍ�ƈ˗��̒ǉ��Ɏ��s���܂���\n" + ErrorUtility.error2String(e));
			*/
			throw e;

		}finally{
			try{// CLOSE����
				stmt3.close();stmt3 = null;
				rs3.close();rs3 = null;
			}catch (Exception e){}
		}
	}
	/**
	 * 4�s�ڂ̑}��
	 * @param requestForm
	 * @param errors
	 * @param user
	 * @param irai
	 * @param zumenAll
	 * @param kaisiNo
	 * @param syuryoNo
	 * @param job_Id
	 * @param day
	 * @param conn
	 * @throws Exception
	 */
	private void doInsert4(RequestForm requestForm, ActionErrors errors, User user, String irai, String zumenAll, String kaisiNo, String syuryoNo, String job_Id, String day, Connection conn) throws Exception{

		if("".equals(kaisiNo)){
			kaisiNo = syuryoNo;//�I���ԍ��ɋL������Ă��J�n�ԍ����Ȃ���ΊJ�n�ԍ��ɋL������
			syuryoNo = "";
		}
		String sagyo_bi = "";//��Ɠ���
		String status = "";//��Ԑ��ڃX�e�[�^�X
		if("1".equals(zumenAll)){
			//�}�ʏo�͎w���̂Ƃ��̂ݍ�ƃX�e�[�^�X=1�ƂȂ�ꍇ�́uSET�v����͂���
			if("B".equals(irai)){
				status = "SET";
			}
			sagyo_bi = day;
		}
		//�˗����e�����}�ؗp�˗��܂��͐}�ʈȊO�ĕt�˗��̏ꍇ�ō�ƃX�e�[�^�X(JOB_STAT)��0�̏ꍇ�͏�Ԑ��ڃX�e�[�^�X(TRANSIT_STAT)�ɁuSET�v���L������
		if("0".equals(zumenAll) && "C".equals(irai) || "D".equals(irai)){
			status = "SET";
		}
		if(requestForm.syutu == null){//�o�͐�
			requestForm.syutu = "";
		}
		java.sql.Statement stmt4 = null;//�f�[�^�[���݃`�F�b�N�p�R�l�N�V�����X�e�[�g�����g
		java.sql.ResultSet rs4 = null;
		try{
			stmt4 = conn.createStatement();
			String strSql4 = "insert into JOB_REQUEST_TABLE(JOB_ID,JOB_NAME,ROW_NO,REQUEST,GOUKI_NO," +
							"CONTENT,START_NO,END_NO,REQUEST_DATE,COPIES," +
							"STAMP_FLAG,SCALE_MODE,SCALE_SIZE,PRINTER_ID,JOB_STAT,JOB_DATE,"+
							"OUTPUT_STAT,USER_ID,USER_NAME,DEPT_NAME,TRANSIT_STAT)" +
							" values('"+job_Id+"', '"+requestForm.irai+"', '4','"+irai+"', '"+requestForm.gouki4+"','"+
							requestForm.genzu4+"','"+kaisiNo+"','"+syuryoNo+"',sysdate,'"+requestForm.busuu4+"','"+
							requestForm.printer_flag+"','"+requestForm.syukusyou4+"','"+requestForm.size4+"','"+requestForm.syutu+"','"+
							zumenAll+"','"+sagyo_bi+"','0','"+user.getId()+"','"+user.getName()+"','"+user.getDeptName()+"','"+status+"')";

			stmt4.executeUpdate(strSql4);
		}catch(java.sql.SQLException e){
			/** ���̃��\�b�h���ŁA�G���[���b�Z�[�W�̏����͕s�v
			ErrorLoger.error(user, this,DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(classId, e.getMessage()));
			category.error("[" + classId + "]:���}�ɍ�ƈ˗��̒ǉ��Ɏ��s���܂���\n" + ErrorUtility.error2String(e));
			*/
			throw e;

		}finally{
			try{// CLOSE����
				stmt4.close();stmt4 = null;
				rs4.close();rs4 = null;
			}catch (Exception e){}
		}
	}
	/**
	 * 5�s�ڂ̑}��
	 * @param requestForm
	 * @param errors
	 * @param user
	 * @param irai
	 * @param zumenAll
	 * @param kaisiNo
	 * @param syuryoNo
	 * @param job_Id
	 * @param day
	 * @param conn
	 * @throws Exception
	 */
	private void doInsert5(RequestForm requestForm, ActionErrors errors, User user, String irai, String zumenAll, String kaisiNo, String syuryoNo, String job_Id, String day, Connection conn) throws Exception{

		if("".equals(kaisiNo)){
			kaisiNo = syuryoNo;//�I���ԍ��ɋL������Ă��J�n�ԍ����Ȃ���ΊJ�n�ԍ��ɋL������
			syuryoNo = "";
		}
		String sagyo_bi = "";//��Ɠ���
		String status = "";//��Ԑ��ڃX�e�[�^�X
		if("1".equals(zumenAll)){
			//�}�ʏo�͎w���̂Ƃ��̂ݍ�ƃX�e�[�^�X=1�ƂȂ�ꍇ�́uSET�v����͂���
			if("B".equals(irai)){
				status = "SET";
			}
			sagyo_bi = day;
		}
		//�˗����e�����}�ؗp�˗��܂��͐}�ʈȊO�ĕt�˗��̏ꍇ�ō�ƃX�e�[�^�X(JOB_STAT)��0�̏ꍇ�͏�Ԑ��ڃX�e�[�^�X(TRANSIT_STAT)�ɁuSET�v���L������
		if("0".equals(zumenAll) && "C".equals(irai) || "D".equals(irai)){
			status = "SET";
		}
		if(requestForm.syutu == null){//�o�͐�
			requestForm.syutu = "";
		}
		java.sql.Statement stmt5 = null;//�f�[�^�[���݃`�F�b�N�p�R�l�N�V�����X�e�[�g�����g
		java.sql.ResultSet rs5 = null;
		try{
			stmt5 = conn.createStatement();
			String strSql5 = "insert into JOB_REQUEST_TABLE(JOB_ID,JOB_NAME,ROW_NO,REQUEST,GOUKI_NO," +
							"CONTENT,START_NO,END_NO,REQUEST_DATE,COPIES," +
							"STAMP_FLAG,SCALE_MODE,SCALE_SIZE,PRINTER_ID,JOB_STAT,JOB_DATE,"+
							"OUTPUT_STAT,USER_ID,USER_NAME,DEPT_NAME,TRANSIT_STAT)" +
							" values('"+job_Id+"', '"+requestForm.irai+"', '5','"+irai+"', '"+requestForm.gouki5+"','"+
							requestForm.genzu5+"','"+kaisiNo+"','"+syuryoNo+"',sysdate,'"+requestForm.busuu5+"','"+
							requestForm.printer_flag+"','"+requestForm.syukusyou5+"','"+requestForm.size5+"','"+requestForm.syutu+"','"+
							zumenAll+"','"+sagyo_bi+"','0','"+user.getId()+"','"+user.getName()+"','"+user.getDeptName()+"','"+status+"')";

			stmt5.executeUpdate(strSql5);
		}catch(java.sql.SQLException e){
			/** ���̃��\�b�h���ŁA�G���[���b�Z�[�W�̏����͕s�v
			ErrorLoger.error(user, this,DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(classId, e.getMessage()));
			category.error("[" + classId + "]:���}�ɍ�ƈ˗��̒ǉ��Ɏ��s���܂���\n" + ErrorUtility.error2String(e));
			*/
			throw e;

		}finally{
			try{ // CLOSE����
				stmt5.close();stmt5 = null;
				rs5.close();rs5 = null;
			}catch (Exception e){}
		}
	}

	/*
	 * ���}�ɍ�ƈ˗��W�J�e�[�u���ɒǉ�������
	 */
	private void doInsert(java.sql.Connection conn, String seq, String job_Id, String gyoNo, String drwg_no, String tourokuNo,User user, ActionErrors errors)throws Exception{
		java.sql.Statement stmt = null;//�f�[�^�[���݃`�F�b�N�p�R�l�N�V�����X�e�[�g�����g
		try{
			stmt = conn.createStatement();
			String strSql ="insert into JOB_REQUEST_EXPAND_TABLE(SEQ_NO,JOB_ID,ROW_NO,EXPAND_DRWG_NO,EXIST)" +
							" values('"+seq+"','"+job_Id+"','"+gyoNo+"','"+drwg_no+"', '"+tourokuNo+"')";

			stmt.executeUpdate(strSql);
		}catch(java.sql.SQLException e){
			//for �V�X�e���Ǘ���
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(classId, e.getMessage()));
			category.error("[" + classId + "]:���}�ɍ�ƈ˗��̒ǉ��Ɏ��s���܂���\n" + ErrorUtility.error2String(e));
		}finally{
			try{// CLOSE����
				stmt.close();stmt = null;
			}catch (Exception e){}
		}

	}

	/*
	 *
	 * �o�^�̂Ȃ��}�Ԃ����}�ɍ�ƈ˗��W�J�e�[�u���ɒǉ�����
	*/
	private void doNew_Insert(java.sql.Connection conn, String seq, String job_Id, String gyoNo, String hani, String hanisitei, User user, ActionErrors errors)throws Exception{
		java.sql.Statement stmt = null;//�f�[�^�[���݃`�F�b�N�p�R�l�N�V�����X�e�[�g�����g
		try{
			stmt = conn.createStatement();
			String strSql ="insert into JOB_REQUEST_EXPAND_TABLE(SEQ_NO,JOB_ID,ROW_NO,EXPAND_DRWG_NO,EXIST)" +
							" values('"+seq+"','"+job_Id+"','"+gyoNo+"','"+hani+"', '"+hanisitei+"')";

			stmt.executeUpdate(strSql);
		}catch(java.sql.SQLException e){
			//for �V�X�e���Ǘ���
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(classId, e.getMessage()));
			category.error("[" + classId + "]:���}�ɍ�ƈ˗��W�J�e�[�u���̒ǉ��Ɏ��s���܂���\n" + ErrorUtility.error2String(e));
			throw new Exception(e.getMessage());
		}finally{
			try{// CLOSE����
				stmt.close();stmt = null;
			}catch (Exception e){}
		}
	}

	/**
	 * �f�[�^�o�^�̃`�F�b�N������
	 */
	private ActionErrors CheckData(RequestForm requestForm, ActionForm form, ActionErrors errors, HttpServletRequest request){
		HashSet usedErrMsgSet = new HashSet();// �����G���[�𕡐��s�\�������Ȃ�����
		requestForm.errlog = "";//�J�n�܂��͏I���ԍ��ł̃G���[�p
		requestForm.errNumber = "";//�J�n�ƏI���ԍ��̃G���[�p
		requestForm.hani_t = "";//�J�n�ƏI���ԍ��ł̃G���[�p
		requestForm.hani_sitei = "";//�͈͎w��
//		String gyoNo = "";//�s�ԍ�
		//�o�͐���w�肵���ꍇ��hidden�Ńf�[�^������
		if(requestForm.syutu != null || !"".equals(requestForm.syutu)){
			requestForm.hiddenSyutu = requestForm.syutu;
		}
		// �t�H�[���ɓ��͂��ꂽ�������A�S�p���甼�p�ցA�啶������������
		// �܂��}�Ԃɂ��ẮA�n�C�t�������ɐ��`����B
		requestForm.formatInpuedData();

		// ���͂���Ă���s�ɂ��āA�G���[�`�F�b�N����B
		RequestDataChecker checker = null;
		if("�}�ʓo�^�˗�".equals(requestForm.irai)){
			checker = new RequestDataCheckerA();
		} else if("�}�ʏo�͎w��".equals(requestForm.irai)){
			checker = new RequestDataCheckerB();
		} else if("���}�ؗp�˗�".equals(requestForm.irai)){
			checker = new RequestDataCheckerC();
		} else if("�}�ʈȊO�ĕt�˗�".equals(requestForm.irai)){
			checker = new RequestDataCheckerD();
		}
		requestForm.hiddenNo1 = "";
		requestForm.hiddenNo2 = "";
		requestForm.hiddenNo3 = "";
		requestForm.hiddenNo4 = "";
		requestForm.hiddenNo5 = "";
		if(requestForm.isInputedLine1()){
			// 1�s�ڂɓ��͂������
			checkLineData(requestForm.getLineData1(), requestForm, errors,
					usedErrMsgSet, checker);
			requestForm.hiddenNo1 = "on";
		}
		if(requestForm.isInputedLine2()){
			// 2�s�ڂɓ��͂������
			checkLineData(requestForm.getLineData2(), requestForm, errors,
					usedErrMsgSet, checker);
			requestForm.hiddenNo2 = "on";
		}
		if(requestForm.isInputedLine3()){
			// 3�s�ڂɓ��͂������
			checkLineData(requestForm.getLineData3(), requestForm, errors,
					usedErrMsgSet, checker);
			requestForm.hiddenNo3 = "on";
		}
		if(requestForm.isInputedLine4()){
			// 4�s�ڂɓ��͂������
			checkLineData(requestForm.getLineData4(), requestForm, errors,
					usedErrMsgSet, checker);
			requestForm.hiddenNo4 = "on";
		}
		if(requestForm.isInputedLine5()){
			// 5�s�ڂɓ��͂������
			checkLineData(requestForm.getLineData5(), requestForm, errors,
					usedErrMsgSet, checker);
			requestForm.hiddenNo5 = "on";
		}

		// 2019.10.23 yamamoto add.
		// ���ׂĂ̍��ڂ������͂̏ꍇ�̓G���[�Ƃ���
		if(StringUtils.isEmpty(requestForm.hiddenNo1)
			&& StringUtils.isEmpty(requestForm.hiddenNo2)
			&& StringUtils.isEmpty(requestForm.hiddenNo3)
			&& StringUtils.isEmpty(requestForm.hiddenNo4)
			&& StringUtils.isEmpty(requestForm.hiddenNo5))
		{
			errors.add(ActionMessages.GLOBAL_MESSAGE,
					new ActionMessage("error.zuban.required" ));
		}

			//�G���[������΃G���[���i�[����
		if(!errors.isEmpty()){
			saveErrors(request, errors);
		}
		return errors;

	}

	/**
	 * ������𐮐��ɕϊ�����B�ϊ��ł��Ȃ����-1��Ԃ�
	 */
	public static int convertInt(String str) {
		int number = 0;
		try{
			// �܂������ɕϊ�����
			number = Integer.valueOf(str).intValue();
			return number;
		} catch (NumberFormatException ne) {
			// �����Ƃ��ĉ��߂ł��Ȃ�
			return -1;
		}
	}

	/**
	 * �J�n�A�I���ԍ��̂ǂ��炩�����͂���Ă���Ƃ��̃`�F�b�N
	 * 1) �}�ʓo�^�˗��A�}�ʏo�͎w���Ȃ�11�P�^�A�܂���12�P�^��?
	 */
	private ActionErrors checkData(RequestForm requestForm, ActionErrors errors, String kaisiNo){
		if("".equals(requestForm.errlog)){
			// �܂����̃G���[�`�F�b�N�ɂ�郁�b�Z�[�W���Ȃ����
			if("�}�ʓo�^�˗�".equals(requestForm.irai) || "�}�ʏo�͎w��".equals(requestForm.irai)){
				if(kaisiNo.length() < 11  || kaisiNo.length() >12){
					errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.allNo.check"));
					requestForm.errlog = "1";
				}
			} else {
				// ����ȊO�̏ꍇ�A20�P�^�ɂ���K�v�����邪�A
				// ���̐����jsp���ōs���B
			}
		}

		return errors;
	}

	/**
	 * �J�n�ԍ��ƏI���ԍ����������͂���Ă���Ƃ��̃`�F�b�N
	 * 1) 11�P�^�̂ݔ͈͎w��ł���
	 * 2) �J�n�ԍ� < �I���ԍ��ł��邩?
	 * 3) ���}�ؗp�˗��A�}�ʈȊO�ĕt�˗��ł͔͈͎w��ł��Ȃ�
	 */
	private ActionErrors checkNumber(RequestForm requestForm, ActionErrors errors, String kaisiNo, String syuNo){
		if(kaisiNo.length() != 11 && "".equals(requestForm.hani_t) || syuNo.length() != 11 && "".equals(requestForm.hani_t)){
			category.debug("�͈͎w���11���݂̂ł�");
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.hani_leng_e.check"));
		} else {
			// �J�n�A�I���Ƃ�11�P�^�̂Ƃ��̂݃`�F�b�N����B
			// '04.Aug.3�ύX by Hirata
			if("".equals(requestForm.errNumber)){
				//�J�n�ԍ�<�I���ԍ��łȂ���΃G���[��\��
				if(kaisiNo.substring(0,10).equals(syuNo.substring(0,10))){
					int kai = kaisiNo.charAt(10);//�J�n�ԍ�
					int syu = syuNo.charAt(10);//�I���ԍ�
					if(kai > syu && "".equals(requestForm.errNumber)){
						errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.hanisitei_s.required"));
						category.debug("�J�n�A�I���ɃG���[����");
						requestForm.errNumber = "1";
					}
				}else if(kaisiNo.substring(0,9).equals(syuNo.substring(0,9))){
					int kai = kaisiNo.charAt(9);//�J�n�ԍ�
					int syu = syuNo.charAt(9);//�I���ԍ�
					if(kai > syu && "".equals(requestForm.errNumber)){
						errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.hanisitei_s.required"));
						category.debug("�J�n�A�I���ɃG���[����");
						requestForm.errNumber = "1";
					}
				}
			}
		}

		//�˗����e�����}�ؗp�˗��܂��͐}�ʈȊO�ĕt�˗��̏ꍇ�͔͈͎w��͂ł��Ȃ�
		if("���}�ؗp�˗�".equals(requestForm.irai) || "�}�ʈȊO�ĕt�˗�".equals(requestForm.irai)){
			if("".equals(requestForm.hani_sitei)){
				category.debug("�}�ʓo�^�˗��A�}�ʏo�͎w���ȊO�͔͈͎w��ł��Ȃ�");
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.hani_igai.required"));
				requestForm.hani_sitei = "1";
			}
		}

		return errors;
	}
	/**
	 * �w�肵���}�Ԃ��A�֎~���ꂽ�������g�p���Ă��邩�`�F�b�N����B
	 * @param drwgNo
	 * @return �֎~���ꂽ�������g�p���Ă���� true
	 */
	/* '04.Aug.16 ���̃`�F�b�N�͕s�v
	private boolean hasProhibitedChar(String drwgNo){
		return (drwgNo.indexOf("I") != -1 || drwgNo.indexOf("O") != -1 ||
						drwgNo.indexOf("Q") != -1 || drwgNo.indexOf("X") != -1);
	}*/

	/**
	 * 1�s���ɂ��Ẵ`�F�b�N���s��
	 * @param lineData
	 * @param requestForm
	 * @param errors
	 * @param flag
	 * @param kaisyuFlag
	 * @param goukiFlag
	 * @param lenghFlag
	 * @param iraiFlag
	 * @param zumenFlag
	 * @param busuuFlag
	 * @param sizeFlag
	 * @param suryoFlag
	 */
	private void checkLineData(RequestFormLineData lineData, RequestForm requestForm, ActionErrors errors,
					HashSet usedErrMsgSet, RequestDataChecker checker){
		//�J�n,�I���ԍ��̃`�F�b�N
		if(!"".equals(lineData.getKaisiNo()) && !"".equals(lineData.getSyuuryouNo())){
			checkNumber(requestForm, errors, lineData.getKaisiNo(), lineData.getSyuuryouNo());
		}else if(!"".equals(lineData.getKaisiNo())){
			checkData(requestForm, errors, lineData.getKaisiNo());
		}else if(!"".equals(lineData.getSyuuryouNo())){
			checkData(requestForm, errors, lineData.getSyuuryouNo());
		}

		// �w�肵���}�Ԃ��A�֎~���ꂽ�������g�p���Ă��邩�`�F�b�N����B
		/* '04.Aug.16 �֎~������ύX�����B
		if(hasProhibitedChar(lineData.getKaisiNo()) || hasProhibitedChar(lineData.getSyuuryouNo())){
			if(! usedErrMsgSet.contains("error.hani_sitei.check")){
				category.debug("�i�Ԃ�[�h]�E[�n]�E[�p]�E[�w]�̕������g�p�ł��܂���");
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.hani_sitei.check"));
				usedErrMsgSet.add("error.hani_sitei.check");
			}
		}*/

		// RequestDataChecker���g�p���āA�`�F�b�N����B
		// Startegy�p�^�[���𗘗p����B
		checker.checkLineData(lineData, requestForm, errors,
								usedErrMsgSet, category);
	}

}
