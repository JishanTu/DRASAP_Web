package tyk.drasap.search;

import static tyk.drasap.common.DrasapPropertiesFactory.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import tyk.drasap.common.CsvItemStrList;
import tyk.drasap.common.DrasapPropertiesFactory;
import tyk.drasap.common.User;

/**
 * 検索ファンクションで使用するユーティリティ
 * @author fumi
 * 作成日: 2004/04/16
 * @version 2013/06/14 yamagishi
 */
public class SearchUtil {
	private CsvItemStrList searchItemStrList;

	public SearchUtil() {
		super();
		try {
			// 2013.06.14 yamagishi modified. start
			//			String beaHome = System.getenv("BEA_HOME");
			//			if (beaHome == null) beaHome = System.getenv("OCE_BEA_HOME");
			//			if (beaHome == null) beaHome = DrasapPropertiesFactory.getDrasapProperties(this).getProperty("oce.BEA_BASE");
			//			searchItemStrList = new CsvItemStrList(beaHome + DrasapPropertiesFactory.getDrasapProperties(this).getProperty("tyk.csvdef.searchItemStrList.path"));
			String apServerHome = System.getenv(BEA_HOME);
			if (apServerHome == null) {
				apServerHome = System.getenv(CATALINA_HOME);
			}
			if (apServerHome == null) {
				apServerHome = System.getenv(OCE_AP_SERVER_HOME);
			}
			if (apServerHome == null) {
				apServerHome = DrasapPropertiesFactory.getDrasapProperties(this).getProperty(OCE_AP_SERVER_BASE);
			}
			searchItemStrList = new CsvItemStrList(apServerHome + DrasapPropertiesFactory.getDrasapProperties(this).getProperty("tyk.csvdef.searchItemStrList.path"));
			// 2013.06.14 yamagishi modified. end
		} catch (FileNotFoundException e) {
			return;
		} catch (IOException e) {
			return;
		}
	}

	/**
	 * 指定したユーザーが使用可能な属性のキーまたは名称がセットされたArrayListを返す。
	 * 注意!! 図番は除かれています!!
	 * isKeyで、セット対象を指定する。
	 * @param user このユーザーについて使用可能か？
	 * @param isKey trueならキーを。falseなら名称を。
	 * @return セットされたArrayList。
	 */
	public ArrayList<String> createEnabledAttrList(User user, boolean isKey) {

		ArrayList<String> list = new ArrayList<String>();
		// 以下、ユーザーが使用可能かチェックする
		if (user.isMachineJpDisplay()) {
			addSearchAttr(list, "MACHINE_JP", isKey, user.getLanguage());
		}
		if (user.isMachineEnDisplay()) {
			addSearchAttr(list, "MACHINE_EN", isKey, user.getLanguage());
		}
		if (user.isUsedForDisplay()) {
			addSearchAttr(list, "USED_FOR", isKey, user.getLanguage());
		}
		if (user.isMaterialDisplay()) {
			addSearchAttr(list, "MATERIAL", isKey, user.getLanguage());
		}
		if (user.isTreatmentDisplay()) {
			addSearchAttr(list, "TREATMENT", isKey, user.getLanguage());
		}
		if (user.isSupplyerJpDisplay()) {
			addSearchAttr(list, "SUPPLYER_JP", isKey, user.getLanguage());
		}
		if (user.isSupplyerEnDisplay()) {
			addSearchAttr(list, "SUPPLYER_EN", isKey, user.getLanguage());
		}
		if (user.isSupplyerTypeDisplay()) {
			addSearchAttr(list, "SUPPLYER_TYPE", isKey, user.getLanguage());
		}
		if (user.isDrwgTypeDisplay()) {
			addSearchAttr(list, "DRWG_TYPE", isKey, user.getLanguage());
		}
		if (user.isDrwgSizeDisplay()) {
			addSearchAttr(list, "DRWG_SIZE", isKey, user.getLanguage());
		}
		if (user.isProcurementDisplay()) {
			addSearchAttr(list, "PROCUREMENT", isKey, user.getLanguage());
		}
		if (user.isIssueDisplay()) {
			addSearchAttr(list, "ISSUE", isKey, user.getLanguage());
		}
		if (user.isSupplyDisplay()) {
			addSearchAttr(list, "SUPPLY", isKey, user.getLanguage());
		}
		if (user.isAttach01Display()) {
			addSearchAttr(list, "ATTACH01", isKey, user.getLanguage());
		}
		if (user.isAttach02Display()) {
			addSearchAttr(list, "ATTACH02", isKey, user.getLanguage());
		}
		if (user.isAttach03Display()) {
			addSearchAttr(list, "ATTACH03", isKey, user.getLanguage());
		}
		if (user.isAttach04Display()) {
			addSearchAttr(list, "ATTACH04", isKey, user.getLanguage());
		}
		if (user.isAttach05Display()) {
			addSearchAttr(list, "ATTACH05", isKey, user.getLanguage());
		}
		if (user.isAttach06Display()) {
			addSearchAttr(list, "ATTACH06", isKey, user.getLanguage());
		}
		if (user.isAttach07Display()) {
			addSearchAttr(list, "ATTACH07", isKey, user.getLanguage());
		}
		if (user.isAttach08Display()) {
			addSearchAttr(list, "ATTACH08", isKey, user.getLanguage());
		}
		if (user.isAttach09Display()) {
			addSearchAttr(list, "ATTACH09", isKey, user.getLanguage());
		}
		if (user.isAttach10Display()) {
			addSearchAttr(list, "ATTACH10", isKey, user.getLanguage());
		}
		if (user.isMachineNoDisplay()) {
			addSearchAttr(list, "MACHINE_NO", isKey, user.getLanguage());
		}
		if (user.isMachineNameDisplay()) {
			addSearchAttr(list, "MACHINE_NAME", isKey, user.getLanguage());
		}
		if (user.isMachineSpec1Display()) {
			addSearchAttr(list, "MACHINE_SPEC1", isKey, user.getLanguage());
		}
		if (user.isMachineSpec2Display()) {
			addSearchAttr(list, "MACHINE_SPEC2", isKey, user.getLanguage());
		}
		if (user.isMachineSpec3Display()) {
			addSearchAttr(list, "MACHINE_SPEC3", isKey, user.getLanguage());
		}
		if (user.isMachineSpec4Display()) {
			addSearchAttr(list, "MACHINE_SPEC4", isKey, user.getLanguage());
		}
		if (user.isMachineSpec5Display()) {
			addSearchAttr(list, "MACHINE_SPEC5", isKey, user.getLanguage());
		}
		if (user.isEngineerDisplay()) {
			addSearchAttr(list, "ENGINEER", isKey, user.getLanguage());
		}
		if (user.isProhibitDisplay()) {
			addSearchAttr(list, "PROHIBIT", isKey, user.getLanguage());
		}
		if (user.isProhibitDateDisplay()) {
			addSearchAttr(list, "PROHIBIT_DATE", isKey, user.getLanguage());
		}
		if (user.isProhibitEmpnoDisplay()) {
			addSearchAttr(list, "PROHIBIT_EMPNO", isKey, user.getLanguage());
		}
		if (user.isProhibitNameDisplay()) {
			addSearchAttr(list, "PROHIBIT_NAME", isKey, user.getLanguage());
		}
		if (user.isPagesDisplay()) {
			addSearchAttr(list, "PAGES", isKey, user.getLanguage());
		}
		if (user.isAclDisplay()) {
			addSearchAttr(list, "ACL_ID", isKey, user.getLanguage());
		}
		if (user.isAclUpdateDisplay()) {
			addSearchAttr(list, "ACL_UPDATE", isKey, user.getLanguage());
		}
		if (user.isAclEmpnoDisplay()) {
			addSearchAttr(list, "ACL_EMPNO", isKey, user.getLanguage());
		}
		if (user.isAclNameDisplay()) {
			addSearchAttr(list, "ACL_NAME", isKey, user.getLanguage());
		}
		if (user.isCreateDateDisplay()) {
			addSearchAttr(list, "CREATE_DATE", isKey, user.getLanguage());
		}
		if (user.isCreateUserDisplay()) {
			addSearchAttr(list, "CREATE_USER", isKey, user.getLanguage());
		}
		if (user.isCadTypeDisplay()) {
			addSearchAttr(list, "CAD_TYPE", isKey, user.getLanguage());
		}
		if (user.isAttachMaxDisplay()) {
			addSearchAttr(list, "ATTACH_MAX", isKey, user.getLanguage());
		}
		if (user.isLatestDisplay()) {
			addSearchAttr(list, "LATEST_FLAG", isKey, user.getLanguage());
		}
		if (user.isReplaceDisplay()) {
			addSearchAttr(list, "REPLACE_FLAG", isKey, user.getLanguage());
		}
		if (user.isCreateDivDisplay()) {
			addSearchAttr(list, "CREATE_DIV", isKey, user.getLanguage());
		}
		if (user.isMediaIdDisplay()) {
			addSearchAttr(list, "MEDIA_ID", isKey, user.getLanguage());
		}
		if (user.isTwinDrwgNoDisplay()) {
			addSearchAttr(list, "TWIN_DRWG_NO", isKey, user.getLanguage());
		}

		return list;
	}

	/**
	 * 検索用のキーまたは名称をリストに追加する。
	 * isKeyにより追加するのがキーか？名称か？変わる。
	 * 名称は、SearchInfo.getAttrMap()から取得する。
	 * @param list 追加される先のList
	 * @param key 追加するキー。名称追加の場合、名称を探すキーとなる。
	 * @param isKey trueならキーを。falseなら名称を。
	 */
	private void addSearchAttr(List<String> list, String key, boolean isKey, String language) {
		ArrayList<String> searchItemStr = searchItemStrList.searchLineData(key);

		if (searchItemStr == null) {
			return;
		}

		if (isKey) {
			list.add(searchItemStr.get(0));
		} else if ("Japanese".equals(language)) {
			list.add(searchItemStr.get(1));
		} else {
			list.add(searchItemStr.get(2));
		}
	}

	public String getSearchAttr(User user, String key, boolean isKey) {
		ArrayList<String> searchItemStr = searchItemStrList.searchLineData(key);

		if (searchItemStr == null) {
			return "";
		}

		if (isKey) {
			return searchItemStr.get(0);
		}
		if ("Japanese".equals(user.getLanguage())) {
			return searchItemStr.get(1);
		}
		return searchItemStr.get(2);
	}

	public String getSearchAttr(String language, String key, boolean isKey) {
		ArrayList<String> searchItemStr = searchItemStrList.searchLineData(key);
		if (searchItemStr == null) {
			return "";
		}
		if (isKey) {
			return searchItemStr.get(0);
		}
		if ("Japanese".equals(language)) {
			return searchItemStr.get(1);
		}
		return searchItemStr.get(2);
	}

}
