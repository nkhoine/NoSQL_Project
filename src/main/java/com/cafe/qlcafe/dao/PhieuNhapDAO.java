package com.cafe.qlcafe.dao;

import com.cafe.qlcafe.MongoConnect;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class PhieuNhapDAO {

    @Autowired
    private NguyenLieuDAO nguyenLieuDAO;

    private MongoCollection<Document> col() {
        return MongoConnect.getCollection("phieunhap");
    }

    public List<Document> layTatCa() {
        List<Document> list = new ArrayList<>();
        col().find().sort(Sorts.descending("Ngaynhap")).into(list);
        return list;
    }

    public void taoPhieuNhap(String mapn, String manv, String nhacungcap,
                             String manl, double soluong, double dongia) {
        Document nl = nguyenLieuDAO.timTheoMa(manl);
        String tennl = (nl != null) ? nl.getString("Tennl") : manl;

        List<Document> chiTiet = new ArrayList<>();
        chiTiet.add(new Document("Manl", manl)
                .append("Tennl", tennl)
                .append("Soluong", soluong)
                .append("Dongia", dongia));

        Document pn = new Document("Mapn", mapn)
                .append("Ngaynhap", new Date())
                .append("Manv", manv)
                .append("Nhacungcap", nhacungcap)
                .append("Chitiet", chiTiet);

        col().insertOne(pn);
        nguyenLieuDAO.congKho(manl, soluong);
    }
}
