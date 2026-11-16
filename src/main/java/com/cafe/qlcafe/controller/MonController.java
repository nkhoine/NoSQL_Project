package com.cafe.qlcafe.controller;

import com.cafe.qlcafe.dao.DanhMucDAO;
import com.cafe.qlcafe.dao.MonDAO;
import com.cafe.qlcafe.dao.NguyenLieuDAO;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/mon")
public class MonController {

    @Autowired private MonDAO monDAO;
    @Autowired private DanhMucDAO danhMucDAO;
    @Autowired private NguyenLieuDAO nguyenLieuDAO;

    @GetMapping
    public String list(@RequestParam(required = false) String keyword, @RequestParam(required = false) String madm,
                       @RequestParam(defaultValue = "1") int page, Model model) {
        int pageSize = 6;
        long total = monDAO.demMon(keyword, madm);
        int totalPages = Math.max(1, (int) Math.ceil((double) total / pageSize));

        model.addAttribute("dsMon", monDAO.timKiemPhanTrang(keyword, madm, page, pageSize));
        model.addAttribute("dsDanhMuc", danhMucDAO.layTatCa());
        model.addAttribute("dsNguyenLieu", nguyenLieuDAO.layTatCa());
        model.addAttribute("keyword", keyword);
        model.addAttribute("madm", madm);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        return "mon";
    }

    @PostMapping("/them")
    public String them(@RequestParam String mamon, @RequestParam String tenmon, @RequestParam String madm,
                       @RequestParam int giaM, @RequestParam(defaultValue = "0") int giaL,
                       @RequestParam(required = false) String manl1, @RequestParam(defaultValue = "0") double sl1,
                       @RequestParam(required = false) String nhanStr, RedirectAttributes ra) {
        if (monDAO.timTheoMa(mamon.trim()) != null) {
            ra.addFlashAttribute("error", "Mã món " + mamon + " đã tồn tại!");
            return "redirect:/mon";
        }
        List<Document> congThuc = new ArrayList<>();
        if (manl1 != null && !manl1.isEmpty() && sl1 > 0) {
            congThuc.add(new Document("Manl", manl1).append("Soluong", sl1));
        }
        List<String> nhan = new ArrayList<>();
        if (nhanStr != null && !nhanStr.trim().isEmpty()) {
            for (String n : nhanStr.split(",")) {
                if (!n.trim().isEmpty()) nhan.add(n.trim());
            }
        }
        monDAO.them(mamon.trim(), tenmon.trim(), madm, giaM, giaL, congThuc, "Đang bán", nhan);
        ra.addFlashAttribute("success", "Đã thêm món " + tenmon + "!");
        return "redirect:/mon";
    }

    @PostMapping("/sua")
    public String sua(@RequestParam String mamon, @RequestParam String tenmon, @RequestParam String madm,
                      @RequestParam int giaM, @RequestParam(defaultValue = "0") int giaL,
                      @RequestParam String trangthai, RedirectAttributes ra) {
        monDAO.sua(mamon, tenmon, madm, giaM, giaL, trangthai);
        ra.addFlashAttribute("success", "Đã cập nhật món " + mamon + "!");
        return "redirect:/mon";
    }

    @PostMapping("/nhan")
    public String capNhatNhan(@RequestParam String mamon, @RequestParam String nhan,
                              @RequestParam String action, RedirectAttributes ra) {
        if ("add".equals(action)) {
            monDAO.themNhan(mamon, nhan);
            ra.addFlashAttribute("success", "Đã thêm nhãn '" + nhan + "' ($addToSet)!");
        } else {
            monDAO.xoaNhan(mamon, nhan);
            ra.addFlashAttribute("success", "Đã gỡ nhãn '" + nhan + "' ($pull)!");
        }
        return "redirect:/mon";
    }

    @GetMapping("/xoa/{mamon}")
    public String xoa(@PathVariable String mamon, RedirectAttributes ra) {
        monDAO.xoa(mamon);
        ra.addFlashAttribute("success", "Đã xóa món " + mamon + "!");
        return "redirect:/mon";
    }
}
