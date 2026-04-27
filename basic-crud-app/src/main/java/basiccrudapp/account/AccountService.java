package basiccrudapp.account;

import basiccrudapp.payment.CreditCard;
import org.springframework.stereotype.Service;

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
}

