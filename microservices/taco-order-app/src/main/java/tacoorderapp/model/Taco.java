package tacoorderapp.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Entity
public class Taco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Ingredient> ingredients;

    private Date createdAt;

    @PrePersist
    void createdAt() {
        this.createdAt = new Date();
    }


}

