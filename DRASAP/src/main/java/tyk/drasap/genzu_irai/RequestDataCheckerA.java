package tyk.drasap.genzu_irai;

import java.util.HashSet;

import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import tyk.drasap.springfw.utils.MessageSourceUtil;

/**
 * �}�ʓo�^�˗��̂Ƃ���RequestDataChecker�B
 * Strategy�p�^�[����ConcreteStrategy���B
 * @author fumi
 */
@Component
public class RequestDataCheckerA implements RequestDataChecker {

	/* (�� Javadoc)
	 * @see tyk.drasap.genzu_irai.RequestDataChecker#checkLineData(tyk.drasap.genzu_irai.RequestFormLineData, tyk.drasap.genzu_irai.RequestForm, org.apache.struts.action.ActionErrors, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean)
	 */
	@Override
	public void checkLineData(RequestFormLineData lineData, RequestForm requestForm, Model errors,
			HashSet<String> usedErrMsgSet, Logger category) {
		MessageSource messageSource = MessageSourceUtil.getMessageSource();
		// �J�nNo�A�I��No
		if ("".equals(lineData.getKaisiNo()) && "".equals(lineData.getSyuuryouNo())) {
			if (!usedErrMsgSet.contains("error.kaisyuu.required")) {
				// �˗����e���}�ʏo�͎w���̏ꍇ�͊J�n�ԍ��A�I���ԍ��͕K�{�ł�
				category.debug("�J�n�ԍ��A�I���ԍ��͕K�{");
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("error.kaisyuu.required", null, null));
				usedErrMsgSet.add("error.kaisyuu.required");
			}
		}
	}

}
