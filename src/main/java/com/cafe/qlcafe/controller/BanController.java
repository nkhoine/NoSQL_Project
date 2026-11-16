package com.cafe.qlcafe.controller;

import com.cafe.qlcafe.dao.BanDAO;
import com.cafe.qlcafe.dao.HoaDonDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/ban")
public class BanController {

    @Autowired private BanDAO banDAO;
    @Autowired private HoaDonDAO hoaDonDAO;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("dsBan", banDAO.layTatCa());
        model.addAttribute("dsBanTrong", banDAO.layBanTrong());
        return "ban";
    }

    @PostMapping("/them")
    public String them(@RequestParam String maban, @RequestParam String tenban,
                       @RequestParam String khuvuc, RedirectAttributes ra) {
        if (banDAO.timTheoMa(maban.trim()) != null) {
            ra.addFlashAttribute("error", "Mã bàn " + maban + " đã tồn tại!");
            return "redirect:/ban";
        }
        banDAO.them(maban.trim(), tenban.trim(), khuvuc);
        ra.addFlashAttribute("success", "Đã thêm " + tenban + "!");
        return "redirect:/ban";
    }

    @PostMapping("/sua")
    public String sua(@RequestParam String maban, @RequestParam String tenban,
                      @RequestParam String khuvuc, RedirectAttributes ra) {
        banDAO.sua(maban, tenban, khuvuc);
        ra.addFlashAttribute("success", "Đã cập nhật thông tin bàn " + maban + "!");
        return "redirect:/ban";
    }

    @PostMapping("/chuyen")
    public String chuyen(@RequestParam String mabanCu, @RequestParam String mabanMoi, RedirectAttributes ra) {
        if (hoaDonDAO.chuyenBan(mabanCu, mabanMoi)) {
            ra.addFlashAttribute("success", "Đã chuyển bàn từ " + mabanCu + " sang " + mabanMoi + "!");
        } else {
            ra.addFlashAttribute("error", "Không thể chuyển bàn! Kiểm tra trạng thái 2 bàn.");
        }
        return "redirect:/ban";
    }

    @GetMapping("/xoa/{maban}")
    public String xoa(@PathVariable String maban, RedirectAttributes ra) {
        banDAO.xoa(maban);
        ra.addFlashAttribute("success", "Đã xóa bàn " + maban + "!");
        return "redirect:/ban";
    }
}
