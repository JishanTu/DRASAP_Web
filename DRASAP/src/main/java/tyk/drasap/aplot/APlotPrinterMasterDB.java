/**
 * -----------------------------------------------------------------------------------
 * JTEKT DRASAP �}�ԎQ��WEB
 * -----------------------------------------------------------------------------------
 * Project Name : tyk.drasap.aplot
 * File Name    : APlotPrinterMasterDB.java
 * Name         : A-PLOT�o�} �v�����^�}�X�^��� �N���X
 * Description  : A-PLOT�p�̃v�����^�}�X�^�����Ǘ�����N���X.
 * -----------------------------------------------------------------------------------
 * Author       : 2018/02/21 hideki_sugiyama
 * -----------------------------------------------------------------------------------
 * Modify       :
 *   Date       Name
 *
 * -----------------------------------------------------------------------------------
 */
package tyk.drasap.aplot;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.log4j.Logger;

/**
 * A-PLOT �v�����^�}�X�^�����Ǘ�����N���X.
 *
 * �w��̃v�����^�[ID�ŁA�v�����^�����}�X�^�e�[�u��[PRINTER_ASSIGN_MASTER]����Ή�����X�L�[�}���擾.
 * �擾�����X�L�[�}�̃v�����^�}�X�^�e�[�u��[PRINTER_MASTER]�̏����擾���ĊǗ�����.
 * @author hideki_sugiyama
 *
 */
public class APlotPrinterMasterDB extends AbstractAPlotSchemaBase {

	/** Logger�ilog4j�j */
	private static Logger category = Logger.getLogger(APlotPrinterMasterDB.class.getName());

	/**
	 * �R���X�g���N�^.
	 *
	 * @param schema �X�L�[�}��.
	 * @param vaults �}�ʊi�[�t�H���_�p�X.
	 * @param spool �X�v�[����t�H���_�p�X.
	 */
	public APlotPrinterMasterDB(String schema) {
		super(schema);
	}

	// �悭�g��������getter��p�ӂ��� >>>>>>>>>>>>>>

	/**
	 * �v�����^�[ID�擾.
	 * @return �v�����^�[ID.
	 */
	public String getPrinterId() {
		return get("PRINTER_ID").toString();
	}

	/**
	 * �v�����^�[���擾.
	 * @return �v�����^�[��.
	 */
	public String getPrinterName() {
		return get("PRINTER_NAME").toString();
	}

	/**
	 * �V���[�gID�擾.
	 * @return �V���[�gID.
	 */
	public String getShortId() {
		return get("SHORT_ID").toString();
	}

	/**
	 * DRASAP���̃v�����^�����}�X�^�e�[�u��[PRINTER_ASSIGN_MASTER]����X�L�[�}�����擾����SQL���쐬.
	 * @param ids �v�����^ID�i�z��j.
	 * @return sql������.
	 */
	private static String selectPrintAssignMaster(String[] ids) {
		// �v�����^�����}�X�^�e�[�u������������SQL��Ԃ�.
		StringBuilder sb = new StringBuilder("");
		for (String i : ids) {
			i = i.trim();
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(String.format("'%s'", i));
		}
		return String.format("SELECT * FROM  PRINTER_ASSIGN_MASTER WHERE AVAILABLE_FLAG = 1 AND PRINTER_ID in (%s) order by PRINTER_ID", sb.toString());
	}

	/**
	 * �v�����^�}�X�^�e�[�u��[PRINTER_MASTER]����v�����^�����擾����SQL���쐬.
	 * @param printerId �v�����^ID.
	 * @param schema �X�L�[�}��.
	 * @return sql������.
	 */
	private static String selectPrintMaster(String printerId, String schema) {

		// �v�����^�}�X�^�e�[�u������������SQL��Ԃ�.
		return String.format("SELECT * FROM %s.PRINTER_MASTER WHERE PRINTER_ID = '%s' order by PRINTER_ID", schema, printerId);
	}

	/**
	 * �v�����^�}�X�^�����擾.
	 * @param stmt DB�R�l�N�g�X�e�[�g�����g.
	 * @param printerId �v�����^ID.
	 * @param schemaName �X�L�[�}��.
	 * @return �v�����^�}�X�^�N���X.
	 * @throws SQLException
	 */
	private static APlotPrinterMasterDB queryPrinterMaster(Statement stmt, String printerId, String schema) throws SQLException {

		APlotPrinterMasterDB newData = null;
		// �w��X�L�[�}�̃v�����^�}�X�^���擾.
		ResultSet rs = stmt.executeQuery(selectPrintMaster(printerId, schema));
		try {
			// �擾������񂩂�X�L�[�}�����擾.
			while (rs.next()) {
				// �}�X�^�[�f�[�^�擾.
				newData = new APlotPrinterMasterDB(schema);
				newData.put("PRINTER_ID", rs.getString("PRINTER_ID"));
				newData.put("SHORT_ID", rs.getString("SHORT_ID"));
				newData.put("PRINTER_NAME", rs.getString("PRINTER_NAME"));
				newData.put("OUTPUT_DEVICE", rs.getString("OUTPUT_DEVICE"));
				newData.put("DESCRIPTION", rs.getString("DESCRIPTION"));
				newData.put("SUBMISSION_WAIT", rs.getInt("SUBMISSION_WAIT"));
				newData.put("QUEUE_WAIT", rs.getInt("QUEUE_WAIT"));
				newData.put("OUTPUT_WAIT", rs.getInt("OUTPUT_WAIT"));
				newData.put("A0_OUTPUT", rs.getString("A0_OUTPUT"));
				newData.put("A1_OUTPUT", rs.getString("A1_OUTPUT"));
				newData.put("A2_OUTPUT", rs.getString("A2_OUTPUT"));
				newData.put("A3_OUTPUT", rs.getString("A3_OUTPUT"));
				newData.put("A4_OUTPUT", rs.getString("A4_OUTPUT"));
				newData.put("A0L_OUTPUT", rs.getString("A0L_OUTPUT"));
				newData.put("A1L_OUTPUT", rs.getString("A1L_OUTPUT"));
				newData.put("A2L_OUTPUT", rs.getString("A2L_OUTPUT"));
				newData.put("A3L_OUTPUT", rs.getString("A3L_OUTPUT"));
				newData.put("A4L_OUTPUT", rs.getString("A4L_OUTPUT"));
				newData.put("MAX_SIZE", rs.getString("MAX_SIZE"));
				newData.put("EUC_NAME", rs.getString("EUC_NAME"));
				newData.put("HORIZONTAL_ROTATION", rs.getString("HORIZONTAL_ROTATION"));
				newData.put("ROTATE_EXTRA180", rs.getString("ROTATE_EXTRA180"));
				newData.put("DOCS_PER_JOBTICKET", rs.getInt("DOCS_PER_JOBTICKET"));
				newData.put("SRI_USE", rs.getString("SRI_USE"));
				newData.put("SRI_WAIT", rs.getInt("SRI_WAIT"));
				newData.put("MAX_SEND_VOLUME", rs.getInt("MAX_SEND_VOLUME"));
				newData.put("NETWORK_SERVER", rs.getString("NETWORK_SERVER"));
				newData.put("NETWORK_USER", rs.getString("NETWORK_USER"));
				newData.put("NETWORK_PASSWORD", rs.getString("NETWORK_PASSWORD"));
				newData.put("NETWORK_DIR", rs.getString("NETWORK_DIR"));
				newData.put("NETWORK_DRIVE", rs.getString("NETWORK_DRIVE"));
				newData.put("FTP_PASSIVE", rs.getString("FTP_PASSIVE"));
				newData.put("NETWORK_PROTOCOL", rs.getString("NETWORK_PROTOCOL"));
				newData.put("RETRY_COUNT", rs.getInt("RETRY_COUNT"));
				newData.put("MEDIA_FEED_STRATEGY_1", rs.getString("MEDIA_FEED_STRATEGY_1"));
				newData.put("MEDIA_FEED_STRATEGY_2", rs.getString("MEDIA_FEED_STRATEGY_2"));
				newData.put("MEDIA_FEED_STRATEGY_3", rs.getString("MEDIA_FEED_STRATEGY_3"));
				newData.put("MEDIA_FEED_STRATEGY_4", rs.getString("MEDIA_FEED_STRATEGY_4"));
				newData.put("PHYSICAL_ROTATION_PORTRAIT", rs.getString("PHYSICAL_ROTATION_PORTRAIT"));
				newData.put("PHYSICAL_SCALING_NONSTD_SIZE", rs.getString("PHYSICAL_SCALING_NONSTD_SIZE"));
				newData.put("TIMEOUT_SEC", rs.getInt("TIMEOUT_SEC"));
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			throw ex;
		} finally {
			if (rs != null) {
				rs.close();
			}
		}
		return newData;
	}

	/**
	 * �v�����^ID����A-PLOT�v�����^�}�X�^���擾����.
	 * @param stmt DB�R�l�N�g�X�e�[�g�����g.
	 * @param ids �v�����^ID�i�z��j
	 * @return �v�����^�}�X�^�i�z��j
	 * @throws SQLException
	 */
	public static APlotPrinterMasterDB[] getPrinterMaster(Statement stmt, String[] ids) throws SQLException {

		ArrayList<APlotPrinterMasterDB> list = new ArrayList<APlotPrinterMasterDB>();
		list.clear();

		// �v�����^�����}�X�^�e�[�u���̏�񂩂�X�L�[�}���擾.
		ResultSet rs = stmt.executeQuery(selectPrintAssignMaster(ids));
		try {
			// �擾������񂩂�X�L�[�}�����擾.
			while (rs.next()) {
				// �}�X�^�[����.
				APlotPrinterMasterDB data = APlotPrinterMasterDB.queryPrinterMaster(stmt, rs.getString("PRINTER_ID"), rs.getString("SCHEMA_NAME"));
				if (data != null) {
					list.add(data);
				}
			}
		} catch (SQLException ex) {
			category.fatal("A-PLOT�o�}�v�����^�}�X�^�擾��SQL�G���[", ex);
			throw ex;
		} finally {
			if (rs != null) {
				rs.close();
			}
		}

		if (list.size() > 0) {
			return list.toArray(new APlotPrinterMasterDB[0]);
		}
		// �f�[�^�Ȃ��̏ꍇ��NULL��Ԃ�.
		return null;
	}

}
