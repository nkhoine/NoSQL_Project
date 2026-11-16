db = db.getSiblingDB("QLCafe");

db.nhanvien.drop();
db.danhmuc.drop();
db.mon.drop();
db.nguyenlieu.drop();
db.ban.drop();
db.khachhang.drop();
db.hoadon.drop();
db.phieunhap.drop();

db.createCollection("nhanvien");
db.createCollection("danhmuc");
db.createCollection("mon");
db.createCollection("nguyenlieu");
db.createCollection("ban");
db.createCollection("khachhang");
db.createCollection("hoadon");
db.createCollection("phieunhap");

const HASH_123456 = "8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92";
db.nhanvien.insertMany([
  {
    Manv: "nv01",
    Hoten: "Nguyễn Văn An",
    Phai: "Nam",
    Ngaysinh: new Date("1998-05-12"),
    Dienthoai: "0901000001",
    Taikhoan: { Tendn: "admin", Matkhau: HASH_123456, Quyen: "Quản lý", Trangthai: "Hoạt động" }
  },
  {
    Manv: "nv02",
    Hoten: "Trần Thị Bình",
    Phai: "Nữ",
    Ngaysinh: new Date("2002-08-20"),
    Dienthoai: "0901000002",
    Taikhoan: { Tendn: "binh", Matkhau: HASH_123456, Quyen: "Thu ngân", Trangthai: "Hoạt động" }
  },
  {
    Manv: "nv03",
    Hoten: "Lê Hoàng Cường",
    Phai: "Nam",
    Ngaysinh: new Date("2001-11-03"),
    Dienthoai: "0901000003",
    Taikhoan: { Tendn: "cuong", Matkhau: HASH_123456, Quyen: "Pha chế", Trangthai: "Hoạt động" }
  },
  {
    Manv: "nv04",
    Hoten: "Phạm Minh Dung",
    Phai: "Nữ",
    Ngaysinh: new Date("2003-02-15"),
    Dienthoai: "0901000004",
    Taikhoan: { Tendn: "dung", Matkhau: HASH_123456, Quyen: "Thu ngân", Trangthai: "Hoạt động" }
  },
  {
    Manv: "nv05",
    Hoten: "Võ Thị Hà",
    Phai: "Nữ",
    Ngaysinh: new Date("2004-07-09"),
    Dienthoai: "0901000005",
    Taikhoan: { Tendn: "ha", Matkhau: HASH_123456, Quyen: "Pha chế", Trangthai: "Hoạt động" }
  },
  {
    Manv: "nv06",
    Hoten: "Đặng Quốc Khánh",
    Phai: "Nam",
    Ngaysinh: new Date("2000-09-25"),
    Dienthoai: "0901000006",
    Taikhoan: { Tendn: "khanh", Matkhau: HASH_123456, Quyen: "Thu ngân", Trangthai: "Hoạt động" }
  }
]);

db.danhmuc.insertMany([
  { Madm: "dm01", Tendm: "Đồ uống", Con: ["dm02", "dm03", "dm04"] },
  { Madm: "dm02", Tendm: "Cà phê", Con: ["dm05", "dm06"] },
  { Madm: "dm03", Tendm: "Trà trái cây", Con: [] },
  { Madm: "dm04", Tendm: "Đá xay & Sinh tố", Con: [] },
  { Madm: "dm05", Tendm: "Cà phê phin truyền thống", Con: [] },
  { Madm: "dm06", Tendm: "Cà phê máy (Espresso)", Con: [] },
  { Madm: "dm07", Tendm: "Bánh ngọt ăn kèm", Con: [] }
]);

db.nguyenlieu.insertMany([
  { Manl: "nl01", Tennl: "Cà phê hạt Robusta rang xay", Donvi: "g", Tonkho: 5000, Toithieu: 1000 },
  { Manl: "nl02", Tennl: "Cà phê hạt Arabica", Donvi: "g", Tonkho: 3000, Toithieu: 800 },
  { Manl: "nl03", Tennl: "Sữa đặc có đường", Donvi: "ml", Tonkho: 8000, Toithieu: 1500 },
  { Manl: "nl04", Tennl: "Sữa tươi không đường", Donvi: "ml", Tonkho: 10000, Toithieu: 2000 },
  { Manl: "nl05", Tennl: "Trà đen đặc biệt", Donvi: "g", Tonkho: 2000, Toithieu: 500 },
  { Manl: "nl06", Tennl: "Đào ngâm miếng", Donvi: "g", Tonkho: 2500, Toithieu: 600 },
  { Manl: "nl07", Tennl: "Siro sả chanh", Donvi: "ml", Tonkho: 1500, Toithieu: 400 },
  { Manl: "nl08", Tennl: "Bột Matcha Nhật Bản", Donvi: "g", Tonkho: 450, Toithieu: 500 },
  { Manl: "nl09", Tennl: "Ly nhựa nắp cầu 500ml", Donvi: "cái", Tonkho: 600, Toithieu: 150 },
  { Manl: "nl10", Tennl: "Bánh Tiramisu hộp", Donvi: "cái", Tonkho: 35, Toithieu: 10 }
]);

db.mon.insertMany([
  {
    Mamon: "m01", Tenmon: "Cà phê đen đá", Madm: "dm05",
    Size: [{ Ten: "M", Gia: 22000 }, { Ten: "L", Gia: 27000 }],
    Congthuc: [{ Manl: "nl01", Soluong: 20 }, { Manl: "nl09", Soluong: 1 }],
    Trangthai: "Đang bán", Nhan: ["Bán chạy"]
  },
  {
    Mamon: "m02", Tenmon: "Cà phê sữa đá", Madm: "dm05",
    Size: [{ Ten: "M", Gia: 25000 }, { Ten: "L", Gia: 30000 }],
    Congthuc: [{ Manl: "nl01", Soluong: 20 }, { Manl: "nl03", Soluong: 35 }, { Manl: "nl09", Soluong: 1 }],
    Trangthai: "Đang bán", Nhan: ["Bán chạy", "Đặc trưng"]
  },
  {
    Mamon: "m03", Tenmon: "Bạc xỉu", Madm: "dm05",
    Size: [{ Ten: "M", Gia: 29000 }, { Ten: "L", Gia: 35000 }],
    Congthuc: [{ Manl: "nl01", Soluong: 15 }, { Manl: "nl03", Soluong: 30 }, { Manl: "nl04", Soluong: 80 }, { Manl: "nl09", Soluong: 1 }],
    Trangthai: "Đang bán", Nhan: ["Bán chạy"]
  },
  {
    Mamon: "m04", Tenmon: "Latte nóng/đá", Madm: "dm06",
    Size: [{ Ten: "M", Gia: 38000 }, { Ten: "L", Gia: 45000 }],
    Congthuc: [{ Manl: "nl02", Soluong: 18 }, { Manl: "nl04", Soluong: 150 }, { Manl: "nl09", Soluong: 1 }],
    Trangthai: "Đang bán", Nhan: ["Món mới"]
  },
  {
    Mamon: "m05", Tenmon: "Americano đá", Madm: "dm06",
    Size: [{ Ten: "M", Gia: 32000 }, { Ten: "L", Gia: 38000 }],
    Congthuc: [{ Manl: "nl02", Soluong: 18 }, { Manl: "nl09", Soluong: 1 }],
    Trangthai: "Đang bán", Nhan: []
  },
  {
    Mamon: "m06", Tenmon: "Trà đào cam sả", Madm: "dm03",
    Size: [{ Ten: "M", Gia: 35000 }, { Ten: "L", Gia: 42000 }],
    Congthuc: [{ Manl: "nl05", Soluong: 10 }, { Manl: "nl06", Soluong: 40 }, { Manl: "nl07", Soluong: 20 }, { Manl: "nl09", Soluong: 1 }],
    Trangthai: "Đang bán", Nhan: ["Bán chạy"]
  },
  {
    Mamon: "m07", Tenmon: "Matcha đá xay", Madm: "dm04",
    Size: [{ Ten: "M", Gia: 45000 }, { Ten: "L", Gia: 52000 }],
    Congthuc: [{ Manl: "nl08", Soluong: 12 }, { Manl: "nl04", Soluong: 120 }, { Manl: "nl03", Soluong: 25 }, { Manl: "nl09", Soluong: 1 }],
    Trangthai: "Đang bán", Nhan: ["Món mới"]
  },
  {
    Mamon: "m08", Tenmon: "Bánh Tiramisu", Madm: "dm07",
    Size: [{ Ten: "M", Gia: 35000 }],
    Congthuc: [{ Manl: "nl10", Soluong: 1 }],
    Trangthai: "Đang bán", Nhan: []
  }
]);

db.ban.insertMany([
  { Maban: "b01", Tenban: "Bàn 01", Khuvuc: "Tầng 1", Trangthai: "Trống", Mahd: null },
  { Maban: "b02", Tenban: "Bàn 02", Khuvuc: "Tầng 1", Trangthai: "Trống", Mahd: null },
  { Maban: "b03", Tenban: "Bàn 03", Khuvuc: "Tầng 1", Trangthai: "Trống", Mahd: null },
  { Maban: "b04", Tenban: "Bàn 04", Khuvuc: "Tầng 1", Trangthai: "Trống", Mahd: null },
  { Maban: "b05", Tenban: "Bàn 05", Khuvuc: "Tầng 2", Trangthai: "Trống", Mahd: null },
  { Maban: "b06", Tenban: "Bàn 06", Khuvuc: "Tầng 2", Trangthai: "Trống", Mahd: null },
  { Maban: "b07", Tenban: "Bàn 07", Khuvuc: "Tầng 2", Trangthai: "Trống", Mahd: null },
  { Maban: "b08", Tenban: "Bàn 08", Khuvuc: "Tầng 2", Trangthai: "Trống", Mahd: null },
  { Maban: "b09", Tenban: "Bàn Sân Vườn 01", Khuvuc: "Sân vườn", Trangthai: "Trống", Mahd: null },
  { Maban: "b10", Tenban: "Bàn Sân Vườn 02", Khuvuc: "Sân vườn", Trangthai: "Trống", Mahd: null }
]);

db.khachhang.insertMany([
  { Makh: "kh01", Tenkh: "Trần Minh Quân", Dienthoai: "0912000001", Diem: 45 },
  { Makh: "kh02", Tenkh: "Lê Thị Ngọc Ánh", Dienthoai: "0912000002", Diem: 120 },
  { Makh: "kh03", Tenkh: "Phạm Hoàng Nam", Dienthoai: "0912000003", Diem: 30 },
  { Makh: "kh04", Tenkh: "Đỗ Thùy Linh", Dienthoai: "0912000004", Diem: 85 },
  { Makh: "kh05", Tenkh: "Hoàng Gia Bảo", Dienthoai: "0912000005", Diem: 15 }
]);

db.phieunhap.insertMany([
  {
    Mapn: "PN0001", Ngaynhap: new Date("2026-10-01T08:30:00Z"), Manv: "nv01",
    Nhacungcap: "Công ty Cà phê Tây Nguyên",
    Chitiet: [
      { Manl: "nl01", Tennl: "Cà phê hạt Robusta rang xay", Soluong: 5000, Dongia: 180 },
      { Manl: "nl02", Tennl: "Cà phê hạt Arabica", Soluong: 3000, Dongia: 240 }
    ]
  },
  {
    Mapn: "PN0002", Ngaynhap: new Date("2026-10-05T09:15:00Z"), Manv: "nv01",
    Nhacungcap: "Đại lý Nguyên liệu Pha chế Sài Gòn",
    Chitiet: [
      { Manl: "nl03", Tennl: "Sữa đặc có đường", Soluong: 8000, Dongia: 45 },
      { Manl: "nl04", Tennl: "Sữa tươi không đường", Soluong: 10000, Dongia: 32 }
    ]
  }
]);

print("Da nap thanh cong du lieu mau vao database QLCafe!");
