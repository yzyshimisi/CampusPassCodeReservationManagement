package org.example.backend.control;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.annotation.WebServlet;
import org.example.backend.dao.DepartmentDao;

@WebServlet("/test")
public class TestServlet extends HttpServlet {
    private final DepartmentDao deptDao = new DepartmentDao();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println(deptDao.exists(1));
        out.println("222get");

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");

        // 读取 JSON 数据
        BufferedReader reader = request.getReader();
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        String requestBody = sb.toString();

        // 把 JSON 字符串直接返回（你也可以解析成 Java 对象）
        String json = "{ \"code\": 200, \"msg\": \"收到前端 JSON\", \"you_sent\": " + requestBody + " }";
        response.getWriter().print(json);
        response.getWriter().print("111");
    }


}

