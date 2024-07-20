package tyk.drasap.genzu_irai;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashSet;

import org.apache.log4j.Category;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;

/**
 * 図面出力指示のときのRequestDataChecker。
 * StrategyパターンのConcreteStrategy役。
 * @author fumi
 */
@SuppressWarnings("deprecation")
public class RequestDataCheckerB implements RequestDataChecker {

	/* (非 Javadoc)
	 * @see tyk.drasap.genzu_irai.RequestDataChecker#checkLineData(tyk.drasap.genzu_irai.RequestFormLineData, tyk.drasap.genzu_irai.RequestForm, org.apache.struts.action.ActionErrors, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean, org.apache.log4j.Category)
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
		// 出力先
		if("".equals(requestForm.syutu)){
			if(! usedErrMsgSet.contains("error.syutu.required")){
				// 出力先を選択してください
				category.debug("出力先を選択してください");
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.syutu.required"));
				usedErrMsgSet.add("error.syutu.required");
			}
		}
		// 部数について
		if("".equals(lineData.getBusuu())){
			if(! usedErrMsgSet.contains("error.busuu.required")){
				// 依頼内容が図面出力指示の場合は部数は必須です
				category.debug("部数は必須");
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.busuu.required"));
				usedErrMsgSet.add("error.busuu.required");
			}
		}else{
			// 部数が入力されていれば
			Number suryo = null;
			try{
				// 数字かどうかのチェック
				suryo = NumberFormat.getInstance().parse(lineData.getBusuu());

				if(suryo.intValue() <= 0){//マイナスはダメ
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
		// 縮小の場合は、サイズを指定してください
		if("1".equals(lineData.getSyukusyou()) && "".equals(lineData.getSize())){
			if(! usedErrMsgSet.contains("error.size1.required")){
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.size1.required"));
				usedErrMsgSet.add("error.size1.required");
			}
		}

	}

}
