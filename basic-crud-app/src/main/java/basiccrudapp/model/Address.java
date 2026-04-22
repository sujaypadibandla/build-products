package basiccrudapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ADDRESS_TEST")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String street1;

    private String street2;

    private String state;

    private String city;

    private String country;

    private Integer zipCode;

    @Enumerated(EnumType.STRING)
    private AddressType addressType;

    public Address(String street1, String street2, String state, String city, String country, Integer zipCode) {

        this.street1 = street1;
        this.street2 = street2;
        this.state = state;
        this.city = city;
        this.country = country;
        this.zipCode = zipCode;
    }

    public enum AddressType {
        HOME,
        OFFICE,
        BILLING,
        SHIPPING
    }


}
