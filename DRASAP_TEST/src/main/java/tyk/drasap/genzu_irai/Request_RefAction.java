package tyk.drasap.genzu_irai;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import tyk.drasap.common.DrasapPropertiesFactory;
import tyk.drasap.common.DrasapUtil;
import tyk.drasap.common.ErrorUtility;
import tyk.drasap.common.MessageManager;
import tyk.drasap.common.User;
import tyk.drasap.errlog.ErrorLoger;
import tyk.drasap.springfw.action.BaseAction;

/**
 * @author KAWAI
 * ���}�ɍ�ƈ˗��ڍ�
 */
@Controller
public class Request_RefAction extends BaseAction {
	// --------------------------------------------------------- Instance Variables
	private String classId = "";

	// --------------------------------------------------------- Methods
	/**
	 * Method execute
	 * @param form
	 * @param request
	 * @param response
	 * @param errors
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/req_ref")
	public String execute(
			Request_RefForm form,
			HttpServletRequest request,
			HttpServletResponse response,
			Model errors)
			throws Exception {

		Request_RefForm request_refForm = form;
		category.debug("Request_RefAction �X�^�[�g");
		request_refForm.listErrors = new ArrayList<String>();//�G���[��\��
		//		ActionErrors errors = new ActionErrors();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		if (user == null) {
			return "timeout";
		}
		try {
			// �N���XID�擾
			classId = DrasapPropertiesFactory.getDrasapProperties(this).getProperty("tyk.drasap.genzu_irai.Request_RefAction");
		} catch (Exception e) {
			category.error("�v���p�e�B�t�@�C���̓Ǎ��݂Ɏ��s���܂���\n" + ErrorUtility.error2String(e));
		}

		java.sql.Connection conn = null;
		java.sql.Statement stmt = null;//�f�[�^�[���݃`�F�b�N�p�R�l�N�V�����X�e�[�g�����g
		java.sql.ResultSet rs = null;
		category.debug("action = " + request_refForm.action);
		if ("".equals(request_refForm.action) || request_refForm.action == null) {
			//���}�ɍ�ƈ˗��ڍ׉�ʂ�\������
			conn = ds.getConnection();
			conn.setAutoCommit(true);// ��g�����U�N�V����
			//�f�[�^���擾����
			doSyokika(user, request_refForm, conn);

			ArrayList<Request_RefElement> iraiList = new ArrayList<Request_RefElement>();//��ʕ\�����e���i�[����
			//��ƒ��ŕ\��������̂Ɋւ���iraiList�Ɋi�[����
			Request_RefElement[] items = request_refForm.irai_List.toArray(new Request_RefElement[0]);
			for (int i = 0; i < items.length; i++) {
				String job_id = items[i].getJob_id();//�W���uID
				String job_stat = items[i].getJob_stat();//��ƃX�e�[�^�X
				String job_name = items[i].getJob_name();//�˗����e
				String gouki = items[i].getGouki();//�����E���@
				String genzu = items[i].getGenzu();//���}���e
				String start = items[i].getStart();//�J�n�ԍ�
				if (start == null) {
					start = "";
				}
				String end = items[i].getEnd();//�I���ԍ�
				if (end == null) {
					end = "";
				}
				String busuu = items[i].getBusuu();//����
				String syuku = items[i].getSyuku();//�k��
				String size = items[i].getSize();//�T�C�Y
				String printer = items[i].getPrinter();//�o�͐�
				String messege = items[i].getMessege();//���b�Z�[�W(�}�ʓo�^�˗��A�}�ʏo�͎w��)
				if (messege == null) {
					messege = "";
				}
				String messege1 = items[i].getMessege1();//���b�Z�[�W(���}�ؗp�˗��A�}�ʈȊO�ĕt�˗�)
				String exist = items[i].getExist();//�o�^�L��
				if (exist == null) {
					exist = "";
				}
				String seq = items[i].getSeq();//�V�[�P���X�ԍ�
				String rowNo = items[i].getRowNo();//�s�ԍ�
				String startNo = "";//1�O�̊J�n�ԍ����`�F�b�N
				String endNo = "";//1�O�̏I���ԍ����`�F�b�N
				if (i > 0) {
					startNo = items[i - 1].getStart();
					if (startNo == null) {
						startNo = "";
					}
					endNo = items[i - 1].getEnd();
					if (endNo == null) {
						endNo = "";
					}
				}
				if (i == 0 || "�}�ʓo�^�˗�".equals(job_name) && !start.equals(startNo) && !end.equals(endNo) && !rowNo.equals(items[i - 1].getRowNo()) || "�}�ʏo�͎w��".equals(job_name) && !start.equals(startNo) && !end.equals(endNo) && !rowNo.equals(items[i - 1].getRowNo())
						|| "�}�ʓo�^�˗�".equals(job_name) && !job_id.equals(items[i - 1].getJob_id()) && start.equals(startNo) && end.equals(endNo) && rowNo.equals(items[i - 1].getRowNo()) || "�}�ʏo�͎w��".equals(job_name) && !job_id.equals(items[i - 1].getJob_id()) && start.equals(startNo) && end.equals(endNo) && rowNo.equals(items[i - 1].getRowNo())
						|| "�}�ʓo�^�˗�".equals(job_name) && !job_id.equals(items[i - 1].getJob_id()) || "�}�ʏo�͎w��".equals(job_name) && !job_id.equals(items[i - 1].getJob_id()) || "�}�ʓo�^�˗�".equals(job_name) && job_id.equals(items[i - 1].getJob_id()) && !rowNo.equals(items[i - 1].getRowNo()) || "�}�ʏo�͎w��".equals(job_name) && job_id.equals(items[i - 1].getJob_id()) && !rowNo.equals(items[i - 1].getRowNo())
						|| "���}�ؗp�˗�".equals(job_name) || "�}�ʈȊO�ĕt�˗�".equals(job_name)) {
					//�J�n�A�I���ԍ�������ꍇ�͐}�Ԃ�W�J����(�˗����e���}�ʓo�^�˗��A�}�ʏo�͈˗��̏ꍇ)
					if (!"".equals(start) && !"".equals(end)) {
						String data = "";//�o�^�ƃ��b�Z�[�W�����邩�`�F�b�N������
						try {
							stmt = conn.createStatement();
							String strSql = "select EXIST,MESSAGE from JOB_REQUEST_EXPAND_TABLE where JOB_ID = '" + job_id + "' and ROW_NO = '" + rowNo + "'";
							rs = stmt.executeQuery(strSql);
							while (rs.next()) {
								String touroku = rs.getString("EXIST");//�o�^�L��
								String message = rs.getString("MESSAGE");//���b�Z�[�W
								if ("0".equals(touroku) || message != null) {
									data = "0";
								}
							}

						} catch (java.sql.SQLException e) {
							request_refForm.listErrors.add(MessageManager.getInstance().getMessage("msg.sql.error", e.getMessage()));
							//for �V�X�e���Ǘ���
							ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
							category.error("[" + classId + "]:���}�ɍ�ƈ˗��W�J�e�[�u���̏����Ɏ��s���܂���\n" + ErrorUtility.error2String(e));
						} finally {
							try {// CLOSE����
								stmt.close();
								stmt = null;
								rs.close();
								rs = null;
							} catch (Exception e) {
							}
						}

						exist = data;//�J�n�A�I���ԍ��͈͓̔��ɓo�^�L��=0�܂��̓��b�Z�[�W������Ό��}�ɍ�ƈ˗����X�g�Ƀ��b�Z�[�W����̃����N�����Ă�
						if ("".equals(exist)) {
							messege = "";
						}
						//�o�^�L���܂��̓��b�Z�[�W�̂ǂ��炩�����邩�m�F������
					} else if ("�}�ʓo�^�˗�".equals(job_name) || "�}�ʏo�͎w��".equals(job_name)) {
						try {
							stmt = conn.createStatement();
							String strSql = "select EXIST,MESSAGE from JOB_REQUEST_EXPAND_TABLE where JOB_ID = '" + job_id + "' and EXPAND_DRWG_NO = '" + start + "' and ROW_NO = '" + rowNo + "'";
							rs = stmt.executeQuery(strSql);
							if (rs.next()) {
								String touroku = rs.getString("EXIST");//�o�^�L��
								String message = rs.getString("MESSAGE");//���b�Z�[�W
								if (touroku == null && message == null) {
									exist = "";
									messege = "";
								} else if ("1".equals(touroku) || "2".equals(touroku)) {//�����Ȃ��̕\���͓o�^�L����0�̎�����
									exist = "";
								}
							}

						} catch (java.sql.SQLException e) {
							request_refForm.listErrors.add(MessageManager.getInstance().getMessage("msg.sql.error", e.getMessage()));
							//for �V�X�e���Ǘ���
							ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
							category.error("[" + classId + "]:���}�ɍ�ƈ˗��W�J�e�[�u���̏����Ɏ��s���܂���\n" + ErrorUtility.error2String(e));
						} finally {
							try {// CLOSE����
								stmt.close();
								stmt = null;
								rs.close();
								rs = null;
							} catch (Exception e) {
							}
						}
					}
					iraiList.add(new Request_RefElement(job_id, job_stat, job_name, gouki, genzu, start, end, busuu, syuku, size, printer, messege, messege1, exist, seq, rowNo));
				}
			}

			//���}�Ɉ˗��ڍׂ̉�ʂɊi�[�����f�[�^��\������
			request_refForm.iraiList = iraiList;
			if (conn != null) {
				conn.close();
			}
			if (request_refForm.iraiList.isEmpty()) {
				//�˗����Ă���f�[�^�͂Ȃ�
				request_refForm.listErrors.add(MessageManager.getInstance().getMessage("msg.irai.nodata"));
			}
		} else if ("button_Mtenkai".equals(request_refForm.action)) {
			//���b�Z�[�W����̃����N������ꍇ�ŁA
			//�}�ʓo�^�˗��A�}�ʏo�͎w���̏ꍇ�͌��}�ɍ�ƈ˗��W�J�e�[�u��(JOB_REQUEST_EXPAND_TABLE)��\������
			String strc = request_refForm.job;//�˗�ID,�˗����e,�s�ԍ��̃f�[�^���擾����
			java.util.StringTokenizer st = new java.util.StringTokenizer(strc, "_");
			ArrayList<Object> list = new ArrayList<>();
			while (st.hasMoreElements()) {
				list.add(st.nextElement());
			}
			String job_id = "";//�˗�ID
			String irai = "";//�˗����e
			String rowNo = "";//�s�ԍ�
			for (int i = 0; i < list.size(); i++) {
				if (i == list.size() - 1) {
					rowNo = list.get(i).toString();
				} else if (i == 1) {
					irai = list.get(i).toString();
				} else {
					job_id = list.get(i).toString();
				}
			}
			request_refForm.job_id = job_id;//�˗�ID
			request_refForm.job_name = irai;//�˗����e
			request_refForm.iraiList = new ArrayList<>();
			//���}�ɍ�Ǝ҂���̃��b�Z�[�W�ɕ\��������
			doTenkai(user, request_refForm, job_id, rowNo);

		} else if ("button_Msagyo".equals(request_refForm.action)) {
			//���b�Z�[�W����̃����N������ꍇ�ŁA���}�ؗp�˗��A�}�ʈȊO�ĕt�˗��̏ꍇ�̃f�[�^��\������
			String strc = request_refForm.job;//�˗�ID,�s�ԍ�,�˗����e�̃f�[�^���擾����
			java.util.StringTokenizer st = new java.util.StringTokenizer(strc, "_");
			ArrayList<Object> list = new ArrayList<>();
			while (st.hasMoreElements()) {
				list.add(st.nextElement());
			}
			String job_id = "";//�˗�ID
			String rowNo = "";//�s�ԍ�
			String irai = "";//�˗����e
			for (int i = 0; i < list.size(); i++) {
				if (i == list.size() - 1) {
					rowNo = list.get(i).toString();
				} else if (i == 1) {
					irai = list.get(i).toString();
				} else {
					job_id = list.get(i).toString();
				}
			}
			request_refForm.job_id = job_id;//�˗�ID
			request_refForm.job_name = irai;//�˗����e
			request_refForm.iraiList = new ArrayList<>();
			//���}�ɍ�Ǝ҂���̃��b�Z�[�W�ɕ\��������
			doSagyo(user, request_refForm, job_id, rowNo);

		}

		errors.addAttribute("request_refForm", request_refForm);
		if ("button_Mtenkai".equals(request_refForm.action) || "button_Msagyo".equals(request_refForm.action)) {
			//���b�Z�[�W����̃����N�������Ď��̉�ʂ�
			return "list";
		}
		//���}�Ɉ˗��ڍ׉�ʂ�
		return "success";
	}

	/*
	 * �����f�[�^���擾����
	 *
	 */
	public void doSyokika(User user, Request_RefForm request_refForm, java.sql.Connection conn) throws Exception {
		request_refForm.irai_List = new ArrayList<>();//�˗����̓��e���i�[����
		ArrayList<Request_RefElement> irai_List = new ArrayList<>();//�˗����̓��e���i�[����
		ArrayList<Request_RefElement> iraiend_List = new ArrayList<>();//�����������e���i�[����

		java.sql.Statement stmt = null;//�f�[�^�[���݃`�F�b�N�p�R�l�N�V�����X�e�[�g�����g
		java.sql.ResultSet rs = null;
		java.sql.Statement stmt1 = null;//�f�[�^�[���݃`�F�b�N�p�R�l�N�V�����X�e�[�g�����g
		java.sql.ResultSet rs1 = null;
		try {
			stmt = conn.createStatement();
			String strSql = "select JOB_REQUEST_TABLE.JOB_ID,JOB_STAT,JOB_NAME,GOUKI_NO,CONTENT,START_NO,END_NO,COPIES,SCALE_MODE," +
					"SCALE_SIZE,PRINTER_ID,JOB_REQUEST_TABLE.ROW_NO,EXIST,JOB_REQUEST_EXPAND_TABLE.MESSAGE," +
					"JOB_REQUEST_EXPAND_TABLE.SEQ_NO" +
					" from JOB_REQUEST_TABLE,JOB_REQUEST_EXPAND_TABLE" +
					" where JOB_REQUEST_TABLE.JOB_ID(+) = JOB_REQUEST_EXPAND_TABLE.JOB_ID" +
					" and JOB_REQUEST_TABLE.ROW_NO = JOB_REQUEST_EXPAND_TABLE.ROW_NO" +
					" and USER_ID = '" + user.getId() + "'" +
					" order by JOB_STAT,JOB_REQUEST_TABLE.JOB_ID,JOB_REQUEST_TABLE.ROW_NO";

			//category.debug("sql = " + strSql);
			rs = stmt.executeQuery(strSql);
			while (rs.next()) {
				String job_id = rs.getString("JOB_ID");//�˗�ID
				String job_stat = rs.getString("JOB_STAT");//���
				String job_name = rs.getString("JOB_NAME");//�˗����e
				String gouki = rs.getString("GOUKI_NO");//�����E���@
				String genzu = rs.getString("CONTENT");//���}���e
				String start = rs.getString("START_NO");//�ԍ�(�J�n)
				if (start != null) {
					//�J�n�ԍ��A�I���ԍ���11���̏ꍇ�́u-�v��t����
					start = DrasapUtil.formatDrwgNo(start);
				}
				String end = rs.getString("END_NO");//�ԍ�(�I��)
				if (end != null) {
					//�J�n�ԍ��A�I���ԍ���11���̏ꍇ�́u-�v��t����
					end = DrasapUtil.formatDrwgNo(end);
				}

				String busuu = rs.getString("COPIES");//����
				String syuku = rs.getString("SCALE_MODE");//�k��
				String size = rs.getString("SCALE_SIZE");//�T�C�Y
				String printer = rs.getString("PRINTER_ID");//�o�͐�
				String messege = rs.getString("MESSAGE");//���b�Z�[�W(�}�ʓo�^�˗��A�}�ʏo�͎w��)
				String messege1 = "";//���b�Z�[�W(���}�ؗp�˗��A�}�ʈȊO�ĕt�˗�)
				String exist = rs.getString("EXIST");//�o�^�L��
				String seq = rs.getString("SEQ_NO");//�V�[�P���X�ԍ�
				String rowNo = rs.getString("ROW_NO");//�s�ԍ�

				//�o�͐���Z�b�g����
				for (int i = 0; i < user.getEnablePrinters().size(); i++) {
					tyk.drasap.common.Printer e = user.getEnablePrinters().get(i);
					String id = e.getId();//�v�����^ID
					if (id.equals(printer)) {
						printer = e.getDisplayName();//�\����
						break;
					}
				}

				if ("�}�ʓo�^�˗�".equals(job_name) && job_id != null && "0".equals(job_stat) || "�}�ʏo�͎w��".equals(job_name) && job_id != null && "0".equals(job_stat)) {
					//��ƒ��ŉ�ʕ\��
					irai_List.add(new Request_RefElement(job_id, job_stat, job_name, gouki, genzu, start, end, busuu, syuku, size, printer, messege, messege1, exist, seq, rowNo));
				} else if ("�}�ʓo�^�˗�".equals(job_name) && job_id != null && "1".equals(job_stat) || "�}�ʓo�^�˗�".equals(job_name) && job_id != null && "2".equals(job_stat)
						|| "�}�ʏo�͎w��".equals(job_name) && job_id != null && "1".equals(job_stat) || "�}�ʏo�͎w��".equals(job_name) && job_id != null && "2".equals(job_stat)) {
					//�����ŉ�ʕ\��
					iraiend_List.add(new Request_RefElement(job_id, job_stat, job_name, gouki, genzu, start, end, busuu, syuku, size, printer, messege, messege1, exist, seq, rowNo));
				}
			}

			//���}�ؗp�˗��A�}�ʈȊO�ĕt�˗��̃f�[�^���擾����
			stmt1 = conn.createStatement();
			String strSql1 = "select JOB_ID,ROW_NO,JOB_NAME,START_NO,END_NO,GOUKI_NO,CONTENT,JOB_STAT,COPIES,SCALE_MODE," +
					"SCALE_SIZE,PRINTER_ID,MESSAGE from JOB_REQUEST_TABLE where USER_ID = '" + user.getId() + "' order by JOB_STAT, JOB_ID, ROW_NO";
			//category.debug("sql = " + strSql);
			rs1 = stmt.executeQuery(strSql1);
			while (rs1.next()) {
				String job_id = rs1.getString("JOB_ID");//�˗�ID
				String job_stat = rs1.getString("JOB_STAT");//���
				String job_name = rs1.getString("JOB_NAME");//�˗����e
				String gouki = rs1.getString("GOUKI_NO");//�����E���@
				String genzu = rs1.getString("CONTENT");//���}���e
				String start = rs1.getString("START_NO");//�ԍ�(�J�n)
				// ���}�ؗp�˗��A�}�ʈȊO�ĕt�˗��ł͐}�Ԃ𐮌`���Ȃ��B
				// '04.Jul.27�ύX by Hirata
				/*if(start != null){
				  //�J�n�ԍ��A�I���ԍ���11���̏ꍇ�́u-�v��t����
				  start = DrasapUtil.formatDrwgNo(start);
				}*/
				String end = rs1.getString("END_NO");//�ԍ�(�I��)
				// ���}�ؗp�˗��A�}�ʈȊO�ĕt�˗��ł͐}�Ԃ𐮌`���Ȃ��B
				// '04.Jul.27�ύX by Hirata
				/* if(end != null){
					  //�J�n�ԍ��A�I���ԍ���11���̏ꍇ�́u-�v��t����
					  end = DrasapUtil.formatDrwgNo(end);
				  }*/
				String busuu = rs1.getString("COPIES");//����
				String syuku = rs1.getString("SCALE_MODE");//�k��
				String size = rs1.getString("SCALE_SIZE");//�T�C�Y
				String printer = rs1.getString("PRINTER_ID");//�o�͐�
				String messege1 = rs1.getString("MESSAGE");//���b�Z�[�W(�}�ʓo�^�˗��A�}�ʏo�͎w��)
				String exist = "";//�o�^�L��
				String messege = "";//���b�Z�[�W(���}�ؗp�˗��A�}�ʈȊO�ĕt�˗�)
				String seq = "";//�V�[�P���X�ԍ�
				String rowNo = rs1.getString("ROW_NO");//�s�ԍ�

				if ("���}�ؗp�˗�".equals(job_name) && "0".equals(job_stat) || "�}�ʈȊO�ĕt�˗�".equals(job_name) && "0".equals(job_stat)) {
					//��ƒ��ŉ�ʕ\��
					irai_List.add(new Request_RefElement(job_id, job_stat, job_name, gouki, genzu, start, end, busuu, syuku, size, printer, messege, messege1, exist, seq, rowNo));
				} else if ("���}�ؗp�˗�".equals(job_name) && "1".equals(job_stat) || "���}�ؗp�˗�".equals(job_name) && "2".equals(job_stat)
						|| "�}�ʈȊO�ĕt�˗�".equals(job_name) && "1".equals(job_stat) || "�}�ʈȊO�ĕt�˗�".equals(job_name) && "2".equals(job_stat)) {
					//�����ŉ�ʕ\��
					iraiend_List.add(new Request_RefElement(job_id, job_stat, job_name, gouki, genzu, start, end, busuu, syuku, size, printer, messege, messege1, exist, seq, rowNo));
				}
			}
			//��ƒ��̃f�[�^�̌��Ɋ����������̂�ǉ�����
			request_refForm.irai_List = irai_List;
			request_refForm.irai_List.addAll(iraiend_List);
		} catch (java.sql.SQLException e) {
			request_refForm.listErrors.add(MessageManager.getInstance().getMessage("msg.sql.error", e.getMessage()));
			//for �V�X�e���Ǘ���
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			category.error("[" + classId + "]:���}�ɍ�ƈ˗��e�[�u���̏����Ɏ��s���܂���\n" + ErrorUtility.error2String(e));
		} finally {
			try {// CLOSE����
				stmt.close();
				stmt = null;
				stmt1.close();
				stmt1 = null;
				rs.close();
				rs = null;
				rs1.close();
				rs1 = null;
			} catch (Exception e) {
			}
		}

	}

	/*
	 * �˗��敪���}�ʓo�^�˗��A�}�ʏo�͎w���̂��̂̃f�[�^�����}�ɍ�Ǝ҂���̃��b�Z�[�W�ɕ\��������
	 *
	 */
	public void doTenkai(User user, Request_RefForm request_refForm, String job_id, String rowNo) throws Exception {
		ArrayList<Request_RefElement> irai_List = new ArrayList<>();
		ArrayList<Request_RefElement> iraiList = new ArrayList<>();
		java.sql.Connection conn = null;
		java.sql.Statement stmt = null;//�f�[�^�[���݃`�F�b�N�p�R�l�N�V�����X�e�[�g�����g
		java.sql.ResultSet rs = null;
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(true);// ��g�����U�N�V����
			stmt = conn.createStatement();

			String strSql = "select JOB_REQUEST_EXPAND_TABLE.SEQ_NO,JOB_REQUEST_EXPAND_TABLE.JOB_ID,JOB_NAME,EXPAND_DRWG_NO,GOUKI_NO,CONTENT,JOB_REQUEST_EXPAND_TABLE.MESSAGE,EXIST,SEQ_NO,JOB_REQUEST_EXPAND_TABLE.ROW_NO" +
					" from JOB_REQUEST_EXPAND_TABLE,JOB_REQUEST_TABLE" +
					" where JOB_REQUEST_EXPAND_TABLE.JOB_ID = JOB_REQUEST_TABLE.JOB_ID(+) and JOB_REQUEST_EXPAND_TABLE.JOB_ID = '" + job_id + "'" +
					" and JOB_REQUEST_EXPAND_TABLE.ROW_NO = '" + rowNo + "' order by JOB_REQUEST_EXPAND_TABLE.SEQ_NO";
			//category.debug("sql(button_Mtenkai) = " + strSql);
			rs = stmt.executeQuery(strSql);
			while (rs.next()) {
				String job_name = rs.getString("JOB_NAME");//�˗����e
				String start = rs.getString("EXPAND_DRWG_NO");//�ԍ�
				//�J�n�ԍ��A�I���ԍ���11���̏ꍇ�́u-�v��t����
				start = DrasapUtil.formatDrwgNo(start);
				String gouki = rs.getString("GOUKI_NO");//�����E���@
				if (gouki == null) {
					gouki = "";
				}
				String genzu = rs.getString("CONTENT");//���}���e
				if (genzu == null) {
					genzu = "";
				}
				String message = rs.getString("MESSAGE");//���b�Z�[�W
				if (message == null) {
					message = "";
				}
				String exist = rs.getString("EXIST");//�o�^�L��
				if (exist == null) {
					exist = "";
				}

				if (!"".equals(message) || "0".equals(exist)) {
					irai_List.add(new Request_RefElement(job_name, gouki, genzu, start, message, exist));
				}
			}
			Request_RefElement[] item = irai_List.toArray(new Request_RefElement[0]);
			for (int j = 0; j < item.length; j++) {
				String job_name = item[j].getJob_name();
				String gouki = item[j].getGouki();
				String genzu = item[j].getGenzu();
				String start = item[j].getStart();
				String message = item[j].getMessege();
				String exist = item[j].getExist();
				if (j == 0 || !start.equals(item[j - 1].getStart())) {
					iraiList.add(new Request_RefElement(job_name, gouki, genzu, start, message, exist));
				}

			}
			//���}�ɍ�Ǝ҂���̃��b�Z�[�W��ʂ̃f�[�^��\������
			request_refForm.iraiList = iraiList;

		} catch (java.sql.SQLException e) {
			request_refForm.listErrors.add(MessageManager.getInstance().getMessage("msg.sql.error", e.getMessage()));
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			category.error("[" + classId + "]:���}�ɍ�ƈ˗��e�[�u���̏����Ɏ��s���܂���\n" + ErrorUtility.error2String(e));
		} finally {
			try {// CLOSE����
				stmt.close();
				stmt = null;
				rs.close();
				rs = null;
				conn.close();
				conn = null;
			} catch (Exception e) {
			}
		}
	}

	/*
	 * �˗����e�����}�ؗp�˗��A�}�ʈȊO�ĕt�˗��̂��̂̃f�[�^�����}�ɍ�Ǝ҂���̃��b�Z�[�W�ɕ\��������
	 *
	 */
	public void doSagyo(User user, Request_RefForm request_refForm, String job_id, String rowNo) throws Exception {
		ArrayList<Request_RefElement> iraiList = new ArrayList<>();
		java.sql.Connection conn = null;
		java.sql.Statement stmt = null;//�f�[�^�[���݃`�F�b�N�p�R�l�N�V�����X�e�[�g�����g
		java.sql.ResultSet rs = null;
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(true);// ��g�����U�N�V����
			stmt = conn.createStatement();
			String strSql = "select JOB_ID,ROW_NO,JOB_NAME,START_NO,END_NO,GOUKI_NO,CONTENT,MESSAGE,JOB_STAT" +
					" from JOB_REQUEST_TABLE where JOB_ID = '" + job_id + "' and ROW_NO = '" + rowNo + "'";
			//category.debug("sql(button_Msagyo) = " + strSql);
			rs = stmt.executeQuery(strSql);

			if (rs.next()) {
				//				String id = rs.getString("JOB_ID");//�˗�ID
				String job_name = rs.getString("JOB_NAME");//�˗����e
				String start = rs.getString("START_NO");//�J�n�ԍ�
				if (start == null) {
					start = "";
				}
				// '04.Aug.3�o�O�C��
				// �J�n�ԍ��A�I���ԍ���11���̏ꍇ�́u-�v��t����
				// start = DrasapUtil.formatDrwgNo(start);
				//				String end_No = rs.getString("END_NO");//�I���ԍ�
				String gouki = rs.getString("GOUKI_NO");//�����E���@
				if (gouki == null) {
					gouki = "";
				}
				String genzu = rs.getString("CONTENT");//���}���e
				if (genzu == null) {
					genzu = "";
				}
				String message = rs.getString("MESSAGE");//���b�Z�[�W
				if (message == null) {
					message = "";
				}
				String exist = rs.getString("JOB_STAT");//��ƃX�e�[�^�X
				if (exist == null || "0".equals(exist)) {
					exist = "";
				}
				if ("2".equals(exist)) {
					exist = "0";
				}

				iraiList.add(new Request_RefElement(job_name, gouki, genzu, start, message, exist));
			}
			//���}�ɍ�Ǝ҂���̃��b�Z�[�W��ʂ̃f�[�^��\������
			request_refForm.iraiList = iraiList;

		} catch (java.sql.SQLException e) {
			request_refForm.listErrors.add(MessageManager.getInstance().getMessage("msg.sql.error", e.getMessage()));
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			category.error("[" + classId + "]:���}�ɍ�ƈ˗��e�[�u���̏����Ɏ��s���܂���\n" + ErrorUtility.error2String(e));
		} finally {
			try {// CLOSE����
				stmt.close();
				stmt = null;
				rs.close();
				rs = null;
				conn.close();
				conn = null;
			} catch (Exception e) {
			}
		}
	}
}
