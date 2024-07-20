package tyk.drasap.system;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import tyk.drasap.common.ErrorUtility;
import tyk.drasap.common.User;
import tyk.drasap.errlog.ErrorLoger;
import tyk.drasap.springfw.action.BaseAction;
import tyk.drasap.springfw.utils.MessageSourceUtil;

/**
 * 管理者設定変更画面のAction。
 * @author Y.eto
 * 作成日: 2006/07/10
 */
@Controller
public class AdminSettingListAction extends BaseAction {
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
	@PostMapping("/adminSettingList")
	public String execute(
			AdminSettingListForm form,
			HttpServletRequest request,
			HttpServletResponse response,
			Model errors)
			throws Exception {
		//category.debug("start");
		//ActionErrors errors = new ActionErrors();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		if (user == null) {
			return "timeout";
		}
		AdminSettingListForm adminSettingListForm = form;
		if (adminSettingListForm == null) {
			adminSettingListForm = new AdminSettingListForm();
			adminSettingListForm.setAct("init");
		} else if ("init".equals(request.getParameter("act"))) {
			adminSettingListForm.setAct("init");
		}

		//
		if ("init".equals(adminSettingListForm.getAct())) {
			addStatusList(adminSettingListForm);
			adminSettingListForm = getAdminSettingList(adminSettingListForm, user, errors);
			session.removeAttribute("conditionListForm");
			session.setAttribute("adminSettingListForm", adminSettingListForm);
			return "success";
		}
		if ("onchange".equals(adminSettingListForm.getAct())) {
			updateModifiedDate(adminSettingListForm, user, errors);
			session.setAttribute("adminSettingListForm", adminSettingListForm);
			return "update";
		}
		if ("update".equals(adminSettingListForm.getAct())) {
			updateAdminSettingList(adminSettingListForm, user, errors);
			session.setAttribute("adminSettingListForm", adminSettingListForm);
			return "update";
		}

		return "success";
	}

	/**
	 * 管理者設定情報を検索する。例外が発生した場合、errorsにaddする。
	 * @param userGroupCode ユーザーグループコード
	 * @param user
	 * @param errors
	 * @throws IOException
	 */
	private AdminSettingListForm getAdminSettingList(AdminSettingListForm adminSettingListForm, User user, Model errors) throws IOException {
		Connection conn = null;
		Statement stmt1 = null;
		ResultSet rs1 = null;
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(true);// 非トランザクション

			StringBuilder sbSql1 = new StringBuilder("select SETTING_ID, ITEM_NAME, VALUE, STATUS, TO_CHAR(UPDATE_DATE,'YY/MM/DD HH24:MI:SS') UPDATE_DATE from ");
			sbSql1.append(" ADMIN_SETTING_MASTER");
			sbSql1.append(" order by SETTING_ID ");
			stmt1 = conn.createStatement();
			//			category.debug(sbSql1.toString());
			rs1 = stmt1.executeQuery(sbSql1.toString());
			while (rs1.next()) {
				AdminSettingListElement adminSettingListElement = new AdminSettingListElement();
				adminSettingListElement.settingId = rs1.getString("SETTING_ID");
				adminSettingListElement.itemName = rs1.getString("ITEM_NAME");
				adminSettingListElement.val = rs1.getString("VALUE");
				adminSettingListElement.setStatus(rs1.getString("STATUS"));
				adminSettingListElement.modifiedDate = rs1.getString("UPDATE_DATE");
				adminSettingListForm.setAdminSettingList(adminSettingListElement);
			}
		} catch (Exception e) {
			// for ユーザー
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.search.list", new Object[] { e.getMessage() }, null));
			// for システム管理者
			ErrorLoger.error(user, this, "検索条件に一致した件数のカウントに失敗" + ErrorUtility.error2String(e));
			// for MUR
			//			category.error("検索条件に一致した件数のカウントに失敗\n" + ErrorUtility.error2String(e));
		} finally {
			try {
				rs1.close();
			} catch (Exception e) {
			}
			try {
				stmt1.close();
			} catch (Exception e) {
			}
			try {
				conn.close();
			} catch (Exception e) {
			}
		}

		return adminSettingListForm;
	}

	/**
	 * 管理者設定情報を更新する。例外が発生した場合、errorsにaddする。
	 * @param userGroupCode ユーザーグループコード
	 * @param user
	 * @param errors
	 * @throws IOException
	 */
	private void updateAdminSettingList(AdminSettingListForm adminSettingListForm, User user, Model errors) throws IOException {
		Connection conn = null;
		PreparedStatement pstmt1 = null;
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(false);// 非トランザクション

			// 変更したものだけ更新
			for (int i = 0; i < adminSettingListForm.getAdminSettingList().size(); i++) {
				AdminSettingListElement adminSettingListElement = adminSettingListForm.getAdminSettingListElement(i);
				if (adminSettingListElement.isUpdate()) {
					String strSql1 = "update ADMIN_SETTING_MASTER set " +
							" VALUE=?,UPDATE_DATE=sysdate, STATUS=?" +
							" where SETTING_ID=?";
					pstmt1 = conn.prepareStatement(strSql1);
					pstmt1.setString(1, adminSettingListElement.getVal());
					pstmt1.setString(2, adminSettingListElement.getStatus());
					pstmt1.setString(3, adminSettingListElement.getSettingId());
					pstmt1.executeUpdate();

					try {
						pstmt1.close();
					} catch (Exception e) {
					}
					adminSettingListElement.setUpdate(false);
				}
			}
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (Exception e2) {
			}
			// for ユーザー
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.search.list", new Object[] { e.getMessage() }, null));
			// for システム管理者
			ErrorLoger.error(user, this, "管理設定項目の更新に失敗" + ErrorUtility.error2String(e));
			// for MUR
			//			category.error("管理設定項目の更新に失敗\n" + ErrorUtility.error2String(e));
		} finally {
			try {
				conn.commit();
			} catch (Exception e2) {
			}
			try {
				conn.setAutoCommit(true);
			} catch (Exception e2) {
			}
			try {
				pstmt1.close();
			} catch (Exception e) {
			}
			try {
				conn.close();
			} catch (Exception e) {
			}
		}

	}

	/**
	 * 更新日設定。例外が発生した場合、errorsにaddする。
	 * @param userGroupCode ユーザーグループコード
	 * @param user
	 * @param errors
	 * @throws IOException
	 */
	private void updateModifiedDate(AdminSettingListForm adminSettingListForm, User user, Model errors) throws IOException {
		Calendar calFrom = Calendar.getInstance();
		String strModifiedDate = new SimpleDateFormat("yy/MM/dd HH:mm:ss").format(calFrom.getTime());
		int indx = Integer.parseInt(adminSettingListForm.getUpdateIndex());
		// 変更したものだけ更新
		AdminSettingListElement adminSettingListElement = adminSettingListForm.getAdminSettingListElement(indx);
		adminSettingListElement.setModifiedDate(strModifiedDate);
		adminSettingListElement.setUpdate(true);
	}

	/**
	 * ステータスリストを作成する。例外が発生した場合、errorsにaddする。
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws
	 */
	private void addStatusList(AdminSettingListForm adminSettingListForm) {
		adminSettingListForm.addStatusList("0");
		adminSettingListForm.addStatsuNameList("無効");
		adminSettingListForm.addStatusList("1");
		adminSettingListForm.addStatsuNameList("有効");

	}
}
