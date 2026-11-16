package com.cafe.qlcafe.dao;

import com.cafe.qlcafe.MongoConnect;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mongodb.client.model.Filters.*;

@Repository
public class NguyenLieuDAO {

    private MongoCollection<Document> col() {
        return MongoConnect.getCollection("nguyenlieu");
    }

    public List<Document> layTatCa() {
        List<Document> list = new ArrayList<>();
        col().find().sort(Sorts.ascending("Manl")).into(list);
        return list;
    }

    public Document timTheoMa(String manl) {
        return col().find(eq("Manl", manl)).first();
    }

    public List<Document> layNguyenLieuSapHet() {
        List<Document> list = new ArrayList<>();
        col().find(expr(new Document("$lte", List.of("$Tonkho", "$Toithieu"))))
                .sort(Sorts.ascending("Tonkho"))
                .into(list);
        return list;
    }

    public void them(String manl, String tennl, String donvi, double tonkho, double toithieu) {
        Document doc = new Document("Manl", manl)
                .append("Tennl", tennl)
                .append("Donvi", donvi)
                .append("Tonkho", tonkho)
                .append("Toithieu", toithieu);
        col().insertOne(doc);
    }

    public void sua(String manl, String tennl, String donvi, double tonkho, double toithieu) {
        col().updateOne(eq("Manl", manl), new Document("$set",
                new Document("Tennl", tennl).append("Donvi", donvi).append("Tonkho", tonkho).append("Toithieu", toithieu)));
    }

    public void congKho(String manl, double soLuong) {
        col().updateOne(eq("Manl", manl), new Document("$inc", new Document("Tonkho", soLuong)));
    }

    public boolean truKho(String manl, double soLuong) {
        UpdateResult res = col().updateOne(
                and(eq("Manl", manl), gte("Tonkho", soLuong)),
                new Document("$inc", new Document("Tonkho", -soLuong))
        );
        return res.getModifiedCount() > 0;
    }

    public String kiemTraDuNguyenLieuChoHoaDon(List<Document> dsMonTrongHd, MonDAO monDAO) {
        Map<String, Double> tongCanDung = new HashMap<>();
        for (Document item : dsMonTrongHd) {
            String mamon = item.getString("Mamon");
            int slLy = item.getInteger("Soluong", 1);
            String size = item.getString("Size");
            double heSoSize = "L".equalsIgnoreCase(size) ? 1.25 : 1.0;

            Document monDoc = monDAO.timTheoMa(mamon);
            if (monDoc != null) {
                List<Document> congThuc = monDoc.getList("Congthuc", Document.class);
                if (congThuc != null) {
                    for (Document ct : congThuc) {
                        String manl = ct.getString("Manl");
                        double dinhLuong = ((Number) ct.get("Soluong")).doubleValue() * slLy * heSoSize;
                        tongCanDung.merge(manl, dinhLuong, Double::sum);
                    }
                }
            }
        }

        for (Map.Entry<String, Double> entry : tongCanDung.entrySet()) {
            Document nl = timTheoMa(entry.getKey());
            if (nl == null) return "Không tìm thấy nguyên liệu mã: " + entry.getKey();
            double tonKho = ((Number) nl.get("Tonkho")).doubleValue();
            if (tonKho < entry.getValue()) {
                return "Không đủ nguyên liệu [" + nl.getString("Tennl") + "] (Cần: " + entry.getValue() + ", Tồn: " + tonKho + ")";
            }
        }
        return null;
    }

    public void truKhoTheoHoaDon(List<Document> dsMonTrongHd, MonDAO monDAO) {
        for (Document item : dsMonTrongHd) {
            String mamon = item.getString("Mamon");
            int slLy = item.getInteger("Soluong", 1);
            String size = item.getString("Size");
            double heSoSize = "L".equalsIgnoreCase(size) ? 1.25 : 1.0;

            Document monDoc = monDAO.timTheoMa(mamon);
            if (monDoc != null) {
                List<Document> congThuc = monDoc.getList("Congthuc", Document.class);
                if (congThuc != null) {
                    for (Document ct : congThuc) {
                        String manl = ct.getString("Manl");
                        double dinhLuong = ((Number) ct.get("Soluong")).doubleValue() * slLy * heSoSize;
                        truKho(manl, dinhLuong);
                    }
                }
            }
        }
    }

    public void xoa(String manl) {
        col().deleteOne(eq("Manl", manl));
    }
}
