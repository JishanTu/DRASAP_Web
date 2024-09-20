package tyk.drasap.search;

import java.util.ArrayList;

import tyk.drasap.common.StringCheck;

/**
 * ����SQL��Order�������쐬����B
 */
public class SearchOrderSentenceMaker {
	ArrayList<String> sortOrderList;// �\�[�g������ێ�����
	ArrayList<String> sqlList;// SQL����ێ����� ex) DRWG_NO DESC�E�E�E�}�Ԃ̋t��

	// --------------------------------------------------------- �R���X�g���N�^
	/**
	 * �R���X�g���N�^
	 */
	public SearchOrderSentenceMaker() {
		sortOrderList = new ArrayList<String>();
		sqlList = new ArrayList<String>();
	}

	// --------------------------------------------------------- ���\�b�h
	/**
	 * �\�[�g������������
	 * @param columnName �\�[�g���鑮����(ex. DRWG_NO)
	 * @param sortWay �\�[�g�������(ex. ����,�~��)
	 * @param sortOrder �\�[�g���鏇��(1,2,...)
	 */
	public void addOrderCondition(String columnName, String sortWay, String sortOrder) {
		// �u�\�[�g��������v�Ɓu�\�[�g���鑮�����v�͓��͂���Ă���K�v������
		sortWay = StringCheck.trimWsp(sortWay);//�u�\�[�g��������v�͑S�p�X�y�[�X�̂��߁Atrim����
		if (sortWay == null || sortWay.length() == 0 ||
				columnName == null || columnName.length() == 0) {
			return;
		}
		// �\�[�g�����ݒ肳��Ă��Ȃ��Ƃ��́A9�ԂƂ���
		if (sortOrder == null || sortOrder.length() == 0) {
			sortOrder = "99";
		}
		// sqlList�ɒǉ����镔�������O�ɍ쐬���Ă���
		String sqlPart = columnName;
		if ("�~��".equals(sortWay) || "Desc".equals(sortWay)) {
			sqlPart += " DESC";
		}
		// �}���ʒu��T��
		boolean inserted = false;// �}��������true
		for (int i = 0; i < sortOrderList.size(); i++) {
			int sortOrderTemp = Integer.parseInt(sortOrder);
			if (sortOrderTemp < Integer.parseInt(sortOrderList.get(i))) {
				sortOrderList.add(i, sortOrder);// �\�[�g�����̃��X�g�ɒǉ�
				sqlList.add(i, sqlPart);// SQL���ێ��̃��X�g�ɒǉ�
				inserted = true;
				break;
			}
		}
		if (!inserted) {// ���O��for���[�v�ő}���ʒu��������Ȃ�������A�Ō�ɒǉ�
			sortOrderList.add(sortOrder);// �\�[�g�����̃��X�g�ɒǉ�
			sqlList.add(sqlPart);// SQL���ێ��̃��X�g�ɒǉ�
		}

	}

	/**
	 * �\�[�g������S�ĉ��������Ƃ���A�uorder by �E�E�E�v�����o���B
	 * ��) order by DRWG_NO
	 * @return SQL��order��B��) order by DRWG_NO
	 */
	public String getSqlOrder() {
		if (sqlList.size() == 0) {
			// Order�������w�肳��Ă��Ȃ����
			return "order by DRWG_NO";
		}
		// Order���w�肳��Ă����
		StringBuilder sbSql1 = new StringBuilder();
		sbSql1.append("order by ");
		for (int i = 0; i < sqlList.size(); i++) {
			sbSql1.append(sqlList.get(i));
			sbSql1.append(',');
		}
		// �Ō�́u,�v������
		sbSql1.deleteCharAt(sbSql1.length() - 1);
		//
		return sbSql1.toString();
	}

}
