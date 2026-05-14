package tacoorderapp.dto;

import lombok.Data;

@Data
public class CreditCardDto {
    private Long id;
    private String number;
    private String expiration;
    private String cvv;
}

