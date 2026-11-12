# Video SRT Tool (BKggDs build)

Ứng dụng Android trích xuất, ghép và nhúng phụ đề (SRT/ASS/WebVTT/mov_text) vào video, kèm
trình phát video xem trước có phụ đề, chọn track âm thanh và toàn màn hình.

Bản này dựa trên APK gốc `Video_SRT_Tool_2.9.1` (không kèm trong repo) và được sửa lại:
đổi gói sang `com.bkggds.videotool`, giao diện 5 ngôn ngữ, trình phát ExoPlayer tự viết,
sửa lỗi hiển thị và làm lại hàng nút điều khiển.

## Tính năng chính

- Nhập video MKV/MP4, xem trước ngay trong app.
- Trích xuất phụ đề văn bản (SRT, ASS/SSA, WebVTT, mov_text) từ video.
- Nhúng phụ đề vào video, ghép nhiều track phụ đề.
- Danh sách track phụ đề theo ngôn ngữ, đặt tên track theo ngôn ngữ hiện tại.
- Trình phát: thanh tua, ±10 giây, tốc độ phát, chọn track âm thanh, chọn phụ đề,
  tỷ lệ khung hình (Fit/Fill/Stretch), toàn màn hình ngang, cử chỉ chạm/kéo.
- Giao diện: English, 中文, 日本語, 한국어, Tiếng Việt.

## Cấu trúc

```
src/sources/          Mã Java (app trong com/bkggds/videotool, ffmpegkit trong com/antonkarpenko)
libs3/                Thư viện cần để biên dịch (AndroidX Media3 1.4.1, Guava, Kotlin stdlib...)
files.txt             Danh sách file nguồn cho javac
build.sh              Biên dịch + đóng gói + ký APK
repackage.py          Chèn dex vào APK gốc, đổi tên gói trong manifest/resources.arsc, đổi version
gen_loc.py            Sinh Localization.java từ strings.tsv + tr1..tr4.tsv
strings.tsv, tr*.tsv  Chuỗi giao diện 5 ngôn ngữ
docs/ANALYSIS.md      Phân tích APK gốc
```

## Yêu cầu build

- JDK 17 (hoặc mới hơn) và Android SDK: `android.jar` (API 36), `d8`, `zipalign`, `apksigner`.
- Python 3.
- APK gốc `Video_SRT_Tool_2.9.1.apk` đặt ở thư mục gốc (dùng làm khung: tài nguyên, manifest,
  thư viện native và dex của ffmpegkit).

`build.sh` đang trỏ tới `/root/android-sdk/android-36/android.jar`; sửa biến `AJ` nếu SDK
của bạn ở chỗ khác.

## Build

```bash
./build.sh <versionCode> <versionName>
# ví dụ
./build.sh 41 "3.1.1-BKggDs"
```

Kết quả: `Video_SRT_Tool_<versionName>.apk` đã ký.

Khoá ký (`bkggds.keystore`) **không** nằm trong repo; build.sh dùng `--ks bkggds.keystore`
với mật khẩu/alias riêng, hãy sửa lại hoặc tạo khoá của bạn:

```bash
keytool -genkeypair -keystore bkggds.keystore -alias bkggds -keyalg RSA -keysize 2048 \
  -validity 10000 -storepass <pass> -keypass <pass> -dname "CN=Local Build"
```

## Ghi chú

- `repackage.py` sửa trực tiếp APK gốc: thay chuỗi tên gói trong string pool của
  `AndroidManifest.xml` (đồng thời sửa kích thước chunk gốc) và ghi đè trường tên 256 byte
  của chunk `ResTable_package` trong `resources.arsc`; `resources.arsc` phải giữ STORED.
- Chỉ đóng gói `arm64-v8a`.
- `versionName` phải dài đúng 12 ký tự (giống `2.9.1-BKggDs`) do cách vá manifest.
