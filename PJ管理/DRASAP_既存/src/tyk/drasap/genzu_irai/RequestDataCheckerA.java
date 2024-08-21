package tyk.drasap.genzu_irai;

import java.util.HashSet;

import org.apache.log4j.Category;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;

/**
 * �}�ʓo�^�˗��̂Ƃ���RequestDataChecker�B
 * Strategy�p�^�[����ConcreteStrategy���B
 * @author fumi
 */
@SuppressWarnings("deprecation")
public class RequestDataCheckerA implements RequestDataChecker {

	/* (�� Javadoc)
	 * @see tyk.drasap.genzu_irai.RequestDataChecker#checkLineData(tyk.drasap.genzu_irai.RequestFormLineData, tyk.drasap.genzu_irai.RequestForm, org.apache.struts.action.ActionErrors, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean)
	 */
	public void checkLineData(RequestFormLineData lineData, RequestForm requestForm, ActionErrors errors,
								HashSet usedErrMsgSet, Category category) {

		// �J�nNo�A�I��No
		if("".equals(lineData.getKaisiNo()) && "".equals(lineData.getSyuuryouNo())){
			if(! usedErrMsgSet.contains("error.kaisyuu.required")){
				// �˗����e���}�ʏo�͎w���̏ꍇ�͊J�n�ԍ��A�I���ԍ��͕K�{�ł�
				category.debug("�J�n�ԍ��A�I���ԍ��͕K�{");
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.kaisyuu.required"));
				usedErrMsgSet.add("error.kaisyuu.required");
			}
		}
	}

}
