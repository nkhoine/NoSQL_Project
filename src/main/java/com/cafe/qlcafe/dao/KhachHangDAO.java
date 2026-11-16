package com.cafe.qlcafe.dao;

import com.cafe.qlcafe.MongoConnect;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.mongodb.client.model.Filters.*;

@Repository
public class KhachHangDAO {

    private MongoCollection<Document> col() {
        return MongoConnect.getCollection("khachhang");
    }

    public List<Document> layTatCa() {
        List<Document> list = new ArrayList<>();
        col().find().sort(Sorts.ascending("Makh")).into(list);
        return list;
    }

    public List<Document> timKiem(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return layTatCa();
        Pattern p = Pattern.compile(Pattern.quote(keyword.trim()), Pattern.CASE_INSENSITIVE);
        List<Document> list = new ArrayList<>();
        col().find(or(regex("Tenkh", p), regex("Dienthoai", p), regex("Makh", p))).into(list);
        return list;
    }

    public Document timTheoMa(String makh) {
        return col().find(eq("Makh", makh)).first();
    }

    public Document timTheoSdt(String sdt) {
        return col().find(eq("Dienthoai", sdt)).first();
    }

    public void them(String makh, String tenkh, String dienthoai, int diem) {
        Document doc = new Document("Makh", makh)
                .append("Tenkh", tenkh)
                .append("Dienthoai", dienthoai)
                .append("Diem", diem);
        col().insertOne(doc);
    }

    public void sua(String makh, String tenkh, String dienthoai, int diem) {
        col().updateOne(eq("Makh", makh), new Document("$set",
                new Document("Tenkh", tenkh).append("Dienthoai", dienthoai).append("Diem", diem)));
    }

    public void congDiem(String makh, int diemCong) {
        if (makh != null && !makh.isEmpty() && diemCong != 0) {
            col().updateOne(eq("Makh", makh), new Document("$inc", new Document("Diem", diemCong)));
        }
    }

    public void xoa(String makh) {
        col().deleteOne(eq("Makh", makh));
    }
}
