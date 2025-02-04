package code.monk.coupon.api.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
@Entity
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items;

    private double total;
    private double discount;

    private double oldTotal;

    public double getOldTotal() {
        return oldTotal;
    }

    public void setOldTotal(double oldTotal) {
        this.oldTotal = oldTotal;
    }

    public Cart() {}

    public Cart(List<CartItem> items) {
        this.items = items;
        this.total = calculateTotal();
        this.discount = 0.0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
        this.total = calculateTotal();
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    private double calculateTotal() {
        return items.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();
    }

    @Override
    public String toString() {
        return "Cart{" +
                "id=" + id +
                ", items=" + items +
                ", total=" + total +
                ", discount=" + discount +
                '}';
    }
}
