<!-- Author: Phan Tuan Thanh -->
# Slot29 Library Hub

Author: **Phan Tuan Thanh**.

Mã nguồn Maven nằm trong thư mục con `slot 29`. Dự án là ứng dụng web Spring Boot minh họa Hibernate. Sau khi khởi động, mở http://localhost:8080 để quản lý sinh viên, thêm đầu sách và mượn/trả sách. Server tiếp tục chạy; bấm Stop trong IDE hoặc Ctrl+C trong terminal để dừng.

## Chạy ngay bằng H2

Máy cần JDK 21 và Maven 3.9 trở lên. Mở PowerShell tại thư mục chứa file này:

```powershell
powershell -ExecutionPolicy Bypass -File .\run.ps1
```

Script build, chạy kiểm thử và chạy demo. Không cần SQL Server cho chế độ này. H2 lưu trong RAM, dữ liệu mất khi chương trình kết thúc.

Kết quả mong đợi có dòng `Tomcat started on port 8080`. Mở http://localhost:8080 trong trình duyệt. Trang web có dữ liệu demo và hiển thị `Author: Phan Tuan Thanh`. Nếu cổng 8080 đang được dùng, chạy JAR với `--server.port=8081` và mở http://localhost:8081.

## Chạy với SQL Server

1. Kết nối đúng instance trong SSMS, mở và chạy toàn bộ `slot 29/sql/setup-sqlserver.sql`. Script tạo `LibraryDB` và bốn bảng, giữ nguyên bảng/dữ liệu đã có. Tài khoản chạy script cần quyền tạo database và bảng. Script không sửa cấu trúc bảng cũ nếu chúng đã tồn tại.
2. Bật TCP/IP cho SQL Server, xác định cổng TCP của instance và cấu hình tài khoản SQL có quyền đọc/ghi trên `LibraryDB`.
3. Trong PowerShell tại thư mục này, thiết lập kết nối rồi chạy:

```powershell
$env:LIBRARY_DB_URL = 'jdbc:sqlserver://localhost:1433;databaseName=LibraryDB;encrypt=true;trustServerCertificate=true'
$env:LIBRARY_DB_USER = 'sa'
$dbCredential = Get-Credential -UserName $env:LIBRARY_DB_USER -Message 'Nhap tai khoan SQL Server'
$env:LIBRARY_DB_PASSWORD = $dbCredential.GetNetworkCredential().Password
powershell -ExecutionPolicy Bypass -File .\run.ps1 -Profile sqlserver
```

Thay `localhost:1433` bằng máy chủ và cổng thực tế. Máy hiện có instance tên `SQL2022`; instance này có thể dùng cổng khác 1433. Dùng SQL Authentication tương ứng với tài khoản đã thiết lập trong SQL Server.

4. Chạy `slot 29/sql/verify-sqlserver.sql` trong SSMS để xem dữ liệu và ràng buộc sau demo. Demo tạo một sinh viên và hai sách nếu chưa có, khôi phục liên kết còn thiếu, chạy lại không tạo dữ liệu demo trùng. Bảng `BORROWINGS` chưa có dòng từ demo many-to-many; phần này được kiểm tra bằng integration test riêng.

Profile `sqlserver` kiểm tra schema bằng `ddl-auto=validate`; nếu báo thiếu bảng/cột, kiểm tra bạn đã chạy script trên đúng server/database. Không có mật khẩu được ghi trong mã nguồn. Việc kết nối thật với SQL Server cần hoàn tất các bước trên.

## Chạy trực tiếp bằng Maven hoặc IDE

Mở thư mục con `slot 29` có `pom.xml` trong IDE và chọn JDK 21. Chạy main class `vn.edu.fpt.slot29.Slot29Application`; profile mặc định là `demo` với H2.

```powershell
Set-Location '.\slot 29'
mvn -B -ntp clean package
java -jar target/slot29-library-hub-1.0.0.jar
# SQL Server (sau khi đặt các biến môi trường ở trên):
java -jar target/slot29-library-hub-1.0.0.jar --spring.profiles.active=sqlserver,demo
```

Chi tiết kiến trúc và bài thực hành nằm trong `slot 29/README.md`.

## API web

- GET /api/library: danh sách sinh viên, sách và liên kết đang mượn.
- POST /api/students: thêm sinh viên (email, firstName, lastName, gpa từ 0 đến 10).
- POST /api/books: thêm sách (isbn, title, author).
- POST /api/students/{studentId}/books/{bookId}: mượn sách.
- DELETE /api/students/{studentId}/books/{bookId}: trả sách.

Giao diện web dùng quan hệ STUDENTS_BOOKS trong bài many-to-many. H2 lưu dữ liệu trong RAM; dùng profile SQL Server để giữ dữ liệu sau khi dừng ứng dụng.
