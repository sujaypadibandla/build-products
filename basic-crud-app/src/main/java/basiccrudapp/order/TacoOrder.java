package basiccrudapp.order;

import basiccrudapp.account.Account;
import basiccrudapp.account.Address;
import basiccrudapp.payment.CreditCard;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Taco_Order")
@Data
public class TacoOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Account account;

    @ManyToOne
    private Address address;

    @ManyToOne
    private CreditCard creditCard;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Taco> tacos = new ArrayList<>();

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime placedAt;

    @PrePersist
    void placedAt() {
        this.placedAt = LocalDateTime.now();
    }
}

