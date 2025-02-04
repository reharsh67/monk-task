package code.monk.coupon.api.repo;

import code.monk.coupon.api.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart,Long> {
}
