// ==================== THANH VIEN A (11 CAU) ====================
db.nhanvien.update({Manv:"nv03"}, {$set:{"Taikhoan.Quyen":"Thu ngân"}});
db.nhanvien.updateOne({Manv:"nv06"}, {$set:{"Taikhoan.Trangthai":"Khóa"}});
db.khachhang.update({Makh:"kh01"}, {$inc:{Diem:9}});
db.nhanvien.find({}, {_id:0, Manv:1, Hoten:1, "Taikhoan.Quyen":1});
db.nhanvien.find({$and:[{Phai:"Nữ"}, {Ngaysinh:{$gte:new Date("2002-01-01")}}]});
db.khachhang.find({Dienthoai:"0912000001"});
db.hoadon.find({"Nhanvien.Manv":"nv02"});
db.nhanvien.aggregate([{$group:{_id:"$Taikhoan.Quyen", Soluong:{$sum:1}}}]);
db.hoadon.aggregate([{$match:{Khachhang:{$ne:null}}}, {$group:{_id:"$Khachhang", Sohd:{$sum:1}}}, {$match:{Sohd:{$gte:5}}}]);
db.hoadon.aggregate([{$match:{Khachhang:{$ne:null}}}, {$sort:{Ngaylap:1}}, {$group:{_id:"$Khachhang.Tenkh", Landau:{$first:"$Ngaylap"}, Gannhat:{$last:"$Ngaylap"}}}]);
db.hoadon.aggregate([{$group:{_id:"$Hinhthuc", TB:{$avg:"$Tongtien"}, Caonhat:{$max:"$Tongtien"}, Thapnhat:{$min:"$Tongtien"}}}]);

// ==================== THANH VIEN B (16 CAU) ====================
db.mon.update({Mamon:"m02"}, {$set:{"Size.0.Gia":27000}});
db.mon.update({Madm:"dm03"}, {$set:{Trangthai:"Ngừng bán"}}, {multi:true});
db.mon.update({Mamon:"m04"}, {$addToSet:{Nhan:"Bán chạy"}});
db.mon.update({Mamon:"m04"}, {$pull:{Nhan:"Món mới"}});
db.danhmuc.update({Madm:"dm01"}, {$addToSet:{Con:"dm07"}});
db.mon.find().pretty();
db.mon.find({Trangthai:"Đang bán"}, {Mamon:1, Tenmon:1, _id:0});
db.mon.find({Tenmon:/^Cà phê/});
db.mon.find({Tenmon:/sữa/});
db.mon.find({Madm:{$in:["dm05","dm06"]}});
db.mon.find().skip(10).limit(10);
db.nguyenlieu.find({Tonkho:{$lt:500}});
db.mon.aggregate([{$group:{_id:"$Madm", Somon:{$sum:1}, Dsmon:{$push:"$Tenmon"}}}]);
db.mon.aggregate([{$unwind:"$Size"}, {$project:{_id:0, Tenmon:{$toUpper:"$Tenmon"}, Size:"$Size.Ten", Giamoi:{$multiply:["$Size.Gia",1.1]}}}]);
db.mon.aggregate([{$unwind:"$Nhan"}, {$group:{_id:"$Nhan", Somon:{$sum:1}}}]);
db.nguyenlieu.aggregate([{$group:{_id:"$Donvi", TongTon:{$sum:"$Tonkho"}, SoLoai:{$sum:1}}}]);

// ==================== THANH VIEN C (16 CAU) ====================
db.ban.update({Maban:"b01"}, {$set:{Trangthai:"Có khách"}});
db.hoadon.update({Mahd:"HD0001"}, {$push:{Mon:{Mamon:"m03", Tenmon:"Bạc xỉu", Size:"M", Gia:29000, Soluong:1, Thanhtien:29000}}});
db.hoadon.updateOne({Mahd:"HD0001"}, {$set:{"Ban.Maban":"b02", "Ban.Tenban":"Bàn 02"}});
db.hoadon.updateOne({Mahd:"HD0001"}, {$set:{Trangthai:"Đã thanh toán", Hinhthuc:"Tiền mặt"}});
db.ban.find({Trangthai:"Trống"});
db.mon.find({Nhan:"Bán chạy"});
db.hoadon.find({Tongtien:{$gt:100000}});
db.hoadon.find({$and:[{Hinhthuc:"Chuyển khoản"}, {Tongtien:{$gte:80000}}]});
db.hoadon.find({$or:[{"Ban.Maban":"b01"}, {"Ban.Maban":"b02"}]});
db.hoadon.find({Mon:{$elemMatch:{Mamon:"m03", Soluong:{$gte:2}}}});
db.hoadon.find().sort({Ngaylap:-1}).limit(5);
db.hoadon.aggregate([{$match:{Trangthai:"Đã thanh toán"}}, {$group:{_id:"$Ngay", Doanhthu:{$sum:"$Tongtien"}, Sohd:{$sum:1}}}, {$sort:{_id:1}}]);
db.hoadon.aggregate([{$unwind:"$Mon"}, {$group:{_id:"$Mon.Tenmon", Soluongban:{$sum:"$Mon.Soluong"}}}, {$sort:{Soluongban:-1}}, {$limit:5}]);
db.hoadon.aggregate([{$group:{_id:"$Nhanvien", Doanhthu:{$sum:"$Tongtien"}, Sohd:{$sum:1}}}]);
db.hoadon.aggregate([{$match:{Ngay:/^2026-10/}}, {$group:{_id:"$Ban.Tenban", Doanhthu:{$sum:"$Tongtien"}}}, {$sort:{Doanhthu:-1}}]);
db.hoadon.aggregate([{$group:{_id:"$Nhanvien.Hoten", Cacban:{$addToSet:"$Ban.Tenban"}}}]);
