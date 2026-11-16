package com.cafe.qlcafe.controller;

import com.cafe.qlcafe.dao.KhachHangDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/khachhang")
public class KhachHangController {

    @Autowired private KhachHangDAO khachHangDAO;

    @GetMapping
    public String list(@RequestParam(required = false) String keyword, Model model) {
        model.addAttribute("dsKhachHang", khachHangDAO.timKiem(keyword));
        model.addAttribute("keyword", keyword);
        return "khachhang";
    }

    @PostMapping("/them")
    public String them(@RequestParam String makh, @RequestParam String tenkh, @RequestParam String dienthoai,
                       @RequestParam(defaultValue = "0") int diem, RedirectAttributes ra) {
        if (khachHangDAO.timTheoMa(makh.trim()) != null) {
            ra.addFlashAttribute("error", "Mã khách hàng " + makh + " đã tồn tại!");
            return "redirect:/khachhang";
        }
        khachHangDAO.them(makh.trim(), tenkh.trim(), dienthoai.trim(), diem);
        ra.addFlashAttribute("success", "Đã thêm khách hàng " + tenkh + "!");
        return "redirect:/khachhang";
    }

    @PostMapping("/sua")
    public String sua(@RequestParam String makh, @RequestParam String tenkh, @RequestParam String dienthoai,
                      @RequestParam int diem, RedirectAttributes ra) {
        khachHangDAO.sua(makh, tenkh, dienthoai, diem);
        ra.addFlashAttribute("success", "Đã cập nhật khách hàng " + makh + "!");
        return "redirect:/khachhang";
    }

    @PostMapping("/congdiem")
    public String congDiem(@RequestParam String makh, @RequestParam int diemCong, RedirectAttributes ra) {
        khachHangDAO.congDiem(makh, diemCong);
        ra.addFlashAttribute("success", "Đã cộng " + diemCong + " điểm (Atomic $inc) cho khách " + makh + "!");
        return "redirect:/khachhang";
    }

    @GetMapping("/xoa/{makh}")
    public String xoa(@PathVariable String makh, RedirectAttributes ra) {
        khachHangDAO.xoa(makh);
        ra.addFlashAttribute("success", "Đã xóa khách hàng " + makh + "!");
        return "redirect:/khachhang";
    }
}
