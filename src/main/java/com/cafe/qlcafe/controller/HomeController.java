package com.cafe.qlcafe.controller;

import com.cafe.qlcafe.dao.*;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {

    @Autowired private ThongKeDAO thongKeDAO;
    @Autowired private BanDAO banDAO;
    @Autowired private NguyenLieuDAO nguyenLieuDAO;
    @Autowired private HoaDonDAO hoaDonDAO;

    @GetMapping("/")
    public String index(@RequestParam(required = false) String error, Model model) {
        List<Document> dsBan = banDAO.layTatCa();
        long banCoKhach = dsBan.stream().filter(b -> "Có khách".equals(b.getString("Trangthai"))).count();
        List<Document> nlSapHet = nguyenLieuDAO.layNguyenLieuSapHet();

        model.addAttribute("tongDoanhThu", thongKeDAO.tinhTongDoanhThu());
        model.addAttribute("tongHoaDon", thongKeDAO.demTongHoaDon());
        model.addAttribute("banCoKhach", banCoKhach);
        model.addAttribute("tongBan", dsBan.size());
        model.addAttribute("nlSapHet", nlSapHet);
        model.addAttribute("hoaDonGanDay", hoaDonDAO.layMoiNhat(8));

        if ("no_permission".equals(error)) {
            model.addAttribute("error", "Tài khoản của bạn không có quyền truy cập chức năng vừa chọn!");
        }
        return "index";
    }
}
