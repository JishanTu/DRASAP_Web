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
 * 原図庫作業依頼履歴のためのAction。
 * 表示データを作成して、jspへ遷移する。
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
		// sessionのuserを確認
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		if (user == null) {
			return "timeout";
		}
		RequestHistoryForm historyForm = form;

		// 作業依頼履歴を取得し、フォームにセットする。
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
			// 開発者用のログ
			category.error("作業依頼履歴の取得に失敗しました\n" + ErrorUtility.error2String(e));
			// 管理者用のログ
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			// ユーザー用のログ
			historyForm.getErrors().add(MessageManager.getInstance().getMessage("msg.sql.error", e.getMessage()));
		}
	}

	/**
	 * 作業依頼履歴のListデータを作成する。
	 * @return 作業依頼履歴のListデータ
	 * @throws Exception
	 */
	private ArrayList<RequestHistoryElement> crtHistoryList() throws Exception {
		// 事前に今日より10日前を求めておく
		Calendar calFrom = Calendar.getInstance();
		calFrom.add(Calendar.DATE, -10);// 10日前に
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
			//必ずコミットを使用し、分離レベルはRepeatable Read以上で
			conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			// トランザクション分離レベルはRepeatable Read
			conn.setAutoCommit(false); // 自動コミットをオフに

			// 1) 図面登録依頼、図面出力指示についての処理
			String strSql1 = "select TO_CHAR(JOB_REQUEST_EXPAND_TABLE.JOB_DATE,'YYYY/MM/DD HH24:MI') JOB_DATE_0," +
					" JOB_REQUEST_EXPAND_TABLE.JOB_USER_NAME, JOB_REQUEST_EXPAND_TABLE.MESSAGE," +
					" TO_CHAR(JOB_REQUEST_TABLE.REQUEST_DATE,'YYYY/MM/DD HH24:MI') REQUEST_DATE_0," +
					" JOB_REQUEST_TABLE.JOB_NAME, JOB_REQUEST_EXPAND_TABLE.EXPAND_DRWG_NO," +
					" JOB_REQUEST_TABLE.GOUKI_NO, JOB_REQUEST_TABLE.CONTENT,JOB_REQUEST_TABLE.COPIES," +
					" JOB_REQUEST_TABLE.SCALE_MODE,JOB_REQUEST_TABLE.SCALE_SIZE," +
					" JOB_REQUEST_TABLE.USER_NAME," +
					" JOB_REQUEST_TABLE.DEPT_NAME" +
					" from JOB_REQUEST_EXPAND_TABLE, JOB_REQUEST_TABLE" +
					// 作業依頼展開テーブルに、作業依頼テーブルを外部結合
					" where JOB_REQUEST_EXPAND_TABLE.JOB_ID=JOB_REQUEST_TABLE.JOB_ID(+)" +
					" and JOB_REQUEST_EXPAND_TABLE.ROW_NO=JOB_REQUEST_TABLE.ROW_NO(+)" +
					// 作業済みである
					" and JOB_REQUEST_EXPAND_TABLE.EXIST is not null" +
					// 指定した期日以降である
					" and JOB_REQUEST_EXPAND_TABLE.JOB_DATE >= TO_DATE('" + strFromDate + "','YYYY/MM/DD')";
			stmt1 = conn.createStatement();
			rs1 = stmt1.executeQuery(strSql1);
			while (rs1.next()) {
				String completeDate = rs1.getString("JOB_DATE_0");// 作業完了日時
				String completeUser = rs1.getString("JOB_USER_NAME");// 作業完了者名
				String requestDate = rs1.getString("REQUEST_DATE_0");// 依頼日時
				String jobName = rs1.getString("JOB_NAME");// ジョブ名
				String drwgNo = rs1.getString("EXPAND_DRWG_NO");// 図番
				drwgNo = DrasapUtil.formatDrwgNo(drwgNo);// 図面登録依頼、図面出力指示では整形する
				String goukiNo = rs1.getString("GOUKI_NO");// 号機
				String genzuContent = rs1.getString("CONTENT");// 原図内容
				String copies = rs1.getString("COPIES");// 部数
				String scaleMode = rs1.getString("SCALE_MODE");// 縮小区分
				String scaleSize = rs1.getString("SCALE_SIZE");// 縮小サイズ
				String message = rs1.getString("MESSAGE");// メッセージ
				String requestUser = rs1.getString("USER_NAME");// 依頼者名
				String deptName = rs1.getString("DEPT_NAME");// 依頼部署名

				historyList.add(new RequestHistoryElement(completeDate, completeUser,
						requestDate, jobName, drwgNo, goukiNo,
						genzuContent, copies, scaleMode, scaleSize,
						message, requestUser, deptName));
			}
			// 2) 原図借用依頼、原図以外焼付についての処理
			String strSql2 = "select TO_CHAR(JOB_DATE,'YYYY/MM/DD HH24:MI') JOB_DATE_0,JOB_USER_NAME," +
					" TO_CHAR(REQUEST_DATE,'YYYY/MM/DD HH24:MI') REQUEST_DATE_0,JOB_NAME," +
					" START_NO,GOUKI_NO,CONTENT,COPIES,SCALE_MODE,SCALE_SIZE," +
					" MESSAGE,USER_NAME,DEPT_NAME" +
					" from JOB_REQUEST_TABLE" +
					// 作業済みの原図借用依頼、原図以外焼付
					" where JOB_STAT <> '0' and REQUEST in ('C','D')" +
					// 指定した期日以降である
					" and JOB_DATE >= TO_DATE('" + strFromDate + "','YYYY/MM/DD')";
			stmt2 = conn.createStatement();
			rs2 = stmt2.executeQuery(strSql2);
			while (rs2.next()) {
				String completeDate = rs2.getString("JOB_DATE_0");// 作業完了日時
				String completeUser = rs2.getString("JOB_USER_NAME");// 作業完了者名
				String requestDate = rs2.getString("REQUEST_DATE_0");// 依頼日時
				String jobName = rs2.getString("JOB_NAME");// ジョブ名
				String drwgNo = rs2.getString("START_NO");// 図番
				String goukiNo = rs2.getString("GOUKI_NO");// 号機
				String genzuContent = rs2.getString("CONTENT");// 原図内容
				String copies = rs2.getString("COPIES");// 部数
				String scaleMode = rs2.getString("SCALE_MODE");// 縮小区分
				String scaleSize = rs2.getString("SCALE_SIZE");// 縮小サイズ
				String message = rs2.getString("MESSAGE");// メッセージ
				String requestUser = rs2.getString("USER_NAME");// 依頼者名
				String deptName = rs2.getString("DEPT_NAME");// 依頼部署名

				historyList.add(new RequestHistoryElement(completeDate, completeUser,
						requestDate, jobName, drwgNo, goukiNo,
						genzuContent, copies, scaleMode, scaleSize,
						message, requestUser, deptName));
			}

		} catch (Exception e) {
			throw e;
		} finally {
			// CLOSE処理
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

		// この時点では、完了日時の降順になっていない。
		// まず昇順にソートする。
		Collections.sort(historyList);
		// 次に降順にソートする。
		ArrayList<RequestHistoryElement> historyList2 = new ArrayList<>();
		for (int i = historyList.size() - 1; i >= 0; i--) {
			historyList2.add(historyList.get(i));
		}

		return historyList2;
	}

}
