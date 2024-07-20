package tyk.drasap.genzu_irai;

import java.util.HashSet;

import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import tyk.drasap.springfw.utils.MessageSourceUtil;

/**
 * ���}�ؗp�˗��̂Ƃ���RequestDataChecker�B
 * Strategy�p�^�[����ConcreteStrategy���B
 * @author fumi
 */
@Component
public class RequestDataCheckerC implements RequestDataChecker {

	@Override
	public void checkLineData(RequestFormLineData lineData, RequestForm requestForm, Model errors,
			HashSet<String> usedErrMsgSet, Logger category) {
		MessageSource messageSource = MessageSourceUtil.getMessageSource();
		// �������@�A���}���e�A�ԍ��̂����ꂩ�̓��͂��K�{
		if ("".equals(lineData.getGouki()) && "".equals(lineData.getGenzu()) &&
				"".equals(lineData.getKaisiNo()) && "".equals(lineData.getSyuuryouNo())) {
			if (!usedErrMsgSet.contains("error.gouki.required")) {
				category.debug("�������@�A���}���e�A�ԍ��̂����ꂩ�̓��͂��K�{");
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("error.gouki.required", null, null));
				usedErrMsgSet.add("error.gouki.required");
			}
		}
	}

}
