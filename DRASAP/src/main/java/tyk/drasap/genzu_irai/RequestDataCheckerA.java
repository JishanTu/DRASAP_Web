package tyk.drasap.genzu_irai;

import java.util.HashSet;

import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import tyk.drasap.springfw.utils.MessageSourceUtil;

/**
 * 図面登録依頼のときのRequestDataChecker。
 * StrategyパターンのConcreteStrategy役。
 * @author fumi
 */
@Component
public class RequestDataCheckerA implements RequestDataChecker {

	/* (非 Javadoc)
	 * @see tyk.drasap.genzu_irai.RequestDataChecker#checkLineData(tyk.drasap.genzu_irai.RequestFormLineData, tyk.drasap.genzu_irai.RequestForm, org.apache.struts.action.ActionErrors, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean)
	 */
	@Override
	public void checkLineData(RequestFormLineData lineData, RequestForm requestForm, Model errors,
			HashSet<String> usedErrMsgSet, Logger category) {
		MessageSource messageSource = MessageSourceUtil.getMessageSource();
		// 開始No、終了No
		if ("".equals(lineData.getKaisiNo()) && "".equals(lineData.getSyuuryouNo())) {
			if (!usedErrMsgSet.contains("error.kaisyuu.required")) {
				// 依頼内容が図面出力指示の場合は開始番号、終了番号は必須です
				category.debug("開始番号、終了番号は必須");
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("error.kaisyuu.required", null, null));
				usedErrMsgSet.add("error.kaisyuu.required");
			}
		}
	}

}
