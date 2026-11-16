package com.cafe.qlcafe.dao;

import com.cafe.qlcafe.MaHoa;
import com.cafe.qlcafe.MongoConnect;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Filters.*;

@Repository
public class NhanVienDAO {

    private MongoCollection<Document> col() {
        return MongoConnect.getCollection("nhanvien");
    }

    public Document dangNhap(String tendn, String matkhau) {
        Document nv = col().find(eq("Taikhoan.Tendn", tendn)).first();
        if (nv != null) {
            Document tk = (Document) nv.get("Taikhoan");
            if (tk != null && "Hoạt động".equals(tk.getString("Trangthai"))) {
                if (MaHoa.kiemTraMatKhau(matkhau, tk.getString("Matkhau"))) {
                    return nv;
                }
            }
        }
        return null;
    }

    public List<Document> layTatCa() {
        List<Document> list = new ArrayList<>();
        col().find().sort(Sorts.ascending("Manv")).into(list);
        return list;
    }

    public Document timTheoMa(String manv) {
        return col().find(eq("Manv", manv)).first();
    }

    public void them(String manv, String hoten, String phai, String ngaysinhStr, String dienthoai,
                     String tendn, String matkhau, String quyen) {
        Date ngaysinh = parseDate(ngaysinhStr);
        Document taikhoan = new Document("Tendn", tendn)
                .append("Matkhau", MaHoa.sha256(matkhau))
                .append("Quyen", quyen)
                .append("Trangthai", "Hoạt động");

        Document doc = new Document("Manv", manv)
                .append("Hoten", hoten)
                .append("Phai", phai)
                .append("Ngaysinh", ngaysinh)
                .append("Dienthoai", dienthoai)
                .append("Taikhoan", taikhoan);
        col().insertOne(doc);
    }

    public void sua(String manv, String hoten, String phai, String ngaysinhStr, String dienthoai,
                    String quyen, String trangthai, String matkhauMoi) {
        Document setFields = new Document("Hoten", hoten)
                .append("Phai", phai)
                .append("Ngaysinh", parseDate(ngaysinhStr))
                .append("Dienthoai", dienthoai)
                .append("Taikhoan.Quyen", quyen)
                .append("Taikhoan.Trangthai", trangthai);

        if (matkhauMoi != null && !matkhauMoi.trim().isEmpty()) {
            setFields.append("Taikhoan.Matkhau", MaHoa.sha256(matkhauMoi.trim()));
        }

        col().updateOne(eq("Manv", manv), new Document("$set", setFields));
    }

    public void doiTrangThaiTaiKhoan(String manv) {
        Document nv = timTheoMa(manv);
        if (nv != null) {
            Document tk = (Document) nv.get("Taikhoan");
            String ttHienTai = tk != null ? tk.getString("Trangthai") : "Hoạt động";
            String ttMoi = "Hoạt động".equals(ttHienTai) ? "Khóa" : "Hoạt động";
            col().updateOne(eq("Manv", manv), new Document("$set", new Document("Taikhoan.Trangthai", ttMoi)));
        }
    }

    public void xoa(String manv) {
        col().deleteOne(eq("Manv", manv));
    }

    public List<Document> thongKeTheoQuyen() {
        List<Document> list = new ArrayList<>();
        col().aggregate(Arrays.asList(
                Aggregates.group("$Taikhoan.Quyen", Accumulators.sum("Soluong", 1))
        )).into(list);
        return list;
    }

    private Date parseDate(String s) {
        try {
            if (s != null && !s.isEmpty()) return new SimpleDateFormat("yyyy-MM-dd").parse(s);
        } catch (Exception ignored) {}
        return new Date();
    }
}
