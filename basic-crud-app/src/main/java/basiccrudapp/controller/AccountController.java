package basiccrudapp.controller;



import basiccrudapp.model.Account;
import basiccrudapp.model.Address;
import basiccrudapp.model.CreditCard;
import basiccrudapp.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    @Autowired
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

}
