package tacoorderapp.controller;

import tacoorderapp.model.TacoOrderRequest;
import tacoorderapp.model.TacoOrderResponse;
import org.springframework.web.bind.annotation.*;
import tacoorderapp.service.TacoOrderService;

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

