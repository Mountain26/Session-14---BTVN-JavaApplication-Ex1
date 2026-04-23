package javaweb_session14.ex1.service;

import javaweb_session14.ex1.model.entity.Order;
import javaweb_session14.ex1.model.entity.Wallet;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    @Autowired
    private SessionFactory sessionFactory;

    public void processPayment(Long orderId, Long walletId, double totalAmount) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            // 1. Cập nhật trạng thái đơn hàng
            Order order = session.get(Order.class, orderId);
            if (order != null) {
                order.setStatus("PAID");
                session.merge(order);
            }

            // Giả lập lỗi hệ thống bất ngờ (Ví dụ: Mất kết nối đến Service Ví)
            if (true) throw new RuntimeException("Kết nối đến cổng thanh toán thất bại!");

            // 2. Trừ tiền trong ví khách hàng
            Wallet wallet = session.get(Wallet.class, walletId);
            if (wallet != null) {
                wallet.setBalance(wallet.getBalance() - totalAmount);
                session.merge(wallet);
            }

            // Thành công => Commit lưu dữ liệu
            transaction.commit();
        } catch (Exception e) {
            System.out.println("Lỗi hệ thống: " + e.getMessage());
            // Có lỗi => Rollback hủy bỏ các thao tác sửa đổi trong phiên
            if (transaction != null) {
                transaction.rollback();
            }
        } finally {
            session.close();
        }
    }
}
