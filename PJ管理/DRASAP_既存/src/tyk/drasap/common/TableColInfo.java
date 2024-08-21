package tyk.drasap.common;

/**
 * テーブルのカラム情報を表す。
 * 
 * @author Y.eto
 * 作成日: 2006/05/10
 */
public class TableColInfo {
    String column_name = "";// 項目名
	String data_type = "";// 項目タイプ
	int data_length;// サイズ
	String nullable="Y";
	// ------------------------------------------------------- コンストラクタ
	/**
	 * コンストラクタ
	 */
	public TableColInfo(String newColumn_name, String newData_type, String newData_length, String newNullable) {
		this.column_name = newColumn_name;
		this.data_type = newData_type;
		this.data_length = Integer.valueOf(newData_length).intValue();
		this.nullable = newNullable;
	}
	// ------------------------------------------------------- getter,setter
	/**
	 * @return
	 */
	public String getColumn_name() {
		return column_name;
	}

	/**
	 * @return
	 */
	public String getData_type() {
		return data_type;
	}

	/**
	 * @return
	 */
	public int getData_length() {
		return data_length;
	}
	/**
	 * @return
	 */
	public String getNullable() {
		return nullable;
	}
}
