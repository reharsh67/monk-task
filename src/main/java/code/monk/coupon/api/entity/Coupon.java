package code.monk.coupon.api.entity;


import jakarta.persistence.*;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;

import java.time.LocalDateTime;

@Entity
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code; // Unique coupon code
    private String type; // cart, product, bxgy
    private Double discountPercentage; // For cart/product discounts
    private Double discountAmount; // Fixed discount value
    private Double threshold; // Minimum cart value for cart-wise discounts
    private String applicableProducts; // JSON string for product-specific discounts
    private int buyQuantity;
    private int freeQuantity;
    private boolean isActive;

    private int repetitionLimit;
    private String freeProducts;

    public String getFreeProducts() {
        return freeProducts;
    }

    public void setFreeProducts(String freeProducts) {
        this.freeProducts = freeProducts;
    }

    public int getBuyQuantity() {
        return buyQuantity;
    }

    public void setBuyQuantity(int buyQuantity) {
        this.buyQuantity = buyQuantity;
    }

    public int getFreeQuantity() {
        return freeQuantity;
    }

    public void setFreeQuantity(int freeQuantity) {
        this.freeQuantity = freeQuantity;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getRepetitionLimit() {
        return repetitionLimit;
    }

    public void setRepetitionLimit(int repetitionLimit) {
        this.repetitionLimit = repetitionLimit;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(Double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public Double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public Double getThreshold() {
        return threshold;
    }

    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }

    public String getApplicableProducts() {
        return applicableProducts;
    }

    public void setApplicableProducts(String applicableProducts) {
        this.applicableProducts = applicableProducts;
    }

    public Boolean isActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        this.isActive = active;
    }
}
