package com.cafe.qlcafe.dao;

import com.cafe.qlcafe.MongoConnect;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.mongodb.client.model.Filters.*;

@Repository
public class MonDAO {

    private MongoCollection<Document> col() {
        return MongoConnect.getCollection("mon");
    }

    public List<Document> layTatCa() {
        List<Document> list = new ArrayList<>();
        col().find().sort(Sorts.ascending("Mamon")).into(list);
        return list;
    }

    public List<Document> layMonDangBan() {
        List<Document> list = new ArrayList<>();
        col().find(eq("Trangthai", "Đang bán")).sort(Sorts.ascending("Mamon")).into(list);
        return list;
    }

    public Document timTheoMa(String mamon) {
        return col().find(eq("Mamon", mamon)).first();
    }

    public List<Document> timKiemPhanTrang(String keyword, String madm, int page, int pageSize) {
        Bson filter = buildFilter(keyword, madm);
        int skip = Math.max(0, (page - 1) * pageSize);
        List<Document> list = new ArrayList<>();
        col().find(filter).sort(Sorts.ascending("Mamon")).skip(skip).limit(pageSize).into(list);
        return list;
    }

    public long demMon(String keyword, String madm) {
        return col().countDocuments(buildFilter(keyword, madm));
    }

    private Bson buildFilter(String keyword, String madm) {
        List<Bson> conditions = new ArrayList<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            Pattern p = Pattern.compile(Pattern.quote(keyword.trim()), Pattern.CASE_INSENSITIVE);
            conditions.add(or(regex("Tenmon", p), regex("Mamon", p)));
        }
        if (madm != null && !madm.trim().isEmpty()) {
            conditions.add(eq("Madm", madm.trim()));
        }
        return conditions.isEmpty() ? new Document() : and(conditions);
    }

    public void them(String mamon, String tenmon, String madm, int giaM, int giaL,
                     List<Document> congthuc, String trangthai, List<String> nhan) {
        List<Document> sizes = new ArrayList<>();
        sizes.add(new Document("Ten", "M").append("Gia", giaM));
        if (giaL > 0) sizes.add(new Document("Ten", "L").append("Gia", giaL));

        Document doc = new Document("Mamon", mamon)
                .append("Tenmon", tenmon)
                .append("Madm", madm)
                .append("Size", sizes)
                .append("Congthuc", congthuc != null ? congthuc : new ArrayList<>())
                .append("Trangthai", trangthai)
                .append("Nhan", nhan != null ? nhan : new ArrayList<>());
        col().insertOne(doc);
    }

    public void sua(String mamon, String tenmon, String madm, int giaM, int giaL, String trangthai) {
        List<Document> sizes = new ArrayList<>();
        sizes.add(new Document("Ten", "M").append("Gia", giaM));
        if (giaL > 0) sizes.add(new Document("Ten", "L").append("Gia", giaL));
        col().updateOne(eq("Mamon", mamon), new Document("$set",
                new Document("Tenmon", tenmon).append("Madm", madm).append("Size", sizes).append("Trangthai", trangthai)));
    }

    public void themNhan(String mamon, String nhan) {
        col().updateOne(eq("Mamon", mamon), new Document("$addToSet", new Document("Nhan", nhan)));
    }

    public void xoaNhan(String mamon, String nhan) {
        col().updateOne(eq("Mamon", mamon), new Document("$pull", new Document("Nhan", nhan)));
    }

    public void ngungBanTheoDanhMuc(String madm) {
        col().updateMany(eq("Madm", madm), new Document("$set", new Document("Trangthai", "Ngừng bán")));
    }

    public void xoa(String mamon) {
        col().deleteOne(eq("Mamon", mamon));
    }
}
