package com.cafe.qlcafe.dao;

import com.cafe.qlcafe.MongoConnect;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

@Repository
public class BanDAO {

    private MongoCollection<Document> col() {
        return MongoConnect.getCollection("ban");
    }

    public List<Document> layTatCa() {
        List<Document> list = new ArrayList<>();
        col().find().sort(Sorts.ascending("Maban")).into(list);
        return list;
    }

    public List<Document> layBanTrong() {
        List<Document> list = new ArrayList<>();
        col().find(eq("Trangthai", "Trống")).sort(Sorts.ascending("Maban")).into(list);
        return list;
    }

    public Document timTheoMa(String maban) {
        return col().find(eq("Maban", maban)).first();
    }

    public void them(String maban, String tenban, String khuvuc) {
        Document doc = new Document("Maban", maban)
                .append("Tenban", tenban)
                .append("Khuvuc", khuvuc)
                .append("Trangthai", "Trống")
                .append("Mahd", null);
        col().insertOne(doc);
    }

    public void sua(String maban, String tenban, String khuvuc) {
        col().updateOne(eq("Maban", maban), new Document("$set",
                new Document("Tenban", tenban).append("Khuvuc", khuvuc)));
    }

    public void moBan(String maban, String mahd) {
        col().updateOne(eq("Maban", maban), new Document("$set",
                new Document("Trangthai", "Có khách").append("Mahd", mahd)));
    }

    public void giaiPhongBan(String maban) {
        col().updateOne(eq("Maban", maban), new Document("$set",
                new Document("Trangthai", "Trống").append("Mahd", null)));
    }

    public void xoa(String maban) {
        col().deleteOne(eq("Maban", maban));
    }
}
