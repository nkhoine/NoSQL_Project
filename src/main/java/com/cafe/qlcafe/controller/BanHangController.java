package com.cafe.qlcafe.controller;

import com.cafe.qlcafe.dao.*;
import jakarta.servlet.http.HttpSession;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/banhang")
public class BanHangController {

    @Autowired private BanDAO banDAO;
    @Autowired private MonDAO monDAO;
    @Autowired private HoaDonDAO hoaDonDAO;
    @Autowired private KhachHangDAO khachHangDAO;

    @GetMapping
    public String pos(@RequestParam(required = false) String maban, Model model) {
        model.addAttribute("dsBan", banDAO.layTatCa());
        model.addAttribute("dsBanTrong", banDAO.layBanTrong());
        model.addAttribute("dsMon", monDAO.layMonDangBan());
        model.addAttribute("dsKhachHang", khachHangDAO.layTatCa());

        if (maban != null && !maban.isEmpty()) {
            Document banChon = banDAO.timTheoMa(maban);
            model.addAttribute("banChon", banChon);
            if (banChon != null && banChon.getString("Mahd") != null) {
                model.addAttribute("hoaDonHienTai", hoaDonDAO.timTheoMa(banChon.getString("Mahd")));
            }
        }
        return "banhang";
    }

    @PostMapping("/moban")
    public String moBan(@RequestParam String maban, HttpSession session) {
        Document user = (Document) session.getAttribute("user");
        hoaDonDAO.taoHoaDonMoi(maban, user);
        return "redirect:/banhang?maban=" + maban;
    }

    @PostMapping("/themmon")
    public String themMon(@RequestParam String maban, @RequestParam String mahd, @RequestParam String mamon,
                          @RequestParam(defaultValue = "M") String size, @RequestParam(defaultValue = "1") int soluong) {
        hoaDonDAO.themMonVaoHoaDon(mahd, mamon, size, soluong);
        return "redirect:/banhang?maban=" + maban;
    }

    @PostMapping("/xoamon")
    public String xoaMon(@RequestParam String maban, @RequestParam String mahd, @RequestParam int index) {
        hoaDonDAO.xoaMonKhoiHoaDon(mahd, index);
        return "redirect:/banhang?maban=" + maban;
    }

    @PostMapping("/chuyenban")
    public String chuyenBan(@RequestParam String mabanCu, @RequestParam String mabanMoi, RedirectAttributes ra) {
        if (hoaDonDAO.chuyenBan(mabanCu, mabanMoi)) {
            ra.addFlashAttribute("success", "Đã chuyển bàn sang " + mabanMoi + "!");
            return "redirect:/banhang?maban=" + mabanMoi;
        }
        ra.addFlashAttribute("error", "Chuyển bàn thất bại!");
        return "redirect:/banhang?maban=" + mabanCu;
    }

    @PostMapping("/thanhtoan")
    public String thanhToan(@RequestParam String maban, @RequestParam String mahd,
                            @RequestParam(required = false) String makh, @RequestParam(defaultValue = "0") int giamgia,
                            @RequestParam(defaultValue = "Tiền mặt") String hinhthuc, RedirectAttributes ra) {
        String err = hoaDonDAO.thanhToan(mahd, makh, giamgia, hinhthuc);
        if (err != null) {
            ra.addFlashAttribute("error", err);
            return "redirect:/banhang?maban=" + maban;
        }
        ra.addFlashAttribute("success", "Thanh toán thành công hóa đơn " + mahd + "! Đã trừ kho & cộng điểm tích lũy.");
        return "redirect:/banhang/in/" + mahd;
    }

    @GetMapping("/in/{mahd}")
    public String inHoaDon(@PathVariable String mahd, Model model) {
        model.addAttribute("hd", hoaDonDAO.timTheoMa(mahd));
        return "hoadon_in";
    }
}
