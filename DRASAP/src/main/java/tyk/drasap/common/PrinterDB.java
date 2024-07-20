package tyk.drasap.common;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * �v�����^�[���X�g(PRINTER_LIST)�ƑΉ������N���X�B
 */
public class PrinterDB {
	/**
	 * �v�����^ID���i�[����printerIdArray�����ɂ��āA
	 * Printer�I�u�W�F�N�g��ArrayList�Ɋi�[���ĕԂ��B
	 * @param printerIdArray�v�����^ID���i�[
	 * @param conn
	 * @return Printer�I�u�W�F�N�g���i�[
	 * @throws Exception
	 */
	public static ArrayList<Printer> getPrinterArray(ArrayList<String> printerIdArray, Connection conn)
			throws Exception {
		ArrayList<Printer> printers = new ArrayList<Printer>();
		// �`�F�b�N
		if (printerIdArray.size() == 0) {
			return printers;
		}
		//
		Statement stmt1 = null;
		ResultSet rs1 = null;
		try {
			// �v�����^ID�Ō�������
			StringBuilder sbSql1 = new StringBuilder("select * from PRINTER_LIST");
			sbSql1.append(" where PRINTER_ID in (");
			for (int i = 0; i < printerIdArray.size(); i++) {
				sbSql1.append("'");
				sbSql1.append(printerIdArray.get(i));
				sbSql1.append("',");
			}
			// �Ō�̃J���}���]���Ȃ̂ō폜
			sbSql1.deleteCharAt(sbSql1.length() - 1);
			sbSql1.append(")");
			sbSql1.append(" order by PRINTER_ID");
			//
			stmt1 = conn.createStatement();
			rs1 = stmt1.executeQuery(sbSql1.toString());
			while (rs1.next()) {
				printers.add(new Printer(rs1.getString("PRINTER_ID"), rs1.getString("PRINTER_NAME"),
						rs1.getString("DISPLAY_NAME"), rs1.getString("DISPLAY_FLAG"),
						rs1.getString("MAX_SIZE"),
						rs1.getString("A0_OUT"), rs1.getString("A1_OUT"), rs1.getString("A2_OUT"), rs1.getString("A3_OUT"),
						rs1.getString("A4_OUT"), rs1.getString("A0L_OUT"), rs1.getString("A1L_OUT"), rs1.getString("A2L_OUT"),
						rs1.getString("A3L_OUT"),
						rs1.getString("CLASSIFY")));
			}

		} catch (Exception e) {
			throw e;

		} finally {
			// CLOSE����
			try {
				rs1.close();
			} catch (Exception e) {
			}
			try {
				stmt1.close();
			} catch (Exception e) {
			}
		}
		//
		return printers;
	}

}
