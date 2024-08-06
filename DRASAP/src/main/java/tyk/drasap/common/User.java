package tyk.drasap.common;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;

import tyk.drasap.search.SearchResultElement;

/**
 * ���O�C���������[�U�[��\���N���X�B
 * ���[�U�[�Ǘ��}�X�^�[�e�[�u��(USER_MASTER)�ɑΉ��B
 * @author FUMI
 * @version 2013/09/13 yamagishi
 */
public class User {
	//	private static Category category = Category.getInstance(User.class.getName());
	// 2013.08.03 yamagishi add. start
	//    private static DataSource ds;
	//
	//    static {
	//        try {
	//            ds = DataSourceFactory.getOracleDataSource();
	//        } catch (Exception e) {
	//        }
	//    }
	// 2013.08.03 yamagishi add. end

	String id = "";// ���[�U�[ID
	String name = "";// ���O(�a)
	String nameE = "";// ���O(�p)
	String host = "";// �z�X�g�� request#getRemoteAddr�Ŏ擾
	String dept = "";// ��������
	String deptName = "";// �������喼
	String defaultUserGroup = "";// �f�t�H���g�̃��[�U�[�O���[�v�B�������Ă��錴�����傩�瓱�����B
	ArrayList<UserGroup> userGroups = new ArrayList<UserGroup>();// �������Ă��郆�[�U�[�O���[�v�B������UserGroup�B
	ArrayList<Printer> enablePrinters = new ArrayList<Printer>();// ���p�\�ȃv�����^�[�B������Printer�B
	String displayCount = "";// ������1�y�[�W������̕\������
	String thumbnailSize = "M";// �T���l�C���T�C�Y
	public static int searchSelColNum = 10;
	public static int viewSelColNum = 12;
	private ArrayList<String> searchSelColList = new ArrayList<>(); // ���������J�������X�g
	private ArrayList<String> viewSelColList = new ArrayList<>(); // �������ʃJ�������X�g
	// 2013.07.24 yamagishi add. start
	private String aclUpdateFlag = "";
	private String aclBatchUpdateFlag = "";
	private String dlManagerFlag = "";
	// 2013.07.24 yamagishi add. end
	// 2019.09.20 yamamoto add. start
	private Date passwdUpdDate = null;
	private String reproUserFlag = "";
	private String dwgRegReqFlag = "";
	// 2019.09.20 yamamoto add. end
	// 2020.02.10 yamamoto add. start
	private String multiPdfFlag = "";
	// 2020.02.10 yamamoto add. end
	private String language = "Japanese";
	private boolean onlyNewest = false;
	//	String language = "English";
	boolean admin = false;// �Ǘ��҂Ȃ� true
	boolean delAdmin = false;// �Ǘ��҂Ȃ� true
	int position;// �E��
	// ���[�U�[�̌�����\���B
	// �������Ă��郆�[�U�[�O���[�v�ɂ���Đ��䂳���B
	boolean viewStamp;// VIEW�̃X�^���v�Btrue�Ȃ�X�^���v����
	boolean eucStamp;// EUC�v�����^�̃X�^���v�Btrue�Ȃ�X�^���v����
	boolean pltrStamp;// ��p�v�����^�̃X�^���v�Btrue�Ȃ�X�^���v����
	boolean reqImport;// �}�ʓo�^�˗��\�Ȃ�true�B
	boolean reqPrint;// �}�ʏo�͎w���\�Ȃ�true�B
	boolean reqCheckout;// ���}�ؗp�˗��\�Ȃ�true�B
	boolean reqOther;// �}�ʈȊO�ĕt�˗��\�Ȃ�true�B
	// ����ȍ~�� xxxxxDisplay �́A�}�ʌ����̉�ʂɂ����Ďg�p�\����\���B
	// true�Ȃ�g�p�\�B
	boolean drwgNoDisplay;// �}�Ԃ�\���\�Ȃ�true�B
	boolean createDateDisplay;// �쐬������\���\�Ȃ�true�B
	boolean createUserDisplay;// �쐬�҂�\���\�Ȃ�true�B
	boolean machineJpDisplay;// ���u����(�a��)��\���\�Ȃ�true�B
	boolean machineEnDisplay;// ���u����(�p��)��\���\�Ȃ�true�B
	boolean usedForDisplay;// �p�r��\���\�Ȃ�true�B
	boolean materialDisplay;// �ގ���\���\�Ȃ�true�B
	boolean treatmentDisplay;// �M�E�\�ʏ�����\���\�Ȃ�true�B
	boolean procurementDisplay;// ���B�敪��\���\�Ȃ�true�B
	boolean supplyerJpDisplay;// ���[�J�[��(�a��)��\���\�Ȃ�true�B
	boolean supplyerEnDisplay;// ���[�J�[��(�p��)��\���\�Ȃ�true�B
	boolean supplyerTypeDisplay;// ���[�J�[�`����\���\�Ȃ�true�B
	boolean attach01Display;// �Y�t�}��1��\���\�Ȃ�true�B
	boolean attach02Display;// �Y�t�}��2��\���\�Ȃ�true�B
	boolean attach03Display;// �Y�t�}��3��\���\�Ȃ�true�B
	boolean attach04Display;// �Y�t�}��4��\���\�Ȃ�true�B
	boolean attach05Display;// �Y�t�}��5��\���\�Ȃ�true�B
	boolean attach06Display;// �Y�t�}��6��\���\�Ȃ�true�B
	boolean attach07Display;// �Y�t�}��7��\���\�Ȃ�true�B
	boolean attach08Display;// �Y�t�}��8��\���\�Ȃ�true�B
	boolean attach09Display;// �Y�t�}��9��\���\�Ȃ�true�B
	boolean attach10Display;// �Y�t�}��10��\���\�Ȃ�true�B
	boolean machineNoDisplay;// ���uNo��\���\�Ȃ�true�B
	boolean machineNameDisplay;// �@�햼�̂�\���\�Ȃ�true�B
	boolean machineSpec1Display;// ���u�d�l1��\���\�Ȃ�true�B
	boolean machineSpec2Display;// ���u�d�l2��\���\�Ȃ�true�B
	boolean machineSpec3Display;// ���u�d�l3��\���\�Ȃ�true�B
	boolean machineSpec4Display;// ���u�d�l4��\���\�Ȃ�true�B
	boolean machineSpec5Display;// ���u�d�l5��\���\�Ȃ�true�B
	boolean drwgTypeDisplay;// �}�ʎ�ނ�\���\�Ȃ�true�B
	boolean drwgSizeDisplay;// �}�ʃT�C�Y��\���\�Ȃ�true�B
	boolean issueDisplay;// ��o�敪��\���\�Ȃ�true�B
	boolean supplyDisplay;// ���Ջ敪��\���\�Ȃ�true�B
	boolean cadTypeDisplay;// CAD��ʂ�\���\�Ȃ�true�B
	boolean engineerDisplay;// �݌v�Җ���\���\�Ȃ�true�B
	boolean prohibitDisplay;// �g�p�֎~�敪��\���\�Ȃ�true�B
	boolean prohibitDateDisplay;// �g�p�֎~������\���\�Ȃ�true�B
	boolean prohibitEmpnoDisplay;// �g�p�֎~�ҐE�Ԃ�\���\�Ȃ�true�B
	boolean prohibitNameDisplay;// �g�p�֎~�Җ��O��\���\�Ȃ�true�B
	boolean pagesDisplay;// �y�[�W����\���\�Ȃ�true�B
	boolean aclDisplay;// �A�N�Z�X���x����\���\�Ȃ�true�B
	boolean aclUpdateDisplay;// �A�N�Z�X���x���ύX������\���\�Ȃ�true�B
	boolean aclEmpnoDisplay;// �A�N�Z�X���x���ŏI�ύX�ҐE�Ԃ�\���\�Ȃ�true�B
	boolean aclNameDisplay;// �A�N�Z�X���x���ŏI�ύX�Җ��O��\���\�Ȃ�true�B
	boolean attachMaxDisplay;// �Y�t�}����\���\�Ȃ�true�B
	boolean latestDisplay;// �ŐV�}�ԋ敪��\���\�Ȃ�true�B
	boolean replaceDisplay;// ���փt���O��\���\�Ȃ�true�B
	boolean createDivDisplay;// �쐬�����R�[�h��\���\�Ȃ�true�B
	boolean mediaIdDisplay;// ���f�B�AID��\���\�Ȃ�true�B
	boolean twinDrwgNoDisplay;// 1��2�i�Ԑ}�Ԃ�\���\�Ȃ�true�B
	// �{���t�H�[�}�b�g���A�N�Z�X���x�����Ɏ���
	// key=�A�N�Z�X���x��  value= 1�FTIFF�A2�FPDF
	HashMap<String, String> viewPrintDoc = new HashMap<String, String>();
	// �A�N�Z�X������HashMap�Ŏ���
	// key=�A�N�Z�X���x�� value=�A�N�Z�X���x���l{1,2,3}
	HashMap<String, String> aclMap = new HashMap<String, String>();
	String maxAclValue = "0";// ���̃��[�U�[�����ō��̃A�N�Z�X���x���l�E�E�E�����J�n�{�^���̃��b�N�Ŏg�p����
	private String sys_id = null;

	// ------------------------------------------ �R���X�g���N�^�[
	/**
	 * @param newHost �z�X�g��
	 */
	public User(String newHost) {
		host = newHost;
	}

	// ------------------------------------------ Method
	/**
	 * ���[�U�[�������p�҃O���[�v�����������B
	 * ���̏������Ń��[�U�[�̌������ύX����Ă����B
	 * �����̊�{�́A�L�����B���[�U�[�O���[�v��1�ł�true�Ȃ�A���[�U�[�Ƃ���true�ɁB
	 * �������X�^���v�����Ɋւ��ẮA�����̌������傩�瓱����錠���Ɠ���ɂ���B
	 * �g�p�\�ȃv�����^�Ɋւ��Ă��A���̏������ōs����B
	 * @param userGroup
	 * @throws Exception
	 */
	public void addUserGroup(UserGroup userGroup) throws Exception {
		userGroups.add(userGroup);
		// ���[�U�[�O���[�v�̌��������ɁA���[�U�[�̌������Z�b�g����
		// ��{�͌������L���Ƃ�B
		reqImport = reqImport || userGroup.reqImport;
		reqPrint = reqPrint || userGroup.reqPrint;
		reqCheckout = reqCheckout || userGroup.reqCheckout;
		reqOther = reqOther || userGroup.reqOther;
		drwgNoDisplay = drwgNoDisplay || userGroup.drwgNoDisplay;
		createDateDisplay = createDateDisplay || userGroup.createDateDisplay;
		createUserDisplay = createUserDisplay || userGroup.createUserDisplay;
		machineJpDisplay = machineJpDisplay || userGroup.machineJpDisplay;
		machineEnDisplay = machineEnDisplay || userGroup.machineEnDisplay;
		usedForDisplay = usedForDisplay || userGroup.usedForDisplay;
		materialDisplay = materialDisplay || userGroup.materialDisplay;
		treatmentDisplay = treatmentDisplay || userGroup.treatmentDisplay;
		procurementDisplay = procurementDisplay || userGroup.procurementDisplay;
		supplyerJpDisplay = supplyerJpDisplay || userGroup.supplyerJpDisplay;
		supplyerEnDisplay = supplyerEnDisplay || userGroup.supplyerEnDisplay;
		supplyerTypeDisplay = supplyerTypeDisplay || userGroup.supplyerTypeDisplay;
		attach01Display = attach01Display || userGroup.attach01Display;
		attach02Display = attach02Display || userGroup.attach02Display;
		attach03Display = attach03Display || userGroup.attach03Display;
		attach04Display = attach04Display || userGroup.attach04Display;
		attach05Display = attach05Display || userGroup.attach05Display;
		attach06Display = attach06Display || userGroup.attach06Display;
		attach07Display = attach07Display || userGroup.attach07Display;
		attach08Display = attach08Display || userGroup.attach08Display;
		attach09Display = attach09Display || userGroup.attach09Display;
		attach10Display = attach10Display || userGroup.attach10Display;
		machineNoDisplay = machineNoDisplay || userGroup.machineNoDisplay;
		machineNameDisplay = machineNameDisplay || userGroup.machineNameDisplay;
		machineSpec1Display = machineSpec1Display || userGroup.machineSpec1Display;
		machineSpec2Display = machineSpec2Display || userGroup.machineSpec2Display;
		machineSpec3Display = machineSpec3Display || userGroup.machineSpec3Display;
		machineSpec4Display = machineSpec4Display || userGroup.machineSpec4Display;
		machineSpec5Display = machineSpec5Display || userGroup.machineSpec5Display;
		drwgTypeDisplay = drwgTypeDisplay || userGroup.drwgTypeDisplay;
		drwgSizeDisplay = drwgSizeDisplay || userGroup.drwgSizeDisplay;
		issueDisplay = issueDisplay || userGroup.issueDisplay;
		supplyDisplay = supplyDisplay || userGroup.supplyDisplay;
		cadTypeDisplay = cadTypeDisplay || userGroup.cadTypeDisplay;
		engineerDisplay = engineerDisplay || userGroup.engineerDisplay;
		prohibitDisplay = prohibitDisplay || userGroup.prohibitDisplay;
		prohibitDateDisplay = prohibitDateDisplay || userGroup.prohibitDateDisplay;
		prohibitEmpnoDisplay = prohibitEmpnoDisplay || userGroup.prohibitEmpnoDisplay;
		prohibitNameDisplay = prohibitNameDisplay || userGroup.prohibitNameDisplay;
		pagesDisplay = pagesDisplay || userGroup.pagesDisplay;
		aclDisplay = aclDisplay || userGroup.aclDisplay;
		aclUpdateDisplay = aclUpdateDisplay || userGroup.aclUpdateDisplay;
		aclEmpnoDisplay = aclEmpnoDisplay || userGroup.aclEmpnoDisplay;
		aclNameDisplay = aclNameDisplay || userGroup.aclNameDisplay;
		attachMaxDisplay = attachMaxDisplay || userGroup.attachMaxDisplay;
		latestDisplay = latestDisplay || userGroup.latestDisplay;
		replaceDisplay = replaceDisplay || userGroup.replaceDisplay;
		createDivDisplay = createDivDisplay || userGroup.createDivDisplay;
		mediaIdDisplay = mediaIdDisplay || userGroup.mediaIdDisplay;
		twinDrwgNoDisplay = twinDrwgNoDisplay || userGroup.twinDrwgNoDisplay;
		// �X�^���v�����敪�Ɋւ��ẮA�f�t�H���g�̃��[�U�[�O���[�v�̒l�Ƃ���
		//
		// �����F�X�^���v�����敪�Ɋւ��Ă��������L���Ƃ�B'04.May.12 by Hirata
		viewStamp = viewStamp || userGroup.viewStamp;// VIEW�̃X�^���v�Btrue�Ȃ�X�^���v����
		eucStamp = eucStamp || userGroup.eucStamp;// EUC�v�����^�̃X�^���v�Btrue�Ȃ�X�^���v����
		pltrStamp = pltrStamp || userGroup.pltrStamp;// ��p�v�����^�̃X�^���v�Btrue�Ȃ�X�^���v����
		// ���̗��p�҃O���[�v���g�p�\�ȃv�����^���A���[�U�[�̎g�p�\�ȃv�����^�Ƃ��ăZ�b�g����B

		for (int i = 0; i < userGroup.enablePrinters.size(); i++) {
			Printer printer = userGroup.enablePrinters.get(i);// ���p�҃O���[�v���g�p�\�ȃv�����^
			// ���łɓ����v�����^���o�^����Ă��Ȃ����m�F����
			boolean registered = false;// ���łɓo�^���݃v�����^�Ȃ�true
			for (int j = 0; j < enablePrinters.size(); j++) {
				// �v�����^ID���r����
				if (printer.getId().equals(enablePrinters.get(j).getId())) {
					registered = true;
					break;
				}
			}
			if (!registered) {// ���o�^�Ȃ�A���[�U�[�̎g�p�\�ȃv�����^�Ƃ��ăZ�b�g����B
				enablePrinters.add(printer);
			}
		}
		// ���p�҃O���[�v�̃A�N�Z�X���x�����A���̃��[�U�[�Ɉڂ��B
		// ���̂Ƃ��L���Ȃ�悤�Ɉڂ�
		Iterator<String> keyIterator = userGroup.aclMap.keySet().iterator();// ���[�U�[�O���[�v�̃A�N�Z�X���x���̃L�[
		while (keyIterator.hasNext()) {
			String aclId = keyIterator.next();// ���[�U�[�O���[�v�̃A�N�Z�X���x���̃L�[
			String grpAclValue = userGroup.aclMap.get(aclId);// ���̃O���[�v�̐ݒ�l
			if (aclMap.containsKey(aclId)) {
				// ���łɓ����A�N�Z�X���x�����ݒ肳��Ă�����
				String uesrAclValue = aclMap.get(aclId);// ���[�U�[�̐ݒ�l
				if (grpAclValue.compareTo(uesrAclValue) == 0) {
					// �O���[�v�ɐݒ肳��Ă���ݒ�l�������̏ꍇ�A"TIFF"��D��
					if ("1".equals(userGroup.viewPrintDoc)) {
						viewPrintDoc.put(aclId, userGroup.viewPrintDoc);
					}
				}
				if (grpAclValue.compareTo(uesrAclValue) > 0) {
					// �O���[�v�ɐݒ肳��Ă���ݒ�l���傫����΁A���[�U�[�ɃZ�b�g������
					aclMap.put(aclId, grpAclValue);
					viewPrintDoc.put(aclId, userGroup.viewPrintDoc);
				}
			} else {
				// �܂����̃A�N�Z�X���x�������ݒ�Ȃ�
				aclMap.put(aclId, grpAclValue);
				viewPrintDoc.put(aclId, userGroup.viewPrintDoc);
			}
			// ���̃��[�U�[�����ō��̃A�N�Z�X���x�����Z�b�g
			if (grpAclValue.compareTo(maxAclValue) > 0) {
				maxAclValue = grpAclValue;
			}
		}
	}

	// 2013.08.03 yamagishi add. start
	/**
	 * isPrintable�̃I�[�o���[�h���\�b�h�BConnection���Ȃ��ꍇ�ɑΉ��B
	 * ��connection�̊Ǘ���Action�ōs���ׁA���p���Ȃ����ƁB
	 * @see isPrintable(SearchResultElement searchResultElement, Connection conn)
	 */

	//    @Deprecated
	//    public boolean isPrintable(SearchResultElement searchResultElement) {
	//        Connection conn = null;
	//        try {
	//            // DB����擾
	//            conn = ds.getConnection();
	//            return isPrintable(searchResultElement, conn, false);
	//        } catch (Exception e) {
	//            return false;
	//        } finally {
	//            if (conn != null) {
	//                try {
	//                    conn.close();
	//                } catch (Exception e) {
	//                }
	//            }
	//        }
	//    }
	// 2013.08.03 yamagishi add. end
	// 2013.06.25 yamagishi modified. start
	//	/**
	//	 * ���̃��[�U�[���A���̐}�ʂ�����\����Ԃ��B
	//	 * View����Tiff�ŕ\���Ȃ�true�B����s��PDF�Ȃ�false�B'04.May.6�ύX
	//	 * 1) ������������E�E�E�A�N�Z�X���x���l��2�ȏ�܂���Admin����������
	//	 * 2) Tiff�ł��邩
	//	 * @param searchResultElement �w�肵���}��
	//	 * @return View����Tiff�ŕ\���Ȃ�true�B����s��PDF�Ȃ�false�B
	//	 */
	//	public boolean isPrintable(SearchResultElement searchResultElement){
	//		if(Integer.parseInt(this.aclMap.get(searchResultElement.getAttr("ACL_ID"))) < 2
	//			&& ! isAdmin()){
	//			// �}�ʂ̃A�N�Z�X���x��ID����擾�ł���A���̃��[�U�[�̃A�N�Z�����x���l��
	//			// 2��菬�����E�E�E��������Ȃ�
	//			// ���A�Ǘ��҂łȂ�
	//			return false;
	//
	//		} else if(! "1".equals(searchResultElement.getFileType())){
	//			// �}�ʃ^�C�v��Tiff�łȂ���΁A����s��
	//			return false;
	//		}
	//		return true;
	//	}
	/**
	 * ���̃��[�U�[���A���̐}�ʂ�����\����Ԃ��B
	 * View����Tiff�ŕ\���Ȃ�true�B����s��PDF�Ȃ�false�B'04.May.6�ύX
	 * 1) ������������E�E�E�A�N�Z�X���x���l��2�ȏ�܂���Admin����������
	 * 2) Tiff�ł��邩
	 * @param searchResultElement �w�肵���}��
	 * @param conn
	 * @return View����Tiff�ŕ\���Ȃ�true�B����s��PDF�Ȃ�false�B
	 */
	public boolean isPrintable(SearchResultElement searchResultElement, Connection conn) throws Exception {
		// DB����擾
		return this.isPrintable(searchResultElement, conn, false);
	}

	/**
	 * ���̃��[�U�[���A���̐}�ʂ�����\����Ԃ��B
	 * View����Tiff�ŕ\���Ȃ�true�B����s��PDF�Ȃ�false�B'04.May.6�ύX
	 * 1) ������������E�E�E�A�N�Z�X���x���l��2�ȏ�܂���Admin����������
	 * 2) Tiff�ł��邩
	 * @param searchResultElement �w�肵���}��
	 * @param conn
	 * @param refSession �f�t�H���g��false
	 * 			true: �Z�b�V��������L���b�V�����擾, false: DB����擾
	 * @return View����Tiff�ŕ\���Ȃ�true�B����s��PDF�Ȃ�false�B
	 */
	public boolean isPrintable(SearchResultElement searchResultElement, Connection conn, boolean refSession) throws Exception {
		// �A�N�Z�X���x���擾
		HashMap<String, String> aclMap = refSession ? this.getAclMap() : this.getAclMap(conn);
		//		if (Integer.parseInt(this.getAclMap(conn).get(searchResultElement.getAttr("ACL_ID"))) < 2
		//				&& !isAdmin()) {
		if (Integer.parseInt(aclMap.get(searchResultElement.getAttr("ACL_ID"))) < 2
				&& !isAdmin() || !"1".equals(searchResultElement.getFileType())) {
			// �}�ʂ̃A�N�Z�X���x��ID����擾�ł���A���̃��[�U�[�̃A�N�Z�����x���l��
			// 2��菬�����E�E�E��������Ȃ�
			// ���A�Ǘ��҂łȂ�
			// �}�ʃ^�C�v��Tiff�łȂ���΁A����s��
			return false;
		}
		return true;
	}
	// 2013.06.25 yamagishi modified. end

	// 2013.06.25 yamagishi modified. start
	//	/**
	//	 * ���̃��[�U�[���A�w��v�����^�ň���\����Ԃ��B
	//	 * View����Tiff�ŕ\���Ȃ�true�B����s��PDF�Ȃ�false�B'04.May.6�ύX
	//	 * 1) ������������E�E�E�A�N�Z�X���x���l��1�ȏ�܂���Admin����������
	//	 * 2) Tiff�ł��邩
	//	 * @param searchResultElement �w�肵���}��
	//	 * @return �w��v�����^�ň���\�Ȃ� true
	//	 */
	//	public boolean isPrintableByReq(SearchResultElement searchResultElement){
	/* OCE�̂��߂̓��ʂȃ��O�o�� **************************
	category.debug("�w�肳�ꂽ�}�ʂ� " + searchResultElement.getDrwgNo() + "�AACL_ID=" + searchResultElement.getAttr("ACL_ID"));
	category.debug("���̂Ƃ��̃��[�U�[��AclMap��");
	Iterator tempKeyItr = aclMap.keySet().iterator();
	while(tempKeyItr.hasNext()){
		Object aclId = tempKeyItr.next();
		category.debug("ACL_ID=" + (String)aclId + ", ACL_VALUE=" + (String)aclMap.get(aclId));
	}
	category.debug("���̌��ʋ��߂���AclValue��" + (String)this.aclMap.get(searchResultElement.getAttr("ACL_ID")));
	*******************************************************/
	//		if(Integer.parseInt(this.aclMap.get(searchResultElement.getAttr("ACL_ID"))) < 1
	//			&& ! isAdmin()){
	//			// �}�ʂ̃A�N�Z�X���x��ID����擾�ł���A���̃��[�U�[�̃A�N�Z�����x���l��
	//			// 1��菬�����E�E�E�����Q�ƌ����Ȃ��B�����Q�ƌ���������Ύw��v�����^�ň���\�B'04.May.6�ύX
	//			// ���A�Ǘ��҂łȂ�
	//			return false;
	//
	//		} else if(! "1".equals(searchResultElement.getFileType())){
	//			// �}�ʃ^�C�v��Tiff�łȂ���΁A����s��
	//			return false;
	//		}
	//		return true;
	//	}
	/**
	 * ���̃��[�U�[���A�w��v�����^�ň���\����Ԃ��B
	 * View����Tiff�ŕ\���Ȃ�true�B����s��PDF�Ȃ�false�B'04.May.6�ύX
	 * 1) ������������E�E�E�A�N�Z�X���x���l��1�ȏ�܂���Admin����������
	 * 2) Tiff�ł��邩
	 * @param searchResultElement �w�肵���}��
	 * @param conn
	 * @return �w��v�����^�ň���\�Ȃ� true
	 */
	public boolean isPrintableByReq(SearchResultElement searchResultElement, Connection conn) throws Exception {
		// DB����擾
		return this.isPrintableByReq(searchResultElement, conn, false);
	}

	/**
	 * ���̃��[�U�[���A�w��v�����^�ň���\����Ԃ��B
	 * View����Tiff�ŕ\���Ȃ�true�B����s��PDF�Ȃ�false�B'04.May.6�ύX
	 * 1) ������������E�E�E�A�N�Z�X���x���l��1�ȏ�܂���Admin����������
	 * 2) Tiff�ł��邩
	 * @param searchResultElement �w�肵���}��
	 * @param conn
	 * @param refSession �f�t�H���g��false
	 * 			true: �Z�b�V��������L���b�V�����擾, false: DB����擾
	 * @return �w��v�����^�ň���\�Ȃ� true
	 */
	public boolean isPrintableByReq(SearchResultElement searchResultElement, Connection conn, boolean refSession) throws Exception {
		// �A�N�Z�X���x���擾
		HashMap<String, String> aclMap = refSession ? this.getAclMap() : this.getAclMap(conn);
		//		if (Integer.parseInt(this.getAclMap(conn).get(searchResultElement.getAttr("ACL_ID"))) < 1
		//				&& !isAdmin()) {
		if (Integer.parseInt(aclMap.get(searchResultElement.getAttr("ACL_ID"))) < 1
				&& !isAdmin() || !"1".equals(searchResultElement.getFileType())) {
			// �}�ʂ̃A�N�Z�X���x��ID����擾�ł���A���̃��[�U�[�̃A�N�Z�����x���l��
			// 1��菬�����E�E�E�����Q�ƌ����Ȃ��B�����Q�ƌ���������Ύw��v�����^�ň���\�B'04.May.6�ύX
			// ���A�Ǘ��҂łȂ�
			// �}�ʃ^�C�v��Tiff�łȂ���΁A����s��
			return false;
		}
		return true;
	}
	// 2013.06.25 yamagishi modified. end

	// 2013.06.25 yamagishi modified. start
	//	/**
	//	 * �w�肳�ꂽ�}�Ԃ��A���̃��[�U�[�ŕύX�\��?
	//	 * @param searchResultElement
	//	 * @return
	//	 */
	//	public boolean isChangableAclv(SearchResultElement searchResultElement){
	//		if(Integer.parseInt(this.aclMap.get(searchResultElement.getAttr("ACL_ID"))) < 3
	//			&& ! isAdmin()){
	//			// �}�ʂ̃A�N�Z�X���x��ID����擾�ł���A���̃��[�U�[�̃A�N�Z�����x���l��
	//			// 3��菬�����E�E�E�ύX�����Ȃ�
	//			// ���A�Ǘ��҂łȂ�
	//			return false;
	//		}
	//		return true;
	//	}
	/**
	 * �w�肳�ꂽ�}�Ԃ��A���̃��[�U�[�ŕύX�\��?
	 * @param searchResultElement
	 * @param conn
	 * @return
	 */
	public boolean isChangableAclv(SearchResultElement searchResultElement, Connection conn) throws Exception {
		// DB����擾
		return this.isChangableAclv(searchResultElement, conn);
	}

	/**
	 * �w�肳�ꂽ�}�Ԃ��A���̃��[�U�[�ŕύX�\��?
	 * @param searchResultElement
	 * @param conn
	 * @param refSession �f�t�H���g��false
	 * 			true: �Z�b�V��������L���b�V�����擾, false: DB����擾
	 * @return
	 */
	public boolean isChangableAclv(SearchResultElement searchResultElement, Connection conn, boolean refSession) throws Exception {
		// �A�N�Z�X���x���擾
		HashMap<String, String> aclMap = refSession ? this.getAclMap() : this.getAclMap(conn);
		//		if (Integer.parseInt(this.getAclMap(conn).get(searchResultElement.getAttr("ACL_ID"))) < 3
		//				&& !isAdmin()) {
		if (Integer.parseInt(aclMap.get(searchResultElement.getAttr("ACL_ID"))) < 3
				&& !isAdmin()) {
			// �}�ʂ̃A�N�Z�X���x��ID����擾�ł���A���̃��[�U�[�̃A�N�Z�����x���l��
			// 3��菬�����E�E�E�ύX�����Ȃ�
			// ���A�Ǘ��҂łȂ�
			return false;
		}
		return true;
	}
	// 2013.06.25 yamagishi modified. end

	/**
	 * ���̃��[�U�[���A�A�N�Z�X���x����ύX�\�Ȃ�ture�B
	 * �u000 < ���[�U�[�̐E�� <= ����E�ʁv�̏����𖞂����K�v������B
	 * @param drasapInfo
	 * @return
	 */
	public boolean hasChangableAclvAuth(DrasapInfo drasapInfo) {
		// ������ύX�B'04.May.19�ύX by Hirata
		return 0 < position &&
				position <= drasapInfo.getAclvChangablePosition();
		//return (this.admin || this.position >= drasapInfo.getAclvChangablePosition());
	}

	/**
	 * ���̃��[�U�[�̎g�p�\�ȃv�����^�[�ꗗ����A�w�肳�ꂽ�v�����^�[�̃I�u�W�F�N�g��Ԃ�
	 * @param printerId
	 * @return
	 */
	public Printer getPrinter(String printerId) {
		for (int i = 0; i < getEnablePrinters().size(); i++) {
			Printer printer = getEnablePrinters().get(i);
			if (printer.getId().equals(printerId)) {
				return printer;
			}
		}
		return null;
	}
	// ------------------------------------------ getter,setter

	/**
	 * @return
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @return
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return
	 */
	public String getNameE() {
		return nameE;
	}

	/**
	 * @param string
	 */
	public void setId(String newId) {
		id = newId;
		if (id == null) {
			id = "";
		}
	}

	/**
	 * @param string
	 */
	public void setName(String newName) {
		name = newName;
		if (name == null) {
			name = "";
		}
	}

	/**
	 * @param string
	 */
	public void setNameE(String newNameE) {
		nameE = newNameE;
		if (nameE == null) {
			nameE = "";
		}
	}

	/**
	 * @return
	 */
	public String getDefaultUserGroup() {
		return defaultUserGroup;
	}

	/**
	 * @param string
	 */
	public void setDefaultUserGroup(String string) {
		defaultUserGroup = string;
		if (defaultUserGroup == null) {
			defaultUserGroup = "";
		}
	}

	/**
	 * @return
	 */
	public String getDept() {
		return dept;
	}

	/**
	 * @param string
	 */
	public void setDept(String string) {
		dept = string;
		if (dept == null) {
			dept = "";
		}
	}

	/**
	 * @return
	 */
	public String getSearchSelCol(int idx) {
		return searchSelColList.get(idx);
	}

	/**
	 * @return
	 */
	public ArrayList<String> getSearchSelColList() {
		return searchSelColList;
	}

	/**
	 * @param string
	 */
	public void setSearchSelCol(int idx, String string) {
		String val = StringUtils.isEmpty(string) ? "" : string;
		searchSelColList.add(idx, val);
	}

	/**
	 * @param string
	 */
	public void setSearchSelColList(ArrayList<String> list) {
		searchSelColList = list;
	}

	/**
	 * @return
	 */
	public String getThumbnailSize() {
		return thumbnailSize;
	}

	/**
	 * @param string
	 */
	public void setThumbnailSize(String thumbnailSize) {
		this.thumbnailSize = thumbnailSize;
	}

	/**
	 * @return
	 */
	public String getDisplayCount() {
		return displayCount;
	}

	/**
	 * @param string
	 */
	public void setDisplayCount(String string) {
		displayCount = string;
		if (displayCount == null) {
			displayCount = "";
		}
	}

	// 2013.06.25 yamagishi modified. start
	/**
	 * DB����̎擾���f�t�H���g�Ƃ���ׁA�p�b�P�[�W�ɕύX
	 * @return
	 */
	//	public HashMap<String, String> getAclMap() {
	HashMap<String, String> getAclMap() {
		return aclMap;
	}

	// 2013.06.25 yamagishi modified. end
	// 2013.06.25 yamagishi add. start
	/**
	 * @return aclMap
	 */
	public HashMap<String, String> getAclMap(Connection conn) throws Exception {
		// ���[�U�[�����A�N�Z�X���x���̃Z�b�g���擾
		aclMap = resetUserAcl(UserGrpAclRelationDB.getAclList(id, conn));
		return aclMap;
	}
	// 2013.06.25 yamagishi add. end

	/**
	 * @return
	 */
	public String getMaxAclValue() {
		return maxAclValue;
	}

	/**
	 * @return
	 */
	public boolean isReqImport() {
		return reqImport;
	}

	/**
	 * @return
	 */
	public boolean isReqPrint() {
		return reqPrint;
	}

	/**
	 * @return
	 */
	public boolean isReqOther() {
		return reqOther;
	}

	/**
	 * @return
	 */
	public boolean isAclDisplay() {
		return aclDisplay;
	}

	/**
	 * @return
	 */
	public boolean isAclEmpnoDisplay() {
		return aclEmpnoDisplay;
	}

	/**
	 * @return
	 */
	public boolean isAclNameDisplay() {
		return aclNameDisplay;
	}

	/**
	 * @return
	 */
	public boolean isAclUpdateDisplay() {
		return aclUpdateDisplay;
	}

	/**
	 * @return
	 */
	public boolean isAttach01Display() {
		return attach01Display;
	}

	/**
	 * @return
	 */
	public boolean isAttach02Display() {
		return attach02Display;
	}

	/**
	 * @return
	 */
	public boolean isAttach03Display() {
		return attach03Display;
	}

	/**
	 * @return
	 */
	public boolean isAttach04Display() {
		return attach04Display;
	}

	/**
	 * @return
	 */
	public boolean isAttach05Display() {
		return attach05Display;
	}

	/**
	 * @return
	 */
	public boolean isAttach06Display() {
		return attach06Display;
	}

	/**
	 * @return
	 */
	public boolean isAttach07Display() {
		return attach07Display;
	}

	/**
	 * @return
	 */
	public boolean isAttach08Display() {
		return attach08Display;
	}

	/**
	 * @return
	 */
	public boolean isAttach09Display() {
		return attach09Display;
	}

	/**
	 * @return
	 */
	public boolean isAttach10Display() {
		return attach10Display;
	}

	/**
	 * @return
	 */
	public boolean isAttachMaxDisplay() {
		return attachMaxDisplay;
	}

	/**
	 * @return
	 */
	public boolean isCadTypeDisplay() {
		return cadTypeDisplay;
	}

	/**
	 * @return
	 */
	public boolean isCreateDateDisplay() {
		return createDateDisplay;
	}

	/**
	 * @return
	 */
	public boolean isCreateDivDisplay() {
		return createDivDisplay;
	}

	/**
	 * @return
	 */
	public boolean isCreateUserDisplay() {
		return createUserDisplay;
	}

	/**
	 * @return
	 */
	public boolean isDrwgNoDisplay() {
		return drwgNoDisplay;
	}

	/**
	 * @return
	 */
	public boolean isDrwgSizeDisplay() {
		return drwgSizeDisplay;
	}

	/**
	 * @return
	 */
	public boolean isDrwgTypeDisplay() {
		return drwgTypeDisplay;
	}

	/**
	 * @return
	 */
	public boolean isEngineerDisplay() {
		return engineerDisplay;
	}

	/**
	 * @return
	 */
	public boolean isEucStamp() {
		return eucStamp;
	}

	/**
	 * @return
	 */
	public boolean isIssueDisplay() {
		return issueDisplay;
	}

	/**
	 * @return
	 */
	public boolean isLatestDisplay() {
		return latestDisplay;
	}

	/**
	 * @return
	 */
	public boolean isMachineEnDisplay() {
		return machineEnDisplay;
	}

	/**
	 * @return
	 */
	public boolean isMachineJpDisplay() {
		return machineJpDisplay;
	}

	/**
	 * @return
	 */
	public boolean isMachineNameDisplay() {
		return machineNameDisplay;
	}

	/**
	 * @return
	 */
	public boolean isMachineNoDisplay() {
		return machineNoDisplay;
	}

	/**
	 * @return
	 */
	public boolean isMachineSpec1Display() {
		return machineSpec1Display;
	}

	/**
	 * @return
	 */
	public boolean isMachineSpec2Display() {
		return machineSpec2Display;
	}

	/**
	 * @return
	 */
	public boolean isMachineSpec3Display() {
		return machineSpec3Display;
	}

	/**
	 * @return
	 */
	public boolean isMachineSpec4Display() {
		return machineSpec4Display;
	}

	/**
	 * @return
	 */
	public boolean isMachineSpec5Display() {
		return machineSpec5Display;
	}

	/**
	 * @return
	 */
	public boolean isMaterialDisplay() {
		return materialDisplay;
	}

	/**
	 * @return
	 */
	public boolean isMediaIdDisplay() {
		return mediaIdDisplay;
	}

	/**
	 * @return
	 */
	public boolean isPagesDisplay() {
		return pagesDisplay;
	}

	/**
	 * @return
	 */
	public boolean isPltrStamp() {
		return pltrStamp;
	}

	/**
	 * @return
	 */
	public boolean isProcurementDisplay() {
		return procurementDisplay;
	}

	/**
	 * @return
	 */
	public boolean isProhibitDateDisplay() {
		return prohibitDateDisplay;
	}

	/**
	 * @return
	 */
	public boolean isProhibitDisplay() {
		return prohibitDisplay;
	}

	/**
	 * @return
	 */
	public boolean isProhibitEmpnoDisplay() {
		return prohibitEmpnoDisplay;
	}

	/**
	 * @return
	 */
	public boolean isProhibitNameDisplay() {
		return prohibitNameDisplay;
	}

	/**
	 * @return
	 */
	public boolean isReplaceDisplay() {
		return replaceDisplay;
	}

	/**
	 * @return
	 */
	public boolean isReqCheckout() {
		return reqCheckout;
	}

	/**
	 * @return
	 */
	public boolean isSupplyDisplay() {
		return supplyDisplay;
	}

	/**
	 * @return
	 */
	public boolean isSupplyerEnDisplay() {
		return supplyerEnDisplay;
	}

	/**
	 * @return
	 */
	public boolean isTreatmentDisplay() {
		return treatmentDisplay;
	}

	/**
	 * @return
	 */
	public boolean isUsedForDisplay() {
		return usedForDisplay;
	}

	/**
	 * @return
	 */
	public boolean isSupplyerJpDisplay() {
		return supplyerJpDisplay;
	}

	/**
	 * @return
	 */
	public boolean isSupplyerTypeDisplay() {
		return supplyerTypeDisplay;
	}

	/**
	 *
	 * @return
	 */
	public boolean isAllEmptyViewSelCol() {
		boolean ret = true;
		for (int i = 0; i < viewSelColList.size(); i++) {
			ret = ret & StringUtils.isEmpty(viewSelColList.get(i));
			if (!ret) {
				break;
			}
		}
		return ret;
	}

	/**
	 * @return
	 */
	public String getViewSelCol(int idx) {
		return viewSelColList.get(idx);
	}

	/**
	 * @return
	 */
	public ArrayList<String> getViewSelColList() {
		return viewSelColList;
	}

	/**
	 * @param string
	 */
	public void setViewSelCol(int idx, String string) {
		String val = StringUtils.isEmpty(string) ? "" : string;
		viewSelColList.add(idx, val);
	}

	/**
	 * @param string
	 */
	public void setViewSelColList(ArrayList<String> list) {
		viewSelColList = list;
	}

	// 2013.09.13 yamagishi add. start
	/**
	 * aclUpdateFlag���擾���܂��B
	 * @return aclUpdateFlag
	 */
	public String getAclUpdateFlag() {
		return aclUpdateFlag;
	}

	/**
	 * aclBatchUpdateFlag���擾���܂��B
	 * @return aclBatchUpdateFlag
	 */
	public String getAclBatchUpdateFlag() {
		return aclBatchUpdateFlag;
	}

	/**
	 * dlManagerFlag���擾���܂��B
	 * @return dlManagerFlag
	 */
	public String getDlManagerFlag() {
		return dlManagerFlag;
	}

	/**
	 * DL�}�l�[�W�������p�\�� true/false�ŕԂ��B
	 */
	public boolean isDLManagerAvailable() {
		return ("1".equals(dlManagerFlag) || "2".equals(dlManagerFlag)) == true;
	}

	/**
	 * DL�}�l�[�W���̕ۑ��{�^�������p�\���Ԃ��B
	 *  1:�ۑ��\�A0:�ۑ��s��
	 */
	public String getDLManagerSaveEnabledFlag() {
		return "2".equals(dlManagerFlag) ? "1" : "0";
	}

	// 2013.09.13 yamagishi add. end
	// 2019.09.20 yamamoto add. start
	/**
	 * passwdUpdDate���擾���܂��B
	 * @return passwdUpdDate
	 */
	public Date getPasswdUpdDate() {
		return passwdUpdDate;
	}

	/**
	 * ���}�Ƀ��[�U�� true/false�ŕԂ��B
	 */
	public boolean isReproUser() {
		return "1".equals(reproUserFlag) == true;
	}

	/**
	 * �}�ʓo�^�˗��\�� true/false�ŕԂ��B
	 */
	public boolean isdwgRegReqFlag() {
		return "1".equals(dwgRegReqFlag) == true;
	}

	/**
	 * �A�N�Z�X���x���ꊇ�X�V��ƃc�[�������p�\�� true/false�ŕԂ��B
	 *  @return NULL / 0�Ffalse <br/> 1�Ftrue
	 */
	public boolean isAclBatchUpdateFlag() {

		if (StringUtils.isEmpty(aclBatchUpdateFlag)) {
			return false;
		}
		return "1".equals(aclBatchUpdateFlag) == true;
	}

	// 2019.09.20 yamamoto add. end

	// 2013.07.24 yamagishi add. start
	/**
	 * aclUpdateFlag��ݒ肵�܂��B
	 * @param aclUpdateFlag aclUpdateFlag
	 */
	public void setAclUpdateFlag(String string) {
		aclUpdateFlag = string;
		if (aclUpdateFlag == null) {
			aclUpdateFlag = "";
		}
	}

	/**
	 * aclBatchUpdateFlag��ݒ肵�܂��B
	 * @param aclBatchUpdateFlag aclBatchUpdateFlag
	 */
	public void setAclBatchUpdateFlag(String string) {
		aclBatchUpdateFlag = string;
		if (aclBatchUpdateFlag == null) {
			aclBatchUpdateFlag = "";
		}
	}

	/**
	 * dlManagerFlag��ݒ肵�܂��B
	 * @param dlManagerFlag dlManagerFlag
	 */
	public void setDlManagerFlag(String string) {
		dlManagerFlag = string;
		if (dlManagerFlag == null) {
			dlManagerFlag = "";
		}
	}

	// 2013.07.24 yamagishi add. end
	// 2019.09.20 yamamoto add. start
	/**
	 * passwdUpdDate��ݒ肵�܂��B
	 * @param string
	 */
	public void setPasswdUpdDate(Date date) {
		passwdUpdDate = date;
		//		if (passwdUpdDate == null) passwdUpdDate = "";
	}

	/**
	 * reproUserFlag��ݒ肵�܂��B
	 * @param string
	 */
	public void setReproUserFlag(String string) {
		reproUserFlag = string;
		if (reproUserFlag == null) {
			reproUserFlag = "";
		}
	}

	/**
	 * dwgRegReqFlag��ݒ肵�܂��B
	 * @param string
	 */
	public void setDwgRegReqFlag(String string) {
		dwgRegReqFlag = string;
		if (dwgRegReqFlag == null) {
			dwgRegReqFlag = "";
		}
	}

	// 2019.09.20 yamamoto add end.
	// 2020.02.10 yamamoto add start.
	/**
	 * multiPdfFlag��ݒ肵�܂��B
	 * @param string
	 */
	public void setMultiPdfFlag(String string) {
		multiPdfFlag = string;
	}

	/**
	 * �}���`PDF�o�͂����p�\�� true/false�ŕԂ��B
	 * @param string
	 */
	public boolean isMultiPdf() {
		return "1".equals(multiPdfFlag) == true;
	}
	// 2020.02.10 yamamoto add end.

	/**
	 * @return
	 */
	public ArrayList<Printer> getEnablePrinters() {
		return enablePrinters;
	}

	/**
	 * @return
	 */
	public boolean isViewStamp() {
		return viewStamp;
	}

	/**
	 * @return
	 */
	public boolean isAdmin() {
		return admin;
	}

	/**
	 * @return
	 */
	public boolean isDelAdmin() {
		return delAdmin;
	}

	/**
	 * �Ǘ��҃t���O���Z�b�g����B1�Ȃ�Ǘ��҂Ƃ���B
	 * @param b
	 */
	public void setAdminFlag(String newAdminFlag) {
		admin = "1".equals(newAdminFlag);

		if ("2".equals(newAdminFlag)) {
			admin = true;
			delAdmin = true;
		}
	}

	public boolean isOnlyNewest() {
		return onlyNewest;
	}

	public void setOnlyNewestFlag(String onlyNewestFlag) {
		onlyNewest = "1".equals(onlyNewestFlag);
	}

	/**
	 * �E�ʂ�Ԃ��Bint�ŕԂ��B
	 * @return
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * �E�ʂ��Z�b�g����B���̂Ƃ�char����int�ɕϊ�����B
	 * �ϊ��ł��Ȃ��ꍇ�́A0���Z�b�g����B
	 * @param i
	 */
	public void setPosition(String newPosition) {
		try {
			position = Integer.parseInt(newPosition.trim());
		} catch (Exception e) {
			position = 0;
		}
	}

	/**
	 * @return
	 */
	public String getDeptName() {
		return deptName;
	}

	/**
	 * @param string
	 */
	public void setDeptName(String string) {
		deptName = string;
		if (deptName == null) {
			deptName = "";
		}
	}

	public String getLanguage() {
		return language;
	}

	public String getLanKey() {
		if ("Japanese".equals(language)) {
			return "jp";
		}
		if ("English".equals(language)) {
			return "en";
		}
		return "jp";
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getSys_id() {
		return sys_id;
	}

	public void setSys_id(String sysId) {
		sys_id = sysId;
	}

	public boolean isTwinDrwgNoDisplay() {
		return twinDrwgNoDisplay;
	}

	// 2013.06.25 yamagishi modified. start
	//	/**
	//	 * @return viewPrintDoc
	//	 */
	//	public String getViewPrintDoc(String aclId) {
	//		String aclValue = this.getAclMap().get(aclId);
	//		if (aclValue == null || aclValue.equals("1")) {
	//			return "noprintPdf";
	//		} else {
	//			if (viewPrintDoc.get(aclId).equals("1")) {
	//				return "tiff";
	//			} else if (viewPrintDoc.get(aclId).equals("2")) {
	//				return "printablePdf";
	//			} else {
	//				return null;
	//			}
	//		}
	//	}
	/**
	 * @param aclId
	 * @param conn
	 * @return viewPrintDoc
	 */
	public String getViewPrintDoc(String aclId, Connection conn) throws Exception {
		// DB����擾
		return this.getViewPrintDoc(aclId, conn, false);
	}

	/**
	 * @param aclId
	 * @param conn
	 * @param refSession �f�t�H���g��false
	 * 			true: �Z�b�V��������L���b�V�����擾, false: DB����擾
	 * @return viewPrintDoc
	 */
	public String getViewPrintDoc(String aclId, Connection conn, boolean refSession) throws Exception {
		// �A�N�Z�X���x���擾
		HashMap<String, String> aclMap = refSession ? this.getAclMap() : this.getAclMap(conn);
		String aclValue = aclMap.get(aclId);
		if (aclValue == null || "1".equals(aclValue)) {
			return "noprintPdf";
		}
		if ("1".equals(viewPrintDoc.get(aclId))) {
			return "tiff";
		}
		if ("2".equals(viewPrintDoc.get(aclId))) {
			return "printablePdf";
		}
		return null;
	}
	// 2013.06.25 yamagishi modified. end

	/**
	 * @return viewPrintDoc
	 */
	public HashMap<String, String> getViewPrintDoc() {
		return viewPrintDoc;
	}

	/**
	 * @param viewPrintDoc �Z�b�g���� viewPrintDoc
	 */
	public void setViewPrintDoc(HashMap<String, String> viewPrintDoc) {
		this.viewPrintDoc = viewPrintDoc;
	}

	// 2013.06.25 yamagishi add. start
	/**
	 * ���p�҃O���[�v�̃A�N�Z�X���x�����A���̃��[�U�[�Ɉڂ��B
	 * ���̂Ƃ��L���Ȃ�悤�Ɉڂ��i���Ď擾�p�AACL�̍ŐV�𔽉f����j
	 * @param newAclList
	 * @throws Exception
	 */
	private HashMap<String, String> resetUserAcl(ArrayList<UserGrpAclRelation> newAclList) {

		viewPrintDoc.clear(); // �ݒ�ς݉{���t�H�[�}�b�g���N���A
		aclMap.clear(); // �ݒ�ς݃A�N�Z�X���x�����N���A

		String aclId = null; // ���[�U�[�O���[�v�̃A�N�Z�X���x���̃L�[
		String grpAclValue = null; // ���̃O���[�v�̃A�N�Z�X���x���ݒ�l
		UserGroup userGroup = null;

		// ���p�҃O���[�v�̃A�N�Z�X���x�����A���̃��[�U�[�Ɉڂ��B
		// ���̂Ƃ��L���Ȃ�悤�Ɉڂ�
		for (UserGrpAclRelation userGrpAcl : newAclList) {
			aclId = userGrpAcl.getAclId();
			grpAclValue = userGrpAcl.getAclValue();
			for (UserGroup element : userGroups) {
				if (element.cd.equals(userGrpAcl.getUserGrpCode())) {
					userGroup = element;
					break;
				}
			}

			if (aclMap.containsKey(aclId)) {
				// ���łɓ����A�N�Z�X���x�����ݒ肳��Ă�����
				String uesrAclValue = aclMap.get(aclId);// ���[�U�[�̐ݒ�l
				if (grpAclValue.compareTo(uesrAclValue) == 0) {
					// �O���[�v�ɐݒ肳��Ă���ݒ�l�������̏ꍇ�A"TIFF"��D��
					if ("1".equals(userGroup.viewPrintDoc)) {
						viewPrintDoc.put(aclId, userGroup.viewPrintDoc);
					}
				}
				if (grpAclValue.compareTo(uesrAclValue) > 0) {
					// �O���[�v�ɐݒ肳��Ă���ݒ�l���傫����΁A���[�U�[�ɃZ�b�g������
					aclMap.put(aclId, grpAclValue);
					viewPrintDoc.put(aclId, userGroup.viewPrintDoc);
				}
			} else {
				// �܂����̃A�N�Z�X���x�������ݒ�Ȃ�
				aclMap.put(aclId, grpAclValue);
				viewPrintDoc.put(aclId, userGroup.viewPrintDoc);
			}
			// ���̃��[�U�[�����ō��̃A�N�Z�X���x�����Z�b�g
			if (grpAclValue.compareTo(maxAclValue) > 0) {
				maxAclValue = grpAclValue;
			}
		}
		return aclMap;
	}
	// 2013.06.25 yamagishi add. end

}
