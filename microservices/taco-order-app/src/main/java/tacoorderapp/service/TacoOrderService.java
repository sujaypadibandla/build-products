package tacoorderapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import tacoorderapp.dto.AccountDto;
import tacoorderapp.dto.AddressDto;
import tacoorderapp.dto.CreditCardDto;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import tacoorderapp.feign.AccountInterface;
import tacoorderapp.model.TacoOrder;
import tacoorderapp.model.TacoOrderRequest;
import tacoorderapp.model.TacoOrderResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tacoorderapp.repository.TacoOrderRepository;

@Service
public class TacoOrderService {

    private final TacoOrderRepository tacoOrderRepository;
    private final RestTemplate restTemplate;
    private final EntityManager entityManager;

    @Autowired
    AccountInterface accountInt;

    public TacoOrderService(TacoOrderRepository tacoOrderRepository,
                            RestTemplate restTemplate,
                            EntityManager entityManager) {
        this.tacoOrderRepository = tacoOrderRepository;
        this.restTemplate = restTemplate;
        this.entityManager = entityManager;
    }

    @Transactional
    public TacoOrderResponse placeOrder(TacoOrderRequest request) {
        TacoOrder order = new TacoOrder();
        order.setAccountId(request.getAccountId());
        order.setAddressId(request.getAddressId());
        order.setCreditCardId(request.getCreditCardId());
        order.setTacos(request.getTacos());

        TacoOrder saved = tacoOrderRepository.save(order);
        entityManager.flush();
        entityManager.clear();

        TacoOrder fetched = tacoOrderRepository.findById(saved.getId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Fetch account, address, and credit card from taco-account-app
//        AccountDto[] accounts = restTemplate.getForObject(
//                "http://localhost:8081/api/v1/accounts/{accountId}",
//                AccountDto[].class, fetched.getAccountId());

        AccountDto account = accountInt.getAccountById(fetched.getAccountId());

//        AddressDto address = restTemplate.getForObject(
//                "http://localhost:8081/api/v1/accounts/{accountId}/addresses/{addressId}",
//                AddressDto.class, fetched.getAccountId(), fetched.getAddressId());

        AddressDto address = accountInt.getAddress(fetched.getAccountId(), fetched.getAddressId());


//        CreditCardDto creditCard = restTemplate.getForObject(
//                "http://localhost:8081/api/v1/accounts/{accountId}/credit-cards/{creditCardId}",
//                CreditCardDto.class, fetched.getAccountId(), fetched.getCreditCardId());


        CreditCardDto creditCard = accountInt.getCreditCard(fetched.getAccountId(), fetched.getCreditCardId());


        return new TacoOrderResponse(fetched.getTacos(), account.getAccountNumber(), address);
    }
}
