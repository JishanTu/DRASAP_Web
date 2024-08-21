package tyk.drasap.common;

import java.util.Date;


/**
 * ACL�ꊇ�X�V��ʗp�̃A�b�v���[�h�f�[�^��\���N���X�B
 * ACL�A�b�v���[�h�f�[�^�e�[�u��(ACL_UPLOAD_TABLE)�ɑΉ��B
 *
 * @author 2013/07/09 yamagishi
 */
public class AclUpload {

	String aclUpdateNo = "";		// �Ǘ�NO
	String userId = "";				// ���[�UID
	String userName = "";			// ����
	String recordNo = "";			// ���R�[�h�ԍ�
	String machineJp = "";			// ���u
	String machineNo = "";			// ���uNO
	String drwgNo = "";				// ��z�}��
	String machineCode = "";		// ���u�R�[�h
	String detailNo = "";			// ���הԍ�
	String pages = "";				// ��
	String itemNo = "";				// �i��
	String itemNoShort = "";		// �i�ԁi�󔒁A�n�C�t���u-�v�����������p�啶���j
	String grpCode = "";			// �O���[�v
	String correspondingFlag = "";	// �Y���}�敪
	String correspondingValue = "";	// �Y���}�敪�i�\���j�l
	String confidentialFlag = "";	// �@���Ǘ��}�敪
	String confidentialValue = "";	// �@���Ǘ��}�敪�i�\���j�l
	String preUpdateAcl = "";		// �ύX�O�A�N�Z�X���x��
	String preUpdateAclName = "";	// �ύX�O�A�N�Z�X���x����
	String postUpdateAcl = "";		// �ύX��A�N�Z�X���x��
	String postUpdateAclName = "";	// �ύX��A�N�Z�X���x����
	String itemName = "";			// �i���i�K�i�^���j
	String message = "";			// ���b�Z�[�W
	Date aclUpdate = null;			// �X�V����

	// ------------------------------------------ constructor
	/**
	 * constructor
	 */
	public AclUpload() {
	}

	// ------------------------------------------ getter,setter
	/**
	 * aclUpdateNo���擾���܂��B
	 * @return aclUpdateNo
	 */
	public String getAclUpdateNo() {
		return aclUpdateNo;
	}
	/**
	 * aclUpdateNo��ݒ肵�܂��B
	 * @param aclUpdateNo aclUpdateNo
	 */
	public void setAclUpdateNo(String aclUpdateNo) {
		this.aclUpdateNo = aclUpdateNo;
	}

	/**
	 * userId���擾���܂��B
	 * @return userId
	 */
	public String getUserId() {
		return userId;
	}
	/**
	 * userId��ݒ肵�܂��B
	 * @param userId userId
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * userName���擾���܂��B
	 * @return userName
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * userName��ݒ肵�܂��B
	 * @param userName userName
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * recordNo���擾���܂��B
	 * @return recordNo
	 */
	public String getRecordNo() {
		return recordNo;
	}
	/**
	 * recordNo��ݒ肵�܂��B
	 * @param recordNo recordNo
	 */
	public void setRecordNo(String recordNo) {
		this.recordNo = recordNo;
	}

	/**
	 * machineJp���擾���܂��B
	 * @return machineJp
	 */
	public String getMachineJp() {
		return machineJp;
	}
	/**
	 * machineJp��ݒ肵�܂��B
	 * @param machineJp machineJp
	 */
	public void setMachineJp(String machineJp) {
		this.machineJp = machineJp;
	}

	/**
	 * machineNo���擾���܂��B
	 * @return machineNo
	 */
	public String getMachineNo() {
		return machineNo;
	}
	/**
	 * machineNo��ݒ肵�܂��B
	 * @param machineNo machineNo
	 */
	public void setMachineNo(String machineNo) {
		this.machineNo = machineNo;
	}

	/**
	 * drwgNo���擾���܂��B
	 * @return drwgNo
	 */
	public String getDrwgNo() {
		return drwgNo;
	}
	/**
	 * drwgNo��ݒ肵�܂��B
	 * @param drwgNo drwgNo
	 */
	public void setDrwgNo(String drwgNo) {
		this.drwgNo = drwgNo;
	}

	/**
	 * machineCode���擾���܂��B
	 * @return machineCode
	 */
	public String getMachineCode() {
		return machineCode;
	}
	/**
	 * machineCode��ݒ肵�܂��B
	 * @param machineCode machineCode
	 */
	public void setMachineCode(String machineCode) {
		this.machineCode = machineCode;
	}

	/**
	 * detailNo���擾���܂��B
	 * @return detailNo
	 */
	public String getDetailNo() {
		return detailNo;
	}
	/**
	 * detailNo��ݒ肵�܂��B
	 * @param detailNo detailNo
	 */
	public void setDetailNo(String detailNo) {
		this.detailNo = detailNo;
	}

	/**
	 * pages���擾���܂��B
	 * @return pages
	 */
	public String getPages() {
		return pages;
	}
	/**
	 * pages��ݒ肵�܂��B
	 * @param pages pages
	 */
	public void setPages(String pages) {
		this.pages = pages;
	}

	/**
	 * itemNo���擾���܂��B
	 * @return itemNo
	 */
	public String getItemNo() {
		return itemNo;
	}
	/**
	 * itemNo��ݒ肵�܂��B
	 * @param itemNo itemNo
	 */
	public void setItemNo(String itemNo) {
		this.itemNo = itemNo;
	}

	/**
	 * correspondingFlag���擾���܂��B
	 * @return correspondingFlag
	 */
	public String getCorrespondingFlag() {
		return correspondingFlag;
	}
	/**
	 * correspondingFlag��ݒ肵�܂��B
	 * @param correspondingFlag correspondingFlag
	 */
	public void setCorrespondingFlag(String correspondingFlag) {
		this.correspondingFlag = correspondingFlag;
	}

	/**
	 * correspondingValue���擾���܂��B
	 * @return correspondingValue
	 */
	public String getCorrespondingValue() {
		return correspondingValue;
	}
	/**
	 * correspondingValue��ݒ肵�܂��B
	 * @param correspondingValue correspondingValue
	 */
	public void setCorrespondingValue(String correspondingValue) {
		this.correspondingValue = correspondingValue;
	}

	/**
	 * confidentialFlag���擾���܂��B
	 * @return confidentialFlag
	 */
	public String getConfidentialFlag() {
		return confidentialFlag;
	}
	/**
	 * confidentialFlag��ݒ肵�܂��B
	 * @param confidentialFlag confidentialFlag
	 */
	public void setConfidentialFlag(String confidentialFlag) {
		this.confidentialFlag = confidentialFlag;
	}

	/**
	 * confidentialValue���擾���܂��B
	 * @return confidentialValue
	 */
	public String getConfidentialValue() {
		return confidentialValue;
	}
	/**
	 * confidentialValue��ݒ肵�܂��B
	 * @param confidentialValue confidentialValue
	 */
	public void setConfidentialValue(String confidentialValue) {
		this.confidentialValue = confidentialValue;
	}

	/**
	 * itemNoShort���擾���܂��B
	 * @return itemNoShort
	 */
	public String getItemNoShort() {
		return itemNoShort;
	}
	/**
	 * itemNoShort��ݒ肵�܂��B
	 * @param itemNoShort itemNoShort
	 */
	public void setItemNoShort(String itemNoShort) {
		this.itemNoShort = itemNoShort;
	}

	/**
	 * grpCode���擾���܂��B
	 * @return grpCode
	 */
	public String getGrpCode() {
		return grpCode;
	}
	/**
	 * grpCode��ݒ肵�܂��B
	 * @param grpCode grpCode
	 */
	public void setGrpCode(String grpCode) {
		this.grpCode = grpCode;
	}

	/**
	 * preUpdateAcl���擾���܂��B
	 * @return preUpdateAcl
	 */
	public String getPreUpdateAcl() {
		return preUpdateAcl;
	}
	/**
	 * preUpdateAcl��ݒ肵�܂��B
	 * @param preUpdateAcl preUpdateAcl
	 */
	public void setPreUpdateAcl(String preUpdateAcl) {
		this.preUpdateAcl = preUpdateAcl;
	}

	/**
	 * preUpdateAclName���擾���܂��B
	 * @return preUpdateAclName
	 */
	public String getPreUpdateAclName() {
		return preUpdateAclName;
	}
	/**
	 * preUpdateAclName��ݒ肵�܂��B
	 * @param preUpdateAclName preUpdateAclName
	 */
	public void setPreUpdateAclName(String preUpdateAclName) {
		this.preUpdateAclName = preUpdateAclName;
	}

	/**
	 * postUpdateAcl���擾���܂��B
	 * @return postUpdateAcl
	 */
	public String getPostUpdateAcl() {
		return postUpdateAcl;
	}
	/**
	 * postUpdateAcl��ݒ肵�܂��B
	 * @param postUpdateAcl postUpdateAcl
	 */
	public void setPostUpdateAcl(String postUpdateAcl) {
		this.postUpdateAcl = postUpdateAcl;
	}

	/**
	 * postUpdateAclName���擾���܂��B
	 * @return postUpdateAclName
	 */
	public String getPostUpdateAclName() {
		return postUpdateAclName;
	}
	/**
	 * postUpdateAclName��ݒ肵�܂��B
	 * @param postUpdateAclName postUpdateAclName
	 */
	public void setPostUpdateAclName(String postUpdateAclName) {
		this.postUpdateAclName = postUpdateAclName;
	}

	/**
	 * itemName���擾���܂��B
	 * @return itemName
	 */
	public String getItemName() {
		return itemName;
	}
	/**
	 * itemName��ݒ肵�܂��B
	 * @param itemName itemName
	 */
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	/**
	 * message���擾���܂��B
	 * @return message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * message��ݒ肵�܂��B
	 * @param message message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * aclUpdate���擾���܂��B
	 * @return aclUpdate
	 */
	public Date getAclUpdate() {
		return aclUpdate;
	}
	/**
	 * aclUpdate��ݒ肵�܂��B
	 * @param aclUpdate aclUpdate
	 */
	public void setAclUpdate(Date aclUpdate) {
		this.aclUpdate = aclUpdate;
	}
}
