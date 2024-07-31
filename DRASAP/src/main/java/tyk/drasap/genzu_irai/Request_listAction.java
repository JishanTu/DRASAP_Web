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
 * 原図庫作業依頼リストを表示する。
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
		category.debug("Request_listAction スタート");
		request_listForm.listErrors = new ArrayList<String>();// エラーの初期化
		ArrayList<Request_listElement> iraiList = new ArrayList<Request_listElement>();//原図庫作業依頼リストのデータを表示するArrayList
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		if (user == null) {
			return "timeout";
		}
		try {
			// クラスID取得
			classId = DrasapPropertiesFactory.getDrasapProperties(this).getProperty("tyk.drasap.genzu_irai.Request_listAction");
		} catch (Exception e) {
			category.error("プロパティファイルの読込みに失敗しました\n" + ErrorUtility.error2String(e));
		}
		category.debug("アクション = " + request_listForm.action);
		if ("".equals(request_listForm.action) || request_listForm.action == null) {
			//初期データを取得する
			request_listForm.iraiList = getData_Search(request_listForm, iraiList, user, classId);
			setSelectList(request_listForm);

		} else if ("button_update".equals(request_listForm.action)) {
			//完了情報登録をする
			Connection conn = null;
			try {
				conn = ds.getConnection();
				//必ずコミットを使用し、分離レベルはRepeatable Read以上で
				conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
				// トランザクション分離レベルはRepeatable Read
				conn.setAutoCommit(false); // 自動コミットをオフに

				//原図庫作業依頼リストに表示する
				Request_listElement[] items = request_listForm.iraiList.toArray(new Request_listElement[0]);
				int countKey = 0;
				//				int totalCount = 0;//全て登録済みかどうかの確認用
				//				boolean flag = true;
				// 依頼リスト1行ごとの処理ルーチン
				for (int i = 0; i < items.length; i++) {
					String seq = items[i].getSeq();//シーケンス番号
					String id = items[i].getJob_id();//依頼ID
					//					String zikan = items[i].getZikan();//時間
					String irai = items[i].getIrai();//依頼内容
					//					String zuban = items[i].getZuban();//番号
					//					String gouki = items[i].getGouki();//号口・号機
					//					String genzu = items[i].getGenzu();//原図内容
					//					String busuu = items[i].getBusuu();//部数
					//					String syukusyou = items[i].getSyukusyou();//縮小
					//					String size = items[i].getSize();//サイズ
					//					String user_name = items[i].getUser_name();//ユーザー名
					//					String busyo_name = items[i].getBusyo_name();//部署名
					String message = items[i].getMessage();//メッセージ
					if (message == null) {
						message = "";
					}
					String hiddenMessage = items[i].getHiddenMessage();//メッセージ(更新する場合にメッセージの内容が変わったかどうかをみる)
					if (hiddenMessage == null) {
						hiddenMessage = "";
					}
					String rowNo = items[i].getRowNo();//行番号
					String touroku = items[i].getTouroku();//登録有無(チェック項目)
					String hiddentouroku = items[i].getHiddenTouroku();//登録有無(チェック項目、更新する場合にメッセージの内容が変わったかどうかをみる)
					if (hiddentouroku == null || "0".equals(hiddentouroku)) {
						hiddentouroku = "";
					}
					if ("図面登録依頼".equals(irai) || "図面出力指示".equals(irai)) {
						// 図面登録依頼または図面出力指示のとき /////////////////////////////
						if (touroku == null || "0".equals(touroku)) {
							//チェックが「完了」なら1「原紙無」なら0で登録をする
							touroku = "";
						} else if ("2".equals(touroku)) {
							touroku = "0";
						}
						if (seq != null) {
							if (!message.equals(hiddenMessage) || !touroku.equals(hiddentouroku)) {
								//更新をする
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

						//同じジョブID,行番号,の展開したデータが全て処理済(<>null)なら原図庫作業依頼テーブルを更新する
						if (isFinishedRowdata(conn, request_listForm, user, id, rowNo)) {
							String status = "";
							if ("図面出力指示".equals(irai)) {// 図面出力指示なら作業ステータスをSETにする
								status = "SET";
							}
							doUpdate_status(conn, request_listForm, user, id, rowNo, status);
							//                            flag = true;
							//                            totalCount = 0;
							countKey = 0;
						}

					} else {
						// 原図借用依頼または図面以外焼付依頼のとき /////////////////////////////
						if (touroku == null || "0".equals(touroku)) {
							touroku = "";
						}
						//原図借用依頼、図面以外焼付依頼の更新
						if (!message.equals(hiddenMessage) || !touroku.equals(hiddentouroku)) {
							if ("".equals(touroku)) {//チェックが「完了」なら1「原紙無」なら2を登録する
								touroku = "0";
							}
							//更新をする
							doUpdate_irai(conn, request_listForm, user, touroku, message, id, rowNo);
						}

					}
				}

				if (request_listForm.listErrors.isEmpty()) {
					category.debug("コミット");
					conn.commit(); // コミット
					//アクセスログを出力
					AccessLoger.loging(user, AccessLoger.FID_GENZ_RES);
					//最新情報を表示する
					request_listForm.iraiList = new ArrayList<Request_listElement>();
					request_listForm.iraiList = getData_Search(request_listForm, iraiList, user, classId);
					if (request_listForm.listErrors.isEmpty()) {
						setSelectList(request_listForm);
						category.debug("依頼登録をしました");
						request_listForm.listErrors.add(MessageManager.getInstance().getMessage("msg.irai.touroku"));
					}
				} else if (conn != null) {
					category.debug("ロールバック");
					request_listForm.listErrors.add(MessageManager.getInstance().getMessage("msg.irai.touroku.error"));
					conn.rollback();
					conn.setAutoCommit(true); // 自動コミットをオンに
				}
			} catch (Exception e) {
				request_listForm.listErrors.add(MessageManager.getInstance().getMessage("msg.irai.unexpected", e.getMessage()));
				category.error("予想外の例外が発生しました\n" + ErrorUtility.error2String(e));
				try {
					if (conn != null) {
						category.debug("ロールバック");
						conn.rollback();
						conn.setAutoCommit(true); // 自動コミットをオンに
					}
				} catch (java.sql.SQLException se2) {
					// SQL失敗しました。
					ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
					category.error("ロールバックに失敗しました" + se2.getMessage());
				}

			} finally {
				// CLOSE処理
				try {
					conn.setAutoCommit(true); // 自動コミットをオンに
					conn.close();
				} catch (Exception e) {
				}
			}

		} else if ("button_iraiKousin".equals(request_listForm.action)) {
			//依頼更新をクリックするとリストを最新で更新する
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
			// 作業依頼履歴ボタンが押されていたら、
			category.debug("--> history");
			return "history";
		}
		category.debug("success -->");
		return "success";
	}

	public ArrayList<Request_listElement> getData_Search(Request_listForm request_listForm, ArrayList<Request_listElement> iraiList, User user, String classId) throws Exception {

		Connection conn = null;
		java.sql.Statement stmt = null;//データー存在チェック用コネクションステートメント
		java.sql.ResultSet rs = null;
		java.sql.Statement stmt1 = null;//データー存在チェック用コネクションステートメント
		java.sql.ResultSet rs1 = null;
		ArrayList<Request_listElement> irai_List = new ArrayList<Request_listElement>();
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(true);// 非トランザクション
			//依頼内容が図面登録依頼または図面出力指示のデータを取得する
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
				String seq = rs.getString("SEQ_NO");//シーケンス番号
				String job_id = rs.getString("JOB_ID");//依頼ID
				String zikan = rs.getString("REQUEST_DATE");//時間
				String irai = rs.getString("JOB_NAME");//依頼内容
				String zuban = rs.getString("EXPAND_DRWG_NO");//図番(図面登録依頼、図面出力依頼の時使用)
				//開始番号、終了番号が11桁の場合は「-」を付ける
				if (zuban != null) {
					zuban = DrasapUtil.formatDrwgNo(zuban);
				}
				String gouki = rs.getString("GOUKI_NO");//号口・号機
				String genzu = rs.getString("CONTENT");//原図内容
				String busuu = rs.getString("COPIES");//部数
				String syukusyou = rs.getString("SCALE_MODE");//縮小
				String size = rs.getString("SCALE_SIZE");//サイズ
				String user_name = rs.getString("USER_NAME");//ユーザ名
				String busyo_name = rs.getString("DEPT_NAME");//部署名
				String message = rs.getString("MESSAGE");//メッセージ(原図庫作業依頼展開テーブル, 図面登録依頼、図面出力依頼の時使用)
				String rowNo = rs.getString("ROW_NO");//行番号
				String touroku = rs.getString("EXIST");//登録有無
				//				String sagyou = rs.getString("JOB_STAT");//作業ステータス

				if ("図面登録依頼".equals(irai) && touroku == null || "図面出力指示".equals(irai) && touroku == null) {
					String hiddenMessage = message;//メッセージのhidden
					String hiddenTouroku = touroku;//登録有無のhidden
					irai_List.add(new Request_listElement(seq, job_id, zikan, irai, zuban, gouki, genzu, busuu, syukusyou, size, user_name,
							busyo_name, message, rowNo, touroku, hiddenMessage, hiddenTouroku));
				}
			}

			//依頼内容が原図借用依頼または図面以外焼付依頼のデータを取得する
			stmt1 = conn.createStatement();
			String strSql1 = "select JOB_ID,to_char(REQUEST_DATE, 'HH24:MI') as REQUEST_DATE,JOB_NAME,START_NO,GOUKI_NO," +
					"CONTENT,COPIES,SCALE_MODE,SCALE_SIZE,USER_NAME," +
					"DEPT_NAME,MESSAGE,ROW_NO,JOB_STAT" +
					" from JOB_REQUEST_TABLE order by JOB_ID,ROW_NO";
			category.debug("sql1 = " + strSql1);
			rs1 = stmt1.executeQuery(strSql1);

			while (rs1.next()) {
				String job_id = rs1.getString("JOB_ID");//依頼ID
				String zikan = rs1.getString("REQUEST_DATE");//時間
				String irai = rs1.getString("JOB_NAME");//依頼内容
				String zuban = rs1.getString("START_NO");//図番(原図借用依頼、図面以外焼付の時使用)
															// 原図借用依頼、図面以外焼付では、整形せずにそのまま出力。
															// 変更 '04/07/22 by Hirata.
				String gouki = rs1.getString("GOUKI_NO");//号口・号機
				String genzu = rs1.getString("CONTENT");//原図内容
				String busuu = rs1.getString("COPIES");//部数
				String syukusyou = rs1.getString("SCALE_MODE");//縮小
				String size = rs1.getString("SCALE_SIZE");//サイズ
				String user_name = rs1.getString("USER_NAME");//ユーザ名
				String busyo_name = rs1.getString("DEPT_NAME");//部署名
				String message = rs1.getString("MESSAGE");//メッセージ(原図庫作業依頼展開テーブル, 図面登録依頼、図面出力依頼の時使用)
				String rowNo = rs1.getString("ROW_NO");//行番号
				String touroku = rs1.getString("JOB_STAT");//作業ステータス

				if ("原図借用依頼".equals(irai) && "0".equals(touroku) || "図面以外焼付依頼".equals(irai) && "0".equals(touroku)) {
					String seq = "";//シーケンス番号
					String hiddenMessage = message;//メッセージのhidden
					String hiddenTouroku = touroku;//登録有無のhidden
					irai_List.add(new Request_listElement(seq, job_id, zikan, irai, zuban, gouki, genzu, busuu, syukusyou, size, user_name,
							busyo_name, message, rowNo, touroku, hiddenMessage, hiddenTouroku));
				}
			}

		} catch (java.sql.SQLException e) {
			request_listForm.listErrors.add(MessageManager.getInstance().getMessage("msg.irai.unexpected", e.getMessage()));
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

		//原図庫作業依頼リストの表示データを取得する
		Request_listElement[] items = irai_List.toArray(new Request_listElement[0]);
		for (int i = 0; i < items.length; i++) {
			String seq = items[i].getSeq();//シーケンス番号
			String job_id = items[i].getJob_id();//依頼ID
			String zuban = items[i].getZuban();//番号
			if (zuban == null) {
				zuban = "";
			}
			String rowNo = items[i].getRowNo();//行番号
			String zikan = items[i].getZikan();//時間
			String irai = items[i].getIrai();//依頼内容
			String gouki = items[i].getGouki();//号口・号機
			String genzu = items[i].getGenzu();//原図内容
			String busuu = items[i].getBusuu();//部数
			String syukusyou = items[i].getSyukusyou();//縮小
			String size = items[i].getSize();//サイズ
			String user_name = items[i].getUser_name();//ユーザー名
			String busyo_name = items[i].getBusyo_name();//部署名
			String message = items[i].getMessage();//メッセージ
			String hiddenMessage = items[i].getHiddenMessage();//メッセージ(更新する場合にメッセージの内容が変わったかどうかをみる)
			String touroku = items[i].getTouroku();//登録有無(チェック項目)
			String hiddenTouroku = items[i].getHiddenTouroku();//登録有無(チェック項目、更新する場合にメッセージの内容が変わったかどうかをみる)

			if (i == 0 || "図面登録依頼".equals(irai) && !job_id.equals(items[i - 1].getJob_id()) || "図面登録依頼".equals(irai) && !zuban.equals(items[i - 1].getZuban()) || "図面出力指示".equals(irai) && !zuban.equals(items[i - 1].getZuban())
					|| "図面出力指示".equals(irai) && !job_id.equals(items[i - 1].getJob_id()) || "原図借用依頼".equals(irai) || "図面以外焼付依頼".equals(irai)) {
				if ("図面登録依頼".equals(irai) || "図面出力指示".equals(irai)) {
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
							busuu = copies;//部数
							genzu = content;//原図内容
							gouki = gouguti;//号口・号機
							syukusyou = syuku;//縮小
							size = saize;//サイズ
						}

					} catch (java.sql.SQLException e) {
						request_listForm.listErrors.add(MessageManager.getInstance().getMessage("msg.sql.error", e.getMessage()));
						ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
						category.error("[" + classId + "]:原図庫作業依頼テーブルの処理に失敗しました\n" + ErrorUtility.error2String(e));
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
				iraiList.add(new Request_listElement(seq, job_id, zikan, irai, zuban, gouki, genzu, busuu, syukusyou, size, user_name,
						busyo_name, message, rowNo, touroku, hiddenMessage, hiddenTouroku));
			}
		}
		if (items.length == 0) {
			request_listForm.listErrors.add(MessageManager.getInstance().getMessage("msg.irai.nodata"));
			category.debug("データがありません");
		}

		//メッセージをプルダウンに格納する
		request_listForm.messageNameList = new ArrayList<String>();
		request_listForm.messageNameList.add("");
		try {
			stmt = conn.createStatement();
			String strSql1 = "select MODULE_ID,MESSAGE_NO,MESSAGE from MESSAGE_MASTER where MODULE_ID = '01' order by MODULE_ID,MESSAGE_NO";
			rs = stmt.executeQuery(strSql1);
			while (rs.next()) {
				//				String id = rs.getString("MODULE_ID");//モジュールID
				//				String messageNo = rs.getString("MESSAGE_NO");//メッセージ番号
				String message = rs.getString("MESSAGE");//メッセージ

				request_listForm.messageNameList.add(message);//メッセージをプルダウンに格納する
			}

		} catch (java.sql.SQLException e) {
			request_listForm.listErrors.add(MessageManager.getInstance().getMessage("msg.sql.error" + e.getMessage()));
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

		return iraiList;
	}

	/**
	 * 図面登録依頼、図面出力指示において、原図庫作業依頼展開テーブルを更新する。
	 * 変更['04.Jul.14]・・・作業者ID、作業者名、作業日時を更新する。
	 */
	public void doUpdate(Connection conn, Request_listForm request_listForm, User user, String seq, String touroku, String message, String id, String rowNo) throws Exception {

		java.sql.Statement stmt = null;
		try {
			stmt = conn.createStatement(); // データ存在チェック用コネクションステートメント
			String strSql = "update JOB_REQUEST_EXPAND_TABLE set EXIST = '" + touroku + "',MESSAGE = '" + message + "'," +
					"JOB_USER_ID='" + user.getId() + "', JOB_USER_NAME='" + user.getName() + "', JOB_DATE=SYSDATE" +
					" where SEQ_NO = '" + seq + "' and JOB_ID = '" + id + "' and ROW_NO = '" + rowNo + "'";
			category.debug("更新をする(図面登録依頼,図面出力指示) = " + strSql);
			int cnt = stmt.executeUpdate(strSql);
			category.debug("更新数 = " + Integer.toString(cnt));

		} catch (Exception e) {
			request_listForm.listErrors.add(MessageManager.getInstance().getMessage("msg.irai.failed.update", e.getMessage()));
			//for システム管理者
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
	 * 原図借用依頼,図面以外焼付での更新処理をする。
	 * 原図庫作業依頼を更新する。
	 * 変更['04.Jul.14]・・・作業者ID、作業者名を更新する。
	 */
	public void doUpdate_irai(Connection conn, Request_listForm request_listForm, User user, String touroku, String message, String id, String rowNo) throws Exception {

		java.sql.Statement stmt = null;
		//作業ステータス(JOB_STAT)が0の時は状態遷移ステータス(TRANSIT_STAT)に「SET」を入力する
		String status = "";
		if ("0".equals(touroku)) {
			status = "SET";
		}
		try {
			stmt = conn.createStatement(); // データ存在チェック用コネクションステートメント
			String strSql = "update JOB_REQUEST_TABLE set JOB_STAT = '" + touroku + "',MESSAGE = '" + message + "', JOB_DATE = sysdate, TRANSIT_STAT = '" + status + "'," +
					"JOB_USER_ID='" + user.getId() + "', JOB_USER_NAME='" + user.getName() + "'" +
					" where JOB_ID = '" + id + "' and ROW_NO = '" + rowNo + "'";
			category.debug("更新をする(原図借用依頼,図面以外焼付) = " + strSql);
			int cnt = stmt.executeUpdate(strSql);
			category.debug("更新数 = " + Integer.toString(cnt));

		} catch (Exception e) {
			request_listForm.listErrors.add(MessageManager.getInstance().getMessage("msg.irai.failed.update", e.getMessage()));
			//for システム管理者
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
	 * 図面登録依頼、図面出力指示では、展開した図番について全ての処理が終わったら、
	 * 原図庫作業依頼テーブルを更新する。
	 * また図面出力指示の場合は作業ステータスをSETで登録する。
	 * 変更['04.Jul.14]・・・作業者ID、作業者名を更新する。
	 */
	public void doUpdate_status(Connection conn, Request_listForm request_listForm, User user, String id, String rowNo, String status) throws Exception {

		java.sql.Statement stmt = null;
		try {
			stmt = conn.createStatement(); // データ存在チェック用コネクションステートメント
			String strSql = "update JOB_REQUEST_TABLE set JOB_STAT = '1', JOB_DATE = sysdate, TRANSIT_STAT = '" + status + "'," +
					"JOB_USER_ID='" + user.getId() + "', JOB_USER_NAME='" + user.getName() + "'" +
					" where JOB_ID = '" + id + "' and ROW_NO = '" + rowNo + "'";
			category.debug("JOB_REQUEST_TABLEを更新 = " + strSql);
			int cnt = stmt.executeUpdate(strSql);
			category.debug("更新数 = " + Integer.toString(cnt));

		} catch (Exception e) {
			request_listForm.listErrors.add(MessageManager.getInstance().getMessage("msg.irai.failed.update", e.getMessage()));
			//for システム管理者
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
	 * 指定したジョブID,行番号の依頼展開データが全て処理されたかを判断する。
	 * @param id ジョブID
	 * @param rowNo 行番号
	 * @return 全て処理されていれば true
	 */
	private boolean isFinishedRowdata(Connection conn, Request_listForm request_listForm, User user,
			String id, String rowNo) throws Exception {

		java.sql.Statement stmt = null;
		java.sql.ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			// 指定したジョブID,行番号で EXIST=null の件数を数える
			String strSql = "select count(*) CNT" +
					" from JOB_REQUEST_EXPAND_TABLE" +
					" where JOB_ID = '" + id + "' and ROW_NO = '" + rowNo + "'" +
					" and EXIST is null";
			rs = stmt.executeQuery(strSql);
			rs.next();
			// 件数=0なら true
			return rs.getInt("CNT") == 0;

		} catch (Exception e) {

			request_listForm.listErrors.add(MessageManager.getInstance().getMessage("msg.sql.error", e.getMessage()));
			//for システム管理者
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));

			throw e;

		} finally {
			// CLOSE処理
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

	/* フォームのセレクト用ArrayListにデータを格納する。
	 */
	public static void setSelectList(Request_listForm request_listForm) throws Exception {

		// チェック項目のキー
		request_listForm.checkKeyList = new ArrayList<String>();
		request_listForm.checkKeyList.add("0");
		request_listForm.checkKeyList.add("1");
		request_listForm.checkKeyList.add("2");

		// チェック項目のネーム
		request_listForm.checkNameList = new ArrayList<String>();
		request_listForm.checkNameList.add("");
		request_listForm.checkNameList.add("完了");
		request_listForm.checkNameList.add("原紙無");

	}

}
