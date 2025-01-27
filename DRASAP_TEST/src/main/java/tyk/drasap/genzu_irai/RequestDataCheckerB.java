package tyk.drasap.genzu_irai;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashSet;

import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import tyk.drasap.springfw.utils.MessageSourceUtil;

/**
 * �}�ʏo�͎w���̂Ƃ���RequestDataChecker�B
 * Strategy�p�^�[����ConcreteStrategy���B
 * @author fumi
 */
@Component
public class RequestDataCheckerB implements RequestDataChecker {

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
		// �o�͐�
		if ("".equals(requestForm.syutu)) {
			if (!usedErrMsgSet.contains("error.syutu.required")) {
				// �o�͐��I�����Ă�������
				category.debug("�o�͐��I�����Ă�������");
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("error.syutu.required", null, null));
				usedErrMsgSet.add("error.syutu.required");
			}
		}
		// �����ɂ���
		if ("".equals(lineData.getBusuu())) {
			if (!usedErrMsgSet.contains("error.busuu.required")) {
				// �˗����e���}�ʏo�͎w���̏ꍇ�͕����͕K�{�ł�
				category.debug("�����͕K�{");
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("error.busuu.required", null, null));
				usedErrMsgSet.add("error.busuu.required");
			}
		} else {
			// ���������͂���Ă����
			Number suryo = null;
			try {
				// �������ǂ����̃`�F�b�N
				suryo = NumberFormat.getInstance().parse(lineData.getBusuu());

				if (suryo.intValue() <= 0) {//�}�C�i�X�̓_��
					if (!usedErrMsgSet.contains("error.mainasu.check")) {
						MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("error.mainasu.check", null, null));
						usedErrMsgSet.add("error.mainasu.check");
					}
				}
			} catch (ParseException e) {
				if (!usedErrMsgSet.contains("error.busuu_sei.required")) {
					MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("error.busuu_sei.required", null, null));
					usedErrMsgSet.add("error.busuu_sei.required");
				}
			}
		}
		// �k���̏ꍇ�́A�T�C�Y���w�肵�Ă�������
		if ("1".equals(lineData.getSyukusyou()) && "".equals(lineData.getSize())) {
			if (!usedErrMsgSet.contains("error.size1.required")) {
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("error.size1.required", null, null));
				usedErrMsgSet.add("error.size1.required");
			}
		}

	}

}
