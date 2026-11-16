package com.cafe.qlcafe.controller;

import com.cafe.qlcafe.MongoConnect;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/saoluu")
public class SaoLuuController {

    @Value("${app.backup.dir:D:/MONGO/BACKUP/fulldb}")
    private String backupDir;

    @Value("${app.export.dir:D:/MONGO/EXPORT/QLCafe}")
    private String exportDir;

    private static final List<String> COLLECTIONS = Arrays.asList(
            "nhanvien", "danhmuc", "mon", "nguyenlieu", "ban", "khachhang", "hoadon", "phieunhap"
    );

    @GetMapping
    public String index(Model model) {
        model.addAttribute("backupDir", backupDir);
        model.addAttribute("exportDir", exportDir);
        model.addAttribute("collections", COLLECTIONS);
        return "saoluu";
    }

    @PostMapping("/backup")
    public String backup(RedirectAttributes ra) {
        try {
            File dir = new File(backupDir);
            if (!dir.exists()) dir.mkdirs();

            ProcessBuilder pb = new ProcessBuilder("mongodump", "-d", "QLCafe", "-o", backupDir);
            pb.redirectErrorStream(true);
            Process p = pb.start();
            if (p.waitFor() == 0) {
                ra.addFlashAttribute("success", "Backup (mongodump) thành công vào thư mục: " + backupDir);
            } else {
                backupJsonFallback(new File(backupDir, "QLCafe_JSON"));
                ra.addFlashAttribute("success", "Đã sao lưu dự phòng (JSON) toàn bộ 8 collection vào: " + backupDir + "/QLCafe_JSON");
            }
        } catch (Exception e) {
            try {
                backupJsonFallback(new File(backupDir, "QLCafe_JSON"));
                ra.addFlashAttribute("success", "Đã sao lưu dự phòng (JSON) toàn bộ 8 collection vào: " + backupDir + "/QLCafe_JSON");
            } catch (Exception ex) {
                ra.addFlashAttribute("error", "Lỗi sao lưu: " + ex.getMessage());
            }
        }
        return "redirect:/saoluu";
    }

    @PostMapping("/restore")
    public String restore(RedirectAttributes ra) {
        try {
            File dumpFolder = new File(backupDir, "QLCafe");
            if (dumpFolder.exists()) {
                ProcessBuilder pb = new ProcessBuilder("mongorestore", "-d", "QLCafe", "--drop", dumpFolder.getAbsolutePath());
                pb.redirectErrorStream(true);
                Process p = pb.start();
                if (p.waitFor() == 0) {
                    ra.addFlashAttribute("success", "Restore (mongorestore) thành công từ: " + dumpFolder.getAbsolutePath());
                    return "redirect:/saoluu";
                }
            }

            File jsonFolder = new File(backupDir, "QLCafe_JSON");
            if (jsonFolder.exists()) {
                restoreJsonFallback(jsonFolder);
                ra.addFlashAttribute("success", "Đã phục hồi dữ liệu thành công từ bản sao lưu JSON!");
            } else {
                ra.addFlashAttribute("error", "Chưa tìm thấy bản sao lưu nào tại: " + backupDir);
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi phục hồi: " + e.getMessage());
        }
        return "redirect:/saoluu";
    }

    @PostMapping("/export")
    public String exportCollection(@RequestParam String collection, RedirectAttributes ra) {
        try {
            File dir = new File(exportDir);
            if (!dir.exists()) dir.mkdirs();
            File outFile = new File(dir, collection + ".json");

            MongoCollection<Document> col = MongoConnect.getCollection(collection);
            List<String> lines = new ArrayList<>();
            for (Document d : col.find()) {
                d.remove("_id");
                lines.add(d.toJson());
            }
            Files.write(outFile.toPath(), lines, StandardCharsets.UTF_8);
            ra.addFlashAttribute("success", "Đã Export collection [" + collection + "] (" + lines.size() + " documents) ra file: " + outFile.getAbsolutePath());
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi Export: " + e.getMessage());
        }
        return "redirect:/saoluu";
    }

    @PostMapping("/import")
    public String importCollection(@RequestParam String collection, RedirectAttributes ra) {
        try {
            File inFile = new File(exportDir, collection + ".json");
            if (!inFile.exists()) {
                ra.addFlashAttribute("error", "Không tìm thấy file: " + inFile.getAbsolutePath());
                return "redirect:/saoluu";
            }
            List<String> lines = Files.readAllLines(inFile.toPath(), StandardCharsets.UTF_8);
            List<Document> docs = new ArrayList<>();
            for (String line : lines) {
                if (!line.trim().isEmpty()) docs.add(Document.parse(line));
            }
            MongoCollection<Document> col = MongoConnect.getCollection(collection);
            col.drop();
            if (!docs.isEmpty()) col.insertMany(docs);
            ra.addFlashAttribute("success", "Đã Import thành công " + docs.size() + " documents vào collection [" + collection + "]!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi Import: " + e.getMessage());
        }
        return "redirect:/saoluu";
    }

    private void backupJsonFallback(File folder) throws Exception {
        if (!folder.exists()) folder.mkdirs();
        MongoDatabase db = MongoConnect.getDatabase();
        for (String cName : COLLECTIONS) {
            List<String> lines = new ArrayList<>();
            for (Document d : db.getCollection(cName).find()) {
                d.remove("_id");
                lines.add(d.toJson());
            }
            Files.write(new File(folder, cName + ".json").toPath(), lines, StandardCharsets.UTF_8);
        }
    }

    private void restoreJsonFallback(File folder) throws Exception {
        MongoDatabase db = MongoConnect.getDatabase();
        for (String cName : COLLECTIONS) {
            File f = new File(folder, cName + ".json");
            if (f.exists()) {
                List<String> lines = Files.readAllLines(f.toPath(), StandardCharsets.UTF_8);
                List<Document> docs = new ArrayList<>();
                for (String line : lines) {
                    if (!line.trim().isEmpty()) docs.add(Document.parse(line));
                }
                MongoCollection<Document> col = db.getCollection(cName);
                col.drop();
                if (!docs.isEmpty()) col.insertMany(docs);
            }
        }
    }
}
