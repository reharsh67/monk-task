package code.monk.coupon.api.service;

import code.monk.coupon.api.entity.Cart;
import code.monk.coupon.api.entity.CartItem;
import code.monk.coupon.api.entity.Coupon;
import code.monk.coupon.api.repo.CouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the CouponService class.
 */
@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CouponService couponService;

    private Coupon coupon;
    private Cart cart;
    private CartItem cartItem;

    /**
     * Sets up the test data before each test.
     */
    @BeforeEach
    void setUp() {
        coupon = new Coupon();
        coupon.setId(1L);
        coupon.setCode("TESTCODE");
        coupon.setType("cart");
        coupon.setDiscountPercentage(10.0);
        coupon.setThreshold(100.0);
        coupon.setActive(true);

        cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setProductId(1L);
        cartItem.setPrice(50.0);
        cartItem.setQuantity(2);

        cart = new Cart();
        cart.setItems(Collections.singletonList(cartItem));
        cart.setTotal(100.0);
    }

    /**
     * Tests the createCoupon method.
     */
    @Test
    void testCreateCoupon() {
        when(couponRepository.save(any(Coupon.class))).thenReturn(coupon);

        Coupon createdCoupon = couponService.createCoupon(coupon);

        assertNotNull(createdCoupon);
        assertEquals("TESTCODE", createdCoupon.getCode());
        verify(couponRepository, times(1)).save(coupon);
    }

    /**
     * Tests the getAllCoupons method.
     */
    @Test
    void testGetAllCoupons() {
        when(couponRepository.findAll()).thenReturn(Collections.singletonList(coupon));

        List<Coupon> coupons = couponService.getAllCoupons();

        assertNotNull(coupons);
        assertEquals(1, coupons.size());
        verify(couponRepository, times(1)).findAll();
    }

    /**
     * Tests the getCouponById method.
     */
    @Test
    void testGetCouponById() {
        when(couponRepository.findById(anyLong())).thenReturn(Optional.of(coupon));

        Optional<Coupon> foundCoupon = couponService.getCouponById(1L);

        assertTrue(foundCoupon.isPresent());
        assertEquals("TESTCODE", foundCoupon.get().getCode());
        verify(couponRepository, times(1)).findById(1L);
    }

    /**
     * Tests the deleteCoupon method.
     */
    @Test
    void testDeleteCoupon() {
        when(couponRepository.findById(anyLong())).thenReturn(Optional.of(coupon));
        doNothing().when(couponRepository).deleteById(anyLong());

        couponService.deleteCoupon(1L);

        verify(couponRepository, times(1)).deleteById(1L);
    }

    /**
     * Tests the updateCoupon method.
     */
    @Test
    void testUpdateCoupon() {
        when(couponRepository.findById(anyLong())).thenReturn(Optional.of(coupon));
        when(couponRepository.save(any(Coupon.class))).thenReturn(coupon);

        Coupon updatedCoupon = couponService.updateCoupon(1L, coupon);

        assertNotNull(updatedCoupon);
        assertEquals("TESTCODE", updatedCoupon.getCode());
        verify(couponRepository, times(1)).save(coupon);
    }

    /**
     * Tests the getApplicableCoupons method for cart-based coupons.
     */
    @Test
    void testGetApplicableCoupons_CartBased() {
        coupon.setType("cart");
        when(couponRepository.findAll()).thenReturn(Collections.singletonList(coupon));

        List<Coupon> applicableCoupons = couponService.getApplicableCoupons(cart);

        assertNotNull(applicableCoupons);
        assertEquals(1, applicableCoupons.size());
        assertEquals("cart", applicableCoupons.get(0).getType());
    }

    /**
     * Tests the getApplicableCoupons method for product-based coupons.
     */
    @Test
    void testGetApplicableCoupons_ProductBased() {
        coupon.setType("product");
        coupon.setApplicableProducts("1");
        when(couponRepository.findAll()).thenReturn(Collections.singletonList(coupon));

        List<Coupon> applicableCoupons = couponService.getApplicableCoupons(cart);

        assertNotNull(applicableCoupons);
        assertEquals(1, applicableCoupons.size());
        assertEquals("product", applicableCoupons.get(0).getType());
    }

    /**
     * Tests the getApplicableCoupons method for buy-x-get-y-based coupons.
     */
    @Test
    void testGetApplicableCoupons_BxGyBased() {
        coupon.setType("bxgy");
        coupon.setApplicableProducts("1");
        coupon.setBuyQuantity(2);
        coupon.setFreeQuantity(1);
        coupon.setRepetitionLimit(1);
        when(couponRepository.findAll()).thenReturn(Collections.singletonList(coupon));

        List<Coupon> applicableCoupons = couponService.getApplicableCoupons(cart);

        assertNotNull(applicableCoupons);
        assertEquals(1, applicableCoupons.size());
        assertEquals("bxgy", applicableCoupons.get(0).getType());
    }

    /**
     * Tests the applyCoupon method for cart-based coupons.
     */
    @Test
    void testApplyCoupon_CartBased() {
        coupon.setType("cart");
        when(couponRepository.findByCode(anyString())).thenReturn(coupon);

        Cart updatedCart = couponService.applyCoupon("TESTCODE", cart);

        assertNotNull(updatedCart);
        assertEquals(90.0, updatedCart.getTotal());
        verify(couponRepository, times(1)).findByCode("TESTCODE");
    }

    /**
     * Tests the applyCoupon method for product-based coupons.
     */
    @Test
    void testApplyCoupon_ProductBased() {
        coupon.setType("product");
        coupon.setApplicableProducts("1");
        when(couponRepository.findByCode(anyString())).thenReturn(coupon);

        Cart updatedCart = couponService.applyCoupon("TESTCODE", cart);

        assertNotNull(updatedCart);
        assertEquals(90.0, updatedCart.getTotal());
        verify(couponRepository, times(1)).findByCode("TESTCODE");
    }

    /**
     * Tests the applyCoupon method for buy-x-get-y-based coupons.
     */
    @Test
    void testApplyCoupon_BxGyBased() {
        coupon.setType("bxgy");
        coupon.setApplicableProducts("1");
        coupon.setFreeProducts("1");
        coupon.setBuyQuantity(2);
        coupon.setFreeQuantity(1);
        coupon.setRepetitionLimit(1);
        when(couponRepository.findByCode(anyString())).thenReturn(coupon);

        Cart updatedCart = couponService.applyCoupon("TESTCODE", cart);

        assertNotNull(updatedCart);
        assertEquals(50.0, updatedCart.getTotal());
        verify(couponRepository, times(1)).findByCode("TESTCODE");
    }
}