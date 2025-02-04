package code.monk.coupon.api.repo;

import code.monk.coupon.api.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    public Coupon findByCode(String code);
}