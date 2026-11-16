package com.cafe.qlcafe.dao;

import com.cafe.qlcafe.MongoConnect;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

@Repository
public class HoaDonDAO {

    @Autowired private BanDAO banDAO;
    @Autowired private MonDAO monDAO;
    @Autowired private NguyenLieuDAO nguyenLieuDAO;
    @Autowired private KhachHangDAO khachHangDAO;

    private MongoCollection<Document> col() {
        return MongoConnect.getCollection("hoadon");
    }

    public List<Document> layMoiNhat(int limit) {
        List<Document> list = new ArrayList<>();
        col().find().sort(Sorts.descending("Ngaylap")).limit(limit).into(list);
        return list;
    }

    public Document timTheoMa(String mahd) {
        return col().find(eq("Mahd", mahd)).first();
    }

    public String taoHoaDonMoi(String maban, Document nhanVien) {
        Document ban = banDAO.timTheoMa(maban);
        if (ban == null) return null;
        if ("Có khách".equals(ban.getString("Trangthai")) && ban.getString("Mahd") != null) {
            return ban.getString("Mahd");
        }

        String mahd = "HD" + System.currentTimeMillis() % 1000000;
        Date now = new Date();
        String ngayStr = new SimpleDateFormat("yyyy-MM-dd").format(now);

        Document hd = new Document("Mahd", mahd)
                .append("Ngaylap", now)
                .append("Ngay", ngayStr)
                .append("Ban", new Document("Maban", ban.getString("Maban")).append("Tenban", ban.getString("Tenban")))
                .append("Nhanvien", new Document("Manv", nhanVien.getString("Manv")).append("Hoten", nhanVien.getString("Hoten")))
                .append("Khachhang", null)
                .append("Mon", new ArrayList<Document>())
                .append("Giamgia", 0)
                .append("Tongtien", 0)
                .append("Trangthai", "Đang phục vụ")
                .append("Hinhthuc", "Tiền mặt");

        col().insertOne(hd);
        banDAO.moBan(maban, mahd);
        return mahd;
    }

    public void themMonVaoHoaDon(String mahd, String mamon, String sizeTen, int soLuong) {
        Document hd = timTheoMa(mahd);
        Document mon = monDAO.timTheoMa(mamon);
        if (hd == null || mon == null) return;

        int gia = 0;
        List<Document> sizes = mon.getList("Size", Document.class);
        if (sizes != null) {
            for (Document s : sizes) {
                if (s.getString("Ten").equalsIgnoreCase(sizeTen)) {
                    gia = ((Number) s.get("Gia")).intValue();
                    break;
                }
            }
            if (gia == 0 && !sizes.isEmpty()) {
                gia = ((Number) sizes.get(0).get("Gia")).intValue();
                sizeTen = sizes.get(0).getString("Ten");
            }
        }

        List<Document> dsMon = hd.getList("Mon", Document.class);
        if (dsMon == null) dsMon = new ArrayList<>();

        boolean daCo = false;
        for (Document item : dsMon) {
            if (mamon.equals(item.getString("Mamon")) && sizeTen.equalsIgnoreCase(item.getString("Size"))) {
                int slMoi = item.getInteger("Soluong", 1) + soLuong;
                item.put("Soluong", slMoi);
                item.put("Thanhtien", slMoi * gia);
                daCo = true;
                break;
            }
        }

        if (!daCo) {
            Document monMoi = new Document("Mamon", mamon)
                    .append("Tenmon", mon.getString("Tenmon"))
                    .append("Size", sizeTen)
                    .append("Gia", gia)
                    .append("Soluong", soLuong)
                    .append("Thanhtien", gia * soLuong);
            dsMon.add(monMoi);
        }

        int tongTien = tinhTongTien(dsMon, hd.getInteger("Giamgia", 0));
        col().updateOne(eq("Mahd", mahd), new Document("$set",
                new Document("Mon", dsMon).append("Tongtien", tongTien)));
    }

    public void xoaMonKhoiHoaDon(String mahd, int index) {
        Document hd = timTheoMa(mahd);
        if (hd == null) return;
        List<Document> dsMon = hd.getList("Mon", Document.class);
        if (dsMon != null && index >= 0 && index < dsMon.size()) {
            dsMon.remove(index);
            int tongTien = tinhTongTien(dsMon, hd.getInteger("Giamgia", 0));
            col().updateOne(eq("Mahd", mahd), new Document("$set",
                    new Document("Mon", dsMon).append("Tongtien", tongTien)));
        }
    }

    public boolean chuyenBan(String mabanCu, String mabanMoi) {
        Document banCu = banDAO.timTheoMa(mabanCu);
        Document banMoi = banDAO.timTheoMa(mabanMoi);
        if (banCu == null || banMoi == null) return false;
        if (!"Có khách".equals(banCu.getString("Trangthai")) || !"Trống".equals(banMoi.getString("Trangthai"))) return false;

        String mahd = banCu.getString("Mahd");
        if (mahd != null) {
            col().updateOne(eq("Mahd", mahd), new Document("$set",
                    new Document("Ban.Maban", banMoi.getString("Maban")).append("Ban.Tenban", banMoi.getString("Tenban"))));
            banDAO.giaiPhongBan(mabanCu);
            banDAO.moBan(mabanMoi, mahd);
            return true;
        }
        return false;
    }

    public String thanhToan(String mahd, String makh, int giamGia, String hinhThuc) {
        Document hd = timTheoMa(mahd);
        if (hd == null) return "Không tìm thấy hóa đơn!";

        List<Document> dsMon = hd.getList("Mon", Document.class);
        if (dsMon == null || dsMon.isEmpty()) return "Hóa đơn chưa có món nào, không thể thanh toán!";

        String loiKho = nguyenLieuDAO.kiemTraDuNguyenLieuChoHoaDon(dsMon, monDAO);
        if (loiKho != null) return loiKho;

        nguyenLieuDAO.truKhoTheoHoaDon(dsMon, monDAO);

        Document khDoc = null;
        if (makh != null && !makh.trim().isEmpty()) {
            Document kh = khachHangDAO.timTheoMa(makh.trim());
            if (kh != null) {
                khDoc = new Document("Makh", kh.getString("Makh")).append("Tenkh", kh.getString("Tenkh"));
            }
        }

        int tongTien = tinhTongTien(dsMon, giamGia);
        if (khDoc != null) {
            int diemCong = Math.max(1, tongTien / 10000);
            khachHangDAO.congDiem(khDoc.getString("Makh"), diemCong);
        }

        col().updateOne(eq("Mahd", mahd), new Document("$set",
                new Document("Khachhang", khDoc)
                        .append("Giamgia", giamGia)
                        .append("Tongtien", tongTien)
                        .append("Trangthai", "Đã thanh toán")
                        .append("Hinhthuc", hinhThuc)));

        Document ban = (Document) hd.get("Ban");
        if (ban != null) banDAO.giaiPhongBan(ban.getString("Maban"));

        return null;
    }

    private int tinhTongTien(List<Document> dsMon, int giamGia) {
        int sum = 0;
        for (Document m : dsMon) sum += ((Number) m.get("Thanhtien")).intValue();
        return Math.max(0, sum - giamGia);
    }
}
