package tyk.drasap.search;

import java.util.HashMap;

/**
 * �����Ɋւ��Ă̏���ێ�����N���X�B
 */
public class SearchInfo {
	static HashMap<String, String> attrMap = new HashMap<String, String>();
	static{
		attrMap.put("CREATE_DATE", "�쐬��");
		attrMap.put("CREATE_USER", "�쐬��");
		attrMap.put("MACHINE_JP", "���u����(�a)");
		attrMap.put("MACHINE_EN", "���u����(�p)");
		attrMap.put("USED_FOR", "�p�r");
		attrMap.put("MATERIAL", "�ގ�");
		attrMap.put("TREATMENT", "�M�E�\�ʏ���");
		attrMap.put("PROCUREMENT", "���B�敪");
		attrMap.put("SUPPLYER_JP", "���[�J�[��(�a)");
		attrMap.put("SUPPLYER_EN", "���[�J�[��(�p)");
		attrMap.put("SUPPLYER_TYPE", "���[�J�[�`��");
		attrMap.put("ATTACH01", "�Y�t�}��1");
		attrMap.put("ATTACH02", "�Y�t�}��2");
		attrMap.put("ATTACH03", "�Y�t�}��3");
		attrMap.put("ATTACH04", "�Y�t�}��4");
		attrMap.put("ATTACH05", "�Y�t�}��5");
		attrMap.put("ATTACH06", "�Y�t�}��6");
		attrMap.put("ATTACH07", "�Y�t�}��7");
		attrMap.put("ATTACH08", "�Y�t�}��8");
		attrMap.put("ATTACH09", "�Y�t�}��9");
		attrMap.put("ATTACH10", "�Y�t�}��10");
		attrMap.put("MACHINE_NO", "���uNO");
		attrMap.put("MACHINE_NAME", "�@�햼��");
		attrMap.put("MACHINE_SPEC1", "���u�d�l1");
		attrMap.put("MACHINE_SPEC2", "���u�d�l2");
		attrMap.put("MACHINE_SPEC3", "���u�d�l3");
		attrMap.put("MACHINE_SPEC4", "���u�d�l4");
		attrMap.put("MACHINE_SPEC5", "���u�d�l5");
		attrMap.put("DRWG_TYPE", "�}�ʎ��");
		attrMap.put("DRWG_SIZE", "�}�ʃT�C�Y");
		attrMap.put("ISSUE", "��o�敪");
		attrMap.put("SUPPLY", "���Ջ敪");
		attrMap.put("CAD_TYPE", "CAD���");
		attrMap.put("ENGINEER", "�݌v�Җ�");
		attrMap.put("PROHIBIT", "�g�p�֎~�敪");
		attrMap.put("PROHIBIT_DATE", "�g�p�֎~����");
		attrMap.put("PROHIBIT_EMPNO", "�g�p�֎~�ҐE��");
		attrMap.put("PROHIBIT_NAME", "�g�p�֎~�Җ��O");
		attrMap.put("PAGES", "�y�[�W��");
		attrMap.put("ACL_ID", "�A�N�Z�X���x��");
		attrMap.put("ACL_UPDATE", "�������ٕύX��");
		attrMap.put("ACL_EMPNO", "�������ٕύX�ҐE��");
		attrMap.put("ACL_NAME", "�������ٕύX�Җ�");
		attrMap.put("ATTACH_MAX", "�Y�t�}��");
		attrMap.put("LATEST_FLAG", "�ŐV�}�ԋ敪");
		attrMap.put("REPLACE_FLAG", "���փt���O");
		attrMap.put("CREATE_DIV", "�쐬�����R�[�h");
		attrMap.put("MEDIA_ID", "���f�B�AID");
		attrMap.put("TWIN_DRWG_NO", "1��2�i�Ԑ}��");
		
	}

	/**
	 * @return
	 */
	public static HashMap<String, String> getAttrMap() {
		return attrMap;
	}

}
