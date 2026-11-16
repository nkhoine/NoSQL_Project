package com.cafe.qlcafe.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.bson.Document;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        Document user = (session != null) ? (Document) session.getAttribute("user") : null;

        if (user == null) {
            response.sendRedirect("/login");
            return false;
        }

        Document tk = (Document) user.get("Taikhoan");
        String quyen = (tk != null) ? tk.getString("Quyen") : "";
        String uri = request.getRequestURI();

        if ("Quản lý".equals(quyen)) return true;

        if ("Thu ngân".equals(quyen)) {
            if (uri.startsWith("/nhanvien") || uri.startsWith("/saoluu")) {
                response.sendRedirect("/?error=no_permission");
                return false;
            }
            return true;
        }

        if ("Pha chế".equals(quyen)) {
            if (uri.equals("/") || uri.startsWith("/mon") || uri.startsWith("/danhmuc")
                    || uri.startsWith("/nguyenlieu") || uri.startsWith("/phieunhap") || uri.startsWith("/logout")) {
                return true;
            }
            response.sendRedirect("/?error=no_permission");
            return false;
        }

        return true;
    }
}
