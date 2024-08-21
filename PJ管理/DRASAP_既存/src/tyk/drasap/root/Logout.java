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
		// TODO �����������ꂽ�R���X�g���N�^�[�E�X�^�u
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException{

		HttpSession session = request.getSession(true);
		// �Z�b�V�����j��
		session.invalidate();

		// ���O�C����ʂɑJ��
		response.sendRedirect(request.getContextPath() + "/root/login.jsp");
	}
}
