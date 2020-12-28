package com.abchina.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @Auther: http://www.bjsxt.com
 * @Date: 2020/12/26
 * @Description: com.abchina.servlet
 * @Version: 1.0
 */
public class OtherServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        String user = request.getParameter("user");
        String username = "";
        // 从session中读取数据时一般用getSession(false)，表示如果有session就返回session，如果没有救返回Null
        HttpSession session = request.getSession(false);
        if (null != session){
            username = (String)session.getAttribute("username");
        }

        PrintWriter out = response.getWriter();
        out.println("OtherServlet user="+user);
        out.println("OtherServlet username="+username);
        out.println("OtherServlet session="+session);

    }
}
