package javaweb_session14.ex1.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "wallets")
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double balance;

    public Wallet() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
}
