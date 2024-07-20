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
 * 図面出力指示のときのRequestDataChecker。
 * StrategyパターンのConcreteStrategy役。
 * @author fumi
 */
@Component
public class RequestDataCheckerB implements RequestDataChecker {

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
		// 出力先
		if ("".equals(requestForm.syutu)) {
			if (!usedErrMsgSet.contains("error.syutu.required")) {
				// 出力先を選択してください
				category.debug("出力先を選択してください");
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("error.syutu.required", null, null));
				usedErrMsgSet.add("error.syutu.required");
			}
		}
		// 部数について
		if ("".equals(lineData.getBusuu())) {
			if (!usedErrMsgSet.contains("error.busuu.required")) {
				// 依頼内容が図面出力指示の場合は部数は必須です
				category.debug("部数は必須");
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("error.busuu.required", null, null));
				usedErrMsgSet.add("error.busuu.required");
			}
		} else {
			// 部数が入力されていれば
			Number suryo = null;
			try {
				// 数字かどうかのチェック
				suryo = NumberFormat.getInstance().parse(lineData.getBusuu());

				if (suryo.intValue() <= 0) {//マイナスはダメ
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
		// 縮小の場合は、サイズを指定してください
		if ("1".equals(lineData.getSyukusyou()) && "".equals(lineData.getSize())) {
			if (!usedErrMsgSet.contains("error.size1.required")) {
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("error.size1.required", null, null));
				usedErrMsgSet.add("error.size1.required");
			}
		}

	}

}
