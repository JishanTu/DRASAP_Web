package tyk.drasap.genzu_irai;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashSet;

import org.apache.log4j.Category;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;

/**
 * �}�ʏo�͎w���̂Ƃ���RequestDataChecker�B
 * Strategy�p�^�[����ConcreteStrategy���B
 * @author fumi
 */
@SuppressWarnings("deprecation")
public class RequestDataCheckerB implements RequestDataChecker {

	/* (�� Javadoc)
	 * @see tyk.drasap.genzu_irai.RequestDataChecker#checkLineData(tyk.drasap.genzu_irai.RequestFormLineData, tyk.drasap.genzu_irai.RequestForm, org.apache.struts.action.ActionErrors, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean, org.apache.log4j.Category)
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
		// �o�͐�
		if("".equals(requestForm.syutu)){
			if(! usedErrMsgSet.contains("error.syutu.required")){
				// �o�͐��I�����Ă�������
				category.debug("�o�͐��I�����Ă�������");
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.syutu.required"));
				usedErrMsgSet.add("error.syutu.required");
			}
		}
		// �����ɂ���
		if("".equals(lineData.getBusuu())){
			if(! usedErrMsgSet.contains("error.busuu.required")){
				// �˗����e���}�ʏo�͎w���̏ꍇ�͕����͕K�{�ł�
				category.debug("�����͕K�{");
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.busuu.required"));
				usedErrMsgSet.add("error.busuu.required");
			}
		}else{
			// ���������͂���Ă����
			Number suryo = null;
			try{
				// �������ǂ����̃`�F�b�N
				suryo = NumberFormat.getInstance().parse(lineData.getBusuu());

				if(suryo.intValue() <= 0){//�}�C�i�X�̓_��
					if(! usedErrMsgSet.contains("error.mainasu.check")){
						errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.mainasu.check"));
						usedErrMsgSet.add("error.mainasu.check");
					}
				}
			} catch(ParseException e){
				if(! usedErrMsgSet.contains("error.busuu_sei.required")){
					errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.busuu_sei.required"));
					usedErrMsgSet.add("error.busuu_sei.required");
				}
			}
		}
		// �k���̏ꍇ�́A�T�C�Y���w�肵�Ă�������
		if("1".equals(lineData.getSyukusyou()) && "".equals(lineData.getSize())){
			if(! usedErrMsgSet.contains("error.size1.required")){
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.size1.required"));
				usedErrMsgSet.add("error.size1.required");
			}
		}

	}

}
