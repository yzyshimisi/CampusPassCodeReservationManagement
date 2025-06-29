package org.example.backend.filter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;

import java.io.IOException;
import java.io.PrintWriter;

@WebFilter("/api/systemAdmin/*")
public class SystemAdminFilter implements Filter {

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        PrintWriter out = response.getWriter();
        Claims claims = (Claims) request.getAttribute("claims");
        Integer adminRole = claims.get("adminRole", Integer.class);

        if(adminRole == 0){
            chain.doFilter(request, response);
        }else{
            out.write("{ \"code\": 401, \"msg\": \"权限错误\", \"data\": { } }");
        }
    }
}
