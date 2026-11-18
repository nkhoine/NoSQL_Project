package com.cafe.qlcafe.controller;

import com.cafe.qlcafe.dao.NguyenLieuDAO;
import com.cafe.qlcafe.dao.PhieuNhapDAO;
import jakarta.servlet.http.HttpSession;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/phieunhap")
public class PhieuNhapController {

    @Autowired private PhieuNhapDAO phieuNhapDAO;
    @Autowired private NguyenLieuDAO nguyenLieuDAO;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("dsPhieuNhap", phieuNhapDAO.layTatCa());
        model.addAttribute("dsNguyenLieu", nguyenLieuDAO.layTatCa());
        return "phieunhap";
    }

    @PostMapping("/tao")
    public String tao(@RequestParam String nhacungcap, @RequestParam String manl,
                      @RequestParam double soluong, @RequestParam double dongia,
                      HttpSession session, RedirectAttributes ra) {
        if (nhacungcap.isBlank() || !Double.isFinite(soluong) || soluong <= 0
                || !Double.isFinite(dongia) || dongia < 0 || nguyenLieuDAO.timTheoMa(manl) == null) {
            ra.addFlashAttribute("error", "Kiểm tra nhà cung cấp, nguyên liệu, số lượng và đơn giá.");
            return "redirect:/phieunhap";
        }

        Document user = (Document) session.getAttribute("user");
        String manv = user != null ? user.getString("Manv") : "nv01";
        String mapn = "PN-" + UUID.randomUUID().toString().substring(0, 12);

        phieuNhapDAO.taoPhieuNhap(mapn, manv, nhacungcap.trim(), manl, soluong, dongia);
        ra.addFlashAttribute("success", "Đã lập phiếu nhập " + mapn + " và cộng tồn kho ($inc) thành công!");
        return "redirect:/phieunhap";
    }
}
