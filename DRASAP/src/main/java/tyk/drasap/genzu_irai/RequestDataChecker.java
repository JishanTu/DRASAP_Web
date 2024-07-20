package tyk.drasap.genzu_irai;

import java.util.HashSet;

import org.apache.log4j.Logger;
import org.springframework.ui.Model;

/**
 * ���}�ɍ�ƈ˗���1�s���̃f�[�^�`�F�b�N���s���N���X�B
 * Strategy�p�^�[����Strategy���B
 * @author fumi
 */
public interface RequestDataChecker {
	/**
	 * ���}�ɍ�ƈ˗���1�s���̃f�[�^�`�F�b�N���s���B
	 * @param lineData
	 * @param requestForm
	 * @param errors
	 * @param flag
	 * @param kaisyuFlag
	 * @param goukiFlag
	 * @param lenghFlag
	 * @param iraiFlag
	 * @param zumenFlag
	 * @param busuuFlag
	 * @param sizeFlag
	 * @param suryoFlag
	 */
	public void checkLineData(RequestFormLineData lineData, RequestForm requestForm, Model errors,
			HashSet<String> usedErrMsgSet, Logger category);

}
