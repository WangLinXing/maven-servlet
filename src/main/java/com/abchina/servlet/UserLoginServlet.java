package com.abchina.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @Auther: wangyq
 * @Date: 2020/12/26
 * @Description: com.abchina.servlet
 * @Version: 1.0
 */
public class UserLoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");

        // 参数放入request　域
        //request.setAttribute("user",username);



        /*
        获取当前请求的session对象，如果木有则创建一个新的session对象
        request.getSession(true) 相当于 request.getSession()
         */
        HttpSession session = request.getSession(true);

        // 使session失效
        //session.invalidate();

        session.setAttribute("username",username);

        PrintWriter out = response.getWriter();
        out.println("SomeServlet username="+username);
        out.println("SomeServlet  session:"+session);
    }
}
