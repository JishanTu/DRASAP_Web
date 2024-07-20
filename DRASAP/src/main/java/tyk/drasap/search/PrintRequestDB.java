package tyk.drasap.search;

import tyk.drasap.aplot.APlotPrintRequest;

/**
 *
 * 2018/02/21 �ύX �Q�l�}�o�}��A-PLOT�o�}�ɕύX.
 * APlotPrintRequest�N���X���p�����Ă�����ɋ@�\���ڍs����.
 * @author hideki_sugiyama
 *
 * ���ȉ�������
 * �Q�l�}�o�͗p�e�[�u������舵��DB�N���X�B
 * '04.Nov.23 �}�Ԃ����M���O����悤�ɕύX
 * 2005-Mar-4 PrintLoger�Ń��M���O��ǉ��B
 * @author FUMI
 * �쐬��: 2004/01/16
 * �ύX�� $Date: 2005/03/18 04:33:22 $ $Author: fumi $
 * @version 2013/06/24 yamagishi
 */
public class PrintRequestDB extends APlotPrintRequest {
	//
	// �V�K��APlotPrintRequest�N���X���쐬���Čp�������ׁA
	// ���L�̃��\�b�h�͎g�p���Ȃ��Ȃ���.
	//
	//	/**
	//	 * SearchResultElement�̃��X�g�����ɁA�Q�l�}�o�͗p�e�[�u���ɏ����o���B
	//	 * ���̂Ƃ��ASearchResultElement�́A�I������Ă��āA����\�Ȑ}�ʂł�����̂̂�
	//	 * @param searchResultList
	//	 * @param printer
	//	 * @param user
	//	 * @param conn
	//	 * @return
	//	 * @throws Exception
	//	 */
	//	public static int insertRequest(ArrayList<SearchResultElement> searchResultList, Printer printer, User user, Connection conn) throws Exception{
	//
	//		PreparedStatement pstmt1 = null;
	//		int cnt = 0;
	//		String logDrwgNo = null;// ���M���O�̂��߂̐}�ԁBcatch�߂Ŏg�p���邽�߂ɒǉ��B2005-Mar-4 by Hirata.
	//		try{
	//			// autoCommit��ture��
	//			conn.setAutoCommit(false);
	//			StringBuffer sbSql1 = new StringBuffer("insert into PRINT_REQUEST_TABLE");
	//			sbSql1.append("(JOB_ID,JOB_NAME,PRINTER_ID,DRWG_NO,OUTPUT_SIZE,COPIES,");
	//			sbSql1.append("STAMP_FLAG,USER_ID,USER_NAME,TRANSIT_STAT,REQUEST_DATE)");
	//			sbSql1.append(" values");
	//			// �Q�l�}�o�͗p�e�[�u����JOB_ID�̓V�[�P���X�𗘗p����
	//			sbSql1.append("(TRIM(TO_CHAR(PRINT_REQUEST_SEQ.NEXTVAL,'0000000000000000')),");
	//			sbSql1.append(" '�Q�l�}�o��', ?, ?, ?, ?,");
	//			sbSql1.append(" ?, ?, ?, 'SET', sysdate)");
	//			//
	//			pstmt1 = conn.prepareStatement(sbSql1.toString());
	//			// ���[�U�[�����Z�b�g����
	//			pstmt1.setString(6, user.getId());// ���[�U�[ID
	//			pstmt1.setString(7, user.getName());// ���[�U�[��
	//			// �}�Ԃ����M���O���邽�߈ꎞ�ۊǂ��郊�X�g
	//			List<String> updatedDrwgNoList = new ArrayList<String>();
	//			for(int i = 0; i < searchResultList.size(); i++){
	//				SearchResultElement searchResultElement = (SearchResultElement) searchResultList.get(i);
	//				// �I������Ă�����̂ŁA�����[�U�[������\�̐}�ʂ̂݁Ainsert����
	//// 2013.06.26 yamagishi modified. start
	////				if(searchResultElement.isSelected() && user.isPrintableByReq(searchResultElement)){
	//				if (searchResultElement.isSelected() && user.isPrintableByReq(searchResultElement, conn, (i > 0))) {
	//// 2013.06.26 yamagishi modified. end
	//					pstmt1.setString(1, printer.getId());// �v�����^ID
	//					pstmt1.setString(2, searchResultElement.getDrwgNo());// �}��
	//					logDrwgNo = searchResultElement.getDrwgNo();// ���M���O�̂��߂ɐ}�Ԃ��R�s�[���Ă���
	//					pstmt1.setString(3, searchResultElement.getDecidePrintSize());// �o�̓T�C�Y�E�E�EdecidePrintSize���g�p����
	//					pstmt1.setString(4, searchResultElement.getCopies());// ����
	//					if(printer.isEucPrinter()){
	//						// EUC�v�����^�Ȃ�
	//						pstmt1.setString(5, user.isEucStamp()?"1":"0");// �X�^���v�L���B�X�^���v����Ȃ�1�B
	//					} else {
	//						// ��p�v�����^�Ȃ�
	//						pstmt1.setString(5, user.isPltrStamp()?"1":"0");// �X�^���v�L���B�X�^���v����Ȃ�1�B
	//					}
	//// 2013.06.24 yamagishi modified. start
	//					// �d�����N�G�X�g�̒����̂��߂̃��O�B2005-Mar-4 by Hirata.
	////					PrintLoger.info(PrintLoger.ACT_WRITE, user.getId(), searchResultElement.getDrwgNo());
	//					PrintLoger.info(PrintLoger.ACT_WRITE, user, searchResultElement.getDrwgNo());
	//// 2013.06.24 yamagishi modified. end
	//					// ������Oracle�ɏ����o��
	//					cnt += pstmt1.executeUpdate();// insert
	//					// ��Ń��M���O���邽�߂Ƀ��X�g�Ɉꎞ�ۊǂ���
	//					updatedDrwgNoList.add(searchResultElement.getDrwgNo());
	//				}
	//			}
	//			// �R�~�b�g
	//			conn.commit();
	//			// '04.Nov.23 �}�Ԃ����M���O����悤�ɕύX
	//			// �A�N�Z�X���O���܂Ƃ߂čs��
	//			AccessLoger.loging(user, AccessLoger.FID_OUT_DRWG,
	//					(String[])updatedDrwgNoList.toArray(new String[0]), user.getSys_id());
	//
	//			return cnt;
	//
	//		} catch(Exception e){
	//// 2013.06.24 yamagishi modified. start
	//			// �d�����N�G�X�g�̒����̂��߂̃��O�B2005-Mar-4 by Hirata.
	////			PrintLoger.info(PrintLoger.FAILED_WRITE, user.getId(), logDrwgNo);
	//			PrintLoger.info(PrintLoger.FAILED_WRITE, user, logDrwgNo);
	//// 2013.06.24 yamagishi modified. end
	//			// rollback����
	//			try{ conn.rollback(); } catch(Exception e2){}
	//			// ��O���Ăяo�����ɓ�����
	//			throw e;
	//
	//		} finally {
	//			// close����
	//			try{ pstmt1.close(); } catch(Exception e){}
	//		}
	//	}

}
