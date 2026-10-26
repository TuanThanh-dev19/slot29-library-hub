<!-- Author: Phan Tuan Thanh -->
# Đưa Slot29 Library Hub lên GitHub

Tên repository: `slot29-library-hub`.

Mô tả: `A Spring Boot library management web app with Hibernate, SQL Server, and student book borrowing.`

1. Mở https://github.com/new và đăng nhập. Tạo repository tên `slot29-library-hub`, chọn Public hoặc Private theo nhu cầu. Không thêm README, .gitignore hoặc license khi tạo vì thư mục local đã có sẵn mã nguồn.
2. Mở PowerShell tại thư mục ngoài chứa README.md và run.ps1, chạy lần lượt:

```powershell
Set-Location 'D:\FA2026\SBA301\slot 29'
git init
git config user.name 'Phan Tuan Thanh'
git config user.email 'YOUR_GITHUB_EMAIL'
git add .
git status
git commit -m "Initial commit: Slot29 Library Hub"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/slot29-library-hub.git
git push -u origin main
```

Thay `YOUR_GITHUB_EMAIL` bằng email GitHub hoặc email noreply của tài khoản. Thay `YOUR_USERNAME` bằng username GitHub của bạn. Khi Git yêu cầu đăng nhập, hoàn tất cửa sổ xác thực. Không nhập mật khẩu tài khoản GitHub vào prompt Git yêu cầu token.

File .gitignore ở thư mục ngoài đã bỏ qua target, .idea, log, dữ liệu local và .env. Không thêm mật khẩu SQL Server vào mã nguồn; dự án đọc thông tin này từ biến môi trường.

Các lần cập nhật sau:

```powershell
git add .
git commit -m "Update library management features"
git push
```

Nếu báo `remote origin already exists`, kiểm tra `git remote -v`. Nếu URL sai, sửa bằng `git remote set-url origin https://github.com/YOUR_USERNAME/slot29-library-hub.git`.

Hướng dẫn chính thức: https://docs.github.com/en/migrations/importing-source-code/using-the-command-line-to-import-source-code/adding-locally-hosted-code-to-github
