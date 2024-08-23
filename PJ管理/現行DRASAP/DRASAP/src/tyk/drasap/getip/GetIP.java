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
    	
    	//category.debug("GetIP����");
  
    	try {

    			//����
    			String response1;
    			response1=req.getRemoteAddr();
    			//String  buf = "�N���C�A���gIP�F"+ response1;
    			//category.debug(buf);
    			//buf="";
    			
    			//�w�b�_�ݒ�
    			res.setContentType("application;charset=UTF-8");   // UTF-8

    			//pw�I�u�W�F�N�g
    			PrintWriter pw = res.getWriter();

    			//�o��
    			pw.print(response1);

    			//�N���[�Y
    			pw.close();
    	}
    	catch(Exception e)
    	{
    		category.debug(e.getMessage());
    		e.printStackTrace();
    	}
    }

}
