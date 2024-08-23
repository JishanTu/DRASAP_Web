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
 * 原図庫作業依頼のAction
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
			category.error("DataSourceの取得に失敗\n" + ErrorUtility.error2String(e));
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
		category.debug("RequestAction スタート");
		ActionErrors errors = new ActionErrors();//エラー内容を表示する
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		if(user == null){
			return mapping.findForward("timeout");
		}

		try{
			// クラスID取得
			classId = DrasapPropertiesFactory.getDrasapProperties(this).getProperty("tyk.drasap.genzu_irai.RequestAction");
		}catch(java.lang.Exception e){
			category.error("プロパティファイルの読込みに失敗しました\n" + ErrorUtility.error2String(e));
		}
		category.debug("action = " + requestForm.action);

		if("button_irai".equals(requestForm.action)){ //原図庫作業依頼をする
			if("".equals(requestForm.irai)){
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.irai.required"));//依頼区分を選択してください
				saveErrors(request, errors);
				return(mapping.findForward("success"));
			}
			//データのチェック
			CheckData(requestForm, form, errors, request);
			if(errors.isEmpty()){
				String irai = "";
				if("図面登録依頼".equals(requestForm.irai)){
					irai = "A";
				}else if("図面出力指示".equals(requestForm.irai)){
					irai = "B";
				}else if("原図借用依頼".equals(requestForm.irai)){
					irai = "C";
				}else if("図面以外焼付依頼".equals(requestForm.irai)){
					irai = "D";
				}

				String zumenAll = "0";//すべての図番が登録済みかどうかのチェック。1の場合はすべて登録済み
				java.sql.Connection conn= null;
				conn = ds.getConnection();
				String job_Id =JobReqSeqDB.getJobId(irai, conn);//原図庫作業依頼でのジョブIDを取得する
				conn.close();
				conn = null;
				java.util.Calendar calendar = java.util.Calendar.getInstance();
				String day = new java.text.SimpleDateFormat("yyyy/MM/dd").format(calendar.getTime());//本日の日付
				try{
					conn = ds.getConnection();
					//必ずコミットを使用し、分離レベルはRepeatable Read以上で
					conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
					// トランザクション分離レベルはRepeatable Read
					conn.setAutoCommit(false);		// 自動コミットをオフに

					// 図面出力指示のとき
					// 出力先が専用プリンタならプリンタスタンプ,EUCならEUCプリンタスタンプを
					// userから取得してtrueであればスタンプフラグを「1」にする
					if("B".equals(irai)){
						// スタンプフラグを初期化する
						requestForm.printer_flag = "0";

						// 指定されたプリンタをユーザーの使用可能なプリンタから取得する
						for(int i = 0; i < user.getEnablePrinters().size(); i++){
							Printer printer = (Printer)user.getEnablePrinters().get(i);
							if(printer.getId().equals(requestForm.syutu)){// プリンタを見つけた
								if(printer.isEucPrinter()){
									// 指定したプリンタがEUCなら
									if(user.isEucStamp()){
										requestForm.printer_flag = "1";
									} else {
										requestForm.printer_flag = "0";
									}
								} else {
									// 専用プリンタなら
									if(user.isPltrStamp()){
										requestForm.printer_flag = "1";
									} else {
										requestForm.printer_flag = "0";
									}
								}
								break;// forループを抜ける
							}
						}
					}else{
						requestForm.printer_flag = "0";
						requestForm.syutu = "";//出力先
					}

					if("on".equals(requestForm.hiddenNo1)){ //1行目の内容を依頼追加する
						String kaisiNo = requestForm.kaisiNo1;//開始番号1
						String syuryoNo = requestForm.syuuryouNo1;//終了番号1
						if("A".equals(irai) || "B".equals(irai)){//図面登録依頼、図面出力指示の場合は図面を展開することができる
							String gyoNo = "1";//行
							doSitei(requestForm,errors,user,irai,request,kaisiNo,syuryoNo,zumenAll,gyoNo, job_Id,conn);
							zumenAll = requestForm.zumenAll;//展開した図面が全て登録済みの場合は作業完了のため「1」
						}
						if(errors.isEmpty()){
							if("".equals(kaisiNo)){//開始番号がなく、終了番号のみの場合は開始番号に登録する
								kaisiNo = syuryoNo;
								syuryoNo = "";
							}
							doInsert1(requestForm, errors, user, irai, zumenAll, kaisiNo, syuryoNo,job_Id, day,conn);
						}
					}
					if("on".equals(requestForm.hiddenNo2)){//2行目の内容を依頼登録する
						String kaisiNo = requestForm.kaisiNo2;//開始番号2
						String syuryoNo = requestForm.syuuryouNo2;//終了番号2
						if("A".equals(irai) || "B".equals(irai)){//図面登録依頼、図面出力指示の場合は図面を展開することができる
							String gyoNo = "2";//行
							doSitei(requestForm,errors,user,irai,request,kaisiNo,syuryoNo,zumenAll,gyoNo,job_Id,conn);
							zumenAll = requestForm.zumenAll;//展開した図面が全て登録済みの場合は作業完了のため「1」
						}
						if(errors.isEmpty()){
							if("".equals(kaisiNo)){//開始番号がなく終了番号のみの場合は開始番号に登録する
								kaisiNo = syuryoNo;
								syuryoNo = "";
							}
							doInsert2(requestForm, errors, user, irai, zumenAll, kaisiNo, syuryoNo, job_Id, day, conn);
						}
					}
					if("on".equals(requestForm.hiddenNo3)){ //3行目の内容を依頼登録する
						String kaisiNo = requestForm.kaisiNo3;//開始番号3
						String syuryoNo = requestForm.syuuryouNo3;//終了番号3
						if("A".equals(irai) || "B".equals(irai)){//図面登録依頼、図面出力指示の場合は図面を展開することができる
							String gyoNo = "3";//行
							doSitei(requestForm,errors,user,irai,request,kaisiNo,syuryoNo,zumenAll,gyoNo,job_Id,conn);
							zumenAll = requestForm.zumenAll;//展開した図面が全て登録済みの場合は作業完了のため「1」
						}
						if(errors.isEmpty()){
							if("".equals(kaisiNo)){//開始番号がなく終了番号のみの場合は開始番号に登録する
								kaisiNo = syuryoNo;
								syuryoNo = "";
							}
							doInsert3(requestForm, errors, user, irai, zumenAll, kaisiNo, syuryoNo, job_Id, day, conn);
						}
					}
					if("on".equals(requestForm.hiddenNo4)){//4行目の内容を依頼登録する
						String kaisiNo = requestForm.kaisiNo4;//開始番号4
						String syuryoNo = requestForm.syuuryouNo4;//終了番号4
						if("A".equals(irai) || "B".equals(irai)){//図面登録依頼、図面出力指示の場合は図面を展開することができる
							String gyoNo = "4";//行
							doSitei(requestForm,errors,user,irai,request,kaisiNo,syuryoNo,zumenAll,gyoNo,job_Id,conn);
							zumenAll = requestForm.zumenAll;//展開した図面が全て登録済みの場合は作業完了のため「1」
						}
						if(errors.isEmpty()){
							if("".equals(kaisiNo)){//開始番号がなく終了番号のみの場合は開始番号に登録する
								kaisiNo = syuryoNo;
								syuryoNo = "";
							}
							doInsert4(requestForm, errors, user, irai, zumenAll, kaisiNo, syuryoNo, job_Id, day, conn);
						}
					}
					if("on".equals(requestForm.hiddenNo5)){//5行目の内容を依頼登録する
						String kaisiNo = requestForm.kaisiNo5;//開始番号5
						String syuryoNo = requestForm.syuuryouNo5;//終了番号5
						if("A".equals(irai) || "B".equals(irai)){//図面登録依頼、図面出力指示の場合は図面を展開することができる
							String gyoNo = "5";//行
							doSitei(requestForm,errors,user,irai,request,kaisiNo,syuryoNo,zumenAll,gyoNo,job_Id,conn);
							zumenAll = requestForm.zumenAll;//展開した図面が全て登録済みの場合は作業完了のため「1」
						}
						if(errors.isEmpty()){
							if("".equals(kaisiNo)){//開始番号がなく終了番号のみの場合は開始番号に登録する
								kaisiNo = syuryoNo;
								syuryoNo = "";
							}
							doInsert5(requestForm, errors, user, irai, zumenAll, kaisiNo, syuryoNo,job_Id, day, conn);
						}
					}
					if(errors.isEmpty()){
						if("on".equals(requestForm.hiddenNo1) || "on".equals(requestForm.hiddenNo2) || "on".equals(requestForm.hiddenNo3) || "on".equals(requestForm.hiddenNo4) || "on".equals(requestForm.hiddenNo5)){
							category.debug("コミット");
							conn.commit();	// コミット
							//アクセスログを取得
							AccessLoger.loging(user, AccessLoger.FID_GENZ_REQ);
							category.debug("依頼登録をしました");
							errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("touroku.irai"));
							saveErrors(request, errors);
						}
					}else{
						if(conn != null){
							category.debug("ロールバック");
							conn.rollback();
							conn.setAutoCommit(true);		// 自動コミットをオンに
						}
					}

				}catch(Exception e){
					errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.outer.exception", e.getMessage()));
					saveErrors(request, errors);
					category.error("予想外の例外が発生しました\n" + ErrorUtility.error2String(e));
					try{
						if(conn != null){
							category.debug("ロールバック");
							conn.rollback();
							conn.setAutoCommit(true);		// 自動コミットをオンに
						}
					}catch(java.sql.SQLException se2){
						// SQL失敗しました。
						errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(classId, se2.getMessage()));
						category.error("ロールバックに失敗しました" + ErrorUtility.error2String(se2));
					}

				}finally{
					// CLOSE処理
					try{
						conn.setAutoCommit(true);		// 自動コミットをオンに
						conn.close();
					}catch (Exception e){}
				}
			}

		}else{
			// 初期データの取得
			setFormData(requestForm, user, errors);
		}

		return(mapping.findForward("success"));
	}

	/**
	 * フォームのプルダウン内容をセットする
	 * @param requestForm
	 * @param user
	 * @param errors
	 */
	private void setFormData(RequestForm requestForm, User user, ActionErrors errors){
		//依頼内容をセットする
		requestForm.iraiList = new ArrayList();//依頼内容
		requestForm.iraiList.add("");
		if(user.isReqImport()){
			requestForm.iraiList.add("図面登録依頼");
		}
		//図面出力指示機能が削除されたため、メニューに表示しない 2018/03/28
		//if(user.isReqPrint()){
		//	requestForm.iraiList.add("図面出力指示");
		//}
		if(user.isReqCheckout()){
			requestForm.iraiList.add("原図借用依頼");
		}
		if(user.isReqOther()){
			requestForm.iraiList.add("図面以外焼付依頼");
		}

		//出力先をセットする
		requestForm.list = new ArrayList();
		for(int i = 0; i < user.getEnablePrinters().size(); i ++){
			tyk.drasap.common.Printer e = (tyk.drasap.common.Printer)user.getEnablePrinters().get(i);
			if(e.isDisplay()){
				String id = e.getId();//プリンタID
				String name = e.getDisplayName();//表示名
				requestForm.list.add(new RequestElement(id, name));
			}
		}

		//原図内容をセットする
		requestForm.genzuNameList = new ArrayList();
		requestForm.genzuNameList.add("");
		// 項目を全面的に見直し by hirata '04.Jul.19
		requestForm.genzuNameList.add("仕様書");
		requestForm.genzuNameList.add("製作明細");
		requestForm.genzuNameList.add("部品明細");
		requestForm.genzuNameList.add("メカ関係一式");
		requestForm.genzuNameList.add("電気回路図以外一式");
		requestForm.genzuNameList.add("電気回路図");
		requestForm.genzuNameList.add("承認図");

		//縮小のコンボ
		requestForm.syukusyouList = new ArrayList();
		requestForm.syukusyouList.add("0");
		requestForm.syukusyouList.add("1");

		//サイズのコンボ
		requestForm.saizuList = new ArrayList();
		requestForm.saizuList.add("");
		requestForm.saizuList.add("A1");
		requestForm.saizuList.add("A2");
		requestForm.saizuList.add("A3");
		requestForm.saizuList.add("A4");
		// 図面出力指示における、サイズの追加 '04.Oct.14 by Hirata
		requestForm.saizuList.add("70.7");
		requestForm.saizuList.add("50");
		requestForm.saizuList.add("35.4");
		requestForm.saizuList.add("25");
	}

/*
 * 依頼内容が図面登録依頼または図面出力指示の場合、図面を展開する。
 *
 */
	private void doSitei(RequestForm requestForm, ActionErrors errors, User user, String irai, HttpServletRequest request, String kaisiNo, String syuryoNo, String zumenAll, String gyoNo, String job_Id, Connection conn) throws Exception{

		requestForm.zumenAll = "0";//図面展開した時にすべて登録済みであれば1
		//開始番号、終了番号のどちらかが入力されている場合は必ず開始番号に登録する
		if("".equals(kaisiNo) || "".equals(syuryoNo)){
			String kai_syuNo = "";//開始番号
			if("".equals(syuryoNo)){
				kai_syuNo = kaisiNo;
			}else{
				kai_syuNo = syuryoNo;
			}
			boolean lenFlag = true;//開始番号が12桁で末尾に??があるかフラグをたてる
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

				java.sql.Statement stmt = null;//データー存在チェック用コネクションステートメント
				java.sql.ResultSet rs = null;
				String touroku = "";//登録有無
				ArrayList zubanList = new ArrayList();
				int count = 0;//最新図番があるか
				try{
					stmt = conn.createStatement();
					String strSql ="select DRWG_NO,LATEST_FLAG from INDEX_DB where DRWG_NO like '"+kai_syuNo+"%' and LATEST_FLAG = '1'";
					rs = stmt.executeQuery(strSql);

					while(rs.next()){
						String drwg_no = rs.getString("DRWG_NO");//図番
						String flag = rs.getString("LATEST_FLAG");//最新図番かどうか
						zumenAll = "1";
						requestForm.zumenAll = "1";//図番は登録済み
						touroku = "1";//登録済み
						count++;
						zubanList.add(new RequestResultElement(drwg_no, flag, touroku));
					}
					if(count == 0){//最新図番がない
						String drwg_no = "";//図番
						if(lenFlag == false){//図番に??がある
							drwg_no = kai_syuNo + "??";
							drwg_no = drwg_no.substring(0,12);
						}else{
							if(kai_syuNo.length() == 12){
								drwg_no = kai_syuNo;
							}else{
								drwg_no = kai_syuNo + "?";
								drwg_no = drwg_no.substring(0,11);//図番に?がある
							}
						}
						String flag = "";//最新図番でない
						touroku = "";//登録されてない
						zumenAll = "0";
						requestForm.zumenAll = "0";//図番の登録はない
						zubanList.add(new RequestResultElement(drwg_no, flag, touroku));
					}

				}catch(java.sql.SQLException e){
					//for システム管理者
					ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
					errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(classId, e.getMessage()));
					category.error("[" + classId + "]:原図庫作業依頼の処理に失敗しました\n" + ErrorUtility.error2String(e));
					throw new Exception(e.getMessage());
				}finally{
					try{// CLOSE処理
						stmt.close();stmt = null;
						rs.close();rs = null;
					}catch (Exception e){}
				}

				RequestResultElement[] items = (RequestResultElement[])zubanList.toArray(new RequestResultElement[0]);
				String seq = "";//シーケンス番号
				int r = 0;
				for(int i = 0; i < items.length; i++){
					String drwg_no = items[i].getDrwgNo();//図番
					if(!"?".equals(drwg_no.substring(10,11))){//開始番号の図番が属性情報テーブルに存在するか
						doHani_s(requestForm, errors, conn, drwg_no, user);
					}
					String tourokuNo = items[i].getTouroku();//登録有無
					if("1".equals(requestForm.hani_s)){//開始番号が登録済みであれば1
						tourokuNo = "1";
						requestForm.zumenAll = "1";
					}
					//シーケンス番号を取得する
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

					//原図庫作業依頼展開テーブルに追加する
					doInsert(conn, seq, job_Id, gyoNo, drwg_no, tourokuNo, user, errors);
					requestForm.hani_s = "";//開始番号
				}

			}else{
				category.debug("最新設変Noの指定は11桁,12桁のみです");
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.hani_leng_l.check"));
				saveErrors(request, errors);
			}

		}else if(!"".equals(kaisiNo) && !"".equals(syuryoNo)){
			//開始番号と終了番号が入力されている
			if(errors.isEmpty()){
				if(kaisiNo.substring(0,10).equals(syuryoNo.substring(0,10))){
					//開始、終了番号の範囲指定(10桁までOK)
					category.debug("*** 開始終了の範囲指定(10桁までOK)");
					int kai = kaisiNo.charAt(10);//開始番号
					int syu = syuryoNo.charAt(10);//終了番号
					//開始番号<終了番号でなければならない
					if(kai < syu && !"?".equals(kaisiNo.substring(10,11)) && !"?".equals(syuryoNo.substring(10,11))){
						java.sql.Statement stmt = null;//データー存在チェック用コネクションステートメント
						java.sql.ResultSet rs = null;
						String hani = kaisiNo.substring(0,10);//10桁までの図番で最新図番があるかを検索する為に10桁にする
						String touroku = "";//登録有無
						ArrayList zubanList = new ArrayList();//図番があれば格納する
						try{
							stmt = conn.createStatement();
							String strSql ="select DRWG_NO,LATEST_FLAG from INDEX_DB where DRWG_NO like '"+hani+"%'";
							rs = stmt.executeQuery(strSql);

							while(rs.next()){
								String drwgNo = rs.getString("DRWG_NO");//図番
								String flag = rs.getString("LATEST_FLAG");//最新図番かどうか
								if("1".equals(flag)){
									touroku = "1";
								}
								zubanList.add(new RequestResultElement(drwgNo,flag,touroku));
							}

						}catch(java.sql.SQLException e){
							//for システム管理者
						    ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
							errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(classId, e.getMessage()));
							category.error("[" + classId + "]:原図庫作業依頼の処理に失敗しました\n" + ErrorUtility.error2String(e));
							throw new Exception(e.getMessage());
						}finally{
							try{// CLOSE処理
								stmt.close();stmt = null;
								rs.close();rs = null;
							}catch (Exception e){}
						}

						RequestResultElement[] items = (RequestResultElement[])zubanList.toArray(new RequestResultElement[0]);
						int r = 0;
						int countt = 0;
						char fro = kaisiNo.substring(10,11).charAt(0);//開始番号の末尾
						char to = syuryoNo.substring(10,11).charAt(0);//終了番号の末尾
						char[] oiban = DrasapUtil.createOibanArray(fro, to);//追番を取得

						String seq = "";//シーケンス番号
						for(int i = 0; i < oiban.length; i++){
							char oiban1 = oiban[i];//追番
							String oi = kaisiNo.substring(0,10) + oiban1;//図番
							boolean flagName = true;//図番が登録済みで追加したかのチェック
							for(int j = 0; j < items.length; j++){
								String drwg_no = items[j].getDrwgNo();
								if(oi.equals(drwg_no)){ //同じ図番があった場合に開始、終了の範囲内であれば登録有無を「1」にする
									int drwgNo_n = drwg_no.charAt(10);
									if(kai <= drwgNo_n && drwgNo_n <= syu){ //図番が開始、終了番号の範囲の中のものかどうか
										String tourokuNo = "1";//登録有無
										//シーケンス番号を取得する
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

										//原図庫作業依頼展開テーブルに追加する
										doInsert(conn, seq, job_Id, gyoNo, drwg_no, tourokuNo, user, errors);
									}
									flagName = false;
									break;
								}
							}

							if(flagName == true){ //登録のない図番を原図庫作業依頼展開テーブルに追加する
								hani = oi;//番号
								//シーケンス番号を取得する
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
								//原図庫作業依頼展開テーブルに追加する
								doNew_Insert(conn, seq, job_Id, gyoNo, hani, hanisitei, user, errors);
								countt++;
							}
						}

						if(countt == 0){
							requestForm.zumenAll = "1";//展開したデータが全て登録済み
						}
					}else{
						category.debug("必ず開始番号 < 終了番号で指定してください");
						errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.hanisitei_s.required"));//必ず開始番号 < 終了番号で指定してください
						saveErrors(request, errors);
					}

				}else if(kaisiNo.substring(0,9).equals(syuryoNo.substring(0,9))){
					//開始、終了番号の範囲指定(9桁までOK)
					category.debug("*** 開始終了の範囲指定(9桁までOK) *****");
					int kai = kaisiNo.charAt(9);//開始
					int syu = syuryoNo.charAt(9);//終了

					if(kai < syu && !"??".equals(kaisiNo.substring(9,11)) && !"??".equals(syuryoNo.substring(9,11))){
						String hani = kaisiNo.substring(0,9);//9桁までの図番

						java.sql.Statement stmt = null;//データー存在チェック用コネクションステートメント
						java.sql.ResultSet rs = null;
						String touroku = "";//登録有無
						ArrayList zubanList = new ArrayList();
						int sitei = 0;
						try{
							stmt = conn.createStatement();
							String strSql ="select DRWG_NO,LATEST_FLAG from INDEX_DB where DRWG_NO like '"+hani+"%' and LATEST_FLAG = '1'";
							rs = stmt.executeQuery(strSql);

							while(rs.next()){
								String drwgNo = rs.getString("DRWG_NO");//図番
								String flag = rs.getString("LATEST_FLAG");//最新図番かどうか
								if("1".equals(flag)){
									touroku = "1";
									sitei++;
								}else{
									touroku = "";
								}
								zubanList.add(new RequestResultElement(drwgNo, flag, touroku));
							}

						}catch(java.sql.SQLException e){
							//for システム管理者
						    ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
							errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(classId, e.getMessage()));
							category.error("[" + classId + "]:原図庫作業依頼の処理に失敗しました\n" + ErrorUtility.error2String(e));
							throw new Exception(e.getMessage());
						}finally{
							try{// CLOSE処理
								stmt.close();stmt = null;
								rs.close();rs = null;
							}catch (Exception e){}
						}

						RequestResultElement[] items = (RequestResultElement[])zubanList.toArray(new RequestResultElement[0]);
						int r = 0;
						String seq = "";//シーケンス番号
						char fro = kaisiNo.substring(9,10).charAt(0);//開始番号の末尾
						char to = syuryoNo.substring(9,10).charAt(0);//終了番号の末尾
						char[] oiban = DrasapUtil.createOibanArray(fro, to);//追番を取得

						String hani_s = "";//開始番号
						String hani_e = "";//終了番号
						if(!"?".equals(kaisiNo.substring(10,11))){
							hani_s = kaisiNo;
						}
						if(!"?".equals(syuryoNo.substring(10,11))){
							hani_e = syuryoNo;
						}

						int countt = 0;
						for(int t = 0; t < oiban.length; t++){
							char oiban1 = oiban[t];//追番
							if(t == 0){
								if("".equals(hani_s)){
									//開始番号の末尾が?
									hani = hani.substring(0,9) + oiban1;
								}else{
									//開始番号が指定の場合
									hani = hani_s;
									doHani_s(requestForm, errors, conn, hani,user);
								}
							}else if(t == oiban.length-1){
								if("".equals(hani_e)){
									//終了番号の末尾が?
									hani = hani.substring(0,9) + oiban1;
								}else{
									//終了番号が指定
									hani = hani_e;
									doHani_e(requestForm, errors, conn, hani, user);
								}
							}else{
								hani = hani.substring(0,9) + oiban1;
							}

							boolean flagName = true;//同じ図番があり、登録したかのチェック
							for(int k = 0; k < items.length; k++){
								String drwg_no = items[k].getDrwgNo();
								if(drwg_no.substring(0,10).equals(hani) || drwg_no.equals(hani)){
									int drwgNo_s = drwg_no.charAt(9);
									if(kai <= drwgNo_s && drwgNo_s <= syu){ //図番が開始、終了番号の中にあるか
										String tourokuNo = items[k].getTouroku();//登録有無
										//シーケンス番号を取得する
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
										//原図庫作業依頼展開テーブルに追加する
										doInsert(conn, seq, job_Id, gyoNo, drwg_no, tourokuNo,user, errors);
									}
									flagName = false;
									break;
								}
							}

							if(flagName == true){ //登録済みの図番でない
								hani = hani + "?";
								hani = hani.substring(0,11);
								String hanisitei = "";
								if("1".equals(requestForm.hani_s) || "1".equals(requestForm.hani_e)){ //開始番号または終了番号の図番が存在するか
									hanisitei = "1";//登録有無
								}
								//シーケンス番号を取得する
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
								//登録のない図番を原図庫作業依頼展開テーブルに追加する
								doNew_Insert(conn, seq, job_Id, gyoNo, hani, hanisitei, user, errors);
								if("".equals(hanisitei)){
									countt++;
								}
							}
							requestForm.hani_s = "";//開始番号が存在するかのチェック用
							requestForm.hani_e = "";//終了番号が存在するかのチェック用
						}

						if(countt == 0){
							requestForm.zumenAll = "1";//展開した図面が全て登録済み
						}
					}else{
						category.debug("必ず開始番号 < 終了番号で指定してください");
						errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.hanisitei_s.required"));//必ず開始番号 < 終了番号で指定してください
						saveErrors(request, errors);
					}
				}else{
					category.debug("図番の先頭9桁が同じでない場合は範囲指定はできない");
					errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.hanisitei.required"));//図番の先頭9桁が同じでない場合は範囲指定はできない
					saveErrors(request, errors);
				}
			}
		}
	}


	private void doHani_s(RequestForm requestForm, ActionErrors errors, Connection conn, String hani, User user) throws Exception{
		//開始番号の図番が属性情報テーブルに存在するか
		java.sql.Statement stmt = null;//データー存在チェック用コネクションステートメント
		java.sql.ResultSet rs = null;
		requestForm.hani_s = "";//開始番号の

		try{
			stmt = conn.createStatement();
			String strSql ="select DRWG_NO,LATEST_FLAG from INDEX_DB where DRWG_NO = '"+hani+"'";

			rs = stmt.executeQuery(strSql);
			if(rs.next()){
//				String drwgNo = rs.getString("DRWG_NO");//図番
	//			String flag = rs.getString("LATEST_FLAG");//最新図番かどうか
				requestForm.hani_s = "1";
			}

		}catch(java.sql.SQLException e){
			ErrorLoger.error(user, this,DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(classId, e.getMessage()));
			category.error("[" + classId + "]:原図庫作業依頼の処理に失敗しました\n" + ErrorUtility.error2String(e));
			throw new Exception(e.getMessage());
		}finally{
			try{// CLOSE処理
				stmt.close();stmt = null;
				rs.close();rs = null;
			}catch (Exception e){}
		}
	}

	private void doHani_e(RequestForm requestForm, ActionErrors errors, Connection conn, String hani, User user) throws Exception{
		//終了番号の図番が属性情報テーブルに存在するか
		java.sql.Statement stmt = null;//データー存在チェック用コネクションステートメント
		java.sql.ResultSet rs = null;
		requestForm.hani_e = "";

		try{
			stmt = conn.createStatement();
			String strSql ="select DRWG_NO,LATEST_FLAG from INDEX_DB where DRWG_NO = '"+hani+"'";

			rs = stmt.executeQuery(strSql);
			if(rs.next()){
//				String drwgNo = rs.getString("DRWG_NO");//図番
//				String flag = rs.getString("LATEST_FLAG");//最新図番かどうか
				requestForm.hani_e = "1";
			}

		}catch(java.sql.SQLException e){
			ErrorLoger.error(user, this,DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(classId, e.getMessage()));
			category.error("[" + classId + "]:原図庫作業依頼の処理に失敗しました\n" + ErrorUtility.error2String(e));
			throw new Exception(e.getMessage());
		}finally{
			try{// CLOSE処理
				stmt.close();stmt = null;
				rs.close();rs = null;
			}catch (Exception e){}
		}
	}

	/**
	 * 1行目を挿入する
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
			kaisiNo = syuryoNo;//終了番号に記入されても開始番号がなければ開始番号に記入する
			syuryoNo = "";
		}
		String sagyo_bi = "";//作業日時
		String status = "";//状態推移ステータス
		if("1".equals(zumenAll)){
			//図面出力指示のときのみ作業ステータス=1となる場合は「SET」を入力する
			if("B".equals(irai)){
				status = "SET";
			}
			sagyo_bi = day;
		}
		//依頼内容が原図借用依頼または図面以外焼付依頼の場合で作業ステータス(JOB_STAT)が0の場合は状態推移ステータス(TRANSIT_STAT)に「SET」を記入する
		if("0".equals(zumenAll) && "C".equals(irai) || "D".equals(irai)){
			status = "SET";
		}

		if(requestForm.syutu == null){//出力先
			requestForm.syutu = "";
		}
		java.sql.Statement stmt = null;//データー存在チェック用コネクションステートメント
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
			/** このメソッド内で、エラーメッセージの処理は不要
			ErrorLoger.error(user, this,DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(classId, e.getMessage()));
			category.error("[" + classId + "]:原図庫作業依頼の追加に失敗しました\n" + ErrorUtility.error2String(e));
			*/
			throw e;

		}finally{
			try{ // CLOSE処理
				stmt.close();stmt = null;
				rs.close();rs = null;
			}catch (Exception e){}
		}
	}
	/**
	 * 2行目の挿入
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
			kaisiNo = syuryoNo;//終了番号に記入されても開始番号がなければ開始番号に記入する
			syuryoNo = "";
		}
		String sagyo_bi = "";//作業日時
		String status = "";//状態推移ステータス
		if("1".equals(zumenAll)){
			//図面出力指示のときのみ作業ステータス=1となる場合は「SET」を入力する
			if("B".equals(irai)){
				status = "SET";
			}
			sagyo_bi = day;
		}
		//依頼内容が原図借用依頼または図面以外焼付依頼の場合で作業ステータス(JOB_STAT)が0の場合は状態推移ステータス(TRANSIT_STAT)に「SET」を記入する
		if("0".equals(zumenAll) && "C".equals(irai) || "D".equals(irai)){
			status = "SET";
	  	}
		if(requestForm.syutu == null){//出力先
			requestForm.syutu = "";
		}
		java.sql.Statement stmt2 = null;//データー存在チェック用コネクションステートメント
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
			/** このメソッド内で、エラーメッセージの処理は不要
			ErrorLoger.error(user, this,DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(classId, e.getMessage()));
			category.error("[" + classId + "]:原図庫作業依頼の追加に失敗しました\n" + ErrorUtility.error2String(e));
			*/
			throw e;

		}finally{
			try{ // CLOSE処理
				stmt2.close();stmt2 = null;
				rs2.close();rs2 = null;
			}catch (Exception e){}
		}
	}
	/**
	 * 3行目の挿入
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
			kaisiNo = syuryoNo;//終了番号に記入されても開始番号がなければ開始番号に記入する
			syuryoNo = "";
		}
		String sagyo_bi = "";//作業日時
		String status = "";//状態推移ステータス
		if("1".equals(zumenAll)){
			//図面出力指示のときのみ作業ステータス=1となる場合は「SET」を入力する
			if("B".equals(irai)){
				status = "SET";
			}
			sagyo_bi = day;
		}
		//依頼内容が原図借用依頼または図面以外焼付依頼の場合で作業ステータス(JOB_STAT)が0の場合は状態推移ステータス(TRANSIT_STAT)に「SET」を記入する
		if("0".equals(zumenAll) && "C".equals(irai) || "D".equals(irai)){
			status = "SET";
		}
		if(requestForm.syutu == null){//出力先
			requestForm.syutu = "";
		}
		java.sql.Statement stmt3 = null;//データー存在チェック用コネクションステートメント
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
			/** このメソッド内で、エラーメッセージの処理は不要
			ErrorLoger.error(user, this,DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(classId, e.getMessage()));
			category.error("[" + classId + "]:原図庫作業依頼の追加に失敗しました\n" + ErrorUtility.error2String(e));
			*/
			throw e;

		}finally{
			try{// CLOSE処理
				stmt3.close();stmt3 = null;
				rs3.close();rs3 = null;
			}catch (Exception e){}
		}
	}
	/**
	 * 4行目の挿入
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
			kaisiNo = syuryoNo;//終了番号に記入されても開始番号がなければ開始番号に記入する
			syuryoNo = "";
		}
		String sagyo_bi = "";//作業日時
		String status = "";//状態推移ステータス
		if("1".equals(zumenAll)){
			//図面出力指示のときのみ作業ステータス=1となる場合は「SET」を入力する
			if("B".equals(irai)){
				status = "SET";
			}
			sagyo_bi = day;
		}
		//依頼内容が原図借用依頼または図面以外焼付依頼の場合で作業ステータス(JOB_STAT)が0の場合は状態推移ステータス(TRANSIT_STAT)に「SET」を記入する
		if("0".equals(zumenAll) && "C".equals(irai) || "D".equals(irai)){
			status = "SET";
		}
		if(requestForm.syutu == null){//出力先
			requestForm.syutu = "";
		}
		java.sql.Statement stmt4 = null;//データー存在チェック用コネクションステートメント
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
			/** このメソッド内で、エラーメッセージの処理は不要
			ErrorLoger.error(user, this,DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(classId, e.getMessage()));
			category.error("[" + classId + "]:原図庫作業依頼の追加に失敗しました\n" + ErrorUtility.error2String(e));
			*/
			throw e;

		}finally{
			try{// CLOSE処理
				stmt4.close();stmt4 = null;
				rs4.close();rs4 = null;
			}catch (Exception e){}
		}
	}
	/**
	 * 5行目の挿入
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
			kaisiNo = syuryoNo;//終了番号に記入されても開始番号がなければ開始番号に記入する
			syuryoNo = "";
		}
		String sagyo_bi = "";//作業日時
		String status = "";//状態推移ステータス
		if("1".equals(zumenAll)){
			//図面出力指示のときのみ作業ステータス=1となる場合は「SET」を入力する
			if("B".equals(irai)){
				status = "SET";
			}
			sagyo_bi = day;
		}
		//依頼内容が原図借用依頼または図面以外焼付依頼の場合で作業ステータス(JOB_STAT)が0の場合は状態推移ステータス(TRANSIT_STAT)に「SET」を記入する
		if("0".equals(zumenAll) && "C".equals(irai) || "D".equals(irai)){
			status = "SET";
		}
		if(requestForm.syutu == null){//出力先
			requestForm.syutu = "";
		}
		java.sql.Statement stmt5 = null;//データー存在チェック用コネクションステートメント
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
			/** このメソッド内で、エラーメッセージの処理は不要
			ErrorLoger.error(user, this,DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(classId, e.getMessage()));
			category.error("[" + classId + "]:原図庫作業依頼の追加に失敗しました\n" + ErrorUtility.error2String(e));
			*/
			throw e;

		}finally{
			try{ // CLOSE処理
				stmt5.close();stmt5 = null;
				rs5.close();rs5 = null;
			}catch (Exception e){}
		}
	}

	/*
	 * 原図庫作業依頼展開テーブルに追加をする
	 */
	private void doInsert(java.sql.Connection conn, String seq, String job_Id, String gyoNo, String drwg_no, String tourokuNo,User user, ActionErrors errors)throws Exception{
		java.sql.Statement stmt = null;//データー存在チェック用コネクションステートメント
		try{
			stmt = conn.createStatement();
			String strSql ="insert into JOB_REQUEST_EXPAND_TABLE(SEQ_NO,JOB_ID,ROW_NO,EXPAND_DRWG_NO,EXIST)" +
							" values('"+seq+"','"+job_Id+"','"+gyoNo+"','"+drwg_no+"', '"+tourokuNo+"')";

			stmt.executeUpdate(strSql);
		}catch(java.sql.SQLException e){
			//for システム管理者
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(classId, e.getMessage()));
			category.error("[" + classId + "]:原図庫作業依頼の追加に失敗しました\n" + ErrorUtility.error2String(e));
		}finally{
			try{// CLOSE処理
				stmt.close();stmt = null;
			}catch (Exception e){}
		}

	}

	/*
	 *
	 * 登録のない図番を原図庫作業依頼展開テーブルに追加する
	*/
	private void doNew_Insert(java.sql.Connection conn, String seq, String job_Id, String gyoNo, String hani, String hanisitei, User user, ActionErrors errors)throws Exception{
		java.sql.Statement stmt = null;//データー存在チェック用コネクションステートメント
		try{
			stmt = conn.createStatement();
			String strSql ="insert into JOB_REQUEST_EXPAND_TABLE(SEQ_NO,JOB_ID,ROW_NO,EXPAND_DRWG_NO,EXIST)" +
							" values('"+seq+"','"+job_Id+"','"+gyoNo+"','"+hani+"', '"+hanisitei+"')";

			stmt.executeUpdate(strSql);
		}catch(java.sql.SQLException e){
			//for システム管理者
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(classId, e.getMessage()));
			category.error("[" + classId + "]:原図庫作業依頼展開テーブルの追加に失敗しました\n" + ErrorUtility.error2String(e));
			throw new Exception(e.getMessage());
		}finally{
			try{// CLOSE処理
				stmt.close();stmt = null;
			}catch (Exception e){}
		}
	}

	/**
	 * データ登録のチェックをする
	 */
	private ActionErrors CheckData(RequestForm requestForm, ActionForm form, ActionErrors errors, HttpServletRequest request){
		HashSet usedErrMsgSet = new HashSet();// 同じエラーを複数行表示させないため
		requestForm.errlog = "";//開始または終了番号でのエラー用
		requestForm.errNumber = "";//開始と終了番号のエラー用
		requestForm.hani_t = "";//開始と終了番号でのエラー用
		requestForm.hani_sitei = "";//範囲指定
//		String gyoNo = "";//行番号
		//出力先を指定した場合にhiddenでデータを持つ
		if(requestForm.syutu != null || !"".equals(requestForm.syutu)){
			requestForm.hiddenSyutu = requestForm.syutu;
		}
		// フォームに入力された文字を、全角から半角へ、大文字を小文字へ
		// また図番については、ハイフン抜きに整形する。
		requestForm.formatInpuedData();

		// 入力されている行について、エラーチェックする。
		RequestDataChecker checker = null;
		if("図面登録依頼".equals(requestForm.irai)){
			checker = new RequestDataCheckerA();
		} else if("図面出力指示".equals(requestForm.irai)){
			checker = new RequestDataCheckerB();
		} else if("原図借用依頼".equals(requestForm.irai)){
			checker = new RequestDataCheckerC();
		} else if("図面以外焼付依頼".equals(requestForm.irai)){
			checker = new RequestDataCheckerD();
		}
		requestForm.hiddenNo1 = "";
		requestForm.hiddenNo2 = "";
		requestForm.hiddenNo3 = "";
		requestForm.hiddenNo4 = "";
		requestForm.hiddenNo5 = "";
		if(requestForm.isInputedLine1()){
			// 1行目に入力があれば
			checkLineData(requestForm.getLineData1(), requestForm, errors,
					usedErrMsgSet, checker);
			requestForm.hiddenNo1 = "on";
		}
		if(requestForm.isInputedLine2()){
			// 2行目に入力があれば
			checkLineData(requestForm.getLineData2(), requestForm, errors,
					usedErrMsgSet, checker);
			requestForm.hiddenNo2 = "on";
		}
		if(requestForm.isInputedLine3()){
			// 3行目に入力があれば
			checkLineData(requestForm.getLineData3(), requestForm, errors,
					usedErrMsgSet, checker);
			requestForm.hiddenNo3 = "on";
		}
		if(requestForm.isInputedLine4()){
			// 4行目に入力があれば
			checkLineData(requestForm.getLineData4(), requestForm, errors,
					usedErrMsgSet, checker);
			requestForm.hiddenNo4 = "on";
		}
		if(requestForm.isInputedLine5()){
			// 5行目に入力があれば
			checkLineData(requestForm.getLineData5(), requestForm, errors,
					usedErrMsgSet, checker);
			requestForm.hiddenNo5 = "on";
		}

		// 2019.10.23 yamamoto add.
		// すべての項目が未入力の場合はエラーとする
		if(StringUtils.isEmpty(requestForm.hiddenNo1)
			&& StringUtils.isEmpty(requestForm.hiddenNo2)
			&& StringUtils.isEmpty(requestForm.hiddenNo3)
			&& StringUtils.isEmpty(requestForm.hiddenNo4)
			&& StringUtils.isEmpty(requestForm.hiddenNo5))
		{
			errors.add(ActionMessages.GLOBAL_MESSAGE,
					new ActionMessage("error.zuban.required" ));
		}

			//エラーがあればエラーを格納する
		if(!errors.isEmpty()){
			saveErrors(request, errors);
		}
		return errors;

	}

	/**
	 * 文字列を整数に変換する。変換できなければ-1を返す
	 */
	public static int convertInt(String str) {
		int number = 0;
		try{
			// まず数字に変換する
			number = Integer.valueOf(str).intValue();
			return number;
		} catch (NumberFormatException ne) {
			// 数字として解釈できない
			return -1;
		}
	}

	/**
	 * 開始、終了番号のどちらかが入力されているときのチェック
	 * 1) 図面登録依頼、図面出力指示なら11ケタ、または12ケタか?
	 */
	private ActionErrors checkData(RequestForm requestForm, ActionErrors errors, String kaisiNo){
		if("".equals(requestForm.errlog)){
			// まだこのエラーチェックによるメッセージがなければ
			if("図面登録依頼".equals(requestForm.irai) || "図面出力指示".equals(requestForm.irai)){
				if(kaisiNo.length() < 11  || kaisiNo.length() >12){
					errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.allNo.check"));
					requestForm.errlog = "1";
				}
			} else {
				// それ以外の場合、20ケタにする必要があるが、
				// その制御はjsp側で行う。
			}
		}

		return errors;
	}

	/**
	 * 開始番号と終了番号が両方入力されているときのチェック
	 * 1) 11ケタのみ範囲指定できる
	 * 2) 開始番号 < 終了番号であるか?
	 * 3) 原図借用依頼、図面以外焼付依頼では範囲指定できない
	 */
	private ActionErrors checkNumber(RequestForm requestForm, ActionErrors errors, String kaisiNo, String syuNo){
		if(kaisiNo.length() != 11 && "".equals(requestForm.hani_t) || syuNo.length() != 11 && "".equals(requestForm.hani_t)){
			category.debug("範囲指定は11桁のみです");
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.hani_leng_e.check"));
		} else {
			// 開始、終了とも11ケタのときのみチェックする。
			// '04.Aug.3変更 by Hirata
			if("".equals(requestForm.errNumber)){
				//開始番号<終了番号でなければエラーを表示
				if(kaisiNo.substring(0,10).equals(syuNo.substring(0,10))){
					int kai = kaisiNo.charAt(10);//開始番号
					int syu = syuNo.charAt(10);//終了番号
					if(kai > syu && "".equals(requestForm.errNumber)){
						errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.hanisitei_s.required"));
						category.debug("開始、終了にエラーあり");
						requestForm.errNumber = "1";
					}
				}else if(kaisiNo.substring(0,9).equals(syuNo.substring(0,9))){
					int kai = kaisiNo.charAt(9);//開始番号
					int syu = syuNo.charAt(9);//終了番号
					if(kai > syu && "".equals(requestForm.errNumber)){
						errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.hanisitei_s.required"));
						category.debug("開始、終了にエラーあり");
						requestForm.errNumber = "1";
					}
				}
			}
		}

		//依頼内容が原図借用依頼または図面以外焼付依頼の場合は範囲指定はできない
		if("原図借用依頼".equals(requestForm.irai) || "図面以外焼付依頼".equals(requestForm.irai)){
			if("".equals(requestForm.hani_sitei)){
				category.debug("図面登録依頼、図面出力指示以外は範囲指定できない");
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.hani_igai.required"));
				requestForm.hani_sitei = "1";
			}
		}

		return errors;
	}
	/**
	 * 指定した図番が、禁止された文字を使用しているかチェックする。
	 * @param drwgNo
	 * @return 禁止された文字を使用していれば true
	 */
	/* '04.Aug.16 このチェックは不要
	private boolean hasProhibitedChar(String drwgNo){
		return (drwgNo.indexOf("I") != -1 || drwgNo.indexOf("O") != -1 ||
						drwgNo.indexOf("Q") != -1 || drwgNo.indexOf("X") != -1);
	}*/

	/**
	 * 1行ずつについてのチェックを行う
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
		//開始,終了番号のチェック
		if(!"".equals(lineData.getKaisiNo()) && !"".equals(lineData.getSyuuryouNo())){
			checkNumber(requestForm, errors, lineData.getKaisiNo(), lineData.getSyuuryouNo());
		}else if(!"".equals(lineData.getKaisiNo())){
			checkData(requestForm, errors, lineData.getKaisiNo());
		}else if(!"".equals(lineData.getSyuuryouNo())){
			checkData(requestForm, errors, lineData.getSyuuryouNo());
		}

		// 指定した図番が、禁止された文字を使用しているかチェックする。
		/* '04.Aug.16 禁止文字を変更した。
		if(hasProhibitedChar(lineData.getKaisiNo()) || hasProhibitedChar(lineData.getSyuuryouNo())){
			if(! usedErrMsgSet.contains("error.hani_sitei.check")){
				category.debug("品番は[Ｉ]・[Ｏ]・[Ｑ]・[Ｘ]の文字を使用できません");
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.hani_sitei.check"));
				usedErrMsgSet.add("error.hani_sitei.check");
			}
		}*/

		// RequestDataCheckerを使用して、チェックする。
		// Startegyパターンを利用する。
		checker.checkLineData(lineData, requestForm, errors,
								usedErrMsgSet, category);
	}

}
