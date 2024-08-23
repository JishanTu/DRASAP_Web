package tyk.drasap.search;

/**
 * @author fumi
 * �쐬��: 2004/01/20
 */
public class AclvChangeElement {
	String drwgNo;// �}��
	String drwgNoFormated;// �t�H�[�}�b�g���ꂽ�}�ԁE�E�E�R���X�g���N�^�œ����ɍ쐬�����
	String oldAclId;// �ύX�O�̃A�N�Z�X���x��ID
	String newAclId;// �ύX��̃A�N�Z�X���x��ID
	String oldProhibit;// �ύX�O�̎g�p�֎~�敪�E�E�EOK�ANG�ŕێ�����
	String newProhibit;// �ύX��̎g�p�֎~�敪
	boolean selected = true;// �I���`�F�b�N�{�b�N�X�ɑΉ�

	// --------------------------------------------- �R���X�g���N�^
	/**
	 * �R���X�g���N�^�B
	 * SearchResultElement�����ɂ���B
	 */
	public AclvChangeElement(SearchResultElement searchResultElement) {
		this.drwgNo = searchResultElement.drwgNo;
		this.drwgNoFormated = searchResultElement.drwgNoFormated;
		this.oldAclId = searchResultElement.getAttr("ACL_ID");// �A�N�Z�����x���l
		this.newAclId = this.oldAclId;
		this.oldProhibit = searchResultElement.getAttr("PROHIBIT");
		this.newProhibit = this.oldProhibit;
	}
	/**
	 * �ύX�O�Ɣ�r���āA�f�[�^�̕ύX�����邩?
	 * ��r����̂̓A�N�Z�X���x��ID,�g�p�֎~�敪
	 * @return �ύX������� true
	 */
	public boolean isModified(){
		// �A�N�Z�X���x��ID�A�g�p�֎~�敪�̂����ꂩ���ύX����Ă����
		// true
		return !(this.newAclId.equals(this.oldAclId) &&
					this.newProhibit.equals(this.oldProhibit));
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
