/*
 * �쐬��: 2004/01/13
 *
 * ���̐������ꂽ�R�����g�̑}�������e���v���[�g��ύX���邽��
 * �E�B���h�E > �ݒ� > Java > �R�[�h���� > �R�[�h�ƃR�����g
 */
package tyk.drasap.genzu_irai;

/**
 * @author KAWAI
 *
 * ���̐������ꂽ�R�����g�̑}�������e���v���[�g��ύX���邽��
 * �E�B���h�E > �ݒ� > Java > �R�[�h���� > �R�[�h�ƃR�����g
 */
public class RequestResultElement {
	
	String drwgNo;
	String flag;
	String touroku;

	public RequestResultElement(String newDrwgNo, String newFlag, String newTouroku){
		this.drwgNo = newDrwgNo;
		this.flag = newFlag;
		this.touroku = newTouroku;
	}
	
	/**
	 * Returns the id.
	 * @return String
	 */
	public String getDrwgNo() {
		return drwgNo;
	}

	/**
	 * Returns the name.
	 * @return String
	 */
	public String getFlag() {
		return flag;
	}
	
	/**
	 * Returns the name.
	 * @return String
	 */
	public String getTouroku() {
		return touroku;
	}

}
