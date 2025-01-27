package tyk.drasap.genzu_irai;

/**
 * @author KAWAI
 *
 * この生成されたコメントの挿入されるテンプレートを変更するため
 * ウィンドウ > 設定 > Java > コード生成 > コードとコメント
 */
public class RequestElement {

	String id;//プリンタID
	String name;//プリンタ名

	public RequestElement(String newId, String newName) {
		id = newId;
		name = newName;
	}

	/**
	 * Returns the id.
	 * @return String
	 */
	public String getId() {
		return id;
	}

	/**
	 * Returns the name.
	 * @return String
	 */
	public String getName() {
		return name;
	}

}
