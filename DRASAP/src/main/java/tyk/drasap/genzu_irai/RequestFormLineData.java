package tyk.drasap.genzu_irai;

/**
 * ���}�ɍ�ƈ˗��ɂ�����s�f�[�^�B
 * �`�F�b�N���[�`���Ŏg�p���邽�߂ɁA�V���ɒ�`�����B
 * @author fumi
 */
public class RequestFormLineData {
	private String gouki;// ���@
	private String genzu;// ���}���e
	private String kaisiNo;// �J�n�ԍ�
	private String syuuryouNo;//�I���ԍ�
	private String busuu;//����
	private String syukusyou;//�k���敪
	private String size;//�k���T�C�Y
	
	/**
	 * �R���X�g���N�^
	 */
	public RequestFormLineData(String gouki, String genzu, String kaisiNo, String syuuryouNo,
				String busuu, String syukusyou, String size) {
		this.gouki = gouki;// ���@
		this.genzu = genzu;// ���}���e
		this.kaisiNo = kaisiNo;// �J�n�ԍ�
		this.syuuryouNo = syuuryouNo;//�I���ԍ�
		this.busuu = busuu;//����
		this.syukusyou = syukusyou;//�k���敪
		this.size = size;//�k���T�C�Y
	}

	//---------------------------------------------------------- getter,setter
	/**
	 * @return
	 */
	public String getBusuu() {
		return busuu;
	}

	/**
	 * @return
	 */
	public String getGenzu() {
		return genzu;
	}

	/**
	 * @return
	 */
	public String getGouki() {
		return gouki;
	}

	/**
	 * @return
	 */
	public String getKaisiNo() {
		return kaisiNo;
	}

	/**
	 * @return
	 */
	public String getSize() {
		return size;
	}

	/**
	 * @return
	 */
	public String getSyukusyou() {
		return syukusyou;
	}

	/**
	 * @return
	 */
	public String getSyuuryouNo() {
		return syuuryouNo;
	}

}
