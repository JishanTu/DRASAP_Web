package tyk.drasap.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * ユーザー管理マスターテーブルと対応したDBクラス。
 */
public class UserGroupDB {
	/**
	 * 取得したユーザー情報を、userに付加する。
	 * 成功すれば trueを返す。
	 * ID,パスワードの比較では大文字小文字を区別しない。
	 * @param user 
	 * @param id
	 * @param passwd
	 * @param conn
	 * @return true=成功。false=IDまたはパスワードが正しくない。
	 * @throws Exception
	 */
	public static ArrayList<UserGroup> getUserGroupArray(ArrayList<String> userGroupCodeArray, Connection conn)
						throws Exception {
		ArrayList<UserGroup> userGroupes = new ArrayList<UserGroup>();
		// チェック
		if(userGroupCodeArray.size() == 0){
			return userGroupes;
		}
		//
		Statement stmt1 = null;
		ResultSet rs1 = null;
		PreparedStatement pstmt2 = null;
		ResultSet rs2 = null;
		try{
			// ユーザーグループCDで検索する
			StringBuffer sbSql1 = new StringBuffer("select * from USER_GROUP_MASTER");
			sbSql1.append(" where USER_GRP_CODE in (");
			for(int i = 0; i < userGroupCodeArray.size(); i++){
				sbSql1.append("'");
				sbSql1.append((String) userGroupCodeArray.get(i));
				sbSql1.append("',");
			}
			// 最後のカンマが余分なので削除
			sbSql1.deleteCharAt(sbSql1.length()-1);
			sbSql1.append(")");
			// 
			stmt1 = conn.createStatement();
			rs1 = stmt1.executeQuery(sbSql1.toString());
			//
			pstmt2 = conn.prepareStatement("select * from USER_GRP_ACL_RELATION" +
						" where USER_GRP_CODE=?");
			while(rs1.next()){
				// プリンタID1から10を取得し、PrinterDBを用いて「Printerオブジェクトを格納したArrayList」を取得する
				ArrayList<String> printerId = new ArrayList<String>();
				if(rs1.getString("PRINTER_ID01") != null){ printerId.add(rs1.getString("PRINTER_ID01")); }
				if(rs1.getString("PRINTER_ID02") != null){ printerId.add(rs1.getString("PRINTER_ID02")); }
				if(rs1.getString("PRINTER_ID03") != null){ printerId.add(rs1.getString("PRINTER_ID03")); }
				if(rs1.getString("PRINTER_ID04") != null){ printerId.add(rs1.getString("PRINTER_ID04")); }
				if(rs1.getString("PRINTER_ID05") != null){ printerId.add(rs1.getString("PRINTER_ID05")); }
				if(rs1.getString("PRINTER_ID06") != null){ printerId.add(rs1.getString("PRINTER_ID06")); }
				if(rs1.getString("PRINTER_ID07") != null){ printerId.add(rs1.getString("PRINTER_ID07")); }
				if(rs1.getString("PRINTER_ID08") != null){ printerId.add(rs1.getString("PRINTER_ID08")); }
				if(rs1.getString("PRINTER_ID09") != null){ printerId.add(rs1.getString("PRINTER_ID09")); }
				if(rs1.getString("PRINTER_ID10") != null){ printerId.add(rs1.getString("PRINTER_ID10")); }
				ArrayList<Printer> enablePrinters = PrinterDB.getPrinterArray(printerId, conn);
				// アクセスレベルを取得する
				HashMap<String, String> aclMap = new HashMap<String, String>();
				pstmt2.setString(1, rs1.getString("USER_GRP_CODE"));
				rs2 = pstmt2.executeQuery();
				while(rs2.next()){
					aclMap.put(rs2.getString("ACL_ID").trim(), rs2.getString("ACL_VALUE"));
				}
				rs2.close();
				//
				userGroupes.add(
				new UserGroup(rs1.getString("USER_GRP_CODE"), rs1.getString("USER_GRP_NAME"),
				rs1.getString("VIEW_STMP_FLAG"),rs1.getString("EUC_STMP_FLAG"),rs1.getString("PLTR_STMP_FLAG"),
				enablePrinters,
				rs1.getString("IMPORT_REQ"),rs1.getString("PRINT_REQ"),rs1.getString("CHECKOUT_REQ"),rs1.getString("OTHER_REQ"),
				rs1.getString("DRWG_NO_DSPFLG"),rs1.getString("CREATE_DATE_DSPFLG"),rs1.getString("CREATE_USER_DSPFLG"),
				rs1.getString("MACHINE_JP_DSPFLG"),rs1.getString("MACHINE_EN_DSPFLG"),
				rs1.getString("USED_FOR_DSPFLG"),rs1.getString("MATERIAL_DSPFLG"),rs1.getString("TREATMENT_DSPFLG"),rs1.getString("PROCUREMENT_DSPFLG"),
				rs1.getString("SUPPLYER_JP_DSPFLG"),rs1.getString("SUPPLYER_EN_DSPFLG"),rs1.getString("SUPPLYER_TYPE_DSPFLG"),
				rs1.getString("ATTACH01_DSPFLG"),rs1.getString("ATTACH02_DSPFLG"),rs1.getString("ATTACH03_DSPFLG"),rs1.getString("ATTACH04_DSPFLG"),
				rs1.getString("ATTACH05_DSPFLG"),rs1.getString("ATTACH06_DSPFLG"),rs1.getString("ATTACH07_DSPFLG"),rs1.getString("ATTACH08_DSPFLG"),
				rs1.getString("ATTACH09_DSPFLG"),rs1.getString("ATTACH10_DSPFLG"),
				rs1.getString("MACHINE_NO_DSPFLG"),rs1.getString("MACHINE_NAME_DSPFLG"),
				rs1.getString("MACHINE_SPEC1_DSPFLG"),rs1.getString("MACHINE_SPEC2_DSPFLG"),rs1.getString("MACHINE_SPEC3_DSPFLG"),
				rs1.getString("MACHINE_SPEC4_DSPFLG"),rs1.getString("MACHINE_SPEC5_DSPFLG"),
				rs1.getString("DRWG_TYPE_DSPFLG"),rs1.getString("DRWG_SIZE_DSPFLG"),
				rs1.getString("ISSUE_DSPFLG"),rs1.getString("SUPPLY_DSPFLG"),
				rs1.getString("CAD_TYPE_DSPFLG"),rs1.getString("ENGINEER_DSPFLG"),
				rs1.getString("PROHIBIT_DSPFLG"),rs1.getString("PROHIBIT_DATE_DSPFLG"),
				rs1.getString("PROHIBIT_EMPNO_DSPFLG"),rs1.getString("PROHIBIT_NAME_DSPFLG"),
				rs1.getString("PAGES_DSPFLG"),
				rs1.getString("ACL_DSPFLG"),rs1.getString("ACL_UPDATE_DSPFLG"),rs1.getString("ACL_EMPNO_DSPFLG"),rs1.getString("ACL_NAME_DSPFLG"),
				rs1.getString("ATTACH_MAX_DSPFLG"),rs1.getString("LATEST_DSPFLG_DSPFLG"),rs1.getString("REPLACE_DSPFLG_DSPFLG"),
				rs1.getString("CREATE_DIV_DSPFLG"),rs1.getString("MEDIA_ID_DSPFLG"),rs1.getString("TWIN_DRWG_NO_DSPFLG"),rs1.getString("VIEW_PRINT_DOC"),
				aclMap)
				);
			}
			
		} catch (Exception e) {
			throw e;
			
		} finally {
			// CLOSE処理
			try{ rs1.close(); } catch (Exception e) {}
			try{ stmt1.close(); } catch (Exception e) {}
			try{ rs2.close(); } catch (Exception e) {}
			try{ pstmt2.close(); } catch (Exception e) {}
		}
		//
		return userGroupes;
	}

}
