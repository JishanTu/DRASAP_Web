/**
 * -----------------------------------------------------------------------------------
 * JTEKT DRASAP �}�ԎQ��WEB
 * -----------------------------------------------------------------------------------
 * Project Name : tyk.drasap.aplot
 * File Name    : APlotRecipientMasterDB.java
 * Name         : A-PLOT�o�} �z�z��}�X�^��� �N���X
 * Description  : A-PLOT�p�̔z�z��}�X�^�����Ǘ�����N���X.
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

import org.apache.log4j.Category;

/**
 * A-PLOT�v�����^�̔z�z��}�X�^�����Ǘ�����N���X.
 *
 * �w��̃v�����^�[ID�̔z�z������Y������X�L�[�}�̔z�z��}�X�^�e�[�u��[RECIPIENT_MASTER]����擾�B
 *
 * @author hideki_sugiyama
 */
@SuppressWarnings("serial")
public class APlotRecipientMasterDB extends AbstractAPlotSchemaBase {


	/** Logger�ilog4j�j */
	@SuppressWarnings("deprecation")
	private static Category category = Category.getInstance(APlotRecipientMasterDB.class.getName());

	/**
	 * �R���X�g���N�^.
	 * @param schema �X�L�[�}��.
	 */
	public APlotRecipientMasterDB(String schema) {
		super(schema);
	}


	/**
	 * �z�z��}�X�^�e�[�u��(RECIPIENT_MASTER)����v�����^�����擾.
	 * @param schemaName �X�L�[�}��.
	 * @param ids �v�����^�[ID�i�z��j
	 * @return SQL������.
	 */
	private static String selectAPlotRecipientMaster(String schemaName, String[] ids ) {
		StringBuilder sb = new StringBuilder("");
		for ( String i : ids ) {
			i = i.trim();
			if ( sb.length() > 0 ) {
				sb.append(", ");
			}
			sb.append(String.format("'%s'", i));
		}
		// �v�����^�}�X�^�e�[�u������������SQL��Ԃ�.
		return String.format("SELECT * FROM %s.RECIPIENT_MASTER R, %s.PWS_PARAM_TBL P WHERE R.PRINTER_ID in ( %s ) AND R.RECIPIENT_ID = P.RECIPIENT_ID order by R.RECIPIENT_ID",
				schemaName,
				schemaName, sb.toString());
	}


	/**
	 * �v�����^ID����A-PLOT�z�z��}�X�^���擾����.
	 * @param stmt DB�R�l�N�g�X�e�[�g�����g.
	 * @param ids �v�����^ID�i�z��j
	 * @return �z�z��}�X�^�i�z��j
	 * @throws SQLException
	 */
	public static APlotRecipientMasterDB[] getRecipientMaster(Statement stmt, String schemaName, String[] ids) throws SQLException {

		ArrayList<APlotRecipientMasterDB> list = new ArrayList<APlotRecipientMasterDB>();
		list.clear();

		// �z�z��}�X�^�e�[�u�����擾.
		ResultSet rs = stmt.executeQuery(selectAPlotRecipientMaster(schemaName, ids));
		try {
			// �擾������񂩂�X�L�[�}�����擾.
			while(rs.next()){
				//
				APlotRecipientMasterDB data = new APlotRecipientMasterDB(schemaName);
				//
				data.clear();
				data.put("RECIPIENT_ID", rs.getString("RECIPIENT_ID"));
				data.put("RECIPIENT_NAME", rs.getString("RECIPIENT_NAME"));
				data.put("PRINTER_ID", rs.getString("PRINTER_ID"));
				data.put("COPIES", rs.getInt("COPIES"));
				data.put("SORT", rs.getString("SORT"));
				data.put("OUTPUT_ORDER", rs.getString("OUTPUT_ORDER"));
				data.put("DATA_FOLDER_PATH", rs.getString("DATA_FOLDER_PATH"));
				data.put("MULTI_PRINTER_MODE", rs.getString("MULTI_PRINTER_MODE"));
				// �p�����[�^�Ǘ���񂩂�̎擾.
				data.put("MEDIA_TYPE", rs.getString("MEDIA_TYPE"));
				data.put("FINISHING", rs.getString("FINISHING"));
				data.put("OUTPUT_LOCATION", rs.getString("OUTPUT_LOCATION"));
				data.put("CUT_TYPE", rs.getString("CUT_TYPE"));
				data.put("MEDIA_SOURCE", rs.getString("MEDIA_SOURCE"));
				data.put("LEADING_EDGE", rs.getString("LEADING_EDGE"));
				data.put("TRAILING_EDGE", rs.getString("TRAILING_EDGE"));
				data.put("BINDING_EDGE", rs.getString("BINDING_EDGE"));
				//
				list.add(data);
			}
		} catch (SQLException ex) {
			category.fatal("A-PLOT�o�}�z�z��}�X�^�擾��SQL�G���[", ex);
			throw ex;
		} finally {
			if ( rs != null ) rs.close();
		}

		if ( list.size() > 0 ) return list.toArray(new APlotRecipientMasterDB[0]);
		// �f�[�^�Ȃ��̏ꍇ��NULL��Ԃ�.
		return null;
	}

}
