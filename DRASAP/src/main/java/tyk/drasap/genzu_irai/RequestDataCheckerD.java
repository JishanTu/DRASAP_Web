package tyk.drasap.genzu_irai;

import java.util.HashSet;

import org.apache.log4j.Category;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;

/**
 * �}�ʈȊO�ĕt�˗��̂Ƃ���RequestDataChecker�B
 * Strategy�p�^�[����ConcreteStrategy���B
 * @author fumi
 */
@SuppressWarnings("deprecation")
public class RequestDataCheckerD implements RequestDataChecker {

	/* (�� Javadoc)
	 * @see tyk.drasap.genzu_irai.RequestDataChecker#checkLineData(tyk.drasap.genzu_irai.RequestFormLineData, tyk.drasap.genzu_irai.RequestForm, org.apache.struts.action.ActionErrors, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean, org.apache.log4j.Category)
	 */
	public void checkLineData(RequestFormLineData lineData, RequestForm requestForm, ActionErrors errors,
							HashSet usedErrMsgSet, Category category) {
		// �������@�A���}���e�A�ԍ��̂����ꂩ�̓��͂��K�{
		if("".equals(lineData.getGouki()) && "".equals(lineData.getGenzu()) &&
				"".equals(lineData.getKaisiNo()) && "".equals(lineData.getSyuuryouNo())){
			if(! usedErrMsgSet.contains("error.gouki.required")){
				category.debug("�������@�A���}���e�A�ԍ��̂����ꂩ�̓��͂��K�{");
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.gouki.required"));
				usedErrMsgSet.add("error.gouki.required");
			}
		}
		// ���}���e�̃`�F�b�N
		if(!"".equals(lineData.getGenzu())){// ���}���e�����͂���Ă���
			if(!"�d�l��".equals(lineData.getGenzu()) && !"���얾��".equals(lineData.getGenzu()) &&
						!"���i����".equals(lineData.getGenzu())){
				// �d�l���A���얾�ׁA���i���ׂ̂ݎw���\
				// '04.Jul.19�ύX by Hirata
				if(! usedErrMsgSet.contains("error.genzu.miss_match")){
					category.debug("�}�ʈȊO�ĕt�˗��Ŏw��ł��錴�}���e�́A�d�l���A���얾�ׁA���i���ׂ݂̂ł�");
					errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.genzu.miss_match"));
					usedErrMsgSet.add("error.genzu.miss_match");
				}
			}
		}
	}

}
