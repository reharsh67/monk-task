package code.monk.coupon.api.service;

import code.monk.coupon.api.entity.Cart;
import code.monk.coupon.api.entity.CartItem;
import code.monk.coupon.api.entity.Coupon;
import code.monk.coupon.api.repo.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CouponService {

    @Autowired
    private CouponRepository couponRepository;

    public Coupon createCoupon(Coupon coupon) {
        return couponRepository.save(coupon);
    }

    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }

    public Optional<Coupon> getCouponById(Long id) {
        return couponRepository.findById(id);
    }

    public void deleteCoupon(long id) {
        if (null != getCouponById(id).get()) {
            couponRepository.deleteById(id);
        }
    }

    public Coupon updateCoupon(Long id, Coupon couponDetails) {
        Coupon coupon = couponRepository.findById(id).orElseThrow(() -> new RuntimeException("Coupon not found"));
        coupon.setCode(couponDetails.getCode());
        coupon.setType(couponDetails.getType());
        coupon.setDiscountPercentage(couponDetails.getDiscountPercentage());
        coupon.setDiscountAmount(couponDetails.getDiscountAmount());
        coupon.setThreshold(couponDetails.getThreshold());
        coupon.setApplicableProducts(couponDetails.getApplicableProducts());
        coupon.setActive(couponDetails.isActive());
        return couponRepository.save(coupon);
    }

    /**
     * Get all applicable coupons for the given cart.
     */
    public List<Coupon> getApplicableCoupons(Cart cart) {
        List<Coupon> allCoupons = couponRepository.findAll();
        List<Coupon> applicableCoupons = new ArrayList<>();

        for (Coupon coupon : allCoupons) {
            if (coupon.isActive()) {
                switch (coupon.getType()) {
                    case "cart":
                        if (cart.getTotal() >= coupon.getThreshold()) {
                            applicableCoupons.add(coupon);
                        }
                        break;
                    case "product":
                        for (CartItem item : cart.getItems()) {
                            if (coupon.getApplicableProducts().contains(item.getProductId().toString())) {
                                applicableCoupons.add(coupon);
                                break;
                            }
                        }
                        break;
                case "bxgy":
                    int totalBuyItems = 0;
                    for (CartItem item : cart.getItems()) {
                        if (coupon.getApplicableProducts().contains(item.getProductId().toString())) {
                            totalBuyItems += item.getQuantity();
                        }
                    }
                    if (totalBuyItems >= coupon.getBuyQuantity()) {
                        applicableCoupons.add(coupon);
                    }
                    break;
                    default:
                        break;
                }
            }
        }
        return applicableCoupons;
    }

    /**
     * Apply a specific coupon to the cart and return the updated cart.
     */
    public Cart applyCoupon(String code, Cart cart) {
        Coupon coupon = couponRepository.findByCode(code);

        if (null != coupon && !coupon.isActive()) {
            throw new IllegalArgumentException("Coupon is inactive or Coupon not found");
        }

        switch (coupon.getType()) {
            case "cart":
                applyCartDiscount(coupon, cart);
                break;
            case "product":
                applyProductDiscount(coupon, cart);
                break;
            case "bxgy":
                applyBxGyDiscount(coupon, cart);
                break;
            default:
                throw new IllegalArgumentException("Unknown coupon type");
        }

        return cart;
    }

    /**
     * Apply a cart-wide discount.
     */
    private void applyCartDiscount(Coupon coupon, Cart cart) {
        if (cart.getTotal() >= coupon.getThreshold()) {
            double discount = (coupon.getDiscountPercentage() / 100) * cart.getTotal();
            cart.setDiscount(discount);
            cart.setOldTotal(cart.getTotal());
            cart.setTotal(cart.getTotal() - discount);
        } else {
            throw new IllegalArgumentException("Cart total does not meet the coupon threshold");
        }
    }

    /**
     * Apply a product-specific discount.
     */
    private void applyProductDiscount(Coupon coupon, Cart cart) {
        String applicableProductsIds = coupon.getApplicableProducts();
        double totalDiscount = 0.0;

        for (CartItem item : cart.getItems()) {
            if (applicableProductsIds.contains(item.getId().toString())) {
                double discount = (coupon.getDiscountPercentage() / 100) * item.getPrice() * item.getQuantity();
                item.setDiscount(discount);
                totalDiscount += discount;
            }
        }

        cart.setDiscount(totalDiscount);
        cart.setOldTotal(cart.getTotal());
        cart.setTotal(cart.getTotal() - totalDiscount);

    }

    /**
     * Apply a Buy X, Get Y Free (BxGy) discount.
     */
    private void applyBxGyDiscount(Coupon coupon, Cart cart) {
        String buyProductIds = coupon.getApplicableProducts();
        String freeProductIds = coupon.getFreeProducts();
        int buyQuantity = coupon.getBuyQuantity();
        int freeQuantity = coupon.getFreeQuantity();
        int repetitionLimit = coupon.getRepetitionLimit();
        int totalBuyItems = 0;
        List<CartItem> buyItems = new ArrayList<>();

        for (CartItem item : cart.getItems()) {
            if (buyProductIds.contains(item.getId().toString())) {
                totalBuyItems += item.getQuantity();
                buyItems.add(item);
            }
        }

        if (totalBuyItems >= buyQuantity) {
            int maxApplicable = Math.min(totalBuyItems / buyQuantity, repetitionLimit);

            List<CartItem> freeItems = new ArrayList<>();
            for (CartItem item : cart.getItems()) {
                if (freeProductIds.contains(item.getId().toString())) {
                    freeItems.add(item);
                }
            }

            for (CartItem item : buyItems) {
                int eligibleFreeItems = (item.getQuantity() / buyQuantity) * freeQuantity;
                eligibleFreeItems = Math.min(eligibleFreeItems, maxApplicable * freeQuantity);
                for (CartItem freeItem : freeItems) {
                    int freeItemsToApply = Math.min(eligibleFreeItems, freeItem.getQuantity());
                    freeItem.setDiscount(freeItem.getPrice() * freeItemsToApply);
                    eligibleFreeItems -= freeItemsToApply;
                    if (eligibleFreeItems <= 0) {
                        break;
                    }
                }
            }

            double totalDiscount = cart.getItems().stream().mapToDouble(CartItem::getDiscount).sum();
            cart.setDiscount(totalDiscount);
            cart.setOldTotal(cart.getTotal());
            cart.setTotal(cart.getTotal() - totalDiscount);
        }
    }


}
