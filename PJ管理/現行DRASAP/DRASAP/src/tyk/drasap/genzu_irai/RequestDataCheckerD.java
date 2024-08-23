package tyk.drasap.genzu_irai;

import java.util.HashSet;

import org.apache.log4j.Category;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;

/**
 * 図面以外焼付依頼のときのRequestDataChecker。
 * StrategyパターンのConcreteStrategy役。
 * @author fumi
 */
@SuppressWarnings("deprecation")
public class RequestDataCheckerD implements RequestDataChecker {

	/* (非 Javadoc)
	 * @see tyk.drasap.genzu_irai.RequestDataChecker#checkLineData(tyk.drasap.genzu_irai.RequestFormLineData, tyk.drasap.genzu_irai.RequestForm, org.apache.struts.action.ActionErrors, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean, org.apache.log4j.Category)
	 */
	public void checkLineData(RequestFormLineData lineData, RequestForm requestForm, ActionErrors errors,
							HashSet usedErrMsgSet, Category category) {
		// 号口号機、原図内容、番号のいずれかの入力が必須
		if("".equals(lineData.getGouki()) && "".equals(lineData.getGenzu()) &&
				"".equals(lineData.getKaisiNo()) && "".equals(lineData.getSyuuryouNo())){
			if(! usedErrMsgSet.contains("error.gouki.required")){
				category.debug("号口号機、原図内容、番号のいずれかの入力が必須");
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.gouki.required"));
				usedErrMsgSet.add("error.gouki.required");
			}
		}
		// 原図内容のチェック
		if(!"".equals(lineData.getGenzu())){// 原図内容が入力されていて
			if(!"仕様書".equals(lineData.getGenzu()) && !"製作明細".equals(lineData.getGenzu()) &&
						!"部品明細".equals(lineData.getGenzu())){
				// 仕様書、製作明細、部品明細のみ指示可能
				// '04.Jul.19変更 by Hirata
				if(! usedErrMsgSet.contains("error.genzu.miss_match")){
					category.debug("図面以外焼付依頼で指定できる原図内容は、仕様書、製作明細、部品明細のみです");
					errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.genzu.miss_match"));
					usedErrMsgSet.add("error.genzu.miss_match");
				}
			}
		}
	}

}
