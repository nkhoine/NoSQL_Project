package com.cafe.qlcafe.controller;

import com.cafe.qlcafe.dao.NguyenLieuDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/nguyenlieu")
public class NguyenLieuController {

    @Autowired private NguyenLieuDAO nguyenLieuDAO;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("dsNguyenLieu", nguyenLieuDAO.layTatCa());
        model.addAttribute("dsSapHet", nguyenLieuDAO.layNguyenLieuSapHet());
        return "nguyenlieu";
    }

    @PostMapping("/them")
    public String them(@RequestParam String manl, @RequestParam String tennl, @RequestParam String donvi,
                       @RequestParam double tonkho, @RequestParam double toithieu, RedirectAttributes ra) {
        if (nguyenLieuDAO.timTheoMa(manl.trim()) != null) {
            ra.addFlashAttribute("error", "Mã nguyên liệu " + manl + " đã tồn tại!");
            return "redirect:/nguyenlieu";
        }
        nguyenLieuDAO.them(manl.trim(), tennl.trim(), donvi, tonkho, toithieu);
        ra.addFlashAttribute("success", "Đã thêm nguyên liệu " + tennl + "!");
        return "redirect:/nguyenlieu";
    }

    @PostMapping("/sua")
    public String sua(@RequestParam String manl, @RequestParam String tennl, @RequestParam String donvi,
                      @RequestParam double tonkho, @RequestParam double toithieu, RedirectAttributes ra) {
        nguyenLieuDAO.sua(manl, tennl, donvi, tonkho, toithieu);
        ra.addFlashAttribute("success", "Đã cập nhật nguyên liệu " + manl + "!");
        return "redirect:/nguyenlieu";
    }

    @GetMapping("/xoa/{manl}")
    public String xoa(@PathVariable String manl, RedirectAttributes ra) {
        nguyenLieuDAO.xoa(manl);
        ra.addFlashAttribute("success", "Đã xóa nguyên liệu " + manl + "!");
        return "redirect:/nguyenlieu";
    }
}
