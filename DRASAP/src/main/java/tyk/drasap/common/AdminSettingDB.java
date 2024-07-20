package tyk.drasap.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * 管理者設定マスターに対応するDBクラス
 *
 * @version 2013/06/27 yamagishi
 */
public class AdminSettingDB {
	/**
	 *
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public static DrasapInfo getDrasapInfo(Connection conn)
			throws Exception {
		//
		DrasapInfo drasapInfo = null;
		//
		PreparedStatement pstmt1 = null;
		ResultSet rs1 = null;
		try {
			// STATUS=1だけを読むように修正 '04.May.27 by Hirata
			String strSql1 = "select * from ADMIN_SETTING_MASTER" +
					" where SETTING_ID=? and STATUS='1'";
			//
			pstmt1 = conn.prepareStatement(strSql1);
			// 001・・・ViewDBドライブ名
			pstmt1.setString(1, "001");
			rs1 = pstmt1.executeQuery();
			String viewDBDrive = null;
			if (rs1.next()) {
				viewDBDrive = rs1.getString("VALUE");
			}
			// 600・・・検索警告件数
			pstmt1.setString(1, "600");
			rs1 = pstmt1.executeQuery();
			String searchWarningCount = null;
			if (rs1.next()) {
				searchWarningCount = rs1.getString("VALUE");
			}
			// 601・・・検索打切件数
			pstmt1.setString(1, "601");
			rs1 = pstmt1.executeQuery();
			String searchLimitCount = null;
			if (rs1.next()) {
				searchLimitCount = rs1.getString("VALUE");
			}
			// 602・・・ビュワーの間引き解像度
			//pstmt1.setString(1,"602");
			//rs1 = pstmt1.executeQuery();
			//String mabikiDpi = null;
			//if(rs1.next()){
			//	mabikiDpi = rs1.getString("VALUE");
			//}
			// 603・・・参考図出力の最大件数
			pstmt1.setString(1, "603");
			rs1 = pstmt1.executeQuery();
			String printRequestMax = null;
			if (rs1.next()) {
				printRequestMax = rs1.getString("VALUE");
			}
			// 604・・・アクセスレベル変更可能な職位
			pstmt1.setString(1, "604");
			rs1 = pstmt1.executeQuery();
			String aclvChangablePosition = null;
			if (rs1.next()) {
				aclvChangablePosition = rs1.getString("VALUE");
			}
			// 605・・・検索における図番属性の最低限の文字数
			pstmt1.setString(1, "605");
			rs1 = pstmt1.executeQuery();
			String minimumIuputDrwgChar = null;
			if (rs1.next()) {
				minimumIuputDrwgChar = rs1.getString("VALUE");
			}
			// 606・・・ビュワー間引き対象サイズ1(100dpi)
			pstmt1.setString(1, "606");
			rs1 = pstmt1.executeQuery();
			String mabiki100dpiSize = null;
			if (rs1.next()) {
				mabiki100dpiSize = rs1.getString("VALUE");
			}
			// 607・・・ビュワー間引き対象サイズ2(200dpi)
			pstmt1.setString(1, "607");
			rs1 = pstmt1.executeQuery();
			String mabiki200dpiSize = null;
			if (rs1.next()) {
				mabiki200dpiSize = rs1.getString("VALUE");
			}
			// 2013.06.27 yamagishi add. start
			// 608・・・複数図番指定時の検索可能件数
			pstmt1.setString(1, "608");
			rs1 = pstmt1.executeQuery();
			String multipleDrwgNoMax = null;
			if (rs1.next()) {
				multipleDrwgNoMax = rs1.getString("VALUE");
			}
			// 2013.06.27 yamagishi add. end
			// 2013.07.10 yamagishi add. start
			// 609・・・該当図入力値
			pstmt1.setString(1, "609");
			rs1 = pstmt1.executeQuery();
			String correspondingValue = null;
			if (rs1.next()) {
				correspondingValue = rs1.getString("VALUE");
			}
			// 610・・・機密管理図入力値（秘）
			pstmt1.setString(1, "610");
			rs1 = pstmt1.executeQuery();
			String confidentialValue = null;
			if (rs1.next()) {
				confidentialValue = rs1.getString("VALUE");
			}
			// 611・・・機密管理図入力値（極秘）
			pstmt1.setString(1, "611");
			rs1 = pstmt1.executeQuery();
			String strictlyConfidentialValue = null;
			if (rs1.next()) {
				strictlyConfidentialValue = rs1.getString("VALUE");
			}
			// 2013.07.10 yamagishi add. end
			// 700・・・閲覧用スタンプ位置(W)
			pstmt1.setString(1, "700");
			rs1 = pstmt1.executeQuery();
			String viewStampW = null;
			if (rs1.next()) {
				viewStampW = rs1.getString("VALUE");
			}
			// 701・・・閲覧用スタンプ位置(L)
			pstmt1.setString(1, "701");
			rs1 = pstmt1.executeQuery();
			String viewStampL = null;
			if (rs1.next()) {
				viewStampL = rs1.getString("VALUE");
			}
			// 705・・・閲覧用スタンプ文字濃度
			pstmt1.setString(1, "705");
			rs1 = pstmt1.executeQuery();
			String ViewStampDeep = null;
			if (rs1.next()) {
				ViewStampDeep = rs1.getString("VALUE");
			}
			// 707・・・閲覧用スタンプ日付形式
			pstmt1.setString(1, "707");
			rs1 = pstmt1.executeQuery();
			String viewStampDateFormat = null;
			if (rs1.next()) {
				viewStampDateFormat = rs1.getString("VALUE");
			}
			// 708・・・閲覧用スタンプでの図番を印字する？
			pstmt1.setString(1, "708");
			rs1 = pstmt1.executeQuery();
			String dispDrwgNoWithView = null;
			if (rs1.next()) {
				dispDrwgNoWithView = rs1.getString("VALUE");
			}
			// 2013.07.12 yamagishi add. start
			// 729・・・該当図用スタンプ位置(W方向)
			pstmt1.setString(1, "729");
			rs1 = pstmt1.executeQuery();
			String correspondingStampW = null;
			if (rs1.next()) {
				correspondingStampW = rs1.getString("VALUE");
			}
			// 730・・・該当図用スタンプ位置(L方向)
			pstmt1.setString(1, "730");
			rs1 = pstmt1.executeQuery();
			String correspondingStampL = null;
			if (rs1.next()) {
				correspondingStampL = rs1.getString("VALUE");
			}
			// 734・・・該当図用スタンプ文字濃淡
			pstmt1.setString(1, "734");
			rs1 = pstmt1.executeQuery();
			String correspondingStampDeep = null;
			if (rs1.next()) {
				correspondingStampDeep = rs1.getString("VALUE");
			}
			// 736・・・該当図用スタンプ文字
			pstmt1.setString(1, "736");
			rs1 = pstmt1.executeQuery();
			String correspondingStampStr = null;
			if (rs1.next()) {
				correspondingStampStr = rs1.getString("VALUE");
			}
			// 2013.07.12 yamagishi add. end

			// 2013.07.10 yamagishi modified. start
			//
			//			drasapInfo = new DrasapInfo(viewDBDrive, searchWarningCount, searchLimitCount,
			//						printRequestMax, aclvChangablePosition,
			//						minimumIuputDrwgChar,
			//						mabiki100dpiSize, mabiki200dpiSize,
			//						viewStampW, viewStampL, ViewStampDeep,
			//						viewStampDateFormat, dispDrwgNoWithView);
			drasapInfo = new DrasapInfo(viewDBDrive, searchWarningCount, searchLimitCount,
					printRequestMax, aclvChangablePosition,
					minimumIuputDrwgChar,
					mabiki100dpiSize, mabiki200dpiSize,
					viewStampW, viewStampL, ViewStampDeep,
					viewStampDateFormat, dispDrwgNoWithView, multipleDrwgNoMax,
					correspondingValue, confidentialValue, strictlyConfidentialValue,
					correspondingStampW, correspondingStampL,
					correspondingStampDeep, correspondingStampStr);
			// 2013.07.10 yamagishi modified. end

		} catch (Exception e) {
			throw e;

		} finally {
			// CLOSE処理
			try {
				rs1.close();
			} catch (Exception e) {
			}
			try {
				pstmt1.close();
			} catch (Exception e) {
			}
		}
		//
		return drasapInfo;
	}

}
