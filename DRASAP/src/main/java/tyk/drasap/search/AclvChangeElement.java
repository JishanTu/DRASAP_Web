package tyk.drasap.search;

/**
 * @author fumi
 * 作成日: 2004/01/20
 */
public class AclvChangeElement {
	String drwgNo;// 図番
	String drwgNoFormated;// フォーマットされた図番・・・コンストラクタで同時に作成される
	String oldAclId;// 変更前のアクセスレベルID
	String newAclId;// 変更後のアクセスレベルID
	String oldProhibit;// 変更前の使用禁止区分・・・OK、NGで保持する
	String newProhibit;// 変更後の使用禁止区分
	boolean selected = true;// 選択チェックボックスに対応

	// --------------------------------------------- コンストラクタ
	/**
	 * コンストラクタ。
	 * SearchResultElementを元にする。
	 */
	public AclvChangeElement(SearchResultElement searchResultElement) {
		drwgNo = searchResultElement.drwgNo;
		drwgNoFormated = searchResultElement.drwgNoFormated;
		oldAclId = searchResultElement.getAttr("ACL_ID");// アクセルレベル値
		newAclId = oldAclId;
		oldProhibit = searchResultElement.getAttr("PROHIBIT");
		newProhibit = oldProhibit;
	}

	/**
	 * 変更前と比較して、データの変更があるか?
	 * 比較するのはアクセスレベルID,使用禁止区分
	 * @return 変更があれば true
	 */
	public boolean isModified() {
		// アクセスレベルID、使用禁止区分のいずれかが変更されていれば
		// true
		return !(newAclId.equals(oldAclId) &&
				newProhibit.equals(oldProhibit));
	}

	// --------------------------------------------- getter,setter
	/**
	 * @return
	 */
	public String getDrwgNo() {
		return drwgNo;
	}

	/**
	 * @return
	 */
	public String getDrwgNoFormated() {
		return drwgNoFormated;
	}

	/**
	 * @return
	 */
	public String getNewAclId() {
		return newAclId;
	}

	/**
	 * @return
	 */
	public String getNewProhibit() {
		return newProhibit;
	}

	/**
	 * @return
	 */
	public String getOldAclId() {
		return oldAclId;
	}

	/**
	 * @return
	 */
	public String getOldProhibit() {
		return oldProhibit;
	}

	/**
	 * @param string
	 */
	public void setNewAclId(String string) {
		newAclId = string;
	}

	/**
	 * @param string
	 */
	public void setNewProhibit(String string) {
		newProhibit = string;
	}

	/**
	 * @return
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * @param b
	 */
	public void setSelected(boolean b) {
		selected = b;
	}

}
