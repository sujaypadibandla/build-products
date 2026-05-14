package tacoorderapp.model;

import lombok.Data;

import java.util.List;

@Data
public class TacoOrderRequest {
    private Long accountId;
    private Long addressId;
    private Long creditCardId;
    private List<Taco> tacos;
}
