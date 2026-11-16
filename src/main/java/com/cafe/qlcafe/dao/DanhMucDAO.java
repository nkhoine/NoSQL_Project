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
public class DanhMucDAO {

    private MongoCollection<Document> col() {
        return MongoConnect.getCollection("danhmuc");
    }

    public List<Document> layTatCa() {
        List<Document> list = new ArrayList<>();
        col().find().sort(Sorts.ascending("Madm")).into(list);
        return list;
    }

    public Document timTheoMa(String madm) {
        return col().find(eq("Madm", madm)).first();
    }

    public void them(String madm, String tendm, List<String> con) {
        Document doc = new Document("Madm", madm)
                .append("Tendm", tendm)
                .append("Con", con != null ? con : new ArrayList<>());
        col().insertOne(doc);
    }

    public void sua(String madm, String tendm) {
        col().updateOne(eq("Madm", madm), new Document("$set", new Document("Tendm", tendm)));
    }

    public void themDanhMucCon(String madmCha, String madmCon) {
        if (madmCha != null && !madmCha.isEmpty() && madmCon != null && !madmCon.isEmpty()) {
            col().updateOne(eq("Madm", madmCha), new Document("$addToSet", new Document("Con", madmCon)));
        }
    }

    public void xoaDanhMucCon(String madmCha, String madmCon) {
        col().updateOne(eq("Madm", madmCha), new Document("$pull", new Document("Con", madmCon)));
    }

    public void xoa(String madm) {
        col().deleteOne(eq("Madm", madm));
    }
}
