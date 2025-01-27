package tyk.drasap.common;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * ユーザーグループを表す。
 * 利用者グループマスターテーブル(USER_GROUP_MASTER)に対応。
 */
public class UserGroup {
	String cd = ""; // 利用者グループコード
	String name = ""; // 利用者グループ名
	boolean viewStamp; // VIEWのスタンプ。trueならスタンプする
	boolean eucStamp; // EUCプリンタのスタンプ。trueならスタンプする
	boolean pltrStamp; // 専用プリンタのスタンプ。trueならスタンプする
	ArrayList<Printer> enablePrinters = new ArrayList<Printer>();// 利用可能なプリンター。内部はPrinter。
	boolean reqImport; // 図面登録依頼可能ならtrue。
	boolean reqPrint; // 図面出力指示可能ならtrue。
	boolean reqCheckout; // 原図借用依頼可能ならtrue。
	boolean reqOther; // 図面以外焼付依頼可能ならtrue。
	// これ以降の xxxxxDisplay は、図面検索の画面において使用可能かを表す。
	// trueなら使用可能。
	boolean drwgNoDisplay; // 図番を表示可能ならtrue。
	boolean createDateDisplay; // 作成日時を表示可能ならtrue。
	boolean createUserDisplay; // 作成者を表示可能ならtrue。
	boolean machineJpDisplay; // 装置名称(和文)を表示可能ならtrue。
	boolean machineEnDisplay; // 装置名称(英文)を表示可能ならtrue。
	boolean usedForDisplay; // 用途を表示可能ならtrue。
	boolean materialDisplay; // 材質を表示可能ならtrue。
	boolean treatmentDisplay; // 熱・表面処理を表示可能ならtrue。
	boolean procurementDisplay; // 調達区分を表示可能ならtrue。
	boolean supplyerJpDisplay; // メーカー名(和文)を表示可能ならtrue。
	boolean supplyerEnDisplay; // メーカー名(英文)を表示可能ならtrue。
	boolean supplyerTypeDisplay;// メーカー形式を表示可能ならtrue。
	boolean attach01Display; // 添付図番1を表示可能ならtrue。
	boolean attach02Display; // 添付図番2を表示可能ならtrue。
	boolean attach03Display; // 添付図番3を表示可能ならtrue。
	boolean attach04Display; // 添付図番4を表示可能ならtrue。
	boolean attach05Display; // 添付図番5を表示可能ならtrue。
	boolean attach06Display; // 添付図番6を表示可能ならtrue。
	boolean attach07Display; // 添付図番7を表示可能ならtrue。
	boolean attach08Display; // 添付図番8を表示可能ならtrue。
	boolean attach09Display; // 添付図番9を表示可能ならtrue。
	boolean attach10Display; // 添付図番10を表示可能ならtrue。
	boolean machineNoDisplay; // 装置Noを表示可能ならtrue。
	boolean machineNameDisplay; // 機種名称を表示可能ならtrue。
	boolean machineSpec1Display;// 装置仕様1を表示可能ならtrue。
	boolean machineSpec2Display;// 装置仕様2を表示可能ならtrue。
	boolean machineSpec3Display;// 装置仕様3を表示可能ならtrue。
	boolean machineSpec4Display;// 装置仕様4を表示可能ならtrue。
	boolean machineSpec5Display;// 装置仕様5を表示可能ならtrue。
	boolean drwgTypeDisplay; // 図面種類を表示可能ならtrue。
	boolean drwgSizeDisplay; // 図面サイズを表示可能ならtrue。
	boolean issueDisplay; // 提出区分を表示可能ならtrue。
	boolean supplyDisplay; // 消耗区分を表示可能ならtrue。
	boolean cadTypeDisplay; // CAD種別を表示可能ならtrue。
	boolean engineerDisplay; // 設計者名を表示可能ならtrue。
	boolean prohibitDisplay; // 使用禁止区分を表示可能ならtrue。
	boolean prohibitDateDisplay; // 使用禁止日時を表示可能ならtrue。
	boolean prohibitEmpnoDisplay; // 使用禁止者職番を表示可能ならtrue。
	boolean prohibitNameDisplay; // 使用禁止者名前を表示可能ならtrue。
	boolean pagesDisplay; // ページ数を表示可能ならtrue。
	boolean aclDisplay; // アクセスレベルを表示可能ならtrue。
	boolean aclUpdateDisplay; // アクセスレベル変更日時を表示可能ならtrue。
	boolean aclEmpnoDisplay; // アクセスレベル最終変更者職番を表示可能ならtrue。
	boolean aclNameDisplay; // アクセスレベル最終変更者名前を表示可能ならtrue。
	boolean attachMaxDisplay; // 添付図数を表示可能ならtrue。
	boolean latestDisplay; // 最新図番区分を表示可能ならtrue。
	boolean replaceDisplay; // 差替フラグを表示可能ならtrue。
	boolean createDivDisplay; // 作成部署コードを表示可能ならtrue。
	boolean mediaIdDisplay; // メディアIDを表示可能ならtrue。
	boolean twinDrwgNoDisplay; // 1物2品番図番を表示可能ならtrue。
	String viewPrintDoc; // 閲覧フォーマット　1：TIFF、2：PDF
	// アクセス権限をHashMapで持つ
	// key=アクセスレベル value=アクセスレベル値{1,2,3}
	HashMap<String, String> aclMap = new HashMap<String, String>();//

	/**
	 * コンストラクタ
	 */
	public UserGroup(String newCd, String newName,
			String newViewStamp, String newEucStamp, String newPltrStamp,
			ArrayList<Printer> newEnablePrinters,
			String newReqImport, String newReqPrint, String newReqCheckout, String newReqOther,
			String newDrwgNoDisplay, String newCreateDateDisplay, String newCreateUserDisplay,
			String newMachineJpDisplay, String newMachineEnDisplay,
			String newUsedForDisplay, String newMaterialDisplay, String newTreatmentDisplay, String newProcurementDisplay,
			String newSupplyerJpDisplay, String newSupplyerEnDisplay, String newSupplyerTypeDisplay,
			String newAttach01Display, String newAttach02Display, String newAttach03Display, String newAttach04Display,
			String newAttach05Display, String newAttach06Display, String newAttach07Display, String newAttach08Display,
			String newAttach09Display, String newAttach10Display,
			String newMachineNoDisplay, String newMachineNameDisplay,
			String newMachineSpec1Display, String newMachineSpec2Display, String newMachineSpec3Display,
			String newMachineSpec4Display, String newMachineSpec5Display,
			String newDrwgTypeDisplay, String newDrwgSizeDisplay,
			String newIssueDisplay, String newSupplyDisplay,
			String newCadTypeDisplay, String newEngineerDisplay,
			String newProhibitDisplay, String newProhibitDateDisplay,
			String newProhibitEmpnoDisplay, String newProhibitNameDisplay,
			String newPagesDisplay,
			String newAclDisplay, String newAclUpdateDisplay, String newAclEmpnoDisplay, String newAclNameDisplay,
			String newAttachMaxDisplay, String newLatestDisplay, String newReplaceDisplay,
			String newCreateDivDisplay, String newMediaIdDisplay, String newTwinDrwgNoDisplay,
			String newViewPrintDoc,
			HashMap<String, String> newAclMap) {
		super();
		cd = newCd;
		name = newName;
		viewStamp = "1".equals(newViewStamp);
		eucStamp = "1".equals(newEucStamp);
		pltrStamp = "1".equals(newPltrStamp);
		enablePrinters = newEnablePrinters;
		reqImport = "1".equals(newReqImport);
		reqPrint = "1".equals(newReqPrint);
		reqCheckout = "1".equals(newReqCheckout);
		reqOther = "1".equals(newReqOther);
		drwgNoDisplay = "1".equals(newDrwgNoDisplay);
		createDateDisplay = "1".equals(newCreateDateDisplay);
		createUserDisplay = "1".equals(newCreateUserDisplay);
		machineJpDisplay = "1".equals(newMachineJpDisplay);
		machineEnDisplay = "1".equals(newMachineEnDisplay);
		usedForDisplay = "1".equals(newUsedForDisplay);
		materialDisplay = "1".equals(newMaterialDisplay);
		treatmentDisplay = "1".equals(newTreatmentDisplay);
		procurementDisplay = "1".equals(newProcurementDisplay);
		supplyerJpDisplay = "1".equals(newSupplyerJpDisplay);
		supplyerEnDisplay = "1".equals(newSupplyerEnDisplay);
		supplyerTypeDisplay = "1".equals(newSupplyerTypeDisplay);
		attach01Display = "1".equals(newAttach01Display);
		attach02Display = "1".equals(newAttach02Display);
		attach03Display = "1".equals(newAttach03Display);
		attach04Display = "1".equals(newAttach04Display);
		attach05Display = "1".equals(newAttach05Display);
		attach06Display = "1".equals(newAttach06Display);
		attach07Display = "1".equals(newAttach07Display);
		attach08Display = "1".equals(newAttach08Display);
		attach09Display = "1".equals(newAttach09Display);
		attach10Display = "1".equals(newAttach10Display);
		machineNoDisplay = "1".equals(newMachineNoDisplay);
		machineNameDisplay = "1".equals(newMachineNameDisplay);
		machineSpec1Display = "1".equals(newMachineSpec1Display);
		machineSpec2Display = "1".equals(newMachineSpec2Display);
		machineSpec3Display = "1".equals(newMachineSpec3Display);
		machineSpec4Display = "1".equals(newMachineSpec4Display);
		machineSpec5Display = "1".equals(newMachineSpec5Display);
		drwgTypeDisplay = "1".equals(newDrwgTypeDisplay);
		drwgSizeDisplay = "1".equals(newDrwgSizeDisplay);
		issueDisplay = "1".equals(newIssueDisplay);
		supplyDisplay = "1".equals(newSupplyDisplay);
		cadTypeDisplay = "1".equals(newCadTypeDisplay);
		engineerDisplay = "1".equals(newEngineerDisplay);
		prohibitDisplay = "1".equals(newProhibitDisplay);
		prohibitDateDisplay = "1".equals(newProhibitDateDisplay);
		prohibitEmpnoDisplay = "1".equals(newProhibitEmpnoDisplay);
		prohibitNameDisplay = "1".equals(newProhibitNameDisplay);
		pagesDisplay = "1".equals(newPagesDisplay);
		aclDisplay = "1".equals(newAclDisplay);
		aclUpdateDisplay = "1".equals(newAclUpdateDisplay);
		aclEmpnoDisplay = "1".equals(newAclEmpnoDisplay);
		aclNameDisplay = "1".equals(newAclNameDisplay);
		attachMaxDisplay = "1".equals(newAttachMaxDisplay);
		latestDisplay = "1".equals(newLatestDisplay);
		replaceDisplay = "1".equals(newReplaceDisplay);
		createDivDisplay = "1".equals(newCreateDivDisplay);
		mediaIdDisplay = "1".equals(newMediaIdDisplay);
		twinDrwgNoDisplay = "1".equals(newTwinDrwgNoDisplay);
		viewPrintDoc = newViewPrintDoc;
		aclMap = newAclMap;
	}

}
