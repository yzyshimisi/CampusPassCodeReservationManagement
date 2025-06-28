package org.example.backend.filter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.utils.ConnUtils;
import org.example.backend.utils.Jwt;

import java.io.IOException;

@WebFilter("/api/*")
public class JwtAuthFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;

        String jwtToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwtToken".equals(cookie.getName())) {
                    jwtToken = cookie.getValue();
                    break;
                }
            }
        }

        Claims claims = new Jwt().validateJwt("jwtToken=" + jwtToken);
        if (claims != null) {
            Integer currentAdminId = Integer.valueOf((String) claims.get("id"));
            ConnUtils.bindOperator(currentAdminId);
            request.setAttribute("currentAdminId", currentAdminId);
            System.out.println("[JWT Filter] 当前操作者 ID = " + currentAdminId);
        }

        try {
            chain.doFilter(request, res);
        } finally {
            ConnUtils.clearOperator();
        }
    }
}