package com.cafe.qlcafe.controller;

import com.cafe.qlcafe.dao.NhanVienDAO;
import jakarta.servlet.http.HttpSession;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @Autowired private NhanVienDAO nhanVienDAO;

    @GetMapping("/login")
    public String showLogin(HttpSession session) {
        if (session.getAttribute("user") != null) return "redirect:/";
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String tendn, @RequestParam String matkhau, HttpSession session, Model model) {
        Document nv = nhanVienDAO.dangNhap(tendn.trim(), matkhau);
        if (nv != null) {
            session.setAttribute("user", nv);
            return "redirect:/";
        }
        model.addAttribute("error", "Sai tên đăng nhập, mật khẩu hoặc tài khoản đã bị khóa!");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
