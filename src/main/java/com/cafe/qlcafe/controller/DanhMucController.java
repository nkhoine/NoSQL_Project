package com.cafe.qlcafe.controller;

import com.cafe.qlcafe.dao.DanhMucDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;

@Controller
@RequestMapping("/danhmuc")
public class DanhMucController {

    @Autowired private DanhMucDAO danhMucDAO;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("dsDanhMuc", danhMucDAO.layTatCa());
        return "danhmuc";
    }

    @PostMapping("/them")
    public String them(@RequestParam String madm, @RequestParam String tendm,
                       @RequestParam(required = false) String madmCha, RedirectAttributes ra) {
        if (danhMucDAO.timTheoMa(madm.trim()) != null) {
            ra.addFlashAttribute("error", "Mã danh mục " + madm + " đã tồn tại!");
            return "redirect:/danhmuc";
        }
        danhMucDAO.them(madm.trim(), tendm.trim(), new ArrayList<>());
        if (madmCha != null && !madmCha.isEmpty()) danhMucDAO.themDanhMucCon(madmCha, madm.trim());
        ra.addFlashAttribute("success", "Đã thêm danh mục " + tendm + "!");
        return "redirect:/danhmuc";
    }

    @PostMapping("/sua")
    public String sua(@RequestParam String madm, @RequestParam String tendm, RedirectAttributes ra) {
        danhMucDAO.sua(madm, tendm);
        ra.addFlashAttribute("success", "Đã cập nhật danh mục " + madm + "!");
        return "redirect:/danhmuc";
    }

    @PostMapping("/themcon")
    public String themCon(@RequestParam String madmCha, @RequestParam String madmCon, RedirectAttributes ra) {
        danhMucDAO.themDanhMucCon(madmCha, madmCon);
        ra.addFlashAttribute("success", "Đã thêm danh mục con " + madmCon + " vào " + madmCha + " ($addToSet)!");
        return "redirect:/danhmuc";
    }

    @GetMapping("/xoa/{madm}")
    public String xoa(@PathVariable String madm, RedirectAttributes ra) {
        danhMucDAO.xoa(madm);
        ra.addFlashAttribute("success", "Đã xóa danh mục " + madm + "!");
        return "redirect:/danhmuc";
    }
}
