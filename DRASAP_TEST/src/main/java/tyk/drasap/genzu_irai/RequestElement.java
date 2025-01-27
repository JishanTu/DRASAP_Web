package tyk.drasap.genzu_irai;

/**
 * @author KAWAI
 *
 * ���̐������ꂽ�R�����g�̑}�������e���v���[�g��ύX���邽��
 * �E�B���h�E > �ݒ� > Java > �R�[�h���� > �R�[�h�ƃR�����g
 */
public class RequestElement {

	String id;//�v�����^ID
	String name;//�v�����^��

	public RequestElement(String newId, String newName) {
		id = newId;
		name = newName;
	}

	/**
	 * Returns the id.
	 * @return String
	 */
	public String getId() {
		return id;
	}

	/**
	 * Returns the name.
	 * @return String
	 */
	public String getName() {
		return name;
	}

}
