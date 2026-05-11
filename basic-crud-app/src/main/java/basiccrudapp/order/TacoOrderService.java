package basiccrudapp.order;

import basiccrudapp.account.AccountRepository;
import basiccrudapp.account.AddressRepository;
import basiccrudapp.payment.CreditCardRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class TacoOrderService {

    private final TacoOrderRepository tacoOrderRepository;
    private final AccountRepository accountRepository;
    private final AddressRepository addressRepository;
    private final CreditCardRepository creditCardRepository;
    private final EntityManager entityManager;

    public TacoOrderService(TacoOrderRepository tacoOrderRepository,
                            AccountRepository accountRepository,
                            AddressRepository addressRepository,
                            CreditCardRepository creditCardRepository,
                            EntityManager entityManager) {
        this.tacoOrderRepository = tacoOrderRepository;
        this.accountRepository = accountRepository;
        this.addressRepository = addressRepository;
        this.creditCardRepository = creditCardRepository;
        this.entityManager = entityManager;
    }

@Transactional
    public TacoOrderResponse placeOrder(TacoOrderRequest request) {
        TacoOrder order = new TacoOrder();
        var account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));
        var address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new RuntimeException("Address not found"));
        order.setAccount(account);
        order.setAddress(address);
        order.setCreditCard(creditCardRepository.findById(request.getCreditCardId())
                .orElseThrow(() -> new RuntimeException("CreditCard not found")));
        order.setTacos(request.getTacos());

        TacoOrder saved = tacoOrderRepository.save(order);
        entityManager.flush();
        entityManager.clear();
        // Re-fetch to get full ingredient details
        TacoOrder fetched = tacoOrderRepository.findById(saved.getId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        return new TacoOrderResponse(fetched.getTacos(), account.getAccountNumber(), address);

    }
}
