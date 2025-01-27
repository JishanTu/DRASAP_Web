package tyk.drasap.genzu_irai;

import java.util.HashSet;

import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import tyk.drasap.springfw.utils.MessageSourceUtil;

/**
 * �}�ʈȊO�ĕt�˗��̂Ƃ���RequestDataChecker�B
 * Strategy�p�^�[����ConcreteStrategy���B
 * @author fumi
 */
@Component
public class RequestDataCheckerD implements RequestDataChecker {

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
		// ���}���e�̃`�F�b�N
		if (!"".equals(lineData.getGenzu())) {// ���}���e�����͂���Ă���
			if (!"�d�l��".equals(lineData.getGenzu()) && !"���얾��".equals(lineData.getGenzu()) &&
					!"���i����".equals(lineData.getGenzu())) {
				// �d�l���A���얾�ׁA���i���ׂ̂ݎw���\
				// '04.Jul.19�ύX by Hirata
				if (!usedErrMsgSet.contains("error.genzu.miss_match")) {
					category.debug("�}�ʈȊO�ĕt�˗��Ŏw��ł��錴�}���e�́A�d�l���A���얾�ׁA���i���ׂ݂̂ł�");
					MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("error.genzu.miss_match", null, null));
					usedErrMsgSet.add("error.genzu.miss_match");
				}
			}
		}
	}

}
