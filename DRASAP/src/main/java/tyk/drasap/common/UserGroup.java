package tyk.drasap.common;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * ���[�U�[�O���[�v��\���B
 * ���p�҃O���[�v�}�X�^�[�e�[�u��(USER_GROUP_MASTER)�ɑΉ��B
 */
public class UserGroup {
	String cd = ""; // ���p�҃O���[�v�R�[�h
	String name = ""; // ���p�҃O���[�v��
	boolean viewStamp; // VIEW�̃X�^���v�Btrue�Ȃ�X�^���v����
	boolean eucStamp; // EUC�v�����^�̃X�^���v�Btrue�Ȃ�X�^���v����
	boolean pltrStamp; // ��p�v�����^�̃X�^���v�Btrue�Ȃ�X�^���v����
	ArrayList<Printer> enablePrinters = new ArrayList<Printer>();// ���p�\�ȃv�����^�[�B������Printer�B
	boolean reqImport; // �}�ʓo�^�˗��\�Ȃ�true�B
	boolean reqPrint; // �}�ʏo�͎w���\�Ȃ�true�B
	boolean reqCheckout; // ���}�ؗp�˗��\�Ȃ�true�B
	boolean reqOther; // �}�ʈȊO�ĕt�˗��\�Ȃ�true�B
	// ����ȍ~�� xxxxxDisplay �́A�}�ʌ����̉�ʂɂ����Ďg�p�\����\���B
	// true�Ȃ�g�p�\�B
	boolean drwgNoDisplay; // �}�Ԃ�\���\�Ȃ�true�B
	boolean createDateDisplay; // �쐬������\���\�Ȃ�true�B
	boolean createUserDisplay; // �쐬�҂�\���\�Ȃ�true�B
	boolean machineJpDisplay; // ���u����(�a��)��\���\�Ȃ�true�B
	boolean machineEnDisplay; // ���u����(�p��)��\���\�Ȃ�true�B
	boolean usedForDisplay; // �p�r��\���\�Ȃ�true�B
	boolean materialDisplay; // �ގ���\���\�Ȃ�true�B
	boolean treatmentDisplay; // �M�E�\�ʏ�����\���\�Ȃ�true�B
	boolean procurementDisplay; // ���B�敪��\���\�Ȃ�true�B
	boolean supplyerJpDisplay; // ���[�J�[��(�a��)��\���\�Ȃ�true�B
	boolean supplyerEnDisplay; // ���[�J�[��(�p��)��\���\�Ȃ�true�B
	boolean supplyerTypeDisplay;// ���[�J�[�`����\���\�Ȃ�true�B
	boolean attach01Display; // �Y�t�}��1��\���\�Ȃ�true�B
	boolean attach02Display; // �Y�t�}��2��\���\�Ȃ�true�B
	boolean attach03Display; // �Y�t�}��3��\���\�Ȃ�true�B
	boolean attach04Display; // �Y�t�}��4��\���\�Ȃ�true�B
	boolean attach05Display; // �Y�t�}��5��\���\�Ȃ�true�B
	boolean attach06Display; // �Y�t�}��6��\���\�Ȃ�true�B
	boolean attach07Display; // �Y�t�}��7��\���\�Ȃ�true�B
	boolean attach08Display; // �Y�t�}��8��\���\�Ȃ�true�B
	boolean attach09Display; // �Y�t�}��9��\���\�Ȃ�true�B
	boolean attach10Display; // �Y�t�}��10��\���\�Ȃ�true�B
	boolean machineNoDisplay; // ���uNo��\���\�Ȃ�true�B
	boolean machineNameDisplay; // �@�햼�̂�\���\�Ȃ�true�B
	boolean machineSpec1Display;// ���u�d�l1��\���\�Ȃ�true�B
	boolean machineSpec2Display;// ���u�d�l2��\���\�Ȃ�true�B
	boolean machineSpec3Display;// ���u�d�l3��\���\�Ȃ�true�B
	boolean machineSpec4Display;// ���u�d�l4��\���\�Ȃ�true�B
	boolean machineSpec5Display;// ���u�d�l5��\���\�Ȃ�true�B
	boolean drwgTypeDisplay; // �}�ʎ�ނ�\���\�Ȃ�true�B
	boolean drwgSizeDisplay; // �}�ʃT�C�Y��\���\�Ȃ�true�B
	boolean issueDisplay; // ��o�敪��\���\�Ȃ�true�B
	boolean supplyDisplay; // ���Ջ敪��\���\�Ȃ�true�B
	boolean cadTypeDisplay; // CAD��ʂ�\���\�Ȃ�true�B
	boolean engineerDisplay; // �݌v�Җ���\���\�Ȃ�true�B
	boolean prohibitDisplay; // �g�p�֎~�敪��\���\�Ȃ�true�B
	boolean prohibitDateDisplay; // �g�p�֎~������\���\�Ȃ�true�B
	boolean prohibitEmpnoDisplay; // �g�p�֎~�ҐE�Ԃ�\���\�Ȃ�true�B
	boolean prohibitNameDisplay; // �g�p�֎~�Җ��O��\���\�Ȃ�true�B
	boolean pagesDisplay; // �y�[�W����\���\�Ȃ�true�B
	boolean aclDisplay; // �A�N�Z�X���x����\���\�Ȃ�true�B
	boolean aclUpdateDisplay; // �A�N�Z�X���x���ύX������\���\�Ȃ�true�B
	boolean aclEmpnoDisplay; // �A�N�Z�X���x���ŏI�ύX�ҐE�Ԃ�\���\�Ȃ�true�B
	boolean aclNameDisplay; // �A�N�Z�X���x���ŏI�ύX�Җ��O��\���\�Ȃ�true�B
	boolean attachMaxDisplay; // �Y�t�}����\���\�Ȃ�true�B
	boolean latestDisplay; // �ŐV�}�ԋ敪��\���\�Ȃ�true�B
	boolean replaceDisplay; // ���փt���O��\���\�Ȃ�true�B
	boolean createDivDisplay; // �쐬�����R�[�h��\���\�Ȃ�true�B
	boolean mediaIdDisplay; // ���f�B�AID��\���\�Ȃ�true�B
	boolean twinDrwgNoDisplay; // 1��2�i�Ԑ}�Ԃ�\���\�Ȃ�true�B
	String viewPrintDoc; // �{���t�H�[�}�b�g�@1�FTIFF�A2�FPDF
	// �A�N�Z�X������HashMap�Ŏ���
	// key=�A�N�Z�X���x�� value=�A�N�Z�X���x���l{1,2,3}
	HashMap<String, String> aclMap = new HashMap<String, String>();//

	/**
	 * �R���X�g���N�^
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
