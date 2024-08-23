package tyk.drasap.genzu_irai;

/**
 * ���}�ɍ�ƈ˗������ŕ\������1���̃f�[�^�ɑΉ�
 * @author fumi
 */
public class RequestHistoryElement implements Comparable {
	private String completeDate;// ��Ɗ�������
	private String completeUser;// ��Ɗ����Җ�
	private String requestDate;// �˗�����
	private String jobName;// �W���u��
	private String drwgNo;// �}��
	private String goukiNo;// ���@
	private String genzuContent;// ���}���e
	private String copies;// ����
	private String scaleMode;// �k���敪
	private String scaleSize;// �k���T�C�Y
	private String message;// ���b�Z�[�W
	private String requestUser;// �˗��Җ�
	private String deptName;// �˗�����
	
	/**
	 * �R���X�g���N�^�[
	 */
	public RequestHistoryElement(String newCompleteDate, String newCompleteUser,
					String newRequestDate, String newJobName, String newDrwgNo, String newGoukiNo,
					String newGenzuContent, String newCopies, String newScaleMode, String newScaleSize,
					String newMessage, String newRequestUser, String newDeptName) {
		this.completeDate = newCompleteDate;// ��Ɗ�������
		if(completeDate == null) completeDate="";
		this.completeUser = newCompleteUser;// ��Ɗ����Җ�
		if(completeUser == null) completeUser="";
		this.requestDate = newRequestDate;// �˗�����
		this.jobName = newJobName;// �W���u��
		if(jobName == null) jobName="";
		this.drwgNo = newDrwgNo;// �}��
		this.goukiNo = newGoukiNo;// ���@
		if(goukiNo == null) goukiNo="";
		this.genzuContent = newGenzuContent;// ���}���e
		if(genzuContent == null) genzuContent="";
		this.copies = newCopies;// ����
		if(copies == null) copies="";
		this.scaleMode = newScaleMode;// �k���敪
		if(scaleMode == null) scaleMode="";
		this.scaleSize = newScaleSize;// �k���T�C�Y
		if(scaleSize == null) scaleSize="";
		this.message = newMessage;// ���b�Z�[�W
		if(message == null) message="";
		this.requestUser = newRequestUser;// �˗��Җ�
		this.deptName = newDeptName;// �˗�����
	}
	
	/**
	 * Collections#sort()���g�p���邽�߂ɁA��������B
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		if(! (o instanceof RequestHistoryElement)){
			// ��r���悤�Ƃ����Ώۂ� RequestHistoryElement �łȂ��B
			throw new ClassCastException("RequestHistoryElement�ɃL���X�g�ł��܂���B");
		}
		RequestHistoryElement target = (RequestHistoryElement) o;
		// ��r�́u��Ɗ��������v��
		return this.completeDate.compareTo(target.getCompleteDate());
	}

	// -------------------------------------- getter,setter
	/**
	 * @return
	 */
	public String getCompleteDate() {
		return completeDate;
	}

	/**
	 * @return
	 */
	public String getCompleteUser() {
		return completeUser;
	}

	/**
	 * @return
	 */
	public String getCopies() {
		return copies;
	}

	/**
	 * @return
	 */
	public String getDeptName() {
		return deptName;
	}

	/**
	 * @return
	 */
	public String getDrwgNo() {
		return drwgNo;
	}

	/**
	 * @return
	 */
	public String getGenzuContent() {
		return genzuContent;
	}

	/**
	 * @return
	 */
	public String getGoukiNo() {
		return goukiNo;
	}

	/**
	 * @return
	 */
	public String getJobName() {
		return jobName;
	}

	/**
	 * @return
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @return
	 */
	public String getRequestDate() {
		return requestDate;
	}

	/**
	 * @return
	 */
	public String getRequestUser() {
		return requestUser;
	}

	/**
	 * @return
	 */
	public String getScaleMode() {
		return scaleMode;
	}

	/**
	 * @return
	 */
	public String getScaleSize() {
		return scaleSize;
	}

}
