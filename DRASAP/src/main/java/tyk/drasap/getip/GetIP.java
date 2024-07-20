package tyk.drasap.getip;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Category;

import tyk.drasap.root.LoginAction;

@WebServlet(urlPatterns={"/getip"})
public class GetIP extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static Category category = Category.getInstance(LoginAction.class.getName());

    @Override
    public void doGet (HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
    	
    	//category.debug("GetIP処理");
  
    	try {

    			//処理
    			String response1;
    			response1=req.getRemoteAddr();
    			//String  buf = "クライアントIP："+ response1;
    			//category.debug(buf);
    			//buf="";
    			
    			//ヘッダ設定
    			res.setContentType("application;charset=UTF-8");   // UTF-8

    			//pwオブジェクト
    			PrintWriter pw = res.getWriter();

    			//出力
    			pw.print(response1);

    			//クローズ
    			pw.close();
    	}
    	catch(Exception e)
    	{
    		category.debug(e.getMessage());
    		e.printStackTrace();
    	}
    }

}
