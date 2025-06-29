package org.example.backend.filter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.utils.ConnUtils;
import org.example.backend.utils.Jwt;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

@WebFilter("/api/*")
public class JwtAuthFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        res.setContentType("application/json;charset=UTF-8");
        HttpServletRequest request = (HttpServletRequest) req;
        String requestURI = request.getRequestURI();

        PrintWriter out = res.getWriter();

        List<String> excludedPaths = Arrays.asList("/api/appointment/add", "/api/appointment/check", "/api/appointment/passCode", "/api/login");

        if (excludedPaths.stream().anyMatch(requestURI::startsWith)) {
            chain.doFilter(req, res);
            return;
        }

        try {
            String jwtToken = request.getHeader("Authorization");
            if(jwtToken == null || jwtToken.isEmpty()) {
                throw new Exception("{ \"code\": 401, \"msg\": \"用户未授权\", \"data\": { } }");
            }

            Claims claims = new Jwt().validateJwt(jwtToken);
            if (claims == null) {
                throw new Exception("{ \"code\": 401, \"msg\": \"Token 过期或出错\", \"data\": { } }");
            }

            try {
                req.setAttribute("claims", claims);
                chain.doFilter(req, res);
            } finally {
                ConnUtils.clearOperator();
            }
        }catch (Exception e){
            out.write(e.getMessage());
        }
    }
}