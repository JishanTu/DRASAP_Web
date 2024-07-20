/**
 * -----------------------------------------------------------------------------------
 * JTEKT DRASAP 図番参照WEB
 * -----------------------------------------------------------------------------------
 * Project Name : tyk.drasap.aplot
 * File Name    : AbstractAPlotSchemaBase.java
 * Name         : A-PLOT出図データベーステーブルベース抽象クラス
 * Description  :
 * -----------------------------------------------------------------------------------
 * Author       : 2018/02/21 hideki_sugiyama
 * -----------------------------------------------------------------------------------
 * Modify       :
 *   Date       Name
 *
 * -----------------------------------------------------------------------------------
 */
package tyk.drasap.aplot;

import java.util.HashMap;

/**
 * A-PLOT出図データベーステーブルベース抽象クラス.
 * 本クラスは抽象クラスなので、直接使用することはできない.
 * @author hideki_sugiyama
 * @
 */
@SuppressWarnings("serial")
public abstract class  AbstractAPlotSchemaBase extends HashMap<String, Object> {

	/** スキーマ名の接頭辞. */
	public static final String SCHEMA_PREFIX = "oj";


	/** スキーマ名. */
	private String schemaName = null;

	/**
	 * コンストラクタ .
	 * @param schema スキーマ名.
	 */
	public AbstractAPlotSchemaBase(String schema) {
		this.schemaName = schema;
	}


	/**
	 * スキーマ名取得.
	 * @return schemaName
	 */
	public String getSchemaName() {
		return this.schemaName;
	}

	/**
	 * insertする値をシングルクォートで括る
	 * @param val 値
	 * @param type 値の型
	 * @return 変換した値
	 */
	protected String quart(String val, String type) {
		String retVal = val;
		if ( "char".equalsIgnoreCase(type) ||
				"varchar".equalsIgnoreCase(type) ||
				"timestamp".equalsIgnoreCase(type) ) {
			// 括る
			retVal = "'" + val + "'";
		}
		return retVal;
	}


	/**
	 * スキーマ名からスキーマ番号に変換する.
	 * @param schemaName 名.
	 * @return スキーマ番号.
	 */
	public int getSchemaNo() {
		// スキーマ番号設定.
		String str = this.schemaName.toLowerCase();
		String no = str.replace(SCHEMA_PREFIX, "");
		if ( !"".equals(no) ) {
			return Integer.parseInt(no);
		}
		return 0;
	}

	@Override
	public Object put(String key, Object value) {
		// 文字列の場合はトリムする.
		if ( value instanceof String ) {
			super.put(key, value.toString().trim());
		}
		// それ以外はそのまま入れる.
		return super.put(key, value);
	}

}
