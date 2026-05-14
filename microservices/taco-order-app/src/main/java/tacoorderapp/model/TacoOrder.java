package tacoorderapp.model;


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


    private Long accountId;

    private Long addressId;

    private Long creditCardId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Taco> tacos = new ArrayList<>();

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime placedAt;

    @PrePersist
    void placedAt() {
        this.placedAt = LocalDateTime.now();
    }
}

