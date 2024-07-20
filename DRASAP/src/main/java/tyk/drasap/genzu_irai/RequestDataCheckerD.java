package tyk.drasap.genzu_irai;

import java.util.HashSet;

import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import tyk.drasap.springfw.utils.MessageSourceUtil;

/**
 * 図面以外焼付依頼のときのRequestDataChecker。
 * StrategyパターンのConcreteStrategy役。
 * @author fumi
 */
@Component
public class RequestDataCheckerD implements RequestDataChecker {

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
		// 原図内容のチェック
		if (!"".equals(lineData.getGenzu())) {// 原図内容が入力されていて
			if (!"仕様書".equals(lineData.getGenzu()) && !"製作明細".equals(lineData.getGenzu()) &&
					!"部品明細".equals(lineData.getGenzu())) {
				// 仕様書、製作明細、部品明細のみ指示可能
				// '04.Jul.19変更 by Hirata
				if (!usedErrMsgSet.contains("error.genzu.miss_match")) {
					category.debug("図面以外焼付依頼で指定できる原図内容は、仕様書、製作明細、部品明細のみです");
					MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("error.genzu.miss_match", null, null));
					usedErrMsgSet.add("error.genzu.miss_match");
				}
			}
		}
	}

}
