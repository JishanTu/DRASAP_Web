package tyk.drasap.root;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class Logout extends HttpServlet {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public Logout() {
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException{

		HttpSession session = request.getSession(true);
		// セッション破棄
		session.invalidate();

		// ログイン画面に遷移
		response.sendRedirect(request.getContextPath() + "/root/login.jsp");
	}
}
