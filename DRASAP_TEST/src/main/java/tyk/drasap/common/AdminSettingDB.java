package tyk.drasap.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * �Ǘ��Ґݒ�}�X�^�[�ɑΉ�����DB�N���X
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
			// STATUS=1������ǂނ悤�ɏC�� '04.May.27 by Hirata
			String strSql1 = "select * from ADMIN_SETTING_MASTER" +
					" where SETTING_ID=? and STATUS='1'";
			//
			pstmt1 = conn.prepareStatement(strSql1);
			// 001�E�E�EViewDB�h���C�u��
			pstmt1.setString(1, "001");
			rs1 = pstmt1.executeQuery();
			String viewDBDrive = null;
			if (rs1.next()) {
				viewDBDrive = rs1.getString("VALUE");
			}
			// 600�E�E�E�����x������
			pstmt1.setString(1, "600");
			rs1 = pstmt1.executeQuery();
			String searchWarningCount = null;
			if (rs1.next()) {
				searchWarningCount = rs1.getString("VALUE");
			}
			// 601�E�E�E�����Ő،���
			pstmt1.setString(1, "601");
			rs1 = pstmt1.executeQuery();
			String searchLimitCount = null;
			if (rs1.next()) {
				searchLimitCount = rs1.getString("VALUE");
			}
			// 602�E�E�E�r�����[�̊Ԉ����𑜓x
			//pstmt1.setString(1,"602");
			//rs1 = pstmt1.executeQuery();
			//String mabikiDpi = null;
			//if(rs1.next()){
			//	mabikiDpi = rs1.getString("VALUE");
			//}
			// 603�E�E�E�Q�l�}�o�͂̍ő匏��
			pstmt1.setString(1, "603");
			rs1 = pstmt1.executeQuery();
			String printRequestMax = null;
			if (rs1.next()) {
				printRequestMax = rs1.getString("VALUE");
			}
			// 604�E�E�E�A�N�Z�X���x���ύX�\�ȐE��
			pstmt1.setString(1, "604");
			rs1 = pstmt1.executeQuery();
			String aclvChangablePosition = null;
			if (rs1.next()) {
				aclvChangablePosition = rs1.getString("VALUE");
			}
			// 605�E�E�E�����ɂ�����}�ԑ����̍Œ���̕�����
			pstmt1.setString(1, "605");
			rs1 = pstmt1.executeQuery();
			String minimumIuputDrwgChar = null;
			if (rs1.next()) {
				minimumIuputDrwgChar = rs1.getString("VALUE");
			}
			// 606�E�E�E�r�����[�Ԉ����ΏۃT�C�Y1(100dpi)
			pstmt1.setString(1, "606");
			rs1 = pstmt1.executeQuery();
			String mabiki100dpiSize = null;
			if (rs1.next()) {
				mabiki100dpiSize = rs1.getString("VALUE");
			}
			// 607�E�E�E�r�����[�Ԉ����ΏۃT�C�Y2(200dpi)
			pstmt1.setString(1, "607");
			rs1 = pstmt1.executeQuery();
			String mabiki200dpiSize = null;
			if (rs1.next()) {
				mabiki200dpiSize = rs1.getString("VALUE");
			}
			// 2013.06.27 yamagishi add. start
			// 608�E�E�E�����}�Ԏw�莞�̌����\����
			pstmt1.setString(1, "608");
			rs1 = pstmt1.executeQuery();
			String multipleDrwgNoMax = null;
			if (rs1.next()) {
				multipleDrwgNoMax = rs1.getString("VALUE");
			}
			// 2013.06.27 yamagishi add. end
			// 2013.07.10 yamagishi add. start
			// 609�E�E�E�Y���}���͒l
			pstmt1.setString(1, "609");
			rs1 = pstmt1.executeQuery();
			String correspondingValue = null;
			if (rs1.next()) {
				correspondingValue = rs1.getString("VALUE");
			}
			// 610�E�E�E�@���Ǘ��}���͒l�i��j
			pstmt1.setString(1, "610");
			rs1 = pstmt1.executeQuery();
			String confidentialValue = null;
			if (rs1.next()) {
				confidentialValue = rs1.getString("VALUE");
			}
			// 611�E�E�E�@���Ǘ��}���͒l�i�ɔ�j
			pstmt1.setString(1, "611");
			rs1 = pstmt1.executeQuery();
			String strictlyConfidentialValue = null;
			if (rs1.next()) {
				strictlyConfidentialValue = rs1.getString("VALUE");
			}
			// 2013.07.10 yamagishi add. end
			// 700�E�E�E�{���p�X�^���v�ʒu(W)
			pstmt1.setString(1, "700");
			rs1 = pstmt1.executeQuery();
			String viewStampW = null;
			if (rs1.next()) {
				viewStampW = rs1.getString("VALUE");
			}
			// 701�E�E�E�{���p�X�^���v�ʒu(L)
			pstmt1.setString(1, "701");
			rs1 = pstmt1.executeQuery();
			String viewStampL = null;
			if (rs1.next()) {
				viewStampL = rs1.getString("VALUE");
			}
			// 705�E�E�E�{���p�X�^���v�����Z�x
			pstmt1.setString(1, "705");
			rs1 = pstmt1.executeQuery();
			String ViewStampDeep = null;
			if (rs1.next()) {
				ViewStampDeep = rs1.getString("VALUE");
			}
			// 707�E�E�E�{���p�X�^���v���t�`��
			pstmt1.setString(1, "707");
			rs1 = pstmt1.executeQuery();
			String viewStampDateFormat = null;
			if (rs1.next()) {
				viewStampDateFormat = rs1.getString("VALUE");
			}
			// 708�E�E�E�{���p�X�^���v�ł̐}�Ԃ��󎚂���H
			pstmt1.setString(1, "708");
			rs1 = pstmt1.executeQuery();
			String dispDrwgNoWithView = null;
			if (rs1.next()) {
				dispDrwgNoWithView = rs1.getString("VALUE");
			}
			// 2013.07.12 yamagishi add. start
			// 729�E�E�E�Y���}�p�X�^���v�ʒu(W����)
			pstmt1.setString(1, "729");
			rs1 = pstmt1.executeQuery();
			String correspondingStampW = null;
			if (rs1.next()) {
				correspondingStampW = rs1.getString("VALUE");
			}
			// 730�E�E�E�Y���}�p�X�^���v�ʒu(L����)
			pstmt1.setString(1, "730");
			rs1 = pstmt1.executeQuery();
			String correspondingStampL = null;
			if (rs1.next()) {
				correspondingStampL = rs1.getString("VALUE");
			}
			// 734�E�E�E�Y���}�p�X�^���v�����Z�W
			pstmt1.setString(1, "734");
			rs1 = pstmt1.executeQuery();
			String correspondingStampDeep = null;
			if (rs1.next()) {
				correspondingStampDeep = rs1.getString("VALUE");
			}
			// 736�E�E�E�Y���}�p�X�^���v����
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
			// CLOSE����
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
