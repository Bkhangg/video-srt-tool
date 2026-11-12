# Phân tích APK: Video_SRT_Tool_2.9.1-BKggDs.apk

Tệp: `/root/.buddy/work/apk-analysis/Video_SRT_Tool_2.9.1.apk`
SHA-256: `38a42736d54d6738b32b8649986d6a6b9d4f6886aa1c0af5b48c577429ca4751`
Kích thước: 16.754.007 bytes (16,7 MB) · 70 tệp trong ZIP

## Nhận dạng
| Mục | Giá trị |
|---|---|
| Package | `com.buddyapps.videotool` |
| Tên | Video SRT Tool |
| versionCode / versionName | 21 / `2.9.1-BKggDs` |
| minSdk / targetSdk | 24 / không khai báo (mặc định = minSdk) |
| compileSdk | 36 (Android 16) |
| Activity | chỉ `MainActivity` (exported, không có intent-filter đáng kể) |
| ABI | chỉ `arm64-v8a` |
| Chứng thư ký | "BKggDs Local Build", tự ký, hiệu lực 2026-11-06 → 2054 |
| Kiểu chữ ký | chỉ v1 (JAR signature, `META-INF/BKGGDS.*`), không có APK Signing Block v2/v3 |

## Quyền
Không khai báo **bất kỳ** `uses-permission` nào — kể cả INTERNET.
→ Ứng dụng hoàn toàn ngoại tuyến: không có quyền mạng thì không thể gửi dữ liệu đi.

## Thành phần
- `classes.dex` 236 KB: `MainActivity` + khoảng 40 lớp phụ trợ
  (`VideoMux`, `SubtitleEditor`, `SrtText`, `MuxPlan`, `SafeSrtNames`,
  `ImportGuard`, `AppTheme`, `FontCatalog`, `Navigation`…), UI vẽ tay
  (Canvas/Paint/PageScrollView) chứ không dùng layout XML.
- `lib/arm64-v8a` 27 MB: FFmpegKit — `libavcodec`, `libavformat`, `libavfilter`,
  `libavutil`, `libswscale`, `libswresample`, `libavdevice` (FFmpeg n8.1.2,
  lavc 62.28.102) + `libc++_shared`.
- `assets/` 5,5 MB: gần như toàn bộ là font (DM Sans, Lora, Literata, Sora… kèm
  giấy phép OFL).
- Không có WebView, không có SDK quảng cáo/analytics/crash reporting.

## Mã độc / dấu hiệu đáng ngờ
Không thấy. Không có lời gọi mạng (`http`, `Socket`, `URLConnection`), không có
`Runtime.exec`/`ProcessBuilder`, không đọc danh bạ/SMS/vị trí, không có chuỗi
mã hoá hay URL lạ. Chuỗi duy nhất khớp "analytics" là một câu thông báo lỗi
phụ đề.

## Chức năng (theo chuỗi UI, có tiếng Việt)
1. Nhập video MKV/MP4 cục bộ (SAF).
2. Liệt kê và trích các track phụ đề text (SRT, ASS/SSA, WebVTT) ra tệp riêng.
3. Soạn/sửa SRT trong app (bàn phím, tìm kiếm, kiểm tra timestamp ngoài video).
4. Nhúng track SRT mới vào MKV/MP4 (mux, không re-encode video).
5. "Extract all tracks" — tách mọi ngôn ngữ.
6. Chặn ghi đè lên video gốc (`ImportGuard`, `SafeSrtNames`).

## Đánh giá
Ứng dụng tiện ích ngoại tuyến, tự ký bởi tác giả "BKggDs", không quyền, không
mạng, không tracker. Rủi ro thấp. Lưu ý: chỉ ký v1 nên Play Protect có thể cảnh
báo "app không rõ nguồn gốc"; không cài được trên máy không phải arm64.
