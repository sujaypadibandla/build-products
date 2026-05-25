package tacoorderapp.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import tacoorderapp.dto.AccountDto;
import tacoorderapp.dto.AddressDto;
import tacoorderapp.dto.CreditCardDto;

@FeignClient(name = "taco-account-service", url = "http://localhost:8081")
public interface AccountInterface {

    @GetMapping("/api/v1/accounts/{accountId}")
    public AccountDto getAccountById(@PathVariable Long accountId);

    @GetMapping("/api/v1/accounts/{accountId}/addresses/{addressId}")
    public AddressDto getAddress(@PathVariable Long accountId, @PathVariable Long addressId);

    @GetMapping("/api/v1/accounts/{accountId}/credit-cards/{creditCardId}")
    public CreditCardDto getCreditCard(@PathVariable Long accountId, @PathVariable Long creditCardId);

}
