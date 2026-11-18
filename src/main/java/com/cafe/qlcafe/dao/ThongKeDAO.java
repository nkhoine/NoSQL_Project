package com.cafe.qlcafe.dao;

import com.cafe.qlcafe.MongoConnect;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.regex;

@Repository
public class ThongKeDAO {

    private MongoCollection<Document> hdCol() {
        return MongoConnect.getCollection("hoadon");
    }

    public List<Document> doanhThuTheoNgay(int limitDays) {
        List<Document> list = new ArrayList<>();
        hdCol().aggregate(Arrays.asList(
                Aggregates.match(eq("Trangthai", "Đã thanh toán")),
                Aggregates.group("$Ngay", Accumulators.sum("Doanhthu", "$Tongtien"), Accumulators.sum("Sohd", 1)),
                Aggregates.sort(Sorts.descending("_id")),
                Aggregates.limit(limitDays),
                Aggregates.sort(Sorts.ascending("_id"))
        )).into(list);
        return list;
    }

    public List<Document> top5MonBanChay() {
        List<Document> list = new ArrayList<>();
        hdCol().aggregate(Arrays.asList(
                Aggregates.match(eq("Trangthai", "Đã thanh toán")),
                Aggregates.unwind("$Mon"),
                Aggregates.group("$Mon.Tenmon", Accumulators.sum("Soluongban", "$Mon.Soluong"), Accumulators.sum("Doanhthu", "$Mon.Thanhtien")),
                Aggregates.sort(Sorts.descending("Soluongban")),
                Aggregates.limit(5)
        )).into(list);
        return list;
    }

    public List<Document> doanhThuTheoNhanVien() {
        List<Document> list = new ArrayList<>();
        hdCol().aggregate(Arrays.asList(
                Aggregates.match(eq("Trangthai", "Đã thanh toán")),
                Aggregates.group("$Nhanvien.Hoten", Accumulators.sum("Doanhthu", "$Tongtien"), Accumulators.sum("Sohd", 1)),
                Aggregates.sort(Sorts.descending("Doanhthu"))
        )).into(list);
        return list;
    }

    public List<Document> doanhThuTheoBanThang(String prefixThang) {
        List<Document> list = new ArrayList<>();
        hdCol().aggregate(Arrays.asList(
                Aggregates.match(regex("Ngay", Pattern.compile("^" + Pattern.quote(prefixThang)))),
                Aggregates.group("$Ban.Tenban", Accumulators.sum("Doanhthu", "$Tongtien"), Accumulators.sum("Sohd", 1)),
                Aggregates.sort(Sorts.descending("Doanhthu"))
        )).into(list);
        return list;
    }

    /** Aggregate 5: mỗi nhân viên đã phục vụ những bàn nào ($addToSet). */
    public List<Document> cacBanTheoNhanVien() {
        List<Document> list = new ArrayList<>();
        hdCol().aggregate(Arrays.asList(
                Aggregates.group("$Nhanvien.Hoten", Accumulators.addToSet("Cacban", "$Ban.Tenban")),
                Aggregates.sort(Sorts.ascending("_id"))
        )).into(list);
        for (Document d : list) {
            List<String> ds = d.getList("Cacban", String.class);
            if (ds != null) java.util.Collections.sort(ds);
        }
        return list;
    }

    public List<Document> thongKeHinhThucThanhToan() {
        List<Document> list = new ArrayList<>();
        hdCol().aggregate(Arrays.asList(
                Aggregates.match(eq("Trangthai", "Đã thanh toán")),
                Aggregates.group("$Hinhthuc",
                        Accumulators.avg("TB", "$Tongtien"),
                        Accumulators.max("Caonhat", "$Tongtien"),
                        Accumulators.min("Thapnhat", "$Tongtien"),
                        Accumulators.sum("Tong", "$Tongtien"),
                        Accumulators.sum("Sohd", 1))
        )).into(list);
        return list;
    }

    public long tinhTongDoanhThu() {
        Document res = hdCol().aggregate(Arrays.asList(
                Aggregates.match(eq("Trangthai", "Đã thanh toán")),
                Aggregates.group(null, Accumulators.sum("Tong", "$Tongtien"))
        )).first();
        return res != null ? ((Number) res.get("Tong")).longValue() : 0L;
    }

    public long demTongHoaDon() {
        return hdCol().countDocuments(eq("Trangthai", "Đã thanh toán"));
    }
}
