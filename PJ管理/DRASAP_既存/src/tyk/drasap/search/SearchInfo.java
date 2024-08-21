package tyk.drasap.search;

import java.util.HashMap;

/**
 * 検索に関しての情報を保持するクラス。
 */
public class SearchInfo {
	static HashMap<String, String> attrMap = new HashMap<String, String>();
	static{
		attrMap.put("CREATE_DATE", "作成日");
		attrMap.put("CREATE_USER", "作成者");
		attrMap.put("MACHINE_JP", "装置名称(和)");
		attrMap.put("MACHINE_EN", "装置名称(英)");
		attrMap.put("USED_FOR", "用途");
		attrMap.put("MATERIAL", "材質");
		attrMap.put("TREATMENT", "熱・表面処理");
		attrMap.put("PROCUREMENT", "調達区分");
		attrMap.put("SUPPLYER_JP", "メーカー名(和)");
		attrMap.put("SUPPLYER_EN", "メーカー名(英)");
		attrMap.put("SUPPLYER_TYPE", "メーカー形式");
		attrMap.put("ATTACH01", "添付図番1");
		attrMap.put("ATTACH02", "添付図番2");
		attrMap.put("ATTACH03", "添付図番3");
		attrMap.put("ATTACH04", "添付図番4");
		attrMap.put("ATTACH05", "添付図番5");
		attrMap.put("ATTACH06", "添付図番6");
		attrMap.put("ATTACH07", "添付図番7");
		attrMap.put("ATTACH08", "添付図番8");
		attrMap.put("ATTACH09", "添付図番9");
		attrMap.put("ATTACH10", "添付図番10");
		attrMap.put("MACHINE_NO", "装置NO");
		attrMap.put("MACHINE_NAME", "機種名称");
		attrMap.put("MACHINE_SPEC1", "装置仕様1");
		attrMap.put("MACHINE_SPEC2", "装置仕様2");
		attrMap.put("MACHINE_SPEC3", "装置仕様3");
		attrMap.put("MACHINE_SPEC4", "装置仕様4");
		attrMap.put("MACHINE_SPEC5", "装置仕様5");
		attrMap.put("DRWG_TYPE", "図面種類");
		attrMap.put("DRWG_SIZE", "図面サイズ");
		attrMap.put("ISSUE", "提出区分");
		attrMap.put("SUPPLY", "消耗区分");
		attrMap.put("CAD_TYPE", "CAD種別");
		attrMap.put("ENGINEER", "設計者名");
		attrMap.put("PROHIBIT", "使用禁止区分");
		attrMap.put("PROHIBIT_DATE", "使用禁止日時");
		attrMap.put("PROHIBIT_EMPNO", "使用禁止者職番");
		attrMap.put("PROHIBIT_NAME", "使用禁止者名前");
		attrMap.put("PAGES", "ページ数");
		attrMap.put("ACL_ID", "アクセスレベル");
		attrMap.put("ACL_UPDATE", "ｱｸｾｽﾚﾍﾞﾙ変更日");
		attrMap.put("ACL_EMPNO", "ｱｸｾｽﾚﾍﾞﾙ変更者職番");
		attrMap.put("ACL_NAME", "ｱｸｾｽﾚﾍﾞﾙ変更者名");
		attrMap.put("ATTACH_MAX", "添付図数");
		attrMap.put("LATEST_FLAG", "最新図番区分");
		attrMap.put("REPLACE_FLAG", "差替フラグ");
		attrMap.put("CREATE_DIV", "作成部署コード");
		attrMap.put("MEDIA_ID", "メディアID");
		attrMap.put("TWIN_DRWG_NO", "1物2品番図番");
		
	}

	/**
	 * @return
	 */
	public static HashMap<String, String> getAttrMap() {
		return attrMap;
	}

}
