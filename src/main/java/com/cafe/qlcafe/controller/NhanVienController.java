package com.cafe.qlcafe.controller;

import com.cafe.qlcafe.dao.NhanVienDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/nhanvien")
public class NhanVienController {

    @Autowired private NhanVienDAO nhanVienDAO;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("dsNhanVien", nhanVienDAO.layTatCa());
        model.addAttribute("thongKeQuyen", nhanVienDAO.thongKeTheoQuyen());
        return "nhanvien";
    }

    @PostMapping("/them")
    public String them(@RequestParam String manv, @RequestParam String hoten, @RequestParam String phai,
                       @RequestParam String ngaysinh, @RequestParam String dienthoai,
                       @RequestParam String tendn, @RequestParam String matkhau, @RequestParam String quyen,
                       RedirectAttributes ra) {
        if (nhanVienDAO.timTheoMa(manv.trim()) != null) {
            ra.addFlashAttribute("error", "Mã nhân viên " + manv + " đã tồn tại!");
            return "redirect:/nhanvien";
        }
        nhanVienDAO.them(manv.trim(), hoten.trim(), phai, ngaysinh, dienthoai.trim(), tendn.trim(), matkhau, quyen);
        ra.addFlashAttribute("success", "Đã thêm nhân viên " + hoten + " thành công!");
        return "redirect:/nhanvien";
    }

    @PostMapping("/sua")
    public String sua(@RequestParam String manv, @RequestParam String hoten, @RequestParam String phai,
                      @RequestParam String ngaysinh, @RequestParam String dienthoai,
                      @RequestParam String quyen, @RequestParam String trangthai,
                      @RequestParam(required = false) String matkhauMoi, RedirectAttributes ra) {
        nhanVienDAO.sua(manv, hoten, phai, ngaysinh, dienthoai, quyen, trangthai, matkhauMoi);
        ra.addFlashAttribute("success", "Đã cập nhật nhân viên " + manv + "!");
        return "redirect:/nhanvien";
    }

    @GetMapping("/khoa/{manv}")
    public String khoa(@PathVariable String manv, RedirectAttributes ra) {
        nhanVienDAO.doiTrangThaiTaiKhoan(manv);
        ra.addFlashAttribute("success", "Đã đổi trạng thái tài khoản của " + manv + "!");
        return "redirect:/nhanvien";
    }

    @GetMapping("/xoa/{manv}")
    public String xoa(@PathVariable String manv, RedirectAttributes ra) {
        if ("nv01".equals(manv)) {
            ra.addFlashAttribute("error", "Không thể xóa tài khoản Quản trị gốc (nv01)!");
            return "redirect:/nhanvien";
        }
        nhanVienDAO.xoa(manv);
        ra.addFlashAttribute("success", "Đã xóa nhân viên " + manv + "!");
        return "redirect:/nhanvien";
    }
}
