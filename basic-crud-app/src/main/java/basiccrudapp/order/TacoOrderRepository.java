package basiccrudapp.order;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TacoOrderRepository extends JpaRepository<TacoOrder, Long> {
}

