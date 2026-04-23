# Bài Tập: Thanh Toán Đơn Hàng (E-commerce Payment)

## Phần 1 - Phân tích logic

### 1. Phân tích nguyên nhân lỗi (Trace code hiện trường giả)
Khi khách hàng nhấn "Thanh toán", đoạn code mẫu thực hiện mở Session nhưng **không hề khởi tạo một Transaction nào**. Luồng chạy bị lỗi như sau:

1. **`Session session = HibernateUtils.getSessionFactory().openSession();`**: Mở ra một phiên làm việc với CSDL.
2. **`session.get(...)` & `session.update(order)`**: Cập nhật trạng thái của Order thành `"PAID"`. Lúc này tùy thuộc vào cấu hình FlushMode hoặc auto-commit của DB, câu lệnh `UPDATE` đơn hàng có thể được đẩy thẳng xuống DB ngay lập tức và ghi nhận thành công.
3. **`throw new RuntimeException(...)`**: Lỗi hệ thống xảy ra ngay sau đó làm đứt gãy luồng thực thi thông thường. Chạy thẳng nhảy vào khối `catch`.
4. **Các lệnh về `Wallet` (trừ tiền) bị bỏ qua**: Vì luồng code nhảy vào `catch`, các dòng code trừ số dư ví hoàn toàn không được chạy.
5. **Gây ra lỗi sai lệch nghiệp vụ**: Trong CSDL lúc này, đơn hàng đã đánh dấu là `"PAID"` (bởi lệnh update thứ nhất) nhưng tiền của khách hàng thì chưa bị trừ. 

### 2. Vòng đời của Transaction đang bị thiếu lệnh gì?
Bản chất của giao dịch thanh toán phải tuân thủ tính ACID (Toàn vẹn dữ liệu - Atomicity: Tất cả thành công, hoặc không có gì thành công). Việc các lệnh update chạy đơn lẻ mà không có khung Transaction sẽ khiến dữ liệu bị nửa nọ nửa kia khi có sự cố.

Trong vòng đời của khối lệnh trên, ta **cần bổ sung các lệnh quản lý Transaction** sau:

- **`Transaction tx = session.beginTransaction();`**: Phải bổ sung ở đầu khối `try` để gom nhóm các lệnh SQL (Update đơn, Update tiền) thành một block giao dịch duy nhất.
- **`tx.commit();`**: Phải được gọi ở cuối khối `try` (khi và chỉ khi không có dòng nào báo lỗi) để lưu toàn bộ các thay đổi vào dữ liệu vĩnh viễn. 
- **`tx.rollback();`**: Cần được gọi trong khối `catch`. Đây là chìa khóa xử lý lỗi: nếu có bất kỳ exception nào nhảy vào catch, hệ thống gọi lệnh rollback() để Database **hoàn tác toàn bộ các thay đổi đã chạy trước đó** (tức là rollback việc đơn hàng thay đổi thành PAID).

Với sự bổ sung 3 lệnh này, hệ thống sẽ đảm bảo: Nếu cổng trừ tiền bị sập, đơn hàng được khôi phục về trạng thái PENDING trước đó, người dùng không mất tiền và không thể lách luật để qua mặt thanh toán.

