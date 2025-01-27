package tyk.drasap.genzu_irai;

import java.util.HashSet;

import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import tyk.drasap.springfw.utils.MessageSourceUtil;

/**
 * 原図借用依頼のときのRequestDataChecker。
 * StrategyパターンのConcreteStrategy役。
 * @author fumi
 */
@Component
public class RequestDataCheckerC implements RequestDataChecker {

	@Override
	public void checkLineData(RequestFormLineData lineData, RequestForm requestForm, Model errors,
			HashSet<String> usedErrMsgSet, Logger category) {
		MessageSource messageSource = MessageSourceUtil.getMessageSource();
		// 号口号機、原図内容、番号のいずれかの入力が必須
		if ("".equals(lineData.getGouki()) && "".equals(lineData.getGenzu()) &&
				"".equals(lineData.getKaisiNo()) && "".equals(lineData.getSyuuryouNo())) {
			if (!usedErrMsgSet.contains("error.gouki.required")) {
				category.debug("号口号機、原図内容、番号のいずれかの入力が必須");
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("error.gouki.required", null, null));
				usedErrMsgSet.add("error.gouki.required");
			}
		}
	}

}
