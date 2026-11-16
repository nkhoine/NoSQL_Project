package com.cafe.qlcafe.controller;

import com.cafe.qlcafe.dao.ThongKeDAO;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/thongke")
public class ThongKeController {

    @Autowired private ThongKeDAO thongKeDAO;

    @GetMapping
    public String index(@RequestParam(defaultValue = "2026-10") String thang, Model model) {
        List<Document> dtNgay = thongKeDAO.doanhThuTheoNgay(14);
        List<Document> topMon = thongKeDAO.top5MonBanChay();
        List<Document> dtNv = thongKeDAO.doanhThuTheoNhanVien();
        List<Document> dtBan = thongKeDAO.doanhThuTheoBanThang(thang);
        List<Document> tkHinhThuc = thongKeDAO.thongKeHinhThucThanhToan();

        model.addAttribute("thang", thang);
        model.addAttribute("dtNgay", dtNgay);
        model.addAttribute("topMon", topMon);
        model.addAttribute("dtNv", dtNv);
        model.addAttribute("dtBan", dtBan);
        model.addAttribute("tkHinhThuc", tkHinhThuc);

        model.addAttribute("ngayLabels", dtNgay.stream().map(d -> d.getString("_id")).collect(Collectors.toList()));
        model.addAttribute("ngayValues", dtNgay.stream().map(d -> ((Number) d.get("Doanhthu")).longValue()).collect(Collectors.toList()));
        model.addAttribute("monLabels", topMon.stream().map(d -> d.getString("_id")).collect(Collectors.toList()));
        model.addAttribute("monValues", topMon.stream().map(d -> ((Number) d.get("Soluongban")).intValue()).collect(Collectors.toList()));

        return "thongke";
    }
}
