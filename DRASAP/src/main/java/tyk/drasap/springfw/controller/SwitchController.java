package tyk.drasap.springfw.controller;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import tyk.drasap.common.DrasapPropertiesFactory;

@Controller
public class SwitchController {
	private static final Logger category = Logger.getLogger(SwitchController.class.getName());

	public SwitchController() {
		System.out.println("SwitchController start ......");
		category.debug("start");
		category.debug("end");
	}

	@GetMapping("/login")
	public String accessLogin(HttpServletRequest request, Model errors) {
		category.debug("accessLogin called");
		HttpSession session = request.getSession();
		// セッション破棄
		session.invalidate();
		// ログイン画面へ遷移
		return "/root/login";
	}

	@GetMapping("/timeout")
	public String accessTimeout(Model errors) {
		category.debug("accessTimeout called");
		return "/root/timeout";
	}

	@GetMapping("/getip")
	public String accessGetIp(
			HttpServletRequest request,
			HttpServletResponse response,
			Model errors)
			throws Exception {
		category.debug("accessGetIp called");

		try {
			//処理
			String remoteAddr = request.getRemoteAddr();
			//String  buf = "クライアントIP："+ remoteAddr;
			//category.debug(buf);

			//ヘッダ設定
			response.setContentType("application;charset=UTF-8"); // UTF-8

			//pwオブジェクト
			PrintWriter pw = response.getWriter();

			//出力
			pw.print(remoteAddr);

			//クローズ
			pw.close();
		} catch (Exception e) {
			category.debug(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@GetMapping("/Logout")
	public String accessLogout(
			HttpServletRequest request,
			Model errors)
			throws Exception {
		category.debug("accessLogout called");
		HttpSession session = request.getSession();
		// セッション破棄
		session.invalidate();

		// ログイン画面に遷移
		return "/root/login";
	}

	@GetMapping("/systemMaintenanceLogin")
	public String accessSystemMaintenanceLogin(Model errors) {
		category.debug("accessSystemMaintenanceLogin called");
		String sysMainLogin = DrasapPropertiesFactory.getDrasapProperties(this).getProperty("system.maintenance.login");
		if ("true".equals(sysMainLogin)) {
			// システムメンテログイン画面に遷移
			return "/system/systemMaintenanceLogin";
		}
		// notfound画面に遷移
		return "/root/notfound";
	}

	@GetMapping("/{path}")
	public Object accessPath(@PathVariable("path") String path, Model errors) {
		category.debug("accessPath called with path: " + path);
		return path;
	}
}
