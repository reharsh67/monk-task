package code.monk.coupon.api.mock;

import code.monk.coupon.api.entity.Cart;
import code.monk.coupon.api.entity.CartItem;
import code.monk.coupon.api.entity.Coupon;
import code.monk.coupon.api.repo.CartRepository;
import code.monk.coupon.api.repo.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CartRepository cartRepository;

    @Override
    public void run(String... args) {
        Coupon coupon1 = new Coupon();
        coupon1.setCode("SAVE10");
        coupon1.setType("cart");
        coupon1.setDiscountPercentage(10.0);
        coupon1.setThreshold(100.0);
        coupon1.setActive(true);

        Coupon coupon2 = new Coupon();
        coupon2.setCode("SAVE20");
        coupon2.setType("cart");
        coupon2.setDiscountPercentage(20.0);
        coupon2.setThreshold(200.0);
        coupon2.setActive(true);

        Coupon coupon3 = new Coupon();
        coupon3.setCode("BUY1GET1");
        coupon3.setType("bxgy");
        coupon3.setApplicableProducts(Arrays.asList(1L, 2L).toString());
        coupon3.setBuyQuantity(1);
        coupon3.setFreeQuantity(1);
        coupon3.setRepetitionLimit(1);
        coupon3.setFreeProducts("5");
        coupon3.setActive(true);

        Coupon coupon4 = new Coupon();
        coupon4.setCode("BUY2GET1");
        coupon4.setType("bxgy");
        coupon4.setApplicableProducts(Arrays.asList(3L, 4L).toString());
        coupon4.setBuyQuantity(2);
        coupon4.setFreeQuantity(1);
        coupon4.setRepetitionLimit(2);
        coupon4.setFreeProducts("5");
        coupon4.setActive(true);

        Coupon coupon5 = new Coupon();
        coupon5.setCode("DISCOUNT5");
        coupon5.setType("product");
        coupon5.setDiscountPercentage(5.0);
        coupon5.setApplicableProducts(Arrays.asList(5L, 6L).toString());
        coupon5.setActive(true);

        Coupon coupon6 = new Coupon();
        coupon6.setCode("DISCOUNT15");
        coupon6.setType("product");
        coupon6.setDiscountPercentage(15.0);
        coupon6.setApplicableProducts(Arrays.asList(7L, 8L).toString());
        coupon6.setActive(true);


        for (int i = 1; i <= 10; i++) {
            Cart cart = new Cart();
            CartItem item1 = new CartItem((long) i, "Product " + i, 100.0 + i, 2, cart);
            CartItem item2 = new CartItem((long) (i + 10), "Product " + (i + 10), 50.0 + i, 3, cart);
            CartItem item3 = new CartItem((long) (i + 20), "Product " + (i + 20), 200.0 + i, 1, cart);

            cart.setItems(Arrays.asList(item1, item2, item3));
            cartRepository.save(cart);
        }

        couponRepository.saveAll(Arrays.asList(coupon1, coupon2, coupon3, coupon4, coupon5, coupon6));
    }
}
