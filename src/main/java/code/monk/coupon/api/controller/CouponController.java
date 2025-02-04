package code.monk.coupon.api.controller;

import code.monk.coupon.api.entity.Cart;
import code.monk.coupon.api.entity.Coupon;
import code.monk.coupon.api.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/coupons")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @PostMapping
    public ResponseEntity<Coupon> createCoupon(@RequestBody Coupon coupon) {
        return ResponseEntity.ok(couponService.createCoupon(coupon));
    }

    @GetMapping
    public ResponseEntity<List<Coupon>> getAllCoupons() {
        return ResponseEntity.ok(couponService.getAllCoupons());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Coupon> getCouponById(@PathVariable Long id) {
        return couponService.getCouponById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Coupon> updateCoupon(@PathVariable Long id, @RequestBody Coupon couponDetails) {
        return ResponseEntity.ok(couponService.updateCoupon(id, couponDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/applicable-coupons")
    public ResponseEntity<List<Coupon>> getApplicableCoupons( @RequestBody Cart cart) {
        return ResponseEntity.ok(couponService.getApplicableCoupons(cart));
    }

    @PostMapping("/apply-coupon")
    public ResponseEntity<Cart> applyCoupon(@RequestParam("code") String code, @RequestBody Cart cart) {
        Cart updatedCart = couponService.applyCoupon(code,cart);
        return ResponseEntity.ok(updatedCart);
    }
}
