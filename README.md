# Summit Spirit - Climbing Gear E-Commerce Website
## 1. Project Description

Summit Spirit là website thương mại điện tử bán **phụ kiện leo núi và trekking**.
Hệ thống cho phép người dùng xem sản phẩm, thêm vào giỏ hàng và đặt hàng trực tuyến.

Project được xây dựng bằng **Java Web (Servlet + JSP)** theo mô hình **MVC + Front Controller (MainController)**.

# 2. System Requirements

Để chạy được project, máy cần cài đặt các phần mềm sau:

### Required Software

| Software                            | Version        |
| ----------------------------------- | -------------- |
| Java JDK                            | 8 hoặc 11      |
| Apache Tomcat                       | 9.x            |
| NetBeans IDE                        | 12+            |
| SQL Server                          | 2019 hoặc 2022 |
| SQL Server Management Studio (SSMS) | Latest         |
# 3. Project Setup
## Step 1: Clone hoặc Download project
Clone từ GitHub:
git clone (https://github.com/LeDat2k4FPT/ISC302.git)
Hoặc download file `.zip` và giải nén.

# Step 2: Import Project vào NetBeans
1. Mở **NetBeans IDE**
2. Chọn **File → Open Project**
3. Chọn thư mục project `SummitSpirit`

# Step 3: Setup Database

## 3.1 Tạo Database
Mở **SQL Server Management Studio (SSMS)** và chạy:
EXECUTE file summit_spirit.sql

## 3.2 Import Database
Chạy file SQL trong thư mục:
database/summit_spirit.sql
File này sẽ tạo các bảng:
* Users
* Products
* ProductVariants
* Categories
* Orders
* OrderDetails
* Colors
* Sizes

# Step 4: Configure Database Connection
Mở file:
DBUtils.java
Cập nhật thông tin kết nối SQL Server:
String url = "jdbc:sqlserver://localhost:1433;databaseName=SUMMIT_SPIRIT";
String user = "sa";
String password = "your_password";

# Step 5: Add SQL Server Driver
Nếu project chưa có driver, thêm thư viện:
mssql-jdbc.jar
Vào **Libraries** của project.

# Step 6: Run Project
1. Chuột phải project trong NetBeans
2. Chọn **Run**
Tomcat sẽ deploy project.
Sau khi chạy thành công, truy cập:
http://localhost:8080/SummitSpirit

# 4. Project Structure
SummitSpirit
│
├── src
│   ├── controller
│   │   └── MainController.java
│   │
│   ├── dao
│   │   ├── ProductDAO.java
│   │   ├── OrderDAO.java
│   │
│   ├── dto
│   │   ├── ProductDTO.java
│   │   ├── OrderDTO.java
│
├── web
│   ├── index.jsp
│   ├── product.jsp
│   ├── orderlist.jsp
│
└── database
    └── summit_spirit.sql
# 5. Default Account (If available)
Admin Account:
Email: admin@gmail.com
Password: 123456
# 6. Troubleshooting
### Cannot connect to SQL Server
Kiểm tra:
* SQL Server đã **Start** chưa
* TCP/IP đã enable chưa
* Port **1433** có mở chưa
### Project cannot run

# 7. TECH STACK
# APIs Used

## 1. Groq API – AI Chatbot Integration
Hệ thống tích hợp **Groq API** để xây dựng AI Chatbot hỗ trợ khách hàng trực tiếp trên website.

### Mục đích

API được sử dụng để:
* Phân tích câu hỏi của người dùng.
* Gợi ý các sản phẩm phụ kiện leo núi phù hợp.
* Hỗ trợ khách hàng tìm kiếm sản phẩm nhanh hơn.

### Quy trình hoạt động
1. Người dùng gửi câu hỏi qua chatbot trên website.
2. Server gửi request đến Groq API.
3. AI xử lý nội dung và tạo phản hồi.
4. Kết quả được trả về và hiển thị cho người dùng.

## 2. VNPAY Payment Gateway – Online Payment Integration
Hệ thống tích hợp **VNPAY API** để xử lý thanh toán trực tuyến cho đơn hàng.

### Chức năng
* Tạo URL thanh toán cho đơn hàng.
* Cho phép người dùng thanh toán qua:
  * Internet Banking
  * QR Code
  * Ứng dụng ngân hàng
* Nhận phản hồi thanh toán từ hệ thống VNPAY.

### Quy trình thanh toán

1. Người dùng chọn phương thức thanh toán **VNPAY** khi checkout.
2. Hệ thống tạo request thanh toán và chuyển hướng người dùng đến trang thanh toán của VNPAY.
3. Người dùng hoàn tất thanh toán qua ngân hàng hoặc QR Code.
4. VNPAY gửi kết quả giao dịch về hệ thống.
5. Website cập nhật trạng thái đơn hàng sau khi thanh toán thành công.

# 8. Notes
* Project sử dụng **Servlet + JSP**
* Mọi request được xử lý thông qua **MainController**
* Database sử dụng **SQL Server**

Kiểm tra:
* JDK version
* Tomcat server đã add vào NetBeans chưa
* Database connection đúng chưa
