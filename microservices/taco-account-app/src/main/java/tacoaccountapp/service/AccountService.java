package tacoaccountapp.service;

import tacoaccountapp.model.Account;
import tacoaccountapp.model.Address;
import tacoaccountapp.model.CreditCard;
import org.springframework.stereotype.Service;
import tacoaccountapp.repository.AccountRepository;

import java.util.ArrayList;
import java.util.List;


@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Account getAccountById(Long accountId) {
        return accountRepository.findById(accountId).orElse(null);
    }

    public String addAccount(Account account) {
        Account out = accountRepository.save(account);
        return "Account added successfully with id: " + out.getId();
    }

    public String addCreditCardToAccount(Long accountId, CreditCard creditCard) {
        Account account = accountRepository.findById(accountId).orElse(null);
        if (account == null) return "Account not found";
        account.getCreditCards().add(creditCard);
        accountRepository.save(account);
        return "Credit card added successfully";
    }

    public String addAddressToAccount(Long accountId, Address address) {
        Account account = accountRepository.findById(accountId).orElse(null);
        if (account == null) return "Account not found";
        account.getAddresses().add(address);
        accountRepository.save(account);
        return "Address added successfully";
    }

    public List<Address> getAddressesByAccountId(Long accountId) {
        Account account = accountRepository.findById(accountId).orElse(null);
        if (account == null) return List.of();
        return new ArrayList<>(account.getAddresses());
    }

    public Address getAddressById(Long accountId, Long addressId) {
        Account account = accountRepository.findById(accountId).orElse(null);
        if (account == null) return null;
        return account.getAddresses().stream()
                .filter(a -> a.getId().equals(addressId))
                .findFirst().orElse(null);
    }

    public List<CreditCard> getCreditCardsByAccountId(Long accountId) {
        Account account = accountRepository.findById(accountId).orElse(null);
        if (account == null) return List.of();
        return new ArrayList<>(account.getCreditCards());
    }

    public CreditCard getCreditCardById(Long accountId, Long creditCardId) {
        Account account = accountRepository.findById(accountId).orElse(null);
        if (account == null) return null;
        return account.getCreditCards().stream()
                .filter(c -> c.getId().equals(creditCardId))
                .findFirst().orElse(null);
    }
}
