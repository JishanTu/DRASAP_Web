package tyk.drasap.genzu_irai;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import tyk.drasap.common.DrasapPropertiesFactory;
import tyk.drasap.common.DrasapUtil;
import tyk.drasap.common.ErrorUtility;
import tyk.drasap.common.MessageManager;
import tyk.drasap.common.User;
import tyk.drasap.errlog.ErrorLoger;
import tyk.drasap.springfw.action.BaseAction;

/**
 * ���}�ɍ�ƈ˗������̂��߂�Action�B
 * �\���f�[�^���쐬���āAjsp�֑J�ڂ���B
 * @author fumi
 */
@Controller
@SessionAttributes("historyForm")
public class RequestHistoryAction extends BaseAction {
	// --------------------------------------------------------- Instance Variables
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
	@PostMapping("/requestHistory")
	public String execute(
			@ModelAttribute("historyForm") RequestHistoryForm form,
			HttpServletRequest request,
			HttpServletResponse response,
			Model errors)
			throws Exception {
		category.debug("start");
		// session��user���m�F
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		if (user == null) {
			return "timeout";
		}
		RequestHistoryForm historyForm = form;

		// ��ƈ˗��������擾���A�t�H�[���ɃZ�b�g����B
		setHistoryData(historyForm, user);

		session.setAttribute("requestHistoryForm", historyForm);

		category.debug("--> success");
		return "success";
	}

	private void setHistoryData(RequestHistoryForm historyForm, User user) {
		try {
			//
			historyForm.getHistoryList().addAll(crtHistoryList());

		} catch (Exception e) {
			// �J���җp�̃��O
			category.error("��ƈ˗������̎擾�Ɏ��s���܂���\n" + ErrorUtility.error2String(e));
			// �Ǘ��җp�̃��O
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			// ���[�U�[�p�̃��O
			historyForm.getErrors().add(MessageManager.getInstance().getMessage("msg.sql.error", e.getMessage()));
		}
	}

	/**
	 * ��ƈ˗�������List�f�[�^���쐬����B
	 * @return ��ƈ˗�������List�f�[�^
	 * @throws Exception
	 */
	private ArrayList<RequestHistoryElement> crtHistoryList() throws Exception {
		// ���O�ɍ������10���O�����߂Ă���
		Calendar calFrom = Calendar.getInstance();
		calFrom.add(Calendar.DATE, -10);// 10���O��
		String strFromDate = new SimpleDateFormat("yyyy/MM/dd").format(calFrom.getTime());
		//
		ArrayList<RequestHistoryElement> historyList = new ArrayList<>();
		Connection conn = null;
		Statement stmt1 = null;
		ResultSet rs1 = null;
		Statement stmt2 = null;
		ResultSet rs2 = null;
		try {
			conn = ds.getConnection();
			//�K���R�~�b�g���g�p���A�������x����Repeatable Read�ȏ��
			conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			// �g�����U�N�V�����������x����Repeatable Read
			conn.setAutoCommit(false); // �����R�~�b�g���I�t��

			// 1) �}�ʓo�^�˗��A�}�ʏo�͎w���ɂ��Ă̏���
			String strSql1 = "select TO_CHAR(JOB_REQUEST_EXPAND_TABLE.JOB_DATE,'YYYY/MM/DD HH24:MI') JOB_DATE_0," +
					" JOB_REQUEST_EXPAND_TABLE.JOB_USER_NAME, JOB_REQUEST_EXPAND_TABLE.MESSAGE," +
					" TO_CHAR(JOB_REQUEST_TABLE.REQUEST_DATE,'YYYY/MM/DD HH24:MI') REQUEST_DATE_0," +
					" JOB_REQUEST_TABLE.JOB_NAME, JOB_REQUEST_EXPAND_TABLE.EXPAND_DRWG_NO," +
					" JOB_REQUEST_TABLE.GOUKI_NO, JOB_REQUEST_TABLE.CONTENT,JOB_REQUEST_TABLE.COPIES," +
					" JOB_REQUEST_TABLE.SCALE_MODE,JOB_REQUEST_TABLE.SCALE_SIZE," +
					" JOB_REQUEST_TABLE.USER_NAME," +
					" JOB_REQUEST_TABLE.DEPT_NAME" +
					" from JOB_REQUEST_EXPAND_TABLE, JOB_REQUEST_TABLE" +
					// ��ƈ˗��W�J�e�[�u���ɁA��ƈ˗��e�[�u�����O������
					" where JOB_REQUEST_EXPAND_TABLE.JOB_ID=JOB_REQUEST_TABLE.JOB_ID(+)" +
					" and JOB_REQUEST_EXPAND_TABLE.ROW_NO=JOB_REQUEST_TABLE.ROW_NO(+)" +
					// ��ƍς݂ł���
					" and JOB_REQUEST_EXPAND_TABLE.EXIST is not null" +
					// �w�肵�������ȍ~�ł���
					" and JOB_REQUEST_EXPAND_TABLE.JOB_DATE >= TO_DATE('" + strFromDate + "','YYYY/MM/DD')";
			stmt1 = conn.createStatement();
			rs1 = stmt1.executeQuery(strSql1);
			while (rs1.next()) {
				String completeDate = rs1.getString("JOB_DATE_0");// ��Ɗ�������
				String completeUser = rs1.getString("JOB_USER_NAME");// ��Ɗ����Җ�
				String requestDate = rs1.getString("REQUEST_DATE_0");// �˗�����
				String jobName = rs1.getString("JOB_NAME");// �W���u��
				String drwgNo = rs1.getString("EXPAND_DRWG_NO");// �}��
				drwgNo = DrasapUtil.formatDrwgNo(drwgNo);// �}�ʓo�^�˗��A�}�ʏo�͎w���ł͐��`����
				String goukiNo = rs1.getString("GOUKI_NO");// ���@
				String genzuContent = rs1.getString("CONTENT");// ���}���e
				String copies = rs1.getString("COPIES");// ����
				String scaleMode = rs1.getString("SCALE_MODE");// �k���敪
				String scaleSize = rs1.getString("SCALE_SIZE");// �k���T�C�Y
				String message = rs1.getString("MESSAGE");// ���b�Z�[�W
				String requestUser = rs1.getString("USER_NAME");// �˗��Җ�
				String deptName = rs1.getString("DEPT_NAME");// �˗�������

				historyList.add(new RequestHistoryElement(completeDate, completeUser,
						requestDate, jobName, drwgNo, goukiNo,
						genzuContent, copies, scaleMode, scaleSize,
						message, requestUser, deptName));
			}
			// 2) ���}�ؗp�˗��A���}�ȊO�ĕt�ɂ��Ă̏���
			String strSql2 = "select TO_CHAR(JOB_DATE,'YYYY/MM/DD HH24:MI') JOB_DATE_0,JOB_USER_NAME," +
					" TO_CHAR(REQUEST_DATE,'YYYY/MM/DD HH24:MI') REQUEST_DATE_0,JOB_NAME," +
					" START_NO,GOUKI_NO,CONTENT,COPIES,SCALE_MODE,SCALE_SIZE," +
					" MESSAGE,USER_NAME,DEPT_NAME" +
					" from JOB_REQUEST_TABLE" +
					// ��ƍς݂̌��}�ؗp�˗��A���}�ȊO�ĕt
					" where JOB_STAT <> '0' and REQUEST in ('C','D')" +
					// �w�肵�������ȍ~�ł���
					" and JOB_DATE >= TO_DATE('" + strFromDate + "','YYYY/MM/DD')";
			stmt2 = conn.createStatement();
			rs2 = stmt2.executeQuery(strSql2);
			while (rs2.next()) {
				String completeDate = rs2.getString("JOB_DATE_0");// ��Ɗ�������
				String completeUser = rs2.getString("JOB_USER_NAME");// ��Ɗ����Җ�
				String requestDate = rs2.getString("REQUEST_DATE_0");// �˗�����
				String jobName = rs2.getString("JOB_NAME");// �W���u��
				String drwgNo = rs2.getString("START_NO");// �}��
				String goukiNo = rs2.getString("GOUKI_NO");// ���@
				String genzuContent = rs2.getString("CONTENT");// ���}���e
				String copies = rs2.getString("COPIES");// ����
				String scaleMode = rs2.getString("SCALE_MODE");// �k���敪
				String scaleSize = rs2.getString("SCALE_SIZE");// �k���T�C�Y
				String message = rs2.getString("MESSAGE");// ���b�Z�[�W
				String requestUser = rs2.getString("USER_NAME");// �˗��Җ�
				String deptName = rs2.getString("DEPT_NAME");// �˗�������

				historyList.add(new RequestHistoryElement(completeDate, completeUser,
						requestDate, jobName, drwgNo, goukiNo,
						genzuContent, copies, scaleMode, scaleSize,
						message, requestUser, deptName));
			}

		} catch (Exception e) {
			throw e;
		} finally {
			// CLOSE����
			try {
				rs1.close();
			} catch (Exception e) {
			}
			try {
				stmt1.close();
			} catch (Exception e) {
			}
			try {
				rs2.close();
			} catch (Exception e) {
			}
			try {
				stmt2.close();
			} catch (Exception e) {
			}
			try {
				conn.close();
			} catch (Exception e) {
			}
		}

		// ���̎��_�ł́A���������̍~���ɂȂ��Ă��Ȃ��B
		// �܂������Ƀ\�[�g����B
		Collections.sort(historyList);
		// ���ɍ~���Ƀ\�[�g����B
		ArrayList<RequestHistoryElement> historyList2 = new ArrayList<>();
		for (int i = historyList.size() - 1; i >= 0; i--) {
			historyList2.add(historyList.get(i));
		}

		return historyList2;
	}

}
