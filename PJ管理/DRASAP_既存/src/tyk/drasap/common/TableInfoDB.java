package tyk.drasap.common;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * ORACLEシステムからカラム情報を取得する。
 * @author Y.eto
 * 作成日: 2006/05/10
 */
public class TableInfoDB {
	/**
	 */
	public static TableInfo getTableInfoArray(String table_name, Connection conn)
						throws Exception {
	    ArrayList<TableColInfo> colInfos = new ArrayList<TableColInfo>();
		TableInfo tableinfo = new TableInfo(table_name);
		// チェック
		if(table_name.length() == 0){
			return tableinfo;
		}
		//
		Statement stmt1 = null;
		ResultSet rs1 = null;
		try{
			// ユーザーグループCDで検索する
			StringBuffer sbSql1 = new StringBuffer("select column_name, data_type, data_length, nullable from user_tab_cols ");
			sbSql1.append(" where table_name = ");
			sbSql1.append("'");
			sbSql1.append(table_name);
			sbSql1.append("' ");
			sbSql1.append(" order by column_id");
			// 
			stmt1 = conn.createStatement();
			rs1 = stmt1.executeQuery(sbSql1.toString());
			//
			while(rs1.next()){
			    colInfos.add(
						new TableColInfo(rs1.getString("column_name"), rs1.getString("data_type"), rs1.getString("data_length"), rs1.getString("nullable"))
		        );
			}
			tableinfo.colInfos = colInfos;
			
		} catch (Exception e) {
			throw e;
			
		} finally {
			// CLOSE処理
			try{ rs1.close(); } catch (Exception e) {}
			try{ stmt1.close(); } catch (Exception e) {}
		}
		//
		return tableinfo;
	}

}
