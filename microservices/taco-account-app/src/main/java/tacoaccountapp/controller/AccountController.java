package tacoaccountapp.controller;

import tacoaccountapp.model.Account;
import tacoaccountapp.model.Address;
import tacoaccountapp.model.CreditCard;
import org.springframework.web.bind.annotation.*;
import tacoaccountapp.service.AccountService;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public List<Account> getAccount() {
        return accountService.getAllAccounts();
    }

    @PostMapping("add")
    public String addAccount(@RequestBody Account account) {
        return accountService.addAccount(account);
    }

    @PostMapping("/{accountId}/credit-cards")
    public String addCreditCard(@PathVariable Long accountId, @RequestBody CreditCard creditCard) {
        return accountService.addCreditCardToAccount(accountId, creditCard);
    }

    @PostMapping("/{accountId}/addresses")
    public String addAddress(@PathVariable Long accountId, @RequestBody Address address) {
        return accountService.addAddressToAccount(accountId, address);
    }

    @GetMapping("/{accountId}")
    public List<Address> getAccountDetails(@PathVariable Long accountId) {
        return accountService.getAddressesByAccountId(accountId);
    }

    @GetMapping("/{accountId}/addresses")
    public List<Address> getAddresses(@PathVariable Long accountId) {
        return accountService.getAddressesByAccountId(accountId);
    }

    @GetMapping("/{accountId}/addresses/{addressId}")
    public Address getAddress(@PathVariable Long accountId, @PathVariable Long addressId) {
        return accountService.getAddressById(accountId, addressId);
    }

    @GetMapping("/{accountId}/credit-cards")
    public List<CreditCard> getCreditCards(@PathVariable Long accountId) {
        return accountService.getCreditCardsByAccountId(accountId);
    }

    @GetMapping("/{accountId}/credit-cards/{creditCardId}")
    public CreditCard getCreditCard(@PathVariable Long accountId, @PathVariable Long creditCardId) {
        return accountService.getCreditCardById(accountId, creditCardId);
    }

}

