package basiccrudapp.order;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1/orders")
public class TacoOrderController {

    private final TacoOrderService tacoOrderService;

    public TacoOrderController(TacoOrderService tacoOrderService) {
        this.tacoOrderService = tacoOrderService;
    }

    @PostMapping("/add")
    public TacoOrderResponse addOrder(@RequestBody TacoOrderRequest request) {

        return tacoOrderService.placeOrder(request);
    }
}

