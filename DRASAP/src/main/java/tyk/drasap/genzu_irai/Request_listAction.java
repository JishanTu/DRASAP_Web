package tyk.drasap.genzu_irai;

import java.sql.Connection;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import tyk.drasap.acslog.AccessLoger;
import tyk.drasap.common.DrasapPropertiesFactory;
import tyk.drasap.common.DrasapUtil;
import tyk.drasap.common.ErrorUtility;
import tyk.drasap.common.MessageManager;
import tyk.drasap.common.User;
import tyk.drasap.errlog.ErrorLoger;
import tyk.drasap.springfw.action.BaseAction;

/**
 * ���}�ɍ�ƈ˗����X�g��\������B
 */
@Controller
public class Request_listAction extends BaseAction {
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
	@PostMapping("/req_list")
	public String execute(
			Request_listForm form,
			HttpServletRequest request,
			HttpServletResponse response,
			Model errors)
			throws Exception {

		Request_listForm request_listForm = form;
		category.debug("Request_listAction �X�^�[�g");
		request_listForm.listErrors = new ArrayList<String>();// �G���[�̏�����
		ArrayList<Request_listElement> iraiList = new ArrayList<Request_listElement>();//���}�ɍ�ƈ˗����X�g�̃f�[�^��\������ArrayList
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		if (user == null) {
			return "timeout";
		}
		try {
			// �N���XID�擾
			classId = DrasapPropertiesFactory.getDrasapProperties(this).getProperty("tyk.drasap.genzu_irai.Request_listAction");
		} catch (Exception e) {
			category.error("�v���p�e�B�t�@�C���̓Ǎ��݂Ɏ��s���܂���\n" + ErrorUtility.error2String(e));
		}
		category.debug("�A�N�V���� = " + request_listForm.action);
		if ("".equals(request_listForm.action) || request_listForm.action == null) {
			//�����f�[�^���擾����
			request_listForm.iraiList = getData_Search(request_listForm, iraiList, user, classId);
			setSelectList(request_listForm);

		} else if ("button_update".equals(request_listForm.action)) {
			//�������o�^������
			Connection conn = null;
			try {
				conn = ds.getConnection();
				//�K���R�~�b�g���g�p���A�������x����Repeatable Read�ȏ��
				conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
				// �g�����U�N�V�����������x����Repeatable Read
				conn.setAutoCommit(false); // �����R�~�b�g���I�t��

				//���}�ɍ�ƈ˗����X�g�ɕ\������
				Request_listElement[] items = request_listForm.iraiList.toArray(new Request_listElement[0]);
				int countKey = 0;
				//				int totalCount = 0;//�S�ēo�^�ς݂��ǂ����̊m�F�p
				//				boolean flag = true;
				// �˗����X�g1�s���Ƃ̏������[�`��
				for (int i = 0; i < items.length; i++) {
					String seq = items[i].getSeq();//�V�[�P���X�ԍ�
					String id = items[i].getJob_id();//�˗�ID
					//					String zikan = items[i].getZikan();//����
					String irai = items[i].getIrai();//�˗����e
					//					String zuban = items[i].getZuban();//�ԍ�
					//					String gouki = items[i].getGouki();//�����E���@
					//					String genzu = items[i].getGenzu();//���}���e
					//					String busuu = items[i].getBusuu();//����
					//					String syukusyou = items[i].getSyukusyou();//�k��
					//					String size = items[i].getSize();//�T�C�Y
					//					String user_name = items[i].getUser_name();//���[�U�[��
					//					String busyo_name = items[i].getBusyo_name();//������
					String message = items[i].getMessage();//���b�Z�[�W
					if (message == null) {
						message = "";
					}
					String hiddenMessage = items[i].getHiddenMessage();//���b�Z�[�W(�X�V����ꍇ�Ƀ��b�Z�[�W�̓��e���ς�������ǂ������݂�)
					if (hiddenMessage == null) {
						hiddenMessage = "";
					}
					String rowNo = items[i].getRowNo();//�s�ԍ�
					String touroku = items[i].getTouroku();//�o�^�L��(�`�F�b�N����)
					String hiddentouroku = items[i].getHiddenTouroku();//�o�^�L��(�`�F�b�N���ځA�X�V����ꍇ�Ƀ��b�Z�[�W�̓��e���ς�������ǂ������݂�)
					if (hiddentouroku == null || "0".equals(hiddentouroku)) {
						hiddentouroku = "";
					}
					if ("�}�ʓo�^�˗�".equals(irai) || "�}�ʏo�͎w��".equals(irai)) {
						// �}�ʓo�^�˗��܂��͐}�ʏo�͎w���̂Ƃ� /////////////////////////////
						if (touroku == null || "0".equals(touroku)) {
							//�`�F�b�N���u�����v�Ȃ�1�u�������v�Ȃ�0�œo�^������
							touroku = "";
						} else if ("2".equals(touroku)) {
							touroku = "0";
						}
						if (seq != null) {
							if (!message.equals(hiddenMessage) || !touroku.equals(hiddentouroku)) {
								//�X�V������
								doUpdate(conn, request_listForm, user, seq, touroku, message, id, rowNo);
							}
						}

						if (countKey == 0 || id.equals(items[i - 1].getJob_id()) && rowNo.equals(items[i - 1].getRowNo())) {
							//							if (touroku == null || "0".equals(touroku) || items.length - 1 == i) {
							//							} else if (!id.equals(items[i + 1].getJob_id()) || !rowNo.equals(items[i + 1].getRowNo())) {
							//								flag = false;
							//							}
							countKey++;
						} else if (touroku == null || "0".equals(touroku)) {
							countKey = 0;
							//							totalCount = 0;
						} else {
							countKey = 1;
							//							totalCount = 0;
							//							totalCount++;
							//							if (items.length - 1 != i) {
							//								if (!id.equals(items[i + 1].getJob_id()) && countKey != 0 || !rowNo.equals(items[i + 1].getRowNo()) && countKey != 0) {
							//									flag = false;
							//								} else if (!id.equals(items[i - 1].getJob_id()) && countKey != 0 || !rowNo.equals(items[i - 1].getRowNo()) && countKey != 0) {
							//									flag = false;
							//								}
							//							} else if (!id.equals(items[i - 1].getJob_id()) && countKey != 0 || !rowNo.equals(items[i - 1].getRowNo()) && countKey != 0) {
							//								flag = false;
							//							}
						}

						//�����W���uID,�s�ԍ�,�̓W�J�����f�[�^���S�ď�����(<>null)�Ȃ猴�}�ɍ�ƈ˗��e�[�u�����X�V����
						if (isFinishedRowdata(conn, request_listForm, user, id, rowNo)) {
							String status = "";
							if ("�}�ʏo�͎w��".equals(irai)) {// �}�ʏo�͎w���Ȃ��ƃX�e�[�^�X��SET�ɂ���
								status = "SET";
							}
							doUpdate_status(conn, request_listForm, user, id, rowNo, status);
							//                            flag = true;
							//                            totalCount = 0;
							countKey = 0;
						}

					} else {
						// ���}�ؗp�˗��܂��͐}�ʈȊO�ĕt�˗��̂Ƃ� /////////////////////////////
						if (touroku == null || "0".equals(touroku)) {
							touroku = "";
						}
						//���}�ؗp�˗��A�}�ʈȊO�ĕt�˗��̍X�V
						if (!message.equals(hiddenMessage) || !touroku.equals(hiddentouroku)) {
							if ("".equals(touroku)) {//�`�F�b�N���u�����v�Ȃ�1�u�������v�Ȃ�2��o�^����
								touroku = "0";
							}
							//�X�V������
							doUpdate_irai(conn, request_listForm, user, touroku, message, id, rowNo);
						}

					}
				}

				if (request_listForm.listErrors.isEmpty()) {
					category.debug("�R�~�b�g");
					conn.commit(); // �R�~�b�g
					//�A�N�Z�X���O���o��
					AccessLoger.loging(user, AccessLoger.FID_GENZ_RES);
					//�ŐV����\������
					request_listForm.iraiList = new ArrayList<Request_listElement>();
					request_listForm.iraiList = getData_Search(request_listForm, iraiList, user, classId);
					if (request_listForm.listErrors.isEmpty()) {
						setSelectList(request_listForm);
						category.debug("�˗��o�^�����܂���");
						request_listForm.listErrors.add(MessageManager.getInstance().getMessage("msg.irai.touroku"));
					}
				} else if (conn != null) {
					category.debug("���[���o�b�N");
					request_listForm.listErrors.add(MessageManager.getInstance().getMessage("msg.irai.touroku.error"));
					conn.rollback();
					conn.setAutoCommit(true); // �����R�~�b�g���I����
				}
			} catch (Exception e) {
				request_listForm.listErrors.add(MessageManager.getInstance().getMessage("msg.irai.unexpected", e.getMessage()));
				category.error("�\�z�O�̗�O���������܂���\n" + ErrorUtility.error2String(e));
				try {
					if (conn != null) {
						category.debug("���[���o�b�N");
						conn.rollback();
						conn.setAutoCommit(true); // �����R�~�b�g���I����
					}
				} catch (java.sql.SQLException se2) {
					// SQL���s���܂����B
					ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
					category.error("���[���o�b�N�Ɏ��s���܂���" + se2.getMessage());
				}

			} finally {
				// CLOSE����
				try {
					conn.setAutoCommit(true); // �����R�~�b�g���I����
					conn.close();
				} catch (Exception e) {
				}
			}

		} else if ("button_iraiKousin".equals(request_listForm.action)) {
			//�˗��X�V���N���b�N����ƃ��X�g���ŐV�ōX�V����
			setSelectList(request_listForm);
			request_listForm.iraiList = new ArrayList<>();
			request_listForm.iraiList = getData_Search(request_listForm, iraiList, user, classId);
		}

		errors.addAttribute("request_listForm", request_listForm);

		if ("button_update".equals(request_listForm.action) || "button_iraiKousin".equals(request_listForm.action)) {
			category.debug("list -->");
			return "list";
		}
		if ("button_print".equals(request_listForm.action)) {
			category.debug("print -->");
			return "print";

		}
		if ("button_history".equals(request_listForm.action)) {
			// ��ƈ˗������{�^����������Ă�����A
			category.debug("--> history");
			return "history";
		}
		category.debug("success -->");
		return "success";
	}

	public ArrayList<Request_listElement> getData_Search(Request_listForm request_listForm, ArrayList<Request_listElement> iraiList, User user, String classId) throws Exception {

		Connection conn = null;
		java.sql.Statement stmt = null;//�f�[�^�[���݃`�F�b�N�p�R�l�N�V�����X�e�[�g�����g
		java.sql.ResultSet rs = null;
		java.sql.Statement stmt1 = null;//�f�[�^�[���݃`�F�b�N�p�R�l�N�V�����X�e�[�g�����g
		java.sql.ResultSet rs1 = null;
		ArrayList<Request_listElement> irai_List = new ArrayList<Request_listElement>();
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(true);// ��g�����U�N�V����
			//�˗����e���}�ʓo�^�˗��܂��͐}�ʏo�͎w���̃f�[�^���擾����
			stmt = conn.createStatement();
			String strSql = "select SEQ_NO,JOB_REQUEST_EXPAND_TABLE.JOB_ID,to_char(REQUEST_DATE, 'HH24:MI') as REQUEST_DATE,JOB_NAME,EXPAND_DRWG_NO,GOUKI_NO," +
					"CONTENT,COPIES,SCALE_MODE,SCALE_SIZE,USER_NAME," +
					"DEPT_NAME,JOB_REQUEST_EXPAND_TABLE.MESSAGE,JOB_REQUEST_EXPAND_TABLE.ROW_NO,EXIST," +
					"JOB_STAT" +
					" from JOB_REQUEST_EXPAND_TABLE,JOB_REQUEST_TABLE where JOB_REQUEST_EXPAND_TABLE.JOB_ID = JOB_REQUEST_TABLE.JOB_ID(+)" +
					" order by JOB_ID,ROW_NO,SEQ_NO";
			category.debug("sql = " + strSql);
			rs = stmt.executeQuery(strSql);

			while (rs.next()) {
				String seq = rs.getString("SEQ_NO");//�V�[�P���X�ԍ�
				String job_id = rs.getString("JOB_ID");//�˗�ID
				String zikan = rs.getString("REQUEST_DATE");//����
				String irai = rs.getString("JOB_NAME");//�˗����e
				String zuban = rs.getString("EXPAND_DRWG_NO");//�}��(�}�ʓo�^�˗��A�}�ʏo�͈˗��̎��g�p)
				//�J�n�ԍ��A�I���ԍ���11���̏ꍇ�́u-�v��t����
				if (zuban != null) {
					zuban = DrasapUtil.formatDrwgNo(zuban);
				}
				String gouki = rs.getString("GOUKI_NO");//�����E���@
				String genzu = rs.getString("CONTENT");//���}���e
				String busuu = rs.getString("COPIES");//����
				String syukusyou = rs.getString("SCALE_MODE");//�k��
				String size = rs.getString("SCALE_SIZE");//�T�C�Y
				String user_name = rs.getString("USER_NAME");//���[�U��
				String busyo_name = rs.getString("DEPT_NAME");//������
				String message = rs.getString("MESSAGE");//���b�Z�[�W(���}�ɍ�ƈ˗��W�J�e�[�u��, �}�ʓo�^�˗��A�}�ʏo�͈˗��̎��g�p)
				String rowNo = rs.getString("ROW_NO");//�s�ԍ�
				String touroku = rs.getString("EXIST");//�o�^�L��
				//				String sagyou = rs.getString("JOB_STAT");//��ƃX�e�[�^�X

				if ("�}�ʓo�^�˗�".equals(irai) && touroku == null || "�}�ʏo�͎w��".equals(irai) && touroku == null) {
					String hiddenMessage = message;//���b�Z�[�W��hidden
					String hiddenTouroku = touroku;//�o�^�L����hidden
					irai_List.add(new Request_listElement(seq, job_id, zikan, irai, zuban, gouki, genzu, busuu, syukusyou, size, user_name,
							busyo_name, message, rowNo, touroku, hiddenMessage, hiddenTouroku));
				}
			}

			//�˗����e�����}�ؗp�˗��܂��͐}�ʈȊO�ĕt�˗��̃f�[�^���擾����
			stmt1 = conn.createStatement();
			String strSql1 = "select JOB_ID,to_char(REQUEST_DATE, 'HH24:MI') as REQUEST_DATE,JOB_NAME,START_NO,GOUKI_NO," +
					"CONTENT,COPIES,SCALE_MODE,SCALE_SIZE,USER_NAME," +
					"DEPT_NAME,MESSAGE,ROW_NO,JOB_STAT" +
					" from JOB_REQUEST_TABLE order by JOB_ID,ROW_NO";
			category.debug("sql1 = " + strSql1);
			rs1 = stmt1.executeQuery(strSql1);

			while (rs1.next()) {
				String job_id = rs1.getString("JOB_ID");//�˗�ID
				String zikan = rs1.getString("REQUEST_DATE");//����
				String irai = rs1.getString("JOB_NAME");//�˗����e
				String zuban = rs1.getString("START_NO");//�}��(���}�ؗp�˗��A�}�ʈȊO�ĕt�̎��g�p)
															// ���}�ؗp�˗��A�}�ʈȊO�ĕt�ł́A���`�����ɂ��̂܂܏o�́B
															// �ύX '04/07/22 by Hirata.
				String gouki = rs1.getString("GOUKI_NO");//�����E���@
				String genzu = rs1.getString("CONTENT");//���}���e
				String busuu = rs1.getString("COPIES");//����
				String syukusyou = rs1.getString("SCALE_MODE");//�k��
				String size = rs1.getString("SCALE_SIZE");//�T�C�Y
				String user_name = rs1.getString("USER_NAME");//���[�U��
				String busyo_name = rs1.getString("DEPT_NAME");//������
				String message = rs1.getString("MESSAGE");//���b�Z�[�W(���}�ɍ�ƈ˗��W�J�e�[�u��, �}�ʓo�^�˗��A�}�ʏo�͈˗��̎��g�p)
				String rowNo = rs1.getString("ROW_NO");//�s�ԍ�
				String touroku = rs1.getString("JOB_STAT");//��ƃX�e�[�^�X

				if ("���}�ؗp�˗�".equals(irai) && "0".equals(touroku) || "�}�ʈȊO�ĕt�˗�".equals(irai) && "0".equals(touroku)) {
					String seq = "";//�V�[�P���X�ԍ�
					String hiddenMessage = message;//���b�Z�[�W��hidden
					String hiddenTouroku = touroku;//�o�^�L����hidden
					irai_List.add(new Request_listElement(seq, job_id, zikan, irai, zuban, gouki, genzu, busuu, syukusyou, size, user_name,
							busyo_name, message, rowNo, touroku, hiddenMessage, hiddenTouroku));
				}
			}

		} catch (java.sql.SQLException e) {
			request_listForm.listErrors.add(MessageManager.getInstance().getMessage("msg.irai.unexpected", e.getMessage()));
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

		//���}�ɍ�ƈ˗����X�g�̕\���f�[�^���擾����
		Request_listElement[] items = irai_List.toArray(new Request_listElement[0]);
		for (int i = 0; i < items.length; i++) {
			String seq = items[i].getSeq();//�V�[�P���X�ԍ�
			String job_id = items[i].getJob_id();//�˗�ID
			String zuban = items[i].getZuban();//�ԍ�
			if (zuban == null) {
				zuban = "";
			}
			String rowNo = items[i].getRowNo();//�s�ԍ�
			String zikan = items[i].getZikan();//����
			String irai = items[i].getIrai();//�˗����e
			String gouki = items[i].getGouki();//�����E���@
			String genzu = items[i].getGenzu();//���}���e
			String busuu = items[i].getBusuu();//����
			String syukusyou = items[i].getSyukusyou();//�k��
			String size = items[i].getSize();//�T�C�Y
			String user_name = items[i].getUser_name();//���[�U�[��
			String busyo_name = items[i].getBusyo_name();//������
			String message = items[i].getMessage();//���b�Z�[�W
			String hiddenMessage = items[i].getHiddenMessage();//���b�Z�[�W(�X�V����ꍇ�Ƀ��b�Z�[�W�̓��e���ς�������ǂ������݂�)
			String touroku = items[i].getTouroku();//�o�^�L��(�`�F�b�N����)
			String hiddenTouroku = items[i].getHiddenTouroku();//�o�^�L��(�`�F�b�N���ځA�X�V����ꍇ�Ƀ��b�Z�[�W�̓��e���ς�������ǂ������݂�)

			if (i == 0 || "�}�ʓo�^�˗�".equals(irai) && !job_id.equals(items[i - 1].getJob_id()) || "�}�ʓo�^�˗�".equals(irai) && !zuban.equals(items[i - 1].getZuban()) || "�}�ʏo�͎w��".equals(irai) && !zuban.equals(items[i - 1].getZuban())
					|| "�}�ʏo�͎w��".equals(irai) && !job_id.equals(items[i - 1].getJob_id()) || "���}�ؗp�˗�".equals(irai) || "�}�ʈȊO�ĕt�˗�".equals(irai)) {
				if ("�}�ʓo�^�˗�".equals(irai) || "�}�ʏo�͎w��".equals(irai)) {
					try {
						stmt = conn.createStatement();
						String strSql = "select COPIES,CONTENT,GOUKI_NO,SCALE_MODE,SCALE_SIZE from JOB_REQUEST_TABLE where JOB_ID = '" + job_id + "' and ROW_NO = '" + rowNo + "'";
						rs = stmt.executeQuery(strSql);
						if (rs.next()) {
							String copies = rs.getString("COPIES");
							String content = rs.getString("CONTENT");
							String gouguti = rs.getString("GOUKI_NO");
							String syuku = rs.getString("SCALE_MODE");
							String saize = rs.getString("SCALE_SIZE");
							busuu = copies;//����
							genzu = content;//���}���e
							gouki = gouguti;//�����E���@
							syukusyou = syuku;//�k��
							size = saize;//�T�C�Y
						}

					} catch (java.sql.SQLException e) {
						request_listForm.listErrors.add(MessageManager.getInstance().getMessage("msg.sql.error", e.getMessage()));
						ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
						category.error("[" + classId + "]:���}�ɍ�ƈ˗��e�[�u���̏����Ɏ��s���܂���\n" + ErrorUtility.error2String(e));
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
				iraiList.add(new Request_listElement(seq, job_id, zikan, irai, zuban, gouki, genzu, busuu, syukusyou, size, user_name,
						busyo_name, message, rowNo, touroku, hiddenMessage, hiddenTouroku));
			}
		}
		if (items.length == 0) {
			request_listForm.listErrors.add(MessageManager.getInstance().getMessage("msg.irai.nodata"));
			category.debug("�f�[�^������܂���");
		}

		//���b�Z�[�W���v���_�E���Ɋi�[����
		request_listForm.messageNameList = new ArrayList<String>();
		request_listForm.messageNameList.add("");
		try {
			stmt = conn.createStatement();
			String strSql1 = "select MODULE_ID,MESSAGE_NO,MESSAGE from MESSAGE_MASTER where MODULE_ID = '01' order by MODULE_ID,MESSAGE_NO";
			rs = stmt.executeQuery(strSql1);
			while (rs.next()) {
				//				String id = rs.getString("MODULE_ID");//���W���[��ID
				//				String messageNo = rs.getString("MESSAGE_NO");//���b�Z�[�W�ԍ�
				String message = rs.getString("MESSAGE");//���b�Z�[�W

				request_listForm.messageNameList.add(message);//���b�Z�[�W���v���_�E���Ɋi�[����
			}

		} catch (java.sql.SQLException e) {
			request_listForm.listErrors.add(MessageManager.getInstance().getMessage("msg.sql.error" + e.getMessage()));
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

		return iraiList;
	}

	/**
	 * �}�ʓo�^�˗��A�}�ʏo�͎w���ɂ����āA���}�ɍ�ƈ˗��W�J�e�[�u�����X�V����B
	 * �ύX['04.Jul.14]�E�E�E��Ǝ�ID�A��ƎҖ��A��Ɠ������X�V����B
	 */
	public void doUpdate(Connection conn, Request_listForm request_listForm, User user, String seq, String touroku, String message, String id, String rowNo) throws Exception {

		java.sql.Statement stmt = null;
		try {
			stmt = conn.createStatement(); // �f�[�^���݃`�F�b�N�p�R�l�N�V�����X�e�[�g�����g
			String strSql = "update JOB_REQUEST_EXPAND_TABLE set EXIST = '" + touroku + "',MESSAGE = '" + message + "'," +
					"JOB_USER_ID='" + user.getId() + "', JOB_USER_NAME='" + user.getName() + "', JOB_DATE=SYSDATE" +
					" where SEQ_NO = '" + seq + "' and JOB_ID = '" + id + "' and ROW_NO = '" + rowNo + "'";
			category.debug("�X�V������(�}�ʓo�^�˗�,�}�ʏo�͎w��) = " + strSql);
			int cnt = stmt.executeUpdate(strSql);
			category.debug("�X�V�� = " + Integer.toString(cnt));

		} catch (Exception e) {
			request_listForm.listErrors.add(MessageManager.getInstance().getMessage("msg.irai.failed.update", e.getMessage()));
			//for �V�X�e���Ǘ���
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
			} catch (Exception e) {
			}
		}
	}

	/**
	 * ���}�ؗp�˗�,�}�ʈȊO�ĕt�ł̍X�V����������B
	 * ���}�ɍ�ƈ˗����X�V����B
	 * �ύX['04.Jul.14]�E�E�E��Ǝ�ID�A��ƎҖ����X�V����B
	 */
	public void doUpdate_irai(Connection conn, Request_listForm request_listForm, User user, String touroku, String message, String id, String rowNo) throws Exception {

		java.sql.Statement stmt = null;
		//��ƃX�e�[�^�X(JOB_STAT)��0�̎��͏�ԑJ�ڃX�e�[�^�X(TRANSIT_STAT)�ɁuSET�v����͂���
		String status = "";
		if ("0".equals(touroku)) {
			status = "SET";
		}
		try {
			stmt = conn.createStatement(); // �f�[�^���݃`�F�b�N�p�R�l�N�V�����X�e�[�g�����g
			String strSql = "update JOB_REQUEST_TABLE set JOB_STAT = '" + touroku + "',MESSAGE = '" + message + "', JOB_DATE = sysdate, TRANSIT_STAT = '" + status + "'," +
					"JOB_USER_ID='" + user.getId() + "', JOB_USER_NAME='" + user.getName() + "'" +
					" where JOB_ID = '" + id + "' and ROW_NO = '" + rowNo + "'";
			category.debug("�X�V������(���}�ؗp�˗�,�}�ʈȊO�ĕt) = " + strSql);
			int cnt = stmt.executeUpdate(strSql);
			category.debug("�X�V�� = " + Integer.toString(cnt));

		} catch (Exception e) {
			request_listForm.listErrors.add(MessageManager.getInstance().getMessage("msg.irai.failed.update", e.getMessage()));
			//for �V�X�e���Ǘ���
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
			} catch (Exception e) {
			}
		}
	}

	/**
	 * �}�ʓo�^�˗��A�}�ʏo�͎w���ł́A�W�J�����}�Ԃɂ��đS�Ă̏������I�������A
	 * ���}�ɍ�ƈ˗��e�[�u�����X�V����B
	 * �܂��}�ʏo�͎w���̏ꍇ�͍�ƃX�e�[�^�X��SET�œo�^����B
	 * �ύX['04.Jul.14]�E�E�E��Ǝ�ID�A��ƎҖ����X�V����B
	 */
	public void doUpdate_status(Connection conn, Request_listForm request_listForm, User user, String id, String rowNo, String status) throws Exception {

		java.sql.Statement stmt = null;
		try {
			stmt = conn.createStatement(); // �f�[�^���݃`�F�b�N�p�R�l�N�V�����X�e�[�g�����g
			String strSql = "update JOB_REQUEST_TABLE set JOB_STAT = '1', JOB_DATE = sysdate, TRANSIT_STAT = '" + status + "'," +
					"JOB_USER_ID='" + user.getId() + "', JOB_USER_NAME='" + user.getName() + "'" +
					" where JOB_ID = '" + id + "' and ROW_NO = '" + rowNo + "'";
			category.debug("JOB_REQUEST_TABLE���X�V = " + strSql);
			int cnt = stmt.executeUpdate(strSql);
			category.debug("�X�V�� = " + Integer.toString(cnt));

		} catch (Exception e) {
			request_listForm.listErrors.add(MessageManager.getInstance().getMessage("msg.irai.failed.update", e.getMessage()));
			//for �V�X�e���Ǘ���
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
			} catch (Exception e) {
			}
		}
	}

	/**
	 * �w�肵���W���uID,�s�ԍ��̈˗��W�J�f�[�^���S�ď������ꂽ���𔻒f����B
	 * @param id �W���uID
	 * @param rowNo �s�ԍ�
	 * @return �S�ď�������Ă���� true
	 */
	private boolean isFinishedRowdata(Connection conn, Request_listForm request_listForm, User user,
			String id, String rowNo) throws Exception {

		java.sql.Statement stmt = null;
		java.sql.ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			// �w�肵���W���uID,�s�ԍ��� EXIST=null �̌����𐔂���
			String strSql = "select count(*) CNT" +
					" from JOB_REQUEST_EXPAND_TABLE" +
					" where JOB_ID = '" + id + "' and ROW_NO = '" + rowNo + "'" +
					" and EXIST is null";
			rs = stmt.executeQuery(strSql);
			rs.next();
			// ����=0�Ȃ� true
			return rs.getInt("CNT") == 0;

		} catch (Exception e) {

			request_listForm.listErrors.add(MessageManager.getInstance().getMessage("msg.sql.error", e.getMessage()));
			//for �V�X�e���Ǘ���
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));

			throw e;

		} finally {
			// CLOSE����
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				stmt.close();
			} catch (Exception e) {
			}
		}

	}

	/* �t�H�[���̃Z���N�g�pArrayList�Ƀf�[�^���i�[����B
	 */
	public static void setSelectList(Request_listForm request_listForm) throws Exception {

		// �`�F�b�N���ڂ̃L�[
		request_listForm.checkKeyList = new ArrayList<String>();
		request_listForm.checkKeyList.add("0");
		request_listForm.checkKeyList.add("1");
		request_listForm.checkKeyList.add("2");

		// �`�F�b�N���ڂ̃l�[��
		request_listForm.checkNameList = new ArrayList<String>();
		request_listForm.checkNameList.add("");
		request_listForm.checkNameList.add("����");
		request_listForm.checkNameList.add("������");

	}

}
