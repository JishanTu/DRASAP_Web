package tyk.drasap.common;

/**
 * �V�X�e���̏���ێ�����B
 * ���̓��e�͊Ǘ��Ґݒ�}�X�^�[����ǂݎ��B
 * ���O�C�����Ɏ擾����B
 *
 * @version 2013/06/27 yamagishi
 */
public class DrasapInfo {
	String viewDBDrive = "F:";
	int searchWarningCount=1000;// �����ɂ�����x������
	int searchLimitCount=20000;// �����ɂ�����Ő،���(���p�\�ő匏��)
	//String mabikiDpi="200";// �r�����[��A0L����A0�ɊԈ��������ł̉𑜓xdpi�E�E�E�p�~ '04.Mar.2
	int printRequestMax = 100;// �Q�l�}�o�͍ő匏��
	int aclvChangablePosition = 999;// �A�N�Z�X���x���ύX�\�ȐE��
	int minimumIuputDrwgChar = 0;// �����ɂ�����}�ԑ����̍Œ���̕�����
	// �Ԉ����T�C�Y�̖��ݒ�ɂ��Ή�����B'04.Jul.19�ύX by Hirata�B
	String mabiki100dpiSize = null;// ���̐}�ʃT�C�Y�ȏ�(�܂�)�ł́A100dpi�ɊԈ�������B(�r���[��)
	String mabiki200dpiSize = null;// mabiki100dpiSize��菬�����A���̐}�ʃT�C�Y�ȏ�(�܂�)�ł́A200dpi�ɊԈ�������B(�r���[��)
	String viewStampW = "10";// �{���p�X�^���v�ʒu(W)
	String viewStampL = "10";// �{���p�X�^���v�ʒu(L)
	String viewStampDeep = "70";// �{���p�X�^���v�����Z�x
	String viewStampDateFormat = "yyyy/MM/dd";// �{���p�X�^���v���t�`��
									// SimpleDateFormat�Ŏg���������ێ�
// 2013.07.12 yamagishi add. start
	String correspondingStampW = "1"; 		// �Y���}�p�X�^���v�ʒu(W����)
	String correspondingStampL = "17820";	// �Y���}�p�X�^���v�ʒu(L����)
	String correspondingStampDeep = "180";	// �Y���}�p�X�^���v�����Z�W
	String correspondingStampStr = "��";	// �Y���}�p�X�^���v����
// 2013.07.12 yamagishi add. end
	boolean dispDrwgNoWithView = true;// �{���p�X�^���v�ł̐}�Ԃ��󎚂���H
	int multipleDrwgNoMax = 1000; // 2013.06.27 yamagishi add.
// 2013.07.10 yamagishi add. start
	String correspondingValue = "��";
	String confidentialValue = "��";
	String strictlyConfidentialValue = "��";
// 2013.07/10 yamagishi add. end

	// ---------------------------------------------------------- �R���X�g���N�^
	/**
	 * �R���X�g���N�^
	 */
// 2013.07.10 yamagishi modified. start
//	public DrasapInfo(String viewDBDrive, String newSearchWarningCount, String newSearchLimitCount,
//			String newPrintRequestMax, String newAclvChangablePosition,
//			String newMinimumIuputDrwgChar,
//			String newMabiki100dpiSize,	String newMabiki200dpiSize,
//			String newViewStampW, String newViewStampL, String newViewStampDeep,
//			String newViewStampDateFormat, String newDispDrwgNoWithView) {
	public DrasapInfo(String viewDBDrive, String newSearchWarningCount, String newSearchLimitCount,
			String newPrintRequestMax, String newAclvChangablePosition,
			String newMinimumIuputDrwgChar,
			String newMabiki100dpiSize,	String newMabiki200dpiSize,
			String newViewStampW, String newViewStampL, String newViewStampDeep,
			String newViewStampDateFormat, String newDispDrwgNoWithView,
			String newMultipleDrwgNoMax,
			String newCorrespondingValue, String newConfidentialValue, String newStrictlyConfidentialValue,
			String newCorrespondingStampW, String newCorrespondingStampL,
			String newCorrespondingStampDeep, String newCorrespondingStampStr) {
// 2013.07.10 yamagishi modified. end

		if (viewDBDrive != null) this.viewDBDrive = viewDBDrive;
		// �����ɂ�����x������
		if(newSearchWarningCount != null){
			try{
				this.searchWarningCount = Integer.parseInt(newSearchWarningCount);
			} catch(NumberFormatException ne){
			}
		}
		// �����ɂ�����Ő،���(���p�\�ő匏��)
		if(newSearchLimitCount != null){
			try{
				this.searchLimitCount = Integer.parseInt(newSearchLimitCount);
			} catch(NumberFormatException ne){
			}
		}
		// �Ԉ��������̉𑜓x�E�E�E�p�~ '04.Mar.2
		//if(newMabikiDpi != null){
		//	mabikiDpi = newMabikiDpi;
		//}
		// �Q�l�}�o�͂ɂ�����ő匏��
		if(newPrintRequestMax != null){
			try{
				this.printRequestMax = Integer.parseInt(newPrintRequestMax);
			} catch(NumberFormatException ne){
			}
		}
		// �A�N�Z�X���x���ύX�\�ȐE��
		if(newAclvChangablePosition != null){
			try{
				this.aclvChangablePosition = Integer.parseInt(newAclvChangablePosition);
			} catch(NumberFormatException ne){
			}
		}
		// �����ɂ�����}�ԑ����̍Œ���̕�����
		if(newMinimumIuputDrwgChar != null){
			try{
				this.minimumIuputDrwgChar = Integer.parseInt(newMinimumIuputDrwgChar);
			} catch(NumberFormatException ne){
			}
		}
		// 100dpi�ɊԈ�������}�ʃT�C�Y
		if(newMabiki100dpiSize != null){
			this.mabiki100dpiSize = newMabiki100dpiSize;
		}
		// 200dpi�ɊԈ�������}�ʃT�C�Y
		if(newMabiki200dpiSize != null){
			this.mabiki200dpiSize = newMabiki200dpiSize;
		}
		// �{���p�X�^���v�ʒu(W)
		if(newViewStampW != null){
			this.viewStampW = newViewStampW;
		}
		// �{���p�X�^���v�ʒu(L)
		if(newViewStampL != null){
			this.viewStampL = newViewStampL;
		}
		// �{���p�X�^���v�����Z�x
		if(newViewStampDeep != null){
			this.viewStampDeep = newViewStampDeep;
		}
		// �{���p�X�^���v���t�`��
		if(newViewStampDateFormat != null){
			if(newViewStampDateFormat.equals("1")){
				// 1�Ȃ�
				this.viewStampDateFormat = "yy/MM/dd";
			} else {
				// ����ȊO�Ȃ�
				this.viewStampDateFormat = "yyyy/MM/dd";
			}
		}
		// �{���p�X�^���v�ł̐}�Ԃ��󎚂���H
		if(newDispDrwgNoWithView != null){
			// 1�Ȃ�}�Ԃ��󎚂���
			this.dispDrwgNoWithView = "1".equals(newDispDrwgNoWithView);
		}
// 2013.06.27 yamagishi add. start
		// �����}�Ԏw�莞�̌����\����
		if (newMultipleDrwgNoMax != null) {
			try {
				this.multipleDrwgNoMax = Integer.parseInt(newMultipleDrwgNoMax);
			} catch (NumberFormatException ne) {
			}
		}
// 2013.06.27 yamagishi add. end
// 2013.07.10 yamagishi add. start
		// �Y���}���͒l
		if (newCorrespondingValue != null) {
			this.correspondingValue = newCorrespondingValue;
		}
		// �@���Ǘ��}���͒l�i��j
		if (newConfidentialValue != null) {
			this.confidentialValue = newConfidentialValue;
		}
		// �@���Ǘ��}���͒l�i�ɔ�j
		if (newStrictlyConfidentialValue != null) {
			this.strictlyConfidentialValue = newStrictlyConfidentialValue;
		}
		// �Y���}�p�X�^���v�ʒu(W����)
		if (newCorrespondingStampW != null) {
			this.correspondingStampW = newCorrespondingStampW;
		}
		// �Y���}�p�X�^���v�ʒu(L����)
		if (newCorrespondingStampL != null) {
			this.correspondingStampL = newCorrespondingStampL;
		}
		// �Y���}�p�X�^���v�����Z�W
		if (newCorrespondingStampDeep != null) {
			this.correspondingStampDeep = newCorrespondingStampDeep;
		}
		// �Y���}�p�X�^���v����
		if (newCorrespondingStampStr != null) {
			this.correspondingStampStr = newCorrespondingStampStr;
		}
// 2013.07.10 yamagishi add. end
	}
	// ---------------------------------------------------------- getter,setter
	/**
	 * @return
	 */
	public int getSearchLimitCount() {
		return searchLimitCount;
	}

	/**
	 * @return
	 */
	public int getSearchWarningCount() {
		return searchWarningCount;
	}

	/**
	 * @return
	 */
	public int getPrintRequestMax() {
		return printRequestMax;
	}

	/**
	 * @return
	 */
	public int getAclvChangablePosition() {
		return aclvChangablePosition;
	}

	/**
	 * @return
	 */
	public int getMinimumIuputDrwgChar() {
		return minimumIuputDrwgChar;
	}

// 2013.07.02 yamagishi add. start
	/**
	 * correspondingStampW���擾���܂��B
	 * @return correspondingStampW
	 */
	public String getCorrespondingStampW() {
		return correspondingStampW;
	}
	/**
	 * correspondingStampL���擾���܂��B
	 * @return correspondingStampL
	 */
	public String getCorrespondingStampL() {
		return correspondingStampL;
	}
	/**
	 * correspondingStampDeep���擾���܂��B
	 * @return correspondingStampDeep
	 */
	public String getCorrespondingStampDeep() {
		return correspondingStampDeep;
	}
	/**
	 * correspondingStampStr���擾���܂��B
	 * @return correspondingStampStr
	 */
	public String getCorrespondingStampStr() {
		return correspondingStampStr;
	}
// 2013.07.02 yamagishi add. end

	/**
	 * @return
	 */
	public boolean isDispDrwgNoWithView() {
		return dispDrwgNoWithView;
	}

	/**
	 * @return
	 */
	public String getViewStampDateFormat() {
		return viewStampDateFormat;
	}

	/**
	 * @return
	 */
	public String getViewStampDeep() {
		return viewStampDeep;
	}

	/**
	 * @return
	 */
	public String getViewStampL() {
		return viewStampL;
	}

	/**
	 * @return
	 */
	public String getViewStampW() {
		return viewStampW;
	}

	/**
	 * @return
	 */
	public String getMabiki100dpiSize() {
		return mabiki100dpiSize;
	}

	/**
	 * @return
	 */
	public String getMabiki200dpiSize() {
		return mabiki200dpiSize;
	}
	public String getViewDBDrive() {
		return viewDBDrive;
	}
	public void setViewDBDrive(String viewDBDrive) {
		this.viewDBDrive = viewDBDrive;
	}
// 2013.06.27 yamagishi add. start
	/**
	 * multipleDrwgNoMax���擾���܂��B
	 * @return multipleDrwgNoMax
	 */
	public int getMultipleDrwgNoMax() {
		return multipleDrwgNoMax;
	}
// 2013.06.27 yamagishi add. end

// 2013.07.10 yamagishi add. start
	/**
	 * correspondingValue���擾���܂��B
	 * @return correspondingValue
	 */
	public String getCorrespondingValue() {
		return correspondingValue;
	}
	/**
	 * confidentialValue���擾���܂��B
	 * @return confidentialValue
	 */
	public String getConfidentialValue() {
		return confidentialValue;
	}
	/**
	 * strictlyConfidentialValue���擾���܂��B
	 * @return strictlyConfidentialValue
	 */
	public String getStrictlyConfidentialValue() {
		return strictlyConfidentialValue;
	}
// 2013.07.10 yamagishi add. end
}
