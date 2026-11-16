package com.cafe.qlcafe.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.bson.Document;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAdvice {

    @ModelAttribute
    public void addCommonAttributes(HttpServletRequest request, HttpSession session, Model model) {
        Document user = (Document) session.getAttribute("user");
        model.addAttribute("currentUser", user);
        model.addAttribute("currentUri", request.getRequestURI());
    }
}
