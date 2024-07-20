/**
 * -----------------------------------------------------------------------------------
 * JTEKT DRASAP 図番参照WEB
 * -----------------------------------------------------------------------------------
 * Project Name : tyk.drasap.aplot
 * File Name    : APlotRecipientMasterDB.java
 * Name         : A-PLOT出図 配布先マスタ情報 クラス
 * Description  : A-PLOT用の配布先マスタ情報を管理するクラス.
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
 * A-PLOTプリンタの配布先マスタ情報を管理するクラス.
 *
 * 指定のプリンターIDの配布先情報を該当するスキーマの配布先マスタテーブル[RECIPIENT_MASTER]から取得。
 *
 * @author hideki_sugiyama
 */
@SuppressWarnings("serial")
public class APlotRecipientMasterDB extends AbstractAPlotSchemaBase {


	/** Logger（log4j） */
	@SuppressWarnings("deprecation")
	private static Category category = Category.getInstance(APlotRecipientMasterDB.class.getName());

	/**
	 * コンストラクタ.
	 * @param schema スキーマ名.
	 */
	public APlotRecipientMasterDB(String schema) {
		super(schema);
	}


	/**
	 * 配布先マスタテーブル(RECIPIENT_MASTER)からプリンタ情報を取得.
	 * @param schemaName スキーマ名.
	 * @param ids プリンターID（配列）
	 * @return SQL文字列.
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
		// プリンタマスタテーブルを検索するSQLを返す.
		return String.format("SELECT * FROM %s.RECIPIENT_MASTER R, %s.PWS_PARAM_TBL P WHERE R.PRINTER_ID in ( %s ) AND R.RECIPIENT_ID = P.RECIPIENT_ID order by R.RECIPIENT_ID",
				schemaName,
				schemaName, sb.toString());
	}


	/**
	 * プリンタIDからA-PLOT配布先マスタを取得する.
	 * @param stmt DBコネクトステートメント.
	 * @param ids プリンタID（配列）
	 * @return 配布先マスタ（配列）
	 * @throws SQLException
	 */
	public static APlotRecipientMasterDB[] getRecipientMaster(Statement stmt, String schemaName, String[] ids) throws SQLException {

		ArrayList<APlotRecipientMasterDB> list = new ArrayList<APlotRecipientMasterDB>();
		list.clear();

		// 配布先マスタテーブルを取得.
		ResultSet rs = stmt.executeQuery(selectAPlotRecipientMaster(schemaName, ids));
		try {
			// 取得した情報からスキーマ名を取得.
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
				// パラメータ管理情報からの取得.
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
			category.fatal("A-PLOT出図配布先マスタ取得でSQLエラー", ex);
			throw ex;
		} finally {
			if ( rs != null ) rs.close();
		}

		if ( list.size() > 0 ) return list.toArray(new APlotRecipientMasterDB[0]);
		// データなしの場合はNULLを返す.
		return null;
	}

}
