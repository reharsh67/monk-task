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

/**
 * Service class for managing coupons.
 */
@Service
public class CouponService {

    @Autowired
    private CouponRepository couponRepository;

    /**
     * Creates a new coupon.
     *
     * @param coupon the coupon to create
     * @return the created coupon
     */
    public Coupon createCoupon(Coupon coupon) {
        return couponRepository.save(coupon);
    }

    /**
     * Retrieves all coupons.
     *
     * @return a list of all coupons
     */
    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }

    /**
     * Retrieves a coupon by its ID.
     *
     * @param id the ID of the coupon
     * @return an Optional containing the coupon if found, or empty if not found
     */
    public Optional<Coupon> getCouponById(Long id) {
        return couponRepository.findById(id);
    }

    /**
     * Deletes a coupon by its ID.
     *
     * @param id the ID of the coupon to delete
     */
    public void deleteCoupon(long id) {
        if (null != getCouponById(id).get()) {
            couponRepository.deleteById(id);
        }
    }

    /**
     * Updates an existing coupon.
     *
     * @param id the ID of the coupon to update
     * @param couponDetails the new details of the coupon
     * @return the updated coupon
     */
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
     * Retrieves all applicable coupons for the given cart.
     *
     * @param cart the cart to check for applicable coupons
     * @return a list of applicable coupons
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
     * Applies a specific coupon to the cart and returns the updated cart.
     *
     * @param code the code of the coupon to apply
     * @param cart the cart to apply the coupon to
     * @return the updated cart
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
     * Applies a cart-wide discount.
     *
     * @param coupon the coupon to apply
     * @param cart the cart to apply the discount to
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
     * Applies a product-specific discount.
     *
     * @param coupon the coupon to apply
     * @param cart the cart to apply the discount to
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
     * Applies a Buy X, Get Y Free (BxGy) discount.
     *
     * @param coupon the coupon to apply
     * @param cart the cart to apply the discount to
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