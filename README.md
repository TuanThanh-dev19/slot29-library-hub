<!-- Author: Phan Tuan Thanh -->
# Slot29 Library Hub

Author: **Phan Tuan Thanh**.

Ứng dụng quản lý thư viện Spring Boot, Hibernate và SQL Server. Mã nguồn Maven nằm trong thư mục con `slot 29`.

## Chạy từ IntelliJ

1. Chọn JDK 21 hoặc JDK 24 x64, reload Maven nếu cần.
2. Chạy `vn.edu.fpt.slot29.Slot29Application`.
3. Đợi `Tomcat started on port 8080`, mở http://localhost:8080.

Mặc định đã bật `sqlserver,demo`, kết nối Windows Authentication tới `localhost:1433` (instance `SQL2022` trên máy này), database **Slot29LibraryDB**. Không cần nhập hoặc lưu mật khẩu. Nếu cấu hình Run cũ có Active profiles là `demo` hoặc `h2`, xóa giá trị đó để dùng mặc định hoặc nhập `sqlserver,demo`.

Working directory trong Run Configuration dùng thư mục ngoài chứa README này hoặc thư mục con có pom.xml. Hai thư mục này đã có DLL xác thực Windows. Nếu báo thiếu DLL, chạy `setup-windows-auth.ps1` hoặc thêm VM option `-Djava.library.path="DUONG_DAN_THU_MUC_CHUA_DLL"`.

Dữ liệu lưu trong SQL Server và còn sau khi dừng ứng dụng. Demo tái sử dụng dữ liệu đã tồn tại, không tạo trùng khi khởi động lại. Database `LibraryDB` của dự án khác được giữ nguyên.

## Chuẩn bị trên máy mới

Máy cần Java x64, Maven và SQL Server. Mở SSMS, kết nối instance bằng Windows Authentication và chạy toàn bộ `slot 29/sql/setup-sqlserver.sql` để tạo database và bảng. Tài khoản Windows cần quyền tạo database/bảng và đọc/ghi dữ liệu ứng dụng. Server cần bật TCP/IP; thay URL nếu máy/cổng khác:

```powershell
$env:LIBRARY_DB_URL = 'jdbc:sqlserver://localhost:1433;databaseName=Slot29LibraryDB;integratedSecurity=true;authenticationScheme=NativeAuthentication;encrypt=true;trustServerCertificate=true'
powershell -ExecutionPolicy Bypass -File .\setup-windows-auth.ps1
powershell -ExecutionPolicy Bypass -File .\run.ps1
```

Script xác thực tải DLL Microsoft JDBC 12.8.1 x64 từ Maven Central. DLL là file nhị phân local, đã được bỏ qua trong Git; máy mới cần tải lại. JDBC và DLL cần cùng phiên bản. Nếu dùng IntelliJ, khởi động lại Run sau khi chuẩn bị.

Trên máy hiện tại đã chạy script tạo schema và cài DLL. Trong SSMS, xem dữ liệu bằng:

```sql
-- Author: Phan Tuan Thanh
USE Slot29LibraryDB;
SELECT * FROM dbo.STUDENTS;
SELECT * FROM dbo.BOOKS;
SELECT * FROM dbo.STUDENTS_BOOKS;
SELECT * FROM dbo.BORROWINGS;
```

Demo web dùng quan hệ STUDENTS_BOOKS. BORROWINGS là mô hình association entity riêng trong bài thực hành. File `slot 29/sql/verify-sqlserver.sql` kiểm tra thêm khóa ngoại và unique index.

## Maven và kiểm thử

```powershell
Set-Location '.\slot 29'
mvn -B -ntp clean package
java -jar target/slot29-library-hub-1.0.0.jar
```

17 integration test dùng H2 riêng, không xóa hoặc sửa dữ liệu SQL Server. Profile SQL Server dùng `ddl-auto=validate`, không tự thay đổi schema.

H2 chỉ là chế độ tùy chọn, dữ liệu mất khi tắt:

```powershell
java -jar target/slot29-library-hub-1.0.0.jar --spring.profiles.active=h2,demo
```

Giữ chương trình chạy khi dùng web. Bấm Stop/Ctrl+C để dừng. Nếu cổng 8080 bị chiếm, dừng lần chạy cũ hoặc truyền `--server.port=8081` và mở http://localhost:8081.

## API web

- GET /api/library: sinh viên, sách, liên kết đang mượn.
- POST /api/students: thêm sinh viên (email, firstName, lastName, gpa 0–10).
- POST /api/books: thêm sách (isbn, title, author).
- POST /api/students/{studentId}/books/{bookId}: mượn sách.
- DELETE /api/students/{studentId}/books/{bookId}: trả sách.

Chi tiết bài thực hành nằm trong `slot 29/README.md`.
