package tyk.drasap.common;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieManage {
	public String getCookie (HttpServletRequest request, User user, String key) {
		String cookieValue = "";
		// クッキーから言語設定を取得
		Cookie[] cookies = request.getCookies();
		if (user != null && user.getId() != null && user.getId().length() > 0) {
			for (int i = 0; i < cookies.length; i ++) {
			    if (cookies[i].getName().equals(user.getId()+key)) cookieValue = cookies[i].getValue();
			}
		} 
		if (cookieValue.length() == 0) {
			if (cookies != null) {
				for (int i = 0; i < cookies.length; i ++) {
				    if (cookies[i].getName().equals(key)) cookieValue = cookies[i].getValue();
				}
			}
		}
		return cookieValue;
	}
	public void setCookie (HttpServletResponse response, User user, String key, String value) {
		if (user == null || user.getId() == null || user.getId().length() == 0) return;
		
		Cookie language = new Cookie(user.getId()+key , value);
		language.setMaxAge(60*60*24*180);
		language.setPath("/DRASAP");
		response.addCookie(language);
		Cookie language2 = new Cookie(key , value);
		language2.setMaxAge(60*60*24*180);
		language2.setPath("/DRASAP");
		response.addCookie(language2);
	}
}
