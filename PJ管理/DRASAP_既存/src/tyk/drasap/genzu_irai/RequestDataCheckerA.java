package tyk.drasap.genzu_irai;

import java.util.HashSet;

import org.apache.log4j.Category;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;

/**
 * 図面登録依頼のときのRequestDataChecker。
 * StrategyパターンのConcreteStrategy役。
 * @author fumi
 */
@SuppressWarnings("deprecation")
public class RequestDataCheckerA implements RequestDataChecker {

	/* (非 Javadoc)
	 * @see tyk.drasap.genzu_irai.RequestDataChecker#checkLineData(tyk.drasap.genzu_irai.RequestFormLineData, tyk.drasap.genzu_irai.RequestForm, org.apache.struts.action.ActionErrors, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean)
	 */
	public void checkLineData(RequestFormLineData lineData, RequestForm requestForm, ActionErrors errors,
								HashSet usedErrMsgSet, Category category) {

		// 開始No、終了No
		if("".equals(lineData.getKaisiNo()) && "".equals(lineData.getSyuuryouNo())){
			if(! usedErrMsgSet.contains("error.kaisyuu.required")){
				// 依頼内容が図面出力指示の場合は開始番号、終了番号は必須です
				category.debug("開始番号、終了番号は必須");
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.kaisyuu.required"));
				usedErrMsgSet.add("error.kaisyuu.required");
			}
		}
	}

}
