package basiccrudapp.order;

import basiccrudapp.account.Address;
import jakarta.transaction.Transactional;

import java.util.List;

public class TacoOrderResponse {

    private List<Taco> tacos;
    private String accountNumber;
    private Address address;

    public TacoOrderResponse(List<Taco> tacos, String accountNumber, Address address) {
        this.tacos = tacos;
        this.accountNumber = accountNumber;
        this.address = address;
    }

    // getters and setters
    public List<Taco> getTacos() { return tacos; }
    public void setTacos(List<Taco> tacos) { this.tacos = tacos; }
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }

}
