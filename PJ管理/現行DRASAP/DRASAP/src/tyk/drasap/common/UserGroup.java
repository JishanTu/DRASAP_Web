package tyk.drasap.common;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * ���[�U�[�O���[�v��\���B
 * ���p�҃O���[�v�}�X�^�[�e�[�u��(USER_GROUP_MASTER)�ɑΉ��B
 */
public class UserGroup {
	String cd = "";				// ���p�҃O���[�v�R�[�h
	String name = "";			// ���p�҃O���[�v��
	boolean viewStamp;			// VIEW�̃X�^���v�Btrue�Ȃ�X�^���v����
	boolean eucStamp;			// EUC�v�����^�̃X�^���v�Btrue�Ȃ�X�^���v����
	boolean pltrStamp;			// ��p�v�����^�̃X�^���v�Btrue�Ȃ�X�^���v����
	ArrayList<Printer> enablePrinters = new ArrayList<Printer>();// ���p�\�ȃv�����^�[�B������Printer�B
	boolean reqImport;			// �}�ʓo�^�˗��\�Ȃ�true�B
	boolean reqPrint;			// �}�ʏo�͎w���\�Ȃ�true�B
	boolean reqCheckout;		// ���}�ؗp�˗��\�Ȃ�true�B
	boolean reqOther;			// �}�ʈȊO�ĕt�˗��\�Ȃ�true�B
	// ����ȍ~�� xxxxxDisplay �́A�}�ʌ����̉�ʂɂ����Ďg�p�\����\���B
	// true�Ȃ�g�p�\�B
	boolean drwgNoDisplay;		// �}�Ԃ�\���\�Ȃ�true�B
	boolean createDateDisplay;	// �쐬������\���\�Ȃ�true�B
	boolean createUserDisplay;	// �쐬�҂�\���\�Ȃ�true�B
	boolean machineJpDisplay;	// ���u����(�a��)��\���\�Ȃ�true�B
	boolean machineEnDisplay;	// ���u����(�p��)��\���\�Ȃ�true�B
	boolean usedForDisplay;		// �p�r��\���\�Ȃ�true�B
	boolean materialDisplay;	// �ގ���\���\�Ȃ�true�B
	boolean treatmentDisplay;	// �M�E�\�ʏ�����\���\�Ȃ�true�B
	boolean procurementDisplay;	// ���B�敪��\���\�Ȃ�true�B
	boolean supplyerJpDisplay;	// ���[�J�[��(�a��)��\���\�Ȃ�true�B
	boolean supplyerEnDisplay;	// ���[�J�[��(�p��)��\���\�Ȃ�true�B
	boolean supplyerTypeDisplay;// ���[�J�[�`����\���\�Ȃ�true�B
	boolean attach01Display;	// �Y�t�}��1��\���\�Ȃ�true�B
	boolean attach02Display;	// �Y�t�}��2��\���\�Ȃ�true�B
	boolean attach03Display;	// �Y�t�}��3��\���\�Ȃ�true�B
	boolean attach04Display;	// �Y�t�}��4��\���\�Ȃ�true�B
	boolean attach05Display;	// �Y�t�}��5��\���\�Ȃ�true�B
	boolean attach06Display;	// �Y�t�}��6��\���\�Ȃ�true�B
	boolean attach07Display;	// �Y�t�}��7��\���\�Ȃ�true�B
	boolean attach08Display;	// �Y�t�}��8��\���\�Ȃ�true�B
	boolean attach09Display;	// �Y�t�}��9��\���\�Ȃ�true�B
	boolean attach10Display;	// �Y�t�}��10��\���\�Ȃ�true�B
	boolean machineNoDisplay;	// ���uNo��\���\�Ȃ�true�B
	boolean machineNameDisplay;	// �@�햼�̂�\���\�Ȃ�true�B
	boolean machineSpec1Display;// ���u�d�l1��\���\�Ȃ�true�B
	boolean machineSpec2Display;// ���u�d�l2��\���\�Ȃ�true�B
	boolean machineSpec3Display;// ���u�d�l3��\���\�Ȃ�true�B
	boolean machineSpec4Display;// ���u�d�l4��\���\�Ȃ�true�B
	boolean machineSpec5Display;// ���u�d�l5��\���\�Ȃ�true�B
	boolean drwgTypeDisplay;	// �}�ʎ�ނ�\���\�Ȃ�true�B
	boolean drwgSizeDisplay;	// �}�ʃT�C�Y��\���\�Ȃ�true�B
	boolean issueDisplay;		// ��o�敪��\���\�Ȃ�true�B
	boolean supplyDisplay;		// ���Ջ敪��\���\�Ȃ�true�B
	boolean cadTypeDisplay;		// CAD��ʂ�\���\�Ȃ�true�B
	boolean engineerDisplay;	// �݌v�Җ���\���\�Ȃ�true�B
	boolean prohibitDisplay;	// �g�p�֎~�敪��\���\�Ȃ�true�B
	boolean prohibitDateDisplay;	// �g�p�֎~������\���\�Ȃ�true�B
	boolean prohibitEmpnoDisplay;	// �g�p�֎~�ҐE�Ԃ�\���\�Ȃ�true�B
	boolean prohibitNameDisplay;	// �g�p�֎~�Җ��O��\���\�Ȃ�true�B
	boolean pagesDisplay;		// �y�[�W����\���\�Ȃ�true�B
	boolean aclDisplay;			// �A�N�Z�X���x����\���\�Ȃ�true�B
	boolean aclUpdateDisplay;	// �A�N�Z�X���x���ύX������\���\�Ȃ�true�B
	boolean aclEmpnoDisplay;	// �A�N�Z�X���x���ŏI�ύX�ҐE�Ԃ�\���\�Ȃ�true�B
	boolean aclNameDisplay;		// �A�N�Z�X���x���ŏI�ύX�Җ��O��\���\�Ȃ�true�B
	boolean attachMaxDisplay;	// �Y�t�}����\���\�Ȃ�true�B
	boolean latestDisplay;		// �ŐV�}�ԋ敪��\���\�Ȃ�true�B
	boolean replaceDisplay;		// ���փt���O��\���\�Ȃ�true�B
	boolean createDivDisplay;	// �쐬�����R�[�h��\���\�Ȃ�true�B
	boolean mediaIdDisplay;		// ���f�B�AID��\���\�Ȃ�true�B
	boolean twinDrwgNoDisplay;	// 1��2�i�Ԑ}�Ԃ�\���\�Ȃ�true�B
	String viewPrintDoc;		// �{���t�H�[�}�b�g�@1�FTIFF�A2�FPDF
	// �A�N�Z�X������HashMap�Ŏ���
	// key=�A�N�Z�X���x�� value=�A�N�Z�X���x���l{1,2,3}
	HashMap<String, String> aclMap = new HashMap<String, String>();// 
	/**
	 * �R���X�g���N�^
	 */
	public UserGroup(String newCd, String newName,
			String newViewStamp,String newEucStamp,String newPltrStamp,
			ArrayList<Printer> newEnablePrinters,
			String newReqImport,String newReqPrint,String newReqCheckout,String newReqOther,
			String newDrwgNoDisplay,String newCreateDateDisplay,String newCreateUserDisplay,
			String newMachineJpDisplay,String newMachineEnDisplay,
			String newUsedForDisplay,String newMaterialDisplay,String newTreatmentDisplay,String newProcurementDisplay,
			String newSupplyerJpDisplay,String newSupplyerEnDisplay,String newSupplyerTypeDisplay,
			String newAttach01Display,String newAttach02Display,String newAttach03Display,String newAttach04Display,
			String newAttach05Display,String newAttach06Display,String newAttach07Display,String newAttach08Display,
			String newAttach09Display,String newAttach10Display,
			String newMachineNoDisplay,String newMachineNameDisplay,
			String newMachineSpec1Display,String newMachineSpec2Display,String newMachineSpec3Display,
			String newMachineSpec4Display,String newMachineSpec5Display,
			String newDrwgTypeDisplay,String newDrwgSizeDisplay,
			String newIssueDisplay,String newSupplyDisplay,
			String newCadTypeDisplay,String newEngineerDisplay,
			String newProhibitDisplay,String newProhibitDateDisplay,
			String newProhibitEmpnoDisplay,String newProhibitNameDisplay,
			String newPagesDisplay,
			String newAclDisplay,String newAclUpdateDisplay,String newAclEmpnoDisplay,String newAclNameDisplay,
			String newAttachMaxDisplay,String newLatestDisplay,String newReplaceDisplay,
			String newCreateDivDisplay,String newMediaIdDisplay,String newTwinDrwgNoDisplay,
			String newViewPrintDoc,
			HashMap<String, String> newAclMap) {
		super();
		this.cd = newCd;
		this.name = newName;
		this.viewStamp = "1".equals(newViewStamp);
		this.eucStamp = "1".equals(newEucStamp);
		this.pltrStamp = "1".equals(newPltrStamp);
		this.enablePrinters = newEnablePrinters;
		this.reqImport = "1".equals(newReqImport);
		this.reqPrint = "1".equals(newReqPrint);
		this.reqCheckout = "1".equals(newReqCheckout);
		this.reqOther = "1".equals(newReqOther);
		this.drwgNoDisplay = "1".equals(newDrwgNoDisplay);
		this.createDateDisplay = "1".equals(newCreateDateDisplay);
		this.createUserDisplay = "1".equals(newCreateUserDisplay);
		this.machineJpDisplay = "1".equals(newMachineJpDisplay);
		this.machineEnDisplay = "1".equals(newMachineEnDisplay);
		this.usedForDisplay = "1".equals(newUsedForDisplay);
		this.materialDisplay = "1".equals(newMaterialDisplay);
		this.treatmentDisplay = "1".equals(newTreatmentDisplay);
		this.procurementDisplay = "1".equals(newProcurementDisplay);
		this.supplyerJpDisplay = "1".equals(newSupplyerJpDisplay);
		this.supplyerEnDisplay = "1".equals(newSupplyerEnDisplay);
		this.supplyerTypeDisplay = "1".equals(newSupplyerTypeDisplay);
		this.attach01Display = "1".equals(newAttach01Display);
		this.attach02Display = "1".equals(newAttach02Display);
		this.attach03Display = "1".equals(newAttach03Display);
		this.attach04Display = "1".equals(newAttach04Display);
		this.attach05Display = "1".equals(newAttach05Display);
		this.attach06Display = "1".equals(newAttach06Display);
		this.attach07Display = "1".equals(newAttach07Display);
		this.attach08Display = "1".equals(newAttach08Display);
		this.attach09Display = "1".equals(newAttach09Display);
		this.attach10Display = "1".equals(newAttach10Display);
		this.machineNoDisplay = "1".equals(newMachineNoDisplay);
		this.machineNameDisplay = "1".equals(newMachineNameDisplay);
		this.machineSpec1Display = "1".equals(newMachineSpec1Display);
		this.machineSpec2Display = "1".equals(newMachineSpec2Display);
		this.machineSpec3Display = "1".equals(newMachineSpec3Display);
		this.machineSpec4Display = "1".equals(newMachineSpec4Display);
		this.machineSpec5Display = "1".equals(newMachineSpec5Display);
		this.drwgTypeDisplay = "1".equals(newDrwgTypeDisplay);
		this.drwgSizeDisplay = "1".equals(newDrwgSizeDisplay);
		this.issueDisplay = "1".equals(newIssueDisplay);
		this.supplyDisplay = "1".equals(newSupplyDisplay);
		this.cadTypeDisplay = "1".equals(newCadTypeDisplay);
		this.engineerDisplay = "1".equals(newEngineerDisplay);
		this.prohibitDisplay = "1".equals(newProhibitDisplay);
		this.prohibitDateDisplay = "1".equals(newProhibitDateDisplay);
		this.prohibitEmpnoDisplay = "1".equals(newProhibitEmpnoDisplay);
		this.prohibitNameDisplay = "1".equals(newProhibitNameDisplay);
		this.pagesDisplay = "1".equals(newPagesDisplay);
		this.aclDisplay = "1".equals(newAclDisplay);
		this.aclUpdateDisplay = "1".equals(newAclUpdateDisplay);
		this.aclEmpnoDisplay = "1".equals(newAclEmpnoDisplay);
		this.aclNameDisplay = "1".equals(newAclNameDisplay);
		this.attachMaxDisplay = "1".equals(newAttachMaxDisplay);
		this.latestDisplay = "1".equals(newLatestDisplay);
		this.replaceDisplay = "1".equals(newReplaceDisplay);
		this.createDivDisplay = "1".equals(newCreateDivDisplay);
		this.mediaIdDisplay = "1".equals(newMediaIdDisplay);
		this.twinDrwgNoDisplay = "1".equals(newTwinDrwgNoDisplay);
		this.viewPrintDoc = newViewPrintDoc;
		this.aclMap = newAclMap;
	}

}
