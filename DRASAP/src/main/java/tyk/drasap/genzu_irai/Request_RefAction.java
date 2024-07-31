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
 * 原図庫作業依頼詳細
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
		category.debug("Request_RefAction スタート");
		request_refForm.listErrors = new ArrayList<String>();//エラーを表示
		//		ActionErrors errors = new ActionErrors();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		if (user == null) {
			return "timeout";
		}
		try {
			// クラスID取得
			classId = DrasapPropertiesFactory.getDrasapProperties(this).getProperty("tyk.drasap.genzu_irai.Request_RefAction");
		} catch (Exception e) {
			category.error("プロパティファイルの読込みに失敗しました\n" + ErrorUtility.error2String(e));
		}

		java.sql.Connection conn = null;
		java.sql.Statement stmt = null;//データー存在チェック用コネクションステートメント
		java.sql.ResultSet rs = null;
		category.debug("action = " + request_refForm.action);
		if ("".equals(request_refForm.action) || request_refForm.action == null) {
			//原図庫作業依頼詳細画面を表示する
			conn = ds.getConnection();
			conn.setAutoCommit(true);// 非トランザクション
			//データを取得する
			doSyokika(user, request_refForm, conn);

			ArrayList<Request_RefElement> iraiList = new ArrayList<Request_RefElement>();//画面表示内容を格納する
			//作業中で表示するものに関してiraiListに格納する
			Request_RefElement[] items = request_refForm.irai_List.toArray(new Request_RefElement[0]);
			for (int i = 0; i < items.length; i++) {
				String job_id = items[i].getJob_id();//ジョブID
				String job_stat = items[i].getJob_stat();//作業ステータス
				String job_name = items[i].getJob_name();//依頼内容
				String gouki = items[i].getGouki();//号口・号機
				String genzu = items[i].getGenzu();//原図内容
				String start = items[i].getStart();//開始番号
				if (start == null) {
					start = "";
				}
				String end = items[i].getEnd();//終了番号
				if (end == null) {
					end = "";
				}
				String busuu = items[i].getBusuu();//部数
				String syuku = items[i].getSyuku();//縮小
				String size = items[i].getSize();//サイズ
				String printer = items[i].getPrinter();//出力先
				String messege = items[i].getMessege();//メッセージ(図面登録依頼、図面出力指示)
				if (messege == null) {
					messege = "";
				}
				String messege1 = items[i].getMessege1();//メッセージ(原図借用依頼、図面以外焼付依頼)
				String exist = items[i].getExist();//登録有無
				if (exist == null) {
					exist = "";
				}
				String seq = items[i].getSeq();//シーケンス番号
				String rowNo = items[i].getRowNo();//行番号
				String startNo = "";//1つ前の開始番号をチェック
				String endNo = "";//1つ前の終了番号をチェック
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
				if (i == 0 || "図面登録依頼".equals(job_name) && !start.equals(startNo) && !end.equals(endNo) && !rowNo.equals(items[i - 1].getRowNo()) || "図面出力指示".equals(job_name) && !start.equals(startNo) && !end.equals(endNo) && !rowNo.equals(items[i - 1].getRowNo())
						|| "図面登録依頼".equals(job_name) && !job_id.equals(items[i - 1].getJob_id()) && start.equals(startNo) && end.equals(endNo) && rowNo.equals(items[i - 1].getRowNo()) || "図面出力指示".equals(job_name) && !job_id.equals(items[i - 1].getJob_id()) && start.equals(startNo) && end.equals(endNo) && rowNo.equals(items[i - 1].getRowNo())
						|| "図面登録依頼".equals(job_name) && !job_id.equals(items[i - 1].getJob_id()) || "図面出力指示".equals(job_name) && !job_id.equals(items[i - 1].getJob_id()) || "図面登録依頼".equals(job_name) && job_id.equals(items[i - 1].getJob_id()) && !rowNo.equals(items[i - 1].getRowNo()) || "図面出力指示".equals(job_name) && job_id.equals(items[i - 1].getJob_id()) && !rowNo.equals(items[i - 1].getRowNo())
						|| "原図借用依頼".equals(job_name) || "図面以外焼付依頼".equals(job_name)) {
					//開始、終了番号がある場合は図番を展開する(依頼内容が図面登録依頼、図面出力依頼の場合)
					if (!"".equals(start) && !"".equals(end)) {
						String data = "";//登録とメッセージがあるかチェックをする
						try {
							stmt = conn.createStatement();
							String strSql = "select EXIST,MESSAGE from JOB_REQUEST_EXPAND_TABLE where JOB_ID = '" + job_id + "' and ROW_NO = '" + rowNo + "'";
							rs = stmt.executeQuery(strSql);
							while (rs.next()) {
								String touroku = rs.getString("EXIST");//登録有無
								String message = rs.getString("MESSAGE");//メッセージ
								if ("0".equals(touroku) || message != null) {
									data = "0";
								}
							}

						} catch (java.sql.SQLException e) {
							request_refForm.listErrors.add(MessageManager.getInstance().getMessage("msg.sql.error", e.getMessage()));
							//for システム管理者
							ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
							category.error("[" + classId + "]:原図庫作業依頼展開テーブルの処理に失敗しました\n" + ErrorUtility.error2String(e));
						} finally {
							try {// CLOSE処理
								stmt.close();
								stmt = null;
								rs.close();
								rs = null;
							} catch (Exception e) {
							}
						}

						exist = data;//開始、終了番号の範囲内に登録有無=0またはメッセージがあれば原図庫作業依頼リストにメッセージありのリンクをたてる
						if ("".equals(exist)) {
							messege = "";
						}
						//登録有無またはメッセージのどちらかがあるか確認をする
					} else if ("図面登録依頼".equals(job_name) || "図面出力指示".equals(job_name)) {
						try {
							stmt = conn.createStatement();
							String strSql = "select EXIST,MESSAGE from JOB_REQUEST_EXPAND_TABLE where JOB_ID = '" + job_id + "' and EXPAND_DRWG_NO = '" + start + "' and ROW_NO = '" + rowNo + "'";
							rs = stmt.executeQuery(strSql);
							if (rs.next()) {
								String touroku = rs.getString("EXIST");//登録有無
								String message = rs.getString("MESSAGE");//メッセージ
								if (touroku == null && message == null) {
									exist = "";
									messege = "";
								} else if ("1".equals(touroku) || "2".equals(touroku)) {//原紙なしの表示は登録有無が0の時だけ
									exist = "";
								}
							}

						} catch (java.sql.SQLException e) {
							request_refForm.listErrors.add(MessageManager.getInstance().getMessage("msg.sql.error", e.getMessage()));
							//for システム管理者
							ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
							category.error("[" + classId + "]:原図庫作業依頼展開テーブルの処理に失敗しました\n" + ErrorUtility.error2String(e));
						} finally {
							try {// CLOSE処理
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

			//原図庫依頼詳細の画面に格納したデータを表示する
			request_refForm.iraiList = iraiList;
			if (conn != null) {
				conn.close();
			}
			if (request_refForm.iraiList.isEmpty()) {
				//依頼しているデータはない
				request_refForm.listErrors.add(MessageManager.getInstance().getMessage("msg.irai.nodata"));
			}
		} else if ("button_Mtenkai".equals(request_refForm.action)) {
			//メッセージありのリンクがある場合で、
			//図面登録依頼、図面出力指示の場合は原図庫作業依頼展開テーブル(JOB_REQUEST_EXPAND_TABLE)を表示する
			String strc = request_refForm.job;//依頼ID,依頼内容,行番号のデータを取得する
			java.util.StringTokenizer st = new java.util.StringTokenizer(strc, "_");
			ArrayList<Object> list = new ArrayList<>();
			while (st.hasMoreElements()) {
				list.add(st.nextElement());
			}
			String job_id = "";//依頼ID
			String irai = "";//依頼内容
			String rowNo = "";//行番号
			for (int i = 0; i < list.size(); i++) {
				if (i == list.size() - 1) {
					rowNo = list.get(i).toString();
				} else if (i == 1) {
					irai = list.get(i).toString();
				} else {
					job_id = list.get(i).toString();
				}
			}
			request_refForm.job_id = job_id;//依頼ID
			request_refForm.job_name = irai;//依頼内容
			request_refForm.iraiList = new ArrayList<>();
			//原図庫作業者からのメッセージに表示させる
			doTenkai(user, request_refForm, job_id, rowNo);

		} else if ("button_Msagyo".equals(request_refForm.action)) {
			//メッセージありのリンクがある場合で、原図借用依頼、図面以外焼付依頼の場合のデータを表示する
			String strc = request_refForm.job;//依頼ID,行番号,依頼内容のデータを取得する
			java.util.StringTokenizer st = new java.util.StringTokenizer(strc, "_");
			ArrayList<Object> list = new ArrayList<>();
			while (st.hasMoreElements()) {
				list.add(st.nextElement());
			}
			String job_id = "";//依頼ID
			String rowNo = "";//行番号
			String irai = "";//依頼内容
			for (int i = 0; i < list.size(); i++) {
				if (i == list.size() - 1) {
					rowNo = list.get(i).toString();
				} else if (i == 1) {
					irai = list.get(i).toString();
				} else {
					job_id = list.get(i).toString();
				}
			}
			request_refForm.job_id = job_id;//依頼ID
			request_refForm.job_name = irai;//依頼内容
			request_refForm.iraiList = new ArrayList<>();
			//原図庫作業者からのメッセージに表示させる
			doSagyo(user, request_refForm, job_id, rowNo);

		}

		errors.addAttribute("request_refForm", request_refForm);
		if ("button_Mtenkai".equals(request_refForm.action) || "button_Msagyo".equals(request_refForm.action)) {
			//メッセージありのリンクを押して次の画面へ
			return "list";
		}
		//原図庫依頼詳細画面へ
		return "success";
	}

	/*
	 * 初期データを取得する
	 *
	 */
	public void doSyokika(User user, Request_RefForm request_refForm, java.sql.Connection conn) throws Exception {
		request_refForm.irai_List = new ArrayList<>();//依頼中の内容を格納する
		ArrayList<Request_RefElement> irai_List = new ArrayList<>();//依頼中の内容を格納する
		ArrayList<Request_RefElement> iraiend_List = new ArrayList<>();//完了した内容を格納する

		java.sql.Statement stmt = null;//データー存在チェック用コネクションステートメント
		java.sql.ResultSet rs = null;
		java.sql.Statement stmt1 = null;//データー存在チェック用コネクションステートメント
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
				String job_id = rs.getString("JOB_ID");//依頼ID
				String job_stat = rs.getString("JOB_STAT");//状態
				String job_name = rs.getString("JOB_NAME");//依頼内容
				String gouki = rs.getString("GOUKI_NO");//号口・号機
				String genzu = rs.getString("CONTENT");//原図内容
				String start = rs.getString("START_NO");//番号(開始)
				if (start != null) {
					//開始番号、終了番号が11桁の場合は「-」を付ける
					start = DrasapUtil.formatDrwgNo(start);
				}
				String end = rs.getString("END_NO");//番号(終了)
				if (end != null) {
					//開始番号、終了番号が11桁の場合は「-」を付ける
					end = DrasapUtil.formatDrwgNo(end);
				}

				String busuu = rs.getString("COPIES");//部数
				String syuku = rs.getString("SCALE_MODE");//縮小
				String size = rs.getString("SCALE_SIZE");//サイズ
				String printer = rs.getString("PRINTER_ID");//出力先
				String messege = rs.getString("MESSAGE");//メッセージ(図面登録依頼、図面出力指示)
				String messege1 = "";//メッセージ(原図借用依頼、図面以外焼付依頼)
				String exist = rs.getString("EXIST");//登録有無
				String seq = rs.getString("SEQ_NO");//シーケンス番号
				String rowNo = rs.getString("ROW_NO");//行番号

				//出力先をセットする
				for (int i = 0; i < user.getEnablePrinters().size(); i++) {
					tyk.drasap.common.Printer e = user.getEnablePrinters().get(i);
					String id = e.getId();//プリンタID
					if (id.equals(printer)) {
						printer = e.getDisplayName();//表示名
						break;
					}
				}

				if ("図面登録依頼".equals(job_name) && job_id != null && "0".equals(job_stat) || "図面出力指示".equals(job_name) && job_id != null && "0".equals(job_stat)) {
					//作業中で画面表示
					irai_List.add(new Request_RefElement(job_id, job_stat, job_name, gouki, genzu, start, end, busuu, syuku, size, printer, messege, messege1, exist, seq, rowNo));
				} else if ("図面登録依頼".equals(job_name) && job_id != null && "1".equals(job_stat) || "図面登録依頼".equals(job_name) && job_id != null && "2".equals(job_stat)
						|| "図面出力指示".equals(job_name) && job_id != null && "1".equals(job_stat) || "図面出力指示".equals(job_name) && job_id != null && "2".equals(job_stat)) {
					//完了で画面表示
					iraiend_List.add(new Request_RefElement(job_id, job_stat, job_name, gouki, genzu, start, end, busuu, syuku, size, printer, messege, messege1, exist, seq, rowNo));
				}
			}

			//原図借用依頼、図面以外焼付依頼のデータを取得する
			stmt1 = conn.createStatement();
			String strSql1 = "select JOB_ID,ROW_NO,JOB_NAME,START_NO,END_NO,GOUKI_NO,CONTENT,JOB_STAT,COPIES,SCALE_MODE," +
					"SCALE_SIZE,PRINTER_ID,MESSAGE from JOB_REQUEST_TABLE where USER_ID = '" + user.getId() + "' order by JOB_STAT, JOB_ID, ROW_NO";
			//category.debug("sql = " + strSql);
			rs1 = stmt.executeQuery(strSql1);
			while (rs1.next()) {
				String job_id = rs1.getString("JOB_ID");//依頼ID
				String job_stat = rs1.getString("JOB_STAT");//状態
				String job_name = rs1.getString("JOB_NAME");//依頼内容
				String gouki = rs1.getString("GOUKI_NO");//号口・号機
				String genzu = rs1.getString("CONTENT");//原図内容
				String start = rs1.getString("START_NO");//番号(開始)
				// 原図借用依頼、図面以外焼付依頼では図番を整形しない。
				// '04.Jul.27変更 by Hirata
				/*if(start != null){
				  //開始番号、終了番号が11桁の場合は「-」を付ける
				  start = DrasapUtil.formatDrwgNo(start);
				}*/
				String end = rs1.getString("END_NO");//番号(終了)
				// 原図借用依頼、図面以外焼付依頼では図番を整形しない。
				// '04.Jul.27変更 by Hirata
				/* if(end != null){
					  //開始番号、終了番号が11桁の場合は「-」を付ける
					  end = DrasapUtil.formatDrwgNo(end);
				  }*/
				String busuu = rs1.getString("COPIES");//部数
				String syuku = rs1.getString("SCALE_MODE");//縮小
				String size = rs1.getString("SCALE_SIZE");//サイズ
				String printer = rs1.getString("PRINTER_ID");//出力先
				String messege1 = rs1.getString("MESSAGE");//メッセージ(図面登録依頼、図面出力指示)
				String exist = "";//登録有無
				String messege = "";//メッセージ(原図借用依頼、図面以外焼付依頼)
				String seq = "";//シーケンス番号
				String rowNo = rs1.getString("ROW_NO");//行番号

				if ("原図借用依頼".equals(job_name) && "0".equals(job_stat) || "図面以外焼付依頼".equals(job_name) && "0".equals(job_stat)) {
					//作業中で画面表示
					irai_List.add(new Request_RefElement(job_id, job_stat, job_name, gouki, genzu, start, end, busuu, syuku, size, printer, messege, messege1, exist, seq, rowNo));
				} else if ("原図借用依頼".equals(job_name) && "1".equals(job_stat) || "原図借用依頼".equals(job_name) && "2".equals(job_stat)
						|| "図面以外焼付依頼".equals(job_name) && "1".equals(job_stat) || "図面以外焼付依頼".equals(job_name) && "2".equals(job_stat)) {
					//完了で画面表示
					iraiend_List.add(new Request_RefElement(job_id, job_stat, job_name, gouki, genzu, start, end, busuu, syuku, size, printer, messege, messege1, exist, seq, rowNo));
				}
			}
			//作業中のデータの後ろに完了したものを追加する
			request_refForm.irai_List = irai_List;
			request_refForm.irai_List.addAll(iraiend_List);
		} catch (java.sql.SQLException e) {
			request_refForm.listErrors.add(MessageManager.getInstance().getMessage("msg.sql.error", e.getMessage()));
			//for システム管理者
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			category.error("[" + classId + "]:原図庫作業依頼テーブルの処理に失敗しました\n" + ErrorUtility.error2String(e));
		} finally {
			try {// CLOSE処理
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
	 * 依頼区分が図面登録依頼、図面出力指示のもののデータを原図庫作業者からのメッセージに表示させる
	 *
	 */
	public void doTenkai(User user, Request_RefForm request_refForm, String job_id, String rowNo) throws Exception {
		ArrayList<Request_RefElement> irai_List = new ArrayList<>();
		ArrayList<Request_RefElement> iraiList = new ArrayList<>();
		java.sql.Connection conn = null;
		java.sql.Statement stmt = null;//データー存在チェック用コネクションステートメント
		java.sql.ResultSet rs = null;
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(true);// 非トランザクション
			stmt = conn.createStatement();

			String strSql = "select JOB_REQUEST_EXPAND_TABLE.SEQ_NO,JOB_REQUEST_EXPAND_TABLE.JOB_ID,JOB_NAME,EXPAND_DRWG_NO,GOUKI_NO,CONTENT,JOB_REQUEST_EXPAND_TABLE.MESSAGE,EXIST,SEQ_NO,JOB_REQUEST_EXPAND_TABLE.ROW_NO" +
					" from JOB_REQUEST_EXPAND_TABLE,JOB_REQUEST_TABLE" +
					" where JOB_REQUEST_EXPAND_TABLE.JOB_ID = JOB_REQUEST_TABLE.JOB_ID(+) and JOB_REQUEST_EXPAND_TABLE.JOB_ID = '" + job_id + "'" +
					" and JOB_REQUEST_EXPAND_TABLE.ROW_NO = '" + rowNo + "' order by JOB_REQUEST_EXPAND_TABLE.SEQ_NO";
			//category.debug("sql(button_Mtenkai) = " + strSql);
			rs = stmt.executeQuery(strSql);
			while (rs.next()) {
				String job_name = rs.getString("JOB_NAME");//依頼内容
				String start = rs.getString("EXPAND_DRWG_NO");//番号
				//開始番号、終了番号が11桁の場合は「-」を付ける
				start = DrasapUtil.formatDrwgNo(start);
				String gouki = rs.getString("GOUKI_NO");//号口・号機
				if (gouki == null) {
					gouki = "";
				}
				String genzu = rs.getString("CONTENT");//原図内容
				if (genzu == null) {
					genzu = "";
				}
				String message = rs.getString("MESSAGE");//メッセージ
				if (message == null) {
					message = "";
				}
				String exist = rs.getString("EXIST");//登録有無
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
			//原図庫作業者からのメッセージ画面のデータを表示する
			request_refForm.iraiList = iraiList;

		} catch (java.sql.SQLException e) {
			request_refForm.listErrors.add(MessageManager.getInstance().getMessage("msg.sql.error", e.getMessage()));
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			category.error("[" + classId + "]:原図庫作業依頼テーブルの処理に失敗しました\n" + ErrorUtility.error2String(e));
		} finally {
			try {// CLOSE処理
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
	 * 依頼内容が原図借用依頼、図面以外焼付依頼のもののデータを原図庫作業者からのメッセージに表示させる
	 *
	 */
	public void doSagyo(User user, Request_RefForm request_refForm, String job_id, String rowNo) throws Exception {
		ArrayList<Request_RefElement> iraiList = new ArrayList<>();
		java.sql.Connection conn = null;
		java.sql.Statement stmt = null;//データー存在チェック用コネクションステートメント
		java.sql.ResultSet rs = null;
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(true);// 非トランザクション
			stmt = conn.createStatement();
			String strSql = "select JOB_ID,ROW_NO,JOB_NAME,START_NO,END_NO,GOUKI_NO,CONTENT,MESSAGE,JOB_STAT" +
					" from JOB_REQUEST_TABLE where JOB_ID = '" + job_id + "' and ROW_NO = '" + rowNo + "'";
			//category.debug("sql(button_Msagyo) = " + strSql);
			rs = stmt.executeQuery(strSql);

			if (rs.next()) {
				//				String id = rs.getString("JOB_ID");//依頼ID
				String job_name = rs.getString("JOB_NAME");//依頼内容
				String start = rs.getString("START_NO");//開始番号
				if (start == null) {
					start = "";
				}
				// '04.Aug.3バグ修正
				// 開始番号、終了番号が11桁の場合は「-」を付ける
				// start = DrasapUtil.formatDrwgNo(start);
				//				String end_No = rs.getString("END_NO");//終了番号
				String gouki = rs.getString("GOUKI_NO");//号口・号機
				if (gouki == null) {
					gouki = "";
				}
				String genzu = rs.getString("CONTENT");//原図内容
				if (genzu == null) {
					genzu = "";
				}
				String message = rs.getString("MESSAGE");//メッセージ
				if (message == null) {
					message = "";
				}
				String exist = rs.getString("JOB_STAT");//作業ステータス
				if (exist == null || "0".equals(exist)) {
					exist = "";
				}
				if ("2".equals(exist)) {
					exist = "0";
				}

				iraiList.add(new Request_RefElement(job_name, gouki, genzu, start, message, exist));
			}
			//原図庫作業者からのメッセージ画面のデータを表示する
			request_refForm.iraiList = iraiList;

		} catch (java.sql.SQLException e) {
			request_refForm.listErrors.add(MessageManager.getInstance().getMessage("msg.sql.error", e.getMessage()));
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			category.error("[" + classId + "]:原図庫作業依頼テーブルの処理に失敗しました\n" + ErrorUtility.error2String(e));
		} finally {
			try {// CLOSE処理
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
