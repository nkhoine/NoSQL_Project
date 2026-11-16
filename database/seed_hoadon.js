const dsBan = db.ban.find().toArray();
const dsNv = db.nhanvien.find({ "Taikhoan.Quyen": { $in: ["Quản lý", "Thu ngân"] } }).toArray();
const dsKh = db.khachhang.find().toArray();
const dsMon = db.mon.find().toArray();
const hinhThucs = ["Tiền mặt", "Chuyển khoản"];

function randInt(min, max) {
  return Math.floor(Math.random() * (max - min + 1)) + min;
}

const hoaDons = [];
for (let i = 1; i <= 200; i++) {
  const mahd = "HD" + String(i).padStart(4, "0");
  const month = i <= 140 ? 10 : 11;
  const day = month === 10 ? randInt(1, 31) : randInt(1, 15);
  const hour = randInt(7, 21);
  const minute = randInt(0, 59);

  const ngayStr = `2026-${String(month).padStart(2, "0")}-${String(day).padStart(2, "0")}`;
  const ngayLap = new Date(`${ngayStr}T${String(hour).padStart(2, "0")}:${String(minute).padStart(2, "0")}:00Z`);

  const ban = dsBan[randInt(0, dsBan.length - 1)];
  const nv = dsNv[randInt(0, dsNv.length - 1)];
  const kh = Math.random() < 0.65 ? dsKh[randInt(0, dsKh.length - 1)] : null;

  const soMon = randInt(1, 4);
  const monList = [];
  let tong = 0;

  for (let j = 0; j < soMon; j++) {
    const m = dsMon[randInt(0, dsMon.length - 1)];
    const sz = m.Size[randInt(0, m.Size.length - 1)];
    const sl = (i === 1 && j === 0) ? 2 : randInt(1, 3);
    const monChon = (i === 1 && j === 0) ? dsMon.find(x => x.Mamon === "m03") : m;
    const sizeChon = (i === 1 && j === 0) ? monChon.Size[0] : sz;
    const thanhTien = sizeChon.Gia * sl;
    tong += thanhTien;

    monList.push({
      Mamon: monChon.Mamon,
      Tenmon: monChon.Tenmon,
      Size: sizeChon.Ten,
      Gia: sizeChon.Gia,
      Soluong: sl,
      Thanhtien: thanhTien
    });
  }

  const giamGia = kh && tong >= 80000 ? 10000 : 0;
  const tongTien = Math.max(0, tong - giamGia);

  hoaDons.push({
    Mahd: mahd,
    Ngaylap: ngayLap,
    Ngay: ngayStr,
    Ban: { Maban: ban.Maban, Tenban: ban.Tenban },
    Nhanvien: { Manv: nv.Manv, Hoten: nv.Hoten },
    Khachhang: kh ? { Makh: kh.Makh, Tenkh: kh.Tenkh } : null,
    Mon: monList,
    Giamgia: giamGia,
    Tongtien: tongTien,
    Trangthai: "Đã thanh toán",
    Hinhthuc: hinhThucs[randInt(0, 1)]
  });
}

db.hoadon.insertMany(hoaDons);
print("Da sinh thanh cong " + hoaDons.length + " hoa don mau!");
