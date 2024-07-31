package tyk.drasap.search;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import tyk.drasap.change_acllog.ChangeAclLogger;
import tyk.drasap.common.AclUpdateNoSequenceDB;
import tyk.drasap.common.AclvMasterDB;
import tyk.drasap.common.DrasapPropertiesFactory;
import tyk.drasap.common.ErrorUtility;
import tyk.drasap.common.MessageManager;
import tyk.drasap.common.User;
import tyk.drasap.errlog.ErrorLoger;
import tyk.drasap.springfw.action.BaseAction;

/**
 * アクセスレベル、使用禁止を変更するAction。
 * '04.Nov.23 図番をロギングするように変更
 * @author fumi
 * 作成日: 2004/01/20
 * @version 2013/09/14 yamagishi
 */
@Controller
@SessionAttributes("aclvChangeForm")
public class AclvChangeAction extends BaseAction {
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
	@PostMapping("/aclvChange")
	public Object execute(
			@ModelAttribute("aclvChangeForm") AclvChangeForm form,
			HttpServletRequest request,
			HttpServletResponse response,
			Model errors)
			throws Exception {
		category.debug("start");
		AclvChangeForm aclvChangeForm = form;
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		// sessionタイムアウトの確認
		if (user == null) {
			return "timeout";
		}
		// AclvChangeFormの初期処理
		aclvChangeForm.errorMessages = new ArrayList<>();// エラーメッセージのクリア
		// act属性による処理の切り分け
		if ("CHECK_ON".equals(aclvChangeForm.getAct())) {
			// 全てにチェック
			for (int i = 0; i < aclvChangeForm.getAclvChangeList().size(); i++) {
				aclvChangeForm.getAclvChangeElement(i).setSelected(true);
			}
			category.debug("--> input");
			return "input";
		}

		if ("CHECK_OFF".equals(aclvChangeForm.getAct())) {
			// 全てのチェック外す
			for (int i = 0; i < aclvChangeForm.getAclvChangeList().size(); i++) {
				aclvChangeForm.getAclvChangeElement(i).setSelected(false);
			}
			category.debug("--> input");
			return "input";
		}

		if ("NEXT".equals(aclvChangeForm.getAct())) {
			// 2013.07.24 yamagishi modified. start
			//MessageResources resources = getResources(request, "application"); // applicatin.properties取得
			// 次画面に進む前のチェック
			//			checkForNext(aclvChangeForm);
			aclvChangeForm.getErrorMessages().clear();
			for (int i = 0; i < aclvChangeForm.getAclvChangeList().size(); i++) {
				aclvChangeForm.getAclvChangeList().get(i).setSelected(Boolean.parseBoolean(request.getParameter("aclvChangeElement[" + i + "].selected")));
				aclvChangeForm.getAclvChangeList().get(i).setNewAclId(request.getParameter("aclvChangeElement[" + i + "].newAclId"));
				aclvChangeForm.getAclvChangeList().get(i).setNewProhibit(request.getParameter("aclvChangeElement[" + i + "].newProhibit"));
			}
			checkForNext(aclvChangeForm, user);
			// 2013.07.24 yamagishi modified. end
			if (aclvChangeForm.getErrorMessages().size() > 0) {
				// 入力画面に戻る
				category.debug("--> input");
				return "input";
			}
			// 確認画面に進む
			category.debug("--> confirm");
			return "confirm";
		}

		if ("SEARCH".equals(aclvChangeForm.getAct())) {
			// 検索画面に戻る
			request.setAttribute("task", "continue");
			category.debug("--> search");
			return "search";
		}

		if ("BACK_INPUT".equals(aclvChangeForm.getAct())) {
			// 入力画面に戻る
			category.debug("--> input");
			return "input";
		}

		if ("CONFIRMED".equals(aclvChangeForm.getAct())) {
			// 2013.07.24 yamagishi modified. start
			//MessageResources resources = getResources(request, "application");
			// 確認OK。更新を行う。
			//			updateAclv(aclvChangeForm, user);
			updateAclv(aclvChangeForm, user);
			// 2013.07.24 yamagishi modified. end
			if (aclvChangeForm.getErrorMessages().size() > 0) {
				// 更新に失敗
				category.debug("--> input");
				return "input";
			}

			// 更新に成功した場合、
			// 検索画面に戻るが、検索結果のみクリアする。・・・アクセスレベルなど変更したため

			// アクセスログを
			// '04.Nov.23変更 図番をロギングするため、ここでのロギングは行わない
			//AccessLoger.loging(user, AccessLoger.FID_CHG_ACL);
			//category.debug("更新した件数は " + cnt);
			request.setAttribute("task", "clear_result");
			category.debug("--> search2");
			return "search2";
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * 次画面に進む前のチェック。
	 * - 選択されているか
	 * - 変更されたデータがあるか
	 * @param aclvChangeForm
	 * @param resources
	 */
	//	private void checkForNext(AclvChangeForm aclvChangeForm){	// 2013.07.24 yamagishi modified. start
	private void checkForNext(AclvChangeForm aclvChangeForm, User user) {
		HashMap<String, String> aclMap = aclvChangeForm.getAclMap();
		HashSet<String> drwgNoSet = new HashSet<String>(); // modifield end.
		// 1) 選択されているか?
		//		また変更されたデータがあるか?
		boolean selected = false;// 選択されていれば true
		boolean modified = false;// 変更されたデータがあるか? あれば true
		for (int i = 0; i < aclvChangeForm.getAclvChangeList().size(); i++) {
			AclvChangeElement aclvChangeElement = aclvChangeForm.getAclvChangeElement(i);
			if (aclvChangeElement.isSelected()) {// 選択されている
				selected = true;
				if (aclvChangeElement.isModified()) {// 変更されている
					modified = true;
				}
			}
			drwgNoSet.add(aclvChangeElement.getDrwgNo()); // 1物2品番用に図番を保存		// 2013.07.24 yamagishi add.
		}
		if (!selected) {
			aclvChangeForm.getErrorMessages().add(MessageManager.getInstance().getMessage("msg.aclv.required.drwg"));
		} else if (!modified) {
			aclvChangeForm.getErrorMessages().add(MessageManager.getInstance().getMessage("msg.aclv.notmodified.aclv"));
			// 2013.09.14 yamagishi add. start
		} else {
			Connection conn = null;
			try {
				conn = ds.getConnection();

				// 1物2品番用追加リスト
				ArrayList<AclvChangeElement> addAclvChangeList = new ArrayList<AclvChangeElement>();
				HashMap<String, String> drwgNoMap = null;
				String twinDrwgNo = null;

				boolean twinDrwgNoValid = true;
				boolean twinAclValid = true;
				for (int i = 0; i < aclvChangeForm.getAclvChangeList().size(); i++) {
					AclvChangeElement aclvChangeElement = aclvChangeForm.getAclvChangeElement(i);

					if (!aclMap.containsKey(aclvChangeElement.getOldAclId()) || !aclMap.containsKey(aclvChangeElement.getNewAclId())) {
						// アクセスレベル情報を保存
						aclMap.putAll(AclvMasterDB.getAclMap(conn, aclvChangeElement.getOldAclId(), aclvChangeElement.getNewAclId()));
					}
					drwgNoMap = AclvMasterDB.getDrwgNoMap(aclvChangeElement.getDrwgNo(), conn);
					if (drwgNoMap.isEmpty()) {
						// 1物2品番以外の場合、スキップ
						continue;
					}

					// 1物2品番整合性チェック
					if (drwgNoMap.get("ID1_TWIN_DRWG_NO") == null || drwgNoMap.get("ID2_TWIN_DRWG_NO") == null
							|| !drwgNoMap.get("ID1_TWIN_DRWG_NO").equals(drwgNoMap.get("ID2_TWIN_DRWG_NO"))) {
						twinDrwgNoValid = false;

						// 1物2品番ACLチェック
					} else if (drwgNoMap.get("ACL_ID") == null || drwgNoMap.get("TWIN_ACL_ID") == null
							|| !drwgNoMap.get("ACL_ID").equals(drwgNoMap.get("TWIN_ACL_ID"))) {
						twinAclValid = false;
					}

					twinDrwgNo = drwgNoMap.get("ID1_TWIN_DRWG_NO") != null ? drwgNoMap.get("ID1_TWIN_DRWG_NO") : drwgNoMap.get("ID2_TWIN_DRWG_NO");
					if (!drwgNoSet.contains(twinDrwgNo)) {
						// 1物2品番が既存リストになければ追加
						SearchResultElement searchResultElement = new SearchResultElement(twinDrwgNo, null, null, null, null);
						searchResultElement.addAttr("ACL_ID", drwgNoMap.get("TWIN_ACL_ID"));
						searchResultElement.addAttr("PROHIBIT", drwgNoMap.get("TWIN_PROHIBIT"));
						AclvChangeElement addAclvChangeElement = new AclvChangeElement(searchResultElement);
						addAclvChangeElement.setNewAclId(aclvChangeElement.getNewAclId());
						addAclvChangeElement.setNewProhibit(aclvChangeElement.getNewProhibit());
						addAclvChangeElement.setSelected(true);
						addAclvChangeList.add(addAclvChangeElement);

						if (!aclMap.containsKey(addAclvChangeElement.getOldAclId()) || !aclMap.containsKey(addAclvChangeElement.getNewAclId())) {
							// 追加分のアクセスレベル情報を保存
							aclMap.putAll(AclvMasterDB.getAclMap(conn, addAclvChangeElement.getOldAclId(), addAclvChangeElement.getNewAclId()));
						}
					}
				}
				// リストの最後に追加
				aclvChangeForm.getAclvChangeList().addAll(addAclvChangeList);

				if (!twinDrwgNoValid) {
					aclvChangeForm.getErrorMessages().add(messageSource.getMessage("system.aclBatchUpdate.upload.nodata.twinDrwgNo", null, null));
				} else if (!twinAclValid) {
					aclvChangeForm.getErrorMessages().add(messageSource.getMessage("system.aclBatchUpdate.upload.notequal.twin.drwgNo.acl", null, null));
				}
			} catch (Exception e) {
				// for ユーザー
				aclvChangeForm.getErrorMessages().add(
						MessageManager.getInstance().getMessage("msg.irai.unexpected", e.getMessage()));
				// for システム管理者
				ErrorLoger.error(user, this,
						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.unexpected"));
				// for MUR
				category.error("予想外の例外が発生\n" + ErrorUtility.error2String(e));
			} finally {
				try {
					conn.close();
				} catch (Exception e) {
				}
			}
			// 2013.09.14 yamagishi add. end
		}
	}

	/**
	 * INDEX_DBに使用禁止とアクセスレベルを更新する。
	 * 例外が発生した場合、AclvChangeForm.getErrorMessages()に追加する。
	 * @param aclvChangeForm
	 * @param user
	 * @param resources
	 * @return 更新した件数。
	 */
	//	private int updateAclv(AclvChangeForm aclvChangeForm){	// 2013.07.24 yamagishi modified.
	private int updateAclv(AclvChangeForm aclvChangeForm, User user) {
		int cnt = 0;
		Connection conn = null;
		PreparedStatement pstmt1 = null;// 使用禁止とアクセスレベルを変更
		PreparedStatement pstmt2 = null;// 使用禁止のみを変更
		PreparedStatement pstmt3 = null;// アクセスレベルのみを変更
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(false);// トランザクション
			// pstmt1の準備・・・使用禁止とアクセスレベルを変更
			pstmt1 = conn.prepareStatement("update INDEX_DB set " +
					" PROHIBIT=?, PROHIBIT_DATE=sysdate, PROHIBIT_EMPNO=?, PROHIBIT_NAME=?," +
					" ACL_ID=?, ACL_UPDATE=sysdate, ACL_EMPNO=?, ACL_NAME=?" +
					" where DRWG_NO=?");
			pstmt1.setString(2, user.getId());// 職番
			pstmt1.setString(3, user.getName());// 名前
			pstmt1.setString(5, user.getId());// 職番
			pstmt1.setString(6, user.getName());// 名前
			// pstmt2の準備・・・使用禁止のみを変更
			pstmt2 = conn.prepareStatement("update INDEX_DB set " +
					" PROHIBIT=?, PROHIBIT_DATE=sysdate, PROHIBIT_EMPNO=?, PROHIBIT_NAME=?" +
					" where DRWG_NO=?");
			pstmt2.setString(2, user.getId());// 職番
			pstmt2.setString(3, user.getName());// 名前
			// pstmt3の準備・・・アクセスレベルのみを変更
			pstmt3 = conn.prepareStatement("update INDEX_DB set " +
					" ACL_ID=?, ACL_UPDATE=sysdate, ACL_EMPNO=?, ACL_NAME=?" +
					" where DRWG_NO=?");
			pstmt3.setString(2, user.getId());// 職番
			pstmt3.setString(3, user.getName());// 名前
			// 図番をロギングするために一時格納するためのList
			//			List updatedDrwgNoList = new ArrayList();
			List<AclvChangeElement> updatedDrwgNoList = new ArrayList<AclvChangeElement>(); // 2013.07.25 yamagishi modified.
			// INDEX_DBに更新をする
			for (int i = 0; i < aclvChangeForm.getAclvChangeList().size(); i++) {
				AclvChangeElement aclvChangeElement = aclvChangeForm.getAclvChangeElement(i);
				if (aclvChangeElement.isSelected() && aclvChangeElement.isModified()) {
					// 選択されて、かつ変更されているときのみ
					if (!aclvChangeElement.oldProhibit.equals(aclvChangeElement.newProhibit)) {
						// 少なくとも使用禁止が変更されている
						if (!aclvChangeElement.oldAclId.equals(aclvChangeElement.newAclId)) {
							// アクセスレベルも変更されている
							// 更新時チェック実施
							checkExclusive(aclvChangeElement.getDrwgNo(), aclvChangeElement.getOldAclId(), conn); // 2013.07.24 yamagishi add.
							pstmt1.setString(1, aclvChangeElement.getNewProhibit());// 使用禁止
							pstmt1.setString(4, aclvChangeElement.getNewAclId());// アクセスレベル
							pstmt1.setString(7, aclvChangeElement.getDrwgNo());// 図番
							cnt += pstmt1.executeUpdate();
						} else {
							// 使用禁止のみ変更されている
							pstmt2.setString(1, aclvChangeElement.getNewProhibit());// 使用禁止
							pstmt2.setString(4, aclvChangeElement.getDrwgNo());// 図番
							cnt += pstmt2.executeUpdate();
						}
					} else {
						// アクセスレベルのみ変更されている
						// 更新時チェック実施
						checkExclusive(aclvChangeElement.getDrwgNo(), aclvChangeElement.getOldAclId(), conn); // 2013.07.24 yamagishi add.
						pstmt3.setString(1, aclvChangeElement.getNewAclId());// アクセスレベル
						pstmt3.setString(4, aclvChangeElement.getDrwgNo());// 図番
						cnt += pstmt3.executeUpdate();
					}
					// 更新した図番を一時格納する
					//					updatedDrwgNoList.add(aclvChangeElement.getDrwgNo()); // 2013.07.25 yamagishi modified.
					updatedDrwgNoList.add(aclvChangeElement);
				}
			}
			// commit
			conn.commit();

			// 2013.07.25 yamagishi modified. start
			//			// '04.Nov.23 図番もロギングするように変更
			//			// アクセスログをまとめて行う
			//			AccessLoger.loging(user, AccessLoger.FID_CHG_ACL,
			//					(String[])updatedDrwgNoList.toArray(new String[0]));
			// 管理NO採番
			String aclUpdateNo = AclUpdateNoSequenceDB.getAclUpdateNo(
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("tyk.upload.aclupdateno.type.changeAcl"), conn);
			HashMap<String, String> aclMap = aclvChangeForm.getAclMap();
			String drwgNo = null;
			String preUpdateAcl = null;
			String postUpdateAcl = null;
			for (AclvChangeElement aclvChangeElement : updatedDrwgNoList) {
				drwgNo = aclvChangeElement.getDrwgNo();
				preUpdateAcl = aclvChangeElement.getOldAclId();
				postUpdateAcl = aclvChangeElement.getNewAclId();
				ChangeAclLogger.logging(user, aclUpdateNo, drwgNo, preUpdateAcl, aclMap.get(preUpdateAcl), postUpdateAcl, aclMap.get(postUpdateAcl), null);
			}
			// 2013.07.25 yamagishi modified. end

		} catch (Exception e) {
			// rollback
			try {
				conn.rollback();
			} catch (Exception e2) {
			}
			// for ユーザー
			aclvChangeForm.getErrorMessages().add(
					MessageManager.getInstance().getMessage("msg.aclv.failed.update.aclv", e.getMessage()));
			// for システム管理者
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			// for MUR
			category.error("アクセスレベルの更新に失敗\n" + ErrorUtility.error2String(e));
		} finally {
			try {
				pstmt1.close();
			} catch (Exception e) {
			}
			try {
				pstmt2.close();
			} catch (Exception e) {
			}
			try {
				pstmt3.close();
			} catch (Exception e) {
			}
			try {
				conn.close();
			} catch (Exception e) {
			}
		}
		//
		return cnt;
	}

	// 2013.07.24 yamagishi add. start
	/**
	 * 属性情報テーブルの図番・ACLが更新されていないかチェックする
	 * @param drwgNo
	 * @param aclId
	 * @param resources
	 * @param conn
	 */
	private void checkExclusive(String drwgNo, String aclId, Connection conn)
			throws Exception {

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			// 更新時チェック用SQL
			String sql = "select count(DRWG_NO) as COUNT from INDEX_DB where DRWG_NO = ? and ACL_ID = ?";
			pstmt = conn.prepareStatement(sql);

			// 更新時チェック
			pstmt.setString(1, drwgNo);
			pstmt.setString(2, aclId);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				// 図番ACL更新判定
				if (rs.getLong("COUNT") <= 0) {
					// 別トランザクションによって図番ACLが更新済みの為、エラー
					throw new Exception("(" + messageSource.getMessage("system.aclBatchUpdate.update.excusive.acl", null, null) + ")");
				}
			}
		} finally {
			// CLOSE処理
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pstmt.close();
			} catch (Exception e) {
			}
		}
	}
	// 2013.07.24 yamagishi add. end
}
