package tyk.drasap.common;


/**
 * ���p�҃O���[�v�ƃA�N�Z�X���x���̊Ԃ̊֘A��\���N���X�B
 * ���p�҃O���[�v�E�A�N�Z�X���x���֘A�e�[�u��(USER_GRP_ACL_RELATION)�ɑΉ��B
 *
 * @author 2013/06/25 yamagishi
 */
public class UserGrpAclRelation {

	String userGrpCode = "";// ���p�҃O���[�v�R�[�h
	String aclId = "";		// �A�N�Z�X���x��
	String aclValue = "";	// �A�N�Z�X���x���l

	// ------------------------------------------ �R���X�g���N�^�[
	/**
	 * constructor
	 */
	public UserGrpAclRelation() {
	}

	// ------------------------------------------ getter,setter
	/**
	 * userGrpCode���擾���܂��B
	 * @return userGrpCode
	 */
	public String getUserGrpCode() {
	    return userGrpCode;
	}
	/**
	 * userGrpCode��ݒ肵�܂��B
	 * @param userGrpCode userGrpCode
	 */
	public void setUserGrpCode(String userGrpCode) {
	    this.userGrpCode = userGrpCode;
	}

	/**
	 * aclId���擾���܂��B
	 * @return aclId
	 */
	public String getAclId() {
	    return aclId;
	}
	/**
	 * aclId��ݒ肵�܂��B
	 * @param aclId aclId
	 */
	public void setAclId(String aclId) {
	    this.aclId = aclId;
	}

	/**
	 * aclValue���擾���܂��B
	 * @return aclValue
	 */
	public String getAclValue() {
	    return aclValue;
	}
	/**
	 * aclValue��ݒ肵�܂��B
	 * @param aclValue aclValue
	 */
	public void setAclValue(String aclValue) {
	    this.aclValue = aclValue;
	}
}
