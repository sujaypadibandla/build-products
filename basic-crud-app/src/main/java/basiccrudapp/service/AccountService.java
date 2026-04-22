package basiccrudapp.service;


import basiccrudapp.dao.AccountRepository;
import basiccrudapp.model.Account;
import basiccrudapp.model.Address;
import basiccrudapp.model.CreditCard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {

    @Autowired
    AccountRepository accountRepository;


    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public String addAccount(Account account) {
        Account out = accountRepository.save(account);
        return out != null ? "Account added successfully with id: " + out.getId() : "Failed to add account";
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
