package tyk.drasap.search;

import java.util.ArrayList;

import tyk.drasap.common.StringCheck;

/**
 * 検索SQLのOrder部分を作成する。
 */
public class SearchOrderSentenceMaker {
	ArrayList<String> sortOrderList;// ソート順序を保持する
	ArrayList<String> sqlList;// SQL文を保持する ex) DRWG_NO DESC・・・図番の逆順

	// --------------------------------------------------------- コンストラクタ
	/**
	 * コンストラクタ
	 */
	public SearchOrderSentenceMaker() {
		sortOrderList = new ArrayList<String>();
		sqlList = new ArrayList<String>();
	}

	// --------------------------------------------------------- メソッド
	/**
	 * ソート条件を加える
	 * @param columnName ソートする属性名(ex. DRWG_NO)
	 * @param sortWay ソートする方向(ex. 昇順,降順)
	 * @param sortOrder ソートする順序(1,2,...)
	 */
	public void addOrderCondition(String columnName, String sortWay, String sortOrder) {
		// 「ソートする方向」と「ソートする属性名」は入力されている必要がある
		sortWay = StringCheck.trimWsp(sortWay);//「ソートする方向」は全角スペースのため、trimする
		if (sortWay == null || sortWay.length() == 0 ||
				columnName == null || columnName.length() == 0) {
			return;
		}
		// ソート順が設定されていないときは、9番とする
		if (sortOrder == null || sortOrder.length() == 0) {
			sortOrder = "99";
		}
		// sqlListに追加する部分を事前に作成しておく
		String sqlPart = columnName;
		if ("降順".equals(sortWay) || "Desc".equals(sortWay)) {
			sqlPart += " DESC";
		}
		// 挿入位置を探す
		boolean inserted = false;// 挿入したらtrue
		for (int i = 0; i < sortOrderList.size(); i++) {
			int sortOrderTemp = Integer.parseInt(sortOrder);
			if (sortOrderTemp < Integer.parseInt(sortOrderList.get(i))) {
				sortOrderList.add(i, sortOrder);// ソート順序のリストに追加
				sqlList.add(i, sqlPart);// SQL文保持のリストに追加
				inserted = true;
				break;
			}
		}
		if (!inserted) {// 直前のforループで挿入位置が見つからなかったら、最後に追加
			sortOrderList.add(sortOrder);// ソート順序のリストに追加
			sqlList.add(sqlPart);// SQL文保持のリストに追加
		}

	}

	/**
	 * ソート条件を全て加えたあとから、「order by ・・・」を取り出す。
	 * 例) order by DRWG_NO
	 * @return SQLのorder句。例) order by DRWG_NO
	 */
	public String getSqlOrder() {
		if (sqlList.size() == 0) {
			// Orderが何も指定されていなければ
			return "order by DRWG_NO";
		}
		// Orderが指定されていれば
		StringBuilder sbSql1 = new StringBuilder();
		sbSql1.append("order by ");
		for (int i = 0; i < sqlList.size(); i++) {
			sbSql1.append(sqlList.get(i));
			sbSql1.append(',');
		}
		// 最後の「,」を除く
		sbSql1.deleteCharAt(sbSql1.length() - 1);
		//
		return sbSql1.toString();
	}

}
