package tyk.drasap.common;


/**
 * 利用者グループとアクセスレベルの間の関連を表すクラス。
 * 利用者グループ・アクセスレベル関連テーブル(USER_GRP_ACL_RELATION)に対応。
 *
 * @author 2013/06/25 yamagishi
 */
public class UserGrpAclRelation {

	String userGrpCode = "";// 利用者グループコード
	String aclId = "";		// アクセスレベル
	String aclValue = "";	// アクセスレベル値

	// ------------------------------------------ コンストラクター
	/**
	 * constructor
	 */
	public UserGrpAclRelation() {
	}

	// ------------------------------------------ getter,setter
	/**
	 * userGrpCodeを取得します。
	 * @return userGrpCode
	 */
	public String getUserGrpCode() {
	    return userGrpCode;
	}
	/**
	 * userGrpCodeを設定します。
	 * @param userGrpCode userGrpCode
	 */
	public void setUserGrpCode(String userGrpCode) {
	    this.userGrpCode = userGrpCode;
	}

	/**
	 * aclIdを取得します。
	 * @return aclId
	 */
	public String getAclId() {
	    return aclId;
	}
	/**
	 * aclIdを設定します。
	 * @param aclId aclId
	 */
	public void setAclId(String aclId) {
	    this.aclId = aclId;
	}

	/**
	 * aclValueを取得します。
	 * @return aclValue
	 */
	public String getAclValue() {
	    return aclValue;
	}
	/**
	 * aclValueを設定します。
	 * @param aclValue aclValue
	 */
	public void setAclValue(String aclValue) {
	    this.aclValue = aclValue;
	}
}
